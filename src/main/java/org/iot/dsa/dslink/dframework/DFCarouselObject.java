package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.DFHelpers.DFConnStrat;
import org.iot.dsa.dslink.dframework.DFHelpers.DFRefChangeStrat;

import java.util.concurrent.atomic.AtomicLong;

public abstract class DFCarouselObject implements Runnable {
    private static AtomicLong s_id = new AtomicLong(0);
    private long id = DFCarouselObject.s_id.addAndGet(1);

    protected long refresh;
    protected DFConnStrat connStrat;
    protected DFRefChangeStrat refChangeStrat;
    protected boolean running = true;

    @Override
    public int hashCode() {return Long.valueOf(id).hashCode();}
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o instanceof DFCarouselObject) { return this.id == ((DFCarouselObject) o).id;}
        return false;
    }
}
