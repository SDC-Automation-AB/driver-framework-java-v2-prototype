package org.iot.dsa.dslink.dfexample;

import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public class TestPointNode extends DFPointNode implements DSIValue {
    
    DSMap parameters;
    private DSInfo value = getInfo("Value");
    
    public TestPointNode() {
    }
    
    public TestPointNode(DSMap parameters) {
        this.parameters = parameters;
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
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Value", DSString.EMPTY);
    }
    
    @Override
    protected void onStable() {
        put("Edit", makeEditAction());
        super.onStable();
    }

    @Override
    public void closeConnection() {
    }
    
    @Override
    public long getRefresh() {
        DSElement rate = parameters.get("Poll Rate");
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getRefresh();
    }
    
    private DSAction makeEditAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((TestPointNode) info.getParent()).edit(invocation.getParameters());
                return null;
            }
        };
        DSElement defLine = parameters.get("Line");
        DSElement defPingRate = parameters.get("Ping Rate");
        act.addDefaultParameter("Line", defLine != null ? defLine : DSLong.NULL, null);
        act.addDefaultParameter("Poll Rate", defPingRate != null ? defPingRate : DSLong.valueOf(REFRESH_DEF), null);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        this.parameters = newParameters;
        put("parameters", parameters.copy());
        put("Edit", makeEditAction());
        restartNode();
    }

    @Override
    public DSValueType getValueType() {
        return DSValueType.STRING;
    }

    @Override
    public DSIValue restore(DSElement element) {
        return valueOf(element);
    }

    @Override
    public DSElement store() {
        return toElement();
    }

    @Override
    public DSElement toElement() {
        return value.getValue().toElement();
    }

    @Override
    public DSIValue valueOf(DSElement element) {
        return value.getValue().valueOf(element);
    }

    @Override
    public boolean poll() {
        try {
            int lineNo = parameters.getInt("Line");
            String str = getParent().getParent().getParent().get("TESTSTRING").toString();
            String result = str.split("\n")[lineNo];
            if (result.toLowerCase().endsWith("fail")) {
                return false;
            } else {
                put(value, DSString.valueOf(result));
                getParent().childChanged(getInfo());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    private TestDeviceNode getParentNode() {
        DSNode parent =  getParent();
        if (parent instanceof TestDeviceNode) {
            return (TestDeviceNode) getParent();
        } else {
            throw new RuntimeException("Wrong parent class");
        }
    }

}
