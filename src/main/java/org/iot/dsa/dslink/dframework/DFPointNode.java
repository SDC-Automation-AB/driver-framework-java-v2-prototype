package org.iot.dsa.dslink.dframework;

import java.util.Iterator;
import java.util.Set;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFPointNode extends DFAbstractNode {

    protected static long REFRESH_DEF = 5000;
    protected static DFHelpers.DFConnStrat CONN_STRAT_DEF = DFHelpers.DFConnStrat.LAZY;
    protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.CONSTANT;
    
    public DFPointNode nextSibling;
    public DFPointNode prevSibling;

    @Override
    public void stopCarObject() {
        if (carObject != null) {
            Set<DFPointNode> set = ((DFLeafCarouselObject) carObject).homeNodes;
            set.remove(this);
            if (set.isEmpty()) {
               carObject.close();
            }
            carObject = null;
        }
    }

    //TODO: Make sure all the nodes get added to the carObject not just one
    @Override
    public void startCarObject() {
        if (carObject == null) {
            Set<DFLeafCarouselObject> b = ((DFDeviceNode) getParent()).batches;
            if (b.isEmpty()) {
                carObject = new DFLeafCarouselObject(this); //TODO fix consturtor
                b.add((DFLeafCarouselObject) carObject);
            } else {
                carObject = b.iterator().next();
                ((DFLeafCarouselObject) carObject).homeNodes.add(this);
            }
        }
    }

    @Override
    boolean isNodeStopped() {
        return isSubscribed();
    }

    //TODO: Make sure only one node gets subscribed/unsubscribed
    @Override
    protected void onSubscribed() {
        startCarObject();
    }
    
    @Override
    protected void onUnsubscribed() {
        stopCarObject();
    }

}
