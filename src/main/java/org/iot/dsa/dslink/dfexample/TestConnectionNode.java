package org.iot.dsa.dslink.dfexample;

import org.iot.dsa.dslink.dframework.DFConnectionNode;
import org.iot.dsa.dslink.dframework.DFHelpers;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public class TestConnectionNode extends DFConnectionNode {
    protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.LINEAR;

    DSMap parameters;
    
    public TestConnectionNode() {
    }
    
    public TestConnectionNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Add Device", makeAddDeviceAction());
    }

    private DSIObject makeAddDeviceAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((TestConnectionNode) info.getParent()).addDevice(invocation.getParameters());
                return null;
            }
        };
        act.addParameter("Name", DSValueType.STRING, null);
        act.addParameter("Line", DSValueType.NUMBER, null);
        act.addDefaultParameter("Ping Rate", DSLong.valueOf(TestConnectionNode.REFRESH_DEF), null);
        return act;
    }

    void addDevice(DSMap deviceParameters) {
        String name = deviceParameters.getString("Name");
        TestDeviceNode device = new TestDeviceNode(deviceParameters);
        put(name, device);
        device.startCarObject();
    }

    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get("parameters");
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
        } else {
            put("parameters", parameters.copy());
        }
    }
    
    @Override
    protected void onStable() {
        put("Edit", makeEditAction());
        super.onStable();
    }

    @Override
    public boolean createConnection() {
        try {
            int lineNo = parameters.getInt("Line");
            String str = getParent().get("TESTSTRING").toString();
            String result = str.split("\n")[lineNo];
            return !result.toLowerCase().endsWith("fail");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean ping() {
        try {
            int lineNo = parameters.getInt("Line");
            String str = getParent().get("TESTSTRING").toString();
            String result = str.split("\n")[lineNo];
            return !result.toLowerCase().endsWith("fail");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void closeConnection() {
    }
    
    @Override
    public long getRefresh() {
        DSElement rate = parameters.get("Ping Rate");
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getRefresh();
    }
    
    private DSAction makeEditAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((TestConnectionNode) info.getParent()).edit(invocation.getParameters());
                return null;
            }
        };
        DSElement defLine = parameters.get("Line");
        DSElement defPingRate = parameters.get("Ping Rate");
        act.addDefaultParameter("Line", defLine != null ? defLine : DSLong.NULL, null);
        act.addDefaultParameter("Ping Rate", defPingRate != null ? defPingRate : DSLong.valueOf(REFRESH_DEF), null);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        this.parameters = newParameters;
        put("parameters", parameters.copy());
        put("Edit", makeEditAction());
        restartNode();
    }

}
