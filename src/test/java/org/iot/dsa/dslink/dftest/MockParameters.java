package org.iot.dsa.dslink.dftest;

/**
 * @author James (Juris) Puchin
 * Created on 1/19/2018
 */

import org.iot.dsa.node.DSMap;

/**
 * MockParameters class is used to mimic Conn/Dev/Point configs in mock tree.
 * It can be used to both store parameters in order to verify that these
 * are being passed correctly though the DSLink, as well as for building
 * more sophisticated mock device objects. (Slave Devices, etc.)
 */
public class MockParameters {
    DSMap mockParameters;

    /**
     * Empty constructor should generate a random set of valid parameters
     */
    public MockParameters() {
        mockParameters = new DSMap();
    }

    /**
     * DSMap constructor allows for creation of a mock parameters object with desired params
     * @param parameters Set of parameters used to verify correctness of connection
     */
    public MockParameters(DSMap parameters) {
        mockParameters = parameters;
    }

    /**
     * Overwrite this method to enable chekcing correct config
     * @param otherSet
     * @return
     */
    boolean verifyParameters(MockParameters otherSet) {
        return true;
    }
}
