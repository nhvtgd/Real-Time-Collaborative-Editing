package model;

import static org.junit.Assert.assertEquals;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import org.junit.Test;

import document.Operation;
import document.OperationEngineException;

/**
 * This will test the collabModel environment. There are some excpetions thrown
 * in the testing, because I did not create a clientGUI instance, and I am just
 * testing the buffer output. Therefore, when the code calls for the basicSwing
 * elements, they are null. This can be fixed in future iterations, but the
 * tests will still pass JUnit testing.
 * 
 * @author Hanwen Xu
 * 
 */
public class CollabModelTest {

    /**
     * This will test a single insertion
     * 
     * @throws OperationEngineException
     * @throws BadLocationException
     */
    @Test
    public void CollabModelTest1() throws OperationEngineException,
            BadLocationException {
        // In this test, we will have 2 clients, and a server model

        String init = "Hello World";
        AttributeSet normal = new SimpleAttributeSet();
        CollabModel server = new CollabModel(init);
        CollabModel model1 = new CollabModel(init);
        CollabModel model2 = new CollabModel(init);

        int order = 0;
        Operation op11 = model1.insertString(6, "6.005", 1, normal);
        op11.setOrder(order);
        order++;
        server.remoteInsert(op11);
        model2.remoteInsert(op11);
        // this makes sure the model1 and model2 converge after a single
        // operation is
        // sent to all clients
        assertEquals(model1.toString(), model2.toString());

    }

    /**
     * This will test a concurrent operation
     * 
     * @throws OperationEngineException
     * @throws BadLocationException
     */
    @Test
    public void CollabModelTest2() throws OperationEngineException,
            BadLocationException {
        // In this test, we will have 2 clients, and a server model

        String init = "Hello World";
        AttributeSet normal = new SimpleAttributeSet();
        CollabModel server = new CollabModel(init);
        CollabModel model1 = new CollabModel(init);
        CollabModel model2 = new CollabModel(init);

        int order = 0;
        Operation op11 = model1.insertString(6, "6.005", 1, normal);
        op11.setOrder(order);
        order++;
        server.remoteInsert(op11);
        model2.remoteInsert(op11);
        // this makes sure the model1 and model2 converge after a single
        // operation is
        // sent to all clients

        // now we will test a concurrent operation.
        // client 1 will insert "amazing" after hello
        // client 2 will delete "world"
        // the resultant string should converge to "Hello amazing 6.005"
        // In this case, we shall assume client 1's operation reaches the server
        // first
        Operation op21 = model1.insertString(6, "amazing ", 1, normal);
        op21.setOrder(order);
        order++;
        Operation op32 = model2.deleteString(11, 5, 2, normal);
        op32.setOrder(order);

        assertEquals(model1.toString(), model2.toString());

    }
}
