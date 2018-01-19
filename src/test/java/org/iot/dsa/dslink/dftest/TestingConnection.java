package org.iot.dsa.dslink.dftest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class TestingConnection {
    
    private static Map<String, TestingConnection> connections = new HashMap<String, TestingConnection>();
    private Map<String, TestingDevice> devices = new HashMap<String, TestingDevice>();
    private boolean pluggedIn = true;
    private boolean running = false;

    ///////////////////////////////////////////////////////////////////////////
    // Connection Controls
    ///////////////////////////////////////////////////////////////////////////

    public static TestingConnection addNewConnection(String name) {
        TestingConnection conn = new TestingConnection();
        connections.put(name, conn);
        return conn;
    }

    /**
     * @param name Name of the connection object
     * @return Connection object, null if not found,
     */
    public static TestingConnection getConnection(String name) {
        return connections.get(name);
    }

    public static String[] getConnectionList() {
        return (String[]) connections.keySet().toArray();
    }

    public static String getNthConnectionName(int n) {
        return getConnectionList()[n];
    }

    public static int getConnectionCount() {
        return connections.size();
    }

    /**
     * Flip connection state
     * @return return new state
     */
    public boolean flipPowerSwitch() {
        pluggedIn = !pluggedIn;
        return pluggedIn;
    }

    public void setPowerState(boolean newState) {
        pluggedIn = newState;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Device Controls
    ///////////////////////////////////////////////////////////////////////////

    TestingDevice addNewDevice(String d) {
        TestingDevice dev = new TestingDevice();
        devices.put(d, dev);
        return dev;
    }

    /**
     * @param name Name of the device object
     * @return Device object, null if not found,
     */
    public TestingDevice getDevice(String name) {
        return devices.get(name);
    }

    public String getNthDeviceName(int n) {
        return getDeviceList()[n];
    }

    public String[] getDeviceList() {
        return (String[]) devices.keySet().toArray();
    }

    public int getDeviceCount() {
        return devices.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Simulation Functions
    ///////////////////////////////////////////////////////////////////////////

    //Connection level

    /**
     * Looks for a connection, throws exception if not found
     * @param connectionString name of the connection
     * @return The connection object
     * @throws TestingException when device is not found
     */
    public static TestingConnection findConnection(String connectionString) throws TestingException {
        TestingConnection result = connections.get(connectionString);
        if (result == null) {
            throw new TestingException("Connection not found");
        } else {
            return result;
        }
    }

    public void connect() throws TestingException {
        if (running) {
            return;
        }
        if (pluggedIn) {
            running = true;
        } else {
            throw new TestingException("Connection Failed");
        }
    }
    
    public void close() {
        running = false;
    }
    
    public boolean isConnected() {
        if (!pluggedIn) {
            close();
        }
        return running; 
    }

    //Device Level

    /**
     * Looks for a device, throws exception if not found
     * @param deviceString name of the device
     * @return The device object
     * @throws TestingException when device is not found
     */
    public TestingDevice findDevice(String deviceString) throws TestingException {
        TestingDevice result = devices.get(deviceString);
        if (result == null) {
            throw new TestingException("Device not found");
        } else {
            return result;
        }
    }

    public boolean pingDevice(TestingDevice device) throws TestingException {
        if (!running) {
            throw new TestingException("Connection not running");
        } else if (!pluggedIn) {
            throw new TestingException("Request Failed");
        } else if (!devices.containsValue(device)) {
            throw new TestingException("Device not found");
        } else {
            return device.isActive();
        }
    }
    
    public Map<String, String> batchRead(TestingDevice device, Set<String> ids) throws TestingException {
        Map<String, String> results = new HashMap<String, String>();
        for (String id: ids) {
            results.put(id, readPoint(device, id));
        }
        return results;
    }

    public String readPoint(TestingDevice device, String pointId) throws TestingException {
        if (!pingDevice(device)) {
            throw new TestingException("Device not responding");
        } else if (!device.hasPoint(pointId)) {
            throw new TestingException("Point not found");
        } else {
            return device.getPointValue(pointId);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc Helpers
    ///////////////////////////////////////////////////////////////////////////

    public static String getPrintout() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, TestingConnection> entry: connections.entrySet()) {
            TestingConnection c = entry.getValue();
            sb.append(entry.getKey());
            sb.append(" : ");
            sb.append(c.pluggedIn);
            sb.append('\n');
            for (Entry<String, TestingDevice> dentry: c.devices.entrySet()) {
                TestingDevice d = dentry.getValue();
                sb.append('\t');
                sb.append(dentry.getKey());
                sb.append(" : ");
                sb.append(d.isActive());
                sb.append('\n');
                for (String pname: d.getNameSet()) {
                    sb.append('\t');
                    sb.append('\t');
                    sb.append(pname);
                    sb.append(" : ");
                    sb.append(d.getPointValue(pname));
                    sb.append('\n');
                }
            }
        }
        return sb.toString();
    }

    public static class TestingException extends Exception {
        private static final long serialVersionUID = -1871011152307899388L;

        public TestingException(String message) {
            super(message);
        }
    }
}
