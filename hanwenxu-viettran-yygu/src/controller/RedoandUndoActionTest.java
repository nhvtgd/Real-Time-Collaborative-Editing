package controller;

import static org.junit.Assert.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

import org.junit.*;

/*
 * Test Strategy: 
 * InsertString of some length then undo
 * after undo is clicked, redo should be clicked too
 * insert empty string then undo should not throw exception
 * don't insert any thing and click undo should not throw an exception
 * insert string 2 time , undo one time then redo two time should throw CannotRedoException
 * 
 * 
 */
@SuppressWarnings("serial")
public class RedoandUndoActionTest extends JFrame {

	@Test
	public void insertSomeString() throws BadLocationException {

		JTextPane text = new JTextPane();
		JButton undoButton = new JButton("Undo");
		JButton redoButton = new JButton("Redo");
		UndoManager manager = new UndoManager();
		undoButton.addActionListener(new UnDoAction(manager));
		redoButton.addActionListener(new RedoAction(manager));
		text.getDocument().addUndoableEditListener(manager);
		text.getDocument().insertString(0, "test", new SimpleAttributeSet());
		undoButton.doClick();
		assertEquals(text.getDocument().getLength(), 0);
		redoButton.doClick();
		assertEquals(text.getDocument().getLength(), 4);

	}
	
	@Test
	public void insertEmptyString() throws BadLocationException {

		JTextPane text = new JTextPane();
		JButton undoButton = new JButton("Undo");
		JButton redoButton = new JButton("Redo");
		UndoManager manager = new UndoManager();
		undoButton.addActionListener(new UnDoAction(manager));
		redoButton.addActionListener(new RedoAction(manager));
		text.getDocument().addUndoableEditListener(manager);
		text.getDocument().insertString(0, "", new SimpleAttributeSet());
		undoButton.doClick();
		assertEquals(text.getDocument().getLength(), 0);
		

	}
	
	@Test
	public void oneUndo() throws BadLocationException {
		JTextPane text = new JTextPane();
		JButton undoButton = new JButton("Undo");
		JButton redoButton = new JButton("Redo");
		UndoManager manager = new UndoManager();
		undoButton.addActionListener(new UnDoAction(manager));
		redoButton.addActionListener(new RedoAction(manager));
		text.getDocument().addUndoableEditListener(manager);
		undoButton.doClick();
		
	
		

	}
	
	@Test(expected = CannotRedoException.class)
	public void illegalRedo() throws BadLocationException {

		JTextPane text = new JTextPane();
		JButton undoButton = new JButton("Undo");
		JButton redoButton = new JButton("Redo");
		UndoManager manager = new UndoManager();
		undoButton.addActionListener(new UnDoAction(manager));
		redoButton.addActionListener(new RedoAction(manager));
		text.getDocument().addUndoableEditListener(manager);
		text.getDocument().insertString(0, "test", new SimpleAttributeSet());
		text.getDocument().insertString(5, "test", new SimpleAttributeSet());
		undoButton.doClick();
		redoButton.doClick();
		redoButton.doClick();
		

	}

}
