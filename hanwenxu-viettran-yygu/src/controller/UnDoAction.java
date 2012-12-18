package controller;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import ui.ErrorDialog;

/* Test
 * This action is already tested in Client and Server Gui by clicking the undo button
 * It will throw cannotUndoException if there is nothing to undo but this won't interrupt
 * the editing process
 * See client Gui for more detail*/
/**
 * This class will make the undo action when the user performs the click using
 * UndoManager Thread-safe argument: This is thread-safe as indicated in the
 * Javadoc of UndoManager
 * 
 * @author viettran
 * 
 */

public class UnDoAction extends AbstractAction {
	private static final long serialVersionUID = -5124268535759633915L;
	/**
	 * This maintains an ordered list of edits and the index of the next edit in
	 * that list.
	 */
	private final UndoManager manager;

	/**
	 * This constructor take in an UndoManager to perform undo and redo action
	 * 
	 * @param manager
	 */
	public UnDoAction(UndoManager manager) {
		this.manager = manager;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			manager.undo();
		} catch (CannotUndoException e) {
			new ErrorDialog("Nothing to Undo man");
			Toolkit.getDefaultToolkit().beep();
		}
	}

}
