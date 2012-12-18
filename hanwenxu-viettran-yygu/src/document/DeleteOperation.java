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

    /**
     * This is the ID for serialization
     */
    private static final long serialVersionUID = 3085129896663389738L;

    /**
     * This is the constructor for our DeleteOperation subclass. We
     * are inheriting a bunch of functions from the abstract class,
     * but we are only changing the transform operations.  
     * @param args
     * @throws OperationEngineException
     */
    public DeleteOperation(Map<String, Object> args) throws OperationEngineException {
        super(args);
        this.type = "delete";
    }
    
    
    /**
     * Transforms this delete to include the effect of an delete. Basically,
     * we will check the position of the other operation.  If the position
     * is such that it will change the position of our operation, we have
     * to modify our operation's position. Only takes into account one operation.
     *
     * @param op Delete to include in this op
     * @return This instance or null if this op has no
     * further effect on other operations
     */
    public Operation transformWithDelete(Operation op) {
        if(!this.key.equals(op.key)) {
            return this;
        }
        if(this.position > op.position) {
            this.position-=op.value.length();
        } else if(this.position == op.position) {
            return null;
        }
        return this;
    }
    
    /**
     * Transforms this delete to include the effect of an update. Basically,
     * we will check the position of the other operation.  If the position
     * is such that it will change the position of our operation, we have
     * to modify our operation's position. Only takes into account one operation.
     *
     * @param op Update to include in this op
     * @return This instance
     */
    public Operation transformWithUpdate(Operation op) {
        return this;
    }
    
    /**
     * Transforms this delete to include the effect of an insert. Basically,
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
        if(this.position >= op.position) {
            this.position += op.value.length();
        }
        
        return this;
    }

}
