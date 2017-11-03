package org.iot.dsa.dslink.dframework;

import java.util.HashSet;
import java.util.Set;

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
    private Set<DFLeafCarouselObject> batches = new HashSet<DFLeafCarouselObject>();
    public boolean noPollBatches() {
        return batches.isEmpty();
    }
    public void addPollBatch(DFLeafCarouselObject batch) {
        batches.add(batch);
    }
    public void removePollBatch(DFLeafCarouselObject batch) {
        batches.remove(batch);
    }
    public DFLeafCarouselObject getPollBatch() {
        if (!noPollBatches()) {
            return batches.iterator().next();
        } else {
            throw new RuntimeException("Tried to get a batch from a Device Node with no Poll Batches.");
        }
    }
}
