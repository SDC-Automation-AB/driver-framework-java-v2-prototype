package org.iot.dsa.dslink.example;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.example.DFWHelpers.DFWConnStrat;
import org.iot.dsa.dslink.example.DFWHelpers.DFWRefChangeStrat;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;

public class DFCarouselObject implements Runnable {


    private DFAbstractNode homeNode;
    private long refresh;
    private DFWConnStrat connStrat;
    private DFWRefChangeStrat refChangeStrat;
    private boolean connected = false;
    private boolean running = true;
    DSIValue status;
    
    public DFCarouselObject(DFAbstractNode home) {
        homeNode = home;
        this.refresh = home.getRefresh();
        this.connStrat = home.getConnStrat();
        this.refChangeStrat = home.getRefreshChangeStrat();

        for (DSInfo info : homeNode) {
            DSIObject o = info.getObject();
            if (o instanceof DFAbstractNode) {
                DFAbstractNode child = (DFAbstractNode) o;
                if (!child.getIsStopped()) {
                    child.startConnection();
                }
            }
        }
        run();
    }
    
    private long getDelay() {
        return 5000;
    }
    
    public void close() {
        running = false;
        //TODO Stop childern
        homeNode.closeConnection();
        homeNode.onDfStopped();
    }
    
//    public void onConnected() { }
//    public void onFailed() { }
    
    @Override
    public void run() {
        if (!running) {
            return;
        }
        if (connected) {
            connected = homeNode.ping();
            if (!connected) {
                homeNode.onFailed();
            }
        } else {
            connected = homeNode.createConnection();
            if (connected) {
                homeNode.onConnected();
            } else {
                homeNode.onFailed();
            }
        }
        DSRuntime.runDelayed(this, getDelay());
    }
}
