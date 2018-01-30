package org.iot.dsa.dslink.dframework.bounds;

import org.iot.dsa.node.DSDouble;
import org.iot.dsa.node.DSElement;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class DoubleBounds implements ParameterBounds<Double> {

    private Double min;
    private Double max;

    private final static double RAND_MAX = 10000;
    private final static double RAND_MIN = -10000;

    public DoubleBounds() {
        this.min = Double.MIN_VALUE;
        this.max = Double.MAX_VALUE;
    }

    /**
     * Creates an Double bounds object with min and max set.
     * @param min inclusive
     * @param max inclusive
     */
    public DoubleBounds(Double min, Double max) {
        if (min >= max) throw new RuntimeException("Min bound has to be less than max.");
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validBounds(DSElement val) {
        Double dVal;
        if ( val.isNumber() )
            dVal = val.toDouble();
        else
            return false;

        return dVal <= max && dVal >= min;
    }

    @Override
    public DSElement generateRandom(Random rand) {
        Double rMax = max > RAND_MAX && min < RAND_MAX ? RAND_MAX : max;
        Double rMin = min < RAND_MIN && max > RAND_MIN ? RAND_MIN : min;
        return DSDouble.valueOf(rand.nextDouble() * (rMax - rMin) + rMin);
    }
}
