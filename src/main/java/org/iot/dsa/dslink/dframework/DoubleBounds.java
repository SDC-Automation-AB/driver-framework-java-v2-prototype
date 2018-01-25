package org.iot.dsa.dslink.dframework;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class DoubleBounds implements ParameterBounds<Double> {

    private Double min;
    private Double max;

    DoubleBounds() {
        this.min = Double.MIN_VALUE;
        this.max = Double.MAX_VALUE;
    }

    /**
     * Creates an Double bounds object with min and max set.
     * @param min inclusive
     * @param max inclusive
     */
    DoubleBounds(Double min, Double max) {
        if (min >= max) throw new RuntimeException("Min bound has to be less than max.");
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validBounds(Double val) {
        return val <= max && val >= min;
    }

    @Override
    public Double generateRandom(Random rand) {
        return rand.nextDouble() * (max - min) + min;
    }
}
