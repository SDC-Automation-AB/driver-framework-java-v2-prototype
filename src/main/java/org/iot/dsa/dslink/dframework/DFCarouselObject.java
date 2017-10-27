package org.iot.dsa.dslink.dframework;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.dframework.DFHelpers.DFConnStrat;
import org.iot.dsa.dslink.dframework.DFHelpers.DFRefChangeStrat;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;

public class DFCarouselObject implements Runnable {


    private DFAbstractNode homeNode;
    private long refresh;
    private DFConnStrat connStrat;
    private DFRefChangeStrat refChangeStrat;
    private boolean connected = false;
    private boolean running = true;
    DSIValue status;
    
    public DFCarouselObject(DFAbstractNode home) {
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
        else { return false; }
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
