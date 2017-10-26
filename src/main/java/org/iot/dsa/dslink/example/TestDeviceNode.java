package org.iot.dsa.dslink.example;

import java.io.File;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public class TestDeviceNode extends DFDeviceNode {
    
    DSMap parameters;
    File fileObj;
    
    public TestDeviceNode() {
    }
    
    public TestDeviceNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get("parameters");
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
        } else {
            put("parameters", parameters.copy());
        }
    }
    
    @Override
    protected void onStable() {
        put("Edit", makeEditAction());
        super.onStable();
    }

    @Override
    boolean createConnection() {  
        String fpath = parameters.getString("Filepath");
        if (fpath == null) {
            return false;
        }
        fileObj = new File(fpath);
        return fileObj.canRead() && fileObj.isFile();
    }

    @Override
    boolean ping() {
        return fileObj != null && fileObj.canRead() && fileObj.isFile();
    }

    @Override
    void closeConnection() {
        fileObj = null;
    }
    
    @Override
    public long getRefresh() {
        DSElement rate = parameters.get("Ping Rate");
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getRefresh();
    }
    
    private DSAction makeEditAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((TestDeviceNode) info.getParent()).edit(invocation.getParameters());
                return null;
            }
        };
        DSElement defFilepath = parameters.get("Filepath");
        DSElement defPingRate = parameters.get("Ping Rate");
        act.addDefaultParameter("Filepath", defFilepath != null ? defFilepath : DSString.EMPTY, null);
        act.addDefaultParameter("Ping Rate", defPingRate != null ? defPingRate : DSLong.valueOf(REFRESH_DEF), null);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        this.parameters = newParameters;
        put("parameters", parameters.copy());
        put("Edit", makeEditAction());
        restartConnection();
    }

}
