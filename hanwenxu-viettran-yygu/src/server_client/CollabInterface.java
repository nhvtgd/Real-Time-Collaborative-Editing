package server_client;

import java.io.IOException;

import document.Operation;

/**
 * The interface for the CollabClient and CollabServer.
 * @author youyanggu
 *
 */
public interface CollabInterface { 
    
    /**
     * Converts the private document into a string.  The GUI will call this
     * for the client
     * @return the string version of the model
     */
    public String toString();
    
    /**
     * This will return the unique integer id of this client or server.  Integer ID
     * must be incrementing integers starting at 0 for the server, 1, 2, 3, ..etc. for the clients
     * @return integer siteID
     */
    public int getID();
    
    /**
     * For the client, this will take the operation that was performed locally, and transmit it
     * to the server.
     * 
     * For the server, this will take the operation that was sent from a client, and update its 
     * own model.
     * @param op
     * @throws IOException 
     */
    public void transmit(Operation op) throws IOException;

    /**
     * Updates the copy of the document using operational transform
     * through a call to the CollabModel's remoteInsert/remoteDelete
     * @param o - the operation that was received to apply to the model
     */
    public void updateDoc(Operation o);


}
