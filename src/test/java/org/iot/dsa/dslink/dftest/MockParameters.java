package org.iot.dsa.dslink.dftest;

/**
 * @author James (Juris) Puchin
 * Created on 1/19/2018
 */

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.node.DSMap;

import java.util.List;
import java.util.Random;

/**
 * MockParameters class is used to mimic Conn/Dev/Point configs in mock tree.
 * It can be used to both store parameters in order to verify that these
 * are being passed correctly though the DSLink, as well as for building
 * more sophisticated mock device objects. (Slave Devices, etc.)
 */
public class MockParameters {

    DSMap mockParameters;
    List<ParameterDefinition> parDefs;
    private final int MAX_RETRIES = 100;

    /**
     * Empty constructor generates a blank parameter set.
     */
    public MockParameters() {
        mockParameters = new DSMap();
    }

    public MockParameters makeRandomParamSet(Random rand) {
        throw new RuntimeException("Must overwrite makeRandomParamSet(Random rand) constructor!");
    }

    /**
     * Produced a random set of mockParameters for the desired EditableNode
     * @param clazz EditableNode class to make pars for
     * @param rand Random object for par generation
     */
    public MockParameters(Class<? extends EditableNode> clazz, Random rand) {
        parDefs = DFUtil.getDummyInstance(clazz).getParameterDefinitions();
        int attempt = 0;
        do {
            if (attempt++ > MAX_RETRIES) throw new RuntimeException("Failed to generate valid parameters, for class " + clazz);
            mockParameters = new DSMap();
            for (ParameterDefinition prDef : parDefs) {
                mockParameters.put(prDef.name, prDef.generateRandom(rand));
            }
        } while (!parametersValid());
    }

    private boolean parametersValid() {
        boolean valid = true;
        try {
            EditableNode.verifyParameters(mockParameters, parDefs);
        } catch (RuntimeException e) {
            valid = false;
        }
        return valid;
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

    public DSMap getParamMap() {
        return mockParameters;
    }

    public boolean copyAll(DSMap copy) {
        if (copy == null) return false;
        copy.putAll(mockParameters);
        return true;
    }
}
