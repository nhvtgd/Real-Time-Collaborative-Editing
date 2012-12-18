package document;

import java.util.Map;

/**
 * We might have to call upon a third operation type, which will tell the client
 * or server to update their stuff. This update isn't used in our implementation
 * so far, but we had plans to use this as a way to set font, color, text style,
 * and alignment of document. If we have time, we would change this operation to
 * handle the format updates.
 * 
 * Unfortunately, we didn't have time to implement any code using this
 * operation, but some of the OperationEngine has this code implemented utilizng
 * UpdateOperation. Therefore, we decided to leave this class in here
 * 
 * @author Hanwen Xu
 * 
 */
public class UpdateOperation extends Operation {

    private static final long serialVersionUID = -4638094934683546104L;

    /**
     * This is the constructor for the update operation.
     * 
     * @param args
     * @throws OperationEngineException
     */
    protected UpdateOperation(Map<String, Object> args)
            throws OperationEngineException {
        super(args);
        this.type = "update";
    }

    /**
     * Transforms this update to include the effect of an update.
     * 
     * @param op
     *            Update to include in this op
     * @return This instance
     */
    public Operation transformWithUpdate(Operation op) {
        if ((op.position != this.position) || (!op.key.equals(this.key))) {
            return this;
        }

        if (this.siteId > op.siteId) {
            this.value = op.value;
        } else if ((this.siteId == op.siteId) && (this.seqId < op.seqId)) {
            this.value = op.value;
        }
        return this;
    }

    /**
     * Transforms this update to include the effect of an insert.
     * 
     * @param op
     *            Insert to include in this op
     * @return This instance
     */
    public Operation transformWithInsert(Operation op) {
        if (!this.key.equals(op.key)) {
            return this;
        }
        if (this.position >= op.position) {
            this.position += op.value.length();
        }
        return this;
    }

    /**
     * Transforms this update to include the effect of a delete.
     * 
     * @param op
     *            Delete to include in this op
     * @return This instance
     */
    public Operation transformWithDelete(Operation op) {
        if (!this.key.equals(op.key)) {
            return this;
        }
        if (this.position > op.position) {
            this.position -= op.value.length();
        } else if (this.position == op.position) {
            return null;
        }
        return this;
    }

}
