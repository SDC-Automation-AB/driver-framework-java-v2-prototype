package org.iot.dsa.dslink.dframework;

import java.util.HashMap;
import java.util.Map;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.util.DSException;

public class DFUtil {
    
    private static Map<Class<? extends EditableNode>, EditableNode> dummyRegistry = new HashMap<Class<? extends EditableNode>, EditableNode>();
    
    public static EditableNode getDummyInstance(Class<? extends EditableNode> clazz) {
        EditableNode dummy = dummyRegistry.get(clazz);
        if (dummy == null) {
            try {
                dummy = (EditableNode) clazz.newInstance();
                dummyRegistry.put(clazz, dummy);
            } catch (Exception e) {
                DSException.throwRuntime(e);
            }
        }
        return dummy;
    }
    
    public static DSAction getAddAction(Class<? extends EditableNode> clazz) { 
        EditableNode dummyInstance = getDummyInstance(clazz);
        return dummyInstance.getAddAction();
    }

}
