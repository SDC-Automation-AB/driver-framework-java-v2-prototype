package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;

import java.util.LinkedList;

public class DFHelpers {
    static final String STATUS = "Status";
    static final String RESTART = "Restart";
    static final String STOP = "Stop";
    static final String START = "Start";
    static final String REMOVE = "Remove";
    static final String IS_STOPPED = "Stopped";
    static final String PRINT = "Print";

    public enum DFConnStrat {
        LAZY,
        ACTIVE,
        HYBRID
    }
    
    public enum DFRefChangeStrat {
        CONSTANT,
        LINEAR,
        EXPONENTIAL
    }
    
    public enum DFStatus {
        NEW("Unknown"),
        CONNECTED("Connected"),
        FAILED("Failed"),
        STOPPED(IS_STOPPED),
        STOPPED_BYP("Stopped by Parent");
        
        String display;
        DFStatus(String display) {
            this.display = display;
        }
        @Override
        public String toString() {
            return display;
        }
    }

    public static String getTestingString(DSNode node) {
        String nodeType = "Unknown";
        if (node instanceof DSRootNode) {nodeType = "Root";}
        else if (node instanceof DFConnectionNode) {nodeType = "Conn";}
        else if (node instanceof DFDeviceNode) {nodeType = "Device";}
        else if (node instanceof DFPointNode) {nodeType = "Point";}

        StringBuilder str = new StringBuilder(nodeType);
        str.append(": ");
        LinkedList<DSInfo> nodes = new LinkedList<DSInfo>();
        LinkedList <DSInfo> actions = new LinkedList<DSInfo>();
        LinkedList <DSInfo> values = new LinkedList<DSInfo>();
        for (DSInfo info : node) {
            if (info.isAction()) {
                actions.add(info);
            } else if (info.isNode()) {
                nodes.add(info);
            } else if (info.isValue()) {
                values.add(info);
            }
        }
        boolean first = true;
        for (DSInfo info : actions) {
            if (!first) str.append(", ");
            str.append(info.getName());
            first = false;
        }
        for (DSInfo info : values) {
            if (!first) str.append(", ");
            str.append(info.getName());
            str.append(":");
            str.append(info.getValue());
            first = false;
        }
        for (DSInfo info : nodes) {
            str.append(" - ");
            str.append(getTestingString(info.getNode()));
        }
        return str.toString();
    }
}
