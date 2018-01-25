package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.bounds.EnumBounds;
import org.iot.dsa.node.DSIEnum;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSString;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class EnumBoundsTest {

    private static final int SEED = 42;
    private static final int NTRY = 25;

    enum EnumOne {
        A, B, C, D
    }

    enum EnumTwo {
        E, F, G, H
    }

    @Test
    public void validBounds() {
        DSJavaEnum a = DSJavaEnum.valueOf(EnumOne.A);
        DSIEnum b = DSJavaEnum.valueOf(EnumOne.B);
        DSJavaEnum f = DSJavaEnum.valueOf(EnumTwo.F);
        EnumBounds one = new EnumBounds(b);
        EnumBounds two = new EnumBounds(f);
        assertEquals(true, one.validBounds(DSString.valueOf(a)));
        assertEquals(true, one.validBounds(DSString.valueOf(b)));
        assertEquals(false, two.validBounds(DSString.valueOf(b)));
        assertEquals(true, two.validBounds(DSString.valueOf(f)));
    }

    @Test
    public void generateRandom() {
        DSJavaEnum a = DSJavaEnum.valueOf(EnumOne.A);
        DSIEnum f = DSJavaEnum.valueOf(EnumTwo.F);
        EnumBounds one = new EnumBounds(a);
        EnumBounds two = new EnumBounds(f);
        Random rand = new Random(SEED);
        for (int i = 0; i < NTRY; i++) {
            assertEquals(true, one.validBounds(one.generateRandom(rand)));
            assertEquals(false, two.validBounds(one.generateRandom(rand)));
        }
    }
}