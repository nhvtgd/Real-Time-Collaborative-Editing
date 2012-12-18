package document;

import java.io.IOException;
import java.net.Socket;

public interface CollabInterface {
    
    ConcurrentBuffer document = null;

    
    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket
     *            socket where the client is connected
     * @throws IOException
     *             if connection has an error or terminates unexpectedly
     */
    void handleConnection(Socket socket);
    
    /**
     * handler for client input
     * 
     * make requested mutations on game state if applicable, then return
     * appropriate message to the user.
     * 
     * @param input
     * @return
     */
    String handleRequest(String input);
    
    /**
     * Modifies the private document by inserting string S at offset pos
     * @param pos, integer, requires to be 0<=pos<=length
     * @param s string, requires String to have ascii character
     */
    void insert(int pos, String s);
    
    /**
     * modifies the private document by deleting size characters from offset pos
     * @param pos, see above requirements
     * @param size, requires 0<=size<=length
     */
    void delete(int pos, int size);
    
    /**
     * converts the private document into a string.  The GUI will call this
     * for the client
     */
    public String toString();

}
