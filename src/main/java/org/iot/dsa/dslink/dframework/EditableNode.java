package org.iot.dsa.dslink.dframework;

import java.util.List;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public abstract class EditableNode extends DSNode {
    
    public abstract List<ParameterDefinition> getParameterDefinitions();
    
    public DSMap parameters;
    
    
    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get(DFHelpers.PARAMETERS);
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
            verifyParameters(parameters, getParameterDefinitions());
        } else {
            verifyParameters(parameters, getParameterDefinitions());
            put(DFHelpers.PARAMETERS, parameters.copy());
        }
    }
    
    @Override
    protected void onStable() {
        put(DFHelpers.ACTION_EDIT, makeEditAction());
        super.onStable();
    }
    
    public DSAction makeEditAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((EditableNode) info.getParent()).edit(invocation.getParameters());
                return null;
            }
        };
        makeEditParameters(act, getParameterDefinitions(), parameters);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        verifyParameters(newParameters, getParameterDefinitions());
        this.parameters = newParameters;
        put(DFHelpers.PARAMETERS, parameters.copy());
        put(DFHelpers.ACTION_EDIT, makeEditAction());
    }
    
    public abstract void onEdit();
    
    
    public static void verifyParameters(DSMap parameters, List<ParameterDefinition> parameterDefinitions) {
        for (ParameterDefinition defn: parameterDefinitions) {
            defn.verify(parameters);
        }
    }
    
    public static void makeAddParameters(DSAction action, List<ParameterDefinition> parameterDefinitions) {
        action.addParameter(DFHelpers.NAME, DSValueType.STRING, null);
        for (ParameterDefinition paramDefn: parameterDefinitions) {
            paramDefn.addToAction(action);
        }
    }
    
    public static void makeEditParameters(DSAction action, List<ParameterDefinition> parameterDefinitions, DSMap parameters) {
        for (ParameterDefinition paramDefn: parameterDefinitions) {
            DSElement def = parameters.get(paramDefn.name);
            paramDefn.addToAction(action, def);
        }
    }
    
    // should only be called on default instance
    public DSAction getAddAction() {
        final EditableNode inst = this;
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                inst.addNewInstance(info.getParent(), invocation.getParameters());
                return null;
            }
        };
        makeAddParameters(act, getParameterDefinitions());
        return act;
    }
    
    // should only be called on default instance
    public abstract void addNewInstance(DSNode parent, DSMap newParameters);

}
