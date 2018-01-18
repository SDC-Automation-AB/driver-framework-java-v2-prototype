package org.iot.dsa.dslink.dftest;

import java.util.HashMap;
import java.util.Map;

public class TestingDevice {
    
    private boolean active = true;
    private Map<String, String> points = new HashMap<String, String>();

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

    /**
     * Add point to the device
     * @param name Name of point
     * @param value Value of point
     */
    void addPoint(String name, String value) {
        points.put(name, value);
    }

    boolean hasPoint(String name) {
        return points.containsKey(name);
    }

    String getPointValue(String name) {
        return points.get(name);
    }

    void changePointValue(String name, String value) {
        points.put(name, value);
    }

    void removePoint(String name) {
        points.remove(name);
    }

    String getNthPointName(int n) {
        return getNameSet()[n];
    }

    String[] getNameSet() {
        return (String[]) points.keySet().toArray();
    }

    int getPointCount() {
        return points.size();
    }
}
