package org.iot.dsa.dslink.dftest;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSIRequester;
import org.iot.dsa.dslink.DSLink;
import org.iot.dsa.dslink.dfexample.RootNode;
import org.iot.dsa.dslink.dfexample.TestConnectionNode;
import org.iot.dsa.dslink.dfexample.TestDeviceNode;
import org.iot.dsa.dslink.dfexample.TestPointNode;
import org.iot.dsa.dslink.dframework.*;
import org.iot.dsa.dslink.requester.AbstractInvokeHandler;
import org.iot.dsa.dslink.requester.AbstractSubscribeHandler;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSStatus;
import org.iot.dsa.time.DSDateTime;
import org.junit.Test;

public class BasicTest {

    private final long seed = 420;
    private static Set<String> unique_names = new HashSet<String>();
    
    @Test
    public void teeeeeessst() {
        preInit();
        
        RootNode root = new RootNode();
        DSLink link = new TestLink(root);
        DSRuntime.run(link);
        
        Random random = new Random(seed);
        Map<DSInfo, SubscribeHandlerImpl> subscriptions = new HashMap<DSInfo, SubscribeHandlerImpl>();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            assert(false);
        }
        DSIRequester requester = link.getConnection().getRequester();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("testing-output.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            assert(false);
        } catch (UnsupportedEncodingException e) {
            assert(false);
        }
        if (writer == null) {
            return;
        }       
        
        for(int i = 0; i < 10; i++) {
            writer.println(doAThing(requester, root, random, subscriptions));
            writer.flush();
        }
        
        writer.close();
    }
    
    private static String doAThing(DSIRequester requester, RootNode root, Random random, Map<DSInfo, SubscribeHandlerImpl> subscriptions) {
        String thingDone;
        if (random.nextInt(2) < 1) {
            thingDone = createOrModifyDevice(random);
        } else {
            thingDone = subscribeOrDoAnAction(requester, root, random, subscriptions);
        }
        
        try {
            Thread.sleep(getPingRate() * 2);
        } catch (InterruptedException e) {
            assert(false);
        }
        
        return thingDone + "\n" + DFHelpers.getTestingString(root, false);
    }
    
    private static String createOrModifyDevice(Random random) {
        int connCount = TestingConnection.connections.size();
        int rrand = random.nextInt(connCount + 1);
        if (rrand >= connCount) {
            String c = generateConnString(random);
            addConn(c);
            return "Creating connection " + c;
        } else {
            Entry<String, TestingConnection> centry = (Entry<String, TestingConnection>) TestingConnection.connections.entrySet().toArray()[rrand];
            String c = centry.getKey();
            TestingConnection conn = centry.getValue();
            int devCount = conn.devices.size();
            int crand = random.nextInt(devCount + 2);
            if (crand > devCount) {
                conn.shouldSucceed = !conn.shouldSucceed;
                return "Setting ShouldSucceed to " + conn.shouldSucceed + " on " + c;
            } else if (crand == devCount) {
                String d = generateDevString(random, conn);
                addDev(conn, d);
                return "Creating device " + c + ":" + d;
            } else {
                Entry<String, TestingDevice> dentry = (Entry<String, TestingDevice>) conn.devices.entrySet().toArray()[crand];
                String d = dentry.getKey();
                TestingDevice dev = dentry.getValue();
                int pointCount = dev.points.size();
                int drand = random.nextInt(pointCount + 2);
                if (drand > pointCount) {
                    dev.active = !dev.active;
                    return "Setting Active to " + dev.active + " on " + c + ":" + d;
                } else if (drand == pointCount) {
                    String p = generatePointString(random, dev);
                    String v = generatePointValue(random);
                    dev.points.put(p, v);
                    return "Setting new point " + c + ":" + d + ":" + p + " to " + v;
                } else {
                    String p = (String) dev.points.keySet().toArray()[drand];
                    int prand = random.nextInt(9);
                    if (prand < 1) {
                        dev.points.remove(p);
                        return "Removing point " + c + ":" + d + ":" + p;
                    } else {
                        String v = generatePointValue(random);
                       dev.points.put(p, v);
                       return "Setting point " + c + ":" + d + ":" + p + " to " + v;
                    }
                }
            }
        }
    }
    
    private static String subscribeOrDoAnAction(DSIRequester requester, RootNode root, Random random, Map<DSInfo, SubscribeHandlerImpl> subscriptions) {
        DSInfo rinfo = pickAChild(root, random);
        if (rinfo.isAction()) {
            return invokeAction(requester, rinfo, random);
        } else {
            assert rinfo.getObject() instanceof TestConnectionNode;
            DSInfo cinfo = pickAChild(rinfo.getNode(), random);
            if (cinfo.isAction()) {
                return invokeAction(requester, cinfo, random);
            } else {
                assert cinfo.getObject() instanceof TestDeviceNode;
                DSInfo dinfo = pickAChild(cinfo.getNode(), random);
                if (dinfo.isAction()) {
                    return invokeAction(requester, dinfo, random);
                } else {
                    assert dinfo.getObject() instanceof TestPointNode;
                    int choice = random.nextInt(10);
                    if (choice == 0) {
                        return invokeAction(requester, dinfo.getNode().getInfo("Remove"), random);
                    } else if (choice == 1) {
                        return invokeAction(requester, dinfo.getNode().getInfo("Edit"), random);
                    } else {
                        String path = dinfo.getNode().getPath();
                        SubscribeHandlerImpl subHandle = subscriptions.remove(dinfo);
                        if (subHandle == null) {
                            subHandle = new SubscribeHandlerImpl();
                            requester.subscribe("/Nodes" + path, 0, subHandle);
                            subscriptions.put(dinfo, subHandle);
                            return "Subscribing to " + path;
                        } else {
                            subHandle.getStream().closeStream();
                            return "Unsubscribing from " + path;
                        }
                        
                    }
                 }
            }
        }
    }
    
    private static String invokeAction(DSIRequester requester, DSInfo actionInfo, Random random) {
        String name = actionInfo.getName();
        DSNode parent = actionInfo.getParent();
        String path = parent.getPath();
        path = path.endsWith("/") ? path + name : path + "/" + name;
        DSMap params = new DSMap();
        if (name.equals("Edit")) {
            return "Edit action not yet supported!";
        } else if (name.equals("Add Connection")) {
            String c = getConnStringToAdd(parent, random);
            params.put("Name", c).put("Connection String", c).put("Ping Rate", getPingRate());
        } else if (name.equals("Add Device")) {
            String d = getDevStringToAdd(parent, random);
            params.put("Name", d).put("Device String", d).put("Ping Rate", getPingRate());
        } else if (name.equals("Add Point")) {
            String p = getPointStringToAdd(parent, random);
            params.put("Name", p).put("ID", p).put("Poll Rate", getPingRate());
        }
        requester.invoke("/Nodes" + path, params, new InvokeHandlerImpl());
        return "Invoking " + path + " with parameters " + params;
    }
    
    private static DSInfo pickAChild(DSNode node, Random random) {
        List<DSInfo> childs = new ArrayList<DSInfo>();
        for (DSInfo info: node) {
            if (info.isAction() && !"Print".equals(info.getName())) {
                childs.add(info);
            } else if (info.isNode()) {
                DSNode n = info.getNode();
                if (n instanceof DFAbstractNode) {
                    childs.add(info);
                }
            }
        }
        int choice = random.nextInt(childs.size());
        return childs.get(choice);
    }
    
    private static long getPingRate() {
        return 1000;
    }

    private static Set<String> getDFNodeNameSet(DSNode parent, Class<? extends DFAbstractNode> className) {
        HashSet<String> nodes = new HashSet<String>();
        for (DSInfo info : parent) {
            if (info.isNode()) {
                if (info.getNode().getClass().isAssignableFrom(className)) {
                    nodes.add(info.getName());
                }
            }
        }
        return nodes;
    }
    
    private static String getConnStringToAdd(DSNode parent, Random random) {
        Set<String> nodes = getDFNodeNameSet(parent, DFConnectionNode.class);
        if (TestingConnection.connections.isEmpty()) {
            return generateConnString(random);
        }
        int choice = random.nextInt(TestingConnection.connections.size());
        String name = (String) TestingConnection.connections.keySet().toArray()[choice];
        if (nodes.contains(name)) {
            return generateConnString(random);
        } else {
            return name;
        }
    }
    
    private static String getDevStringToAdd(DSNode parent, Random random) {
        Set<String> nodes = getDFNodeNameSet(parent, DFDeviceNode.class);
        TestingConnection conn = TestingConnection.connections.get(parent.getName());
        if (conn == null || conn.devices.isEmpty()) {
            return generateDevString(random, conn);
        }
        int choice = random.nextInt(conn.devices.size());
        String name = (String) conn.devices.keySet().toArray()[choice];
        if (nodes.contains(name)) {
            return generateDevString(random, conn);
        } else {
            return name;
        }
    }
    
    private static String getPointStringToAdd(DSNode parent, Random random) {
        Set<String> nodes = getDFNodeNameSet(parent, DFPointNode.class);
        TestingConnection conn = TestingConnection.connections.get(parent.getParent().getName());
        TestingDevice dev = conn != null ? conn.devices.get(parent.getName()) : null;
        if (dev == null || dev.points.isEmpty()) {
            return generatePointString(random, dev);
        }
        int choice = random.nextInt(dev.points.size());
        String name = (String) dev.points.keySet().toArray()[choice];
        if (nodes.contains(name)) {
            return generatePointString(random, dev);
        } else {
            return name;
        }
    }
    
    private static boolean notUnique(String name) {
        if (unique_names.contains(name)) {
            return true;
        } else {
            unique_names.add(name);
            return false;
        }
    }

    private static String pickAName(String[] mods, String[] names, Random rand, boolean camel) {
        String str;
        do {
            String one = mods[rand.nextInt(mods.length)];
            String two = names[rand.nextInt(names.length)];
            if (camel) str = one + two;
            else str = one + "_" + two;
        } while (notUnique(str));
        return str;
    }

    private static String generateConnString(Random random) {
        return pickAName(DFHelpers.colors, DFHelpers.places, random, true);
    }
    
    private static String generateDevString(Random random, TestingConnection conn) {
        return pickAName(DFHelpers.colors, DFHelpers.animals, random, true);
    }
    
    private static String generatePointString(Random random, TestingDevice dev) {
        return pickAName(DFHelpers.colors, DFHelpers.parts, random, false);
    }
    
    private static String generatePointValue(Random random) {
        return DFHelpers.adjectives[random.nextInt(DFHelpers.adjectives.length)];
    }
  
    private static void preInit() {
        
    }
    
    private static TestingConnection addConn(String c) {
        TestingConnection conn = new TestingConnection();
        TestingConnection.connections.put(c, conn);
        return conn;
    }
    
    private static TestingDevice addDev(TestingConnection conn, String d) {
        TestingDevice dev = new TestingDevice();
        conn.devices.put(d, dev);
        return dev;
    }
    
    
    private static class SubscribeHandlerImpl extends AbstractSubscribeHandler {

        @Override
        public void onUpdate(DSDateTime dateTime, DSElement value, DSStatus status) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onClose() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onError(String type, String msg, String detail) {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    
    private static class InvokeHandlerImpl extends AbstractInvokeHandler {
        
        @Override
        public void onColumns(DSList list) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onInsert(int index, DSList rows) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onMode(Mode mode) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onReplace(int start, int end, DSList rows) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onTableMeta(DSMap map) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onUpdate(DSList row) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onClose() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onError(String type, String msg, String detail) {
            // TODO Auto-generated method stub
            
        }
    }

}
