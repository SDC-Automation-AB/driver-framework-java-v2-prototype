package org.iot.dsa.dslink.dfexample;

import java.util.ArrayList;
import java.util.List;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValueType;

public class TestPointNode extends DFPointNode {
    
    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeParam("ID", DSValueType.STRING, null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault("Poll Rate", DSLong.valueOf(TestDeviceNode.DEFAULT_PING_RATE), null, null));
    }
    
    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }
    
    
    public TestPointNode() {
    }
    
    public TestPointNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public long getPollRate() {
        DSElement rate = parameters.get("Poll Rate");
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getPollRate();
    }
    
    String getPointID() {
        return parameters.getString("ID");
    }

    @Override
    public boolean isNull() {
        return this == null;
    }

//    private TestDeviceNode getParentNode() {
//        DSNode parent =  getParent();
//        if (parent instanceof TestDeviceNode) {
//            return (TestDeviceNode) getParent();
//        } else {
//            throw new RuntimeException("Wrong parent class");
//        }
//    }

}
