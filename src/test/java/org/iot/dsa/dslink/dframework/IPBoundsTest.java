package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.bounds.IPBounds;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSString;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class IPBoundsTest {

    private static final int SEED = 42;
    private static final int NTRY = 100;

    @Test
    public void validBounds() {
        IPBounds ib;
        ib = new IPBounds();
        assertEquals(false, ib.validBounds(DSString.valueOf("4.1.1.1.46")));
        assertEquals(true, ib.validBounds(DSString.valueOf("1.1.1.1")));
        assertEquals(true, ib.validBounds(DSString.valueOf("255.255.255.255")));
        assertEquals(false, ib.validBounds(DSString.valueOf("1.1.n0.1")));
        assertEquals(false, ib.validBounds(DSString.valueOf("n.a.e.v")));
        assertEquals(false, ib.validBounds(DSString.valueOf("275.1.1.1")));
        assertEquals(false, ib.validBounds(DSString.valueOf("junks")));
    }

    @Test
    public void generateRandom() {
        Random rd = new Random(SEED);
        IPBounds ib = new IPBounds();
        for (int n = 0; n < NTRY; n++) {
            DSElement ip = ib.generateRandom(rd);
            System.out.println(ip);
            assertEquals(true, ib.validBounds(ip));
        }
    }
}