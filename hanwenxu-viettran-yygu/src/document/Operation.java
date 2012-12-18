package document;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * This is the abstract class for the operation. A single operation is anything
 * that inserts or delete text from the datatype GapBuffer. This operation data
 * type can allow for creating from various different calls, as well as
 * transforming due to concurrent operations. This is the major part where we
 * will write the transformation algorithm in the subclasses of this class.
 * 
 * Thread safety argument: This class is thread safe because this data is
 * generated, and modified by only one thread. Therefore, there should be no
 * locking or deadlocking, or memory issues.
 * 
 * @author Hanwen Xu
 * 
 */
public abstract class Operation implements Serializable {

    /**
     * the ID number, for serializing
     */
    private static final long serialVersionUID = -4459256618218580312L;
    /**
     * no more magic number
     */
    protected final static int infinity = Integer.MAX_VALUE;
    /**
     * siteId: Integer client ID where the op originated
     */
    protected int siteId;
    /**
     * seqId: The number of operation processed at this clientID
     */
    protected int seqId;

    /**
     * type of operation, such as delete or insert
     */
    protected String type = null;

    /**
     * boolean variable denoting if this operation was performed locally
     */
    protected boolean local = false;

    /**
     * state machine of the client when the operation was created.
     */
    protected ClientState clientState = null;

    /**
     * The document that is being modified by this operation
     */
    protected String key = null;

    /**
     * The value being inserted, string.
     */
    protected String value = null;

    /**
     * The offset of the operation.
     */
    protected int position;

    /**
     * The universal order in which the server received this operation. Doesn't
     * matter for local operations.
     */
    private int order;

    /**
     * If this operation is mutable or not.
     */
    protected boolean immutable;

    /**
     * Place to store referenceable operations
     */
    protected Vector<Operation> xCache = null;

    /**
     * This is a constructor which returns a new operation. This allows creation
     * after specifying what type of operation is desired. This will then call
     * the constructor of the subclasses and return a new operation.
     * 
     * @param type
     *            , requires to be either insert, delete, or update
     * @param args
     *            , requires to be a map of correct type
     * @returns a new Operation of the specified subtype, returns null if the
     *          type is not matched correctly
     * @throws OperationEngineException
     *             if any of the operation creation function fails.
     */
    public static Operation createOperationFromType(String type,
            Map<String, Object> properties) throws OperationEngineException {
        Operation op = null;

        if (type.equals("insert")) {
            op = new InsertOperation(properties);
        } else if (type.equals("delete")) {
            op = new DeleteOperation(properties);
        } else if (type.equals("update")) {
            op = new UpdateOperation(properties);
        }

        return op;
    }

    /**
     * This is another constructor of the Operation class. By using a object
     * array state, we can construct a new operation
     * 
     * @param state
     *            , requires to be an object array
     * @return new operation
     * @throws OperationEngineException
     */
    public static Operation createOperationFromState(Object[] state)
            throws OperationEngineException {
        return null;
    }

    /**
     * We will use this function to create a unique history key so that its
     * history of operations can be referenced easily
     * 
     * @param site
     *            , requires to be any integer of the site of the IP
     * @param seq
     *            , requires to be another integer of the sequence
     * @return a new string that will serve as a key.
     */
    public static String createHistoryKey(int site, int seq) {
        return new Integer(site).toString() + "," + new Integer(seq).toString();
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("{siteId : " + this.siteId);
        b.append(",seqId : " + this.seqId);
        b.append(",type :" + type);
        b.append(",contextVector : " + this.clientState);
        b.append(",key : " + this.key);
        b.append(",position : " + this.position);
        b.append(",value : " + this.value);
        b.append(",order : " + this.getOrder());
        b.append("}");

        return b.toString();
    }

    /**
     * Contains information about a local or remote event for transformation.
     * 
     * Initializes the operation from serialized state or individual props if
     * state is not defined in the args parameter.
     * 
     * @param properties
     *            Map containing the following: state: Array in format returned
     *            by getState bundling the following individual parameter values
     *            siteId: Integer site ID where the op originated contextVector:
     *            Context in which the op occurred key: Name of the property the
     *            op affected value: Value of the op position: Integer position
     *            of the op in a linear collection order: Integer sequence
     *            number of the op in the total op order across all sites seqId:
     *            Integer sequence number of the op at its originating site. If
     *            undefined, computed from the context vector and site ID.
     *            immutable: True if the op cannot be changed, most likely
     *            because it is in a history buffer somewhere to this instance
     * @throws OperationEngineException
     */
    @SuppressWarnings("unchecked")
    protected Operation(Map<String, Object> properties)
            throws OperationEngineException {
        if (properties == null) {
            this.type = null;
            return;
        }

        if (properties.containsKey("state")) {
            this.setState((Object[]) properties.get("state"));
            this.local = false;
        } else {
            this.siteId = ((Integer) properties.get("siteId")).intValue();
            this.clientState = (ClientState) properties
                    .get("contextVector");
            this.key = (String) properties.get("key");
            this.value = (String) properties.get("value");
            this.position = ((Integer) properties.get("position")).intValue();

            Integer ord = (Integer) properties.get("order");
            if (ord == null) {
                this.setOrder(Operation.infinity);
            } else {
                this.setOrder(ord.intValue());
            }

            if (properties.containsKey("seqId")) {
                this.seqId = ((Integer) properties.get("seqId")).intValue();
            } else if (this.clientState != null) {
                this.seqId = this.clientState.getSeqForClient(this.siteId) + 1;
            } else {
                throw new OperationEngineException(
                        "missing sequence id for new operation");
            }

            if (properties.containsKey("xCache")) {
                this.xCache = (Vector<Operation>) properties.get("xCache");
            } else {
                this.xCache = null;
            }

            this.local = ((Boolean) properties.get("local")).booleanValue() || false;
        }

        this.immutable = false;

        if (this.xCache == null) {
            this.xCache = new Vector<Operation>();
        }
    }

    /**
     * This function will return a new operation after transforming the current
     * operation with the specified data type
     * 
     * @param op
     *            requires to be an operation
     * @return a new operation that is transformed after the specified operation
     *         is applied
     */
    public abstract Operation transformWithDelete(Operation op);

    public abstract Operation transformWithInsert(Operation op);

    public abstract Operation transformWithUpdate(Operation op);

    /**
     * Serializes the operation as an array of values for transmission. Also
     * useful for copying the operation and sharing with other operations, or
     * functions, or classes
     * 
     * @return {Object[]} Array with the name of the operation type and all of
     *         its instance variables as primitive JS types
     */
    public Object[] getState() {
        Object[] properties = { this.type, this.key, this.value, this.position,
                this.clientState.getState(), this.seqId, this.siteId,
                this.getOrder() };

        return properties;
    }

    /**
     * Can use this function to copy another operation, and set all of the
     * parameters, given a correct state object array.
     * 
     * @param properties
     *            Array in the format returned by getState
     * @throws OperationEngineException
     */
    public void setState(Object[] properties) throws OperationEngineException {
        if (!((String) properties[0]).equals(this.type)) {
            throw new OperationEngineException(
                    "setState invoked with state from wrong op type");
        } else if (this.immutable) {
            throw new OperationEngineException("op is immutable");
        }

        this.key = (String) properties[1];
        this.value = (String) properties[2];
        this.position = ((Integer) properties[3]).intValue();

        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("state", (Object[]) properties[4]);

        this.clientState = new ClientState(args);

        this.seqId = ((Integer) properties[5]).intValue();
        this.siteId = ((Integer) properties[6]).intValue();

        if (properties.length >= 8) {
            this.setOrder(((Integer) properties[7]).intValue());
        } else {
            this.setOrder(Operation.infinity);
        }
    }

    /**
     * Makes a copy of this operation object. Takes a shortcut and returns a ref
     * to this instance if the op is marked as mutable.
     * 
     * @throws OperationEngineException
     * 
     * @return Operation object
     */
    public Operation copy() throws OperationEngineException {
        HashMap<String, Object> properties = new HashMap<String, Object>();

        properties.put("siteId", new Integer(this.siteId));
        properties.put("seqId", new Integer(this.seqId));
        properties.put("contextVector", this.clientState.copy());
        properties.put("key", this.key);
        properties.put("value", this.value);
        properties.put("position", new Integer(this.position));
        properties.put("order", new Integer(this.getOrder()));
        properties.put("local", new Boolean(this.local));
        properties.put("xCache", this.xCache);

        Operation op;
        try {
            op = Operation.createOperationFromType(this.type, properties);
        } catch (OperationEngineException e) {
            e.printStackTrace();
            op = null;
        }

        return op;
    }

    /**
     * Gets a version of the given operation previously transformed into the
     * given context if available.
     * 
     * @param cv
     *            Context of the transformed op to seek
     * @throws OperationEngineException
     * @return Copy of the transformed operation from the cache or null if not
     *         found in the cache
     */
    public Operation getFromCache(ClientState cv)
            throws OperationEngineException {
        Vector<Operation> cache = this.xCache;
        int l = cache.size();
        Operation xop;

        for (int i = 0; i < l; i++) {
            xop = cache.elementAt(i);
            if (xop.clientState.equals(cv)) {
                return xop.copy();
            }
        }

        return null;
    }

    /**
     * Caches a transformed copy of this original operation for faster future
     * transformations.
     * 
     * @param siteCount
     *            Integer count of active sites, including the local one
     * @throws OperationEngineException
     */
    public void addToCache(int siteCount) throws OperationEngineException {
        Vector<Operation> cache = this.xCache;
        Operation cop = this.copy();

        cop.immutable = true;

        cache.addElement(cop);

        int diff = cache.size() - (siteCount - 1);
        if (diff > 0) {
            Operation[] operationArray = new Operation[cache.size()];
            operationArray = cache.toArray(operationArray);
            Operation[] newArr = Arrays.copyOf(operationArray, diff);

            cache.removeAllElements();
            for (int i = 0; i < newArr.length; i++) {
                cache.addElement(newArr[i]);
            }
        }
    }

    /**
     * Computes an ordered comparison of this op and another based on their
     * context vectors. Used for sorting operations by their contexts.
     * 
     * @param op
     *            Other operation
     * @return -1 if this op is ordered before the other, 0 if they are in the
     *         same context, and 1 if this op is ordered after the other
     */
    public int compareByState(Operation op) {
        int rv = this.clientState.compare(op.clientState);
        if (rv == 0) {
            if (this.siteId < op.siteId) {
                return -1;
            } else if (this.siteId > op.siteId) {
                return 1;
            } else {
                return 0;
            }
        }
        return rv;
    }

    /**
     * Computes an ordered comparison of this op and another based on their
     * position in the total op order. If the order is the same, then we have to
     * take into account which one was local, and assign that priority. If they
     * are both local or both remote, then we take into account the SeqID.
     * 
     * @param op
     *            Other operation
     * @return -1 if this op is ordered before the other, 0 if they are in the
     *         same context, and 1 if this op is ordered after the other
     */
    public int compareByOrder(Operation op) {
        if (this.getOrder() == op.getOrder()) {
            if (this.local == op.local) {
                int solution;
                if (this.seqId < op.seqId) {
                    solution = -1;
                } else {
                    solution = 1;
                }
                return solution;
            } else if (this.local && !op.local) {
                return 1;
            } else if (!this.local && op.local) {
                return -1;
            }
        } else if (this.getOrder() < op.getOrder()) {
            return -1;
        } else if (this.getOrder() > op.getOrder()) {
            return 1;
        }

        return -1;
    }

    /**
     * Transforms this operation to include the effects of the operation
     * provided as a parameter IT(this, op). Upgrade the context of this op to
     * reflect the inclusion of the other.
     * 
     * @throws OperationEngineException
     * 
     * @return This operation, transformed in-place, or null if its effects are
     *         nullified by the transform
     * @throws {Error} If this op to be transformed is immutable or if the this
     *         operation subclass does not implement the transform method needed
     *         to handle the passed op
     */
    public Operation transformWith(Operation op)
            throws OperationEngineException {
        if (this.immutable) {
            throw new OperationEngineException(
                    "attempt to transform immutable op");
        }

        Operation rv = null;
        if (op.type.equals("delete")) {
            rv = this.transformWithDelete(op);
        } else if (op.type.equals("insert")) {
            rv = this.transformWithInsert(op);
        } else if (op.type.equals("update")) {
            rv = this.transformWithUpdate(op);
        }

        if (rv != null) {
            this.upgradeContextTo(op);
        }

        return rv;
    }

    /**
     * Upgrades the context of this operation to reflect the inclusion of a
     * single other operation from some site.
     * 
     * @param op
     *            The operation to include in the context of this op
     * @throws OperationEngineException
     * @throws {Error} If this op to be upgraded is immutable
     */
    public void upgradeContextTo(Operation op) throws OperationEngineException {
        if (this.immutable) {
            throw new OperationEngineException(
                    "attempt to upgrade context of immutable op");
        }

        this.clientState.setSeqForClient(op.siteId, op.seqId);
    }

    /**
     * Get the siteID
     * 
     * @return the siteID integer
     */
    public int getSiteId() {
        return this.siteId;
    }

    /**
     * Get the type
     * 
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Get the SeqID integer
     * 
     * @return seqID for this class
     */
    public int getSeqId() {
        return this.seqId;
    }

    /**
     * Get the string of the operation value
     * 
     * @return string containing the vlaue
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Get the integer of the offset position
     * 
     * @return int position
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Get the ContextVector associated with this operation
     * 
     * @return ContextVector
     */
    public ClientState getClientState() {
        return this.clientState;
    }

    /**
     * Mutator that changes the immutable status
     * 
     * @param immutable
     *            , requires to be a boolean mutates the immutable variable.
     */
    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }

    /**
     * get the order of the operation
     * 
     * @return int order
     */
    public int getOrder() {
        return order;
    }

    /**
     * set the order of the operation
     * 
     * @param order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Return the key value;
     */
    public String getKey() {
        return this.key;
    }

    /**
     * sets the key value
     * @param key
     *            value to set to
     */
    public void setKey(String key) {
        this.key = key;
    }

}
