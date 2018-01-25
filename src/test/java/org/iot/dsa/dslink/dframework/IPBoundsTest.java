package org.iot.dsa.dslink.dframework;

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
        assertEquals(false, ib.validBounds("4.1.1.1.46"));
        assertEquals(true, ib.validBounds("1.1.1.1"));
        assertEquals(true, ib.validBounds("255.255.255.255"));
        assertEquals(false, ib.validBounds("1.1.n0.1"));
        assertEquals(false, ib.validBounds("n.a.e.v"));
        assertEquals(false, ib.validBounds("275.1.1.1"));
        assertEquals(false, ib.validBounds("junks"));
    }

    @Test
    public void generateRandom() {
        Random rd = new Random(SEED);
        IPBounds ib = new IPBounds();
        for (int n = 0; n < NTRY; n++) {
            String ip = ib.generateRandom(rd);
            System.out.println(ip);
            assertEquals(true, ib.validBounds(ip));
        }
    }
}