package controller;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import model.CollabModel;
import ui.ClientGui;
import document.OperationEngineException;

/*This is tested in Client Gui and Server GUI
 * by typing in the documents and observing the changes appropriately
 * This is most appropriate to test in the Gui to observer if any insertion, deletion
 * or Attribute change will get reflecte in the command line with the correct change.*/

/**
 * This class is the controller listener for changes in the main document, then
 * it will access the collabModel and make the update for the document
 * appropriately
 * 
 * @author viettran
 * 
 */
public class TextChangeListener implements DocumentListener {
    @SuppressWarnings("unused")
	/** clientGui or serverGUI */	
    private final ClientGui gui;
	/** the underlying model */
	private final CollabModel model;

	/**
	 * This construct takes in the view and the model and make the appropriate
	 * changes
	 * 
	 * @param gui
	 *            the clientGui or serverGui
	 * @param model
	 *            the underlying model
	 */
	public TextChangeListener(ClientGui gui, CollabModel model) {
		this.gui = gui;
		this.model = model;
	}

	/**
	 * Unsupported for this program since we won't record any attribute change
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {

	}

	/**
	 * This will get the insert Event from the model and get the text that
	 * change and insert it to the appropriate place in the documents and send
	 * out to other clients
	 * 
	 * @param e
	 *            the insert Event modifies: collabModel
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {		
		int pos = e.getOffset();

		String change = null;
		try {
			change = e.getDocument().getText(pos, e.getLength());
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}

		if (!model.remote) {
			AttributeSet normal = new SimpleAttributeSet();
			try {
				model.addString(pos, change, normal);
			} catch (OperationEngineException e1) {
				e1.printStackTrace();
			}
		} else {
			model.remote = false;
		}

	}

	/**
	 * This will get the delete Event from the model and get the text that
	 * change and delete it to the appropriate place in the documents and send
	 * out notification to other clients if they exit.
	 * 
	 * @param e
	 *            the insert Event modifies: collabModel
	 * */
	@Override
	public void removeUpdate(DocumentEvent e) {
		int pos = e.getOffset();

		int change = 0;
		change = e.getLength();

		if (!model.remote) {
			AttributeSet normal = new SimpleAttributeSet();
			try {
				model.deleteString(pos, change, normal);
			} catch (OperationEngineException e1) {
				e1.printStackTrace();
			}
		} else {
			model.remote = false;
		}
	}

}
