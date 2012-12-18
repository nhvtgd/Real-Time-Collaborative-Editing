package document;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;


/**
 * This is the vector that allows us to map out causality between
 * operations.  This will map the string to a operation object.  
 * 
 * Thread safety argument:  Since no new threads are created, this class should be thread safe.  Also, no new
 * thread or process will need to access shared memory.  Therefore, this entire class should be thread safe.
 * @author Hanwen Xu
 *
 */
public class ContextVector {

    private int[] sites;
    
    public ContextVector(Map<String, Object> args) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Converts the contents of this context vector sites array to a string.
     *
     * @return All integers in the vector (for debug)
     */
    @Override
    public String toString() {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Serializes this context vector.
     *
     * @return Array of integer sequence numbers
     */
    public int[] getState() {
        throw new RuntimeException("Not implemented");
    }
    
    
    /**
     * Makes an independent copy of this context vector.
     * @throws OperationEngineException 
     *
     * @return Copy of this context vector
     */
    public ContextVector copy() throws OperationEngineException {
        
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Makes an independent copy of the array in this context vector.
     *
     * @return Copy of this context vector's sites array
     */
    public int[] copySites() {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Computes the difference in sequence numbers at each site between this
     * context vector and the one provided.
     *
     * @param cv Other context vector object
     * @return Represents the difference between this vector and the one passed
     */
    public ContextDifference subtract(ContextVector cv) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Finds the oldest sequence number in the difference in sequence numbers
     * for each site between this context and the one provided.
     *
     * @param cv Other context vector object
     * @return Represents the oldest difference for each
     * site between this vector and the one passed
     */
    public ContextDifference oldestDifference(ContextVector cv) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Increases the size of the context vector to the given size. Initializes
     * new entries with zeros.
     *
     * @param count Desired integer size of the vector
     */
    public void growTo(int count) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Gets the sequence number for the given site in this context vector.
     * Grows the vector if it does not include the site yet.
     * 
     * @param site Integer site ID
     * @return Integer sequence number for the site
     */
    public int getSeqForSite(int site) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Sets the sequence number for the given site in this context vector.
     * Grows the vector if it does not include the site yet.
     * 
     * @param site Integer site ID
     * @param seq Integer sequence number
     */
    public void setSeqForSite(int site, int seq) {  
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Gets the size of this context vector.
     *
     * @return Integer size
     */
    public int getSize() {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Determines if this context vector equals the other in terms of the
     * sequence IDs at each site. If the vectors are of different sizes, treats
     * missing entries as suffixed zeros.
     *
     * @param cv Other context vector
     * @return True if equal, false if not
     */
    public boolean equals(ContextVector cv) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Computes an ordered comparison of two context vectors according to the
     * sequence IDs at each site. If the vectors are of different sizes, 
     * treats missing entries as suffixed zeros.
     *
     * @param cv Other context vector
     * @return -1 if this context vector is ordered before the other,
     *   0 if they are equal, or 1 if this context vector is ordered after the
     *   other
     */
    public int compare(ContextVector cv) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * public accessor to retrieve the sites
     * @return int[] listing all of the sites
     */
    public int[] getSites() {
        throw new RuntimeException("Not implemented");
    }

}
