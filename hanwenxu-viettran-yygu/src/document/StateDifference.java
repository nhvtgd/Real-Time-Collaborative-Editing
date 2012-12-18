package document;

import java.util.Vector;

/**
 * This class is a way to store the differences in state for a collection of
 * vectors. With this, it is easier to determine causality between operations,
 * and allow the recursive algorithm in the engine to determine how far back in
 * the history buffer we need to check to transform a received operation with.
 * 
 * Thread safety argument: This function should always be thread safe since this
 * code is only accessed on a local machine, and no new threads are created.
 * Since no new threads are created, and no other process access shared memory,
 * then there should be thread safety.
 * 
 * Design: We initially stored the variables in a ArrayList, but then we found
 * out that Vectors had much better runtime for our procedures, since we are
 * only adding to the end of our datatype. This will help in the user-happiness as they are
 * editing the GUI, and have less local latency.
 * 
 * @author Hanwen Xu
 * 
 */
public class StateDifference {

    /**
     * This vector will store the clientIDS
     */
    public Vector<Integer> clients;
    
    /**
     * This will the respective sequenceID of the operations
     */
    public Vector<Integer> sequenceID;

    /**
     * This is the constructor for the context difference. We need to have two
     * vectors, one for the sites and one for the sequenceID. clients: the
     * site/client number sequenceID: the number of operations that is different
     * 
     * both data types will be stored in a vector, adding the vector whenever we
     * have more sites. The sequenceID pos will correspond to its site.
     */
    public StateDifference() {
        this.clients = new Vector<Integer>();
        this.sequenceID = new Vector<Integer>();
    }

    /**
     * This command will be called when we want to add a range of differences
     * for a client. This will be useful in the _transform function called in
     * the OperationEngine
     * 
     * @param client
     *            Integer site ID, has to be corresponding to a client in the
     *            system
     * @param start
     *            First integer operation sequence number, inclusive
     * @param end
     *            Last integer operation sequence number, exclusive
     */
    public void addRange(int client, int begin, int end) {
        for (int i = begin; i < end; i++) {
            this.addSiteSeq(client, i);
        }
    }

    /**
     * Adds a single operation to the difference. This basically means that
     * client has a different operation at the specified sequence ID.
     * 
     * @param client
     *            Integer site ID
     * @param seqID
     *            Integer sequence number
     */
    public void addSiteSeq(int client, int seqID) {
        this.clients.addElement(client);
        this.sequenceID.addElement(seqID);
    }

    /**
     * We will get all the operations in the HistoryBuffer that are specified in
     * this contextDifference. Since we know the specific way that keys of the
     * operation are constructed, we can recreate the key for a specific
     * operation, and then get it from the history buffer. This will allow us to
     * access the information about this different operation, such as what value
     * it was, and what offset. This is extremely useful in the _transform
     * function found in the operationEngine.
     * 
     * @return Array of keys for HistoryBuffer lookups
     */
    public String[] getHistoryBufferKeys() {
        Vector<String> arr = new Vector<String>();
        for (int i = 0; i < this.clients.size(); i++) {
            String key = Operation.createHistoryKey(this.clients.elementAt(i),
                    this.sequenceID.elementAt(i));
            arr.addElement(key);
        }

        String[] strArr = new String[arr.size()];
        return arr.toArray(strArr);
    }

}
