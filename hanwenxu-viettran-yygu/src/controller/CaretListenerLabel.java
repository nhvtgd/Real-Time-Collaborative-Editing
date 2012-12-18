package controller;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/*Test Strategy:
 * The caret shows at the bottom of the documents of client and server
 * This is tested by asking different users to type at the same time and check the 
 * position show at every character. This is tested in both clientGui and ServerGui
 * 
 * */
@SuppressWarnings("serial")
/**
 * This class will listen to the carret changes in the documents and shows it 
 * as the JLabel at the bottom of the document including the current position
 * It plays the role of both the listener and the JLabel components
 * Thread safe-argument:
 * This is thread safe since the action that modifies the TextArea is invoked in Swing
 * thread which won't interfere in the main thread currently running.
 * @author viettran
 *
 */
public class CaretListenerLabel extends JLabel implements CaretListener {
	/** Get the new line of the OS */
	private static final String newline = "\n";

	/**
	 * This constructor take in the string Label of the carret and the textPane
	 * where the caret originate from
	 * 
	 * @param label
	 * @param textPane
	 */
	public CaretListenerLabel(String label) {
		super(label);

	}

	// Might not be invoked from the event dispatch thread.
	@Override
	public void caretUpdate(CaretEvent e) {
		displaySelectionInfo(e.getDot(), e.getMark());

	}

	/**
	 * Set the Caret position status for the text Document Need to invoke in the
	 * event dispatching thread in order to keep the thread safe
	 * 
	 * @dot: int the location of the caret
	 * @mark: int the location of other end of a logical selection. If there is
	 *        no selection, this will be the same as dot. Modifies: label object
	 */
	protected void displaySelectionInfo(final int dot, final int mark) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (dot == mark) { // no selection
					// Convert it to view coordinates.
					setText("Text position: " + dot + newline);
				} else if (dot < mark) {
					setText("Selection from: " + dot + " to " + mark + newline);
				} else {
					setText("Selection from: " + mark + " to " + dot + newline);
				}
			}
		});
	}
}
