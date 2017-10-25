package org.iot.dsa.dslink.example;

public class DFWHelpers {
    public enum DFWConnStrat {
        LAZY,
        ACTIVE,
        HYBRID;
    }
    
    public enum DFWRefChangeStrat {
        CONSTANT,
        LINEAR,
        EXPONENTIAL;
    }
    
    public enum DFWStat {
        NEW,
        CONNECTED,
        FAILED;
    }
}
