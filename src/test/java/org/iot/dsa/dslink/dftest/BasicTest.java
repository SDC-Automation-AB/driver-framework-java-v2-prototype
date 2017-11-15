package org.iot.dsa.dslink.dftest;

import com.acuity.iot.dsa.dslink.test.TestLink;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSIRequester;
import org.iot.dsa.dslink.DSLink;
import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.dslink.dfexample.RootNode;
import org.iot.dsa.dslink.dfexample.TestConnectionNode;
import org.iot.dsa.dslink.dfexample.TestDeviceNode;
import org.iot.dsa.dslink.dfexample.TestPointNode;
import org.iot.dsa.dslink.dframework.*;
import org.iot.dsa.dslink.requester.AbstractInvokeHandler;
import org.iot.dsa.dslink.requester.AbstractSubscribeHandler;
import org.iot.dsa.node.*;
import org.iot.dsa.time.DSDateTime;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;

public class BasicTest {

    private static final long TEST_STEPS = 1000;
    private static final long SETUP_STEPS = 10;
    private static final boolean FLAT_TREE = false;
    private static final long seed = 420;

    private static final long MIN_CON = 3;
    private static final long MAX_CON = 8;
    private static final long MIN_DEV = 6;
    private static final long MAX_DEV = 16;
    private static final long MIN_PNT = 12;
    private static final long MAX_PNT = 24;

    private static final double PROB_ROOT = .1;
    private static final double PROB_CON = .2;
    private static final double PROB_DEV = .3;
    private static final double PROB_PNT = .4;
    private static final double PROB_SWAP_CON_STATE = .5;
    private static final double PROB_SWAP_DEV_STATE = .5;
    private static final double PROB_REMOVE_PNT = .1;

    private static final double PROB_OF_BAD_CONFIG = 0.01;

    private static Set<String> unique_names = new HashSet<String>();
    private static long step_counter = 0;
    private static long conn_node_counter = 0;
    private static long dev_node_counter = 0;
    private static long pnt_node_counter = 0;
    private static long conn_dev_counter = 0;
    private static long dev_dev_counter = 0;
    private static long pnt_dev_counter = 0;
    private static final String DELIM = "\n\n=================================================================================";

    @Test
    public void testyMcTester() {
        //assert(1 == PROB_ROOT + PROB_CON + PROB_DEV + PROB_PNT);
        preInit();

        RootNode root = new RootNode();
        DSLink link = new TestLink(root);
        DSRuntime.run(link);

        Random random = new Random(seed);
        Map<DSInfo, SubscribeHandlerImpl> subscriptions = new HashMap<DSInfo, SubscribeHandlerImpl>();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            assert (false);
        }
        DSIRequester requester = link.getConnection().getRequester();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("testing-output.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            assert (false);
        } catch (UnsupportedEncodingException e) {
            assert (false);
        }
        if (writer == null) {
            return;
        }

        while (step_counter < TEST_STEPS) {
            writer.println(doAThing(requester, root, random, subscriptions));
            writer.flush();
            step_counter++;
        }

        writer.close();
    }

    private static boolean setupIncomplete() {
        boolean complete = false;
        if (step_counter < SETUP_STEPS) {
            complete = true;
        } else if (conn_dev_counter < MIN_CON) {
            complete = true;
        } else if (dev_dev_counter < MIN_DEV) {
            complete = true;
        } else if (pnt_dev_counter < MIN_PNT) {
            complete = true;
        }
        return complete;
    }

    private static String doAThing(DSIRequester requester, RootNode root, Random random, Map<DSInfo, SubscribeHandlerImpl> subscriptions) {
        String thingDone;
        if (random.nextInt(2) < 1 || setupIncomplete()) {
            thingDone = createOrModifyDevice(random);
        } else {
            thingDone = subscribeOrDoAnAction(requester, root, random, subscriptions);
        }

        try {
            Thread.sleep(DFCarouselObject.getDelay() * 2);
        } catch (InterruptedException e) {
            assert (false);
        }

        String result = thingDone + "\n" + TestingConnection.getPrintout() + "\n" + DFHelpers.getTestingString(root, FLAT_TREE) + DELIM;
        System.out.println(result); //TODO: Remove debug
        return result;
    }

    private static String createOrModifyDevice(Random random) {
        double rand = random.nextDouble();
        if ((rand < PROB_ROOT || conn_dev_counter < MIN_CON) && conn_dev_counter < MAX_CON) {
            String c = generateConnString(random);
            addConn(c);
            conn_dev_counter++;
            return "Creating connection " + c;
        } else {
            int rrand = random.nextInt(TestingConnection.connections.size());
            Entry<String, TestingConnection> centry = (Entry<String, TestingConnection>) TestingConnection.connections.entrySet().toArray()[rrand];
            String c = centry.getKey();
            TestingConnection conn = centry.getValue();
            rand = random.nextDouble();
            if (((rand < PROB_CON / (1 - PROB_ROOT)) || dev_dev_counter < MIN_DEV)) {
                rand = random.nextDouble();
                if ((rand >= PROB_SWAP_CON_STATE || dev_dev_counter < MIN_DEV) && dev_dev_counter < MAX_DEV) {
                    String d = generateDevString(random);
                    addDev(conn, d);
                    dev_dev_counter++;
                    return "Creating device " + c + ":" + d;
                } else {
                    conn.shouldSucceed = !conn.shouldSucceed;
                    return "Setting ShouldSucceed to " + conn.shouldSucceed + " on " + c;
                }
            } else {
                int devCount = conn.devices.size();
                if (devCount == 0) return createOrModifyDevice(random);
                int crand = random.nextInt(devCount);
                Entry<String, TestingDevice> dentry = (Entry<String, TestingDevice>) conn.devices.entrySet().toArray()[crand];
                String d = dentry.getKey();
                TestingDevice dev = dentry.getValue();
                rand = random.nextDouble();
                if (((rand < PROB_DEV / (1 - PROB_ROOT - PROB_CON)) || pnt_dev_counter < MIN_PNT)) {
                    if ((rand >= PROB_SWAP_DEV_STATE || pnt_dev_counter < MIN_PNT) && pnt_dev_counter < MAX_PNT) {
                        String p = generatePointString(random);
                        String v = generatePointValue(random);
                        dev.points.put(p, v);
                        pnt_dev_counter++;
                        return "Creating new point " + c + ":" + d + ":" + p + " to " + v;
                    } else {
                        dev.active = !dev.active;
                        return "Setting Active to " + dev.active + " on " + c + ":" + d;
                    }
                } else {
                    int pointCount = dev.points.size();
                    if (pointCount == 0) return createOrModifyDevice(random);
                    int drand = random.nextInt(pointCount);
                    String p = (String) dev.points.keySet().toArray()[drand];
                    rand = random.nextDouble();
                    if (rand < PROB_REMOVE_PNT || pnt_dev_counter > MAX_PNT) {
                        dev.points.remove(p);
                        pnt_dev_counter--;
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
        DSInfo rinfo = pickAChild(root, random, 1);
        if (rinfo.isAction()) {
            return invokeAction(requester, rinfo, random);
        } else {
            assert rinfo.getObject() instanceof TestConnectionNode;
            DSInfo cinfo = pickAChild(rinfo.getNode(), random, 2);
            if (cinfo.isAction()) {
                return invokeAction(requester, cinfo, random);
            } else {
                assert cinfo.getObject() instanceof TestDeviceNode;
                DSInfo dinfo = pickAChild(cinfo.getNode(), random, 3);
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
                            requester.subscribe(path, 0, subHandle);
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
            params.put("Name", c).put("Connection String", c).put("Ping Rate", DFCarouselObject.getDelay());
            conn_node_counter++;
        } else if (name.equals("Add Device")) {
            String d = getDevStringToAdd(parent, random);
            params.put("Name", d).put("Device String", d).put("Ping Rate", DFCarouselObject.getDelay());
            dev_node_counter++;
        } else if (name.equals("Add Point")) {
            String p = getPointStringToAdd(parent, random);
            params.put("Name", p).put("ID", p).put("Poll Rate", DFCarouselObject.getDelay());
            pnt_node_counter++;
        } else if (name.equals("Remove")) {
            if (parent instanceof DFConnectionNode) {
                conn_node_counter--;
                for (String devName : getDFNodeNameSet(parent, DFDeviceNode.class)) {
                    dev_node_counter--;
                    pnt_node_counter -= getDFNodeNameSet(parent.getNode(devName), DFPointNode.class).size();
                }
                
            } else if (actionInfo.getParent() instanceof DFDeviceNode) {
                dev_node_counter--;
                pnt_node_counter -= getDFNodeNameSet(parent, DFPointNode.class).size();
            } else if (actionInfo.getParent() instanceof DFPointNode) {
                pnt_node_counter--;
            } else {
                throw new RuntimeException("Trying to remove a non DFNode: " + actionInfo.getParent().getName());
            }
        }
        requester.invoke(path, params, new InvokeHandlerImpl());
        return "Invoking " + path + " with parameters " + params;
    }

    private static DSInfo pickAChild(DSNode node, Random random, int level) {
        List<DSInfo> actions = new ArrayList<DSInfo>();
        List<DSInfo> childs = new ArrayList<DSInfo>();
        for (DSInfo info : node) {
            if (info.isAction() && !"Print".equals(info.getName())) {
                actions.add(info);
            } else if (info.isNode()) {
                DSNode n = info.getNode();
                if (n instanceof DFAbstractNode) {
                    childs.add(info);
                }
            }
        }
        boolean chooseChild = true;
        boolean tooMany = false;
        boolean tooFew = false;

        switch (level) {
            case (1):
                if (conn_node_counter < MIN_CON) tooFew = true;
                else if (conn_node_counter >= conn_dev_counter) tooMany = true;
                break;
            case (2):
                if (dev_node_counter < MIN_DEV) tooFew = true;
                else if (dev_node_counter >= dev_dev_counter) tooMany = true;
                break;
            case (3):
                if (pnt_node_counter < MIN_PNT) tooFew = true;
                else if (pnt_node_counter >= pnt_dev_counter) tooMany = true;
                break;
        }

        if (actions.isEmpty() || (node instanceof DSRootNode && tooMany)) {
            chooseChild = true;
        } else if (childs.isEmpty()) {
            chooseChild = false;
        } else {
            double choice = random.nextDouble();
            if (level == 1) {
                if (choice < PROB_ROOT) chooseChild = false;
            } else if (level == 2) {
                if (choice < PROB_CON / (1 - PROB_ROOT)) chooseChild = false;
            } else if (level == 3) {
                if (choice < PROB_DEV / (1 - PROB_ROOT - PROB_CON)) chooseChild = false;
            }
        }
        if (chooseChild) {
            int choice = random.nextInt(childs.size());
            return childs.get(choice);
        } else {
            DSInfo action;
            do {
                int choice = random.nextInt(actions.size());
                action = actions.get(choice);
            } while (!actionIsProper(action, tooFew, tooMany));
            return action;
        }
    }

    private static boolean actionIsProper(DSInfo action, boolean few, boolean many) {
        if (few && action.getName().startsWith("Remove")) {
            return false;
        } else if (many && action.getName().startsWith("Add")) {
            return false;
        }
        return true;
    }

    private static Set<String> getDFNodeNameSet(DSNode parent, Class<? extends DFAbstractNode> className) {
        HashSet<String> nodes = new HashSet<String>();
        for (DSInfo info : parent) {
            if (info.isNode()) {
                if (className.isAssignableFrom(info.getNode().getClass())) {
                    nodes.add(info.getName());
                }
            }
        }
        return nodes;
    }
    
    
    private static String getChildNameStringHelper(Object[] possibleNames, Set<String> nodes, Random random) {
        int size = possibleNames.length;
        if (random.nextDouble() >= PROB_OF_BAD_CONFIG) {
            int choice = size > 0 ? random.nextInt(size) : 0;
            for (int i = 0; i < size; i++) {
                int nextIdx = (i + choice) % size;
                String name = (String) possibleNames[nextIdx];
                if (!nodes.contains(name)) return name;
            }
        }
        return null;
    }

    private static String getConnStringToAdd(DSNode parent, Random random) {
        Set<String> nodes = getDFNodeNameSet(parent, DFConnectionNode.class);
        Object[] possibleNames = TestingConnection.connections.keySet().toArray();
        String name = getChildNameStringHelper(possibleNames, nodes, random);
        return name != null ? name : generateConnString(random);
    }

    private static String getDevStringToAdd(DSNode parent, Random random) {
        Set<String> nodes = getDFNodeNameSet(parent, DFDeviceNode.class);
        TestingConnection conn = TestingConnection.connections.get(parent.getName());
        String name = null;
        if (conn != null) {
            Object[] possibleNames = conn.devices.keySet().toArray();
            name = getChildNameStringHelper(possibleNames, nodes, random);
        }
        return name != null ? name : generateDevString(random);
    }

    private static String getPointStringToAdd(DSNode parent, Random random) {
        Set<String> nodes = getDFNodeNameSet(parent, DFPointNode.class);
        TestingConnection conn = TestingConnection.connections.get(parent.getParent().getName());
        TestingDevice dev = conn != null ? conn.devices.get(parent.getName()) : null;
        String name = null;
        if (dev != null) {
            Object[] possibleNames = dev.points.keySet().toArray();
            name = getChildNameStringHelper(possibleNames, nodes, random);
        }
        return name != null ? name : generatePointString(random);
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

    private static String generateDevString(Random random) {
        return pickAName(DFHelpers.colors, DFHelpers.animals, random, true);
    }

    private static String generatePointString(Random random) {
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
