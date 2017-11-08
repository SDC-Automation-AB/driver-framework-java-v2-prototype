package org.iot.dsa.dslink.dframework;

/**
 * @author James (Juris) Puchin
 * Created on 11/1/2017
 */
public abstract class DFBranchNode extends DFAbstractNode {

    private DFBranchCarouselObject carObject;

    //Carousel Management Methods
    abstract public boolean createConnection();
    abstract public boolean ping();
    abstract public void closeConnection();

    public synchronized void stopCarObject() {
            if (carObject != null) {
                carObject.close();
                carObject = null;
            }
    }

    public synchronized void startCarObject() {
            if (carObject == null) {
                carObject = new DFBranchCarouselObject(this);
            }
    }

    @Override
    protected void onStable() {
        super.onStable();
        if (isNodeStopped()) put(DFHelpers.START, makeStartStopAction());
        else put(DFHelpers.STOP, makeStartStopAction());
    }
}
