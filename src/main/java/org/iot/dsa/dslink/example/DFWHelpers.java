package org.iot.dsa.dslink.example;

public class DFWHelpers {
    static final String STATUS = "Status";
    static final String RESTART = "Restart";
    static final String STOP = "Stop";
    static final String START = "Start";
    static final String REMOVE = "Remove";
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
    
    public enum DFStatus {
        NEW("Unknown"),
        CONNECTED("Connected"),
        FAILED("Failed"),
        STOPPED(IS_STOPPED);
        
        String display;
        DFStatus(String display) {
            this.display = display;
        }
        @Override
        public String toString() {
            return display;
        }
    }
}
