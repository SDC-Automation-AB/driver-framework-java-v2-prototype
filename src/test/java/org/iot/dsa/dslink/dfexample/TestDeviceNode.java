package org.iot.dsa.dslink.dfexample;

import org.iot.dsa.dslink.dframework.DFDeviceNode;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.dftest.TestingDevice;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestDeviceNode extends DFDeviceNode {
    
    DSMap parameters;
    TestingDevice devObj;
    
    public TestDeviceNode() {
    }
    
    public TestDeviceNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Add Point", makeAddPointAction());
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
        boolean success;
        try {
            String devStr = parameters.getString("Device String");
            devObj = getParentNode().connObj.getDevice(devStr);
            success = getParentNode().connObj.pingDevice(devObj);
        } catch (Exception e) {
            success = false;
        }
        if (!success) {
            devObj = null;
        }
        return success;
    }

    @Override
    public boolean ping() {
        try {
            return getParentNode().connObj.pingDevice(devObj);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void closeConnection() {
        devObj = null;
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
                ((TestDeviceNode) info.getParent()).edit(invocation.getParameters());
                return null;
            }
        };
        DSElement defStr = parameters.get("Device String");
        DSElement defPingRate = parameters.get("Ping Rate");
        act.addDefaultParameter("Device String", defStr != null ? defStr : DSString.EMPTY, null);
        act.addDefaultParameter("Ping Rate", defPingRate != null ? defPingRate : DSLong.valueOf(DEFAULT_PING_RATE), null);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        this.parameters = newParameters;
        put("parameters", parameters.copy());
        put("Edit", makeEditAction());
        restartNode();
    }
    
    private DSAction makeAddPointAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((TestDeviceNode) info.getParent()).addPoint(invocation.getParameters());
                return null;
            }
        };
        act.addParameter("Name", DSValueType.STRING, null);
        act.addParameter("ID", DSValueType.STRING, null);
        act.addDefaultParameter("Poll Rate", DSLong.valueOf(DEFAULT_PING_RATE), null);
        return act;
    }

    void addPoint(DSMap pointParameters) {
        String name = pointParameters.getString("Name");
        TestPointNode point = new TestPointNode(pointParameters);
        put(name, point);
//        point.startCarObject();
    }
    
    TestConnectionNode getParentNode() {
        DSNode parent = getParent();
        if (parent instanceof TestConnectionNode) {
            return (TestConnectionNode) parent;
        } else {
            throw new RuntimeException("Wrong parent class");
        }
    }

    @Override
    public boolean batchPoll(Set<DFPointNode> points) {
        try {
            Map<String, TestPointNode> polledPoints = new HashMap<String, TestPointNode>();
            for (DFPointNode p: points) {
                TestPointNode point = (TestPointNode) p;
                polledPoints.put(point.getPointID(), point);
            }

            Set<String> batch = polledPoints.keySet();
            Map<String, String> results = getParentNode().connObj.batchRead(devObj, batch);

            for (Map.Entry<String, String> entry: results.entrySet()) {
                TestPointNode point = polledPoints.get(entry.getKey());
                point.updateValue(entry.getValue());
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
