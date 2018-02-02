package org.iot.dsa.dslink.dftest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class TestingConnection {

    private static Map<String, TestingConnection> connections = new HashMap<String, TestingConnection>();
    private Map<String, TestingDevice> devices = new HashMap<String, TestingDevice>();
    protected String name;
    protected MockParameters connParams;
    protected boolean pluggedIn = true;
    protected boolean running = false;

    ///////////////////////////////////////////////////////////////////////////
    // Connection Controls
    ///////////////////////////////////////////////////////////////////////////

    public TestingConnection addNewConnection(String name, Random rand) {
        TestingConnection conn = new TestingConnection(name);
        connections.put(name, conn);
        return conn;
    }

    protected void addConnection(String name, TestingConnection conn) {
        connections.put(name, conn);
    }

    /**
     * Blank constructor to be used for adding initial connections only.
     */
    public TestingConnection() {
        this.name = null;
        this.connParams = null;
    }

    protected TestingConnection(String name) {
        this.name = name;
        this.connParams = new MockParameters();
    }

    protected TestingConnection(String name, MockParameters pars) {
        this.name = name;
        this.connParams = pars;
    }

    /**
     * @param name Name of the connection object
     * @return Connection object, null if not found,
     */
    public static TestingConnection getConnection(String name) {
        return connections.get(name);
    }

    public static String[] getConnectionList() {
        return connections.keySet().toArray(new String[connections.size()]);
    }

    public static String getNthConnectionName(int n) {
        return getConnectionList()[n];
    }

    public static int getConnectionCount() {
        return connections.size();
    }

    /**
     * Flip connection state
     *
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

    protected TestingDevice addNewDevice(String name, Random rand) {
        TestingDevice dev = new TestingDevice(name);
        devices.put(name, dev);
        return dev;
    }

    protected void addDevice(String name, TestingDevice dev) {
        devices.put(name, dev);
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
        return devices.keySet().toArray(new String[devices.size()]);
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
     *
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

    public void connect(MockParameters connParams) throws TestingException {
        if (invalidConnParams(connParams)) {
            throw new TestingException("Invalid connection params");
        }

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

    public boolean isConnected(MockParameters params) {
        if (!pluggedIn) {
            close();
        }
        if (invalidConnParams(params)) {
            return false;
        }
        return running;
    }

    private boolean invalidConnParams(MockParameters params) {
        return !connParams.verifyParameters(params);
    }

    //Device Level

    /**
     * Looks for a device, throws exception if not found
     *
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

    public boolean pingDevice(TestingDevice device, MockParameters params) throws TestingException {
        if (!running) {
            throw new TestingException("Connection not running");
        } else if (!pluggedIn) {
            throw new TestingException("Request Failed");
        } else if (!devices.containsValue(device)) {
            throw new TestingException("Device not found");
        } else if (device.invalidDevParams(params)) {
            throw new TestingException("Invalid Device params");
        } else {
            return device.isActive();
        }
    }

    public Map<String, String> batchRead(TestingDevice device, MockParameters devParams, Set<String> ids) throws TestingException {
        Map<String, String> results = new HashMap<String, String>();
        for (String id : ids) {
            results.put(id, readPoint(device, devParams, id));
        }
        return results;
    }

    public String readPoint(TestingDevice device, MockParameters devParams, String pointId) throws TestingException {
        if (!pingDevice(device, devParams)) {
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

    public static String getPrintout(boolean verbose) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, TestingConnection> entry : connections.entrySet()) {
            TestingConnection c = entry.getValue();
            sb.append(entry.getKey());
            sb.append(" : ");
            sb.append(c.pluggedIn);
            if (verbose) {
                sb.append(" ");
                sb.append(c.connParams.getParamMap());
            }
            sb.append('\n');
            for (Entry<String, TestingDevice> dentry : c.devices.entrySet()) {
                TestingDevice d = dentry.getValue();
                sb.append('\t');
                sb.append(dentry.getKey());
                sb.append(" : ");
                sb.append(d.isActive());
                if (verbose) {
                    sb.append(" ");
                    sb.append(d.devParams.getParamMap());
                }
                sb.append('\n');
                for (String pname : d.getNameSet()) {
                    sb.append('\t');
                    sb.append('\t');
                    sb.append(pname);
                    sb.append(" : ");
                    sb.append(d.getPointValue(pname));
                    if (verbose) {
                        TestingPoint p = d.points.get(pname);
                        sb.append(" ");
                        sb.append(p.pointParams.getParamMap());
                    }
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
