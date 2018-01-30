package org.iot.dsa.dslink.dframework;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class DFUtilTests {
    
    private static class TestEditableNode extends EditableNode {
        public TestEditableNode() {}
        @Override
        public List<ParameterDefinition> getParameterDefinitions() {
            return null;
        } 
    }
    
    private static class ParentEditableNode extends EditableNode {
        public ParentEditableNode() {}
        @Override
        public List<ParameterDefinition> getParameterDefinitions() {
            return null;
        } 
    }
    
    private static class ChildEditableNode extends ParentEditableNode {
        public ChildEditableNode() {}
    }
    
    @Test
    public void oneDummyInstanceCorrectType() {
        EditableNode en1 = DFUtil.getDummyInstance(TestEditableNode.class);
        assertTrue(en1 instanceof TestEditableNode);
    }
    
    @Test
    public void oneDummyInstanceForOneClass() {
        EditableNode en1 = DFUtil.getDummyInstance(TestEditableNode.class);
        EditableNode en2 = DFUtil.getDummyInstance(TestEditableNode.class);
        assertTrue(en1 == en2);
    }
    
    @Test
    public void multipleDummyInstancesCorrectType() {
        EditableNode cen1 = DFUtil.getDummyInstance(ChildEditableNode.class);
        EditableNode ten1 = DFUtil.getDummyInstance(TestEditableNode.class);
        EditableNode pen1 = DFUtil.getDummyInstance(ParentEditableNode.class);
        
        assertTrue(ten1 instanceof TestEditableNode);
        assertTrue(pen1 instanceof ParentEditableNode);
        assertTrue(cen1 instanceof ChildEditableNode);
        assertTrue(pen1 != cen1);
    }
    
    @Test
    public void oneDummyInstancePerClassForMultipleClasses() {
        EditableNode ten1 = DFUtil.getDummyInstance(TestEditableNode.class);
        EditableNode pen1 = DFUtil.getDummyInstance(ParentEditableNode.class);
        EditableNode cen1 = DFUtil.getDummyInstance(ChildEditableNode.class);
        EditableNode cen2 = DFUtil.getDummyInstance(ChildEditableNode.class);
        EditableNode ten2 = DFUtil.getDummyInstance(TestEditableNode.class);
        EditableNode pen2 = DFUtil.getDummyInstance(ParentEditableNode.class);
        
        
        assertTrue(ten1 == ten2);
        assertTrue(pen1 == pen2);
        assertTrue(cen1 == cen2);
        assertTrue(pen1 != cen1);
    }

}
