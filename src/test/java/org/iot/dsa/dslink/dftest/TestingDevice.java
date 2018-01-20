package org.iot.dsa.dslink.dftest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestingDevice {

    private String name;
    private MockParameters devParams;
    private boolean active = true;
    private Map<String, TestingPoint> points = new ConcurrentHashMap<>();

    TestingDevice(String name) {
        this.name = name;
        this.devParams = new MockParameters();
    }

    /**
     * Flip device state
     * @return return new state
     */
    boolean flipDev() {
        active = !active;
        return isActive();
    }

    void setDevActive(boolean act) {
        active = act;
    }

    boolean isActive() {
        return active;
    }

    boolean invalidDevParams(MockParameters params) {
        return !devParams.verifyParameters(params);
    }

    /**
     * Add point to the device
     * @param name Name of point
     * @param value Value of point
     */
    void addPoint(String name, String value) {
        points.put(name, new TestingPoint(name, value));
    }

    boolean hasPoint(String name) {
        return points.containsKey(name);
    }

    String getPointValue(String name) {
        return points.get(name).getValue();
    }

    void changePointValue(String name, String value) {
        points.get(name).setValue(value);
    }

    void removePoint(String name) {
        points.remove(name);
    }

    String getNthPointName(int n) {
        return getNameSet()[n];
    }

    String[] getNameSet() {
        return points.keySet().toArray(new String[points.size()]);
    }

    int getPointCount() {
        return points.size();
    }
}
