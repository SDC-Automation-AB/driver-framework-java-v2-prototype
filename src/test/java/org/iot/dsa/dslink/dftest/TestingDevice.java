package org.iot.dsa.dslink.dftest;

import org.iot.dsa.node.DSMap;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class TestingDevice {

    protected String name;
    protected MockParameters devParams;
    protected boolean active = true;
    protected Map<String, TestingPoint> points = new ConcurrentHashMap<>();

    protected TestingDevice(String name) {
        this.name = name;
        this.devParams = new MockParameters();
    }

    protected TestingDevice(String name, MockParameters pars) {
        this.name = name;
        this.devParams = pars;
    }

    /**
     * Flip device state
     *
     * @return return new state
     */
    protected boolean flipDev() {
        active = !active;
        return isActive();
    }

    protected void setDevActive(boolean act) {
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
     *
     * @param name  Name of point
     * @param value Value of point
     */
    protected void addPoint(String name, String value, Random rand) {
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

    protected void removePoint(String name) {
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

    void putPointParams(String pointName, DSMap copy) {
        points.get(pointName).pointParams.putAll(copy);
    }
}
