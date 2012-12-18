package document;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * This class will test the functionality of the Operation code.  
 * @author Hanwen Xu
 *
 */
public class OperationTest {

    /**
     * This will test the basic constructor for the Operation
     * @throws OperationEngineException
     */
    @Test
    public void OperationTest1() throws OperationEngineException{
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);
        
        Object[] opState = { "insert", "document", "hello world", 5, cv4, 1,
                10, 0 };
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("siteId", opState[5]);
        properties.put("key", opState[1]);
        properties.put("value", opState[2]);
        properties.put("position", opState[3]);
        properties.put("contextVector", opState[4]);
        properties.put("seqId", opState[6]);
        properties.put("order", opState[7]);
        properties.put("local", false);
        Operation op = Operation.createOperationFromType("insert", properties);

        Object[] opState1 = { "insert", "document", "hello world", 2, cv4, 3,
                4, 0 };
        properties.put("siteId", opState1[5]);
        properties.put("key", opState1[1]);
        properties.put("value", opState1[2]);
        properties.put("position", opState1[3]);
        properties.put("contextVector", opState1[4]);
        properties.put("seqId", opState1[6]);
        properties.put("order", opState1[7]);
        properties.put("local", false);
        Operation op1 = Operation.createOperationFromType("insert", properties);

        Object[] opState2 = { "insert", "document", "hello world", 2, cv4, 6,
                7, 0 };
        properties.put("siteId", opState2[5]);
        properties.put("key", opState2[1]);
        properties.put("value", opState2[2]);
        properties.put("position", opState2[3]);
        properties.put("contextVector", opState2[4]);
        properties.put("seqId", opState2[6]);
        properties.put("order", opState2[7]);
        properties.put("local", false);
        Operation op2 = Operation.createOperationFromType("insert", properties);
        
        assertEquals(op.getValue(), "hello world");
        assertEquals(op1.getValue(), "hello world");
        assertEquals(op2.getValue(), "hello world");

    }
    
    /**
     * This will test the basic constructor for the Operation
     * @throws OperationEngineException
     */
    @Test
    public void OperationTest2() throws OperationEngineException{
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);
        
        Object[] opState = { "insert", "document", "hello world", 5, cv4, 1,
                10, 0 };
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("siteId", opState[5]);
        properties.put("key", opState[1]);
        properties.put("value", opState[2]);
        properties.put("position", opState[3]);
        properties.put("contextVector", opState[4]);
        properties.put("seqId", opState[6]);
        properties.put("order", opState[7]);
        properties.put("local", false);
        Operation op = Operation.createOperationFromType("insert", properties);

        Object[] opState1 = { "insert", "document", "hello world", 2, cv4, 3,
                4, 0 };
        properties.put("siteId", opState1[5]);
        properties.put("key", opState1[1]);
        properties.put("value", opState1[2]);
        properties.put("position", opState1[3]);
        properties.put("contextVector", opState1[4]);
        properties.put("seqId", opState1[6]);
        properties.put("order", opState1[7]);
        properties.put("local", false);
        Operation op1 = Operation.createOperationFromType("insert", properties);

        Object[] opState2 = { "insert", "document", "hello world", 2, cv4, 6,
                7, 0 };
        properties.put("siteId", opState2[5]);
        properties.put("key", opState2[1]);
        properties.put("value", opState2[2]);
        properties.put("position", opState2[3]);
        properties.put("contextVector", opState2[4]);
        properties.put("seqId", opState2[6]);
        properties.put("order", opState2[7]);
        properties.put("local", false);
        Operation op2 = Operation.createOperationFromType("insert", properties);
        
        assertEquals(op.getKey(), "document");
        assertEquals(op1.getKey(), "document");
        assertEquals(op2.getKey(), "document");

    }
    
    /**
     * This will test the basic constructor for the Operation
     * @throws OperationEngineException
     */
    @Test
    public void OperationTest3() throws OperationEngineException{
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);
        
        Object[] opState = { "insert", "document", "hello world", 5, cv4, 1,
                10, 0 };
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("siteId", opState[5]);
        properties.put("key", opState[1]);
        properties.put("value", opState[2]);
        properties.put("position", opState[3]);
        properties.put("contextVector", opState[4]);
        properties.put("seqId", opState[6]);
        properties.put("order", opState[7]);
        properties.put("local", false);
        Operation op = Operation.createOperationFromType("insert", properties);

        Object[] opState1 = { "insert", "document", "hello world", 2, cv4, 3,
                4, 0 };
        properties.put("siteId", opState1[5]);
        properties.put("key", opState1[1]);
        properties.put("value", opState1[2]);
        properties.put("position", opState1[3]);
        properties.put("contextVector", opState1[4]);
        properties.put("seqId", opState1[6]);
        properties.put("order", opState1[7]);
        properties.put("local", false);
        Operation op1 = Operation.createOperationFromType("insert", properties);

        Object[] opState2 = { "insert", "document", "hello world", 2, cv4, 6,
                7, 0 };
        properties.put("siteId", opState2[5]);
        properties.put("key", opState2[1]);
        properties.put("value", opState2[2]);
        properties.put("position", opState2[3]);
        properties.put("contextVector", opState2[4]);
        properties.put("seqId", opState2[6]);
        properties.put("order", opState2[7]);
        properties.put("local", false);
        Operation op2 = Operation.createOperationFromType("insert", properties);

        
        assertEquals(op.getType(), "insert");
        assertEquals(op1.getType(), "insert");
        assertEquals(op2.getType(), "insert");
        
    }
    
    /**
     * This will test the basic constructor for the Operation
     * @throws OperationEngineException
     */
    @Test
    public void OperationTest4() throws OperationEngineException{
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);
        
        Object[] opState = { "insert", "document", "hello world", 5, cv4, 1,
                10, 0 };
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("siteId", opState[5]);
        properties.put("key", opState[1]);
        properties.put("value", opState[2]);
        properties.put("position", opState[3]);
        properties.put("contextVector", opState[4]);
        properties.put("seqId", opState[6]);
        properties.put("order", opState[7]);
        properties.put("local", false);
        Operation op = Operation.createOperationFromType("insert", properties);

        Object[] opState1 = { "insert", "document", "hello world", 2, cv4, 3,
                4, 0 };
        properties.put("siteId", opState1[5]);
        properties.put("key", opState1[1]);
        properties.put("value", opState1[2]);
        properties.put("position", opState1[3]);
        properties.put("contextVector", opState1[4]);
        properties.put("seqId", opState1[6]);
        properties.put("order", opState1[7]);
        properties.put("local", false);
        Operation op1 = Operation.createOperationFromType("insert", properties);

        Object[] opState2 = { "insert", "document", "hello world", 2, cv4, 6,
                7, 0 };
        properties.put("siteId", opState2[5]);
        properties.put("key", opState2[1]);
        properties.put("value", opState2[2]);
        properties.put("position", opState2[3]);
        properties.put("contextVector", opState2[4]);
        properties.put("seqId", opState2[6]);
        properties.put("order", opState2[7]);
        properties.put("local", false);
        Operation op2 = Operation.createOperationFromType("insert", properties);
        
        assertEquals(op.getSeqId(), 10);
        assertEquals(op1.getSeqId(), 4);
        assertEquals(op2.getSeqId(), 7);
        
        
    }
    
    /**
     * This will test the basic constructor for the Operation
     * @throws OperationEngineException
     */
    @Test
    public void OperationTest5() throws OperationEngineException{
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);
        
        Object[] opState = { "insert", "document", "hello world", 5, cv4, 1,
                10, 0 };
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("siteId", opState[5]);
        properties.put("key", opState[1]);
        properties.put("value", opState[2]);
        properties.put("position", opState[3]);
        properties.put("contextVector", opState[4]);
        properties.put("seqId", opState[6]);
        properties.put("order", opState[7]);
        properties.put("local", false);
        Operation op = Operation.createOperationFromType("insert", properties);

        Object[] opState1 = { "insert", "document", "hello world", 2, cv4, 3,
                4, 0 };
        properties.put("siteId", opState1[5]);
        properties.put("key", opState1[1]);
        properties.put("value", opState1[2]);
        properties.put("position", opState1[3]);
        properties.put("contextVector", opState1[4]);
        properties.put("seqId", opState1[6]);
        properties.put("order", opState1[7]);
        properties.put("local", false);
        Operation op1 = Operation.createOperationFromType("insert", properties);

        Object[] opState2 = { "insert", "document", "hello world", 2, cv4, 6,
                7, 0 };
        properties.put("siteId", opState2[5]);
        properties.put("key", opState2[1]);
        properties.put("value", opState2[2]);
        properties.put("position", opState2[3]);
        properties.put("contextVector", opState2[4]);
        properties.put("seqId", opState2[6]);
        properties.put("order", opState2[7]);
        properties.put("local", false);
        Operation op2 = Operation.createOperationFromType("insert", properties);
        
        assertEquals(op.getPosition(), 5);
        assertEquals(op1.getPosition(), 2);
        assertEquals(op2.getPosition(), 2);
        
    }
    
    /**
     * This will test the basic constructor for the Operation
     * @throws OperationEngineException
     */
    @Test
    public void OperationTest6() throws OperationEngineException{
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);
        
        Object[] opState = { "insert", "document", "hello world", 5, cv4, 1,
                10, 0 };
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("siteId", opState[5]);
        properties.put("key", opState[1]);
        properties.put("value", opState[2]);
        properties.put("position", opState[3]);
        properties.put("contextVector", opState[4]);
        properties.put("seqId", opState[6]);
        properties.put("order", opState[7]);
        properties.put("local", false);
        Operation op = Operation.createOperationFromType("insert", properties);

        Object[] opState1 = { "insert", "document", "hello world", 2, cv4, 3,
                4, 0 };
        properties.put("siteId", opState1[5]);
        properties.put("key", opState1[1]);
        properties.put("value", opState1[2]);
        properties.put("position", opState1[3]);
        properties.put("contextVector", opState1[4]);
        properties.put("seqId", opState1[6]);
        properties.put("order", opState1[7]);
        properties.put("local", false);
        Operation op1 = Operation.createOperationFromType("insert", properties);

        Object[] opState2 = { "insert", "document", "hello world", 2, cv4, 6,
                7, 0 };
        properties.put("siteId", opState2[5]);
        properties.put("key", opState2[1]);
        properties.put("value", opState2[2]);
        properties.put("position", opState2[3]);
        properties.put("contextVector", opState2[4]);
        properties.put("seqId", opState2[6]);
        properties.put("order", opState2[7]);
        properties.put("local", false);
        Operation op2 = Operation.createOperationFromType("insert", properties);
        
        assertEquals(op.getPosition(), 5);
        assertEquals(op1.getPosition(), 2);
        assertEquals(op2.getPosition(), 2);
        
    }

}
