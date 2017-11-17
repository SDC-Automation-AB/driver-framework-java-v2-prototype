package org.iot.dsa.dslink.dfexample;

import org.iot.dsa.dslink.dframework.DFConnectionNode;
import org.iot.dsa.dslink.dframework.DFHelpers;
import org.iot.dsa.dslink.dftest.TestingConnection;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public class TestConnectionNode extends DFConnectionNode {
    protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.LINEAR;

    DSMap parameters;
    TestingConnection connObj;
    
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
        act.addParameter("Device String", DSValueType.STRING, null);
        act.addDefaultParameter("Ping Rate", DSLong.valueOf(DFHelpers.DEFAULT_PING_DELAY), null);
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
            String cs = parameters.getString("Connection String");
            connObj = TestingConnection.getConnection(cs);
            connObj.connect();
            return true;
        } catch (Exception e) {
            connObj = null;
            return false;
        }
    }

    @Override
    public boolean ping() {
        try {
            return connObj.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void closeConnection() {
        if (connObj != null) {
            connObj.close();
        }
        connObj = null;
    }
    
    @Override
    public long getPingRate() {
        DSElement rate = parameters.get("Ping Rate");
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getPingRate();
    }
    
    private DSAction makeEditAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((TestConnectionNode) info.getParent()).edit(invocation.getParameters());
                return null;
            }
        };
        DSElement defStr = parameters.get("Connection String");
        DSElement defPingRate = parameters.get("Ping Rate");
        act.addDefaultParameter("Connection String", defStr != null ? defStr : DSString.EMPTY, null);
        act.addDefaultParameter("Ping Rate", defPingRate != null ? defPingRate : DSLong.valueOf(DFHelpers.DEFAULT_PING_DELAY), null);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        this.parameters = newParameters;
        put("parameters", parameters.copy());
        put("Edit", makeEditAction());
        restartNode();
    }

}
