package document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * This vector will be the representation of our state machine. We shall explain
 * this in more detail:
 * 
 * A data representation of our state machine will be an integer array. e.g:
 * 
 * State machine1 --> [0, 1, 2, 3, 4, 5]
 * 
 * State machine2 --> [1, 2, 3, 4, 5]
 * 
 * The index corresponds to the ClientID. The integer value at that position
 * represents the number of operations processed in this state that are sent
 * from clientID denoted by index position.
 * 
 * Thread safety argument: Since no new threads are created, this class should
 * be thread safe. Also, no new thread or process will need to access shared
 * memory. Therefore, this entire class should be thread safe.
 * 
 * design notes: We initially coded with ArrayLists, but then we tested Vector
 * and int[] arrays in the operationEngine test for speed. We found that int[]
 * had the best overall performance, so we decided to stick with int[].
 * 
 * @author Hanwen Xu
 * 
 */
public class ClientState implements Serializable {

    /**
     * This is the serializable ID number
     */
    private static final long serialVersionUID = 5715462582805781166L;
    
    /**
     * This datatype will store all the information about the state of the location originating
     * this operation  
     */
    private int[] clients;

    /**
     * This is just the standard constructor. We wanted to make the constructor
     * very flexible so that it can take in multiple different types of Maps,
     * whether it is a simple count map, or a mapping of a ClientState. This
     * will allow a ClientState to be even constructed from an Operation, by
     * accessing the operation's variables by utilizing accessors. This will be
     * extremely handy when we implement the _transform function in the
     * OperationEngine.
     * 
     * @param properties
     *            extremely flexible. Have to test various map types as
     *            specified: a map containing a count key a map containing a
     *            ClientState key a map containing a sites key a map
     *            containing a state key
     * 
     *            The priority of the key also follows this order. E.g., if it
     *            contains both a count and sites, count takes priority. This is
     *            arbitrary, and has no impact on the algorithm
     * @throws OperationEngineException
     *             catch all, for when the engine doesn't process, or when the
     *             key is not found.
     */
    public ClientState(Map<String, Object> properties)
            throws OperationEngineException {
        if (properties.containsKey("count")) {
            this.clients = new int[((Integer) properties.get("count"))
                    .intValue()];
        } else if (properties.containsKey("contextVector")) {
            this.clients = ((ClientState) properties.get("contextVector"))
                    .copyClients();
        } else if (properties.containsKey("sites")) {
            this.clients = (int[]) properties.get("sites");
        } else if (properties.containsKey("state")) {
            this.clients = (int[]) properties.get("state");
        } else {
            throw new OperationEngineException("uninitialized context vector");
        }
    }

    /**
     * Provides a public copy of the current state. 
     * 
     * Also serializes this ClientState. Also returns a copy, but useful for
     * transmitting between client/server across an internet protocol.
     * 
     * design comment: we had some concern that this breaches safety, because it
     * might cause the sites to be accessed by outside code we don't want it to
     * access also, this is the same as getSite().
     * 
     * @return Array of integer sequence numbers
     */
    public int[] getState() {
        return this.clients;
    }

    /**
     * Makes an independent copy of this ClientState.
     * 
     * @throws OperationEngineException
     * 
     * @return Copy of this ClientState
     */
    public ClientState copy() throws OperationEngineException {

        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("contextVector", this);

        return new ClientState(args);
    }

    /**
     * Makes an independent copy of the array in this ClientState. This is
     * useful for sharing ClientState when we are running the _transform
     * function in the OperationEngine. We need to be able to make copies, and
     * modify the ClientState.
     * 
     * @return Copy of this ClientState's sites array
     */
    public int[] copyClients() {
        return Arrays.copyOf(this.clients, this.clients.length);
    }

    /**
     * Increases the size of the ClientState to the given size. Initializes
     * new entries with zeros. This is useful for when a new client joins, we
     * can include that client in our state by growing the array.
     * 
     * @param count
     *            Final size of clients array. IF count <= current length, do
     *            nothing
     */
    public void growTo(int count) {

        int l = this.clients.length;

        if (l < count) {
            int[] newClients = new int[count];
            for (int i = 0; i < count; i++) {
                if (i < l) {
                    newClients[i] = this.clients[i];
                } else {
                    newClients[i] = 0;
                }
            }
            this.clients = newClients;
        }
    }

    /**
     * Gets the sequence number for the given site in this ClientState. Grows
     * the ClientState if it does not include the site yet.
     * 
     * @param client
     *            Integer clientID to grow to. Requires 0<=client. Otherwise,
     *            will throw ArrayOutOfBounds exception
     * @return Integer sequence number for the site
     */
    public int getSeqForClient(int client) {
        if (client < 0) {
            throw new ArrayIndexOutOfBoundsException("inproper client input");
        }
        if (this.clients.length <= client) {
            this.growTo(client + 1);
        }
        return this.clients[client];
    }

    /**
     * Sets the sequence number for the given site in this ClientState. Grows
     * the ClientState if it does not include the site yet.
     * 
     * @param client
     *            Integer client ID Requires 0<=client. Otherwise, will throw
     *            ArrayOutOfBounds exception
     * @param seq
     *            Integer sequence number
     */
    public void setSeqForClient(int client, int seq) {
        if (client < 0) {
            throw new ArrayIndexOutOfBoundsException("inproper client input");
        }
        if (this.clients.length <= client) {
            this.growTo(client + 1);
        }
        this.clients[client] = seq;
    }

    /**
     * Gets the size of this ClientState.
     * 
     * @return Integer size
     */
    public int getSize() {
        return this.clients.length;
    }

    /**
     * Computes the difference in sequence numbers at each site between this
     * ClientState and the one provided. This will be utilized heavily in the
     * transform algorithm to determine the offset.
     * 
     * This will not be absolute difference. only difference if this one has
     * more operations than the other state. Otherwise, return an empty
     * StateDifference
     * 
     * @param cv
     *            Other context ClientState object
     * @return A StateDifference object representing what operations are
     *         different between these two states
     */
    public StateDifference subtract(ClientState cv) {
        StateDifference sd = new StateDifference();
        for (int i = 0; i < this.clients.length; i++) {
            int a = this.getSeqForClient(i);
            int b = cv.getSeqForClient(i);
            if (a - b > 0) {
                sd.addRange(i, b + 1, a + 1);
            }
        }
        return sd;
    }

    /**
     * This will return a StateDifference that will point out the oldest
     * operation difference between these two states. If there is no difference
     * then it will return an empty stateDifference
     * 
     * @param cv
     *            Other ClientState object
     * @return Represents the oldest difference for each site between this
     *         ClientState and the one passed
     */
    public StateDifference oldestDifference(ClientState cv) {
        StateDifference sd = new StateDifference();
        for (int i = 0; i < this.clients.length; i++) {
            int a = this.getSeqForClient(i);
            int b = cv.getSeqForClient(i);
            if (a - b > 0) {
                sd.addSiteSeq(i, b + 1);
            }
        }
        return sd;
    }

    /**
     * This will be a boolean function to see if the ClientState values contain
     * the same sequence IDs.  Unindexed values will be treated as 0, since we
     * can always growTo.  
     * 
     * @param cv
     *            Other ClientState
     * @return True if equal, false if not
     */
    public boolean equals(ClientState cv) {
        int[] a = this.clients;
        int[] b = cv.clients;

        // account for different size ClientStates
        int max = Math.max(a.length, b.length);
        for (int i = 0; i < max; i++) {
            int va = 0;
            if (i < a.length) {
                va = a[i];
            }
            int vb = 0;
            if (i < b.length) {
                vb = b[i];
            }
            if (va != vb) {
                return false;
            }
        }
        return true;
    }

    /**
     * This will be a comparator function to see if the ClientState values contain
     * the same sequence IDs.  Unindexed values will be treated as 0, since we
     * can always growTo. 
     * 
     * If a single sequence is less than the other, then this state is occuring
     * before the other state
     * 
     * @param cv
     *            Other ClientState
     * @return -1 if this ClientState is ordered before the other, 0 if they
     *         are equal, or 1 if this ClientState is ordered after the other
     */
    public int compare(ClientState cv) {
        int[] a = this.clients;
        int[] b = cv.clients;
        // acount for different size ClientStates
        int max = Math.max(a.length, b.length);
        for (int i = 0; i < max; i++) {
            int va = 0;
            if (i < a.length) {
                va = a[i];
            }
            int vb = 0;
            if (i < b.length) {
                vb = b[i];
            }
            if (va < vb) {
                return -1;
            } else if (va > vb) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Converts the contents of this ClientState sites array to a string.
     * 
     * @return All integers in the ClientState (for our JUnit testing suite)
     */
    @Override
    public String toString() {
        return Arrays.toString(this.clients);
    }

}
