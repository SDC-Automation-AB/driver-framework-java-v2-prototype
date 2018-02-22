package org.iot.dsa.dslink.dframework;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;

import java.util.concurrent.atomic.AtomicBoolean;

public class DFBranchCarouselObject extends DFCarouselObject {

    private final DFBranchNode homeNode;
    private PingConRunner runner = new PingConRunner();
    private AtomicBoolean connected = new AtomicBoolean(false);
    private DFBranchDelayCalculator calculator;

    DFBranchCarouselObject(DFBranchNode home) {
        homeNode = home;
        this.calculator = homeNode.getPingReconnectCalculator(this);
        DSRuntime.run(this);
    }

    private boolean iAmAnOrphan() {
        if (homeNode.getParent() instanceof DFAbstractNode) {
            DFAbstractNode par = (DFAbstractNode) homeNode.getParent();
            return !par.isNodeConnected();
        } else if (homeNode.getParent() instanceof DSMainNode) {
            return false;
        } else {
            throw new RuntimeException("Wrong parent class: " + homeNode.getParent().getName() + "-" + homeNode.getParent().getClass());
        }
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
            if (runnerNotRunning()) {
                DSRuntime.runDelayed(runner, 0);
            }
            if (connected.get()) {
                homeNode.onConnected();
                killOrSpawnChildren(false);
            } else {
                homeNode.onFailed();
                killOrSpawnChildren(true);
            }
            DSRuntime.runDelayed(this, calculator.getDelay());
        }
    }

    public boolean isConnected() {
        return connected.get();
    }

    private boolean runnerNotRunning() {
        return !runner.running.get();
    }

    private class PingConRunner implements Runnable {

        AtomicBoolean running;

        @Override
        public void run() {
            try {
                running.set(true);
                if (connected.get()) {
                    connected.set(homeNode.ping());
                } else {
                    connected.set(homeNode.createConnection());
                }
            } finally {
                running.set(false);
            }
        }
    }
}
