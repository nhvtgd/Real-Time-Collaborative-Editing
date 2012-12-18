package host;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.Test;

public class HostTest {

    /**
     * Overall Testing strategy: 
     * 
     * This project can be broken down into three components: The GUI, the server/client, and the 
     * operational transform algorithm. We will test each component separately using a combination 
     * of automated unit tests and manual tests. The scope of the test would include  
     * insert and delete commands that a user would typicall type. We will flood the server 
     * with many of these commands concurrently to make sure that they can handle simultaneous edits.
     * Of course, we also want to make sure that any illegal operations by the client do not crash
     * the server, and are handled appropriate in our program. Exception handling is crucial, and 
     * we want to make sure that our tests handle these corner cases.
     * Lastly, after ensuring that each component works 
     * successfully, we will manually run the whole program to make sure all project 
     * specifications are satisfied.
     * 
     * The order of testing would be as follows: we first make sure that the server and client 
     * connections can be established. This would be done using mostly manual tests. Next, we 
     * test the GUI to make sure that a user changing the text would generate appropriate commands 
     * on both the client side and server side. Finally, we want to make sure the operational 
     * transform algorithm works such that concurrent edits on the document will be handled
     * correctly and the final document is what we expected. Passing the tests in this order 
     * would gives us high confidence that our project follows all the specifications.
     * 
     * Testing strategy for the GUI is defined in: ServerGui.java and ClientGui.java
     * 
     * Testing strategy for the OT Algorithm is specified in: OperationTest.java
     * 
     * Testing strategy for the server/client is specified in: CollabClient.java & CollabServer.java
     * 
     * To test the host, we should be able to provide various inputs to make sure that the
     * host parses them correctly and starts the appropriate GUI with the correct parameters.
     * For example, calling Host with the parameters "server localhost 4444" should create a server on 
     * the local network with a port number of 4444. Calling "client localhost 4444" should create a 
     * client and connect it to the server we just created, since they share the same IP Address and 
     * port number. On the other hand, calling "client localhost 4443" should throw an Exception, 
     * because no server with that port number exists. So we just want to exhaust all the different 
     * classes of  possibitilies for the user inputs and make sure they are handled appropriately.
     * 
     * Of course, we will also do a lot of manual testing.
     * @throws IOException 
     */
    
    @Test
    public void HostTest1() throws IOException {
        InputStream stdin = System.in;
        String input = "server localhost 4444";
        
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        //Scanner scanner = new Scanner(System.in);
        try {
            
            Host.main(new String[]{});
            assertEquals(Host.host.port, 4444);
        //System.out.println(scanner.nextLine());
        }
        finally {
            System.out.println("hi");
            System.setIn(stdin);
        }
    }

}
