package org.iot.dsa.dslink.dframework;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class DoubleBoundsTest {

    private static final int SEED = 42;
    private static final int NTRY = 100;

    @Test
    public void validBounds() {
        DoubleBounds ib;
        ib = new DoubleBounds();
        assertEquals(true, ib.validBounds(Double.MAX_VALUE));
        assertEquals(true, ib.validBounds(Double.MIN_VALUE));
        ib = new DoubleBounds(-10.554, 15.432);
        assertEquals(false, ib.validBounds(18.134));
        assertEquals(true, ib.validBounds(-1.415));
    }

    @Test
    public void generateRandom() {
        Random rd = new Random(SEED);
        DoubleBounds ib = new DoubleBounds(-3.124, 42.222);
        for (int n = 0; n < NTRY; n++) {
            double nextDouble = ib.generateRandom(rd);
            System.out.println(nextDouble);
            assertEquals(true, ib.validBounds(nextDouble));
        }
    }
}