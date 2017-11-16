package org.iot.dsa.dslink.dframework;

public abstract class DFDelayCalculator {
    
    protected DFBranchNode homeNode;
    protected DFCarouselObject carObject;
    
    public DFDelayCalculator(DFBranchNode homeNode, DFCarouselObject carObject) {
        this.homeNode = homeNode;
        this.carObject = carObject;
    }
    
    public abstract long getDelay();
    
    

}
