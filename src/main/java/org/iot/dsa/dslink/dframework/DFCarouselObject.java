package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.DFHelpers.DFConnStrat;
import org.iot.dsa.dslink.dframework.DFHelpers.DFRefChangeStrat;

public abstract class DFCarouselObject implements Runnable {
    long refresh;
    DFConnStrat connStrat;
    DFRefChangeStrat refChangeStrat;
    boolean running = true;

    public static long getDelay() {
        return 50;
    }
}
