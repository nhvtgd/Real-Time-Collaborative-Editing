package document;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * This will containing the testing for all code associated with the Operational Transform algorithm
 * 
 * We will first test the Operation class, and its subclasses DeleteOperation, InsertOperation, and UpdateOperation
 * we will do this by creating instances of the classes, calling mutators, and then printing out a toString representation
 * and comparing it to some expected value.  Since we have not created much of the toString and mutators, we are not sure
 * what we want the final outcome to be.  We added some hypothetical tests instead.
 * 
 * We will then test the ContextDifference code.  We will do this by creating a set of contextVectors, and
 * then calling each operation and testing if the vector is the expected vector by using a toString method.
 * 
 * We will then test ContextVector, and ContextVectorTable.  We will create instances of ContextVector and table
 * and modify it using the mutators.  We will then check the toString method and compare with the expected output.
 * 
 * We will then test the HistoryBuffer.  We will create a HistoryBuffer, add operations and then test if it 
 * outputs the expected historyBuffer list of operations
 * 
 * It is a bit tricky to test the OperationEngine.  We need to generate a set of concurrent operations to be tested.
 * Then we will have an expected output of transformed operations.  We will need to create a toString method to
 * test the transformed operations.
 * 
 * We need to make sure that we get a decent code coverage. We will make sure to test various edge cases for the 
 * classes, such as null input, and every method in the class.
 * 
 * @author Hanwen Xu
 *
 */
public class OperationTest {

    /**
     * A note, since we haven't fully implemented the code yet, we are still unsure what the proper
     * output should be for most of the functions. We will update the tests as we go through the coding procedure.
     * @throws OperationEngineException
     */
    @Test
    public void OperationTest() throws OperationEngineException {
        /**
         * This will test operation functions
         */
        
        InsertOperation inOp = new InsertOperation(null);
        DeleteOperation deOp = new DeleteOperation(null);
        inOp.addToCache(1111);
        deOp.addToCache(4444);
        
        
        assertEquals(inOp.toString(), "testing");
        assertEquals(deOp.toString(), "testing");
    }
    
    @Test
    public void ContextDifferenceTest() throws OperationEngineException{
        /**
         * This will test contextDifference class
         */
        
        ContextDifference cd = new ContextDifference();
        InsertOperation inOp = new InsertOperation(null);
        DeleteOperation deOp = new DeleteOperation(null);
        cd.addRange(111, 0, 20);
        cd.addSiteSeq(184124, 3);
        assertEquals(cd.toString(), "testing");
    }
    
    @Test
    public void ContextVectorTest() throws OperationEngineException{
        /**
         * This will test the history buffer
         */
        ContextVector cv = new ContextVector(null);
        ContextVector cv2 = new ContextVector(null);
        InsertOperation inOp = new InsertOperation(null);
        DeleteOperation deOp = new DeleteOperation(null);
        cv.compare(cv2);
        assertEquals(cv.copy(), cv.copy());
        assertEquals(cv.getSites(), "something");
        assertEquals(cv.toString(), "testing");
    }
    
    @Test
    public void HistoryBufferTest() throws OperationEngineException{
        /**
         * This will test the history buffer
         */
        InsertOperation inOp = new InsertOperation(null);
        DeleteOperation deOp = new DeleteOperation(null);
        HistoryBuffer hb = new HistoryBuffer();
        hb.addLocal(inOp);
        hb.addRemote(deOp);
        
        assertEquals(hb.toString(), "delete, insert, offsets");
        
    }
    
    @Test
    public void OperationEngineTest() throws OperationEngineException{
        /**
         * This will test the operation engine
         */
        
        InsertOperation inOp = new InsertOperation(null);
        DeleteOperation deOp = new DeleteOperation(null);
        OperationEngine OE = new OperationEngine(0);
        OperationEngine OE2 = new OperationEngine(2);
        
        assertEquals(OE.toString(), "something");
        
        
        
    }
    
}
