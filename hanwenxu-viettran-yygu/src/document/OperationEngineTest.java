package document;

import static org.junit.Assert.assertEquals;



import org.junit.Test;

/**
 * This will containing the testing for all code associated with the Operational Transform algorithm
 * 
 * We will first test the Operation class, and its subclasses DeleteOperation, InsertOperation, and UpdateOperation
 * we will do this by creating instances of the classes, calling mutators, and then printing out a toString representation
 * and comparing it to some expected value.  Since we have not created much of the toString and mutators, we are not sure
 * what we want the final outcome to be.  We added some hypothetical tests instead.
 *
 * 
 * We need to make sure that we get a decent code coverage. We will make sure to test various edge cases for the 
 * classes, such as null input, and every method in the class.
 * 
 * @author Hanwen Xu
 *
 */
public class OperationEngineTest {

    /**
     * This test will be a basic OperationEngine creation, and a basic Operation creation.
     * This will concurrently test the OperationEngine.push(Operation op) procedure as well, taking basic parameters 
     * 
     * The operation engine has code that calls on the Operation, so it inherently tests the operation
     * code as well.  
     * @throws OperationEngineException
     */
    @Test
    public void OperationEngineTest1() throws OperationEngineException {
        
        
        //initialized at the server, communicated to individual client
        int siteID1 = 1;
        int siteID2 = 2;
        int siteID3 = 3;
        int siteID4 = 4;
        
        OperationEngine oes = new OperationEngine(0); //created at the server
        OperationEngine oe1 = new OperationEngine(siteID1); //created at the client
        OperationEngine oe2 = new OperationEngine(siteID2);
        OperationEngine oe3 = new OperationEngine(siteID3);
        OperationEngine oe4 = new OperationEngine(siteID4);
        
        int[] temp = new int[0];
        
        int order = 0; //maintained at the server
        //op11 --> first number is operation number, second number is siteID
        Operation op11 = oe1.push(true, "document", "a", "insert", 0, 1, temp, 0);
        //after op11 is sent to server, op10 is created at the server.  op11 is also transmitted to the clients with order 0
        op11.setOrder(order);
        order++;
        Operation op10 = oes.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        Operation op12 = oe2.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        Operation op13 = oe3.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        Operation op14 = oe4.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        //server increment order
    
        assertEquals(op11.getPosition(), 0);
        assertEquals(op10.getPosition(), 0);
        assertEquals(op12.getPosition(), 0);
        assertEquals(op13.getPosition(), 0);
        assertEquals(op14.getPosition(), 0);
        assertEquals(op10.getValue(), "a");
        assertEquals(op11.getValue(), "a");
        assertEquals(op12.getValue(), "a");
        assertEquals(op13.getValue(), "a");
        assertEquals(op14.getValue(), "a");
        
        
        Operation op22 = oe2.push(true, "document", "b", "insert", 1, 2, temp, 0);
        op22.setOrder(order);
        Operation op20 = oes.pushRemoteOp(op22);
        Operation op21 = oe1.pushRemoteOp(op22);
        Operation op23 = oe3.pushRemoteOp(op22);
        Operation op24 = oe4.pushRemoteOp(op22);
        order++;
        
        assertEquals(op20.getPosition(), 1);
        assertEquals(op21.getPosition(), 1);
        assertEquals(op22.getPosition(), 1);
        assertEquals(op23.getPosition(), 1);
        assertEquals(op24.getPosition(), 1);
        assertEquals(op20.getValue(), "b");
        assertEquals(op21.getValue(), "b");
        assertEquals(op22.getValue(), "b");
        assertEquals(op23.getValue(), "b");
        assertEquals(op24.getValue(), "b");
        
        Operation op33 = oe3.push(true, "document", "c", "insert", 2, 3, temp, 0);
        op33.setOrder(order);
        order++;
        oes.pushRemoteOp(op33);
        oe1.pushRemoteOp(op33);
        oe2.pushRemoteOp(op33);
        oe4.pushRemoteOp(op33);

        Operation op41 = oe1.push(true, "document", "d", "insert", 3, 1, temp, 0);
        op41.setOrder(order);
        order++;
        oes.pushRemoteOp(op41);
        oe2.pushRemoteOp(op41);
        oe3.pushRemoteOp(op41);
        oe4.pushRemoteOp(op41);

        Operation op54 = oe4.push(true, "document", "e", "insert", 4, 4, temp, 0);
        //client.transmit(op54) ---> server
        
        op54.setOrder(order);
        order++;
        Operation op50 = oes.pushRemoteOp(op54);
        Operation op51 = oe1.pushRemoteOp(op54);
        Operation op52 = oe2.pushRemoteOp(op54);
        Operation op53 = oe3.pushRemoteOp(op54);

        assertEquals(op50.getPosition(), 4);
        assertEquals(op51.getPosition(), 4);
        assertEquals(op52.getPosition(), 4);
        assertEquals(op53.getPosition(), 4);
        assertEquals(op54.getPosition(), 4);
        assertEquals(op50.getValue(), "e");
        assertEquals(op51.getValue(), "e");
        assertEquals(op52.getValue(), "e");
        assertEquals(op53.getValue(), "e");
        assertEquals(op54.getValue(), "e");
        
        //final document at stable state is abcde
    }
    
    
    /**
     * This OperationEngineTest will test concurrent operations
     * @throws OperationEngineException
     */
    @Test
    public void OperationEngineTest2() throws OperationEngineException{
      //initialized at the server, communicated to individual client
        int siteID1 = 1;
        int siteID2 = 2;
        int siteID3 = 3;
        int siteID4 = 4;
        
        OperationEngine oes = new OperationEngine(0); //created at the server
        OperationEngine oe1 = new OperationEngine(siteID1); //created at the client
        OperationEngine oe2 = new OperationEngine(siteID2);
        OperationEngine oe3 = new OperationEngine(siteID3);
        OperationEngine oe4 = new OperationEngine(siteID4);
        
        int[] temp = new int[0];
        
        int order = 0; //maintained at the server
        //op11 --> first number is operation number, second number is siteID
        Operation op11 = oe1.push(true, "document", "a", "insert", 0, 1, temp, 0);
        //after op11 is sent to server, op10 is created at the server.  op11 is also transmitted to the clients with order 0
        op11.setOrder(order);
        order++;
        oes.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe2.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe3.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe4.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        //server increment order
        
        
        Operation op22 = oe2.push(true, "document", "b", "insert", 1, 2, temp, 0);
        op22.setOrder(order);
        oes.pushRemoteOp(op22);
        oe1.pushRemoteOp(op22);
        oe3.pushRemoteOp(op22);
        oe4.pushRemoteOp(op22);
        order++;
 
        
        Operation op33 = oe3.push(true, "document", "c", "insert", 2, 3, temp, 0);
        op33.setOrder(order);
        order++;
        oes.pushRemoteOp(op33);
        oe1.pushRemoteOp(op33);
        oe2.pushRemoteOp(op33);
        oe4.pushRemoteOp(op33);

        Operation op41 = oe1.push(true, "document", "d", "insert", 3, 1, temp, 0);
        op41.setOrder(order);
        order++;
        oes.pushRemoteOp(op41);
        oe2.pushRemoteOp(op41);
        oe3.pushRemoteOp(op41);
        oe4.pushRemoteOp(op41);

        Operation op54 = oe4.push(true, "document", "e", "insert", 4, 4, temp, 0);
        //client.transmit(op54) ---> server
        
        op54.setOrder(order);
        order++;
        oes.pushRemoteOp(op54);
        oe1.pushRemoteOp(op54);
        oe2.pushRemoteOp(op54);
        oe3.pushRemoteOp(op54);
        
        //final document at stable state is abcde
        
        
        //concurrent operation test.  server first receives message to insert A at index 2, forming
        //abAcde
        //then receives message to delete d, forming
        //abce at the client.  We need to merge the two to form abAce
        Operation op61 = oe1.push(true, "document", "A", "insert", 2, 1, temp, 0);
        op61.setOrder(order);
        order++;
        Operation op72 = oe2.push(true, "document", "d", "delete", 3, 2, temp, 0);
        op72.setOrder(order);
        order++;
        Operation op60 = oes.pushRemoteOp(op61);
        Operation op70 = oes.pushRemoteOp(op72);
        
        //The server should first insert "A" at index 2, and then delete "d" now at 4, instead of 3
        assertEquals(op60.getPosition(), 2);
        assertEquals(op70.getPosition(), 4);
        assertEquals(op60.getValue(), "A");
        assertEquals(op70.getValue(), "d");
        
        //after some time, client 1 will receive op72 and process it
        //it should delete "d" from index 4
        Operation op71 = oe1.pushRemoteOp(op72);
        assertEquals(op71.getPosition(), 4);
        
        //after some time, client 2 will receive op61 and process it
        Operation op62 = oe2.pushRemoteOp(op61);
        assertEquals(op62.getPosition(), 2);
        assertEquals(op62.getValue(), "A");
        
        //lets assume client 3 gets the operations in order op61, op72 from the server
        Operation op63 = oe3.pushRemoteOp(op61);
        Operation op73 = oe3.pushRemoteOp(op72);
        assertEquals(op63.getPosition(), 2);
        assertEquals(op73.getPosition(), 4);
        
        //lets assume client 4 gets the operations in order op72, op61
        //The first thing will be to delete "d" from index 3, and then insert "A" at index 2
        Operation op74 = oe4.pushRemoteOp(op72);
        Operation op64 = oe4.pushRemoteOp(op61);
        assertEquals(op64.getPosition(), 2);
        assertEquals(op74.getPosition(), 3);
    }
    
    
    /**
     * This test will test a new client joining in the middle of an operation
     * @throws OperationEngineException
     */
    @Test
    public void operationEngineTest3() throws OperationEngineException{
      //initialized at the server, communicated to individual client
        int siteID1 = 1;
        int siteID2 = 2;
        int siteID3 = 3;
        int siteID4 = 4;
        
        OperationEngine oes = new OperationEngine(0); //created at the server
        OperationEngine oe1 = new OperationEngine(siteID1); //created at the client
        OperationEngine oe2 = new OperationEngine(siteID2);
        OperationEngine oe3 = new OperationEngine(siteID3);
        OperationEngine oe4 = new OperationEngine(siteID4);
        
        int[] temp = new int[0];
        
        int order = 0; //maintained at the server
        //op11 --> first number is operation number, second number is siteID
        Operation op11 = oe1.push(true, "document", "a", "insert", 0, 1, temp, 0);
        //after op11 is sent to server, op10 is created at the server.  op11 is also transmitted to the clients with order 0
        op11.setOrder(order);
        order++;
        oes.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe2.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe3.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe4.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        //server increment order
        
        
        Operation op22 = oe2.push(true, "document", "b", "insert", 1, 2, temp, 0);
        op22.setOrder(order);
        oes.pushRemoteOp(op22);
        oe1.pushRemoteOp(op22);
        oe3.pushRemoteOp(op22);
        oe4.pushRemoteOp(op22);
        order++;
 
        
        Operation op33 = oe3.push(true, "document", "c", "insert", 2, 3, temp, 0);
        op33.setOrder(order);
        order++;
        oes.pushRemoteOp(op33);
        oe1.pushRemoteOp(op33);
        oe2.pushRemoteOp(op33);
        oe4.pushRemoteOp(op33);

        Operation op41 = oe1.push(true, "document", "d", "insert", 3, 1, temp, 0);
        op41.setOrder(order);
        order++;
        oes.pushRemoteOp(op41);
        oe2.pushRemoteOp(op41);
        oe3.pushRemoteOp(op41);
        oe4.pushRemoteOp(op41);

        Operation op54 = oe4.push(true, "document", "e", "insert", 4, 4, temp, 0);
        //client.transmit(op54) ---> server
        
        op54.setOrder(order);
        order++;
        oes.pushRemoteOp(op54);
        oe1.pushRemoteOp(op54);
        oe2.pushRemoteOp(op54);
        oe3.pushRemoteOp(op54);
        
        //final document at stable state is abcde
        
        
        //concurrent operation test.  server first receives message to insert A at index 2, forming
        //abAcde
        //then receives message to delete d, forming
        //abce at the client.  We need to merge the two to form abAce
        Operation op61 = oe1.push(true, "document", "A", "insert", 2, 1, temp, 0);
        op61.setOrder(order);
        order++;
        Operation op72 = oe2.push(true, "document", "d", "delete", 3, 2, temp, 0);
        op72.setOrder(order);
        order++;
        oes.pushRemoteOp(op61);
        oes.pushRemoteOp(op72);
        
        
        //after some time, client 1 will receive op72 and process it
        //it should delete "d" from index 4
        oe1.pushRemoteOp(op72);
        
        //after some time, client 2 will receive op61 and process it
        oe2.pushRemoteOp(op61);
        
        //lets assume client 3 gets the operations in order op61, op72 from the server
        oe3.pushRemoteOp(op61);
        oe3.pushRemoteOp(op72);
        
        //lets assume client 4 gets the operations in order op72, op61
        //The first thing will be to delete "d" from index 3, and then insert "A" at index 2
        oe4.pushRemoteOp(op72);
        oe4.pushRemoteOp(op61);
        
        //lets assume a new client is joining the fray.  We shall create a new siteID, incrementing
        //the counter
        int siteID5 = 5;
        OperationEngine oe5 = new OperationEngine(siteID5);
        //for every operation in the server's history buffer, transmit it to the 
        //new client, so that the client's history buffer can get synced up.

        //we first need to transmit the current string and contextVector to the new client
        oe5.setCV(oes.copyClientState());
        //If this wasn't synced up, any changes client5 will make will simply be appended
        //to the end of the current document in the convergent state
        assertEquals(oe5.copyClientState().toString(), oes.copyClientState().toString());
    }
    
    
    /**
     * This will test a concurrent operation by a new client
     * @throws OperationEngineException
     */
    @Test
    public void operationEngineTest4() throws OperationEngineException{
      //initialized at the server, communicated to individual client
        int siteID1 = 1;
        int siteID2 = 2;
        int siteID3 = 3;
        int siteID4 = 4;
        
        OperationEngine oes = new OperationEngine(0); //created at the server
        OperationEngine oe1 = new OperationEngine(siteID1); //created at the client
        OperationEngine oe2 = new OperationEngine(siteID2);
        OperationEngine oe3 = new OperationEngine(siteID3);
        OperationEngine oe4 = new OperationEngine(siteID4);
        
        int[] temp = new int[0];
        
        int order = 0; //maintained at the server
        //op11 --> first number is operation number, second number is siteID
        Operation op11 = oe1.push(true, "document", "a", "insert", 0, 1, temp, 0);
        //after op11 is sent to server, op10 is created at the server.  op11 is also transmitted to the clients with order 0
        op11.setOrder(order);
        order++;
        oes.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe2.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe3.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe4.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        //server increment order
        
        
        Operation op22 = oe2.push(true, "document", "b", "insert", 1, 2, temp, 0);
        op22.setOrder(order);
        oes.pushRemoteOp(op22);
        oe1.pushRemoteOp(op22);
        oe3.pushRemoteOp(op22);
        oe4.pushRemoteOp(op22);
        order++;
 
        
        Operation op33 = oe3.push(true, "document", "c", "insert", 2, 3, temp, 0);
        op33.setOrder(order);
        order++;
        oes.pushRemoteOp(op33);
        oe1.pushRemoteOp(op33);
        oe2.pushRemoteOp(op33);
        oe4.pushRemoteOp(op33);

        Operation op41 = oe1.push(true, "document", "d", "insert", 3, 1, temp, 0);
        op41.setOrder(order);
        order++;
        oes.pushRemoteOp(op41);
        oe2.pushRemoteOp(op41);
        oe3.pushRemoteOp(op41);
        oe4.pushRemoteOp(op41);

        Operation op54 = oe4.push(true, "document", "e", "insert", 4, 4, temp, 0);
        //client.transmit(op54) ---> server
        
        op54.setOrder(order);
        order++;
        oes.pushRemoteOp(op54);
        oe1.pushRemoteOp(op54);
        oe2.pushRemoteOp(op54);
        oe3.pushRemoteOp(op54);
        
        //final document at stable state is abcde
        
        
        //concurrent operation test.  server first receives message to insert A at index 2, forming
        //abAcde
        //then receives message to delete d, forming
        //abce at the client.  We need to merge the two to form abAce
        Operation op61 = oe1.push(true, "document", "A", "insert", 2, 1, temp, 0);
        op61.setOrder(order);
        order++;
        Operation op72 = oe2.push(true, "document", "d", "delete", 3, 2, temp, 0);
        op72.setOrder(order);
        order++;
        oes.pushRemoteOp(op61);
        oes.pushRemoteOp(op72);
        
        
        //after some time, client 1 will receive op72 and process it
        //it should delete "d" from index 4
        oe1.pushRemoteOp(op72);
        
        //after some time, client 2 will receive op61 and process it
        oe2.pushRemoteOp(op61);
        
        //lets assume client 3 gets the operations in order op61, op72 from the server
        oe3.pushRemoteOp(op61);
        oe3.pushRemoteOp(op72);
        
        //lets assume client 4 gets the operations in order op72, op61
        //The first thing will be to delete "d" from index 3, and then insert "A" at index 2
        oe4.pushRemoteOp(op72);
        oe4.pushRemoteOp(op61);
        
        //lets assume a new client is joining the fray.  We shall create a new siteID, incrementing
        //the counter
        int siteID5 = 5;
        OperationEngine oe5 = new OperationEngine(siteID5);
        //for every operation in the server's history buffer, transmit it to the 
        //new client, so that the client's history buffer can get synced up.

        //we first need to transmit the current string and contextVector to the new client
        oe5.setCV(oes.copyClientState());
        //If this wasn't synced up, any changes client5 will make will simply be appended
        //to the end of the current document in the convergent state
        Operation op85 = oe5.push(true, "document", "A", "insert", 2, 5, temp, 0);
        op85.setOrder(order);
        order++;
        Operation op91 = oe1.push(true, "document", "c", "delete", 3, 1, temp, 0);
        op91.setOrder(order);
        order++;
        Operation op80 = oes.pushRemoteOp(op85);
        Operation op90 = oes.pushRemoteOp(op91);
        assertEquals(op80.getPosition(), 2);
        assertEquals(op90.getPosition(), 4);
        
    }
    
   
    /**
     * This test will test multiple character concurrent submission.
     * @throws OperationEngineException
     */
    @Test
    public void operationEngineTest5() throws OperationEngineException{
      //initialized at the server, communicated to individual client
        int siteID1 = 1;
        int siteID2 = 2;
        int siteID3 = 3;
        int siteID4 = 4;
        
        OperationEngine oes = new OperationEngine(0); //created at the server
        OperationEngine oe1 = new OperationEngine(siteID1); //created at the client
        OperationEngine oe2 = new OperationEngine(siteID2);
        OperationEngine oe3 = new OperationEngine(siteID3);
        OperationEngine oe4 = new OperationEngine(siteID4);
        
        int[] temp = new int[0];
        
        int order = 0; //maintained at the server
        //op11 --> first number is operation number, second number is siteID
        Operation op11 = oe1.push(true, "document", "a", "insert", 0, 1, temp, 0);
        //after op11 is sent to server, op10 is created at the server.  op11 is also transmitted to the clients with order 0
        op11.setOrder(order);
        order++;
        oes.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe2.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe3.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe4.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        //server increment order
        
        
        Operation op22 = oe2.push(true, "document", "b", "insert", 1, 2, temp, 0);
        op22.setOrder(order);
        oes.pushRemoteOp(op22);
        oe1.pushRemoteOp(op22);
        oe3.pushRemoteOp(op22);
        oe4.pushRemoteOp(op22);
        order++;
 
        
        Operation op33 = oe3.push(true, "document", "c", "insert", 2, 3, temp, 0);
        op33.setOrder(order);
        order++;
        oes.pushRemoteOp(op33);
        oe1.pushRemoteOp(op33);
        oe2.pushRemoteOp(op33);
        oe4.pushRemoteOp(op33);

        Operation op41 = oe1.push(true, "document", "d", "insert", 3, 1, temp, 0);
        op41.setOrder(order);
        order++;
        oes.pushRemoteOp(op41);
        oe2.pushRemoteOp(op41);
        oe3.pushRemoteOp(op41);
        oe4.pushRemoteOp(op41);

        Operation op54 = oe4.push(true, "document", "e", "insert", 4, 4, temp, 0);
        //client.transmit(op54) ---> server
        
        op54.setOrder(order);
        order++;
        oes.pushRemoteOp(op54);
        oe1.pushRemoteOp(op54);
        oe2.pushRemoteOp(op54);
        oe3.pushRemoteOp(op54);
        
        //final document at stable state is abcde
        
        
        //concurrent operation test.  server first receives message to insert A at index 2, forming
        //abAcde
        //then receives message to delete d, forming
        //abce at the client.  We need to merge the two to form abAce
        Operation op61 = oe1.push(true, "document", "A", "insert", 2, 1, temp, 0);
        op61.setOrder(order);
        order++;
        Operation op72 = oe2.push(true, "document", "d", "delete", 3, 2, temp, 0);
        op72.setOrder(order);
        order++;
        oes.pushRemoteOp(op61);
        oes.pushRemoteOp(op72);
        
        
        //after some time, client 1 will receive op72 and process it
        //it should delete "d" from index 4
        oe1.pushRemoteOp(op72);
        
        //after some time, client 2 will receive op61 and process it
        oe2.pushRemoteOp(op61);
        
        //lets assume client 3 gets the operations in order op61, op72 from the server
        oe3.pushRemoteOp(op61);
        oe3.pushRemoteOp(op72);
        
        //lets assume client 4 gets the operations in order op72, op61
        //The first thing will be to delete "d" from index 3, and then insert "A" at index 2
        oe4.pushRemoteOp(op72);
        oe4.pushRemoteOp(op61);
        
        //lets assume a new client is joining the fray.  We shall create a new siteID, incrementing
        //the counter
        int siteID5 = 5;
        OperationEngine oe5 = new OperationEngine(siteID5);
        //for every operation in the server's history buffer, transmit it to the 
        //new client, so that the client's history buffer can get synced up.

        //we first need to transmit the current string and contextVector to the new client
        oe5.setCV(oes.copyClientState());
        //If this wasn't synced up, any changes client5 will make will simply be appended
        //to the end of the current document in the convergent state
        Operation op85 = oe5.push(true, "document", "A", "insert", 2, 5, temp, 0);
        op85.setOrder(order);
        order++;
        Operation op91 = oe1.push(true, "document", "c", "delete", 3, 1, temp, 0);
        op91.setOrder(order);
        order++;
        oes.pushRemoteOp(op85);
        oes.pushRemoteOp(op91);

        
        //let us now test with multiple character submission.  
        //current convergent document is abAAe
        //client 1 will insert 'hello world' at the beginning.
        //client 2 will concurrently delete 'abAAe'
        //server receives client 1's operations before client 2's.
        
        Operation op101 = oe1.push(true, "document", "hello world", "insert", 0, 1, temp, 0);
        op101.setOrder(order);
        order++;
        Operation op112 = oe2.push(true, "document", "abAAe", "delete", 0, 2, temp, 0);
        op112.setOrder(order);
        order++;
        
        //if the server first processes op100, the position of 'abAAe' is offset by 11 chars.
        //the delete operation needs to be at offset 11 now.
        Operation op100 = oes.pushRemoteOp(op101);
        Operation op110 = oes.pushRemoteOp(op112);
        assertEquals(op100.getPosition(), 0);
        assertEquals(op100.getValue(), "hello world");
        assertEquals(op110.getPosition(), 11);
    }
    
    /**
     * This test will test operations with different key values, replicating 
     * new clients joining who wish to edit a different document, or create a new document
     * in the server.
     * @throws OperationEngineException 
     */
    @Test
    public void operationEngineTest6() throws OperationEngineException{
      //initialized at the server, communicated to individual client
        int siteID1 = 1;
        int siteID2 = 2;
        int siteID3 = 3;
        int siteID4 = 4;
        
        OperationEngine oes = new OperationEngine(0); //created at the server
        OperationEngine oe1 = new OperationEngine(siteID1); //created at the client
        OperationEngine oe2 = new OperationEngine(siteID2);
        OperationEngine oe3 = new OperationEngine(siteID3);
        OperationEngine oe4 = new OperationEngine(siteID4);
        
        int[] temp = new int[0];
        
        int order = 0; //maintained at the server
        //op11 --> first number is operation number, second number is siteID
        Operation op11 = oe1.push(true, "document", "a", "insert", 0, 1, temp, 0);
        //after op11 is sent to server, op10 is created at the server.  op11 is also transmitted to the clients with order 0
        op11.setOrder(order);
        order++;
        oes.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe2.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe3.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        oe4.push(false, op11.key, op11.value, op11.type, op11.position, op11.siteId, op11.clientState.copyClients(), order);
        //server increment order
        
        
        Operation op22 = oe2.push(true, "document", "b", "insert", 1, 2, temp, 0);
        op22.setOrder(order);
        oes.pushRemoteOp(op22);
        oe1.pushRemoteOp(op22);
        oe3.pushRemoteOp(op22);
        oe4.pushRemoteOp(op22);
        order++;
 
        
        Operation op33 = oe3.push(true, "document", "c", "insert", 2, 3, temp, 0);
        op33.setOrder(order);
        order++;
        oes.pushRemoteOp(op33);
        oe1.pushRemoteOp(op33);
        oe2.pushRemoteOp(op33);
        oe4.pushRemoteOp(op33);

        Operation op41 = oe1.push(true, "document", "d", "insert", 3, 1, temp, 0);
        op41.setOrder(order);
        order++;
        oes.pushRemoteOp(op41);
        oe2.pushRemoteOp(op41);
        oe3.pushRemoteOp(op41);
        oe4.pushRemoteOp(op41);

        Operation op54 = oe4.push(true, "document", "e", "insert", 4, 4, temp, 0);
        //client.transmit(op54) ---> server
        
        op54.setOrder(order);
        order++;
        oes.pushRemoteOp(op54);
        oe1.pushRemoteOp(op54);
        oe2.pushRemoteOp(op54);
        oe3.pushRemoteOp(op54);
        
        //final document at stable state is abcde
        
        
        //concurrent operation test.  server first receives message to insert A at index 2, forming
        //abAcde
        //then receives message to delete d, forming
        //abce at the client.  We need to merge the two to form abAce
        Operation op61 = oe1.push(true, "document", "A", "insert", 2, 1, temp, 0);
        op61.setOrder(order);
        order++;
        Operation op72 = oe2.push(true, "document", "d", "delete", 3, 2, temp, 0);
        op72.setOrder(order);
        order++;
        oes.pushRemoteOp(op61);
        oes.pushRemoteOp(op72);
        
        
        //after some time, client 1 will receive op72 and process it
        //it should delete "d" from index 4
        oe1.pushRemoteOp(op72);
        
        //after some time, client 2 will receive op61 and process it
        oe2.pushRemoteOp(op61);
        
        //lets assume client 3 gets the operations in order op61, op72 from the server
        oe3.pushRemoteOp(op61);
        oe3.pushRemoteOp(op72);
        
        //lets assume client 4 gets the operations in order op72, op61
        //The first thing will be to delete "d" from index 3, and then insert "A" at index 2
        oe4.pushRemoteOp(op72);
        oe4.pushRemoteOp(op61);
        
        //lets assume a new client is joining the fray.  We shall create a new siteID, incrementing
        //the counter
        int siteID5 = 5;
        OperationEngine oe5 = new OperationEngine(siteID5);
        //for every operation in the server's history buffer, transmit it to the 
        //new client, so that the client's history buffer can get synced up.

        //we first need to transmit the current string and contextVector to the new client
        oe5.setCV(oes.copyClientState());
        //If this wasn't synced up, any changes client5 will make will simply be appended
        //to the end of the current document in the convergent state
        Operation op85 = oe5.push(true, "document", "A", "insert", 2, 5, temp, 0);
        op85.setOrder(order);
        order++;
        Operation op91 = oe1.push(true, "document", "c", "delete", 3, 1, temp, 0);
        op91.setOrder(order);
        order++;
        oes.pushRemoteOp(op85);
        oes.pushRemoteOp(op91);

        
        //let us now test with multiple character submission.  
        //current convergent document is abAAe
        //client 1 will insert 'hello world' at the beginning.
        //client 2 will concurrently delete 'abAAe'
        //server receives client 1's operations before client 2's.
        
        Operation op101 = oe1.push(true, "document", "hello world", "insert", 0, 1, temp, 0);
        op101.setOrder(order);
        order++;
        Operation op112 = oe2.push(true, "document", "abAAe", "delete", 0, 2, temp, 0);
        op112.setOrder(order);
        order++;
        
        //if the server first processes op100, the position of 'abAAe' is offset by 11 chars.
        //the delete operation needs to be at offset 11 now.
        oes.pushRemoteOp(op101);
        oes.pushRemoteOp(op112);
        
        //Now, we have "hello world" at the item "document" for the server and the clients.
        //In our model, clients can only handle one file at a time, but the server can create 
        //and have many documents.  Therefore, we shall simulate another batch of clients joining
        //and starting a new document.  
        
        OperationEngine oe6 = new OperationEngine(6);
        OperationEngine oe7 = new OperationEngine(7);
        
        Operation newop16 = oe6.push(true, "doc2", "hello", "insert", 0, 6, temp, 0);
        int newOrder = 0;
        newop16.setOrder(newOrder);
        newOrder++;
        Operation op121 = oe1.push(true, "document", "world", "delete", 6, 1, temp, 0);
        op121.setOrder(order);
        order++;
        
        Operation newop10 = oes.pushRemoteOp(newop16);
        Operation op120 = oes.pushRemoteOp(op121);
        Operation newop17 = oe7.pushRemoteOp(newop16);
        
        assertEquals(op120.getPosition(), 6);
        assertEquals(newop10.getPosition(), 0);
        assertEquals(newop16.getPosition(), 0);
        assertEquals(newop17.getPosition(), 0);
        
        
    }
   
    
    
    
    
    
}
