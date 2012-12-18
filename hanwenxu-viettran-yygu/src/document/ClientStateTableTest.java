package document;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * This is the test suite for the Table We will be
 * 
 * @author Hanwen Xu
 * 
 */
public class ClientStateTableTest {

    /**
     * This will be used to test the constructor for the ClientStateTable. We
     * will also be testing the toString method just by comparing with expected
     * strings
     * 
     * @throws OperationEngineException
     */
    @Test
    public void ClientStateTableTest1() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();
        map1.put("count", 3);

        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        ClientStateTable cvt1 = new ClientStateTable(cv1, 1);
        ClientStateTable cvt2 = new ClientStateTable(cv2, 3);
        ClientStateTable cvt3 = new ClientStateTable(cv3, 5);
        ClientStateTable cvt4 = new ClientStateTable(cv4, 0);

        String cvt1exp = "[[0, 0], [0, 0, 0]]";
        String cvt2exp = "[[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0]]";
        String cvt3exp = "[[0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [1, 2, 3, 4, 5]]";
        String cvt4exp = "[[1, 0, 1, 3, 0]]";

        assertEquals(cvt1.toString(), cvt1exp);
        assertEquals(cvt2.toString(), cvt2exp);
        assertEquals(cvt3.toString(), cvt3exp);
        assertEquals(cvt4.toString(), cvt4exp);

    }

    /**
     * This test will test the getMatchingClients method. We will test various
     * forms of possible ClientStates and array size
     */
    @Test
    public void getMatchingClientsTest1() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();
        map1.put("count", 3);

        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        ClientStateTable cvt1 = new ClientStateTable(cv1, 1);
        ClientStateTable cvt2 = new ClientStateTable(cv2, 3);
        ClientStateTable cvt3 = new ClientStateTable(cv3, 5);
        ClientStateTable cvt4 = new ClientStateTable(cv4, 0);

        int[] match1 = cvt1.getMatchingClients(cv1, -1);
        int[] match2 = cvt2.getMatchingClients(cv2, 1);
        int[] match3 = cvt3.getMatchingClients(cv3, -1);
        int[] match4 = cvt4.getMatchingClients(cv4, -1);

        int[] m1expected = { 0, 1 };
        int[] m2expected = { 0, 2, 3 };
        int[] m3expected = { 5 };
        int[] m4expected = { 0 };

        assertArrayEquals(match1, m1expected);
        assertArrayEquals(match2, m2expected);
        assertArrayEquals(match3, m3expected);
        assertArrayEquals(match4, m4expected);
    }

    /**
     * This test will examine the getState method We will be testing normal and
     * empty ClientStateTables.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void getStateTest1() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();
        map1.put("count", 3);

        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        ClientStateTable cvt1 = new ClientStateTable(cv1, 1);
        ClientStateTable cvt2 = new ClientStateTable(cv2, 3);
        ClientStateTable cvt3 = new ClientStateTable(cv3, 5);
        ClientStateTable cvt4 = new ClientStateTable(cv4, 0);

        int[][] state1 = cvt1.getState();
        int[][] state2 = cvt2.getState();
        int[][] state3 = cvt3.getState();
        int[][] state4 = cvt4.getState();

        assertEquals(state1.length, 2);
        assertEquals(state2.length, 4);
        assertEquals(state3.length, 6);
        assertEquals(state4.length, 1);
    }

    /**
     * This test will examine the ability to run setState. We will test with all
     * the tables we have used above, with normal and empty set.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void setStateTest1() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();
        map1.put("count", 3);

        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        ClientStateTable cvt1 = new ClientStateTable(cv1, 1);
        ClientStateTable cvt2 = new ClientStateTable(cv2, 3);
        ClientStateTable cvt3 = new ClientStateTable(cv3, 5);
        ClientStateTable cvt4 = new ClientStateTable(cv4, 0);

        int[][] state1 = cvt1.getState();
        int[][] state2 = cvt2.getState();
        int[][] state3 = cvt3.getState();
        int[][] state4 = cvt4.getState();

        cvt1.setState(state2);
        int[][] newState1 = cvt1.getState();
        assertEquals(newState1.length, 4);

        cvt2.setState(state1);
        int[][] newState2 = cvt2.getState();
        assertEquals(newState2.length, 2);

        cvt3.setState(state4);
        int[][] newState3 = cvt3.getState();
        assertEquals(newState3.length, 1);

        cvt4.setState(state3);
        int[][] newState4 = cvt4.getState();
        assertEquals(newState4.length, 6);

    }

    /**
     * This test will test the growTo function. We will be using the same
     * ClientStateTables as above, with normal and empty type parameters.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void growToTest1() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();
        map1.put("count", 3);

        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        ClientStateTable cvt1 = new ClientStateTable(cv1, 1);
        ClientStateTable cvt2 = new ClientStateTable(cv2, 3);
        ClientStateTable cvt3 = new ClientStateTable(cv3, 5);
        ClientStateTable cvt4 = new ClientStateTable(cv4, 0);

        cvt1.growTo(20);
        cvt2.growTo(40);
        cvt3.growTo(50);
        cvt4.growTo(200);

        assertEquals(cvt1.getState().length, 20);
        assertEquals(cvt2.getState().length, 40);
        assertEquals(cvt3.getState().length, 50);
        assertEquals(cvt4.getState().length, 200);
    }

    /**
     * This test will test the getClientState function. We will be using the
     * same ClientStateTables as above, with normal and empty type parameters.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void getClientStateTest1() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();
        map1.put("count", 3);

        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        ClientStateTable cvt1 = new ClientStateTable(cv1, 1);
        ClientStateTable cvt2 = new ClientStateTable(cv2, 3);
        ClientStateTable cvt3 = new ClientStateTable(cv3, 5);
        ClientStateTable cvt4 = new ClientStateTable(cv4, 0);

        ClientState cvt1get1 = cvt1.getClientState(1);
        ClientState cvt2get3 = cvt2.getClientState(3);
        ClientState cvt3get5 = cvt3.getClientState(5);
        ClientState cvt4get0 = cvt4.getClientState(0);

        int[] expected1 = { 0, 0, 0 };
        int[] expected2 = { 0, 0, 0 };

        assertArrayEquals(cvt1get1.getState(), expected1);
        assertArrayEquals(cvt2get3.getState(), expected2);
        assertArrayEquals(cvt3get5.getState(), sites);
        assertArrayEquals(cvt4get0.getState(), state);
    }

    /**
     * This test will test the setClientState function. We will be using the
     * same ClientStateTables as above, with normal and empty type parameters.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void setClientStateTest1() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();
        map1.put("count", 3);

        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        ClientStateTable cvt1 = new ClientStateTable(cv1, 1);
        ClientStateTable cvt2 = new ClientStateTable(cv2, 3);
        ClientStateTable cvt3 = new ClientStateTable(cv3, 5);
        ClientStateTable cvt4 = new ClientStateTable(cv4, 0);

        cvt1.setClientState(4, cv2);
        cvt1.setClientState(-1, cv2);

        cvt2.setClientState(7, cv3);
        cvt2.setClientState(-25, cv2);

        cvt3.setClientState(14, cv4);
        cvt3.setClientState(-25, cv2);

        cvt4.setClientState(14, cv3);
        cvt4.setClientState(-15, cv2);

        assertEquals(cvt1.getState().length, 5);
        assertEquals(cvt2.getState().length, 8);
        assertEquals(cvt3.getState().length, 15);
        assertEquals(cvt4.getState().length, 15);
    }

    /**
     * This test will test the operationUpdate function. We will be using the
     * same ClientStateTables as above, with normal and empty type parameters.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void operationUpdateTest1() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();
        map1.put("count", 3);

        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        ClientStateTable cvt1 = new ClientStateTable(cv1, 1);
        ClientStateTable cvt2 = new ClientStateTable(cv2, 3);
        ClientStateTable cvt3 = new ClientStateTable(cv3, 5);
        ClientStateTable cvt4 = new ClientStateTable(cv4, 0);

        Object[] opState = { "insert", "document", "hello world", 5,
                cv4, 1, 10, 0 };
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
        
        cvt1.operationUpdate(op);
        cvt2.operationUpdate(op);
        cvt3.operationUpdate(op);
        cvt4.operationUpdate(op);
        
        assertEquals(cvt1.getState().length, 2);
        assertEquals(cvt2.getState().length, 4);
        assertEquals(cvt3.getState().length, 6);
        assertEquals(cvt4.getState().length, 2);
    }

    /**
     * This test will test the getMinimumClientState function. We will be
     * using the same ClientStateTables as above, with normal and empty type
     * parameters.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void getMinimumClientStateTest1() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();
        map1.put("count", 3);

        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);

        ClientState cv4 = new ClientState(map4);

        ClientStateTable cvt1 = new ClientStateTable(cv1, 1);
        ClientStateTable cvt2 = new ClientStateTable(cv2, 3);
        ClientStateTable cvt3 = new ClientStateTable(cv3, 5);
        ClientStateTable cvt4 = new ClientStateTable(cv4, 0);
        
        ClientState cv11 = cvt1.getMinimumClientState();
        ClientState cv21 = cvt2.getMinimumClientState();
        ClientState cv31 = cvt3.getMinimumClientState();
        ClientState cv41 = cvt4.getMinimumClientState();
        
        int[] exp1 = {0, 0};
        int[] exp2 = {0,0,0,0};
        int[] exp3 = {0, 0, 0, 0, 0, 0};
        int[] exp4 = {1, 0, 1, 3, 0};
        
        assertArrayEquals(cv11.getState(), exp1);
        assertArrayEquals(cv21.getState(), exp2);
        assertArrayEquals(cv31.getState(), exp3);
        assertArrayEquals(cv41.getState(), exp4);
    }

}
