package org.iot.dsa.dslink.dframework;

public abstract class DFCarouselObject implements Runnable {
    boolean running = true;
    protected DFDelayCalculator calculator;

    public long getDelay() {
        return calculator.getDelay();
    }
}
