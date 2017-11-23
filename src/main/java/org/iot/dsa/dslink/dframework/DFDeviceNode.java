package org.iot.dsa.dslink.dframework;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFDeviceNode extends DFBranchNode {

    //protected static long REFRESH_DEF = DFHelpers.DEFAULT_PING_DELAY;
    //protected static long DEFAULT_POLL_RATE = DFHelpers.DEFAULT_PING_DELAY;
    //private Map<DFLeafCarouselObject, Boolean> batches = new ConcurrentHashMap<DFLeafCarouselObject, Boolean>();
    private Map<Long, DFLeafCarouselObject> batches = new ConcurrentHashMap<Long, DFLeafCarouselObject>();
    
    //Carousel Management Methods
    abstract public boolean createConnection();

    abstract public boolean ping();

    abstract public void closeConnection();

    abstract public boolean batchPoll(Set<DFPointNode> points);

    synchronized boolean noPollBatches() {
        return batches.isEmpty();
    }

    synchronized void addPollBatch(DFLeafCarouselObject batch, Long pollRate) {
        batches.put(pollRate, batch);
    }

    synchronized void removePollBatch(DFLeafCarouselObject batch, Long pollRate) {
        if (!batches.remove(pollRate, batch)) {
            throw new RuntimeException("Error removing batch from device");
        };
    }

    synchronized DFLeafCarouselObject getPollBatch(DFPointNode point) {
            DFLeafCarouselObject batch = batches.get(point.getPollRate());
            if (batch == null) {
                batch = new DFLeafCarouselObject(point, this);
            } else {
                batch.addHomeNode(point);
            }
            return batch;
    }
}
