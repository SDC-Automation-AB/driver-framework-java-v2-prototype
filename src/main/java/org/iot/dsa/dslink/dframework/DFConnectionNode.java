package org.iot.dsa.dslink.dframework;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFConnectionNode extends DFAbstractNode {

    public static long REFRESH_DEF = 5000;
    public static DFHelpers.DFConnStrat CONN_STRAT_DEF = DFHelpers.DFConnStrat.LAZY;
    public static DFHelpers.DFRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFHelpers.DFRefChangeStrat.CONSTANT;

    @Override
    protected void onStable() {
        super.onStable();
        if (!getIsStopped()) {
            startCarObject();
        }
    }
}
