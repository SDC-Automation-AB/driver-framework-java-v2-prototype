package org.iot.dsa.dslink.example;

import java.util.LinkedList;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.example.DFWHelpers.DFWConnStrat;
import org.iot.dsa.dslink.example.DFWHelpers.DFWRefChangeStrat;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSMap;

public abstract class DFWConnection implements Runnable {
    LinkedList <DFWDevice> children;
    int refresh;
    DFWConnStrat connStrat;
    DFWRefChangeStrat refChangeStrat;
    boolean connected = false;
    boolean running = true;
    DSMap parameters;
    DSIValue status;
    
    public DFWConnection(DSMap parameters) {
        this.parameters = parameters;
        run();
    }
    
    private long getDelay() {
        return 5000;
    }
    
    public void close() {
        running = false;
        cleanupChildren();
        closeConnection();
    }

    abstract public boolean createConnection(DSMap parameters);
    abstract public boolean ping();
    abstract public void closeConnection();
    
    abstract public void onConnected();
    abstract public void onFailed();
    
    @Override
    public void run() {
        if (!running) {
            //TODO Stop childern
            //closeConnection();
            return;
        }
        if (connected) {
            connected = ping();
            if (!connected) {
                onFailed();
            }
        } else {
            connected = createConnection(parameters);
            if (connected) {
                onConnected();
            } else {
                onFailed();
            }
        }
        DSRuntime.runDelayed(this, getDelay());
    }
}
