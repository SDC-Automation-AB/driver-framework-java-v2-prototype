package org.iot.dsa.dslink.dframework;

public class DFBranchDelayCalculator extends DFDelayCalculator {
    
    protected DFBranchNode homeNode;
    protected DFBranchCarouselObject carObject;
    private long reconnectRate;
    
    public DFBranchDelayCalculator(DFBranchNode homeNode, DFBranchCarouselObject carObject) {
        this.homeNode = homeNode;
        this.carObject = carObject;
        this.reconnectRate = homeNode.getPingRate();
    }

    @Override
    public long getDelay() {
        if (carObject.isConnected()) {
            reconnectRate = homeNode.getPingRate();
            return homeNode.getPingRate();
        } else {
            reconnectRate *= homeNode.getReconnectDelayMultiplier();
            return reconnectRate;
        }
    }

}
