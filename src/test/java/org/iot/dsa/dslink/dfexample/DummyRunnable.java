package org.iot.dsa.dslink.dfexample;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.logging.DSLogger;

public class DummyRunnable extends DSLogger implements Runnable {
    
    long i = 0;
    
    public DummyRunnable() {
        DSRuntime.run(this);
    }

    @Override
    public void run() {
        info("Hello " + i);
        i++;
        DSRuntime.runDelayed(this, 1000);
    }

}
