package server_client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.text.BadLocationException;

import ui.ErrorDialog;
import ui.ServerGui;
import document.DeleteOperation;
import document.InsertOperation;
import document.Operation;
import document.OperationEngineException;
import document.Pair;

/*
 * Thread safety: The server spawns multiple threads. Datatypes that are
 * editable by multiple threads (e.g. list of usernames) are all synchronized so
 * that only one thread may make an edit at any point. This prevents race
 * conditions. It is possible for operations from clients to be received
 * concurrently without locks - that is the entire purpose of the operational
 * transformation algorithm. The server simply relays the operations to the
 * operational transform engine, which will then handle all the concurrency
 * issues. The associated GUI is also made to be threadsafe within its own
 * process. For example, the server GUI is sychronized when it wants to switch
 * document views.
 * 
 * Rep invariant: port, IP, gui is not none
 * 
 * Testing strategy: See ServerGui.java for the GUI tests. We can test concurrent edits
 * by generating multiple insert and delete operations 
 * and seeing whether the final document state is what we expected
 * - this is done in the document package. This
 * simulates commands passed by various connected clients. In addition, we need to make 
 * sure that the commands are then relayed to the clients. This is tested using JUnit tests
 * in CollabServerTest.java. We designed methods with the goal of making the 
 * server testable without socket connections.
 * 
 * To ensure connectivity between server and clients, we also use manual
 * tests to make sure connections are established as planned.
 * 
 * Passing these tests ensure that the client is able to handle different edits
 * coming from its connected clients, correctly update the document to reflect
 * those edits, and successfully send these changes to all other clients.
 */

/**
 * 
 * Creates a server that will store the main copy of each document. It will block
 * until a client connects, and then send a copy of the requested document to the client.
 * It will start a new thread to handle each new client connection. Each time a
 * client makes a change to the document, it will send it to the server, which
 * will process the change using the operational transform algorithm, and then
 * relay it to the rest of the clients.
 * 
 * Note: a server must be present before a client can connect. A server's 
 * document is not editable by the user
 *
 * @author youyanggu
 * 
 */
public class CollabServer implements CollabInterface {
    /** maximum number of clients allowed at a time */
	private static final int MAX_CLIENTS = 30;
    /** default document name */
    private static final String DEFAULT_DOC_NAME = "default";
    /** lock object*/
	Object lock = new Object();
    /** server socket that accepts client connections*/
	private ServerSocket serverSocket;
    /** number of clients actively connected*/
	private int users = 0;
    /** port number of the server*/
	private int port = 0;
    /** IP address of the server */
	private String ip = "";
    /** name of the server */
	private String serverName;
    /** JFrame used by the server GUI for display */
	private JFrame frame;
    /** GUI object for the server */
	private ServerGui displayGui;
    /** order of the operations */
	private int order;
	/** Data structure to keep track of clients */
	private final ArrayList<Pair<Socket, Boolean>> clientSockets = new ArrayList<Pair<Socket, Boolean>>();

	/** A hash of socket to its associated input/output streams. We want to use these
	 * two streams each time we need to communicate between a particular client and the server
	 */
	private final HashMap<Socket, Pair<ObjectInputStream, ObjectOutputStream>> socketStreams = new HashMap<Socket, Pair<ObjectInputStream, ObjectOutputStream>>();

	/** List of all users */
	private final ArrayList<String> usernames = new ArrayList<String>();

	/** List of all documents */
	private final HashMap<String, ServerGui> documents = new HashMap<String, ServerGui>();

	/**
	 * Constructor for making a server. It will set the port number, create a
	 * server socket, and generate a central GUI.
	 * List of documents and socket names are also initialized
	 *  
	 * @param port
	 *            - targeted port number
	 * @param IP
	 *            - targeted IP address
	 * 
	 */
	public CollabServer(String IP, int port, String name) {
		// Sets server info
		this.port = port;
		this.serverName = DEFAULT_DOC_NAME;
		this.ip = IP;
		// Create a server socket for clients to connect to
		try {
			this.serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			System.err.println("Cannot create server socket at IP: " + this.ip
					+ ", port: " + this.port);
			return;
		}
		// Add default document to document list
		try {
			documents.put(DEFAULT_DOC_NAME,
					new ServerGui(this, this.serverName));
		} catch (OperationEngineException e) {
			e.printStackTrace();
		}
		// Update document list, set the current display GUI to be the default document
		this.displayGui = documents.get(DEFAULT_DOC_NAME);
		ArrayList<String> docs = new ArrayList<String>();
		docs.addAll(documents.keySet());
		this.displayGui.updateDocumentsList(docs.toArray());

		// The server is viewed as the zeroth socket
		clientSockets.add(null);
		System.out.println("Server created.");
	}

	/**
	 * Run the server, listening for client connections and handling them.
	 * Starts up a non-editable GUI dedicated to handling client connections.
	 * Never returns unless an exception is thrown. Creates a new thread for
	 * every new connection.
	 * 
	 * @throws IOException
	 *             if the main server socket is broken (IOExceptions from
	 *             individual clients do *not* terminate serve()).
	 */
	public void serve() throws IOException {
		frame = new JFrame("Collab Edit Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Add content to the window.
		frame.add(this.displayGui);

		// Display the window.
		frame.pack();
		frame.setVisible(true);

		while (true) {
			if (this.users > MAX_CLIENTS) {
				throw new RuntimeException("Too many clients!");
			}
			// block until a client connects
			try {
				final Socket socket = serverSocket.accept();

				// New client detected; create new thread to handle each connection
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							handleConnection(socket);
						} catch (IOException e) {

						} finally {
							try {
								socket.close();
							} catch (IOException e) {
								new ErrorDialog(e.toString());
							}
						}
					}
				});
				thread.start();
			} catch (Exception e) {
				System.err.println("An exception occured. See below.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handle a single client connection. Returns when client disconnects, after
	 * closing all the appropriate sockets and streams. This is where various
	 * information passing will be done between the client and server.
	 * 
	 * @param socket
	 *            socket via which the client is connected
	 * @throws IOException
	 *             if connection has an error or terminates unexpectedly
	 */
	public void handleConnection(Socket socket) throws IOException {
		int clientID = -1;
		String documentID = "";
		String clientName = "";
		ObjectOutputStream out = new ObjectOutputStream(
				socket.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

		// Add stream to HashMap
		socketStreams.put(socket,
				new Pair<ObjectInputStream, ObjectOutputStream>(in, out));

		try {
			// Sends list of documents to client
			ArrayList<String> temp = new ArrayList<String>();
			temp.addAll(documents.keySet());
			out.writeObject(temp);
			out.flush();

			// Receives which document to edit
			Object input = in.readObject();
			if (!(input instanceof String)) {
				throw new RuntimeException("Expected document name");
			}

			synchronized (lock) {
				// If document does not exist, create it
				documentID = (String) input;
				if (!documents.containsKey(documentID)) {
					ServerGui newGui = new ServerGui(this, documentID);
					newGui.setModelKey(documentID);
					documents.put(documentID, newGui);
				}				
				this.users++;
				// Add to ArrayList of sockets
				clientSockets.add(new Pair<Socket, Boolean>(socket, true));
				// sets client ID
				clientID = clientSockets.size() - 1;
			}
			if (!documents.containsKey(documentID)) {
				throw new RuntimeException("Missing document ID in map");
			}
			// Sends to client the client ID
			out.writeObject(clientID);
			out.flush();
			System.out.println("Client #" + clientID + " connected");
			// Sends to client the initial String in the document
			out.writeObject(documents.get(documentID).getText());
			out.flush();
			// Sends to client the ContextVector of the document model
			try {
				out.writeObject(documents.get(documentID).getCollabModel()
						.copyOfCV());
			} catch (OperationEngineException e1) {
				e1.printStackTrace();
			}
			out.flush();

			// Receives username of client. Updates users.
			input = in.readObject();
			if (!(input instanceof String)) {
				throw new RuntimeException("Expected client username");
			}
			clientName = (String) input;
			synchronized (lock) {
				usernames.add(clientName);

			}
			updateUsers();

			// Receives operations from client. That's all the server is
			// expecting from
			// the client from now on.
			input = in.readObject();
			while (input != null) {
				parseInput(input, documentID, clientID);
				input = in.readObject();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (OperationEngineException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// Client closed
			return;
		} finally {
			// Clean up, close connections
			if (clientID == -1)
				return;
			// set connection as closed
			System.out.println("Connection to client #" + clientID + " lost.");
			synchronized (lock) {
				try {
					clientSockets.get(clientID).second = false;
					this.users--;
					usernames.remove(clientName);
					// need to update the view of who still in the edit room
					documents.get(documentID).updateUsers(
							this.usernames.toArray());
				} catch (Exception e) {
					System.err.println("Client not found");
				}
			}
			updateUsers();
			out.close();
			in.close();
		}
	}

	/**
	 * Takes an object that was received from the client and parses it. It
	 * should be an operation that was performed on the client. The server
	 * should then update its corresponding document and notify all clients
	 * 
	 * @param input
	 *            - object sent from the client to parse
	 * @param documentID
	 *            - the document that the client is editing
	 * @param clientID
	 *            - the identification of the client sending the object
	 * @throws IOException
	 *             - caused if the operation object is corrupt, or if the socket
	 *             connection breaks
	 */
	public void parseInput(Object input, String documentID, int clientID)
			throws IOException {
		if (input instanceof Operation) {
			transmit((Operation) input); // also mutates the input
			updateDoc((Operation) input);
		} else
			throw new RuntimeException("Unrecognized object type");
	}

	/**
	 * Updates the copy of the document using operational transform through a
	 * call to the CollabModel's remoteInsert/remoteDelete
	 * 
	 * @param o
	 *            - the operation that was received from the client to apply to
	 *            the model
	 */
	@Override
	public void updateDoc(Operation o) {
		// Gets the document to apply the operation to
		String documentID = o.getKey();
		try {
			if (o instanceof InsertOperation) {
				ServerGui current = documents.get(documentID);
				if (current == null) {
					o.setKey("Insert tested");
					return;
				}
				current.setModelKey(documentID);
				current.getCollabModel().remoteInsert(o);
				current.getTextArea().setEditable(false);
			} else if (o instanceof DeleteOperation) {

				ServerGui current = documents.get(documentID);
				if (current == null) {
					o.setKey("Delete tested");
					return;
				}
				current.setModelKey(documentID);
				current.getCollabModel().remoteDelete(o);
				current.getTextArea().setEditable(false);
			} else {
				throw new RuntimeException("Shouldn't reach here");
			}

		} catch (OperationEngineException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the ID of the server, which is always 0
	 */
	@Override
	public int getID() {
		return 0;
	}

	/**
	 * After a change by a client is received and the change is reflected in the
	 * server, this method will be called to send the changes to all the other
	 * clients, who will then apply their own OT algorithm to generate the most
	 * recent copy of the document.
	 * 
	 * @throws IOException if the input/output stream is corrupt
	 */
	@Override
	public void transmit(Operation o) throws IOException {
		ObjectOutputStream out = null;
		// Increment the order so the Operation Engine can determine 
		// the relative position of all the operations
		synchronized (lock) {
		    o.setOrder(order);
		    order++;
		}

		for (int i = 1; i < clientSockets.size(); i++) {
			Pair<Socket, Boolean> p = clientSockets.get(i);
			Socket currentSocket = p.first;
			Boolean activeSocket = p.second;
			// Connection is already closed, or this is the operation that was
			// originally sent by that client. So we don't send.
			if (!activeSocket || i == o.getSiteId())
				continue;
			// Otherwise we send the operation
			out = socketStreams.get(currentSocket).second;

			out.writeObject(o);
			out.flush();
		}

	}

	/**
	 * Sends a Pair of users and documents to all active clients to update the
	 * right pane. It is called each time a new client joins.
	 * 
	 * @throws IOException
	 *             - if the socket connection is corrupted
	 */
	@SuppressWarnings("unchecked")
	public void updateUsers() throws IOException {
		ObjectOutputStream out = null;
		this.displayGui.updateUsers(((ArrayList<String>) usernames.clone())
				.toArray());
		ArrayList<String> docs = new ArrayList<String>();
		docs.addAll(documents.keySet());
		// Sorts the document list in alphabetical order
		Collections.sort(docs);
		this.displayGui.updateDocumentsList(docs.toArray());

		// For each client
		for (int i = 1; i < clientSockets.size(); i++) {
			// retrieve the sockets for the client
			Pair<Socket, Boolean> p = clientSockets.get(i);
			Socket currentSocket = p.first;
			Boolean activeSocket = p.second;
			// Connection is already closed, So we don't send.
			if (!activeSocket)
				continue;
			// Makes sure socket exists in map
			if (!socketStreams.containsKey(currentSocket))
				throw new RuntimeException("Socket not found in HashMap");
			out = socketStreams.get(currentSocket).second;
			// Creates a cloned pair of usernames and documents
			Pair<ArrayList<String>, ArrayList<String>> pair = new Pair<ArrayList<String>, ArrayList<String>>(
					(ArrayList<String>) usernames.clone(),
					(ArrayList<String>) docs.clone());
			// Sends it to the client
			out.writeObject(pair);
			out.flush();
		}
	}

	/**
	 * Switches between documents to be displayed on the server. This happens
	 * when a user clicks on a document name on the server GUI.
	 * 
	 * @param document
	 *            - the document name to be switched
	 */
	public synchronized void switchScreen(String document) {
		if (!documents.containsKey(document))
			throw new RuntimeException("Document not found!");
		this.displayGui = documents.get(document);
		this.displayGui.updateUsers(this.usernames.toArray());
		ArrayList<String> temp = new ArrayList<String>();
        temp.addAll(this.getDocumentsName());
        // Sorts the document list in alphabetical order
        Collections.sort(temp);       
		this.displayGui.updateDocumentsList(temp.toArray());
		this.displayGui.repaint();
		this.displayGui.revalidate();

		frame.setContentPane(displayGui);
		frame.setVisible(true);

	}

	/**
	 * Get the list of documents currently store in the model
	 * @return the list of documents
	 */
	public Set<String> getDocumentsName() {
		return documents.keySet();
	}

	/** @return ip address of server */
	public String getIP() {
		return this.ip;
	}

	/** @return port number of server */
	public int getPort() {
		return this.port;
	}

	/** @return username of server */
	public String getUsername() {
		return this.serverName;
	}

	/** @return HashMap of documents to ServerGuis */
	public HashMap<String, ServerGui> getDocuments() {
		return this.documents;
	}

	/** @return serverSockets */
	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}

	/** @return order of server */
	public int getOrder() {
		return this.order;
	}

	/** @return number of users */
	public int getNumOfUsers() {
		return this.users;
	}

	/** set number of users
	 * @param number of users
	 */
	public void setNumOfUsers(int users) {
		this.users = users;
	}

}
