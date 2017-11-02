package org.iot.dsa.dslink.dframework;

import java.util.Map;
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
    public Set<DFLeafCarouselObject>batches;
}
