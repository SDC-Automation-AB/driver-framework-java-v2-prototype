package org.iot.dsa.dslink.dframework;

public class DFConstantDelayCalculator extends DFDelayCalculator {
    
    
    public DFConstantDelayCalculator(DFBranchNode homeNode, DFCarouselObject carObject) {
        super(homeNode, carObject);
    }

    @Override
    public long getDelay() {
        return homeNode.getPingRate();
    }

}
