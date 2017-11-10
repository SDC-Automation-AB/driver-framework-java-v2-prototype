package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.DFHelpers.DFConnStrat;
import org.iot.dsa.dslink.dframework.DFHelpers.DFRefChangeStrat;

import java.util.concurrent.atomic.AtomicLong;

public abstract class DFCarouselObject implements Runnable {
    private static AtomicLong s_id = new AtomicLong(0);
    private long id = DFCarouselObject.s_id.addAndGet(1);

    long refresh;
    DFConnStrat connStrat;
    DFRefChangeStrat refChangeStrat;
    boolean running = true;

    //TODO: Figure out if overwriting hash code is OK
    //It's ok, maybe we should revert to Java's default hash code
    @Override
    public int hashCode() {return Long.valueOf(id).hashCode();}
    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o instanceof DFCarouselObject) { return this.id == ((DFCarouselObject) o).id;}
        return false;
    }
}
