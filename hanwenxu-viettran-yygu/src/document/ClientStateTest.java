package document;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * This code will test the functions of the ClientState Our testing strategy
 * will be to test each function and trying to cover as many cases and edge
 * cases as we can.
 * 
 * @author Hanwen Xu
 * 
 */
public class ClientStateTest {

    /**
     * This will be the basic ClientStateTest. We will make a basic map
     * datatype for each key type.
     * 
     * Incidentally, also test the toString() function
     * 
     * @throws OperationEngineException
     */
    @Test
    public void ClientStateTest1() throws OperationEngineException {
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

        int[] cv1expected = { 0, 0, 0 };

        assertEquals(cv1.toString(), Arrays.toString(cv1expected));
        assertEquals(cv2.toString(), Arrays.toString(cv1.getState()));
        assertEquals(cv3.toString(), Arrays.toString(sites));
        assertEquals(cv4.toString(), Arrays.toString(state));

    }

    /**
     * This will give a map without any expected key, and an exception should be
     * thrown
     * 
     * @throws OperationEngineException
     */
    @Test(expected = OperationEngineException.class)
    public void ClientStateTest2() throws OperationEngineException {
        Map<String, Object> map1 = new HashMap<String, Object>();
        ClientState cv = new ClientState(map1);
        cv.copy();
    }

    /**
     * This test will test the getState method with normal input parameters.
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

        int[] cv1expected = { 0, 0, 0 };

        assertArrayEquals(cv1.getState(), cv1expected);
        assertArrayEquals(cv2.getState(), cv1.getState());
        assertArrayEquals(cv3.getState(), sites);
        assertArrayEquals(cv4.getState(), state);
    }

    /**
     * This test will examine the getState() method This will test the case of
     * an empty sites variable
     * 
     * @throws OperationEngineException
     */
    @Test
    public void getStateTest2() throws OperationEngineException {
        int[] cv1expected = {};

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("count", 0);

        ClientState cv1 = new ClientState(map1);

        assertArrayEquals(cv1.getState(), cv1expected);
    }

    /**
     * This test will test the basic functionality of copy, by comparing the
     * sites of the copied CV to the original
     * 
     * @throws OperationEngineException
     */
    @Test
    public void copyTest1() throws OperationEngineException {
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

        ClientState cv5 = cv1.copy();
        ClientState cv6 = cv2.copy();
        ClientState cv7 = cv3.copy();
        ClientState cv8 = cv4.copy();

        assertArrayEquals(cv1.getState(), cv5.getState());
        assertArrayEquals(cv2.getState(), cv6.getState());
        assertArrayEquals(cv3.getState(), cv7.getState());
        assertArrayEquals(cv4.getState(), cv8.getState());
    }

    /**
     * This test will test to make sure that the copied CV will not mutate the
     * original CV when a change is applied to the copy, and the copy will not
     * change when a change is applied to the original.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void copyTest2() throws OperationEngineException {
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

        ClientState cv5 = cv1.copy();
        ClientState cv6 = cv2.copy();
        ClientState cv7 = cv3.copy();
        ClientState cv8 = cv4.copy();

        // testing to see that copies no longer match
        assertNotSame(cv1, cv5);
        assertNotSame(cv2, cv6);
        assertNotSame(cv3, cv7);
        assertNotSame(cv4, cv8);
    }

    /**
     * This will test the edge case when the sites variable is an empty int[]
     * array
     * 
     * @throws OperationEngineException
     */
    @Test
    public void copyTest3() throws OperationEngineException {

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("count", 0);

        ClientState cv1 = new ClientState(map1);
        ClientState cv2 = cv1.copy();

        assertNotSame(cv1, cv2);
    }

    /**
     * This test will test the basic functionality of copySites(), by comparing
     * the sites of the copied CV to the original
     * 
     * @throws OperationEngineException
     */
    @Test
    public void copySitesTest1() throws OperationEngineException {
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

        int[] cv5 = cv1.copyClients();
        int[] cv6 = cv2.copyClients();
        int[] cv7 = cv3.copyClients();
        int[] cv8 = cv4.copyClients();

        assertArrayEquals(cv1.getState(), cv5);
        assertArrayEquals(cv2.getState(), cv6);
        assertArrayEquals(cv3.getState(), cv7);
        assertArrayEquals(cv4.getState(), cv8);
    }

    /**
     * This test will test to make sure that the copied clients will not mutate
     * the original clients when a change is applied to the copy, and the copy
     * will not change when a change is applied to the original.
     * 
     * @throws OperationEngineException
     */
    @Test
    public void copySitesTest2() throws OperationEngineException {
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

        int[] cv5 = cv1.copyClients();
        int[] cv6 = cv2.copyClients();
        int[] cv7 = cv3.copyClients();
        int[] cv8 = cv4.copyClients();

        // testing to see that copies no longer match
        assertNotSame(cv1.getState(), cv5);
        assertNotSame(cv2.getState(), cv6);
        assertNotSame(cv3.getState(), cv7);
        assertNotSame(cv4.getState(), cv8);
    }

    /**
     * This will test the edge case when the sites variable is an empty int[]
     * array
     * 
     * @throws OperationEngineException
     */
    @Test
    public void copySitesTest3() throws OperationEngineException {

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("count", 0);

        ClientState cv1 = new ClientState(map1);
        int[] cv2 = cv1.copyClients();

        assertNotSame(cv1.getState(), cv2);
    }

    /**
     * This will test the growTo method We will test basic input parameters in
     * this test
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

        cv1.growTo(10);
        cv2.growTo(7);
        cv3.growTo(12);
        cv4.growTo(15);

        int[] cv3exp = { 1, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0, 0 };
        int[] cv4exp = { 1, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        assertArrayEquals(cv1.getState(), new int[10]);
        assertArrayEquals(cv2.getState(), new int[7]);
        assertArrayEquals(cv3.getState(), cv3exp);
        assertArrayEquals(cv4.getState(), cv4exp);
    }

    /**
     * This will test the growTo method We will test nonsense input, such as 0
     * and negative integer
     * 
     * @throws OperationEngineException
     */
    @Test
    public void growToTest2() throws OperationEngineException {
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
        int[] cv1ex = cv1.copyClients();
        int[] cv2ex = cv2.copyClients();
        int[] cv3ex = cv3.copyClients();
        int[] cv4ex = cv4.copyClients();

        cv1.growTo(3);
        cv2.growTo(0);
        cv3.growTo(-12);
        cv4.growTo(-15);

        assertArrayEquals(cv1.getState(), cv1ex);
        assertArrayEquals(cv2.getState(), cv2ex);
        assertArrayEquals(cv3.getState(), cv3ex);
        assertArrayEquals(cv4.getState(), cv4ex);
    }

    /**
     * This test will test the getSeqForClient method We will be testing normal
     * input parameters
     * 
     * @throws OperationEngineException
     */
    @Test
    public void getSeqForClientTest1() throws OperationEngineException {
        Map<String, Object> map3 = new HashMap<String, Object>();

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        assertEquals(cv3.getSeqForClient(0), 1);
        assertEquals(cv3.getSeqForClient(2), 3);
        assertEquals(cv3.getSeqForClient(3), 4);
        assertEquals(cv3.getSeqForClient(4), 5);
        assertEquals(cv3.getSeqForClient(5), 0);

    }

    /**
     * This test will test the getSeqForClient method We will be testing nonsense
     * input parameters
     * @throws OperationEngineException 
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void getSeqForClientTest2() throws OperationEngineException {
        Map<String, Object> map3 = new HashMap<String, Object>();

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);
        assertEquals(cv3.getSeqForClient(-1), 2);
    }
    
    /**
     * This will test the setSeqForClient method.  
     * This will test the normal input.  
     * @throws OperationEngineException 
     */
    @Test
    public void setSeqForClientTest1() throws OperationEngineException{
        Map<String, Object> map3 = new HashMap<String, Object>();
        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);
        cv3.setSeqForClient(0,3);
        cv3.setSeqForClient(2, 8);
        cv3.setSeqForClient(7, 1);
        
        int[] expected = { 3, 2, 8, 4, 5 , 0, 0, 1};
        assertArrayEquals(cv3.getState(), expected);
    }
    
    /**
     * This will test the setSeqForClient method.  
     * This will test the nonsense input, should throw an exception  
     * @throws OperationEngineException 
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void setSeqForClientTest2() throws OperationEngineException{
        Map<String, Object> map3 = new HashMap<String, Object>();
        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);
        cv3.setSeqForClient(-1,3);
        cv3.setSeqForClient(2, 8);
        cv3.setSeqForClient(7, 1);
    }

    /**
     * This test will examine the getSize method.  Should be pretty straightforward,
     * and return the length of the internal array.  
     * This will test normal parameter inputs.  
     * @throws OperationEngineException 
     */
    @Test
    public void getSizeTest1() throws OperationEngineException{
        Map<String, Object> map3 = new HashMap<String, Object>();
        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);
        
        ClientState cv3 = new ClientState(map3);
        int size1 = cv3.getSize();
        
        cv3.growTo(15);
        int size2 = cv3.getSize();
        
        assertEquals(size1, 5);
        assertEquals(size2, 15);
        
    }
    
    /**
     * This test will examine the getSize method.  Should be pretty straightforward,
     * and return the length of the internal array.  
     * This will test empty array case
     * @throws OperationEngineException
     */
    @Test
    public void getSizeTest2() throws OperationEngineException{
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("count", 0);

        ClientState cv1 = new ClientState(map1);
        int size0 = cv1.getSize();
        assertEquals(size0, 0);
    }
    
    /**
     * This test will test the subtract method.
     * We shall test normal input parameters
     * @throws OperationEngineException 
     * 
     */
    @Test
    public void subtractTest1() throws OperationEngineException{
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);
        ClientState cv4 = new ClientState(map4);
        
        StateDifference sd3 = cv3.subtract(cv4);
        assertEquals(sd3.clients.toString(), "[1, 1, 2, 2, 3, 4, 4, 4, 4, 4]");
        assertEquals(sd3.sequenceID.toString(), "[1, 2, 2, 3, 4, 1, 2, 3, 4, 5]");
    }
    
    /**
     * This test will test the empty set case for subtract method.  
     * @throws OperationEngineException
     */
    @Test
    public void subtractTest2() throws OperationEngineException{
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        map1.put("count", 3);
     // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);
        
        StateDifference sd1 = cv1.subtract(cv2);
        StateDifference sd2 = cv2.subtract(cv1);
        assertEquals(sd1.clients.toString(), "[]");
        assertEquals(sd2.clients.toString(), "[]");
    }
    
    /**
     * This will test the oldestDifference method.
     * We will first test the normal case
     * @throws OperationEngineException 
     */
    @Test
    public void oldestDifferenceTest1() throws OperationEngineException{
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);
        ClientState cv4 = new ClientState(map4);
        
        StateDifference sd3 = cv3.oldestDifference(cv4);
        assertEquals(sd3.clients.toString(), "[1, 2, 3, 4]");
        assertEquals(sd3.sequenceID.toString(), "[1, 2, 4, 1]");
    }
    
    /**
     * This will test the oldestDifference method
     * We will test the empty state case
     */
    @Test
    public void oldestDifferenceTest2() throws OperationEngineException{
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        map1.put("count", 3);
        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);
        
        StateDifference sd1 = cv1.oldestDifference(cv2);
        StateDifference sd2 = cv2.oldestDifference(cv1);
        assertEquals(sd1.clients.toString(), "[]");
        assertEquals(sd2.clients.toString(), "[]");
    }
    
    /**
     * This test will test the equals function. 
     * We will test the simple is the same, same length
     * @throws OperationEngineException
     */
    @Test
    public void equalsTest1() throws OperationEngineException{
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        map1.put("count", 3);
        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);
        
        assertTrue(cv1.equals(cv2));
    }
    
    /**
     * This test will test the equals function. 
     * We will test the simple is the same, different lengths
     * @throws OperationEngineException
     */
    @Test
    public void equalsTest2() throws OperationEngineException{
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        map1.put("count", 3);
        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);
        cv2.growTo(15);
        
        assertTrue(cv1.equals(cv2));
    }
    
    /**
     * This will test the equals function
     * Will test the false response
     * @throws OperationEngineException
     */
    @Test
    public void equalsTest3() throws OperationEngineException{
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);
        ClientState cv4 = new ClientState(map4);
        
        assertTrue(!cv3.equals(cv4));
    }
    
    /**
     * This test will test the compare method.
     * We will use normal input parameters with response 0
     * @throws OperationEngineException 
     */
    @Test
    public void compareTest1() throws OperationEngineException{
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        map1.put("count", 3);
        // cv1 should be a constructor that creates a state of length 3,
        // all the values should be set to 0
        ClientState cv1 = new ClientState(map1);

        map2.put("contextVector", cv1);

        // cv2 should be a copy of cv1
        ClientState cv2 = new ClientState(map2);
        
        assertEquals(cv1.compare(cv2), 0);
        assertEquals(cv2.compare(cv1), 0);
    }
    
    /**
     * This test will test the compare method.
     * We will use normal CV parameters, with non-zero response
     * @throws OperationEngineException 
     */
    @Test
    public void compareTest2() throws OperationEngineException{
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<String, Object> map4 = new HashMap<String, Object>();

        int[] sites = { 1, 2, 3, 4, 5 };
        map3.put("sites", sites);

        // cv3 should be an a state of length equal to the length of the sites
        // array
        // however, since we don't have sequence data, each value should be 0
        ClientState cv3 = new ClientState(map3);

        int[] state = { 1, 0, 1, 3, 0 };
        map4.put("state", state);
        ClientState cv4 = new ClientState(map4);
        
        assertEquals(cv3.compare(cv4), 1);
        assertEquals(cv4.compare(cv3), -1);
    }
    
    

}
