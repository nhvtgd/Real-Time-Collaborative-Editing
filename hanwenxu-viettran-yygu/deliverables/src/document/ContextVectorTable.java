package document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * This class will serve as a storage mechanism for all of the context vectors.
 * This will make it easier to identify causality.  
 * 
 * Thread safety argument:  Since this will not create any new threads, and since it is accessed by only
 * one processor, there should be no conflict, deadlocking, or any other concurrency issues.  This is
 * only used at a local processing server or client.
 * @author Hanwen Xu
 *
 */
public class ContextVectorTable {

private ArrayList<ContextVector> cvt;
    
    public ContextVectorTable(ContextVector cv, int site) throws OperationEngineException {
        this.cvt = new ArrayList<ContextVector>();
        this.growTo(site + 1);
        this.cvt.set(site, cv);
    }
    
    /**
     * Converts the contents of this context vector table to a string.
     *
     * @return {String} All context vectors in the table (for debug)
     */
    @Override
    public String toString() {
        String[] arr = new String[this.cvt.size()];
        int l = this.cvt.size();
        for(int i = 0; i < l; i++) {
            ContextVector cv = this.cvt.get(i);
            arr[i] = cv.toString();
        }
        return Arrays.toString(arr);
    }
    
    /**
     * Gets the index of each entry in the table frozen to (i.e., sharing a 
     * reference with, the given context vector, skipping the one noted in the 
     * skip param.
     *
     * @param cv Context vector instance
     * @param skip Integer index to skip
     * @return Integer indices of table slots referencing the
     * context vector
     */
    public int[] getEquivalents(ContextVector cv, int skip) {
        
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Serializes the state of this context vector table for transmission.
     *
     * @return Array of context vectors serialized as arrays
     */
    public int[][] getState() {
        int l = this.cvt.size();
        int[][] arr = new int[l][];
        
        for(int i=0; i < l; i++) {
            ContextVector cv = this.cvt.get(i);
            
            arr[i] = cv.getState();
        }
        return arr;
    }
    
    /**
     * Unserializes context vector table contents to initialize this intance.
     *
     * @param arr Array in the format returned by getState
     * @throws OperationEngineException 
     */
    public void setState(int[][] arr) throws OperationEngineException {
        
        throw new RuntimeException("Not implemented");
        
    }
    
    /**
     * Increases the size of the context vector table to the given size.
     * Inceases the size of all context vectors in the table to the given size.
     * Initializes new entries with zeroed context vectors.
     *
     * @param count Desired integer size
     * @throws OperationEngineException 
     */
    public void growTo(int count) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Gets the context vector for the given site. Grows the table if it does 
     * not include the site yet and returns a zeroed context vector if so.
     *
     * @param site Integer site ID
     * @throws OperationEngineException 
     * @return Context vector for the given site
     */
    public ContextVector getContextVector(int site) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the context vector for the given site. Grows the table if it does
     * not include the site yet.
     *
     * @param site Integer site ID
     * @param cv Context vector instance
     * @throws OperationEngineException 
     */
    public void updateWithContextVector(int site, ContextVector cv) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the context vector for the site on the given operation. Grows the 
     * table if it does not include the site yet.
     *
     * @param op Operation with the site ID and context vector
     * @throws OperationEngineException 
     */
    public void updateWithOperation(Operation op) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Gets the context vector with the minimum sequence number for each site
     * among all context vectors in the table. Gets null if the minimum
     * vector cannot be constructed because the table is empty.
     * @throws OperationEngineException 
     *
     * @return Minium context vector
     */
    public ContextVector getMinimumContextVector() throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
}
