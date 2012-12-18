package document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This class will serve as a storage mechanism for all of the  ClientStates.
 * This will make it easier to identify causality, and pull the historical
 * operations from the historybuffer. This is used in the OperationEngine in the
 * _transform algorithm to see what operations have been done since the remote
 * operation was received.
 * 
 * Thread safety argument: Since this will not create any new threads, and since
 * it is accessed by only one processor, there should be no conflict,
 * deadlocking, or any other concurrency issues. This is only used at a local
 * processing server or client.
 * 
 * @author Hanwen Xu
 * 
 */
public class ClientStateTable {

    /**
     * We will store the  ClientStates in an ArrayList. This is because we
     * often have to access and modify random positions of the table. Therefore,
     * ArrayList performed better in runtime speeds.
     */
    private ArrayList<ClientState> cvt;

    /**
     * This is the constructor for the ClientStateTable. We will use this to
     * store a table of ClientState, or states, associated with each client in
     * the network
     * 
     * @param cv
     *            , this is the  ClientState we are starting out with
     * @param client
     *            , the first client to be added to the table
     * @throws OperationEngineException
     */
    public ClientStateTable(ClientState cv, int client)
            throws OperationEngineException {
        this.cvt = new ArrayList<ClientState>();
        this.growTo(client + 1);
        this.cvt.set(client, cv);
    }

    /**
     * This is just an easy way to represent the cvt in a easily testable
     * format.
     * 
     * @return {String} All ClientStates in the table (for Junit testing)
     */
    @Override
    public String toString() {
        String[] values = new String[this.cvt.size()];
        int l = this.cvt.size();
        for (int i = 0; i < l; i++) {
            ClientState cv = this.cvt.get(i);
            values[i] = cv.toString();
        }

        return Arrays.toString(values);
    }

    /**
     * This will allow us to see which clients have equivalent ClientStates
     * stored in the table. This will allow us to see in what type of 
     * each operation was performed in.
     * 
     * Added the integer parameter skip, to allow us to skip clients we don't
     * need, such as the current local client.
     * 
     * @param cv
     *             ClientState we are checking with
     * @param skip
     *            Integer index to skip
     * @return Clients in an integer array
     */
    public int[] getMatchingClients(ClientState cv, int skip) {

        ArrayList<Integer> equiv = new ArrayList<Integer>();

        int l = this.cvt.size();

        for (int i = 0; i < l; i++) {
            if (i != skip && this.cvt.get(i).equals(cv)) {
                equiv.add(new Integer(i));
            }
        }

        int[] clients = new int[equiv.size()];
        int index = 0;
        for (Integer i : equiv) {
            clients[index] = i.intValue();
            index++;
        }

        return clients;
    }

    /**
     * Increases the size of all  ClientStates in the table to the given size,
     * since they must have a position for all the clients, and increases the
     * current ClientStateTable size All initial sequences are set to 0.
     * 
     * @param finalSize
     *            Table size
     * @throws OperationEngineException
     */
    public void growTo(int finalSize) throws OperationEngineException {
        int l = cvt.size();

        for (int i = 0; i < l; i++) {
            this.cvt.get(i).growTo(finalSize);
        }

        for (int j = l; j < finalSize; j++) {
            HashMap<String, Object> properties = new HashMap<String, Object>();
            properties.put("count", finalSize);
            ClientState cv = new ClientState(properties);
            this.cvt.add(cv);
        }
    }

    /**
     * Provides a public copy of the current state. This will allow us to copy
     * information about the ClientStateTable without mutating it.
     * 
     * Also useful for serializing and transmitting over an internet protocol.
     * 
     * @return Array of  ClientStates in a nested array
     */
    public int[][] getState() {
        int l = this.cvt.size();
        int[][] state = new int[l][];

        for (int i = 0; i < l; i++) {
            ClientState cv = this.cvt.get(i);
            state[i] = cv.getState();
        }
        return state;
    }

    /**
     * We will use this method to reset a ClientStateTable. The input will the
     * state of another table.
     * 
     * @param state
     *            Array in the format returned by getState, allowing us to copy
     *            another table.
     * @throws OperationEngineException
     */
    public void setState(int[][] state) throws OperationEngineException {

        this.cvt = new ArrayList<ClientState>(state.length);

        for (int i = 0; i < state.length; i++) {
            HashMap<String, Object> properties = new HashMap<String, Object>();
            properties.put("state", state[i]);
            // it is annoying to have to construct ClientStates with maps.
            this.cvt.add(new ClientState(properties));
        }

    }

    /**
     * Attempts to t
     * 
     * @param client
     *            Integer client ID
     * @throws OperationEngineException
     * @return  ClientState for the given client
     */
    public ClientState getClientState(int client)
            throws OperationEngineException {
        if (this.cvt.size() <= client) {
            this.growTo(client + 1);
        }
        return this.cvt.get(client);
    }

    /**
     * Sets the  ClientState for the given client. Grows the table if it does
     * not include the client yet.
     * 
     * @param client
     *            Integer client ID, if client<0, do nothing.
     * @param cv
     *             ClientState instance
     * @throws OperationEngineException
     */
    public void setClientState(int client, ClientState cv)
            throws OperationEngineException {
        if (client >= 0) {
            if (this.cvt.size() <= client) {
                this.growTo(client + 1);
            }
            if (cv.getSize() <= client) {
                cv.growTo(client + 1);
            }
            this.cvt.set(client, cv);
        }
    }

    /**
     * Updates the table with a new operation
     * 
     * @param op
     *            Operation with the client ID and  ClientState
     * @throws OperationEngineException
     */
    public void operationUpdate(Operation op) throws OperationEngineException {
        ClientState cv = op.getClientState().copy();
        cv.setSeqForClient(op.siteId, op.seqId);
        this.setClientState(op.siteId, cv);
    }

    /**
     * gets the smallest context ClientState, i.e. the one with the least amount of processed operations. 
     * 
     * @throws OperationEngineException
     * 
     * @return Minimum context ClientState, return null if this cvt is null or if the
     *         size is null.
     */
    public ClientState getMinimumClientState()
            throws OperationEngineException {
        if (this.cvt == null || this.cvt.size() == 0) {
            return null;
        }

        ClientState mcv = this.cvt.get(0).copy();
        int l = this.cvt.size();

        for (int i = 1; i < l; i++) {
            ClientState cv = this.cvt.get(i);
            for (int client = 0; client < l; client++) {
                int seq = cv.getSeqForClient(client);
                int min = mcv.getSeqForClient(client);
                if (seq < min) {
                    mcv.setSeqForClient(client, seq);
                }
            }
        }
        return mcv;
    }
}
