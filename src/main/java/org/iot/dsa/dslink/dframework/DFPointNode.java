package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.DFHelpers.DFStatus;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public abstract class DFPointNode extends DFAbstractNode implements DSIValue {

    //protected static long REFRESH_DEF = DFHelpers.DEFAULT_PING_DELAY;
    //protected static DFHelpers.DFConnStrat CONN_STRAT_DEF = DFHelpers.DFConnStrat.LAZY;
    //protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.CONSTANT;

    private DFLeafCarouselObject carObject;
    private DFDeviceNode parDevice;
    
    private DSInfo valueInfo = getInfo("Value");
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Value", DSString.EMPTY);
    }

    private DFDeviceNode getParentDev() {
        if (parDevice == null) {
            parDevice = (DFDeviceNode) getParent();
        }
        return parDevice;
    }

    @Override
    public void stopCarObject() {
        DFDeviceNode dev = getParentDev();
        synchronized (dev) {
            if (carObject != null) {
                carObject.close(this);
                carObject = null;
            }
        }
    }

    @Override
    public void startCarObject() {
        DFDeviceNode dev = getParentDev();
        synchronized (dev) {
            if (carObject == null) {
                carObject = dev.getPollBatch(this);
            }
        }
    }

    @Override
    boolean isNodeStopped() {
        return !isSubscribed();
    }

    @Override
    protected void onSubscribed() {
        startCarObject();
        //System.out.println("Started Node: " + get("Value")); //DEBUG
    }

    @Override
    protected void onUnsubscribed() {
        put(DFHelpers.STATUS, DSString.valueOf(DFStatus.STOPPED));
        stopCarObject();
        //System.out.println("Stopped Node: " + get("Value")); //DEBUG
    }
    
    public long getPollRate() {
        return getParentDev().getPingRate();
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
