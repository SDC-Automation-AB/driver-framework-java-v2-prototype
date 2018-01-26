package org.iot.dsa.dslink.dframework;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSDouble;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.junit.Before;
import org.junit.Test;

public class ParameterDefinitionTests {
    private static final double DELTA = .00001;
    
    private DSMap parametersRightTypes;
    private DSMap parametersWrongTypes;
    
    private enum Color {RED, GREEN, BLUE}
    
    @Before
    public void before() {
        parametersRightTypes = new DSMap().put("BoolVal", false).put("DoubVal", 2.3).put("StrVal", "Hello!").put("EnumVal", "GREEN");
        parametersWrongTypes = new DSMap().put("DoubVal", false).put("StrVal", 2.3).put("EnumVal", "Hello!").put("BoolVal", "GREEN");
    }
    
    @Test
    public void verifyRightBoolNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParam("BoolVal", DSValueType.BOOL, null, null);
        pdef.verify(parametersRightTypes);
        assertEquals(false, parametersRightTypes.getBoolean("BoolVal"));
    }
    
    @Test
    public void verifyWrongBoolNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParam("BoolVal", DSValueType.BOOL, null, null);
        try {
            pdef.verify(parametersWrongTypes);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter BoolVal"));
        }
        assertEquals("GREEN", parametersWrongTypes.getString("BoolVal"));
    }
    
    @Test
    public void verifyMissingBoolNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParam("BoolVal", DSValueType.BOOL, null, null);
        DSMap parameters = new DSMap();
        try {
            pdef.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Missing Parameter BoolVal"));
        }
        assertTrue(parameters.isEmpty());
    }
    
    @Test
    public void verifyRightDoubNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParam("DoubVal", DSValueType.NUMBER, null, null);
        pdef.verify(parametersRightTypes);
        assertEquals(2.3, parametersRightTypes.getDouble("DoubVal"), DELTA);
    }
    
    @Test
    public void verifyWrongDoubNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParam("DoubVal", DSValueType.NUMBER, null, null);
        try {
            pdef.verify(parametersWrongTypes);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter DoubVal"));
        }
        assertEquals(false, parametersWrongTypes.getBoolean("DoubVal"));
    }
    
    @Test
    public void verifyMissingDoubNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParam("DoubVal", DSValueType.NUMBER, null, null);
        DSMap parameters = new DSMap();
        try {
            pdef.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Missing Parameter DoubVal"));
        }
        assertTrue(parameters.isEmpty());
    }
    
    @Test
    public void verifyRightStrNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParam("StrVal", DSValueType.STRING, null, null);
        pdef.verify(parametersRightTypes);
        assertEquals("Hello!", parametersRightTypes.getString("StrVal"));
    }
    
    @Test
    public void verifyWrongStrNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParam("StrVal", DSValueType.STRING, null, null);
        try {
            pdef.verify(parametersWrongTypes);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter StrVal"));
        }
        assertEquals(2.3, parametersWrongTypes.getDouble("StrVal"), DELTA);
    }
    
    @Test
    public void verifyMissingStrNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParam("StrVal", DSValueType.STRING, null, null);
        DSMap parameters = new DSMap();
        try {
            pdef.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Missing Parameter StrVal"));
        }
        assertTrue(parameters.isEmpty());
    }
    
    @Test
    public void verifyRightEnumNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeEnumParam("EnumVal", DSJavaEnum.valueOf(Color.RED), null, null);
        pdef.verify(parametersRightTypes);
        assertEquals(Color.GREEN, Color.valueOf(parametersRightTypes.getString("EnumVal")));
    }
    
    @Test
    public void verifyWrongEnumNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeEnumParam("EnumVal", DSJavaEnum.valueOf(Color.RED), null, null);
        try {
            pdef.verify(parametersWrongTypes);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter EnumVal"));
        }
        assertEquals("Hello!", parametersWrongTypes.getString("EnumVal"));
    }
    
    @Test
    public void verifyMissingEnumNoDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeEnumParam("EnumVal", DSJavaEnum.valueOf(Color.RED), null, null);
        DSMap parameters = new DSMap();
        try {
            pdef.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Missing Parameter EnumVal"));
        }
        assertTrue(parameters.isEmpty());
    }
    
    @Test
    public void verifyRightBoolWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("BoolVal", DSBool.TRUE, null, null);
        pdef.verify(parametersRightTypes);
        assertEquals(false, parametersRightTypes.getBoolean("BoolVal"));
    }
    
    @Test
    public void verifyWrongBoolWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("BoolVal", DSBool.TRUE, null, null);
        try {
            pdef.verify(parametersWrongTypes);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter BoolVal"));
        }
        assertEquals("GREEN", parametersWrongTypes.getString("BoolVal"));
    }
    
    @Test
    public void verifyMissingBoolWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("BoolVal", DSBool.TRUE, null, null);
        DSMap parameters = new DSMap();
        pdef.verify(parameters);
        assertEquals(true, parameters.getBoolean("BoolVal"));
    }

    @Test
    public void verifyRightDoubWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("DoubVal", DSDouble.valueOf(42), null, null);
        pdef.verify(parametersRightTypes);
        assertEquals(2.3, parametersRightTypes.getDouble("DoubVal"), DELTA);
    }
    
    @Test
    public void verifyWrongDoubWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("DoubVal", DSDouble.valueOf(42), null, null);
        try {
            pdef.verify(parametersWrongTypes);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter DoubVal"));
        }
        assertEquals(false, parametersWrongTypes.getBoolean("DoubVal"));
    }
    
    @Test
    public void verifyMissingDoubWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("DoubVal", DSDouble.valueOf(42), null, null);
        DSMap parameters = new DSMap();
        pdef.verify(parameters);
        assertEquals(42.0, parameters.getDouble("DoubVal"), DELTA);
    }
    
    @Test
    public void verifyRightStrWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("StrVal", DSString.valueOf("Hi"), null, null);
        pdef.verify(parametersRightTypes);
        assertEquals("Hello!", parametersRightTypes.getString("StrVal"));
    }
    
    @Test
    public void verifyWrongStrWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("StrVal", DSString.valueOf("Hi"), null, null);
        try {
            pdef.verify(parametersWrongTypes);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter StrVal"));
        }
        assertEquals(2.3, parametersWrongTypes.getDouble("StrVal"), DELTA);
    }
    
    @Test
    public void verifyMissingStrWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("StrVal", DSString.valueOf("Hi"), null, null);
        DSMap parameters = new DSMap();
        pdef.verify(parameters);
        assertEquals("Hi", parameters.getString("StrVal"));
    }
    
    @Test
    public void verifyRightEnumWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("EnumVal", DSJavaEnum.valueOf(Color.RED), null, null);
        pdef.verify(parametersRightTypes);
        assertEquals(Color.GREEN, Color.valueOf(parametersRightTypes.getString("EnumVal")));
    }
    
    @Test
    public void verifyWrongEnumWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("EnumVal", DSJavaEnum.valueOf(Color.RED), null, null);
        try {
            pdef.verify(parametersWrongTypes);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter EnumVal"));
        }
        assertEquals("Hello!", parametersWrongTypes.getString("EnumVal"));
    }
    
    @Test
    public void verifyMissingEnumWithDefault() {
        ParameterDefinition pdef = ParameterDefinition.makeParamWithDefault("EnumVal", DSJavaEnum.valueOf(Color.RED), null, null);
        DSMap parameters = new DSMap();
        pdef.verify(parameters);
        assertEquals(Color.RED, Color.valueOf(parameters.getString("EnumVal")));
    }

}
