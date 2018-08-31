package org.iot.dsa.dslink.dframework.bounds;

import org.iot.dsa.node.*;

import java.util.*;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class EnumBounds implements ParameterBounds<DSIEnum> {

    private Set<String> stringSet;

    public EnumBounds(EnumSet validVals) {
        stringifySet(validVals);
    }

    public EnumBounds(Set<String> validVals) {
        stringSet = validVals;
    }

    public EnumBounds(DSIEnum allValid) {
        DSList lst = new DSList();
        Iterator itr = allValid.getEnums(lst).iterator();
        stringSet = new HashSet<String>();
        while (itr.hasNext()) {
            Object next = itr.next();
            DSElement nextEl;

            if (next instanceof DSElement) nextEl = (DSElement) next;
            else throw new RuntimeException("EnumBounds needs a proper DSIEnum to work");

            if (nextEl.isString()) stringSet.add(nextEl.toString());
            else throw new RuntimeException("EnumBounds needs a DSIEnum containing Strings");
        }
    }
//This constructor overload might be faster, but complains about unchecked operations.
//    public EnumBounds(DSJavaEnum allValid) {
//        Enum en = allValid.toEnum();
//        if (en == null) throw new RuntimeException("EnumBounds needs an enum to work.");
//        EnumSet eSet = EnumSet.allOf(en.getClass());
//        stringifySet(eSet);
//    }

    private void stringifySet(EnumSet set) {
        stringSet = new HashSet<String>();
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
