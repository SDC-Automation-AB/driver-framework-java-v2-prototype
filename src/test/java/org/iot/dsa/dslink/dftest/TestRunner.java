package org.iot.dsa.dslink.dftest;

import org.iot.dsa.DSRuntime;

import java.sql.Timestamp;

/**
 * @author James (Juris) Puchin
 * Created on 11/20/2017
 */
public class TestRunner implements Runnable {

    private long i = 0;
    private long waitTime;
    private final long maxDelay;
    private long prevTimestamp;
    private long testEndTime;
    private final FailCallback failCallbackHandle;
    boolean stop = false;

    public TestRunner(FailCallback failHandle, long waitTime, long maxDelay, long totalTestTime) {
        this.waitTime = waitTime;
        this.maxDelay = maxDelay;
        this.failCallbackHandle = failHandle;
        this.prevTimestamp = System.currentTimeMillis();
        this.testEndTime = prevTimestamp+ totalTestTime;
        DSRuntime.run(this);
    }

    @Override
    public void run() {
        long curTimestamp = System.currentTimeMillis();
        i++;
        System.out.println(new Timestamp(curTimestamp) + ": Hello " + i);

        if (curTimestamp - prevTimestamp > waitTime + maxDelay) {
            synchronized (failCallbackHandle) {
                failCallbackHandle.failed();
                failCallbackHandle.notify();
            }
        } else if (curTimestamp < testEndTime) {
            prevTimestamp = curTimestamp;
            if (!stop) DSRuntime.runDelayed(this, waitTime);
        } else {
            synchronized (failCallbackHandle) {
                failCallbackHandle.notify();
            }
        }
    }
}