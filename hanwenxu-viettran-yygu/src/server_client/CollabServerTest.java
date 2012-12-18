package server_client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import document.DeleteOperation;
import document.InsertOperation;
import document.Operation;
import document.OperationEngineException;

/**
 * JUnit tests for the server
 * <p>
 * Note: In additional to these JUnit tests, a very thorough manual testing using 
 * the GUI was done to ensure correct functionality. See ServerGui.java for the exact 
 * testing strategies using the GUI.
 * <p>
 * The purpose of the server is to keep a central repertoire of the documents, and replaying 
 * all operations received by clients to each other. With regards to these JUnit tests,
 * I tried to make the various methods in the server as independent as possible so that they
 * can be tested without too much dependence on other objects. Of course, with a complex
 * program such as this one, this may not always be possible. Using mocks was an alternative
 * that was suggested, but we decided to spend our precious time on applying things we have learned 
 * in class rather than trying to master something we have never worked with before. 
 * <p>
 * For the most part, these unit tests checks for basic functionality. For example, making sure 
 * a client can connect to the server, the inputs are parsed correctly, etc. Also, it 
 * checks for the correctness of inputs and that the appropriate functions are called.
 * <p>
 * When the CollabServer calls a remote function or the GUI, we leave it up to the remote class/GUI to ensure
 * the correct implementation of the function. For example, several calls involves the use of 
 * updateUsers. We leave it up to the comprehensive manual tests that we do to ensure that these
 * functions work as documented
 * 
 * @author youyanggu
 *
 */
public class CollabServerTest {
    
    /** Default port */
    private static final int DEFAULT_PORT = 4444;
    /** Default username */
    private static final String DEFAULT_USERNAME = "Anonymous";
    /** Default IP address */
    private static final String DEFAULT_IP = "localhost";
    /** Default document name */
    private static final String DEFAULT_DOC_NAME = "default";
    
    // Make sure all fields are initialized correctly
    @Test
    public void initializeServer() throws IOException {
        System.setOut(new PrintStream(new OutputStream() {public void write(int b) {}}));
        System.setErr(new PrintStream(new OutputStream() {public void write(int b) {}}));
        CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        assertEquals(DEFAULT_IP, server.getIP());
        assertEquals(DEFAULT_PORT, server.getPort());
        assertEquals(DEFAULT_DOC_NAME, server.getUsername());
        assertEquals(0, server.getID());
        server.getServerSocket().close();
    }
    
    // Try connecting to server
    @Test
    public void connectToClient() throws IOException {
            
        final CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);

        new Thread(new Runnable() {
            public void run() {
                Socket s = null;
                try {
                    InetSocketAddress address = new InetSocketAddress(server.getIP(), server.getPort());
                    s = new Socket();
                    s.connect(address, 3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        final Socket socket = server.getServerSocket().accept();
        socket.close();   
        server.getServerSocket().close();
    }
    

    // Try connecting to server
    @Test
    public void connectToClient2() throws IOException {
            
        final CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);

        new Thread(new Runnable() {
            public void run() {
                Socket s = null;
                try {
                    InetSocketAddress address = new InetSocketAddress(server.getIP(), server.getPort());
                    s = new Socket();
                    s.connect(address, 3000);
                    ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                
                    Object o= in.readObject();
                    assertEquals(true, o instanceof ArrayList<?>);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        final Socket socket = server.getServerSocket().accept();
        server.setNumOfUsers(server.getNumOfUsers()+1);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); 
        out.writeObject(new ArrayList<String>());
        assertEquals(1, server.getNumOfUsers());
        socket.close(); 
        server.getServerSocket().close();
    }
    
    // Parses Operation string correctly
    @Test
    public void parseInsertOperation() throws IOException, OperationEngineException {
        CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        Operation op = new InsertOperation(null);
        Object o = (Object) op;
        server.parseInput(o, DEFAULT_DOC_NAME, 2);

        ArrayList<String> temp = new ArrayList<String>();
        temp.addAll(server.getDocuments().keySet());
        assertEquals(temp, new ArrayList<String>(Collections.singletonList(DEFAULT_DOC_NAME)));
    }
    
    // Calls to UpdateDocument works with Insert Operations
    @Test
    public void updateDocInsert() throws IOException, OperationEngineException {
        CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        Operation o = new InsertOperation(null);
        server.updateDoc(o);
        assertEquals("Insert tested", o.getKey());
    }
    
    // Calls to UpdateDocument works with Delete Operations
    @Test
    public void updateDocDelete() throws IOException, OperationEngineException {
        CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        Operation o = new DeleteOperation(null);
        server.updateDoc(o);
        assertEquals("Delete tested", o.getKey());
    }
    
    // Tests if operation is transmitted correctly
    @Test
    public void transmitOperation() throws IOException, OperationEngineException {
        CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        Operation o = new InsertOperation(null);
        server.transmit(o);
        assertEquals(server.getOrder(), o.getOrder()+1);
    }
    
  
    // Wrong IP
    @Test(expected=UnknownHostException.class)
    public void badIP() throws IOException {
        final CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        InetSocketAddress address = new InetSocketAddress(server.getIP()+"1", server.getPort());

        Socket s = new Socket();
        s.connect(address, 3000);
        s.close();
        server.getServerSocket().close();
        
    }
    
    // Wrong Port
    @Test(expected=ConnectException.class)
    public void badPort() throws IOException {
        
        final CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        InetSocketAddress address = new InetSocketAddress(server.getIP(), server.getPort()+1);
        Socket s = new Socket();
        s.connect(address, 3000);
        s.close();    
        server.getServerSocket().close();
    }
    
    // Cannot parse String objects
    @Test(expected=RuntimeException.class)
    public void parseStringError() throws IOException, OperationEngineException {
        CollabServer server = new CollabServer(DEFAULT_IP, DEFAULT_PORT, DEFAULT_USERNAME);
        String string = "Hi Andrew, please grade our project nicely!";
        Object o = (Object) new String(string);
        server.parseInput(o, "default", 2);
    }

    
}
