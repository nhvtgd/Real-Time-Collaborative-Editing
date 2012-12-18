package document;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

/**
 * Tests for the Pair class to make sure pairs can be initialized and compared.
 * @author youyanggu
 *
 */
public class PairTest {
    
    /**
     *  Initializes and tests for equality
     */
    @Test
    public void testEquals(){
        Pair<Integer, String> pair1 = new Pair<Integer, String>(5,"hello");
        Pair<Integer, String> pair2 = new Pair<Integer, String>(5,"hello");
        Pair<Integer, String> pair3 = new Pair<Integer, String>(4,"hello");
        Pair<String, String> pair4 = new Pair<String, String>("hello","world");
        Pair<ArrayList<String>, HashMap<String, Integer>> pair5 = 
                new Pair<ArrayList<String>, HashMap<String, Integer>>(new ArrayList<String>(),new HashMap<String, Integer>());
        
        assertTrue(pair1.equals(pair1));
        assertTrue(pair1.equals(pair2));
        assertTrue(pair2.equals(pair1));
        
        assertFalse(pair1.equals(pair3));
        assertFalse(pair3.equals(pair1));
        assertFalse(pair3.equals(pair2));
        
        
        assertFalse(pair4.equals(pair1));
        assertFalse(pair5.equals(pair4));
        
    }
    
    /**
     *  Makes sure the hashcode for identical pairs are also equal
     */
    @Test 
    public void testHashCode(){
        Pair<Integer, String> pair1 = new Pair<Integer, String>(40,"foo");
        Pair<Integer, String> pair2 = new Pair<Integer, String>(40,"foo");
        assertTrue(pair1.hashCode() == pair2.hashCode());
    }

}

