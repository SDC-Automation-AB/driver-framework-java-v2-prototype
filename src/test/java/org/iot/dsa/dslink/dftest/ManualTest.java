package org.iot.dsa.dslink.dftest;

import java.util.Scanner;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSLink;

public class ManualTest implements Runnable {
    
    static final Object monitor = new Object();

    public static void main(String[] args) {
        TestingConnection daniel = new TestingConnection();
        TestingConnection.connections.put("Daniel", daniel);
        
        TestingDevice teapot = new TestingDevice();
        daniel.devices.put("Teapot", teapot);
        TestingDevice toaster = new TestingDevice();
        daniel.devices.put("Toaster", toaster);
        TestingDevice trebuchet = new TestingDevice();
        daniel.devices.put("Trebuchet", trebuchet);
        
        teapot.addPoint("Temperature", "178");
        teapot.addPoint("Target", "212");
        teapot.addPoint("WaterLevel", "6");
        
        toaster.addPoint("Setting", "Bagel");
        toaster.addPoint("MinutesRemaining", "0");
        toaster.addPoint("Color", "Red");
        
        trebuchet.addPoint("Loaded", "True");
        trebuchet.addPoint("LaunchAngle", "45");
        trebuchet.addPoint("LaunchDirection", "North-East");
        trebuchet.addPoint("Projectile", "Rock");
        
        DSRuntime.run(new ManualTest());
        
        DSLink.main(args);
        
        synchronized(monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
            }
        }
    }
    
    private static String getPrintOut() {
        return TestingConnection.getPrintout();  
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while(true) {
            String tok = sc.next();
            if (tok.toLowerCase().startsWith("print")) {
                System.out.println(getPrintOut());
            } else if (tok.toLowerCase().startsWith("exit")) {
                break;
            } else if (tok.toLowerCase().startsWith("startc")) {
                String c = sc.next();
                System.out.println(setConnShouldSucceed(c, true));
            } else if (tok.toLowerCase().startsWith("stopc")) {
                String c = sc.next();
                System.out.println(setConnShouldSucceed(c, false));
            } else if (tok.toLowerCase().startsWith("startd")) {
                String c = sc.next();
                String d = sc.next();
                System.out.println(setDevActive(c, d, true));
            } else if (tok.toLowerCase().startsWith("stopd")) {
                String c = sc.next();
                String d = sc.next();
                System.out.println(setDevActive(c, d, false));
            } else if (tok.toLowerCase().startsWith("set")) {
                String c = sc.next();
                String d = sc.next();
                String p = sc.next();
                String v = sc.next();
                System.out.println(setValue(c, d, p, v));
            } else if (tok.toLowerCase().startsWith("del")) {
                String c = sc.next();
                String d = sc.next();
                String p = sc.next();
                System.out.println(clearValue(c, d, p));
            }
        }
        sc.close();
        synchronized(monitor) {
            monitor.notifyAll();
        }
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
                dev.setDevActive(active);
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
                dev.addPoint(p, v);
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
                dev.removePoint(p);
                return c + ":" + d + ":" + p + " removed";
            } else {
                return c + ":" + d + " not found";
            }
        } else {
            return c + " not found";
        }
    }

}
