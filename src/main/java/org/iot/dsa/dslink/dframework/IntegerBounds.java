package org.iot.dsa.dslink.dframework;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class IntegerBounds implements ParameterBounds<Integer> {
    private Integer min;
    private Integer max;

    IntegerBounds() {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
    }

    /**
     * Creates an Integer bounds object with min and max set.
     * @param min inclusive
     * @param max inclusive
     */
    IntegerBounds(Integer min, Integer max) {
        if (min >= max) throw new RuntimeException("Min bound has to be less than max.");
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validBounds(Integer val) {
        return val <= max && val >= min;
    }

    @Override
    public Integer generateRandom(Random rand) {
        if (rand.nextBoolean()) return rand.nextInt(max);
        else return -rand.nextInt(-min);
    }
}
