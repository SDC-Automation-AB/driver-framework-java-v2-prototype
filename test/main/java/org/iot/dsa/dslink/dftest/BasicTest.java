package org.iot.dsa.dslink.dftest;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSIRequester;
import org.iot.dsa.dslink.DSLink;
import org.iot.dsa.dslink.dfexample.RootNode;
import org.junit.Test;

public class BasicTest {
    
    @Test
    public void teeeeeessst() {
        preInit();
        
        RootNode root = new RootNode();
        DSLink link = new TestLink(root);
        DSRuntime.run(link);
        
        DSIRequester requester = link.getConnection().getRequester();
        
        
    }
    
    private void preInit() {
        TestingConnection daniel = new TestingConnection();
        TestingConnection.connections.put("Daniel", daniel);
        
        TestingDevice teapot = new TestingDevice();
        daniel.devices.put("Teapot", teapot);
        TestingDevice toaster = new TestingDevice();
        daniel.devices.put("Toaster", toaster);
        TestingDevice trebuchet = new TestingDevice();
        daniel.devices.put("Trebuchet", trebuchet);
        
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
    
    private static String setConnShouldSucceed(String c, boolean shouldSucceed) {
        TestingConnection conn = TestingConnection.connections.get(c);
        if (conn != null) {
            conn.shouldSucceed = shouldSucceed;
            return c + " - shouldSucceed set to " + shouldSucceed;
        } else {
            return c + " not found";
        }
    }
    
    private static String setDevActive(String c, String d, boolean active) {
        TestingConnection conn = TestingConnection.connections.get(c);
        if (conn != null) {
            TestingDevice dev = conn.devices.get(d);
            if (dev != null) {
                dev.active = active;
                return c + ":" + d + " - active set to " + active;
            } else {
                return c + ":" + d + " not found";
            }
        } else {
            return c + " not found";
        }
    }
    
    private static String setValue(String c, String d, String p, String v) {
        TestingConnection conn = TestingConnection.connections.get(c);
        if (conn != null) {
            TestingDevice dev = conn.devices.get(d);
            if (dev != null) {
                dev.points.put(p, v);
                return c + ":" + d + ":" + p + " set to " + v;
            } else {
                return c + ":" + d + " not found";
            }
        } else {
            return c + " not found";
        }
    }
    
    private static String clearValue(String c, String d, String p) {
        TestingConnection conn = TestingConnection.connections.get(c);
        if (conn != null) {
            TestingDevice dev = conn.devices.get(d);
            if (dev != null) {
                dev.points.remove(p);
                return c + ":" + d + ":" + p + " removed";
            } else {
                return c + ":" + d + " not found";
            }
        } else {
            return c + " not found";
        }
    }

}
