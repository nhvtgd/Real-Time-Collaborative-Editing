package document;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class HistoryBufferTest {

    /**
     * This will test the basic constructor, and the getCount method, and the
     * toString method.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void HistoryBufferTest1() throws OperationEngineException {
        HistoryBuffer hb1 = new HistoryBuffer();

        assertEquals(hb1.getSize(), 0);
        assertEquals(hb1.toString(), "{ops : {},size : 0}");
    }

    /**
     * This will test the getState() function We will test the empty case
     */
    @Test
    public void getStateTest1() {
        HistoryBuffer hb1 = new HistoryBuffer();
        Object[] nope = new Object[0];

        assertEquals(Arrays.toString(nope), Arrays.toString(hb1.getState()));
    }

    /**
     * This will test the getState() function We will test the normal case
     * 
     * @throws OperationEngineException
     */
    @Test
    public void getStateTest2() throws OperationEngineException {
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        HistoryBuffer hb1 = new HistoryBuffer();
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

        hb1.addLocalOperation(op);
        hb1.addLocalOperation(op1);
        assertEquals(hb1.getState().length, 2);
    }

    /**
     * This will test the addLocalOperation() function We will test the normal
     * case
     * 
     * @throws OperationEngineException
     */
    @Test
    public void addLocalOperationTest1() throws OperationEngineException {
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        HistoryBuffer hb1 = new HistoryBuffer();
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

        hb1.addLocalOperation(op);
        hb1.addLocalOperation(op1);
        hb1.addLocalOperation(op2);
        hb1.addLocalOperation(op2); // duplicate op should be ignored
        assertEquals(hb1.getState().length, 3);
    }

    /**
     * This will test the addRemoteOperation() function We will test the normal
     * operation case
     * 
     * @throws OperationEngineException
     */
    @Test
    public void addRemoteOperationTest1() throws OperationEngineException {
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        HistoryBuffer hb1 = new HistoryBuffer();
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

        hb1.addRemoteOperation(op);
        hb1.addRemoteOperation(op1);
        hb1.addRemoteOperation(op2);
        assertEquals(hb1.getState().length, 3);
    }

    /**
     * This will test the addRemoteOperation() function We will test the
     * duplicate operation case
     * 
     * @throws OperationEngineException
     */
    @Test(expected = OperationEngineException.class)
    public void addRemoteOperationTest2() throws OperationEngineException {
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        HistoryBuffer hb1 = new HistoryBuffer();
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

        hb1.addRemoteOperation(op);
        hb1.addRemoteOperation(op1);
        hb1.addRemoteOperation(op2);
        hb1.addRemoteOperation(op2);
        assertEquals(hb1.getState().length, 3);
    }

    /**
     * This will test the remoteOperation() function We will test the duplicate
     * operation case
     * 
     * @throws OperationEngineException
     */
    @Test
    public void removeOperationTest2() throws OperationEngineException {
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        HistoryBuffer hb1 = new HistoryBuffer();
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

        hb1.addRemoteOperation(op);
        hb1.addRemoteOperation(op1);
        hb1.addRemoteOperation(op2);
        hb1.removeOperation(op1);
        assertEquals(hb1.getState().length, 2);
        hb1.removeOperation(op);
        assertEquals(hb1.getState().length, 1);
        hb1.removeOperation(op2);
        assertEquals(hb1.getState().length, 0);
    }

    /**
     * This will test the setState() function We will test the normal
     * 
     * @throws OperationEngineException
     */
    @Test
    public void setStateTest1() throws OperationEngineException {
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        HistoryBuffer hb1 = new HistoryBuffer();
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

        hb1.addRemoteOperation(op);
        hb1.addRemoteOperation(op1);
        hb1.addRemoteOperation(op2);
        hb1.removeOperation(op1);
        assertEquals(hb1.getState().length, 2);
        hb1.removeOperation(op);
        assertEquals(hb1.getState().length, 1);
        hb1.removeOperation(op2);
        assertEquals(hb1.getState().length, 0);
        HistoryBuffer hb2 = new HistoryBuffer();
        assertEquals(hb1.getState().length, hb2.getState().length);
    }

}
