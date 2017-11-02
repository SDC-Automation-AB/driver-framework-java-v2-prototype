package org.iot.dsa.dslink.dframework;

/**
 * @author James (Juris) Puchin
 * Created on 11/1/2017
 */
public abstract class DFBranchNode extends DFAbstractNode {

    //Carousel Management Methods
    abstract public boolean createConnection();
    abstract public boolean ping();
    abstract public void closeConnection();

    public void stopCarObject() {
        if (carObject != null) {
            carObject.close();
            carObject = null;
        }
    }

    public void startCarObject() {
        if (carObject == null) {
            carObject = new DFBranchCarouselObject(this);
        }
    }
}
