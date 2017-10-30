package org.iot.dsa.dslink.dftest;

import java.util.HashMap;
import java.util.Map;

public class TestingConnection {
    
    static Map<String, TestingConnection> connections = new HashMap<String, TestingConnection>();
    
    Map<String, TestingDevice> devices = new HashMap<String, TestingDevice>();
    boolean shouldSucceed = true;
    
    
    private boolean running = false;
    
    public static TestingConnection getConnection(String connectionString) throws TestingException {
        TestingConnection result = connections.get(connectionString);
        if (result == null) {
            throw new TestingException("Configuration Fault");
        } else {
            return result;
        }
    }
    
    public void connect() throws TestingException {
        if (running) {
            return;
        }
        if (shouldSucceed) {
            running = true;
        } else {
            throw new TestingException("Connection Failed");
        }
    }
    
    public void close() {
        running = false;
    }
    
    public boolean isConnected() {
        return running && shouldSucceed; 
    }
    
    public TestingDevice getDevice(String deviceString) throws TestingException {
        TestingDevice result = devices.get(deviceString);
        if (result == null) {
            throw new TestingException("No such device");
        } else {
            return result;
        }
    }
    
    public boolean pingDevice(TestingDevice device) throws TestingException {
        if (!running) {
            throw new TestingException("Connection not running");
        } else if (!shouldSucceed) {
            throw new TestingException("Request Failed");
        } else if (!devices.containsValue(device)) {
            throw new TestingException("Device not found");
        } else {
            return device.active;
        }
    }
    
    public String readPoint(TestingDevice device, String pointId) throws TestingException {
        if (!pingDevice(device)) {
            throw new TestingException("Device not responding");
        } else if (!device.points.containsKey(pointId)) {
            throw new TestingException("Point not found");
        } else {
            return device.points.get(pointId);
        }
    }
    
    
    public static class TestingException extends Exception {
        private static final long serialVersionUID = -1871011152307899388L;

        public TestingException(String message) {
            super(message);
        }
    }
}
