package org.iot.dsa.dslink.dfexample;

import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;


public class RootNode extends DSRootNode {

    /**
     * Defines the permanent children of this node type, their existence is guaranteed in all
     * instances.  This is only ever called once per, type per process.
     */
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((RootNode) info.getParent()).addConnection(invocation.getParameters());
                return null;
            }
        };
        act.addParameter("Name", DSValueType.STRING, null);
        act.addParameter("Line", DSValueType.NUMBER, null);
        act.addDefaultParameter("Ping Rate", DSLong.valueOf(TestConnectionNode.REFRESH_DEF), null);
        declareDefault("Add Connection", act);
        declareDefault("TESTSTRING", DSString.valueOf(TESTSTRING));
    }

    private void addConnection(DSMap parameters) {
        String name = parameters.getString("Name");
        put(name, new TestConnectionNode(parameters));
    }
    
    public final static String TESTSTRING = "Conn1\nDev1\nP1fail\nP2\nDev2\nP1\nConn2\nDevA\nDevB\nPa\nPb\nPc\nConn3\n";

}
