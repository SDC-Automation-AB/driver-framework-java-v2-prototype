package org.iot.dsa.dslink.dframework;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSLong;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class LongBoundsTest {

    private static final int SEED = 42;
    private static final int NTRY = 100;

    @Test
    public void validBounds() {
        LongBounds ib;
        ib = new LongBounds();
        assertEquals(true, ib.validBounds(DSLong.valueOf(Long.MAX_VALUE)));
        assertEquals(true, ib.validBounds(DSLong.valueOf(Long.MIN_VALUE)));
        ib = new LongBounds(-10l, 15l);
        assertEquals(false, ib.validBounds(DSLong.valueOf(18)));
        assertEquals(true, ib.validBounds(DSLong.valueOf(-1)));
    }

    @Test
    public void generateRandom() {
        Random rd = new Random(SEED);
        LongBounds ib = new LongBounds(-3l, 42l);
        for (int n = 0; n < NTRY; n++) {
            DSElement nextInt = ib.generateRandom(rd);
            System.out.println(nextInt);
            assertEquals(true, ib.validBounds(nextInt));
        }
    }
}