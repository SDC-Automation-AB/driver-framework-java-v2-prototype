package org.iot.dsa.dslink.dframework.bounds;

import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSElement;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/25/2018
 */
public class BooleanBounds implements ParameterBounds<Boolean> {

    public BooleanBounds() {

    }

    @Override
    public boolean validBounds(DSElement val) {
        return val.isBoolean();
    }

    @Override
    public DSElement generateRandom(Random rand) {
        return DSBool.valueOf(rand.nextBoolean());
    }
}
