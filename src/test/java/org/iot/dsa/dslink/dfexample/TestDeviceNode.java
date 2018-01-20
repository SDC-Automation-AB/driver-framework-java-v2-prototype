package org.iot.dsa.dslink.dfexample;

import org.iot.dsa.dslink.dframework.DFDeviceNode;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingDevice;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSValueType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestDeviceNode extends DFDeviceNode {
    
    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeParam("Device String", DSValueType.STRING, null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault("Ping Rate", DSLong.valueOf(TestConnectionNode.DEFAULT_PING_RATE), null, null));
    }
    
    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }
    
    TestingDevice devObj;
    
    public TestDeviceNode() {
    }
    
    public TestDeviceNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Add Point", DFUtil.getAddAction(TestPointNode.class));
    }
    
    @Override
    protected void onStable() {
        super.onStable();
    }

    @Override
    public boolean createConnection() {
        boolean success;
        try {
            String devStr = parameters.getString("Device String");
            devObj = getParentNode().connObj.findDevice(devStr);
            success = getParentNode().connObj.pingDevice(devObj, new MockParameters(parameters));
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
            return getParentNode().connObj.pingDevice(devObj, new MockParameters(parameters));
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
            Map<String, String> results = getParentNode().connObj.batchRead(devObj, new MockParameters(parameters), batch);

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
