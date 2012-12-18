package document;

import java.util.Queue;

/**
 * This class will allow the client or server to call OTTransform and then to modify an operation.  This might
 * be scrapped in the future.
 * @author Hanwen Xu
 *
 */
public class OTAlgorithm {

    /**
     * This will only be called by the Client type programs
     * @return Queue new queue of transformed operations
     */
    public Queue clientTransform(){
        throw new RuntimeException("not implemented");
    }
    
    /**
     * This will only be called by the server type programs
     * @return Queue new queue of transformed operations
     */
    public Queue serverTransform(){
        throw new RuntimeException("not implemented");
    }

}
