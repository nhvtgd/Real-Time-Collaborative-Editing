package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;

import model.CollabModel;
import server_client.CollabClient;
import server_client.CollabInterface;
import controller.CaretListenerLabel;
import controller.RedoAction;
import controller.TextChangeListener;
import controller.UnDoAction;
import document.OperationEngineException;

/*Test Strategy:
 * Current client Gui support BOLD, ITALIC, UNDERLINE COPY, CUT, PASTE, so I have tested 
 * all of those functions by observing the changes in the selected text when I perform
 * a click on those Button. If there is a selected text, the action will be carried out.
 * For BOLD,ITALIC, UNDERLINE if there is no selected text, it will change the font of the whole 
 * document. BOLD, ITALIC, UNDERLINE currently support local changes only. 
 * 
 * COPY , CUT and PASTE don't do anything if there is nothing selected or nothing store 
 * in the clipboard. The user can click on these three buttons or using the standard CTRL + C, CTRL +X, CTRL + PASTE
 * on selected text to perform the respective operation.
 * 
 * UNDO and REDO will be tested by letting multiple user typing, and undo should perform the last action 
 * of all of the users, same things will work for redo. It will generate CanUndoException in the console and 
 * popup an error Dialog if there nothing to undo or redo, but it won't affect the documents in any way. There is
 * extra shortcut for UNDO (CTRL + Z) and REDO (CTRL + Y) if the user doesn't want to click.
 * 
 * For resizing, all of the components are appropriately resizing after I expand the window.
 * The scrollPane in the Who is viewing using horizontal Wrap so if more users joining the documents, it will
 * add it in vertically, and user can scroll down to view all people. 
 * 
 * The Caret is shown at the bottom of the main documents and can be tested by move the UP, DOWN, LEFT, RIGHT or button click
 * and compare with different users for consistency.
 * 
 * ScrollPane is automatically scrolling down when display area is full. The displayUser box also shows each user name 
 * row by row and automatically scroll down if there is overflow in the displayUser box.
 * The clientGUI after the client connects to the server, it will display everything in the current state 
 * of the server textArea and the current users in the chatroom/editing (not including the ChatArea),
 * this also get tested by connecting to the server mutliple time and observe the state of the GUI.
 * 
 * The SrollPane that displays all of the current documents are also display and update when new user connects. This is tested 
 * by creating multiple clients with multiple documents to see if the display is consistent across all users and servers
 * 
 * 
 * All of this procedure has to be thread-safe by performing the transform operation to all of the user/client pairs
 * For concurrency, the gui can be tested by doing the same procedure and observe if they're 
 * reflected upon all users. 
 * 
 * The server will update its userLoginPanel whenever a client connects to it, this has been
 * tested by connected multiple clients to the server and its show the updated clients list in the userLoginPane.
 * When the user logs out, the server will reflect that in the userLoginPanel by removing the name as the specific
 * location. All of this changes will be view by both server and client.
 * The server will also update the list of documents if new documents are created and removed it from the view
 * if no one edits it any more. These features will be shown to all of the other clients.
 * 
 * */

/*
 * Design pattern
 * Client Gui, the main gui of the collaborative editing This implement the MVC
 * pattern where the view is what the user sees and can interact with (i.e The
 * Toolbar, main Document, who is viewing, list of documents), the model is
 * where those information should be store when there is some changes in the
 * view after the user interact with it. Some models that implement in these
 * design are DefaultListModel to control who views/edit the document,
 * DefaultStyleDocuments collabModel is the model for the collab edit main document which will
 * store the current state of the shared documents or separate documents),
 * the DefaultListModel listOfDocuments also stores the list of the documents 
 * currently editing. The controllers are the buttons that listen to the changes 
 * in the documents including all of the button in the tools bar, the carret listeners,
 * the DocumentsListener. The controller will have get and set method to call and 
 * make changes on the view and the model appropriately. This makes the program
 * achieve modularity and seperate the concern on some of the components. We also 
 * utilize the GUI internal Model for all of the JTextPane, JList so that we don't 
 * need to reinvent the wheel and also store the model effectly without causing 
 * too much Memory usage*/

/**
 * The main GUI of the client and server (the server GUI is identical to
 * clientGUI due to internal design except the server can't edit the documents).
 * This GUI will pop up after getting enough information from the users about
 * the name,IP address, and port.
 * 
 * Thread-safety argument: This GUI is thread safe because the OT transform
 * won't rely on any thread running seperately from the clients. Also, this is
 * run on SwingUtilities.invokeLater thread which is Swing thread, so it won't
 * affect the main thread that handle the main logic of the program. All of its
 * listener also carried out in seperate Swing Thread to maintain the thread
 * safety.
 * 
 * @author viettran
 * 
 */
public class ClientGui extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2826617458739559881L;
	/** The initial text of the document */
	private String init = "";

	/** Main document of the collaborative editing */
	protected JTextPane textArea;
	/** toggle Bold Button to bold text */
	protected JToggleButton boldButton;
	/** toggle Italic Button to italicize text */
	protected JToggleButton italicButton;
	/** toggle Underline Button to underline text */
	protected JToggleButton underlineButton;
	/** make copy of document */
	protected JButton copyButton;
	/** paste the content in the clipboard */
	protected JButton pasteButton;
	/** delete the content in the clipboard */
	protected JButton cutButton;
	/** list of all of the users currently editing */
	protected JList userList;
	/** the pane that whole the display of the list of users */
	protected JScrollPane chatScrollPane;
	/** the pane to display the content of the chat */
	protected JTextArea chatDisplay;

	/** the input of the chat */
	protected JTextField chatInput;
	/** the pane that store the collab edit GUI */
	protected JPanel wholePane;
	/**
	 * this Model control the user in userLogin Make it easy to add or remove
	 * Element
	 */
	protected DefaultListModel listModel;
	/** the model that store the current state of the Document */
	private CollabModel collabModel;

	/** the client */
	private CollabClient collabClient;
	/** the interface for both client and server */
	// private CollabInterface collabInterface;

	/**
	 * this Model control the documents in the program Make it easy to add or
	 * remove Element
	 */
	private DefaultListModel documentListModel;

	/** Label will be put on Client */
	private final String label;
	/** list of all of the documentss currently editing */
	protected JList documentList;

	/**
	 * EditGui sets up the GUI for the local client
	 * 
	 * @throws OperationEngineException
	 * 
	 */
	public ClientGui(String init, String label) throws OperationEngineException {
		this.label = label;
		this.init = init;
		createGUI();

	}

	/**
	 * EditGui sets up the GUI for the local client
	 * 
	 * @throws OperationEngineException
	 * 
	 */
	public ClientGui(String init, CollabInterface cc, String label)
			throws OperationEngineException {
		this.label = label;
		this.init = init;
		// create the GUI for this user
		createGUI();
		// create the model that take in the content of the textArea and a site
		// id
		collabClient = (CollabClient) cc;
		collabModel = new CollabModel(textArea, cc);
		// listen to change in the document
		textArea.getDocument().addDocumentListener(
				new TextChangeListener(this, collabModel));

	}

	/**
	 * Created the initial GUI which is the same for both client and server it
	 * also adds all of the necessary listener to the appropriate components
	 * inside the GUI
	 * */
	void createGUI() {

		setLayout(new BorderLayout());

		FlowLayout toolBarLayout = new FlowLayout();
		toolBarLayout.setAlignment(FlowLayout.LEFT);
		JPanel toolBarPane = new JPanel();

		// Create all of the buttons
		ImageIcon bold = new ImageIcon("raw/bold.png");
		boldButton = new JToggleButton(bold);
		boldButton.setToolTipText("BOLD TEXT");
		// Action boldAction = new BoldAction();
		boldButton.addActionListener(new StyledEditorKit.BoldAction());

		ImageIcon italic = new ImageIcon("raw/italic.png");
		italicButton = new JToggleButton(italic);
		italicButton.setToolTipText("ITALICIZE TEXT");
		italicButton.addActionListener(new StyledEditorKit.ItalicAction());

		ImageIcon underline = new ImageIcon("raw/underline.png");
		underlineButton = new JToggleButton(underline);
		underlineButton.setToolTipText("UNDERLINE TEXT");
		underlineButton
				.addActionListener(new StyledEditorKit.UnderlineAction());

		// copy key
		ImageIcon copy = new ImageIcon("raw/copy.png");
		copyButton = new JButton(copy);
		copyButton.setToolTipText("Copy Text");
		copyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.copy();

			}
		});
		// paste key
		ImageIcon paste = new ImageIcon("raw/paste.png");
		pasteButton = new JButton(paste);
		pasteButton.setToolTipText("Paste Text");
		pasteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.paste();

			}
		});

		// cut key
		ImageIcon cut = new ImageIcon("raw/cut.png");
		cutButton = new JButton(cut);
		cutButton.setToolTipText("Cut Text");
		cutButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.cut();
			}
		});

		ImageIcon undo = new ImageIcon("raw/undo.png");
		JButton undoButton = new JButton(undo);

		ImageIcon redo = new ImageIcon("raw/redo.png");

		JButton redoButton = new JButton(redo);
		JMenuBar toolBar = new JMenuBar();

		// add all button to toolBar
		toolBar.add(boldButton);
		toolBar.add(italicButton);
		toolBar.add(underlineButton);
		toolBar.add(copyButton);
		toolBar.add(pasteButton);
		toolBar.add(cutButton);
		toolBar.add(undoButton);
		toolBar.add(redoButton);
		toolBarPane.setLayout(toolBarLayout);
		toolBarPane.add(toolBar);

		// Create a text area.
		textArea = createTextPane();
		CaretListenerLabel caretLabelListener = new CaretListenerLabel(
				"Caret Status:");
		textArea.addCaretListener(caretLabelListener);

		// carry out doing
		UndoManager manager = new UndoManager();
		textArea.getDocument().addUndoableEditListener(manager);
		Action undoAction = new UnDoAction(manager);
		undoButton.addActionListener(undoAction);

		Action redoAction = new RedoAction(manager);
		redoButton.addActionListener(redoAction);
		this.registerKeyboardAction(undoAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK),
				JComponent.WHEN_FOCUSED);
		this.registerKeyboardAction(redoAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK),
				JComponent.WHEN_FOCUSED);

		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(250, 250));
		areaScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(label),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)),
				areaScrollPane.getBorder()));

		userList = createUserLogin(new String[] { "No users right now" });
		JScrollPane userLoginScrollPane = new JScrollPane(userList);
		userLoginScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		userLoginScrollPane.setPreferredSize(new Dimension(250, 145));
		userLoginScrollPane.setMinimumSize(new Dimension(10, 10));

		// Create a chat display
		JPanel documenAreaPane = new JPanel();
		GroupLayout documentsListGroupLayout = new GroupLayout(documenAreaPane);

		documentList = createDocumentsLogin(new String[] { "No documents right now" });

		JLabel documentsLabel = new JLabel("List of Documents");
		JScrollPane documentsListScrollPane = new JScrollPane(documentList);
		userLoginScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		userLoginScrollPane.setPreferredSize(new Dimension(250, 145));
		userLoginScrollPane.setMinimumSize(new Dimension(10, 10));

		documentsListGroupLayout.setHorizontalGroup(documentsListGroupLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(userLoginScrollPane).addComponent(documentsLabel)
				.addComponent(documentsListScrollPane));

		documentsListGroupLayout.setVerticalGroup(documentsListGroupLayout
				.createSequentialGroup().addComponent(userLoginScrollPane)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(documentsLabel)
				.addComponent(documentsListScrollPane));
		documenAreaPane.setLayout(documentsListGroupLayout);

		JPanel rightPane = new JPanel(new GridLayout(1, 0));
		rightPane.add(documenAreaPane);
		rightPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Who is viewing"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Put everything together.
		JPanel leftPane = new JPanel(new BorderLayout());

		leftPane.add(toolBarPane, BorderLayout.PAGE_START);
		leftPane.add(areaScrollPane, BorderLayout.CENTER);
		leftPane.add(caretLabelListener, BorderLayout.SOUTH);

		wholePane = new JPanel();
		GroupLayout allGroup = new GroupLayout(wholePane);
		allGroup.setHorizontalGroup(allGroup.createSequentialGroup()
				.addComponent(leftPane).addComponent(rightPane));
		allGroup.setVerticalGroup(allGroup
				.createParallelGroup(Alignment.BASELINE).addComponent(leftPane)
				.addComponent(rightPane));
		wholePane.setLayout(allGroup);
		add(wholePane);
	}

	/**
	 * update the text at the new position Modifies: The main Collab Edit text
	 * 
	 * @param text
	 *            the String to be inserted to the document
	 * @param position
	 *            the position of the string
	 * @throws BadLocationException
	 */

	protected void setTextToDocument(String text, int position)
			throws BadLocationException {
		StyledDocument doc = textArea.getStyledDocument();
		doc.insertString(position, text, doc.getStyle("regular"));

	}

	/**
	 * update the text at the new position with some initial textStyle Modifies:
	 * The main Collab Edit text
	 * 
	 * @param text
	 *            the String to be inserted to the document
	 * @param position
	 *            the position of the string
	 * 
	 * @throws BadLocationException
	 */

	protected void setTextToDocument(String text, int position, Style textStyle)
			throws BadLocationException {
		StyledDocument doc = textArea.getStyledDocument();
		doc.insertString(position, text, textStyle);

	}

	/**
	 * Update the new list of user currently editting the document if there is
	 * people in the chat room, it will be replaced by by this new user lists
	 * Modifies listModel
	 * 
	 * @param objects
	 *            String of users' name
	 * 
	 */
	public void updateUsers(Object[] objects) {
		listModel.clear();
		if (objects.length > 0) {
			for (Object i : objects)
				listModel.addElement(i);
		}
		wholePane.revalidate();
		wholePane.repaint();
	}

	/**
	 * Update the new list of user currently editting the document if there is
	 * people in the chat room, it will be replaced by by this new user lists
	 * Modifies listModel
	 * 
	 * @param objects
	 *            String of users' name
	 * 
	 */
	public void updateDocumentsList(Object[] documents) {
		documentListModel.clear();
		if (documents.length > 0) {
			for (Object i : documents)
				documentListModel.addElement(i);
		}
		wholePane.revalidate();
		wholePane.repaint();
	}

	/**
	 * Returns a list of people of who are already logged in.
	 * 
	 * @return JList holds the list of all the people modifies: listModel and
	 *         userList. Both will be initialize with this initial data
	 */
	private JList createUserLogin(String[] initialData) {
		listModel = new DefaultListModel();
		if (initialData.length > 0) {
			for (String i : initialData)
				listModel.addElement(i);
		}
		userList = new JList(listModel);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		return userList;

	}

	/**
	 * Returns a list of documents currently editing.
	 * 
	 * @return JList holds the list of the documents currentingly editing
	 *         modifies: documentListModel and documentList. Both will be
	 *         initialize with this initial strings
	 */
	private JList createDocumentsLogin(String[] strings) {
		documentListModel = new DefaultListModel();
		if (strings.length > 0) {
			for (String i : strings)
				documentListModel.addElement(i);
		}
		documentList = new JList(documentListModel);
		documentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		return documentList;

	}

	/**
	 * This creates the textArea where we can collab edit the document and add
	 * style to it, the intial font is just regular text
	 * 
	 * @return JTextPane, set to editable. the server probalbly should be
	 *         assigned to uneditable.
	 */
	private JTextPane createTextPane() {

		JTextPane textPane = new JTextPane();
		StyledDocument doc = textPane.getStyledDocument();
		addStylesToDocument(doc);

		try {
			doc.insertString(0, this.init, doc.getStyle("regular"));

		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text into text pane.");
		}

		return textPane;
	}

	/**
	 * 
	 * @return String content of the TextArea
	 */
	public String getText() {
		try {
			return textArea.getDocument().getText(0,
					textArea.getDocument().getLength());
		} catch (BadLocationException e) {
			return "Yo Bad Luck";
		}
	}

	/**
	 * Get the List of the user that login to the collab edit server
	 * 
	 * @return JList list of users
	 */
	public JList getListOfUser() {
		return userList;
	}

	/**
	 * This adds the styles for the document. This supports different different
	 * type of option including bold, italic,etc. This enable the user to call
	 * basic string i.e "bold" to the text bold. Modifies: Input document
	 * 
	 * @param doc
	 *            , an input StyledDocument to add Style to
	 */
	protected void addStylesToDocument(StyledDocument doc) {
		// Initialize some styles.
		Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);

		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");

		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);

		s = doc.addStyle("bold", regular);
		StyleConstants.setBold(s, true);

		s = doc.addStyle("small", regular);
		StyleConstants.setFontSize(s, 10);

		s = doc.addStyle("large", regular);
		StyleConstants.setFontSize(s, 16);

		s = doc.addStyle("icon", regular);
		StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

		s = doc.addStyle("button", regular);
		StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

	}

	/**
	 * Get an integer siteID of the client ie 1, 2 ,3 ,...
	 * 
	 * @return int the client siteID
	 */
	public int getSiteID() {
		return this.collabClient.getID();
	}

	/**
	 * Get the model of this client
	 * 
	 * @return CollabModel
	 */
	public CollabModel getCollabModel() {
		return this.collabModel;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 * 
	 * @throws OperationEngineException
	 */
	private static void createAndShowGUI() throws OperationEngineException {
		// Create and set up the window.
		JFrame frame = new JFrame("Collab Edit 6.005 Project 2");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Add content to the window.
		frame.add(new ClientGui("Welcome to Collab Edit!", "Client"));

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * For testing purpose only This will call the GUI directly from here
	 * */
	public static void main(String[] args) {
		// Schedule a job for the event dispatching thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				try {
					createAndShowGUI();
				} catch (OperationEngineException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * This will access the CollabModel and set the document key
	 * 
	 * @param str
	 */
	public void setModelKey(String str) {
		this.collabModel.setKey(str);
	}

	/**
	 * Used by the server to make it uneditable
	 * 
	 * @return the textArea (where the document is being edited).
	 */
	public JTextPane getTextArea() {
		return this.textArea;
	}

	/**
	 * Get initial string. Used for testing
	 * 
	 * @return init - the initial string for the document
	 */
	public String getInit() {
		return this.init;
	}

}
