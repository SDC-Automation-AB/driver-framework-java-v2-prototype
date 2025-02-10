package org.iot.dsa.dslink.dftest;

import org.iot.dsa.dslink.ActionResults;
import org.iot.dsa.dslink.dfexample.MainNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSIActionRequest;

/**
 * @author James (Juris) Puchin
 * Created on 11/20/2017
 */
public class DSRuntimeRootNodeTest extends MainNode implements FailCallback {
    private static final long ITERATION_LENGTH = 1000;
    private static final long MAX_DELAY_ERROR = 100;
    private static final long TOTAL_TEST_TIME = 10000;
    private boolean failed = false;
    /**
     * Defines the permanent children of this node type, their existence is guaranteed in all
     * instances.  This is only ever called once per, type per process.
     */
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        DSAction act = new DSAction() {
            @Override
            public ActionResults invoke(DSIActionRequest request) {
                ((DSRuntimeRootNodeTest)request.getTarget()).startDummy(request.getParameters());
                return super.invoke(request);
            }
        };
        declareDefault("Start Dummy", act);
    }

    private void startDummy(DSMap parameters) {
        new TestRunner(this, ITERATION_LENGTH, MAX_DELAY_ERROR, TOTAL_TEST_TIME);
    }

    @Override
    public void failed() {
        failed = true;
    }
}
