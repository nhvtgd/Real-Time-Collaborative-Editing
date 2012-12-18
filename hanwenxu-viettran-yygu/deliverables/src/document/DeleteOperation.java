package document;

import java.util.Map;


/**
 * This DeleteOperation is the implementaiton of the abstract class Operation.
 * 
 * This will reperent a delete operation at a certain place, as stated by the mapping
 * of string to object.
 * 
 * Thread safety argument:  This operation is only accessed and created by one thread.  Also, this does not
 * create any new threads.  Therefore, this datatype should be thread safe for our purposes.  
 * @author Hanwen Xu
 *
 */
public class DeleteOperation extends Operation {

    protected DeleteOperation(Map<String, Object> args) throws OperationEngineException {
        super(args);
        this.type = "delete";
    }
    
    
    /**
     * Transforms this delete to include the effect of a delete.
     *
     * @param op Delete to include in this op
     * @return This instance or null if this op has no
     * further effect on other operations
     */
    public Operation transformWithDelete(Operation op) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * No-op. Update has no effect on a delete.
     *
     * @param op Update to include in this op
     * @return This instance
     */
    public Operation transformWithUpdate(Operation op) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Transforms this delete to include the effect of an insert.
     *
     * @param op Insert to include in this op
     * @return This instance
     */
    public Operation transformWithInsert(Operation op) {
        throw new RuntimeException("Not implemented");
    }

}
