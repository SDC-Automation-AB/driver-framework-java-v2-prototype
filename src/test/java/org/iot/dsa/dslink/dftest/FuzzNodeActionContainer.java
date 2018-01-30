package org.iot.dsa.dslink.dftest;

import org.iot.dsa.dslink.dframework.DFDeviceNode;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;

/**
 * @author James (Juris) Puchin
 * Created on 1/29/2018
 */
abstract class FuzzNodeActionContainer {
    abstract String invokeAction(DSInfo actionInfo);

    static long getFuzzPingRate() {
        return FuzzTest.PING_POLL_RATE;
    }

    static String addConnectionHelper(DSNode parent) {
        FuzzTest.conn_node_counter++;
        return FuzzTest.getConnStringToAdd(parent);
    }

    static String addDeviceHelper(DSNode parent) {
        FuzzTest.dev_node_counter++;
        return FuzzTest.getDevStringToAdd(parent);
    }

    static String addPintHelper(DSNode parent) {
        FuzzTest.pnt_node_counter++;
        String pnt = FuzzTest.getPointStringToAdd(parent);
        FuzzTest.queuedAction = new FuzzTest.DelayedActionOrSub(parent, pnt);
        return pnt;
    }

    static void removeConnectionHelper(DSNode conNode) {
        FuzzTest.conn_node_counter--;
        for (String devName : FuzzTest.getDFNodeNameSet(conNode, DFDeviceNode.class)) {
            DSNode devNode = conNode.getNode(devName);
            removeDeviceHelper(devNode);
        }
    }

    static void removeDeviceHelper(DSNode devNode) {
        FuzzTest.dev_node_counter--;
        for (String pointName : FuzzTest.getDFNodeNameSet(devNode, DFPointNode.class)) {
            removePointHelper(devNode.getInfo(pointName));
        }
    }

    static void removePointHelper(DSInfo pntInfo) {
        FuzzTest.pnt_node_counter--;
        FuzzTest.SubscribeHandlerImpl handle = FuzzTest.subscriptions.remove(pntInfo);
        if (handle != null) handle.getStream().closeStream();
    }
}
