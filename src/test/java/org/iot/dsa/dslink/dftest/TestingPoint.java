package org.iot.dsa.dslink.dftest;

/**
 * @author James (Juris) Puchin
 * Created on 1/19/2018
 */
public class TestingPoint {
    private String name;
    private String value;
    private MockParameters pointParams;

    TestingPoint(String name, String value) {
        this.name = name;
        this.value = value;
        this.pointParams = new MockParameters();
    }

    String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }
}
