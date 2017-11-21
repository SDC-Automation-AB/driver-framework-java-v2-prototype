package org.iot.dsa.dslink.dftest;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.fail;

/**
 * @author James (Juris) Puchin
 * Created on 11/20/2017
 */
public class DSRuntimeTest implements FailCallback {
    private static final long ITERATION_LENGTH = 100;
    private static final long MAX_DELAY_ERROR = 100;
    private static final long TOTAL_TEST_TIME = 60000;
    private static final int THREAD_COUNT = 100;
    private boolean failed = false;
    private static ArrayList<TestRunner> runners = new ArrayList<TestRunner>(THREAD_COUNT);

    @Override
    public void failed() {
        failed = true;
    }

    @Test
    public void testDSRuntime() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            runners.add(i, new TestRunner(this, ITERATION_LENGTH, MAX_DELAY_ERROR, TOTAL_TEST_TIME));
        }

        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (TestRunner runner : runners) {
                try {
                    runner.stop = true;
                } catch (NullPointerException e) {
                    //No big deal
                }
            }
        }
        if (failed) fail();
    }
}
