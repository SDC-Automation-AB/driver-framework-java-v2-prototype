package org.iot.dsa.dslink.dframework;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;

public class DFBranchCarouselObject extends DFCarouselObject{

    private final DFBranchNode homeNode;
    private boolean connected = false;
    
    DFBranchCarouselObject(DFBranchNode home) {
        homeNode = home;
        this.calculator = homeNode.getDelayCalculator(this);
        DSRuntime.run(this);
    }

    private boolean iAmAnOrphan() {
        if (homeNode.getParent() instanceof  DFAbstractNode) {
            DFAbstractNode par = (DFAbstractNode) homeNode.getParent();
            return !par.isNodeConnected();
        }
        else if (homeNode.getParent() instanceof DSRootNode) { return false; }
        else { throw new RuntimeException("Wrong parent class: " + homeNode.getParent().getName() + "-" + homeNode.getParent().getClass()); }
    }

    private void killOrSpawnChildren(boolean kill) {
        for (DSInfo info : homeNode) {
            DSIObject o = info.getObject();
            if (o instanceof DFAbstractNode) {
                DFAbstractNode child = (DFAbstractNode) o;
                    if (kill) {
                        child.stopCarObject();
                    } else {
                        if (!child.isNodeStopped())
                            child.startCarObject();
                    }
            }
        }
    }
    
    void close() {
        running = false;
        killOrSpawnChildren(true);
        homeNode.closeConnection();
        homeNode.onDfStopped();
    }
    
    @Override
    public void run() {
        synchronized (homeNode) {
            if (!running) {
                return;
            }
            if (iAmAnOrphan()) {
                homeNode.stopCarObject();
                return;
            }
            //Can add redundant check for isNodeStopped here
            if (connected) {
                connected = homeNode.ping();
                if (!connected) {
                    homeNode.onFailed();
                    killOrSpawnChildren(true);
                }
            } else {
                connected = homeNode.createConnection();
                if (connected) {
                    homeNode.onConnected();
                    killOrSpawnChildren(false);
                } else {
                    homeNode.onFailed();
                    killOrSpawnChildren(true);
                }
            }
            DSRuntime.runDelayed(this, getDelay());
        }
    }
}
