package org.iot.dsa.dslink.dframework.bounds;

import org.iot.dsa.node.DSDouble;
import org.iot.dsa.node.DSElement;

import java.util.Random;

public class FloatBounds implements ParameterBounds<Float> {

    private Float min = -Float.MAX_VALUE;
    private Float max = Float.MAX_VALUE;

    private final static Float RAND_MAX = 10000f;
    private final static Float RAND_MIN = -10000f;

    public FloatBounds() {
    }

    /**
     * Creates an Float bounds object with min and max set.
     * @param min inclusive
     * @param max inclusive
     */
    public FloatBounds(Float min, Float max) {
        if (min != null) this.min = min;
        if (max != null) this.max = max;
        if (this.min >= this.max) throw new RuntimeException("Min bound has to be less than max.");
    }

    @Override
    public boolean validBounds(DSElement val) {
        Float dVal;
        if (val.isNumber())
            dVal = val.toFloat();
        else
            return false;

        return dVal <= max && dVal >= min;
    }

    @Override
    public DSElement generateRandom(Random rand) {
        Float rMax = max > RAND_MAX && min < RAND_MAX ? RAND_MAX : max;
        Float rMin = min < RAND_MIN && max > RAND_MIN ? RAND_MIN : min;
        return DSDouble.valueOf(rand.nextFloat() * (rMax - rMin) + rMin);
    }
}
