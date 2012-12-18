package document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * This is the abstract class for the operation.  A single operation is anything that inserts or delete text from the datatype GapBuffer.
 * This operation data type can allow for creating from various different calls, as well as transforming due to concurrent operations.
 * This is the major part where we will write the transformation algorithm in the subclasses of this class.
 * 
 * Thread safety argument:  This class is thread safe because this data is generated, and modified by only one thread.  Therefore,
 * there should be no locking or deadlocking, or memory issues.  
 * @author Hanwen Xu
 *
 */
public abstract class Operation {

    /**
     * 
     */
    protected final static int infinity = Integer.MAX_VALUE;
    protected int siteId;
    protected int seqId;
    protected String type = null;
    protected boolean local = false;
    protected ContextVector contextVector = null;
    protected String key = null;
    protected String value = null;
    protected int position;
    protected int order;
    protected boolean immutable;
    protected Vector<Operation> xCache = null;
    
    /**
     * This is a constructor which returns a new operation.  This allows creation after specifying what type of operation
     * is desired.  This will then call the constructor of the subclasses and return a new operation.
     * @param type, requires to be either insert, delete, or update
     * @param args, requires to be a map of correct type
     * @returns a new Operation of the specified subtype, returns null if the type is not matched correctly
     * @throws OperationEngineException if any of the operation creation function fails.
     */
    public static Operation createOperationFromType(String type, Map<String, Object> args) throws OperationEngineException  {
        Operation op = null;
        
        if(type.equals("insert")) {
            op = new InsertOperation(args);
        }
        else if(type.equals("delete")) {
            op = new DeleteOperation(args);
        }
        else if(type.equals("update")) {
            op = new UpdateOperation(args);
        }
        
        return op;
    }
    
    /**
     * This is another constructor of the Operation class.  By using a object array state, we can
     * construct a new operation
     * @param state, requires to be an object array
     * @return new operation
     * @throws OperationEngineException
     */
    public static Operation createOperationFromState(Object[] state) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * We will use this function to create a unique history key so that its history of operations can be referenced easily
     * @param site, requires to be any integer of the site of the IP
     * @param seq, requires to be another integer of the sequence
     * @return a new string that will serve as a string.
     */
    public static String createHistoryKey(int site, int seq) {
        throw new RuntimeException("Not implemented");
    }
    
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("{siteId : " + this.siteId);
        b.append(",seqId : " + this.seqId);
        b.append(",type :" + type);
        b.append(",contextVector : " + this.contextVector);
        b.append(",key : " + this.key);
        b.append(",position : " + this.position);
        b.append(",order : " + this.order);
        b.append("}");
        
        return b.toString();
    }
    /**
     * Contains information about a local or remote event for transformation.
     *
     * Initializes the operation from serialized state or individual props if
     * state is not defined in the args parameter.
     *
     * @param args Map containing the following:
     *        <li>state Array in format returned by getState 
     *            bundling the following individual parameter values
     *        <li>siteId Integer site ID where the op originated
     *        <li>contextVector Context in which the op occurred
     *        <li>key Name of the property the op affected
     *        <li>value Value of the op
     *        <li>position Integer position of the op in a linear collection
     *        <li>order Integer sequence number of the op in the 
     *            total op order across all sites
     *        <li>seqId Integer sequence number of the op at its originating site.
     *            If undefined, computed from the context vector and site ID.
     *        <li>immutable True if the op cannot be changed, most likely because
     *            it is in a history buffer somewhere to this instance
     * @throws OperationEngineException 
     */
    @SuppressWarnings("unchecked")
    protected Operation(Map<String, Object> args) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * This function will return a new operation after transforming the current operation with the specified data type
     * @param op requires to be an operation
     * @return a new operation that is transformed after the specified operation is applied
     */
    public abstract Operation transformWithDelete(Operation op);
    
    public abstract Operation transformWithInsert(Operation op);
    
    public abstract Operation transformWithUpdate(Operation op);
    
    
    /**
     * Serializes the operation as an array of values for transmission.
     *
     * @return {Object[]} Array with the name of the operation type and all
     * of its instance variables as primitive JS types
     */
    public Object[] getState() {
         // use an array to minimize the wire format
        Object[] arr = {
                this.type, 
                this.key, 
                this.value, 
                this.position, 
                this.contextVector.getSites(), 
                this.seqId, 
                this.siteId,
                this.order
        };
        
        return arr;
    }
    
    /**
     * Unserializes operation data and sets it as the instance data. Throws an
     * exception if the state is not from an operation of the same type.
     *
     * @param arr Array in the format returned by getState
     * @throws OperationEngineException 
     */
    public void setState(Object[] arr) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Makes a copy of this operation object. Takes a shortcut and returns
     * a ref to this instance if the op is marked as mutable.
     * @throws OperationEngineException 
     *
     * @return Operation object
     */
    public Operation copy() throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Gets a version of the given operation previously transformed into the
     * given context if available.
     *
     * @param cv Context of the transformed op to seek
     * @throws OperationEngineException 
     * @return Copy of the transformed operation from the 
     * cache or null if not found in the cache
     */
    public Operation getFromCache(ContextVector cv) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Caches a transformed copy of this original operation for faster future
     * transformations.
     *
     * @param siteCount Integer count of active sites, including the local one
     * @throws OperationEngineException 
     */
    public void addToCache(int siteCount) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Computes an ordered comparison of this op and another based on their
     * context vectors. Used for sorting operations by their contexts.
     *
     * @param op Other operation
     * @return -1 if this op is ordered before the other, 0 if they
     * are in the same context, and 1 if this op is ordered after the other
     */
    public int compareByContext(Operation op) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Computes an ordered comparison of this op and another based on their
     * position in the total op order.
     *
     * @param op Other operation
     * @return -1 if this op is ordered before the other, 0 if they
     * are in the same context, and 1 if this op is ordered after the other
     */
    public int compareByOrder(Operation op) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Transforms this operation to include the effects of the operation
     * provided as a parameter IT(this, op). Upgrade the context of this
     * op to reflect the inclusion of the other.
     * @throws OperationEngineException 
     *
     * @return This operation, transformed in-place, or null
     * if its effects are nullified by the transform
     * @throws {Error} If this op to be transformed is immutable or if the
     * this operation subclass does not implement the transform method needed
     * to handle the passed op
     */
    public Operation transformWith(Operation op) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Upgrades the context of this operation to reflect the inclusion of a
     * single other operation from some site.
     *
     * @param op The operation to include in the context of this op
     * @throws OperationEngineException 
     * @throws {Error} If this op to be upgraded is immutable
     */
    public void upgradeContextTo(Operation op) throws OperationEngineException {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Get the siteID
     * @return the siteID integer
     */
    public int getSiteId() {
        return this.siteId;
    }

    /**
     * Get the SeqID integer
     * @return seqID for this class
     */
    public int getSeqId() {
        return this.seqId;
    }

    /**
     * Get the string of the operation value
     * @return string containing the vlaue
     */
    public String getValue() {
        return this.value;
    }
    
    /**
     * Get the integer of the offset position
     * @return int position
     */
    public int getPosition() {
        return this.position;
    }
    
    /**
     * Get the ContextVector associated with this operation
     * @return ContextVector
     */
    public ContextVector getContextVector() {
        return this.contextVector;
    }

    /**
     * Mutator that changes the immutable status
     * @param immutable, requires to be a boolean
     * mutates the immutable variable.  
     */
    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }

}
