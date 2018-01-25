package org.iot.dsa.dslink.dframework;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class IntegerBoundsTest {

    private static final int SEED = 42;
    private static final int NTRY = 100;

    @Test
    public void validBounds() {
        IntegerBounds ib;
        ib = new IntegerBounds();
        assertEquals(true, ib.validBounds(Integer.MAX_VALUE));
        assertEquals(true, ib.validBounds(Integer.MIN_VALUE));
        ib = new IntegerBounds(-10, 15);
        assertEquals(false, ib.validBounds(18));
        assertEquals(true, ib.validBounds(-1));
    }

    @Test
    public void generateRandom() {
        Random rd = new Random(SEED);
        IntegerBounds ib = new IntegerBounds(-3, 42);
        for (int n = 0; n < NTRY; n++) {
            int nextInt = ib.generateRandom(rd);
            System.out.println(nextInt);
            assertEquals(true, ib.validBounds(nextInt));
        }
    }
}