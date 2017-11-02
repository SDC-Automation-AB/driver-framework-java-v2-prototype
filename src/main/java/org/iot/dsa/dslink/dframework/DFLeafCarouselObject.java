package org.iot.dsa.dslink.dframework;

import java.util.Map;
import java.util.Set;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.dslink.dframework.DFHelpers.DFConnStrat;
import org.iot.dsa.dslink.dframework.DFHelpers.DFRefChangeStrat;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;

public class DFLeafCarouselObject extends DFCarouselObject {
    Set<DFPointNode> homeNodes;
    
    public DFLeafCarouselObject(DFAbstractNode home) {
        this.refresh = home.getRefresh();
        this.connStrat = home.getConnStrat();
        this.refChangeStrat = home.getRefreshChangeStrat();
        DSRuntime.run(this);
    }
    
    private DFPointNode getAHomeNode() {
        return homeNodes.iterator().next();
    }

    private DFDeviceNode getADeviceNode() {
        return (DFDeviceNode) homeNodes.iterator().next().getParent();
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
        for (DFPointNode n: homeNodes) {
            n.onDfStopped();
        }
    }
    
    @Override
    public void run() {
        if (!running) {
            return;
        }
        if (iAmAnOrphan()) {
            for (DFPointNode n: homeNodes) {
                n.stopCarObject();
            }
            return;
        }
        //Can add redundant check for isNodeStopped here
        boolean success;

        DSNode dev = getAHomeNode().getParent();
        if (dev instanceof DFDeviceNode) {
            success = ((DFDeviceNode) dev).batchPoll(homeNodes);
        } else {
            throw new RuntimeException("Wrong parent class, no device found.");
        }

        if (!success) {
            for (DFPointNode n: homeNodes) {
                n.onFailed();
            }
        }
        DSRuntime.runDelayed(this, getDelay());
    }

}
