package org.iot.dsa.dslink.dftest;

import com.acuity.iot.dsa.dslink.test.TestLink;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.lang3.StringUtils;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSIRequester;
import org.iot.dsa.dslink.DSLink;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.dslink.dfexample.MainNode;
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

import static org.junit.Assert.*;

public class FuzzTest {

    public static long TEST_STEPS = 1000;
    public static long SETUP_STEPS = 60;
    public static boolean REGENERATE_OUTPUT = true; //Set to false if you don't want to re-run the Fuzz
    public static boolean FLAT_TREE = false;
    public static boolean VERBOSE = false;
    public static long SEED = 420;

    public static long MIN_CON = 2;
    public static long MAX_CON = 4;
    public static long MIN_DEV = 4;
    public static long MAX_DEV = 8;
    public static long MIN_PNT = 16;
    public static long MAX_PNT = 24;

    public static double PROB_ROOT = .1;
    public static double PROB_CON = .2;
    public static double PROB_DEV = .3;
    public static double PROB_PNT = .4;
    public static double PROB_ACTION = .5;

    public static boolean UNPLUG_DEVICES = true;
    public static double PROB_OFF_CON_STATE = .3;
    public static double PROB_OFF_DEV_STATE = .3;
    public static double PROB_ON_CON_STATE = .7;
    public static double PROB_ON_DEV_STATE = .7;
    public static double PROB_REMOVE_PNT = .1;


    public static double PROB_OF_BAD_CONFIG = 0.01;

    public static long PING_POLL_RATE = 15;
    public static long INTERSTEP_WAIT_TIME = PING_POLL_RATE * 2;
    public static int SUBSCRIBE_DELAY_RETRIES = 100;
    public static int SUBSCRIBE_DELAY_WAIT_MILIS = 100;

    private static Set<String> unique_names = new HashSet<String>();
    static Map<DSInfo, SubscribeHandlerImpl> subscriptions = new HashMap<DSInfo, SubscribeHandlerImpl>();
    private static final Random random = new Random(SEED);
    private static DSMainNode staticRootNode = null;
    public static DSIRequester requester = null;
    static DelayedActionOrSub queuedAction = null;

    static long step_counter = 0;
    static long conn_node_counter = 0;
    static long dev_node_counter = 0;
    static long pnt_node_counter = 0;
    static long conn_dev_counter = 0;
    static long dev_dev_counter = 0;
    static long pnt_dev_counter = 0;

    private static final String DELIM = "\n\n== STEP ===============================================================================";
    public static final String MASTER_OUT_FILENAME = "master-output.txt";
    public static final String TESTING_OUT_FILENAME = "testing-output.txt";
    private static final String PY_TEST_JAR = "/py_tests";
    private static final String PY_TEST_DIR = "src\\main\\resources\\py_tests\\";

    private static PythonInterpreter interp;
    public static final boolean PRINT_TO_CONSOLE = true;

    public static void prepareToFuzz(DSMainNode root) {
        staticRootNode = root;
        DSLink link = new TestLink(staticRootNode);
        DSRuntime.run(link);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        requester = link.getConnection().getRequester();

    }

    @Before
    public void setUp() {
        if (REGENERATE_OUTPUT) {
            assertEquals(1.0, PROB_ROOT + PROB_CON + PROB_DEV + PROB_PNT, .01);

            PrintWriter writer = getNewPrintWriter(TESTING_OUT_FILENAME);
            builFuzzDoubleTree(TEST_STEPS, writer, new MainNode(), new TestingConnection(), new DFFuzzNodeAction());

            writer.close();
            REGENERATE_OUTPUT = false;
        }
    }

    public static void buildMockTree(int size, TestingConnection seedObject) {
        while (step_counter < size) {
            System.out.println(createOrModifyDevice(seedObject));
            System.out.println(TestingConnection.getPrintout(false));
            step_counter++;
        }
        System.out.println(DELIM.replaceFirst("STEP", "COMPLETE TREE"));
        System.out.println(TestingConnection.getPrintout(true));
    }

    public static void builFuzzDoubleTree(long size, PrintWriter writer, DSMainNode root, TestingConnection seedObject, FuzzNodeActionContainer fz) {
        prepareToFuzz(root);

        //Main loop
        while (step_counter < size) {
            String thing = doAThing(seedObject, fz);
            waitAWhile();
            printResult(thing, writer, VERBOSE);
            step_counter++;
        }

        System.out.println(DELIM.replaceFirst("STEP", "COMPLETE TREE"));
        printResult("Final Summary", null, true);
    }

    //@Test
    public void buildMockTreeTest() {
        buildMockTree(100, new TestingConnection());
    }

    //@Test
    public void buildActionTreeTest() {
        builFuzzDoubleTree(100, null, new MainNode(), new TestingConnection(), new DFFuzzNodeAction());
    }

    /**
     * Checks whether the output file is an exact match to the golden output.
     *
     * @throws IOException Failed to find the required inputs/outputs
     */
    //@Test
    public void exactMatchTest() throws IOException {
        performDiff(MASTER_OUT_FILENAME, TESTING_OUT_FILENAME);
    }

    public static void performDiff(String master, String test) throws IOException {
        List<String> masterLines = fileToLines(new File(master));
        List<String> testingLines = fileToLines(new File(test));

        Patch<String> diff = DiffUtils.diff(masterLines, testingLines);
        List<String> diffText = DiffUtils.generateUnifiedDiff(master, test, masterLines, diff, 0);
        if (!diffText.isEmpty()) {
            String diffString = StringUtils.join(diffText, '\n');
            fail("Output does not match:\n" + diffString);
        }
    }

    private static List<String> fileToLines(File file) throws IOException {
        final List<String> lines = new ArrayList<String>();
        String line;
        final BufferedReader in = new BufferedReader(new FileReader(file));
        while ((line = in.readLine()) != null) {
            lines.add(line);
        }
        in.close();

        return lines;
    }

    /**
     * This tests whether the python testing framework is working correctly
     */
    @Test
    public void pythonFrameworkTest() throws Exception {
        String t_name = "helloo_world.py";
        runPythonTestFromJar(t_name);
    }

    /**
     * Checks that, at any point in time, if a point is not "Stopped", then it was subscribed
     * to at some point in the past, and was not unsubscribed from since then.
     */
    @Test
    public void connected_was_subbed() throws Exception {
        String t_name = "connected_was_subbed.py";
        runPythonTestFromJar(t_name);
    }

    /**
     * Checks that whenever a point or device node is "Connected" or "Failed", its parent is "Connected"
     */
    @Test
    public void parent_connected() throws Exception {
        String t_name = "parent_connected.py";
        runPythonTestFromJar(t_name);
    }

    /**
     * After a point node is subscribed to, checks that if its parent is "Connected" and the
     * corresponding point exists on the device, then the node's status is "Connected" and its value
     * is the same as the value of the point on the device
     */
    @Test
    public void subbed_is_connected() throws Exception {
        String t_name = "subbed_is_connected.py";
        runPythonTestFromJar(t_name);
    }

    /**
     * After a point node is subscribed to, checks that if its parent is "Connected" and the
     * corresponding point doesn't exist on the device, then the node's status is "Failed"
     */
    @Test
    public void subbed_is_failed() throws Exception {
        String t_name = "subbed_is_failed.py";
        runPythonTestFromJar(t_name);
    }

    /**
     * After a point node is unsubscribed from, checks that if its parent is "Connected", then the
     * node's status is "Stopped"
     */
    @Test
    public void unsubbed_is_stopped() throws Exception {
        String t_name = "unsubbed_is_stopped.py";
        runPythonTestFromJar(t_name);
    }

    /**
     * Checks that active ("Connected") point nodes update their values correctly
     */
    @Test
    public void value_updates() throws Exception {
        String t_name = "value_updates.py";
        runPythonTestFromJar(t_name);
    }

    @Test
    public void add_works() throws Exception {
        String t_name = "add_works.py";
        runPythonTestFromJar(t_name);
    }

    @Test
    public void remove_works() throws Exception {
        String t_name = "remove_works.py";
        runPythonTestFromJar(t_name);
    }

    @Test
    public void stop_start_works() throws Exception {
        String t_name = "stop_start_works.py";
        runPythonTestFromJar(t_name);
    }

    @Test
    public void all_subscriptions_work() throws Exception {
        String t_name = "all_subscriptions_work.py";
        runPythonTestFromJar(t_name);
    }



    //TODO: write python test to check that when a testing conn/dev/point is deactivated, it's corresponding node is failed
    //TODO: write test to check that when a testing conn/dev/point is activated, it's corresponding node is active

    public static void runPythonTestFromJar(String fileName) throws Exception {
        String exec = PY_TEST_JAR + "/" + fileName;
        PythonInterpreter terp = getPyInterpreter();
        runFileFromJar(exec, terp);
    }

    public static void runPythonTestFromDir(String fileName) throws Exception {
        String exec = PY_TEST_DIR + fileName;
        PythonInterpreter terp = getPyInterpreter();
        runFileFromDir(exec, terp);
    }

    private static PythonInterpreter getPyInterpreter() {
        if (interp == null) {
//            PythonInterpreter.initialize(System.getProperties(), System.getProperties(), new String[0]);
            interp = new PythonInterpreter();

            //This imports the output_parser.py dependency, all other dependencies have to be added here as well
            InputStream parser_dependency = FuzzTest.class.getClass().getResourceAsStream("/py_tests/output_parser.py");
            interp.execfile(parser_dependency);

            //This is no longer necessary, as path is not being used
//            //This sets up the interpreter to understand scripts
//            interp.exec("import os\n" +
//               //     "os.chdir(\"" + PY_TEST_DIR + "\")\n" +
//                    "import sys\n" +
//                    "sys.path.append(os.getcwd())\n");
        }
        return interp;
    }

    static void runFileFromDir(String scriptName, PythonInterpreter interp) throws Exception {
        File f = new File(scriptName);
        InputStream pyStr = new FileInputStream(f);
        execPyScript(interp, pyStr, scriptName);
    }

    static void runFileFromJar(String scriptName, PythonInterpreter interp) throws Exception {
        InputStream pyStr = FuzzTest.class.getClass().getResourceAsStream(scriptName);
        execPyScript(interp, pyStr, scriptName);
    }

    static void execPyScript(PythonInterpreter interp, InputStream scriptStream, String name) {
        try {
            interp.execfile(scriptStream);
            System.out.println("Test " + name + ": PASSED!");
        } catch (Exception e) {
            System.out.println("Test " + name + ": FAILED!");
            throw e;
        }
    }

    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static PrintWriter getNewPrintWriter(String fileName) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
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
                Thread.sleep(INTERSTEP_WAIT_TIME);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }
    }

    private static void printResult(String thingDone, PrintWriter writer, boolean verbose) {
        String result = thingDone + "\n" + TestingConnection.getPrintout(verbose) + "\n" + DFHelpers.getTestingString(staticRootNode, FLAT_TREE, verbose) + DELIM.replaceFirst("STEP", Long.toString(step_counter + 1));
        if (writer != null) {
            writer.println(result);
            writer.flush();
        }
        if (PRINT_TO_CONSOLE) System.out.println(result);
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

    private static String doAThing(TestingConnection tstConn, FuzzNodeActionContainer fzNode) {
        String thingDone;
        if (queuedAction != null) {
            thingDone = queuedAction.act();
            queuedAction = null;
        } else if (random.nextDouble() > PROB_ACTION || setupIncomplete()) {
            thingDone = createOrModifyDevice(tstConn);
        } else {
            thingDone = subscribeOrDoAnAction(fzNode);
        }
        return thingDone;
    }

    /**
     * Perform an action on the global mock device tree
     * @param testConnSeed Dummy TestingConnection instance used to seed the mock tree construction.
     * @return Return a description of the action performed
     * */
    private static String createOrModifyDevice(TestingConnection testConnSeed) {
        double rand = random.nextDouble();
        //Create a connection
        if ((rand < PROB_ROOT || conn_dev_counter < MIN_CON) && conn_dev_counter < MAX_CON) {
            String c = generateConnString();
            testConnSeed.addNewConnection(c, random);
            conn_dev_counter++;
            return "Creating connection " + c;
            //Or choose a connection to act on
        } else {
            int rrand = random.nextInt(TestingConnection.getConnectionCount());
            String c = TestingConnection.getNthConnectionName(rrand);
            TestingConnection conn = TestingConnection.getConnection(c);
            rand = random.nextDouble();
            //Act on the connection (create new device or flip state)
            if (((rand < PROB_CON / (1 - PROB_ROOT)) || dev_dev_counter < MIN_DEV)) {
                rand = random.nextDouble();
                Double flipChance = conn.pluggedIn ? PROB_OFF_CON_STATE : PROB_ON_CON_STATE;
                if ((rand >= flipChance || dev_dev_counter < MIN_DEV) && dev_dev_counter < MAX_DEV) {
                    String d = generateDevString();
                    conn.addNewDevice(d, random);
                    dev_dev_counter++;
                    return "Creating device " + c + ":" + d;
                } else {
                    if (UNPLUG_DEVICES) {
                        return "Setting ShouldSucceed to " + conn.flipPowerSwitch() + " on " + c;
                    } else {
                        return "Unplugging connections is disabled, skipping " + c;
                    }
                }
                //Or choose a device to act on
            } else {
                int devCount = conn.getDeviceCount();
                if (devCount == 0) return createOrModifyDevice(new TestingConnection());
                int crand = random.nextInt(devCount);
                String d = conn.getNthDeviceName(crand);
                TestingDevice dev = conn.getDevice(d);
                rand = random.nextDouble();
                //Act on a device (create a new point or flip state)
                if (((rand < PROB_DEV / (1 - PROB_ROOT - PROB_CON)) || pnt_dev_counter < MIN_PNT)) {
                    rand = random.nextDouble();
                    Double flipChance = dev.active ? PROB_OFF_DEV_STATE : PROB_ON_DEV_STATE;
                    if ((rand >= flipChance || pnt_dev_counter < MIN_PNT) && pnt_dev_counter < MAX_PNT) {
                        String p = generatePointString();
                        String v = generatePointValue();
                        dev.addPoint(p, v, random);
                        pnt_dev_counter++;
                        return "Creating new point " + c + ":" + d + ":" + p + " to " + v;
                    } else {
                        if (UNPLUG_DEVICES) {
                            return "Setting Active to " + dev.flipDev() + " on " + c + ":" + d;
                        } else {
                            return "Unplugging devices is disabled, skipping " + c + ":" + d;
                        }
                    }
                    //Or choose a point to act on
                } else {
                    int pointCount = dev.getPointCount();
                    if (pointCount == 0) return createOrModifyDevice(new TestingConnection());
                    int drand = random.nextInt(pointCount);
                    String p = dev.getNthPointName(drand);
                    rand = random.nextDouble();
                    //Either delete or change the value of a point
                    if (rand < PROB_REMOVE_PNT || pnt_dev_counter > MAX_PNT) {
                        dev.removePoint(p);
                        pnt_dev_counter--;
                        return "Removing point " + c + ":" + d + ":" + p;
                    } else {
                        String v = generatePointValue();
                        dev.changePointValue(p, v);
                        return "Setting point " + c + ":" + d + ":" + p + " to " + v;
                    }
                }
            }
        }
    }

    private static String subscribeOrDoAnAction(FuzzNodeActionContainer fz) {
        DSInfo rinfo = pickAChild(staticRootNode, 1);
        if (rinfo.isAction()) {
            return fz.invokeAction(rinfo, random);
        } else {
            assertTrue(rinfo.getObject() instanceof DFConnectionNode);
            DSInfo cinfo = pickAChild(rinfo.getNode(), 2);
            if (cinfo.isAction()) {
                return fz.invokeAction(cinfo, random);
            } else {
                assertTrue(cinfo.getObject() instanceof DFDeviceNode);
                DSInfo dinfo = pickAChild(cinfo.getNode(), 3);
                if (dinfo.isAction()) {
                    return fz.invokeAction(dinfo, random);
                } else {
                    assertTrue(dinfo.getObject() instanceof DFPointNode);
                    int choice = random.nextInt(10);
                    if (choice == 0) {
                        return fz.invokeAction(dinfo.getNode().getInfo("Remove"), random);
                    } else if (choice == 1) {
                        return fz.invokeAction(dinfo.getNode().getInfo("Edit"), random);
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

    //TODO: Remove old unreachable code once DF is done
 /*   private static String invokeAction(DSInfo actionInfo) {
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
            queuedAction = new DelayedActionOrSub(parent, p);
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
    }*/

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

        if (actions.isEmpty() || (node instanceof DSMainNode && tooMany)) {
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

    static Set<String> getDFNodeNameSet(DSNode parent, Class<? extends DFAbstractNode> className) {
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

    private static String getChildNameStringHelper(String[] possibleNames, Set<String> nodes) {
        int size = possibleNames.length;
        if (random.nextDouble() >= PROB_OF_BAD_CONFIG) {
            int choice = size > 0 ? random.nextInt(size) : 0;
            for (int i = 0; i < size; i++) {
                int nextIdx = (i + choice) % size;
                String name = possibleNames[nextIdx];
                if (!nodes.contains(name)) return name;
            }
        }
        return null;
    }

    static String getConnStringToAdd(DSNode parent, DSMap params) {
        Set<String> nodes = getDFNodeNameSet(parent, DFConnectionNode.class);
        String[] possibleNames = TestingConnection.getConnectionList();
        String name = getChildNameStringHelper(possibleNames, nodes);

        if (name != null) {
            TestingConnection.putConnectionParams(name, params);
            return name;
        } else {
            return generateConnString();
        }
    }

    static String getDevStringToAdd(DSNode parent, DSMap params) {
        Set<String> nodes = getDFNodeNameSet(parent, DFDeviceNode.class);
        TestingConnection conn = TestingConnection.getConnection(parent.getName());
        String name = null;
        if (conn != null) {
            String[] possibleNames = conn.getDeviceList();
            name = getChildNameStringHelper(possibleNames, nodes);
        }

        if (name != null) {
            conn.putDeviceParams(name, params);
            return name;
        } else {
            return generateDevString();
        }
    }

    static String getPointStringToAdd(DSNode parent, DSMap params) {
        Set<String> nodes = getDFNodeNameSet(parent, DFPointNode.class);
        TestingConnection conn = TestingConnection.getConnection(parent.getParent().getName());
        TestingDevice dev = conn != null ? conn.getDevice(parent.getName()) : null;
        String name = null;
        if (dev != null) {
            String[] possibleNames = dev.getNameSet();
            name = getChildNameStringHelper(possibleNames, nodes);
        }

        if (name != null) {
            dev.putPointParams(name, params);
            return name;
        } else {
            return generatePointString();
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

    public static String dsMapToPrettyString(DSMap map) {
        StringBuilder b = new StringBuilder();
        b.append("(");
        for (int i = 0; i < map.size(); i++) {
            if (i != 0) b.append(", ");
            String key = map.get(i).toString();
            b.append(key);
            b.append(":");
            String val = map.get(key).toString();
            b.append(val);
        }
        b.append(")");
        return b.toString();
    }

    static class SubscribeHandlerImpl extends AbstractSubscribeHandler {

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

    public static class InvokeHandlerImpl extends AbstractInvokeHandler {

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
            System.out.println("Type: " + type + " MSG: " + msg + " Detail: " + detail);
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
                int step = SUBSCRIBE_DELAY_RETRIES;

                while (parent.getInfo(pointName) == null && --step > 0) {
                    try {
                        Thread.sleep(SUBSCRIBE_DELAY_WAIT_MILIS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (parent.getInfo(pointName) != null) {
                    return FuzzTest.subscribeOrUnsubscribe(parent.getInfo(pointName));
                } else {
                    return "Subscription attempt failed: " + parent.getName() + " " + pointName;
                }
            } else {
                FuzzTest.requester.invoke(path, params, new FuzzTest.InvokeHandlerImpl());
                return "Invoking Queued:" + path + " with parameters " + params;
            }
        }
    }

}
