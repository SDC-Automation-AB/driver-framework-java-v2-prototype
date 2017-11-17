package org.iot.dsa.dslink.dframework;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFConnectionNode extends DFBranchNode {

    @Override
    protected void onStable() {
        super.onStable();
        if (!isNodeStopped()) {
            startCarObject();
        }
    }
}
