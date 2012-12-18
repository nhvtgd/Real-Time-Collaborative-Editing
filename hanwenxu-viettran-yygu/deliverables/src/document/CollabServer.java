package document;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import ui.ServerGui;

/**
 * 
 * Creates a server that will store the main copy of the document. It will block
 * until a client connects, and then send a copy of the document to the client. 
 * It will start a new thread to handle each new client connection. 
 * Each time a client makes a change to the document, it will send it to the 
 * server, which will process the change using the operational transform 
 * algorithm, and then relay it to the rest of the clients.
 * 
 * Thread safety: The server will be made to be thread-safe. The methods
 * that handle requests from the client will be synchronized, and 
 * all datatypes will be made immutable and thread=safe to ensure that 
 * concurrent edits are implemented correctly. The associated GUI will 
 * also be threadsafe.
 * 
 * Rep invariant: port, IP, gui is not none
 * 
 * Testing strategy: See ServerGui.java for the GUI tests. The server contains a copy of the 
 * document stored as a GapBuffer type. We can test concurrent edits by generating 
 * multiple insert and delete operations (via multiple threads) and seeing whether the final document
 * state is what we expected. This simulates commands passed by various connected clients.
 * At this point, we only need to test insert and delete operations.
 * These commands would be added to the server queue, and then processed 
 * using the operational transform algorithm we will soon implement.
 * In addition, we need to make sure that the commands are then 
 * relayed to the clients. All of this will eventually be done as JUnit tests. We will 
 * design methods that are testable without socket connections. 
 * 
 * 
 * To ensure connectivity between server and clients, we will mostly use manual tests to 
 * make sure connections are established as planned.
 * 
 * Passing these tests ensure that the client is able to handle different edits coming 
 * from its connected clients, correctly update the document to reflect those edits, 
 * and successfully send these changes to all other clients.
 * 
 * 
 */
public class CollabServer {

    private final int MAX_CLIENTS = 100;
    Object lock = new Object();
    private ServerSocket serverSocket;
    private int users = 0;
    private GapBuffer document;
    private BlockingQueue<Map<String,List<String>>> commandQueue;
    
    private ServerGui gui;
    
    private int port = 0;
    private String ip = "";
    
    // Keep track of MAX_CLIENTS clients
    private ArrayList<Socket> clientSockets = new ArrayList<Socket>();
    
    private ArrayList<String> usernames = new ArrayList<String>();

    /**
     * Constructor for making a server. It will set the port number, 
     * create a server socket, and generate a central GUI.
     * 
     * @param port - port number
     * @param IP - ip address
     * 
     */
    public CollabServer(String IP, int port) throws IOException {
    	//Take the IP as name for the server
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.gui = new ServerGui(IP);
    }
    

    /**
     * Run the server, listening for client connections and handling them. Never
     * returns unless an exception is thrown. Creates a new thread for every new
     * connection.
     * 
     * @throws IOException
     *             if the main server socket is broken (IOExceptions from
     *             individual clients do *not* terminate serve()).
     */
    public void serve() throws IOException {
        JFrame frame = new JFrame("Collab Edit Demo");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add content to the window.
        frame.add(this.gui);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
        
        while (true) {
            // block until a client connects
            System.out.println("Blocking with port: " + this.port);
            final Socket socket = serverSocket.accept();
            System.out.println("Client connected");
            // handle the client
            
            synchronized(lock) {
                // keeps track of all the clients
                clientSockets.add(socket);
                this.users++;
                usernames.add("User" + users);
                this.gui.updateUsers(usernames.toArray());
            }
            
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        handleConnection(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            synchronized(lock) {
                                clientSockets.remove(socket);
                                socket.close();
                            }
                            
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * This is where various information passing will be done between the 
     * client and server.
     * 
     * @param socket
     *            socket where the client is connected
     * @throws IOException
     *             if connection has an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());        
        
        out.writeObject(this.gui.getText());
        out.flush();
        
        try {
            //fill in later
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.users--;
            usernames.remove(this.users+"");
            out.close();
            in.close();
        }
    }
    
    /**
     * Modifies the private document by inserting string S at offset pos
     * @param pos, integer, requires to be 0<=pos<=length
     * @param s string, requires String to have ascii character
     */
    private void insert(int pos, String s){
        document.insert(pos, s);
    }
    
    /**
     * modifies the private document by deleting size characters from offset pos
     * @param pos, see above requirements
     * @param size, requires 0<=size<=length
     */
    private void delete(int pos, int size){
        document.delete(pos, size);
    }
    
    /**
     * After a change by a client is received and the change is reflected in the 
     * server, this method will be called to send the changes to all the other clients, 
     * who will then apply their own OT algorithm to generate the most recent copy 
     * of the document.
     * 
     * @throws IOException - if the input/output streams cannot be initiated
     */
    private void notifyClients() throws IOException {
        for (Socket i: clientSockets) {
            ObjectInputStream in = new ObjectInputStream(i.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(i.getOutputStream());   
            throw new UnsupportedOperationException();
        }
        
    }
}