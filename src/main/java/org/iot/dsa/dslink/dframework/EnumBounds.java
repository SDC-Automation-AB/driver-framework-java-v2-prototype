package org.iot.dsa.dslink.dframework;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIEnum;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSString;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class EnumBounds implements ParameterBounds<DSIEnum> {

    private Set<String> stringSet;

    EnumBounds(EnumSet validVals) {
        stringifySet(validVals);
    }

    EnumBounds(Set<String> validVals) {
        stringSet = validVals;
    }

    EnumBounds(DSJavaEnum allValid) {
        Enum en = allValid.toEnum();
        if (en == null) throw new RuntimeException("EnumBounds needs an enum to work.");
        EnumSet eSet = EnumSet.allOf(en.getClass());
        stringifySet(eSet);
    }

    private void stringifySet(EnumSet set) {
        stringSet = new HashSet<>();
        for (Object validVal : set) {
            stringSet.add(validVal.toString());
        }
    }

    @Override
    public boolean validBounds(DSElement val) {
        if (val.isString())
            return stringSet.contains(val.toString());
        else
            return false;
    }

    @Override
    public DSElement generateRandom(Random rand) {
        int v = rand.nextInt(stringSet.size());
        Object a = stringSet.toArray()[v];
        return DSString.valueOf(a);
    }
}
