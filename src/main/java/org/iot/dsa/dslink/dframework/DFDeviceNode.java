package org.iot.dsa.dslink.dframework;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFDeviceNode extends DFBranchNode {

    private Map<DFLeafCarouselObject, Boolean> batches = new ConcurrentHashMap<DFLeafCarouselObject, Boolean>();


    //Carousel Management Methods
    abstract public boolean createConnection();

    abstract public boolean ping();

    abstract public void closeConnection();

    abstract public boolean batchPoll(Set<DFPointNode> points);

    synchronized boolean noPollBatches() {
        return batches.isEmpty();
    }

    synchronized void addPollBatch(DFLeafCarouselObject batch) {
        batches.put(batch, false);
    }

    synchronized void removePollBatch(DFLeafCarouselObject batch) {
        batches.remove(batch);
    }

    synchronized DFLeafCarouselObject getPollBatch() {
        if (!noPollBatches()) {
            return batches.keySet().iterator().next();
        } else {
            throw new RuntimeException("Tried to get a batch from a Device Node with no Poll Batches.");
        }
    }

    public DFDelayCalculator getBatchDelayCalculator(DFLeafCarouselObject carObject) {
        return new DFConstantDelayCalculator(this, carObject);
    }
}
