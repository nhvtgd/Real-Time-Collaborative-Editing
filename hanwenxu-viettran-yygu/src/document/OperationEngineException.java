package document;

/**
 * This is a new exception type I am going to use for the 
 * operationEngine.  This will be thrown whenever an error is encountered in the engine
 */
public class OperationEngineException extends Exception{

    /**
     * @param args
     */
    public OperationEngineException(String string) {
        // TODO Auto-generated method stub
        super(string);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
}
