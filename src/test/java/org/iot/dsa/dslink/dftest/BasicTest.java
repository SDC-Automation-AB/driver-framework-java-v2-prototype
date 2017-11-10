package org.iot.dsa.dslink.dftest;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSIRequester;
import org.iot.dsa.dslink.DSLink;
import org.iot.dsa.dslink.dfexample.RootNode;
import org.iot.dsa.dslink.dframework.DFHelpers;
import org.iot.dsa.dslink.requester.AbstractInvokeHandler;
import org.iot.dsa.dslink.requester.AbstractSubscribeHandler;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSStatus;
import org.iot.dsa.time.DSDateTime;
import org.junit.Test;

public class BasicTest {

    private static Set<String> unique_names = new HashSet<String>();
    
    @Test
    public void teeeeeessst() {
        preInit();
        
        RootNode root = new RootNode();
        DSLink link = new TestLink(root);
        DSRuntime.run(link);
        
        DSIRequester requester = link.getConnection().getRequester();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            assert(false);
        }
        
        
    }
    
    private static String doAThing(DSIRequester requester, RootNode root, Random random) {
        String thingDone;
        if (random.nextInt(2) < 1) {
            thingDone = createOrModifyDevice(random);
        } else {
            thingDone = subscribeOrDoAnAction(requester, root, random);
        }
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            assert(false);
        }
        
        return thingDone + "\n" + DFHelpers.getTestingString(root);
    }
    
    private String createOrModifyDevice(Random random) {
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
    
    private static String subscribeOrDoAnAction(DSIRequester requester, RootNode root, Random random) {
        
        return null;
    }
    
    private boolean notUnique(String name) {
        if (unique_names.contains(name)) {
            return true;
        } else {
            unique_names.add(name);
            return false;
        }
    }

    private String pickAName(String[] mods, String[] names, Random rand) {
        String str;
        do {
            String one = mods[rand.nextInt(mods.length)];
            String two = names[rand.nextInt(names.length)];
            str = one + two;
        } while (notUnique(str));
        return str;
    }

    private String generateConnString(Random random) {
        return pickAName(DFHelpers.colors, DFHelpers.places, random);
    }
    
    private String generateDevString(Random random, TestingConnection conn) {
        return pickAName(DFHelpers.colors, DFHelpers.animals, random);
    }
    
    private String generatePointString(Random random, TestingDevice dev) {
        return pickAName(DFHelpers.colors, DFHelpers.parts, random);
    }
    
    private String generatePointValue(Random random) {
        return DFHelpers.adjectives[random.nextInt(DFHelpers.adjectives.length)];
    }
    
    
    private static void invoke(DSIRequester requester, String action, DSMap params) {
        requester.invoke("/" + action, params, new InvokeHandlerImpl());
    }
    
    private static void invoke(DSIRequester requester, String c, String action, DSMap params) {
        requester.invoke("/" + c + "/" + action, params, new InvokeHandlerImpl());
    }
    
    private static void invoke(DSIRequester requester, String c, String d, String action, DSMap params) {
        requester.invoke("/" + c + "/" + d + "/" + action, params, new InvokeHandlerImpl());
    }
    
    private static void invoke(DSIRequester requester, String c, String d, String p, String action, DSMap params) {
        requester.invoke("/" + c + "/" + d + "/" + p + "/" + action, params, new InvokeHandlerImpl());
    }
    
    private static SubscribeHandlerImpl subscribe(DSIRequester requester, String c, String d, String p) {
        SubscribeHandlerImpl handle = new SubscribeHandlerImpl();
        requester.subscribe("/" + c + "/" + d + "/" + p, 0, handle);
        return handle;
    }
    
    
    
    private static void preInit() {
        TestingConnection daniel = addConn("Daniel");
        
        TestingDevice teapot = addDev(daniel, "Teapot");
        TestingDevice toaster = addDev(daniel, "Toaster");
        TestingDevice trebuchet = addDev(daniel, "Trebuchet");
        
        teapot.points.put("Temperature", "178");
        teapot.points.put("Target", "212");
        teapot.points.put("WaterLevel", "6");
        
        toaster.points.put("Setting", "Bagel");
        toaster.points.put("MinutesRemaining", "0");
        toaster.points.put("Color", "Red");
        
        trebuchet.points.put("Loaded", "True");
        trebuchet.points.put("LaunchAngle", "45");
        trebuchet.points.put("LaunchDirection", "North-East");
        trebuchet.points.put("Projectile", "Rock");
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
