package model;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;

import server_client.CollabInterface;

import document.ClientState;
import document.Operation;
import document.OperationEngine;
import document.OperationEngineException;

/**
 * to Hold the main Document of the Collab edit, might implement on top of gap
 * buffer
 * 
 * @author viettran
 * 
 */
public class CollabModel extends DefaultStyledDocument {

    
    /**
     * This is the code used to represent this serializable object
     */
    private static final long serialVersionUID = -882083489483528319L;

    /**
     * This is the string that we are storing. For debugging, mainly
     */
    private StringBuilder buffer;
    
    /**
     * This is the document we are editing
     */
    private JTextPane mainDocument;
    
    /**
     * This is the client's local operationEngine
     */
    private OperationEngine oe;
    
    /**
     * This is the collabInterface, either a server or client
     */
    private CollabInterface collab;
    
    /**
     * This is the unique identifying key showing which document the client is editing
     */
    private String OPKEY = "document";
    
    /**
     * permanent string, no more magic string
     */
    private final static String INSERT = "insert";
    
    /**
     * permanent string, no more magic string
     */
    private final static String DELETE = "delete";
    
    /**
     * Local pointer, for making sure remote changes don't get retransmitted
     */
    public boolean remote = false;
    
    /**
     * This is the unique clientID. 
     */
    private int siteID;

    /**
     * This will be deprecated because it lacks connectivity to the
     * mainDocument. This only serves as a testing tool to see if the buffer
     * correctly matches the gui for local operations. The siteID is hack coded
     * 
     * @param init
     *            , initial string found in the gui
     * @throws OperationEngineException
     */
    public CollabModel(String init) throws OperationEngineException {
        int tempID = 1;
        this.buffer = new StringBuilder(init);
        this.oe = new OperationEngine(tempID);
        collab = null;
        mainDocument = null;
    }

    /**
     * This constructor will be the primary constructor. We will also add
     * connectivity to the CollabClient so that they can send operation messages
     * to the server
     * 
     * @param mainDocument
     * @param siteID
     * @throws OperationEngineException
     */
    public CollabModel(JTextPane mainDocument, int siteID)
            throws OperationEngineException {
        this.buffer = new StringBuilder(mainDocument.getText());
        this.mainDocument = mainDocument;
        this.oe = new OperationEngine(siteID);
        collab = null;
    }

    /**
     * This constructor will be the primary constructor. We will also add
     * connectivity to the CollabClient so that they can send operation messages
     * to the server
     * 
     * @param mainDocument
     * @param siteID
     * @param collab
     * @throws OperationEngineException
     */
    public CollabModel(JTextPane mainDocument, CollabInterface collab)
            throws OperationEngineException {
        this.buffer = new StringBuilder(mainDocument.getText());
        this.mainDocument = mainDocument;
        siteID = collab.getID();
        this.oe = new OperationEngine(siteID);
        this.collab = collab;
    }

    /**
     * This is called by the TextChangeListener to update the local
     * OperationEngine and the StringBuilder This function will also call the
     * collabClient, and tell it to send an operation to the server.
     * 
     * @param offset
     *            , requires to be less than length of the builder. This is the
     *            offset of the string to be added to the buffer
     * @param text
     *            , requires to be valid text. This is the text to be inserted
     *            into the buffer
     * @param siteID
     *            , requires to be a valid siteID, otherwise ContextVector will
     *            be wrong. This is the identification number of the siteID
     *            originating this operation
     * @param styleOfText
     *            , requires to be normal for now. This is the style of the text
     *            being modified
     * @throws OperationEngineException
     */
    public Operation insertString(int offset, String text, int siteID,
            AttributeSet styleOfText) throws OperationEngineException {
        int[] temp = new int[0];
        Operation top = oe.push(true, OPKEY, text, INSERT, offset, siteID,
                temp, 0);
        // buffer.insert(offset, text);
        if (collab != null) {
            try {
                collab.transmit(top);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return top;
    }

    /**
     * This is called by the TextChangeListener to update the local
     * OperationEngine and the StringBuilder This function will also call the
     * collabClient, and tell it to send an operation to the server.
     * 
     * @param offset
     *            , requires to be less than length of the builder. This is the
     *            offset of the string to be added to the buffer
     * @param text
     *            , requires to be valid text. This is the text to be inserted
     *            into the buffer

     * @param styleOfText
     *            , requires to be normal for now. This is the style of the text
     *            being modified
     * @throws OperationEngineException
     */
    public void addString(int offset, String text, AttributeSet styleOfText)
            throws OperationEngineException {
        int[] temp = new int[0];
        Operation top = null;
        top = oe.push(true, OPKEY, text, INSERT, offset, siteID, temp, 0);

        // buffer.insert(offset, text);
        if (collab != null) {
            try {
                collab.transmit(top);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * This is called by the TextChangeListener to update the OperationEngine
     * and the StringBuilder This function will also call the collabClient, and
     * tell it to send an operation to the server.
     * 
     * @param offset
     *            , requires to be less than length of the builder. This is the
     *            offset of the string to be added to the buffer
     * @param text
     *            , requires to be valid text. This is the text to be inserted
     *            into the buffer
     * @param siteID
     *            , requires to be a valid siteID, otherwise ContextVector will
     *            be wrong. This is the identification number of the siteID
     *            originating this operation
     * @param styleOfText
     *            , requires to be normal for now. This is the style of the text
     *            being modified
     */
    public Operation deleteString(int offset, int length, int siteID,
            AttributeSet styleOfText) throws OperationEngineException {
        int[] temp = new int[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        Operation top = oe.push(true, OPKEY, sb.toString(), DELETE, offset,
                siteID, temp, 0);
        buffer.delete(offset, offset+length);
        if (collab != null) {
            try {
                collab.transmit(top);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return top;
    }

    /**
     * 
     * @param offset
     * @param length
     * @param styleOfText
     * @throws OperationEngineException
     */
    public void deleteString(int offset, int length, AttributeSet styleOfText)
            throws OperationEngineException {
        int[] temp = new int[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        Operation top = oe.push(true, OPKEY, sb.toString(), DELETE, offset,
                siteID, temp, 0);
        // buffer.delete(offset, offset+length);
        if (collab != null) {
            try {
                collab.transmit(top);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * This is the remoteInsert function. It will be called by the collabClient
     * 
     * @param op
     *            , this is the Operation that is sent from the server. We will
     *            use the local operation engine to return a transformed
     *            operation that will allow us to modify the buffer and the
     *            mainDocument. We directly access the main document.
     * @throws OperationEngineException
     * @throws BadLocationException
     *             - thrown when an invalid insert occurs
     */
    public Operation remoteInsert(Operation op)
            throws OperationEngineException, BadLocationException {
        if (op.getKey() == null)
            return null;
        if (op.getKey().equals(OPKEY)) {
            //System.out.println("opkey equals");
            final Operation top = oe.pushRemoteOp(op);
            if (top == null) {
                // System.out.println(siteID+" has received the following operation remotely: null");
                // throw new
                // RuntimeException("can't perform same operation twice");
                return top;
            }
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        // System.out.println(siteID+" has received the following operation remotely, and is about to push: "+op.toString());
                        mainDocument.setEditable(false);
                        // mainDocument.getCaret().setVisible(true);

                        // System.out.println(siteID+" has processed the remote operation: "+top.toString());
                        int offset = top.getPosition();
                        String value = top.getValue();

                        // not sure if the buffer is really necessary, since we
                        // can just directly change the gui
                        // buffer.insert(offset, value);
                        AttributeSet temp = new SimpleAttributeSet();
                        if (mainDocument != null) {
                            remote = true;
                            try {
                                mainDocument.getDocument().insertString(offset,
                                        value, temp);
                                int caretPos = mainDocument.getCaretPosition();
                                if (offset < caretPos) {
                                    int max = mainDocument.getDocument()
                                            .getLength();
                                    int newpos = Math.min(caretPos, max);
                                    newpos = Math.max(0, newpos);
                                    mainDocument.setCaretPosition(newpos);
                                }
                            } catch (BadLocationException e) {
                                throw new RuntimeException(e);
                            }
                            mainDocument.setEditable(true);

                        }
                    }
                });

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return top;
        } else {
            //System.out.println("opkey does not equals");
            //System.out.println("OPKEY is: " + OPKEY);
            //System.out.println("op.key is:" + op.getKey());
            return null;
        }
    }

    /**
     * This is the remoteDelete function. It will be called by the collabClient
     * 
     * @param op
     * @throws OperationEngineException
     * @throws BadLocationException
     */
    public Operation remoteDelete(Operation op)
            throws OperationEngineException, BadLocationException {
        // System.out.println(siteID+" has received the following operation remotely, and is about to push: "+op.toString());
        if (op.getKey() == null)
            return null;
        if (op.getKey().equals(OPKEY)) {
            // mainDocument.getCaret().setVisible(true);
            final Operation top = oe.pushRemoteOp(op);
            if (top == null) {
                // System.out.println(siteID+" has received the following operation remotely: null");
                return top;
                // throw new
                // RuntimeException("can't perform same operation twice");
            }
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        mainDocument.setEditable(false);
                        // System.out.println(siteID+" has processed the remote operation: "+top.toString());
                        int offset = top.getPosition();
                        String value = top.getValue();

                        // buffer.delete(offset, offset+value.length());
                        if (mainDocument != null) {
                            remote = true;
                            try {
                                mainDocument.getDocument().remove(offset,
                                        value.length());
                                int caretPos = mainDocument.getCaretPosition();
                                if (offset < caretPos) {
                                    int max = mainDocument.getDocument()
                                            .getLength();
                                    int newpos = Math.min(
                                            caretPos, max);
                                    newpos = Math.max(0, newpos);
                                    mainDocument.setCaretPosition(newpos);
                                }
                            } catch (BadLocationException e) {
                                throw new RuntimeException(e);
                            }
                            mainDocument.setEditable(true);
                            
                        }

                    }
                });
                // System.out.println(siteID+" has processed the remote operation: "+top.toString()
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return top;
        } else {
            return null;
        }
    }

    /**
     * For debug purposes, we would like to see what the buffer is holding.
     */
    @Override
    public String toString() {
        return buffer.toString();
    }

    /**
     * This will get the associated operationEngine
     * @return OperationEngine associated with this client
     */
    public OperationEngine getOE() {
        return this.oe;
    }

    /**
     * Sets the ClientState associated with this client
     * @param cv
     */
    public void setCV(ClientState cv) {
        oe.setCV(cv);
    }

    /**
     * Return the a copy of hte ClientSTate of this client
     * @return ClientSTate this clientSTate
     * @throws OperationEngineException
     */
    public ClientState copyOfCV() throws OperationEngineException {
        return oe.copyClientState();
    }

    /**
     * Sets the document key
     * @param str
     */
    public void setKey(String str) {
        this.OPKEY = str;
    }
}
