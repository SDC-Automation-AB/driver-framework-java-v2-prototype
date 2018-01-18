package org.iot.dsa.dslink.dftest;

import com.acuity.iot.dsa.dslink.test.TestLink;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.lang3.StringUtils;
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
import org.junit.Before;
import org.junit.Test;
import org.python.util.PythonInterpreter;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import static org.junit.Assert.*;

public class FuzzTest {

    private static final long TEST_STEPS = 1000;
    private static final long SETUP_STEPS = 60;
    private static final boolean FLAT_TREE = false;
    private static final boolean VERBOSE = false;
    private static final long SEED = 420;

    private static final long MIN_CON = 2;
    private static final long MAX_CON = 4;
    private static final long MIN_DEV = 4;
    private static final long MAX_DEV = 8;
    private static final long MIN_PNT = 16;
    private static final long MAX_PNT = 24;

    private static final double PROB_ROOT = .1;
    private static final double PROB_CON = .2;
    private static final double PROB_DEV = .3;
    private static final double PROB_PNT = .4;
    private static final double PROB_SWAP_CON_STATE = .5;
    private static final double PROB_SWAP_DEV_STATE = .5;
    private static final double PROB_REMOVE_PNT = .1;

    private static final double PROB_OF_BAD_CONFIG = 0.01;

    private static final long PING_POLL_RATE = 100;

    private static Set<String> unique_names = new HashSet<String>();
    private static Map<DSInfo, SubscribeHandlerImpl> subscriptions = new HashMap<DSInfo, SubscribeHandlerImpl>();
    private static final Random random = new Random(SEED);
    private static RootNode staticRootNode = null;
    private static DSIRequester requester = null;
    private static DelayedActionOrSub queuedAction = null;

    private static long step_counter = 0;
    private static long conn_node_counter = 0;
    private static long dev_node_counter = 0;
    private static long pnt_node_counter = 0;
    private static long conn_dev_counter = 0;
    private static long dev_dev_counter = 0;
    private static long pnt_dev_counter = 0;
    private static final String DELIM = "\n\n== STEP ===============================================================================";

    private static final String MASTER_OUT_FILENAME = "master-output.txt";
    private static final String TESTING_OUT_FILENAME = "testing-output.txt";
    private static final String PY_TEST_DIR = "py_tests";

    private static PythonInterpreter interp;
    private static boolean REGENERATE_OUTPUT = false; //Set to false if you don't want to re-run the Fuzz

    @Before
    public void setUp() {
        if (REGENERATE_OUTPUT) {
            assertEquals(1.0, PROB_ROOT + PROB_CON + PROB_DEV + PROB_PNT, .01);
            staticRootNode = new RootNode();
            DSLink link = new TestLink(staticRootNode);
            DSRuntime.run(link);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }

            requester = link.getConnection().getRequester();
            PrintWriter writer = getNewPrintWriter();

            //Main loop
            while (step_counter < TEST_STEPS) {
                String result = doAThing();
                waitAWhile();
                printResult(result, writer);
                step_counter++;
            }

            writer.close();
            REGENERATE_OUTPUT = false;
        }
    }

    /**
     * Checks whether the output file is an exact match to the golden output.
     * @throws IOException
     */
    @Test
    public void exactMatchTest() throws IOException {
        List<String> masterLines = fileToLines(new File(MASTER_OUT_FILENAME));
        List<String> testingLines = fileToLines(new File(TESTING_OUT_FILENAME));

        Patch<String> diff = DiffUtils.diff(masterLines, testingLines);
        List<String> diffText = DiffUtils.generateUnifiedDiff(MASTER_OUT_FILENAME, TESTING_OUT_FILENAME, masterLines, diff, 0);
        if (!diffText.isEmpty()) {
            String diffString = StringUtils.join(diffText, '\n');
            fail("Output does not match:\n" + diffString);
        }
    }

    /**
     * This tests whether the python testing framework is working correctly
     */
    @Test
    public void pythonFrameworkTest() throws Exception {
        String t_name = "helloo_world.py";
        runPythonTest(t_name);
    }
    
    /**
     * Checks that, at any point in time, if a point is not "Stopped", then it was subscribed 
     * to at some point in the past, and was not unsubscribed from since then.
     */
    @Test
    public void connected_was_subbed() throws Exception {
        String t_name = "connected_was_subbed.py";
        runPythonTest(t_name);
    }
    
    /**
     * Checks that whenever a point or device node is "Connected" or "Failed", its parent is "Connected"
     */
    @Test
    public void parent_connected() throws Exception {
        String t_name = "parent_connected.py";
        runPythonTest(t_name);
    }

    /**
     * After a point node is subscribed to, checks that if its parent is "Connected" and the
     * corresponding point exists on the device, then the node's status is "Connected" and its value
     * is the same as the value of the point on the device
     */
    @Test
    public void subbed_is_connected() throws Exception {
        String t_name = "subbed_is_connected.py";
        runPythonTest(t_name);
    }

    /**
     * After a point node is subscribed to, checks that if its parent is "Connected" and the
     * corresponding point doesn't exist on the device, then the node's status is "Failed"
     */
    @Test
    public void subbed_is_failed() throws Exception {
        String t_name = "subbed_is_failed.py";
        runPythonTest(t_name);
    }

    /**
     * After a point node is unsubscribed from, checks that if its parent is "Connected", then the
     * node's status is "Stopped"
     */
    @Test
    public void unsubbed_is_stopped() throws Exception {
        String t_name = "unsubbed_is_stopped.py";
        runPythonTest(t_name);
    }

    /**
     * Checks that active ("Connected") point nodes update their values correctly
     */
    @Test
    public void value_updates() throws Exception {
        String t_name = "value_updates.py";
        runPythonTest(t_name);
    }

    private static void runPythonTest(String fileName) throws Exception {
        String exec = PY_TEST_DIR + "\\" + fileName;
        PythonInterpreter terp = getPyInterpreter();
        runFile(exec, terp);
    }

    private static PythonInterpreter getPyInterpreter() {
        if (interp == null) {
            interp = new PythonInterpreter();

            //This sets up the interpreter to understand scripts
            interp.exec("import os\n" +
                    "os.chdir(\"" + PY_TEST_DIR + "\")\n" +
                    "import sys\n" +
                    "sys.path.append(os.getcwd())\n");
        }
        return interp;
    }

    static void runFile(String str, PythonInterpreter interp) throws Exception {
        File f = new File(str);
        InputStream s = new FileInputStream(f);
        try {
            interp.execfile(s);
            System.out.println("Test " + str + ": PASSED!");
        } catch (Exception e) {
            System.out.println("Test " + str + ": FAILED!");
            throw e;
        }
    }

    private List<String> fileToLines(File file) throws IOException {
        final List<String> lines = new ArrayList<String>();
        String line;
        final BufferedReader in = new BufferedReader(new FileReader(file));
        while ((line = in.readLine()) != null) {
            lines.add(line);
        }
        in.close();

        return lines;
    }

    private static PrintWriter getNewPrintWriter() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(TESTING_OUT_FILENAME, "UTF-8");
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            fail(e.getMessage());
        }
        if (writer == null) {
            fail("Failed to set up PrintWriter");
        }
        return writer;
    }

    private static void waitAWhile() {
        if (!setupIncomplete()) {
            try {
                Thread.sleep(PING_POLL_RATE * 2);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }
    }

    private static void printResult(String thingDone, PrintWriter writer) {
        String result = thingDone + "\n" + TestingConnection.getPrintout() + "\n" + DFHelpers.getTestingString(staticRootNode, FLAT_TREE, VERBOSE) + DELIM.replaceFirst("STEP", Long.toString(step_counter + 1));
        writer.println(result);
        writer.flush();
        System.out.println(result); //TODO: Remove debug
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

    private static String doAThing() {
        String thingDone;
        if (queuedAction != null) {
            thingDone = queuedAction.act();
            queuedAction = null;
        } else if (random.nextInt(2) < 1 || setupIncomplete()) {
            thingDone = createOrModifyDevice();
        } else {
            thingDone = subscribeOrDoAnAction();
        }
        return thingDone;
    }

    private static String createOrModifyDevice() {
        double rand = random.nextDouble();
        if ((rand < PROB_ROOT || conn_dev_counter < MIN_CON) && conn_dev_counter < MAX_CON) {
            String c = generateConnString();
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
                    String d = generateDevString();
                    addDev(conn, d);
                    dev_dev_counter++;
                    return "Creating device " + c + ":" + d;
                } else {
                    conn.shouldSucceed = !conn.shouldSucceed;
                    return "Setting ShouldSucceed to " + conn.shouldSucceed + " on " + c;
                }
            } else {
                int devCount = conn.devices.size();
                if (devCount == 0) return createOrModifyDevice();
                int crand = random.nextInt(devCount);
                Entry<String, TestingDevice> dentry = (Entry<String, TestingDevice>) conn.devices.entrySet().toArray()[crand];
                String d = dentry.getKey();
                TestingDevice dev = dentry.getValue();
                rand = random.nextDouble();
                if (((rand < PROB_DEV / (1 - PROB_ROOT - PROB_CON)) || pnt_dev_counter < MIN_PNT)) {
                    if ((rand >= PROB_SWAP_DEV_STATE || pnt_dev_counter < MIN_PNT) && pnt_dev_counter < MAX_PNT) {
                        String p = generatePointString();
                        String v = generatePointValue();
                        dev.points.put(p, v);
                        pnt_dev_counter++;
                        return "Creating new point " + c + ":" + d + ":" + p + " to " + v;
                    } else {
                        dev.active = !dev.active;
                        return "Setting Active to " + dev.active + " on " + c + ":" + d;
                    }
                } else {
                    int pointCount = dev.points.size();
                    if (pointCount == 0) return createOrModifyDevice();
                    int drand = random.nextInt(pointCount);
                    String p = (String) dev.points.keySet().toArray()[drand];
                    rand = random.nextDouble();
                    if (rand < PROB_REMOVE_PNT || pnt_dev_counter > MAX_PNT) {
                        dev.points.remove(p);
                        pnt_dev_counter--;
                        return "Removing point " + c + ":" + d + ":" + p;
                    } else {
                        String v = generatePointValue();
                        dev.points.put(p, v);
                        return "Setting point " + c + ":" + d + ":" + p + " to " + v;
                    }
                }
            }
        }
    }

    private static String subscribeOrDoAnAction() {
        DSInfo rinfo = pickAChild(staticRootNode, 1);
        if (rinfo.isAction()) {
            return invokeAction(rinfo);
        } else {
            assertTrue(rinfo.getObject() instanceof TestConnectionNode);
            DSInfo cinfo = pickAChild(rinfo.getNode(), 2);
            if (cinfo.isAction()) {
                return invokeAction(cinfo);
            } else {
                assertTrue(cinfo.getObject() instanceof TestDeviceNode);
                DSInfo dinfo = pickAChild(cinfo.getNode(), 3);
                if (dinfo.isAction()) {
                    return invokeAction(dinfo);
                } else {
                    assertTrue(dinfo.getObject() instanceof TestPointNode);
                    int choice = random.nextInt(10);
                    if (choice == 0) {
                        return invokeAction(dinfo.getNode().getInfo("Remove"));
                    } else if (choice == 1) {
                        return invokeAction(dinfo.getNode().getInfo("Edit"));
                    } else {
                        return subscribeOrUnsubscribe(dinfo);
                    }
                }
            }
        }
    }

    private static String subscribeOrUnsubscribe(DSInfo dsInfo) {
        String path = dsInfo.getNode().getPath();
        SubscribeHandlerImpl subHandle = subscriptions.remove(dsInfo);
        if (subHandle == null) {
            subHandle = new SubscribeHandlerImpl();
            requester.subscribe(path, 0, subHandle);
            subscriptions.put(dsInfo, subHandle);
            return "Subscribing to " + path;
        } else {
            subHandle.getStream().closeStream();
            return "Unsubscribing from " + path;
        }
    }

    private static String invokeAction(DSInfo actionInfo) {
        String name = actionInfo.getName();
        DSNode parent = actionInfo.getParent();
        String path = parent.getPath();
        path = path.endsWith("/") ? path + name : path + "/" + name;
        DSMap params = new DSMap();
        if (name.equals("Edit")) {
            return "Edit action not yet supported!";
        } else if (name.equals("Add Connection")) {
            String c = getConnStringToAdd(parent);
            params.put("Name", c).put("Connection String", c).put("Ping Rate", PING_POLL_RATE);
            conn_node_counter++;
        } else if (name.equals("Add Device")) {
            String d = getDevStringToAdd(parent);
            params.put("Name", d).put("Device String", d).put("Ping Rate", PING_POLL_RATE);
            dev_node_counter++;
        } else if (name.equals("Add Point")) {
            String p = getPointStringToAdd(parent);
            params.put("Name", p).put("ID", p).put("Poll Rate", PING_POLL_RATE);
            queuedAction = new DelayedActionOrSub(actionInfo.getParent(), p);
            pnt_node_counter++;
        } else if (name.equals("Remove")) {
            if (parent instanceof DFConnectionNode) {
                conn_node_counter--;
                for (String devName : getDFNodeNameSet(parent, DFDeviceNode.class)) {
                    dev_node_counter--;
                    DSNode devNode = parent.getNode(devName);
                    for (String pointName : getDFNodeNameSet(devNode, DFPointNode.class)) {
                        pnt_node_counter--;
                        SubscribeHandlerImpl handle = subscriptions.remove(devNode.getInfo(pointName));
                        if (handle != null) handle.getStream().closeStream();
                    }
                }
            } else if (parent instanceof DFDeviceNode) {
                dev_node_counter--;
                for (String pointName : getDFNodeNameSet(parent, DFPointNode.class)) {
                    pnt_node_counter--;
                    SubscribeHandlerImpl handle = subscriptions.remove(parent.getInfo(pointName));
                    if (handle != null) handle.getStream().closeStream();
                }
            } else if (parent instanceof DFPointNode) {
                pnt_node_counter--;
                SubscribeHandlerImpl handle = subscriptions.remove(parent.getInfo());
                if (handle != null) handle.getStream().closeStream();
            } else {
                throw new RuntimeException("Trying to remove a non DFNode: " + actionInfo.getParent().getName());
            }
        }
        requester.invoke(path, params, new InvokeHandlerImpl());
        return "Invoking " + path + " with parameters " + params;
    }

    private static DSInfo pickAChild(DSNode node, int level) {
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

    private static String getChildNameStringHelper(Object[] possibleNames, Set<String> nodes) {
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

    private static String getConnStringToAdd(DSNode parent) {
        Set<String> nodes = getDFNodeNameSet(parent, DFConnectionNode.class);
        Object[] possibleNames = TestingConnection.connections.keySet().toArray();
        String name = getChildNameStringHelper(possibleNames, nodes);
        return name != null ? name : generateConnString();
    }

    private static String getDevStringToAdd(DSNode parent) {
        Set<String> nodes = getDFNodeNameSet(parent, DFDeviceNode.class);
        TestingConnection conn = TestingConnection.connections.get(parent.getName());
        String name = null;
        if (conn != null) {
            Object[] possibleNames = conn.devices.keySet().toArray();
            name = getChildNameStringHelper(possibleNames, nodes);
        }
        return name != null ? name : generateDevString();
    }

    private static String getPointStringToAdd(DSNode parent) {
        Set<String> nodes = getDFNodeNameSet(parent, DFPointNode.class);
        TestingConnection conn = TestingConnection.connections.get(parent.getParent().getName());
        TestingDevice dev = conn != null ? conn.devices.get(parent.getName()) : null;
        String name = null;
        if (dev != null) {
            Object[] possibleNames = dev.points.keySet().toArray();
            name = getChildNameStringHelper(possibleNames, nodes);
        }
        return name != null ? name : generatePointString();
    }

    private static boolean notUnique(String name) {
        if (unique_names.contains(name)) {
            return true;
        } else {
            unique_names.add(name);
            return false;
        }
    }

    private static String pickAName(String[] mods, String[] names, boolean camel) {
        String str;
        do {
            String one = mods[random.nextInt(mods.length)];
            String two = names[random.nextInt(names.length)];
            if (camel) str = one + two;
            else str = one + "_" + two;
        } while (notUnique(str));
        return str;
    }

    private static String generateConnString() {
        return pickAName(DFHelpers.colors, DFHelpers.places, true);
    }

    private static String generateDevString() {
        return pickAName(DFHelpers.colors, DFHelpers.animals, true);
    }

    private static String generatePointString() {
        return pickAName(DFHelpers.colors, DFHelpers.parts, false);
    }

    private static String generatePointValue() {
        return DFHelpers.adjectives[random.nextInt(DFHelpers.adjectives.length)];
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

    static class DelayedActionOrSub {
        String path = null;
        DSMap params = null;
        DSNode parent = null;
        String pointName = null;

        /**
         * Constructor for doing an action
         */
        DelayedActionOrSub(String path, DSMap params) {
            this.path = path;
            this.params = params;
        }

        /**
         * Constructor for subscribing or unsubscribing
         */
        DelayedActionOrSub(DSNode parent, String pointName) {
            this.parent = parent;
            this.pointName = pointName;
        }

        String act() {
            if (parent != null) {
                return FuzzTest.subscribeOrUnsubscribe(parent.getInfo(pointName));
            } else {
                FuzzTest.requester.invoke(path, params, new FuzzTest.InvokeHandlerImpl());
                return "Invoking Queued:" + path + " with parameters " + params;
            }
        }
    }

}
