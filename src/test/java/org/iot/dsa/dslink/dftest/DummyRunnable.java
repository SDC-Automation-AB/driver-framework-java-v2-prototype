package org.iot.dsa.dslink.dftest;

import org.iot.dsa.DSRuntime;

import java.sql.Timestamp;

/**
 * @author Daniel
 * Created on 11/20/2017
 */
public class DummyRunnable implements Runnable {

    long i = 0;

    public DummyRunnable() {
        DSRuntime.run(this);
    }

    @Override
    public void run() {
        long curTimestamp = System.currentTimeMillis();
        i++;
        System.out.println(new Timestamp(curTimestamp) + ": Hello " + i);
        DSRuntime.runDelayed(this, 1000);
    }

}
