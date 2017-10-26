package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.DFHelpers.DFStatus;
import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFAbstractNode extends DSNode {

    protected static long REFRESH_DEF;
    protected static DFHelpers.DFConnStrat CONN_STRAT_DEF;
    protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF;

    DFCarouselObject carObject;

    private final DSInfo is_stopped = getInfo(DFHelpers.IS_STOPPED);
    boolean getIsStopped() {
        return is_stopped.getValue().equals(DSBool.TRUE);
    }

    //Carousel Management Methods
    abstract public boolean createConnection();
    abstract public boolean ping();
    abstract public void closeConnection();

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(DFHelpers.STATUS, DSString.valueOf(DFStatus.NEW)).setReadOnly(true);
        //TODO: add full timestamp reporting
        declareDefault(DFHelpers.STOP, makeStopAction());
        declareDefault(DFHelpers.START, makeStartAction());
        declareDefault(DFHelpers.RESTART, makeRestartAction());
        declareDefault(DFHelpers.REMOVE, makeRemoveAction());

        declareDefault(DFHelpers.IS_STOPPED, DSBool.FALSE).setReadOnly(true).setHidden(true);
    }

    DSAction makeStopAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DFAbstractNode) info.getParent()).stopCarObject();
                put(DFHelpers.IS_STOPPED, DSBool.TRUE);
                return null;
            }
        };
        return act;
    }

    public void stopCarObject() {
        if (carObject != null) {
            carObject.close();
            carObject = null;
        }
    }

    DSAction makeStartAction() {
        return new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DFAbstractNode) info.getParent()).startCarObject();
                return null;
            }
        };
    }

    public void startCarObject() {
        put(DFHelpers.IS_STOPPED, DSBool.FALSE);
        if (carObject == null) {
            carObject = new DFCarouselObject(this);
        }
    }

    DSAction makeRestartAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DFAbstractNode) info.getParent()).restartConnection();
                return null;
            }
        };
        return act;
    }
    
    public void restartConnection() {
        stopCarObject();
        startCarObject();
    }
    
    DSAction makeRemoveAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DFAbstractNode) info.getParent()).removeConnection();
                return null;
            }
        };
        return act;
    }
    
    void removeConnection() {
        stopCarObject();
        getParent().remove(getInfo());
    }

    public long getRefresh() {
        return REFRESH_DEF;
    }

    public DFHelpers.DFConnStrat getConnStrat() {
        return CONN_STRAT_DEF;
    }

    public DFHelpers.DFRefChangeStrat getRefreshChangeStrat() {
        return REFRESH_CHANGE_STRAT_DEF;
    }

    public void onConnected() {
        put(DFHelpers.STATUS, DSString.valueOf(DFStatus.CONNECTED));
    }
    
    public void onFailed() {
        put(DFHelpers.STATUS, DSString.valueOf(DFStatus.FAILED));
    }

    public void onDfStopped() {
        put(DFHelpers.STATUS, DSString.valueOf(DFStatus.STOPPED));
    }
}
