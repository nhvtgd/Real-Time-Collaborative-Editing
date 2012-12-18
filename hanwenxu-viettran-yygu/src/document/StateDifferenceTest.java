package document;

import static org.junit.Assert.*;


import java.util.Vector;

import org.junit.Test;


/**
 * This code will allow us to check between different contexts.  This is useful
 * mostly in the recursive algorithm because we want to see how far back in the history buffer
 * we need to check to change the offset value. 
 * @author Hanwen Xu
 *
 */
public class StateDifferenceTest {

    /**
     * Basic test, tests creating a StateDifference, and checking if parameters are instantiated
     * @throws OperationEngineException
     */
    @Test
    public void ContextDifferenceTest1() throws OperationEngineException{

        StateDifference sd = new StateDifference();
        Vector<Integer> sites = sd.clients;
        Vector<Integer> seqs = sd.sequenceID;
        assertEquals(sites, new Vector<Integer>());
        assertEquals(seqs, new Vector<Integer>());
    }
    
    
    /**
     * This test will test the addSiteSeq test.
     * We will test normal integer inputs
     * 
     */
    @Test
    public void addSiteSeqTest1(){
        StateDifference sd = new StateDifference();
        sd.addSiteSeq(0, 1);
        sd.addSiteSeq(1, 2);
        sd.addSiteSeq(2, 3);
        sd.addSiteSeq(3, 4);
        
        Integer[] expectedClients = {0, 1, 2, 3};
        Integer[] expectedSeq = {1, 2, 3, 4};
        
        Integer[] intArr = new Integer[sd.clients.size()];
        assertArrayEquals(sd.clients.toArray(intArr), expectedClients);
        assertArrayEquals(sd.sequenceID.toArray(intArr), expectedSeq);
         
    }
    
    /**
     * This will test addSiteSeq, but with edge cases of 0, bigINT inputs for the
     * client number and sequenceID number
     */
    @Test
    public void addSiteSeqTest2(){
        StateDifference sd = new StateDifference();
        
        int big = Integer.MAX_VALUE;
        sd.addSiteSeq(0, 0);
        sd.addSiteSeq(big, big);
        sd.addSiteSeq(0, big);
        sd.addSiteSeq(big, 0);
        sd.addSiteSeq(-big, -big);
        
        Integer[] expectedClients = {0, big, 0, big, -big};
        Integer[] expectedSeq = {0, big, big, 0, -big};
        
        Integer[] intArr = new Integer[sd.clients.size()];
        assertArrayEquals(sd.clients.toArray(intArr), expectedClients);
        assertArrayEquals(sd.sequenceID.toArray(intArr), expectedSeq);
    }
    
    /**
     * This will test the addRange function.  
     * We will test normal integer inputs
     */
    @Test
    public void addRangeTest1(){
        StateDifference sd = new StateDifference();
        
        sd.addRange(0, 1, 4);
        sd.addRange(1, 3, 5);
        Integer[] expectedClients = {0, 0, 0, 1, 1};
        Integer[] expectedSeq = {1, 2, 3, 3, 4};
        
        Integer[] intArr = new Integer[sd.clients.size()];
        assertArrayEquals(sd.clients.toArray(intArr), expectedClients);
        assertArrayEquals(sd.sequenceID.toArray(intArr), expectedSeq);
    }
    
    /**
     * This will test the addRange function.  
     * We will test bigInt edge case integer inputs
     */
    @Test
    public void addRangeTest2(){
        StateDifference sd = new StateDifference();
        int big = (int) (Integer.MAX_VALUE*.00001);
        sd.addRange(0, 0, big);
        Integer[] expectedClients = new Integer[big];
        Integer[] expectedSeq = new Integer[big];
        for(int i=0; i<big; i++){
            expectedSeq[i] = i;
            expectedClients[i] = 0;
        }
        Integer[] intArr = new Integer[sd.clients.size()];
        assertArrayEquals(sd.clients.toArray(intArr), expectedClients);
        assertArrayEquals(sd.sequenceID.toArray(intArr), expectedSeq);
        
    }
    
    /**
     * This will test the addRange function.  
     * We will test start>end edge case integer inputs
     */
    @Test
    public void addRangeTest3(){
        StateDifference sd = new StateDifference();
        
        sd.addRange(0, 3, 2);
        sd.addRange(1, 1, 1);
        
        Integer[] expectedClients = {};
        Integer[] expectedSeq = {};
        
        Integer[] intArr = new Integer[sd.clients.size()];
        assertArrayEquals(sd.clients.toArray(intArr), expectedClients);
        assertArrayEquals(sd.sequenceID.toArray(intArr), expectedSeq);
    }
    
    /**
     * This will test the getHistoryBufferKeys function
     * This test will test normal case
     */
    @Test
    public void getHistoryBufferKeysTest1(){
        StateDifference sd = new StateDifference();
        sd.addRange(0, 1, 4);
        sd.addRange(1, 3, 5);
        String[] expectedKeys = {"0,1", "0,2", "0,3", "1,3", "1,4"};
        
        assertArrayEquals(sd.getHistoryBufferKeys(), expectedKeys);
    }
    
    
    /**
     * This will test the getHistoryBufferKeys function
     * This will test the empty StateDifference
     */
    @Test
    public void getHistoryBufferKeysTest2(){
        StateDifference sd = new StateDifference();
        String[] expectedKeys = {};
        
        assertArrayEquals(sd.getHistoryBufferKeys(), expectedKeys);
    }
    
}
