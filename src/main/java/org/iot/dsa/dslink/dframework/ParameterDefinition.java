package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.bounds.EnumBounds;
import org.iot.dsa.dslink.dframework.bounds.ParameterBounds;
import org.iot.dsa.node.*;
import org.iot.dsa.node.action.DSAction;

import java.util.Random;

public class ParameterDefinition {

    public final String name;
    public final DSValueType type;
    public final DSIEnum enumtype;
    public final DSIValue def;
    private final ParameterBounds<?> bounds;
    public final String description;
    public final String placeholder;


    protected ParameterDefinition(String name, DSValueType type, DSIEnum enumtype, DSIValue def, ParameterBounds<?> bounds,
                                  String description, String placeholder) {
        super();
        this.name = name;
        this.type = type;
        this.enumtype = enumtype;
        this.def = def;
        this.bounds = bounds;
        this.description = description;
        this.placeholder = placeholder;
    }

    public static ParameterDefinition makeParam(String name, DSValueType type, String description, String placeholder) {
        return new ParameterDefinition(name, type, null, null, null, description, placeholder);
    }

    /**
     * Automatically includes ParameterBounds object.
     */
    public static ParameterDefinition makeEnumParam(String name, DSIEnum enumtype, String description, String placeholder) {
        return new ParameterDefinition(name, null, enumtype, null, new EnumBounds(enumtype), description, placeholder);
    }

    public static ParameterDefinition makeParamWithDefault(String name, DSIValue def, String description, String placeholder) {
        return new ParameterDefinition(name, null, null, def, null, description, placeholder);
    }

    public static ParameterDefinition makeParamWithBounds(String name, DSValueType type, ParameterBounds<?> bounds, String description, String placeholder) {
        return new ParameterDefinition(name, type, null, null, bounds, description, placeholder);
    }

    public static ParameterDefinition makeParamWithBoundsAndDef(String name, DSIValue def, ParameterBounds<?> bounds, String description, String placeholder) {
        return new ParameterDefinition(name, null, null, def, bounds, description, placeholder);
    }

    public DSMetadata addToAction(DSAction action, DSIValue defOverride) {
        DSMetadata metadata;
        if (defOverride != null) {
            DSIEnum et = null;
            if (enumtype != null) {
                et = enumtype;
            } else if (def instanceof DSIEnum) {
                et = (DSIEnum) def;
            }

            if (et == null) {
                metadata = action.addDefaultParameter(name, defOverride, description);
            } else {
                DSIEnum def;
                if (et instanceof DSJavaEnum) {
                    def = ((DSJavaEnum) et).valueOf(defOverride.toElement().toString());
                } else if (et instanceof DSFlexEnum) {
                    def = ((DSFlexEnum) et).valueOf(defOverride.toElement().toString());
                } else {
                    throw new RuntimeException("Unexpected runtime class for DSIEnum");
                }
                metadata = action.addDefaultParameter(name, (DSIValue) def, description);
            }
        } else if (def != null) {
            metadata = action.addDefaultParameter(name, def, description);
        } else if (enumtype != null) {
            metadata = action.addParameter(name, (DSIValue) enumtype, description);
        } else {
            metadata = action.addParameter(name, type, description);
        }

        if (placeholder != null) {
            metadata.setPlaceHolder(placeholder);
        }
        return metadata;
    }

    public DSMetadata addToAction(DSAction action) {
        return addToAction(action, null);
    }

    public void verify(DSMap parameters) {
        DSElement paramVal = parameters.get(name);
        if (paramVal == null) {
            if (def != null) {
                paramVal = def.toElement();
                parameters.put(name, paramVal);
            } else {
                throw new RuntimeException("Missing Parameter " + name);
            }
        } else {
            boolean rightType = false;
            if (def != null) {
                if (def.getValueType().equals(DSValueType.ENUM)) {
                    rightType = paramVal.isString() && def instanceof DSIEnum
                            && ((DSIEnum) def).getEnums(null).contains(paramVal);
                } else {
                    rightType = def.getValueType().equals(paramVal.getValueType());
                }
            } else if (enumtype != null) {
                rightType = paramVal.isString() && enumtype.getEnums(null).contains(paramVal);
            } else if (type != null) {
                rightType = type.equals(paramVal.getValueType());
            }
            if (!rightType) {
                throw new RuntimeException("Unexpected Type on Parameter " + name);
            }
        }

        if (bounds != null && paramVal != null) {
            if (!bounds.validBounds(paramVal)) throw new RuntimeException("Parameter Value out of bounds: " + name);
        }
    }

    /**
     * Returns a random value, if bounds are defined.
     *
     * @return a valid random parameter value, or null
     */
    public DSElement generateRandom(Random rand) {
        return bounds != null ? bounds.generateRandom(rand) : null;
    }
}
