package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.bounds.IntegerBounds;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSLong;
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
        assertEquals(true, ib.validBounds(DSLong.valueOf(Integer.MAX_VALUE)));
        assertEquals(true, ib.validBounds(DSLong.valueOf(Integer.MIN_VALUE)));
        ib = new IntegerBounds(-10, 15);
        assertEquals(false, ib.validBounds(DSLong.valueOf(18)));
        assertEquals(true, ib.validBounds(DSLong.valueOf(-1)));
    }

    @Test
    public void generateRandom() {
        Random rd = new Random(SEED);
        IntegerBounds ib = new IntegerBounds(-3, 42);
        for (int n = 0; n < NTRY; n++) {
            DSElement nextInt = ib.generateRandom(rd);
            System.out.println(nextInt);
            assertEquals(true, ib.validBounds(nextInt));
        }
    }
}