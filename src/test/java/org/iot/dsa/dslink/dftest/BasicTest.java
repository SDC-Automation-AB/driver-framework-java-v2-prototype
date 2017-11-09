package org.iot.dsa.dslink.dftest;

import java.util.HashMap;
import java.util.Map;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSIRequester;
import org.iot.dsa.dslink.DSLink;
import org.iot.dsa.dslink.dfexample.RootNode;
import org.iot.dsa.dslink.requester.OutboundInvokeHandler;
import org.iot.dsa.dslink.requester.OutboundListHandler;
import org.iot.dsa.dslink.requester.OutboundStream;
import org.iot.dsa.dslink.requester.OutboundSubscribeHandler;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSMap.Entry;
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
        
        //e.g.
        MirrorNode mirrorRoot = new MirrorNode("/");
        list(requester, mirrorRoot);

        
    }
    
    private void list(DSIRequester requester, MirrorNode mnode) {
        requester.list(mnode.path, mnode);
    }
    
    private void subscribe(DSIRequester requester, MirrorNode mnode) {
        requester.subscribe(mnode.path, 0, mnode);
    }
    
    private void invoke(DSIRequester requester, MirrorNode mnode, DSMap params) {
        requester.invoke(mnode.path, params, new MirrorInvokeHandler());
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
    
    public static class MirrorNode implements OutboundSubscribeHandler, OutboundListHandler{
        String path;
        Map<String, MirrorNode> children = new HashMap<String, MirrorNode>();
        Map<String, DSElement> metadata = new HashMap<String, DSElement>();
        DSElement value;
        
        
        OutboundStream subStream;
        OutboundStream listStream;
        
        MirrorNode(String path) {
            this.path = path;
        }
        
        MirrorNode addChild(String name) {
            String childPath = path + name + "/";
            MirrorNode ret = new MirrorNode(childPath);
            children.put(name, new MirrorNode(childPath));
            return ret;
        }
        
        void closeSub() {
            subStream.closeStream();
        }
        
        void closeList() {
            listStream.closeStream();
        }
        
        @Override
        public void onClose() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onError(String type, String msg, String detail) {
            assert(false);
        }

        @Override
        public void onInit(String path, OutboundStream stream) {
            this.listStream = stream;
        }

        @Override
        public void onInitialized() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onRemove(String name) {
            if (name.startsWith("@") || name.startsWith("$")) {
                metadata.remove(name);
            } else {
                children.remove(name);
            }   
        }

        @Override
        public void onUpdate(String name, DSElement value) {
            if (value.isMap()) {
                MirrorNode child = addChild(name);
                DSMap map = value.toMap();
                for (int i=0; i<map.size(); i++) {
                    Entry entry = map.getEntry(i);
                    child.onUpdate(entry.getKey(), entry.getValue());
                }
            } else if (name.startsWith("$") || name.startsWith("@")){
                metadata.put(name, value);
            }
        }

        @Override
        public void onInit(String path, int qos, OutboundStream stream) {
            this.subStream = stream;
        }

        @Override
        public void onUpdate(DSDateTime dateTime, DSElement value, DSStatus status) {
            this.value = value;
        }
        
    }
    
    private static class MirrorInvokeHandler implements OutboundInvokeHandler {

        @Override
        public void onClose() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onError(String type, String msg, String detail) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onColumns(DSList list) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onInit(String path, DSMap params, OutboundStream stream) {
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
        
    }

}
