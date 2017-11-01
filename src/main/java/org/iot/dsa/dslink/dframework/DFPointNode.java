package org.iot.dsa.dslink.dframework;

import java.util.Iterator;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFPointNode extends DFAbstractNode {

    protected static long REFRESH_DEF = 5000;
    protected static DFHelpers.DFConnStrat CONN_STRAT_DEF = DFHelpers.DFConnStrat.LAZY;
    protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.CONSTANT;
    
    public DFPointNode nextSibling;
    
    
    @Override
    boolean isNodeStopped() {
        return isSubscribed();
    }
    
    @Override
    protected void onSubscribed() {
        startCarObject();
    }
    
    @Override
    protected void onUnsubscribed() {
        stopCarObject();
    }
    
    @Override
    public boolean createConnection() {
        return poll();
    }

    @Override
    public boolean ping() {
        return poll();
    }
    
    public abstract boolean poll();

}
