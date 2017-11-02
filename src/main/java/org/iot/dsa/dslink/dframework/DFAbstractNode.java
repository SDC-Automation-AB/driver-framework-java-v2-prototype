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
    boolean isNodeStopped() {
        return is_stopped.getValue().equals(DSBool.TRUE);
    }
    private final DSInfo status = getInfo(DFHelpers.STATUS);
    boolean isNodeConnected() { return status.getValue().equals(DSString.valueOf(DFStatus.CONNECTED)); }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(DFHelpers.STATUS, DSString.valueOf(DFStatus.NEW)).setReadOnly(true);
        //TODO: add full timestamp reporting
        declareDefault(DFHelpers.RESTART, makeRestartAction());
        declareDefault(DFHelpers.REMOVE, makeRemoveAction());

        declareDefault(DFHelpers.IS_STOPPED, DSBool.FALSE).setReadOnly(true).setHidden(true);
    }

    DSAction makeStartStopAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                DFAbstractNode par = ((DFAbstractNode) info.getParent());
                if (par.isNodeStopped()) {
                    par.setNodeRunning();
                }
                else {
                    par.setNodeStopped();
                }
                return null;
            }
        };
        return act;
    }

    @Override
    protected void onStable() {
        super.onStable();
        if (isNodeStopped()) put(DFHelpers.START, makeStartStopAction());
        else put(DFHelpers.STOP, makeStartStopAction());
    }

    public void setNodeStopped() {
        put(DFHelpers.IS_STOPPED, DSBool.TRUE);
        put(DFHelpers.STATUS, DSString.valueOf(DFStatus.STOPPED));
        stopCarObject();
        remove(DFHelpers.STOP);
        put(DFHelpers.START,makeStartStopAction());
    }

    public void setNodeRunning() {
        put(DFHelpers.IS_STOPPED, DSBool.FALSE);
        startCarObject();
        remove(DFHelpers.START);
        put(DFHelpers.STOP,makeStartStopAction());
    }

    public abstract void stopCarObject();
    public abstract void startCarObject();

    public void restartNode() {
        setNodeStopped();
        setNodeRunning();
    }

    DSAction makeRestartAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DFAbstractNode) info.getParent()).restartNode();
                return null;
            }
        };
        return act;
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
        if (!isNodeStopped())
            put(DFHelpers.STATUS, DSString.valueOf(DFStatus.STOPPED_BYP));
        else
            put(DFHelpers.STATUS, DSString.valueOf(DFStatus.STOPPED));
    }
}
