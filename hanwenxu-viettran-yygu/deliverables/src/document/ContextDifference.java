package document;

import java.util.Arrays;
import java.util.Vector;

/**
 * This class is a way to store the differences in context for a collection of vectors. With this,
 * it is easier to determine causality between operations.  
 * 
 * Thread safety argument:  This function should always be thread safe since this code is only accessed on a local machine, and no
 * new threads are created.  Since no new threads are created, and no other process access shared memory, then there should be thread
 * safety.
 * @author Hanwen Xu
 *
 */
public class ContextDifference {

    public Vector<Integer> sites;
    public Vector<Integer> seqs;
    
    
    public ContextDifference() {
        this.sites = new Vector<Integer>();
        this.seqs = new Vector<Integer>();
    }
    
    
    /**
     * Adds a range of operations to the difference.
     *
     * @param site Integer site ID
     * @param start First integer operation sequence number, inclusive
     * @param end Last integer operation sequence number, exclusive
     */
    public void addRange(int site, int start, int end) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Adds a single operation to the difference.
     *
     * @param site Integer site ID
     * @param seq Integer sequence number
     */
    public void addSiteSeq(int site, int seq) {
        throw new RuntimeException("Not implemented");        
    }
    
    /**
     * Gets the history buffer keys for all the operations represented in this
     * context difference.
     *
     * @return Array of keys for HistoryBuffer lookups
     */
    public String[] getHistoryBufferKeys() {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Converts the contents of this context difference to a string.
     *
     * @return All keys in the difference (for debug)
     */
    public String toString() {
        throw new RuntimeException("Not implemented");
    }

}
