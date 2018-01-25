package org.iot.dsa.dslink.dframework;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public interface ParameterBounds <T> {
    /**
     * Check if a given value is in bounds
     * @param val value to check
     * @return true if in bounds
     */
    boolean validBounds(T val);

    /**
     * Generates an appropriate value of the right value type
     * @param rand Random object for generation
     * @return a value in bounds
     */
    T generateRandom(Random rand);
}
