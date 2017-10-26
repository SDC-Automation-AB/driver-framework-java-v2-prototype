package org.iot.dsa.dslink.example;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFConnectionNode extends DFAbstractNode {

    protected static long REFRESH_DEF = 5000;
    protected static DFWHelpers.DFWConnStrat CONN_STRAT_DEF = DFWHelpers.DFWConnStrat.LAZY;
    protected static DFWHelpers.DFWRefChangeStrat REFRESH_CHANGE_STRAT_DEF = DFWHelpers.DFWRefChangeStrat.CONSTANT;

    @Override
    protected void onStable() {
        super.onStable();
        if (!getIsStopped()) {
            startConnection();
        }
    }
}
