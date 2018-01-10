package org.iot.dsa.dslink.dfexample;

import java.util.ArrayList;
import java.util.List;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;

public class TestPointNode extends DFPointNode implements DSIValue {
    
    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeParam("ID", DSValueType.STRING, null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault("Poll Rate", DSLong.valueOf(TestDeviceNode.DEFAULT_PING_RATE), null, null));
    }
    
    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }
    
    private DSInfo value = getInfo("Value");
    
    public TestPointNode() {
    }
    
    public TestPointNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public void addNewInstance(DSNode parent, DSMap newParameters) {
        String name = newParameters.getString("Name");
        TestPointNode point = new TestPointNode(newParameters);
        parent.put(name, point);
//        point.startCarObject();
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Value", DSString.EMPTY);
    }
    
    @Override
    protected void onStable() {
        super.onStable();
    }
    
    @Override
    public long getPollRate() {
        DSElement rate = parameters.get("Poll Rate");
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getPollRate();
    }

    @Override
    public DSValueType getValueType() {
        return DSValueType.STRING;
    }

    @Override
    public DSElement toElement() {
        return value.getValue().toElement();
    }

    @Override
    public DSIValue valueOf(DSElement element) {
        return value.getValue().valueOf(element);
    }
    
    void updateValue(String val) {
        put(value, DSString.valueOf(val));
        getParent().childChanged(getInfo());
    }
    
    String getPointID() {
        return parameters.getString("ID");
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
