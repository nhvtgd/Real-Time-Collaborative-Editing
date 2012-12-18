package document;

import java.util.Map;

/**
 * We might have to call upon a third operation type, which will tell the client or server to update their stuff.
 * @author Hanwen Xu
 *
 */
public class UpdateOperation extends Operation {

    protected UpdateOperation(Map<String, Object> args) throws OperationEngineException {
        super(args);
        this.type = "update";
    }
    
    /**
     * Transforms this update to include the effect of an update.
     *
     * @param op Update to include in this op
     * @return This instance
     */
    public Operation transformWithUpdate(Operation op) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Transforms this update to include the effect of an insert.
     *
     * @param op Insert to include in this op
     * @return This instance
     */
    public Operation transformWithInsert(Operation op) {
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Transforms this update to include the effect of a delete.
     *
     * @param op Delete to include in this op
     * @return This instance
     */
    public Operation transformWithDelete(Operation op) {
        throw new RuntimeException("Not implemented");
    }

}
