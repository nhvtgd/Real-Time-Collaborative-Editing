package server_client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.text.BadLocationException;

import document.ClientState;
import document.DeleteOperation;
import document.InsertOperation;
import document.Operation;
import document.OperationEngineException;
import document.Pair;
import document.UpdateOperation;

import model.CollabModel;
import ui.ClientGui;
import ui.DocumentSelectionPage;
import ui.ErrorDialog;


/* Thread safety argument: This is a single-threaded class that communicates with 
 * a single server via a socket connection. Multiple threads will not have 
 * access to the client's datatypes at any point of the program, so this 
 * class is thread-safe by default.
 * 
 * Rep invariant; ip, port, gui is not null
 * 
 * Testing strategy: GUI testing strategies is detailed in ClientGUI.java. The
 * GUI will be used to test when a user types text, making sure that the
 * commands the client generates are appropriate.
 * 
 * We will design methods that are testable without socket connections. We 
 * used automated JUnit tests to simulate commands passed by the server and
 * ensure that the client correctly updates its document to reflect the changes.
 * All commands involving the operational transform algorithm are tested 
 * independently in OperationTest.java and OperationEngineTest.java to make sure 
 * operations correctly update the document in a timely manner.
 */ 

/**
 * This is the client that will connect to a server. A unique instance of the
 * client will be created each time a user opens up a client. It will receive a
 * copy of the central document from the server. Changes to the document on this
 * particular client will be sent to the server. Changes to the document by
 * other clients will be relayed by the server, and this client will apply an OT
 * algorithm to correctly update its own document. All of this will be reflected
 * on the GUI.
 * 
 * 
 * Passing these tests ensure that our client is able to display the latest
 * version of the document while a user is adding new text.
 * 
 * @author youyanggu
 * 
 */
public class CollabClient implements CollabInterface {
    /** Timeout on socket connection attempts */
    private static final int TIMEOUT = 2000;
    /** unique to each client. Used to differentiate operations */
	private int siteID;
	/** document the client is editing */
	private String document = "";
	
	/** port number of client */
	private int port;
	/** ip number of client */
	private String ip;
	/** username of client */
	private String name;
	
	/** label to display at the top of the document GUI */
	protected String label = "Client";	
	/** Intializes socket and streams to null */
	private Socket s = null;
	
	/** outputstream to send objects to server */
	protected ObjectOutputStream out = null;
	/** inputstream to receive objects from server */
	protected ObjectInputStream in = null;
	/** client GUI used to display the document */
	protected ClientGui gui;

	/**
	 * Constructor to start the client. Simply sets the identifiers. 
	 * The connection to the server come later in the connect() call.
	 * 
	 * @param IP - the ip address of the host to connect to
	 * @param port - the port number of the host to connect to
	 * @param name - the name alias given to the client
	 */
	public CollabClient(String IP, int port, String name) {
		this.ip = IP;
		this.port = port;
		this.name = name;
	}

	/**
	 * Starts the client and try to connec to the server with the paramters
	 * given in the constructor. It success, it will continue a connection with
	 * the server until an exception is thrown. A GUI is created that will
	 * hopefully reflect the current state of the document. Various message
	 * passing will be used to send insert/delete updates. The document will
	 * hopefully be updated with edits made by both the client itself and other
	 * clients, relayed by the server.
	 * 
	 * @throws IOException if the socket is broken or corrupted
	 * @throws OperationEngineException if the operation caused an exception when being processed by
     * the operation engine
	 */
	public void connect() throws IOException {
		
	    // Establishes a socket connection
		System.out.println("Connecting to port: " + this.port + " at: " + this.ip);
		InetSocketAddress address = new InetSocketAddress(this.ip, this.port);
		try {
		    s = new Socket();
		    s.connect(address, TIMEOUT);
		} catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + this.name);
            return;
        } catch (IOException e) {
		    System.err.println("Timeout: cannot connect to IP: " + this.ip + " , port: " + this.port + ". Please try again.");
		    return;
		} catch (Exception e) {
		    System.err.println("Your port number (" + this.port + ") or IP (" + this.ip + ") is incorrect. Please try again");
		    return;
		}
		
		// Connection established. Communicates with server
		try {
			out = new ObjectOutputStream(s.getOutputStream());
			in = new ObjectInputStream(s.getInputStream());
		
			Object o= in.readObject();

			if(!(o instanceof ArrayList<?>)) {
                throw new RuntimeException("Expected ArrayList of documents");                
            }
			
			// Popup for user to select document. We already checked above for object type
            @SuppressWarnings("unchecked")
            JFrame f = new DocumentSelectionPage((ArrayList<String>) o,this);
			
            // Waits until user selects document to edit. Calls setDocument() on return
            // to set the document to edit
		    while (f.isVisible()) {
		        try {
		            Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
		    }
		    
		    // Sends to server the document this client wants to edit
			out.writeObject(document);
			out.flush();
			
			// Reads in operations from the server
			o = in.readObject();			
			while (o != null) {
			    parseInput(o);				
				o = in.readObject();
				
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
		    // Close connection
		    s.close();
			out.close();
			in.close();
			System.exit(0);

		}
	}

	/**
	 * Takes an object that was received from the server and parses it according 
	 * to its type. Performs the appropriate calls and updates based on the object received
	 * @param o - object sent from the server to parse
	 * @throws IOException - caused if the operation object is corrupt, or if the socket connection breaks
	 * @throws OperationEngineException - if the operation caused an exception when being processed by
	 * the operation engine
	 */
	@SuppressWarnings("unchecked")
    public void parseInput(Object o) throws IOException {	    
	    if (o instanceof Operation) {
	        // We received an operation from the server. Update the local document
            updateDoc((Operation) o);
        } else if (o instanceof Integer) {
            // The server is sending the unique client identifiers
            this.siteID = ((Integer) o).intValue();
            if (this.name.equals("Anonymous")) 
                this.name += "" + this.siteID;
            out.writeObject(this.name);
            out.flush();
            label = this.name + " is editing document: " + this.document;

        } else if (o instanceof String) {
            // The server just sent the initial string of the document
            // Start up the GUI with this string
            try {
                this.gui = new ClientGui((String) o, this, label);
            } catch (OperationEngineException e) {
                e.printStackTrace();
            }
            this.gui.setModelKey(document);

            JFrame frame = new JFrame("Collab Edit Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // Add content to the JFrame window.
            frame.add(this.gui);
            // Display the window.
            frame.pack();
            frame.setVisible(true);
        } else if (o instanceof ClientState) {
            // Updates the ContextVector of the GUI with the one sent by the server
            CollabModel collab = this.gui.getCollabModel();
            collab.setCV((ClientState) o);
        } else if (o instanceof Pair<?, ?>) {
            // Updates list of current users and documetns
            ArrayList<String> users = ((Pair<ArrayList<String>, ArrayList<String>>) o).first;
            ArrayList<String> documents = ((Pair<ArrayList<String>, ArrayList<String>>) o).second;
            this.gui.updateUsers(users.toArray());
            this.gui.updateDocumentsList(documents.toArray()); 
        } else {
            throw new RuntimeException("Unrecognized object type received by client");
        }
	}
	
	/**
	 * Updates the client's copy of the document using operational transform
	 * through a call to the CollabModel's remoteInsert/remoteDelete
	 * @param o - the operation that was received from the server to 
	 * apply to the model
	 */
	@Override
	public void updateDoc(Operation o) {
		try {
			if (o instanceof InsertOperation) {
				this.gui.getCollabModel().remoteInsert((Operation) o);
			} else if (o instanceof DeleteOperation) {
				this.gui.getCollabModel().remoteDelete((Operation) o);
			} else if (o instanceof UpdateOperation) {
				throw new UnsupportedOperationException();
			} else {
				throw new RuntimeException("Shouldn't reach here");
			}
		} catch (OperationEngineException e) {
			new ErrorDialog(e.toString());
		} catch (BadLocationException e) {
			new ErrorDialog(e.toString());
		}
	}

	/**
	 * @return the siteID of the document
	 */
	@Override
	public int getID() {
		return siteID;
	}

	/**
	 * Transmits local changes to the server via an operatoin
	 * @param the operation to transmit to server
	 * @throws IOException if the OutputStream is corrupted or broken
	 */
	@Override
	public void transmit(Operation o) throws IOException {
		if (out == null)
			throw new RuntimeException("Socket not initialized.");
		out.writeObject(o);
		out.flush();
	}

	/**
	 * Used by the document selector popup to set the 
	 * @param new name of the document
	 */
	public void setDocument(String text) {
		document = text;	
	}
	
	/** @return ip address of client */
	public String getIP() {
	    return this.ip;
	}
	
	/** @return port number of client */
	public int getPort() {
	    return this.port;
	}
	
	/** @return username of client */
	public String getUsername() {
	    return this.name;
	}

}
