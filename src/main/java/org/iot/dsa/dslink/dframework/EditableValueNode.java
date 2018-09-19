package org.iot.dsa.dslink.dframework;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;

public abstract class EditableValueNode extends EditableNode implements DSIValue{
    
    private DSInfo valueInfo = getInfo("Value");
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Value", DSString.EMPTY);
    }
    
    /**
     * This fires the NODE_CHANGED topic when the value child changes.  Overrides should call
     * super.onChildChanged.
     */
    @Override
    public void onChildChanged(DSInfo child) {
        if (child == valueInfo) {
            fire(VALUE_TOPIC, null);
        }
    }

    @Override
    public DSValueType getValueType() {
        return valueInfo.getValue().getValueType();
    }

    @Override
    public DSElement toElement() {
        return valueInfo.getValue().toElement();
    }

    @Override
    public void onSet(DSIValue value) {
        put(valueInfo, value);
        onValueSet(value);
    }
    
    @Override
    public void onSet(DSInfo info, DSIValue value) {
        if (valueInfo.equals(info)) {
            onValueSet(value);
        }
    }
    
    public void onValueSet(DSIValue value) {
        
    }

    @Override
    public DSIValue valueOf(DSElement element) {
        return valueInfo.getValue().valueOf(element);
    }
    
    public void updateValue(DSIValue value) {
        put(valueInfo, value);
    }

    

}
