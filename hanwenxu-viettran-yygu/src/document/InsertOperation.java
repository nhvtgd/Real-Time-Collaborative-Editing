package document;

import java.util.Map;

/**
 * This class creates a subclass for the insert operation.  The functions that are different are transformation with insert or
 * transformation with deletes.  These require a special implementation according to the operational transform algorithm. 
 * 
 * This code will also contain much of the necessary transformation algorithm.  This will be transformed
 * according to the type of the other concurrent operation.
 * 
 * Thread safety argument:  Because this code is only accessed by one thread, there should be no concurrent issues.
 * There will be no deadlocking since no lock is required, and the program will not spawn any new threads.
 * @author Hanwen Xu
 *
 */
public class InsertOperation extends Operation {

    /**
     * This is the serializable ID for transmitting
     */
    private static final long serialVersionUID = -7860774059727089325L;

    /**
     * This is the constructor for the InsertOperation subclass. We
     * are inheriting a bunch of functions from the abstract class,
     * but we are only changing the transform operations.  
     * @param args
     * @throws OperationEngineException
     */
    public InsertOperation(Map<String, Object> args) throws OperationEngineException {
        super(args);
        this.type = "insert";
    }
    
    /**
     * Transforms this insert to include the effect of an insert. Basically,
     * we will check the position of the other operation.  If the position
     * is such that it will change the position of our operation, we have
     * to modify our operation's position. Only takes into account one operation.
     *
     * @param op Insert to include in this op
     * @return This instance
     */
    public Operation transformWithInsert(Operation op) {
        if(!this.key.equals(op.key)) {
            return this;
        }

        if(this.position > op.position || 
            (this.position == op.position && this.siteId <= op.siteId)) {
            this.position+=op.value.length();
        }
        return this;
    }
    
    /**
     * Transforms this insert to include the effect of an delete. Basically,
     * we will check the position of the other operation.  If the position
     * is such that it will change the position of our operation, we have
     * to modify our operation's position. Only takes into account one operation.
     *
     * @param op Delete to include in this op
     * @return {InsertOperation} This instance
     */
    public Operation transformWithDelete(Operation op) {
        if (!this.key.equals(op.key)) {
            return this;
        }
        if (this.position > op.position) {
            this.position-=op.value.length();
        }
        return this;
    }
    
    /**
     * Transforms this insert to include the effect of an update. Basically,
     * we will check the position of the other operation.  If the position
     * is such that it will change the position of our operation, we have
     * to modify our operation's position. Only takes into account one operation.
     *
     *
     * @param op Update to include in this op
     * @return This instance
     */
    public Operation transformWithUpdate(Operation op) {
        return this;
    }

}
