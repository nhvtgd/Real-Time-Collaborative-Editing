package controller;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import server_client.CollabServer;
import ui.ServerGui;
/*
 * Test strategy:
 * This is tested in Gui by creating multiple users with multiple documents
 * The server will be able to switch between screens to observe the change of
 * other documents currently editing by other clients. The switching doesn't cause
 * any side effect on the editing of document of other users.
 * This can't be tested through JUnit since there is no mechanism to perform
 * a select on JList withouth incurring infinite loop. Therefore, this is most
 * appropriate to test in the Gui*/
/**
 * This class is a controller that will listen to the selection of the server to
 * switch between documents that currently editing
 * Thread-safe argument: 
 * 	The method that change the document view is run in EDT so it won't interfere with
 * 	the main thread that running the program
 * @author viettran
 */
public class DocumentSelectionListener {
	/** The collab Server that store the information about the documents */
	private final CollabServer collabServer;
	/** The Gui that send the changes to the controller */
	private final ServerGui serverGui;

	/**
	 * This constructor take in the view and the server model and add the
	 * listener to the view
	 * 
	 * @param serverGui
	 *            the server view
	 * @param collabServer
	 *            the model that store information
	 */
	public DocumentSelectionListener(ServerGui serverGui,
			CollabServer collabServer) {
		this.serverGui = serverGui;		
		this.collabServer = collabServer;
		serverGui.addListSelectionListener(new DocumentSelection());
	}

	/**
	 * This is a helper class that implements listSelectionListener to listen to
	 * changes in the document and called the model and effectly switching views
	 * between documents
	 */
	class DocumentSelection implements ListSelectionListener {
		/**
		 * Called when the selection is no longer adjusting and if there is
		 * document selected, it will let the collabServer switch view between
		 * documents
		 */
		
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (e.getValueIsAdjusting() == false) {
						int index = serverGui.getListOfDocuments()
								.getSelectedIndex();
						if (index != -1) {
							collabServer.switchScreen((String) serverGui
									.getListOfDocuments().getModel()
									.getElementAt(index));
						}
					}

				}
			});

		}

	}

}
