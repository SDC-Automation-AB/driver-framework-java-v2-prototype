package org.iot.dsa.dslink.dframework;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFPointNode extends DFAbstractNode {

    protected static long REFRESH_DEF = 5000;
    protected static DFHelpers.DFConnStrat CONN_STRAT_DEF = DFHelpers.DFConnStrat.LAZY;
    protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.CONSTANT;

    private DFLeafCarouselObject carObject;

    @Override
    public void stopCarObject() {
        synchronized(this) {
            if (carObject != null) {
                carObject.close(this);
                carObject = null;
            }
        }
    }

    @Override
    public void startCarObject() {
        synchronized(this) {
            if (carObject == null) {
                DFDeviceNode dev = (DFDeviceNode) getParent();
                if (dev.noPollBatches()) {
                    carObject = new DFLeafCarouselObject(this, dev);
                } else {
                    carObject = dev.getPollBatch();
                    carObject.homeNodes.add(this);
                }
            }
        }
    }

    @Override
    boolean isNodeStopped() {
        return !isSubscribed();
    }

    //TODO: Make sure only one node gets subscribed/unsubscribed
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
