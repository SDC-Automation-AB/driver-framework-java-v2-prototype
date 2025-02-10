package org.iot.dsa.dslink.dframework;

import java.util.List;
import org.iot.dsa.dslink.ActionResults;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;

import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSIActionRequest;
import org.iot.dsa.util.DSException;

public abstract class EditableNode extends DSNode {
    
    public abstract List<ParameterDefinition> getParameterDefinitions();
    
    public DSMap parameters;
    
    public EditableNode() {
        super();
    }
    
    private void setParameters(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
    }
    
    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get(DFHelpers.PARAMETERS);
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
        } else {
            put(DFHelpers.PARAMETERS, parameters.copy());
        }
        try {
            verifyParameters(parameters, getParameterDefinitions());
        } catch (Exception e) {
            getParent().remove(getInfo());
            DSException.throwRuntime(e);
        }
    }
    
    @Override
    protected void onStable() {
        put(DFHelpers.ACTION_EDIT, makeEditAction());
        super.onStable();
    }
    
    public DSMainNode getMainNode() {
        DSNode n = this;
        while (n != null) {
            if (n instanceof DSMainNode) {
                return (DSMainNode) n;
            }
            n = n.getParent();
        }
        DSException.throwRuntime(new RuntimeException("Main node not found, parent is null"));
        return null;
    }
    
    public DSAction makeRemoveAction() {
        DSAction act = new DSAction() {

            @Override
            public ActionResults invoke(DSIActionRequest request) {
                ((EditableNode)request.getTarget()).delete();
                return null;
            }
        };
        return act;
    }
    
    public void delete() {
        getParent().remove(getInfo());
    }
    
    
    public DSAction makeEditAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResults invoke(DSIActionRequest request) {
                ((EditableNode)request.getTarget()).edit(request.getParameters());
                return super.invoke(request);
            }
        };
        makeEditParameters(act, getParameterDefinitions(), parameters);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        preEdit(newParameters);
        verifyParameters(newParameters, getParameterDefinitions());
        this.parameters = newParameters;
        put(DFHelpers.PARAMETERS, parameters.copy());
        put(DFHelpers.ACTION_EDIT, makeEditAction());
        onEdit();
    }
    
    public void preEdit(DSMap newParameters) {};
    
    public void onEdit() {};
    
    public void onAdded() {};
    
    
    public static void verifyParameters(DSMap parameters, List<ParameterDefinition> parameterDefinitions) {
        for (ParameterDefinition defn: parameterDefinitions) {
            defn.verify(parameters);
        }
    }
    
    public static void makeAddParameters(DSAction action, List<ParameterDefinition> parameterDefinitions) {
        action.addParameter(DFHelpers.NAME, DSString.EMPTY, null);
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
    
    // should only be called on dummy instance
    public DSAction getAddAction() {
        final EditableNode inst = this;
        DSAction act = new DSAction() {

            @Override
            public ActionResults invoke(DSIActionRequest request) {
                inst.addNewInstance((DSNode) request.getTarget(), request.getParameters());
                return super.invoke(request);
            }
        };
        makeAddParameters(act, getParameterDefinitions());
        return act;
    }
    
    // should only be called on dummy instance
    private void addNewInstance(DSNode parent, DSMap newParameters) {
        String name = newParameters.getString(DFHelpers.NAME);
        verifyParameters(newParameters, getParameterDefinitions());
        try {
            EditableNode inst = getClass().newInstance();
            inst.setParameters(newParameters);
            parent.put(name, inst);
            inst.onAdded();
        } catch (Exception e) {
            DSException.throwRuntime(e);
        }
    }

}
