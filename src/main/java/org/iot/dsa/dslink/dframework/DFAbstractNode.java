package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.dframework.DFHelpers.DFStatus;
import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSIStatus;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSStatus;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.util.DSException;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFAbstractNode extends EditableNode implements DSIStatus {

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
        //declareDefault(DFHelpers.PRINT, makePrintAction());

        declareDefault(DFHelpers.IS_STOPPED, DSBool.FALSE).setReadOnly(true).setHidden(true);
    }

    DSAction makeStartStopAction() {
        DSAction act = new DSAction.Parameterless() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                DFAbstractNode par = ((DFAbstractNode) info.get());
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

    private void setNodeStopped() {
        remove(DFHelpers.STOP);
        stopCarObject();
        put(DFHelpers.IS_STOPPED, DSBool.TRUE);
        put(DFHelpers.STATUS, DSString.valueOf(DFStatus.STOPPED));
        put(DFHelpers.START,makeStartStopAction());
    }

    private void setNodeRunning() {
        remove(DFHelpers.START);
        startCarObject();
        put(DFHelpers.IS_STOPPED, DSBool.FALSE);
        put(DFHelpers.STOP,makeStartStopAction());
    }

    public abstract void stopCarObject();
    public abstract void startCarObject();

    protected void restartNode() {
        setNodeStopped();
        setNodeRunning();
    }

    private DSAction makeRestartAction() {
        DSAction act = new DSAction.Parameterless() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DFAbstractNode) info.get()).restartNode();
                return null;
            }
        };
        return act;
    }
    
    @Override
    public void onEdit() {
        restartNode();
    }

//    private DSAction makePrintAction() {
//        DSAction act = new DSAction() {
//            @Override
//            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
//                System.out.println(DFHelpers.getTestingString(info.getParent(), false, true));
//                return null;
//            }
//        };
//        return act;
//    }
    
    @Override
    public void delete() {
        stopCarObject();
        super.delete();
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
    
    public DFStatus getDFStatus() {
        String statStr = status.getValue().toElement().toString();
        for (DFStatus stat : DFStatus.values()) {
            if (stat.toString().equals(statStr)) {
                return stat;
            }
        }
        DSException.throwRuntime(new RuntimeException("Unexpected DFStatus text"));
        return null;
    }

    @Override
    public DSStatus getStatus() {
        DFStatus dfstat = getDFStatus();
        switch (dfstat) {
            case NEW: return DSStatus.unknown;
            case CONNECTED: return DSStatus.ok;
            case FAILED: return DSStatus.down;
            case STOPPED: return DSStatus.disabled;
            case STOPPED_BYP: return DSStatus.disabled;
            default: return DSStatus.unknown;
        }
    }
}
