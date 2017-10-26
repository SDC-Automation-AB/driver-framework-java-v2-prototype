package org.iot.dsa.dslink.example;

public class DFWHelpers {
    static final String STATUS = "Status";
    static final String RESTART = "Restart";
    static final String STOP = "Stop";
    static final String START = "Start";
    static final String IS_STOPPED = "Stopped";

    public enum DFWConnStrat {
        LAZY,
        ACTIVE,
        HYBRID
    }
    
    public enum DFWRefChangeStrat {
        CONSTANT,
        LINEAR,
        EXPONENTIAL
    }
    
    public enum DFWStat {
        NEW,
        CONNECTED,
        FAILED
    }
}
