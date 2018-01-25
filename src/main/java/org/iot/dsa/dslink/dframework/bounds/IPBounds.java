package org.iot.dsa.dslink.dframework.bounds;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSString;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class IPBounds implements ParameterBounds<String> {

    //host can not be all binary 0s refers to that network (e.g. 131.107.0.0)
    //host can not be all binary 1s refers to broadcast on that network (e.g. 213.142.199.255)
    //all 0s in network mean this network (e.g. 0.0.12.145)
    //all 1s in network means all networks (e.g. 255.255.1.2)
    @Override
    public boolean validBounds(DSElement val) {
        String sVal;
        if (val.isString())
            sVal = val.toString();
        else
            return false;

        if (sVal.toLowerCase().equals("localhost")) return true;

        String[] vals = sVal.split("\\.");
        if (vals.length != 4) return false;
        for (String s : vals) {
            Integer i;
            try {
                i = new Integer(s);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (i < 0 || i > 255) return false;
        }
        return true;
    }

    private int getIPVal(Random rand) {
        return rand.nextInt(254) + 1;
    }

    @Override
    public DSElement generateRandom(Random rand) {
        int first = 127;
        while (first == 127) first = getIPVal(rand);
        StringBuilder ip = new StringBuilder();
        ip.append(first);
        for (int i = 0; i < 3; i++) {
            ip.append(".");
            ip.append(getIPVal(rand));
        }
        return DSString.valueOf(ip.toString());
    }
}
