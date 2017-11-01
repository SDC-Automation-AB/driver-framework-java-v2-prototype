package org.iot.dsa.dslink.dframework;

import java.util.Map;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.dslink.dframework.DFHelpers.DFConnStrat;
import org.iot.dsa.dslink.dframework.DFHelpers.DFRefChangeStrat;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;

public class DFLeafCarouselObject extends DFCarouselObject {
    private Map<String, DFPointNode> homeNodes;
    
    public DFLeafCarouselObject(DFAbstractNode home) {
//        homeNode = home;
        this.refresh = home.getRefresh();
        this.connStrat = home.getConnStrat();
        this.refChangeStrat = home.getRefreshChangeStrat();
        DSRuntime.run(this);
    }
    
    private DFPointNode getAHomeNode() {
        return homeNodes.values().iterator().next();
    }

    private boolean iAmAnOrphan() {
        DFPointNode homeNode = getAHomeNode();
        if (homeNode.getParent() instanceof  DFDeviceNode) {
            DFDeviceNode par = (DFDeviceNode) homeNode.getParent();
            return !par.isNodeConnected();
        }
        else if (homeNode.getParent() instanceof DSRootNode) { return false; }
        else { throw new RuntimeException("Wrong parent class"); }
    }
    
    private long getDelay() {
        return 5000;
    }
    
    public void close() {
        running = false;
        for (DFPointNode n: homeNodes.values()) {
            n.closeConnection();
            n.onDfStopped();
        }
    }
    
    @Override
    public void run() {
        if (!running) {
            return;
        }
        if (iAmAnOrphan()) {
            for (DFPointNode n: homeNodes.values()) {
                n.stopCarObject();
            }
            return;
        }
        //Can add redundant check for isNodeStopped here 
        boolean success = homeNode.ping();
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
