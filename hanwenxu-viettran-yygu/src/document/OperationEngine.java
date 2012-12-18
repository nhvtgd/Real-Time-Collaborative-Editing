package document;

import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

/**
 * This will control the operation transform at a single site in the client
 * server architecture. This can be either a server or a client being
 * controlled. This will then take the operations and transform them by calling
 * the operation transform functions.
 * 
 * Thread safety argument: Since this engine is only run at one client or one
 * server, there should be no concurrency issues. No lock is every given or
 * acquired, so no deadlocking should occur. In addition, none of the memory is
 * being accessed by multiple processes.
 * 
 * @author Hanwen Xu
 * 
 */
public class OperationEngine {

    /**
     * This variable will be used to identify this particular Operation Engine.
     * We need to make sure that siteIDs are created in increasing, consecutive
     * integer order, starting at 0.
     */
    private int siteId;

    /**
     * This is the local state machine, represented through a ClientState
     */
    private ClientState cs = null;

    /**
     * This will allow the operation engine know what state every other client
     * is in at the stable state
     */
    private ClientStateTable cst = null;

    /**
     * This is the history buffer, to keep track of all previously processed
     * operations.
     */
    private HistoryBuffer historybuffer = null;

    /**
     * This integer keeps track of what number of clients are presently in the
     * network
     */
    private int siteCount = 1;

    /**
     * Controls the operational transformation algorithm. Provides a public API
     * for operation processing.
     * 
     * @param siteId
     *            Unique integer site ID for this engine instance
     */
    public OperationEngine(int siteId) throws OperationEngineException {
        this.siteId = siteId;

        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("count", siteId + 1);
        this.cs = new ClientState(args);
        this.cst = new ClientStateTable(this.cs, siteId);
        this.historybuffer = new HistoryBuffer();
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("{siteId : " + siteId);
        b.append(",ClientState : " + this.cs);
        b.append(",ClientStaterTable : " + this.cst);
        b.append(",HistoryBuffer : " + this.historybuffer);
        b.append(",ClientCount : " + this.siteCount);
        b.append("}");

        return b.toString();
    }

    /**
     * Makes a copy of the engine ClientState representing the local document
     * state.
     * 
     * @throws OperationEngineException
     * 
     * @return Copy of the ClientState for the local site
     */
    public ClientState copyClientState() throws OperationEngineException {
        return this.cs.copy();
    }

    /**
     * Factory method that creates an operation object initialized with the
     * given values.
     * 
     * @param local
     *            True if the operation was originated locally, false if not
     * @param key
     *            Operation key
     * @param value
     *            Operation value
     * @param type
     *            Type of operation: update, insert, delete
     * @param position
     *            Operation integer position
     * @param site
     *            Integer site ID where a remote op originated. Ignored for
     *            local operations which adopt the local site ID.
     * @param cv
     *            Operation context. Ignored for local operations which adopt
     *            the local site context.
     * @param order
     *            Place of the operation in the total order. Ignored for local
     *            operations which are not yet assigned a place in the order.
     * @throws OperationEngineException
     * @return Subclass instance matching the given type
     */
    public Operation createOp(boolean local, String key, String value,
            String type, int position, int site, int[] cv, int order)
            throws OperationEngineException {
        Map<String, Object> args = new HashMap<String, Object>();
        if (local) {
            args.put("key", key);
            args.put("position", new Integer(position));
            args.put("value", value);
            args.put("siteId", new Integer(this.siteId));
            args.put("contextVector", this.copyClientState());
            args.put("local", true);
        } else {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("sites", cv);
            ClientState clientState = new ClientState(map);

            args.put("key", key);
            args.put("position", new Integer(position));
            args.put("value", value);
            args.put("siteId", new Integer(site));
            args.put("contextVector", clientState);
            args.put("order", order);
            args.put("local", false);
        }

        return Operation.createOperationFromType(type, args);

    }

    /**
     * Creates an operation object and pushes it into the operation engine
     * algorithm. The parameters and return value are the same as those
     * documented for createOp.
     * 
     * @throws OperationEngineException
     * @return transformed operation, can be used to mutate buffer
     */
    public Operation push(boolean local, String key, String value, String type,
            int position, int site, int[] cv, int order)
            throws OperationEngineException {

        Operation op = this.createOp(local, key, value, type, position, site,
                cv, order);
        if (local) {
            return this.pushLocalOp(op);
        } else {
            return this.pushRemoteOp(op);
        }
    }

    /**
     * Procceses a local operation and adds it to the history buffer.
     * 
     * @param op
     *            Local operation
     * @return Reference to the pass parameter
     */
    public Operation pushLocalOp(Operation op) {
        this.cs.setSeqForClient(op.getSiteId(), op.getSeqId());
        this.historybuffer.addLocalOperation(op);
        return op;
    }

    /**
     * Procceses a remote operation, transforming it if required, and adds the
     * original to the history buffer.
     * 
     * @param op
     *            Remote operation
     * @throws OperationEngineException
     * @return New, transformed operation object or null if the effect of the
     *         passed operation is nothing
     */
    public Operation pushRemoteOp(Operation op) throws OperationEngineException {
        Operation top = null;

        if (this.hasProcessedOp(op)) {
            this.historybuffer.addRemoteOperation(op);
            System.out.println("already processed");
            return null;
        } else if (this.cs.equals(op.getClientState())) {
            top = op.copy();
        } else {
            StateDifference cd = this.cs.subtract(op.getClientState());
            op.setImmutable(true);
            top = this.fullTransform(op, cd);
        }

        this.cs.setSeqForClient(op.getSiteId(), op.getSeqId());
        this.historybuffer.addRemoteOperation(op);
        this.cst.operationUpdate(op);

        return top;
    }

    /**
     * Gets the size of the history buffer in terms of stored operations.
     * 
     * @return Integer size
     */
    public int getBufferSize() {
        return this.historybuffer.getSize();
    }

    /**
     * Gets if the engine has already processed the give operation based on its
     * ClientState and the ClientState of this engine instance.
     * 
     * @param op
     *            Operation to check
     * @return True if the engine already processed this operation, false if not
     */
    public boolean hasProcessedOp(Operation op) {
        int seqId = this.cs.getSeqForClient(op.getSiteId());
        // System.out.println("seqID is : "+seqId);
        // System.out.println("opSeqID is: "+op.getSeqId());
        // System.out.println("the siteID is : "+op.getSiteId());
        // System.out.println("The has processed contextVector: "+this.cv);
        // console.log('op processed? %s: this.cv=%s, seqId=%d, op.siteId=%d,
        // op.cv=%s, op.seqId=%d',
        // (seqId >= op.seqId), this.cv.toString(), seqId, op.siteId,
        // op.contextVector.toString(), op.seqId);
        return (seqId >= op.getSeqId());
    }

    /**
     * Gets the number of sites known to be participating, including this site.
     * 
     * @return Integer count
     */
    public int getSiteCount() {
        return this.siteCount;
    };

    /**
     * Executes a recursive step in the operation transformation control
     * algorithm. This method assumes it will NOT be called if no transformation
     * is needed in order to reduce the number of operation copies needed.
     * 
     * @param op
     *            Operation to transform
     * @param cd
     *            ClientState difference between the given op and the document
     *            state at the time of this recursive call
     * @throws OperationEngineException
     * @return A new operation, including the effects of all of the operations
     *         in the context difference or null if the operation can have no
     *         further effect on the document state
     */
    private Operation fullTransform(Operation op, StateDifference cd)
            throws OperationEngineException {

        // we first the get the operations that are different, namely
        // the ones we have done locally, but were not seen yet at the remote
        // client.
        Stack<Operation> ops = this.historybuffer.getOpsForDifference(cd);
        Operation prevOperation = null;
        StateDifference previousStateDifference = null;
        Operation prevCachedOperation = null;
        Operation cachedOperation = null;

        op = op.copy();

        // for all the ops in the difference, we need to transform
        for (int i = 0; i < ops.size(); i++) {
            prevOperation = ops.elementAt(i);
            if (!op.getClientState().equals(prevOperation.getClientState())) {
                // see if we've cached a transform of this op in the desired
                // context to avoid recursion
                prevCachedOperation = prevOperation.getFromCache(op
                        .getClientState());
                if (prevCachedOperation != null) {
                    prevOperation = prevCachedOperation;
                } else {
                    // transform needed to update the state of previousOperation
                    // to current Operation
                    previousStateDifference = op.getClientState().subtract(
                            prevOperation.getClientState());
                    if (previousStateDifference.clients == null
                            || previousStateDifference.clients.size() == 0) {
                        throw new OperationEngineException(
                                "transform produced empty StateDifference");
                    }
                    prevCachedOperation = this.fullTransform(prevOperation,
                            previousStateDifference);
                    if (prevCachedOperation == null) {
                        op.upgradeContextTo(prevOperation);
                        continue;
                    }
                    // now we only need the cachedOperation
                    prevOperation = prevCachedOperation;
                }
            }
            if (!op.getClientState().equals(prevOperation.getClientState())) {
                throw new OperationEngineException(
                        "ClientStates not convergent after updating");
            }
            // make a copy of the op as is before transform
            cachedOperation = op.copy();
            // transform op to include previousOperation now that ClientStates
            // match
            op = op.transformWith(prevOperation);
            if (op == null) {
                // op target was deleted by another earlier op so return now
                // do not continue because no further transforms have any
                // meaning on this op
                return null;
            }
            // cache the transformed op
            op.addToCache(this.siteCount);

            prevOperation = prevOperation.copy();
            prevOperation = prevOperation.transformWith(cachedOperation);
            if (prevOperation != null) {
                prevOperation.addToCache(this.siteCount);
            }
        }
        return op;
    }

    /**
     * Accessor to retrieve the SiteID integer
     * 
     * @return int siteId
     */
    public int getSiteId() {
        return this.siteId;
    }

    /**
     * only used for creating a new client, and then syncing with the central
     * server
     * 
     * @param cv
     */
    public void setCV(ClientState cv) {
        this.cs = cv;
        cv.growTo(siteId);
    }
}
