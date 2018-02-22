package org.iot.dsa.dslink.dframework;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSMainNode;
import java.util.HashSet;
import java.util.Set;

public class DFLeafCarouselObject extends DFCarouselObject {

    private Set<DFPointNode> homeNodes = new HashSet<DFPointNode>();
    private final DFDeviceNode homeDevice;

    DFLeafCarouselObject(DFPointNode homePoint, DFDeviceNode homeDev) {
        homeDevice = homeDev;
        synchronized (homeDevice) {
            homeNodes.add(homePoint);
        }
        homeDevice.addPollBatch(this, homePoint.getPollRate());
        DSRuntime.run(this);
    }

    void addHomeNode(DFPointNode node) {
        synchronized (homeDevice) {
            homeNodes.add(node);
        }
    }

    private DFPointNode getAHomeNode() {
        return homeNodes.iterator().next();
    }

    private boolean amIanOrphan() {
        DFPointNode homeNode = getAHomeNode();
        if (homeNode.getParent() instanceof DFDeviceNode) {
            DFDeviceNode par = (DFDeviceNode) homeNode.getParent();
            return !par.isNodeConnected();
        } else if (homeNode.getParent() instanceof DSMainNode) {
            return false;
        } else {
            throw new RuntimeException("Wrong parent class");
        }
    }

    void close(DFPointNode node) {
        long pollRate = node.getPollRate();
        node.onDfStopped();
        if (!homeNodes.remove(node)) {
            System.out.println("Node is missing!");
        }
        if (homeNodes.isEmpty()) {
            running = false;
            homeDevice.removePollBatch(this, pollRate);
        }
    }

    private boolean containsNode(String name) {
        for (DFPointNode node : homeNodes) {
            if (node.getName().equals(name)) return true;
        }
        return false;
    }

    @Override
    public void run() {
        synchronized (homeDevice) {
            if (!running) {
                return;
            }

            if (amIanOrphan()) {
                for (DFPointNode n : homeNodes) {
                    n.stopCarObject();
                }
                return;
            }

            //TODO: IMPORTANT!!! Make batch pool asynch
            //Can add redundant check for isNodeStopped here
            boolean success = homeDevice.batchPoll(homeNodes);

            if (!success) {
                for (DFPointNode n : homeNodes) {
                    n.onFailed();
                }
            } else {
                for (DFPointNode n : homeNodes) {
                    n.onConnected();
                }
            }
            DSRuntime.runDelayed(this, getAHomeNode().getPollRate());
        }
    }
    
}
