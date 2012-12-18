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
public class InsertOperation extends Operation{

    protected InsertOperation(Map<String, Object> args) throws OperationEngineException {
        super(args);
        this.type = "insert";
    }
    
    /**
     * Transforms this insert to include the effect of an insert. Assumes 
     * the control algorithm breaks the CP2 pre-req to ensure convergence.
     *
     * @param op Insert to include in this op
     * @return This instance
     */
    public Operation transformWithInsert(Operation op) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Transforms this insert to include the effect of a delete.
     *
     * @param op Delete to include in this op
     * @return {InsertOperation} This instance
     */
    public Operation transformWithDelete(Operation op) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * No-op. Update has no effect on an insert.
     *
     * @param op Update to include in this op
     * @return This instance
     */
    public Operation transformWithUpdate(Operation op) {
        throw new RuntimeException("Not implemented");
    }

}
