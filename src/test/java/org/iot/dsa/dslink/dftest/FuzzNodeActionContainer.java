package org.iot.dsa.dslink.dftest;

import org.iot.dsa.dslink.dframework.DFDeviceNode;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/29/2018
 */
public abstract class FuzzNodeActionContainer {

    public abstract String invokeAction(DSInfo actionInfo, Random rand);

    protected static long getFuzzPingRateMillis() {
        return FuzzTest.PING_POLL_RATE;
    }

    protected static Double getFuzzPingRateSec() {
        return FuzzTest.PING_POLL_RATE / 1000.0;
    }

    protected static String addConnectionHelper(DSNode parent, DSMap params) {
        FuzzTest.conn_node_counter++;
        return FuzzTest.getConnStringToAdd(parent, params);
    }

    protected static String addDeviceHelper(DSNode parent, DSMap params) {
        FuzzTest.dev_node_counter++;
        return FuzzTest.getDevStringToAdd(parent, params);
    }

    protected static String addPintHelper(DSNode parent, DSMap params) {
        FuzzTest.pnt_node_counter++;
        String pnt = FuzzTest.getPointStringToAdd(parent, params);
        FuzzTest.queuedAction = new FuzzTest.DelayedActionOrSub(parent, pnt);
        return pnt;
    }

    protected static void removeConnectionHelper(DSNode conNode) {
        FuzzTest.conn_node_counter--;
        for (String devName : FuzzTest.getDFNodeNameSet(conNode, DFDeviceNode.class)) {
            DSNode devNode = conNode.getNode(devName);
            removeDeviceHelper(devNode);
        }
    }

    protected static void removeDeviceHelper(DSNode devNode) {
        FuzzTest.dev_node_counter--;
        for (String pointName : FuzzTest.getDFNodeNameSet(devNode, DFPointNode.class)) {
            removePointHelper(devNode.getInfo(pointName));
        }
    }

    protected static void removePointHelper(DSInfo pntInfo) {
        FuzzTest.pnt_node_counter--;
        FuzzTest.SubscribeHandlerImpl handle = FuzzTest.subscriptions.remove(pntInfo);
        if (handle != null) handle.getStream().closeStream();
    }
}
