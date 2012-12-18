package ui;

import javax.swing.JList;
import javax.swing.event.ListSelectionListener;

import model.CollabModel;
import server_client.CollabInterface;
import server_client.CollabServer;
import controller.DocumentSelectionListener;
import controller.TextChangeListener;
import document.OperationEngineException;

/*
 * Testing Strategy:
 * See ClientGui
 * In addition to what currently tested in ClientGUI, the text documents of the server
 * is uneditable, everything else is identical to the strategy indicated in Client Gui
 * So we can tested by trying to edit the documents
 * 
 * This follows the MVC design pattern too*/

/**
 * ServerGui inherit from Edit Gui, it includes some methods to get the
 * collabModel getSideID and setModelKey. Thread-safe argument This is
 * thread-safe for the same reason as Client GUI since it is run in the seperate
 * swing thread which won't interfere with the main thread. See Client GUI
 * thread-safe argument for more information
 * 
 * @author
 * 
 */

public class ServerGui extends ClientGui {

	private static final long serialVersionUID = -1426186299786063098L;
	/** The underlying model of both client and server */
	private final CollabModel collabModel;
	//private final DocumentSelectionListener controller;
	private final CollabServer collabServer;
	/** The initial String to show in the Documents */
	private static final String WELCOME_MESSAGE = "Welcome to Collab Edit";
	/**
	 * The name to show on top of the documents to see whose document belong to
	 */
	private static final String PROMPT_FOR_SERVER = "Server for document: ";

	/**
	 * ServerGui sets up the GUI for the local client
	 * 
	 * @param cs
	 *            : The CollabInterface which is implemented by both
	 *            CollabClient and CollabServer
	 * @param serverName
	 *            : The name of the Server modifies: TextPane of the
	 *            collabServer is uneditable
	 * @throws OperationEngineException
	 * 
	 */
	public ServerGui(CollabInterface cs, String serverName)
			throws OperationEngineException {
		super(WELCOME_MESSAGE, PROMPT_FOR_SERVER + serverName);
		collabServer = (CollabServer) cs;
		collabModel = new CollabModel(textArea, collabServer);
		// listen to change in the document
		textArea.getDocument().addDocumentListener(
				new TextChangeListener(this, collabModel));
		textArea.setEditable(false);

		// add the controller here and add model to view
		new DocumentSelectionListener(this, collabServer);

	}

	/**
	 * @return the default sizeID of the server is 0
	 */
	@Override
	public int getSiteID() {
		return 0;
	}

	/**
	 * @return the REFERENCE to the underlying CollabModel of the server
	 */
	@Override
	public CollabModel getCollabModel() {
		return this.collabModel;
	}

	/**
	 * This will access the CollabModel and set the document key
	 * 
	 * @param str
	 *            Modifies collabModel by changing its siteID
	 */
	@Override
	public void setModelKey(String str) {
		this.collabModel.setKey(str);
	}

	/**
	 * Documents listener to add the model to the view
	 * 
	 * @param l
	 *            the List Selection Listener
	 */
	public void addListSelectionListener(ListSelectionListener l) {
		documentList.addListSelectionListener(l);
	}

	/**
	 * This method returns the REFERENCE of the JList that currently store the
	 * list of documents in the GUI
	 * 
	 * @return JList
	 */
	public JList getListOfDocuments() {
		return documentList;
	}

}
