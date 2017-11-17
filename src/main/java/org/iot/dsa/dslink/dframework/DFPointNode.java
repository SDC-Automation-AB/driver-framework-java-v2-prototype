package org.iot.dsa.dslink.dframework;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public abstract class DFPointNode extends DFAbstractNode {

    protected static long REFRESH_DEF = DFHelpers.DEFAULT_PING_DELAY;
    protected static DFHelpers.DFConnStrat CONN_STRAT_DEF = DFHelpers.DFConnStrat.LAZY;
    protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.CONSTANT;

    private DFLeafCarouselObject carObject;
    private DFDeviceNode parDevice;

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
                if (dev.noPollBatches()) {
                    carObject = new DFLeafCarouselObject(this, dev);
                } else {
                    carObject = dev.getPollBatch();
                    carObject.addHomeNode(this);
                }
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
        System.out.println("Started Node: " + get("Value"));
    }

    @Override
    protected void onUnsubscribed() {
        stopCarObject();
        System.out.println("Stopped Node: " + get("Value"));
    }

}
