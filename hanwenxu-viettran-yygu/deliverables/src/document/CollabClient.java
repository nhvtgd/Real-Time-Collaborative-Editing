package document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import ui.ClientGui;
import ui.ServerGui;

/**
 * This is the client that will connect to a server. A unique instance of the client 
 * will be created each time a user opens up a client. It will receive a copy of 
 * the central document from the server. Changes to the document on this particular 
 * client will be sent to the server. Changes to the document by other clients 
 * will be relayed by the server, and this client will apply an OT algorithm
 * to correctly update its own document. All of this will be reflected on the 
 * GUI.
 * 
 * Thread safety argument: Only the server that the client is connected to will 
 * access this client, so we do not have an issue with mutliple threads trying to 
 * access the same data. 
 * 
 * Rep invariant; ip, port, gui is not null
 * 
 * Testing strategy: GUI testing strategies is detailed in ClientGUI.java.
 * The GUI will be used to test when a user types text, making sure that 
 * the commands the client generates are appropriate.
 * 
 * We will design methods that are testable without socket connections. 
 * We can use automated JUnit tests to simulate commands passed by the server and 
 * ensure that the client correctly updates its document to reflect the changes. 
 * The commands should be added to the queue and then parsed one at a time, using 
 * the operational transform algorithm to correctly update the document in a 
 * timely manner. At this point, we only need to test insert and delete operations.
 * 
 * While the client is processing commands from the server, we can change the state 
 * of the document to simulate a user editing the document. The client should still 
 * correctly update the document, taking into account the effect that the user 
 * generates as he/she is editing.
 * 
 * Passing these tests ensure that our client is able to display the latest version 
 * of the document while a user is adding new text.
 * 
 * 
 * 
 */
public class CollabClient implements CollabInterface {
    
    private GapBuffer document;
    private BlockingQueue<Map<String,List<String>>> commandQueue;
    
    int port;
    String ip;
    
    private ClientGui gui;
    
    /**
     * Constructor to start the client.
     * @param IP - the ip address of the host to connect to
     * @param port - the port number of the host to connect to
     */
    public CollabClient(String IP, int port) {
        this.ip = IP;
        this.port = port;
    }
    
    /**
     * Starts the client and try to connec to the server with the paramters given
     * in the constructor. It success, it will continue a connection with the server 
     * until an exception is thrown. A GUI is created that will hopefully reflect the 
     * current state of the document. Various message passing will be used to send 
     * insert/delete updates. The document will hopefully be updated with edits made 
     * by both the client itself and other clients, relayed by the server.
     * 
     * @throws IOException
     */
    public void connect() throws IOException {
        Socket s = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            System.out.println("Trying to connect with port: " + this.port + " " + this.ip);
            InetAddress address = InetAddress.getByName(ip);
            s = new Socket(address, port);
            out = new ObjectOutputStream(s.getOutputStream());
            in = new ObjectInputStream(s.getInputStream());
            try {
                Object o = in.readObject();
                if (o instanceof String) {
                    System.out.println((String) o);
                    this.gui = new ClientGui((String) o);
                    JFrame frame = new JFrame("Collab Edit Demo");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    // Add content to the window.
                    frame.add(this.gui);

                    // Display the window.
                    frame.pack();
                    frame.setVisible(true);
                }
                
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.");

        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: taranis.");
            
        }
    }
    
    /**
     * These methods will be implemented later, and will handle all the 
     * editing operations. These methods will send commands to the server that 
     * it is connected to.
     */
    @Override
    public void handleConnection(Socket socket) {
        // TODO Auto-generated method stub

    }

    @Override
    public String handleRequest(String input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void insert(int pos, String s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(int pos, int size) {
        // TODO Auto-generated method stub

    }



}
