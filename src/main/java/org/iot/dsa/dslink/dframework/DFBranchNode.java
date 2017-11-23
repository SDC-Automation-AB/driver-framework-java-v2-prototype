package org.iot.dsa.dslink.dframework;

/**
 * @author James (Juris) Puchin
 * Created on 11/1/2017
 */
public abstract class DFBranchNode extends DFAbstractNode {
    public static long DEFAULT_PING_RATE = DFHelpers.DEFAULT_PING_DELAY;
    
    
    private DFBranchCarouselObject carObject;

    //Carousel Management Methods
    abstract public boolean createConnection();
    abstract public boolean ping();
    abstract public void closeConnection();

    public void stopCarObject() {
        synchronized (this) {
            if (carObject != null) {
                carObject.close();
                carObject = null;
            }
        }
    }

    public void startCarObject() {
        synchronized (this) {
            if (carObject == null) {
                carObject = new DFBranchCarouselObject(this);
            }
        }
    }

    @Override
    protected void onStable() {
        super.onStable();
        if (isNodeStopped()) put(DFHelpers.START, makeStartStopAction());
        else put(DFHelpers.STOP, makeStartStopAction());
    }
    
    DFBranchDelayCalculator getPingReconnectCalculator(DFBranchCarouselObject carObject) {
        return new DFBranchDelayCalculator(this, carObject);
    }

    public long getPingRate() {
        return DEFAULT_PING_RATE;
    }

}
