package org.iot.dsa.dslink.dframework;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSMainNode;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DFLeafCarouselObject extends DFCarouselObject {

    private Set<DFPointNode> homeNodes = new HashSet<DFPointNode>();
    private final DFDeviceNode homeDevice;
    private BatchPollRunner runner = new BatchPollRunner();
    private Map<DFPointNode, Boolean> successes = new ConcurrentHashMap<DFPointNode, Boolean>();
    //private AtomicBoolean success = new AtomicBoolean(false);

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

            if (runnerNotRunning()) {
                Map<DFPointNode, Boolean> latestSuccesses = this.successes;
                for (DFPointNode n : homeNodes) {
                    if (latestSuccesses.get(n) != null && latestSuccesses.get(n)) {
                        n.onConnected();
                    } else {
                        n.onFailed();
                    }
                }

                DSRuntime.runDelayed(runner,0);
            }

            DSRuntime.runDelayed(this, getAHomeNode().getPollRate());
        }
    }

    private boolean runnerNotRunning() {
        if (runner == null) {
            runner = new BatchPollRunner();
            homeDevice.warn("Runner is dead: " + homeDevice.getName());
            return false;
        } else {
            return !runner.running.get();
        }
    }

    private class BatchPollRunner implements Runnable {

        AtomicBoolean running = new AtomicBoolean();

        @Override
        public void run() {
            //synchronized (homeDevice.pingConLock) {
                try {
                    running.set(true);
                    successes = homeDevice.batchPoll(homeNodes);
                } finally {
                    running.set(false);
                }
           // }
        }
    }
}
