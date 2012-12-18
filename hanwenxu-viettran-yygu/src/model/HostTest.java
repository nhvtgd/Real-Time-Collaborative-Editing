package model;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class HostTest {

    /**
     * To test the host, we should be able to provide various inputs to make sure that the
     * host parses them correctly and starts the appropriate GUI with the correct parameters.
     * For example, calling Host with the parameters "server localhost 4444" should create a server on 
     * the local network with a port number of 4444. Calling "client localhost 4444" should create a 
     * client and connect it to the server we just created, since they share the same IP Address and 
     * port number. On the other hand, calling "client localhost 4443" should throw an Exception, 
     * because no server with that port number exists. So we just want to exhaust all the different 
     * classes of  possibitilies for the user inputs and make sure they are handled appropriately.
     * <p>
     * The following tests show a various combination of inputs that are either viewed 
     * as valid or invalid when parsed by the host. When following the format described upon running 
     * the program, the command would launch an appropriate server or client GUI.
     * Not following the template would cause Exceptions to be thrown, as shown in the 
     * tests below.
     * <p>
     * Of course, we also do a lot of manual testing to ensure that all 
     * commands are parsed and passed in to the server/client constructors correctly.
     */
    
    /** default username */
    static final String DEFAULT_USERNAME = "Anonymous";
    
    // server, IP, port (implied username)
    @Test
    public void HostTest1() throws IOException {
        String input = "server 123.456.789.0 4444";
        Host h = Host.parseInputs(new InputStreamReader(new ByteArrayInputStream(input.getBytes())), true);
        
        assertEquals(h.getType().toLowerCase(),"server");
        assertEquals(h.getPort(), 4444);
        assertEquals(h.getUserName(), DEFAULT_USERNAME);
        assertEquals(h.getIP(),"123.456.789.0");
        
    }
    
    // server (implied IP, port, and username)
    @Test
    public void HostTest2() throws IOException {
        String input = "serVer";
        Host h = Host.parseInputs(new InputStreamReader(new ByteArrayInputStream(input.getBytes())), true);
        
        assertEquals(h.getType().toLowerCase(),"server");
        assertEquals(h.getPort(), 4444);
        assertEquals(h.getUserName(), DEFAULT_USERNAME);
        assertEquals(h.getIP(),"localhost");
        
    }
    
    // server and username (implied IP, port)
    @Test
    public void HostTest3() throws IOException {
        String input = "Server Youyang";
        Host h = Host.parseInputs(new InputStreamReader(new ByteArrayInputStream(input.getBytes())), true);
        
        assertEquals(h.getUserName(),"Youyang");
        assertEquals(h.getType().toLowerCase(),"server");
        assertEquals(h.getPort(), 4444);
        assertEquals(h.getIP(),"localhost");
        
    }
    
    //server username IP port (no implications)
    @Test
    public void HostTest4() throws IOException {
        String input = "CLIenT Youyang 224.1.2.3 1242";
        Host h = Host.parseInputs(new InputStreamReader(new ByteArrayInputStream(input.getBytes())), true);
        
        assertEquals(h.getUserName(),"Youyang");
        assertEquals(h.getType().toLowerCase(),"client");
        assertEquals(h.getPort(), 1242);
        assertEquals(h.getIP(),"224.1.2.3");
        
    }
    
    // client IP port (implied username)
    @Test
    public void HostTest5() throws IOException {
        String input = "client 18.189.68.48 1242";
        Host h = Host.parseInputs(new InputStreamReader(new ByteArrayInputStream(input.getBytes())), true);
        
        assertEquals(h.getUserName(), DEFAULT_USERNAME);
        assertEquals(h.getType().toLowerCase(),"server");
        assertEquals(h.getPort(), 1242);
        assertEquals(h.getIP(),"224.1.2.3");
        
    }
    
    // Missing port
    @Test(expected=RuntimeException.class)
    public void missingPort() throws IOException {
        String input = "CLIenT Youyang 224.1.2.3";
        Host.parseInputs(new InputStreamReader(new ByteArrayInputStream(input.getBytes())), true);
    }
    
    // Too many arguments
    @Test(expected=RuntimeException.class)
    public void extraStuff() throws IOException {
        String input = "CLIenT Youyang 224.1.2.3 4444 blah blah blah";
        Host.parseInputs(new InputStreamReader(new ByteArrayInputStream(input.getBytes())), true);
    }
    
    // Missing server or client
    @Test(expected=RuntimeException.class)
    public void noServerOrClient() throws IOException {
        String input = "Youyang 224.1.2.3 4444";
        Host.parseInputs(new InputStreamReader(new ByteArrayInputStream(input.getBytes())), true);
    }
    
    // Bad port number
    @Test(expected=RuntimeException.class)
    public void badPort() throws IOException {
        String input = "server serverName 123.456.789 badPort";
        Host.parseInputs(new InputStreamReader(new ByteArrayInputStream(input.getBytes())), true);
    }

}
