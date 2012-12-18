package server_client;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

import document.DeleteOperation;
import document.InsertOperation;
import document.Operation;
import document.OperationEngineException;

import ui.ClientGui;

/**
 * JUnit tests for the client
 * <p>
 * Note: In additional to these JUnit tests, a very thorough manual testing using 
 * the GUI was done to ensure correct functionality. See ClientGui.java for the exact 
 * testing strategies using the GUI.
 * <p>
 * With regards to these JUnit tests,
 * I tried to make the various methods in the client as independent as possible so that they
 * can be tested without too much dependence on other objects. Of course, with a complex
 * program such as this one, this may not always be possible. Using mocks was an alternative
 * that was suggested, but we decided to spend our precious time on applying things we have learned 
 * in class rather than trying to master something we have never worked with before. 
 * <p>
 * For the most part, these unit tests checks for basic functionality. For example, making sure 
 * the client connects to the server, the inputs are parsed correctly, etc. Also, it 
 * checks for the correctness of inputs and that the appropriate functions are called.
 * <p>
 * When the CollabClient calls a remote function, we leave it up to the remote class to ensure
 * the correct implementation of the function. For example, several calls involves the use of 
 * remoteInserts and remoteDeletes. We leave it up to the comprehensive operational transform tests
 * to run more rigorous tests.
 * 
 * @author youyanggu
 *
 */
public class CollabClientTest {
 
    /** Default port */
    private static final int DEFAULT_PORT = 4444;
    /** Default username */
    private static final String DEFAULT_USERNAME = "Anonymous";
    /** Default IP address*/
    private static final String DEFAULT_IP = "localhost";
    
    // Make sure all fields are initialized correctly
    @Test
    public void initializeClient() {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        assertEquals(DEFAULT_IP, client.getIP());
        assertEquals(DEFAULT_PORT, client.getPort());
        assertEquals(DEFAULT_USERNAME, client.getUsername());
    }
    
    // Try connecting to server
    @Test
    public void connectToServer() throws IOException {
        final CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        new Thread(new Runnable() {
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(client.getPort());
                    serverSocket.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
        InetSocketAddress address = new InetSocketAddress(client.getIP(), client.getPort());

        Socket s = new Socket();
        s.connect(address, 3000);
        s.close();
    }
     
    
    // Parses clientID correctly
    @Test
    public void parseClientID() throws IOException {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        Object o = (Object) new Integer(4);
        client.out = new ObjectOutputStream(new FileOutputStream("null.txt"));
        client.parseInput(o);
        assertEquals("Anonymous4 is editing document: ", client.label);
    }
    
    // Parses clientID correctly, this time with custom username
    @Test
    public void parseClientID2() throws IOException {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, "Youyang");
        Object o = (Object) new Integer(13);
        client.out = new ObjectOutputStream(new FileOutputStream("null.txt"));
        client.parseInput(o);
        assertEquals("Youyang is editing document: ", client.label);
    }
    
    // Sets document name correctly
    @Test
    public void setDocument() throws IOException {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, "Youyang");
        Object o = (Object) new Integer(13);
        client.out = new ObjectOutputStream(new FileOutputStream("null.txt"));
        client.setDocument("Youyang's document");
        client.parseInput(o);
        assertEquals("Youyang is editing document: Youyang's document", client.label);
    }

    // Parses initial document string correctly
    @Test
    public void parseString() throws IOException, OperationEngineException {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        String string = "Hi Andrew, please grade our project nicely!";
        Object o = (Object) new String(string);
        client.parseInput(o);

        assertEquals(string, client.gui.getInit());
    }
    
    // Calls to UpdateDocument works with InsertOperations
    @Test
    public void updateDocInsert() throws IOException, OperationEngineException {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        client.gui = new ClientGui("Welcome", client, "Client");
        Operation o = new InsertOperation(null);
        client.updateDoc(o);
    }
    
    // Calls to UpdateDocument works with DeleteOperations
    @Test
    public void updateDocDelete() throws IOException, OperationEngineException {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        client.gui = new ClientGui("Welcome", client, "Client");
        Operation o = new DeleteOperation(null);
        client.updateDoc(o);
    }
    
    // bad Port number
    @Test(expected=Exception.class)
    public void wrongPort() throws IOException {
        Socket s = new Socket();
        try {
            final CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
            new Thread(new Runnable() {
                public void run() {
                    try {                    
                        ServerSocket serverSocket = new ServerSocket(client.getPort());
                        serverSocket.accept();
                    } catch (IOException e) {}                 
                }
            }).start();
            
            InetSocketAddress address = new InetSocketAddress(client.getIP(), client.getPort()+1);
     
            s.connect(address, 3000);
            s.close();
        } finally {
            s.close();
        }
    }
    
    // bad IP address
    @Test(expected=Exception.class)
    public void wrongIP() throws IOException {
        Socket s = new Socket();
        try {
            final CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
            new Thread(new Runnable() {
                public void run() {
                    try {                    
                        ServerSocket serverSocket = new ServerSocket(client.getPort());
                        serverSocket.accept();
                    } catch (IOException e) {}                 
                }
            }).start();
            
            InetSocketAddress address = new InetSocketAddress(client.getIP()+"2", client.getPort());
     
            s.connect(address, 3000);
            s.close();
        } finally {
            s.close();
        }
    }
    
    // Should not be able to parse a generic Object
    @Test(expected=RuntimeException.class)
    public void badObjectType() throws IOException {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        Object o = new Object();
        client.out = new ObjectOutputStream(new FileOutputStream("null.txt"));
        client.parseInput(o);
    }
    
    // updateDoc() should not be able to handle null operations
    @Test(expected=RuntimeException.class)
    public void updateDocError() throws IOException, OperationEngineException {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        Operation o = null;
        client.updateDoc(o);
    }

    // transmit() should not be able to handle null operations
    @Test(expected=RuntimeException.class)
    public void badOperation() throws IOException {
        CollabClient client = new CollabClient(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        Operation o = null;
        client.transmit(o);
    }

}
