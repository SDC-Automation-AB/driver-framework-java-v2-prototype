package org.iot.dsa.dslink.dfexample;

import java.util.ArrayList;
import java.util.List;
import org.iot.dsa.dslink.dframework.DFConnectionNode;
import org.iot.dsa.dslink.dframework.DFHelpers;
import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.dftest.TestingConnection;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSValueType;

public class TestConnectionNode extends DFConnectionNode {
    protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.LINEAR;
    
    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeParam("Connection String", DSValueType.STRING, null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault("Ping Rate", DSLong.valueOf(TestConnectionNode.DEFAULT_PING_RATE), null, null));
    }
    
    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    TestingConnection connObj;
    
    public TestConnectionNode() {
    }
    
    public TestConnectionNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public void addNewInstance(DSNode parent, DSMap newParameters) {
        String name = newParameters.getString("Name");
        parent.put(name, new TestConnectionNode(newParameters));
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Add Device", DFUtil.getAddAction(TestDeviceNode.class));
    }
    
    @Override
    protected void onStable() {
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

}
