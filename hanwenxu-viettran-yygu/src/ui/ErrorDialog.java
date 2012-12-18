package ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

/* Test Strategy: 
 By generate all type of error causing by the user such as wrong IP and Port,
 * 	client connects without the server open, server reconnects when it's already running, 
 *  We can close it and it will close down without effecting any part of the program.
 *  
 * 
 */

/**
 * This class will generate a pop up indicate the type of error that occur
 * during the connection. It won't affect anything in the program.
 * 
 * Thread-safe: This is thread-safe since it seperate from any other thread and
 * it launches independently from other threads. Will close down when the user
 * close it.
 * 
 * @author viettran
 * 
 * 
 */
public class ErrorDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7549769136652082371L;

	@SuppressWarnings("serial")
	public ErrorDialog(String message) {
		this.setLayout(new GridLayout(0, 1));
		this.add(new JLabel(message, JLabel.CENTER));
		this.add(new JButton(new AbstractAction("Close") {

			@Override
			public void actionPerformed(ActionEvent e) {
				ErrorDialog.this.setVisible(false);
				ErrorDialog.this.dispatchEvent(new WindowEvent(
						ErrorDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		}));
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println(e.paramString());
			}
		});

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

}
