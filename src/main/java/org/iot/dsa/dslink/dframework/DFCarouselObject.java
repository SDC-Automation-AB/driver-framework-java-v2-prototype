package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.DFHelpers.DFConnStrat;
import org.iot.dsa.dslink.dframework.DFHelpers.DFRefChangeStrat;

public abstract class DFCarouselObject implements Runnable {
    protected long refresh;
    protected DFConnStrat connStrat;
    protected DFRefChangeStrat refChangeStrat;
    protected boolean running = true;

    abstract public void close();
}
