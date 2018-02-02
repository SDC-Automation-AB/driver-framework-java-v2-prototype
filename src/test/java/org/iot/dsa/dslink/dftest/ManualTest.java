package org.iot.dsa.dslink.dftest;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSLink;

import java.util.Random;
import java.util.Scanner;


public class ManualTest implements Runnable {

    static final Object monitor = new Object();
    static Random nullRand = null;

    public static void main(String[] args) {

        TestingConnection daniel = new TestingConnection().addNewConnection("Daniel", nullRand);

        TestingDevice teapot = daniel.addNewDevice("Teapot", nullRand);
        TestingDevice toaster = daniel.addNewDevice("Toaster", nullRand);
        TestingDevice trebuchet = daniel.addNewDevice("Trebuchet", nullRand);

        teapot.addPoint("Temperature", "178", nullRand);
        teapot.addPoint("Target", "212", nullRand);
        teapot.addPoint("WaterLevel", "6", nullRand);

        toaster.addPoint("Setting", "Bagel", nullRand);
        toaster.addPoint("MinutesRemaining", "0", nullRand);
        toaster.addPoint("Color", "Red", nullRand);

        trebuchet.addPoint("Loaded", "True", nullRand);
        trebuchet.addPoint("LaunchAngle", "45", nullRand);
        trebuchet.addPoint("LaunchDirection", "North-East", nullRand);
        trebuchet.addPoint("Projectile", "Rock", nullRand);

        DSRuntime.run(new ManualTest());

        DSLink.main(args);

        synchronized (monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    private static String getPrintOut() {
        return TestingConnection.getPrintout(false);
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
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
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    private static String setConnShouldSucceed(String c, boolean shouldSucceed) {
        TestingConnection conn = TestingConnection.getConnection(c);
        if (conn != null) {
            conn.setPowerState(shouldSucceed);
            return c + " - shouldSucceed set to " + shouldSucceed;
        } else {
            return c + " not found";
        }
    }

    private static String setDevActive(String c, String d, boolean active) {
        TestingConnection conn = TestingConnection.getConnection(c);
        if (conn != null) {
            TestingDevice dev = conn.getDevice(d);
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
        TestingConnection conn = TestingConnection.getConnection(c);
        if (conn != null) {
            TestingDevice dev = conn.getDevice(d);
            if (dev != null) {
                dev.addPoint(p, v, nullRand);
                return c + ":" + d + ":" + p + " set to " + v;
            } else {
                return c + ":" + d + " not found";
            }
        } else {
            return c + " not found";
        }
    }

    private static String clearValue(String c, String d, String p) {
        TestingConnection conn = TestingConnection.getConnection(c);
        if (conn != null) {
            TestingDevice dev = conn.getDevice(d);
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
