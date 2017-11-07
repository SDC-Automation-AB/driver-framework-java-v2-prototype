package org.iot.dsa.dslink.dframework;

import java.util.HashSet;
import java.util.Set;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSRootNode;

public class DFLeafCarouselObject extends DFCarouselObject {
    Set<DFPointNode> homeNodes = new HashSet<DFPointNode>();
    DFDeviceNode homeDevice;
    
    public DFLeafCarouselObject(DFPointNode homePoint, DFDeviceNode homeDev) {
        this.refresh = homePoint.getRefresh();
        this.connStrat = homePoint.getConnStrat();
        this.refChangeStrat = homePoint.getRefreshChangeStrat();
        homeNodes.add(homePoint);
        homeDevice = homeDev;
        homeDevice.addPollBatch(this);
        DSRuntime.run(this);
    }
    
    private DFPointNode getAHomeNode() {
        return homeNodes.iterator().next();
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
    
    public void close(DFPointNode node) {
        node.onDfStopped();
        if (!homeNodes.remove(node)) {
            System.out.println("Node is missing!");
        }
        if (homeNodes.isEmpty()) {
            running = false;
            homeDevice.removePollBatch(this);
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
        boolean success = homeDevice.batchPoll(homeNodes);

        if (!success) {
            for (DFPointNode n: homeNodes) {
                n.onFailed();
            }
        } else {
            for (DFPointNode n: homeNodes) {
                n.onConnected();
            }
        }
        DSRuntime.runDelayed(this, getDelay());
    }

}
