package org.iot.dsa.dslink.dfexample;

import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.node.*;
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
        act.addParameter("Connection String", DSValueType.STRING, null);
        act.addDefaultParameter("Ping Rate", DSLong.valueOf(TestConnectionNode.DEFAULT_PING_RATE), null);
        declareDefault("Add Connection", act);
    }

    private void addConnection(DSMap parameters) {
        String name = parameters.getString("Name");
        put(name, new TestConnectionNode(parameters));
    }

//    @Override
//    protected void declareDefaults() {
//        super.declareDefaults();
//        DSAction act = new DSAction() {
//            @Override
//            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
//                ((RootNode) info.getParent()).startDummy(invocation.getParameters());
//                return null;
//            }
//        };
//        declareDefault("Start Dummy", act);
//    }

    private void startDummy(DSMap parameters) {
        new DummyRunnable();
    }
}
