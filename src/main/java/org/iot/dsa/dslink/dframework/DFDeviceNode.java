package org.iot.dsa.dslink.dframework;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFDeviceNode extends DFBranchNode {

    protected static long REFRESH_DEF = 5000;
    protected static DFHelpers.DFConnStrat CONN_STRAT_DEF = DFHelpers.DFConnStrat.LAZY;
    protected static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.CONSTANT;


    //Carousel Management Methods
    abstract public boolean createConnection();
    abstract public boolean ping();
    abstract public void closeConnection();

    abstract public boolean batchPoll(Set<DFPointNode> points);
    private Map<DFLeafCarouselObject, Boolean> batches = new ConcurrentHashMap<DFLeafCarouselObject, Boolean>();
    public boolean noPollBatches() {
        synchronized (this) {
            return batches.isEmpty();
        }
    }
    public void addPollBatch(DFLeafCarouselObject batch) {
        synchronized (this) { batches.put(batch, false); }
    }
    public void removePollBatch(DFLeafCarouselObject batch) {
        synchronized (this) { batches.remove(batch); }
    }
    public DFLeafCarouselObject getPollBatch() {
        synchronized (this) {
            if (!noPollBatches()) {
                return batches.keySet().iterator().next();
            } else {
                throw new RuntimeException("Tried to get a batch from a Device Node with no Poll Batches.");
            }
        }
    }
}
