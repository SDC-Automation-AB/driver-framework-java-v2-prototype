package org.iot.dsa.dslink.dframework;

public class DFLeafDelayCalculator extends DFDelayCalculator {
    
    
    protected DFDeviceNode homeNode;
    protected DFLeafCarouselObject carObject;
    
    public DFLeafDelayCalculator(DFDeviceNode homeNode, DFLeafCarouselObject carObject) {
        this.homeNode = homeNode;
        this.carObject = carObject;
    }

    @Override
    public long getDelay() {
        return carObject.getAHomeNode().getPollRate();
    }

}
