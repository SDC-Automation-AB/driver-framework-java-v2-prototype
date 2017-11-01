package org.iot.dsa.dslink.dframework;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.dslink.dframework.DFHelpers.DFConnStrat;
import org.iot.dsa.dslink.dframework.DFHelpers.DFRefChangeStrat;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;

public class DFBranchCarouselObject extends DFCarouselObject{

    private DFAbstractNode homeNode;
    private boolean connected = false;
    
    public DFBranchCarouselObject(DFAbstractNode home) {
        homeNode = home;
        this.refresh = home.getRefresh();
        this.connStrat = home.getConnStrat();
        this.refChangeStrat = home.getRefreshChangeStrat();
        DSRuntime.run(this);
    }

    private boolean iAmAnOrphan() {
        if (homeNode.getParent() instanceof  DFAbstractNode) {
            DFAbstractNode par = (DFAbstractNode) homeNode.getParent();
            return !par.isNodeConnected();
        }
        else if (homeNode.getParent() instanceof DSRootNode) { return false; }
        else { throw new RuntimeException("Wrong parent class"); }
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
    
    private long getDelay() {
        return 5000;
    }
    
    public void close() {
        running = false;
        killOrSpawnChildren(true);
        homeNode.closeConnection();
        homeNode.onDfStopped();
    }
    
    @Override
    public void run() {
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
