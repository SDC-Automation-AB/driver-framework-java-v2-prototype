package org.iot.dsa.dslink.dftest;

import org.iot.dsa.dslink.dframework.DFConnectionNode;
import org.iot.dsa.dslink.dframework.DFDeviceNode;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;

/**
 * @author James (Juris) Puchin
 * Created on 1/29/2018
 */
public class DFFuzzNodeAction extends FuzzNodeActionContainer {

    @Override
    String invokeAction(DSInfo actionInfo) {
        String name = actionInfo.getName();
        DSNode parent = actionInfo.getParent();
        String path = parent.getPath();
        path = path.endsWith("/") ? path + name : path + "/" + name;
        DSMap params = new DSMap();
        //TODO: Generalize actions to work with ParameterDefinitions
        //TODO: Figure out how to get parameters from the valid devices?
        // -in getDevStringToAdd, return a MockParameters or Parameters
        // -get them from some separate source and put them in
        if (name.equals("Edit")) {
            return "Edit action not yet supported!";
        } else if (name.equals("Add Connection")) {
            String c = addConnectionHelper(parent);
            params.put("Name", c).put("Connection String", c).put("Ping Rate", getFuzzPingRate());
        } else if (name.equals("Add Device")) {
            String d = addDeviceHelper(parent);
            params.put("Name", d).put("Device String", d).put("Ping Rate", getFuzzPingRate());
        } else if (name.equals("Add Point")) {
            String p = addPintHelper(parent);
            params.put("Name", p).put("ID", p).put("Poll Rate", getFuzzPingRate());
        } else if (name.equals("Remove")) {
            if (parent instanceof DFConnectionNode) {
                removeConnectionHelper(parent);
            } else if (parent instanceof DFDeviceNode) {
                removeDeviceHelper(parent);
            } else if (parent instanceof DFPointNode) {
                removePointHelper(parent.getInfo());
            } else {
                throw new RuntimeException("Trying to remove a non DFNode: " + actionInfo.getParent().getName());
            }
        }
        FuzzTest.requester.invoke(path, params, new FuzzTest.InvokeHandlerImpl());
        return "Invoking " + path + " with parameters " + params;
    }
}
