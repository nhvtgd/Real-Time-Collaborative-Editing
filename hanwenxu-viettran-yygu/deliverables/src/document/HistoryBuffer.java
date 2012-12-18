package document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.Stack;
import java.util.Arrays;
import java.util.Comparator;

import java.util.logging.Logger;

/**
 * This history buffer keeps track of what operations have
 * been performed.  This is useful for transformation algorithm.  
 * 
 * We will use this for both the client and server side code so that they can
 * recall the order in which they receive operations
 * 
 * Thread safety argument:  Since this code is only accessed by one process, and there is no
 * shared memory, this code should be thread safe.
 * @author Hanwen Xu
 *
 */
public class HistoryBuffer {

    private static final Logger log = Logger.getLogger(HistoryBuffer.class
            .getName());
    
    private HashMap<String, Operation> ops = null;
    private int size = 0;

    public HistoryBuffer() {
        this.ops = new HashMap<String, Operation>();
        this.size = 0;
    }
    
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("{ops : " + this.ops);
        b.append(",size : " + this.size);
        b.append("}");
        
        return b.toString();
    }
    
    /**
     * Serializes the history buffer contents to seed a remote instance.
     *
     * @return {Object[]} Serialized operations in the history
     */
    public Object[] getState() {
        throw new RuntimeException("not implemented");
    }
    
    /**
     * Unserializes history buffer contents to initialize this instance.
     *
     * @param arr Array in the format returned by getState
     */
    public void setState(Object[] arr) {
        throw new RuntimeException("not implemented");
    }
    
    /**
     * Retrieves all of the operations represented by the given context
     * differences from the history buffer. Sorts them by total order, placing
     * any ops with an unknown place in the order (i.e., local ops) at the end
     * sorted by their sequence IDs. Throws an exception when a requested 
     * operation is missing from the history.
     *
     * @param cd  Context difference object
     * @throws OperationEngineException 
     * @return Sorted operations
     */ 
    public Stack<Operation> getOpsForDifference(ContextDifference cd) throws OperationEngineException {
        throw new RuntimeException("not implemented");
    }
    
    /**
     * Adds a local operation to the history.
     *
     * @param op Local operation to add
     */
    public void addLocal(Operation op) {
        throw new RuntimeException("not implemented");
    }
    
    /**
     * Adds a received operation to the history. If the operation already 
     * exists in the history, simply updates its order attribute. If not, 
     * adds it. Throws an exception if the op does not include its place in 
     * the total order or if the op with the same key already has an assigned
     * place in the total order.
     *
     * @param op Received operation to add
     * @throws OperationEngineException 
     */
    public void addRemote(Operation op) throws OperationEngineException {
        throw new RuntimeException("not implemented");
    }
    
    /**
     * Removes and returns an operation in the history.
     *
     * @param op Operation to locate for removal
     * @return Removed operation
     */
    public Operation remove(Operation op) {
        throw new RuntimeException("not implemented");
    }
    
    /**
     * Gets the number of operations in the history.
     *
     * @return Integer count
     */
    public int getCount() {
        return this.size;
    }
    
    
    /**
     * Gets all operations in the history buffer sorted by context.
     *
     * @return Sorted operations
     */
    public Stack<Operation> getContextSortedOperations() {
        
        throw new RuntimeException("not implemented");
    }

}
