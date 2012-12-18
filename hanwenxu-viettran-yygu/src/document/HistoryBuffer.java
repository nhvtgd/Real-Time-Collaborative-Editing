package document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.Stack;
import java.util.Arrays;
import java.util.Comparator;

/**
 * This history buffer keeps track of what operations have been performed. This
 * is useful for transformation algorithm.
 * 
 * We will use this for both the client and server side code so that they can
 * recall the order in which they receive operations
 * 
 * We shall also make sure all of the operations in the history are immutable,
 * since they are only to be used as a record.
 * 
 * Thread safety argument: Since this code is only accessed by one process, and
 * there is no shared memory, this code should be thread safe.
 * 
 * @author Hanwen Xu
 * 
 */
public class HistoryBuffer {

    /**
     * This will store all of the processed operations in the client
     */
    private HashMap<String, Operation> ops = null;

    /**
     * Number of operations stored
     */
    private int size = 0;

    /**
     * This is the constructor for the HistoryBuffer. It will create a new
     * hashmap, and instatiate the size counter.
     */
    public HistoryBuffer() {
        this.ops = new HashMap<String, Operation>();
        this.size = 0;
    }

    /**
     * Gets the number of operations in the history.
     * 
     * @return Integer size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Adds a local operation Remember to set it as immutable
     * 
     * @param op
     *            Local operation
     */
    public void addLocalOperation(Operation op) {
        String key = Operation.createHistoryKey(op.siteId, op.seqId);
        this.ops.put(key, op);
        op.immutable = true;
        ++this.size;
    }

    /**
     * Adds a received operation to the history. If the operation already exists
     * in the history, simply updates its order attribute. If not, adds it.
     * Throws an exception if the op does not include its place in the total
     * order or if the op with the same key already has an assigned place in the
     * total order.
     * 
     * We must also set the operation as immutable.
     * 
     * @param op
     *            Received operation to add
     * @throws OperationEngineException
     */
    public void addRemoteOperation(Operation op)
            throws OperationEngineException {
        String key = Operation.createHistoryKey(op.siteId, op.seqId);
        Operation eop = this.ops.get(key);

        if (op.getOrder() == Operation.infinity) {
            throw new OperationEngineException("remote op missing total order");
        } else if (eop != null) {
            if (eop.getOrder() != Operation.infinity) {
                throw new OperationEngineException(
                        "duplicate op in total order: old=" + eop.getOrder()
                                + " new=" + op.getOrder());
            }
            eop.setOrder(op.getOrder());
        } else {
            this.ops.put(key, op);
            op.immutable = true;
            ++this.size;
        }
    }

    /**
     * Removes and returns an operation in the history.
     * 
     * @param op
     *            Operation to locate for removal, set to mutable because it is
     *            no longer in history
     * @return Removed operation
     */
    public Operation removeOperation(Operation op) {
        String key = Operation.createHistoryKey(op.siteId, op.seqId);

        op = this.ops.remove(key);

        op.immutable = false;

        --this.size;
        return op;
    }

    /**
     * Gets all operations in the history buffer sorted by context.
     * 
     * @return Sorted operations
     */
    public Stack<Operation> getContextSortedOperations() {

        Collection<Operation> collection = this.ops.values();
        Operation[] arr = new Operation[collection.size()];
        arr = collection.toArray(arr);

        Arrays.sort(arr, new Comparator<Operation>() {
            public int compare(Operation a, Operation b) {
                return a.compareByState(b);
            }
        });

        Stack<Operation> stack = new Stack<Operation>();
        stack.addAll(Arrays.asList(arr));

        return stack;
    }

    /**
     * Useful for JUnit testing.
     */
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("{ops : " + this.ops);
        b.append(",size : " + this.size);
        b.append("}");

        return b.toString();
    }

    /**
     * provides a copy of the data inside this HistoryBuffer. Also allows it to
     * be serialized and transferred across the network. It will be an array of
     * Operation state objects.
     * 
     * @return {Object[]} copy of internal data.
     */
    public Object[] getState() {
        Vector<Object[]> v = new Vector<Object[]>();
        Operation op = null;

        for (String key : this.ops.keySet()) {
            op = this.ops.get(key);
            v.addElement(op.getState());
        }

        return v.toArray();
    }

    /**
     * allows the history buffer to take in an array of objects, each object is
     * a state object of an operation. This will allow the HistoryBuffer to make
     * a copy of another HistoryBuffer.
     * 
     * @param opStates
     *            Array in the format returned by getState
     */
    public void setState(Object[] opStates) {
        this.size = 0;
        this.ops.clear();
        for (int i = 0; i < opStates.length; i++) {
            Operation op = null;
            try {
                op = Operation.createOperationFromState((Object[]) opStates[i]);
                this.addLocalOperation(op);
            } catch (OperationEngineException e) {
                ;
            }
        }
    }

    /**
     * Retrieves all of the operations represented by the given context
     * differences from the history buffer. Sorts them by total order, placing
     * any ops with an unknown place in the order (i.e., local ops) at the end
     * sorted by their sequence IDs. Throws an exception when a requested
     * operation is missing from the history.
     * 
     * @param cd
     *            Context difference object
     * @throws OperationEngineException
     * @return Sorted operations
     */
    public Stack<Operation> getOpsForDifference(StateDifference cd)
            throws OperationEngineException {
        String[] keys = cd.getHistoryBufferKeys();

        Vector<Operation> opsStack = new Vector<Operation>();
        int l = keys.length;
        String key;
        Operation op;

        for (int i = 0; i < l; i++) {
            key = keys[i];
            op = this.ops.get(key);
            if (op == null) {
                throw new OperationEngineException(
                        "HistoryBuffer error-- We are missing ops for context: i="
                                + i + " key=" + key + " keys="
                                + keys.toString());
            }
            opsStack.addElement(op);
        }

        Operation[] arr = new Operation[opsStack.size()];
        arr = opsStack.toArray(arr);
        Arrays.sort(arr, new Comparator<Operation>() {
            public int compare(Operation a, Operation b) {
                return a.compareByOrder(b);
            }
        });

        Stack<Operation> stack = new Stack<Operation>();
        stack.addAll(Arrays.asList(arr));

        return stack;
    }

}
