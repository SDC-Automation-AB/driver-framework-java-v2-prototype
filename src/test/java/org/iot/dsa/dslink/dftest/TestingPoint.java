package org.iot.dsa.dslink.dftest;

/**
 * @author James (Juris) Puchin
 * Created on 1/19/2018
 */
public class TestingPoint {
    protected String name;
    protected String value;
    protected MockParameters pointParams;

    protected TestingPoint(String name, String value) {
        this.name = name;
        this.value = value;
        this.pointParams = new MockParameters();
    }

    protected TestingPoint(String name, String value, MockParameters parameters) {
        this(name, value);
        pointParams = parameters;
    }

    String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }
}
