package org.iot.dsa.dslink.dframework.bounds;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSLong;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class LongBounds implements ParameterBounds<Long> {

    private Long min;
    private Long max;


    private final static long RAND_MAX = 1000;
    private final static long RAND_MIN = -1000;

    public LongBounds() {
        this.min = Long.MIN_VALUE;
        this.max = Long.MAX_VALUE;
    }

    /**
     * Creates an Long bounds object with min and max set.
     * @param min inclusive
     * @param max inclusive
     */
    public LongBounds(Long min, Long max) {
        if (min >= max) throw new RuntimeException("Min bound has to be less than max.");
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validBounds(DSElement val) {
        Long iVal;
        if ( val.isLong() || val.isInt() )
            iVal = val.toLong();
        else
            return false;

        return iVal <= max && iVal >= min;
    }

    @Override
    public DSElement generateRandom(Random rand) {
        Long rMax = max > RAND_MAX && min < RAND_MAX ? RAND_MAX : max;
        Long rMin = min < RAND_MIN && max > RAND_MIN ? RAND_MIN : min;
        Double answer = rand.nextDouble() * (rMax - rMin) + rMin;
        return DSLong.valueOf(answer.longValue());
    }
}
