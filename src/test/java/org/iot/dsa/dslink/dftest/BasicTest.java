package org.iot.dsa.dslink.dftest;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSIRequester;
import org.iot.dsa.dslink.DSLink;
import org.iot.dsa.dslink.dfexample.RootNode;
import org.iot.dsa.dslink.dframework.DFHelpers;
import org.iot.dsa.dslink.requester.AbstractInvokeHandler;
import org.iot.dsa.dslink.requester.AbstractSubscribeHandler;
import org.iot.dsa.dslink.requester.OutboundInvokeHandler;
import org.iot.dsa.dslink.requester.OutboundStream;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSStatus;
import org.iot.dsa.time.DSDateTime;
import org.junit.Test;

public class BasicTest {
    
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
    
    private static String performCommand(DSIRequester requester, RootNode root, String command) {
        
        
        return DFHelpers.getTestingString(root);
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
