package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import sun.awt.OrientableFlowLayout;

/*Test Strategy:
 * Current client Gui support BOLD, ITALIC, COPY, CUT, PASTE, so I have tested 
 * all of those functions by observing the changes in the selected text when I perform
 * a click on those Button. If there is a selected text, the action will be carried out.
 * For BOLD and ITALIC, if there is no selected text, it will change the font of the whole 
 * document, COPY , CUT and PASTE don't do anything if there is nothing selected or nothing store 
 * in the clipboard.
 * UNDO and REDO will be tested by letting multiple user typing, and undo should perform the last action 
 * of all of the users, same things will work for redo.
 * For resizing, all of the components are appropriately resizing after I expand the window.
 * Current the chat box take the input for the user and display in order in the chat window 
 * with the current time , and this is been test throughly by perform multiple chatting in the text box. The
 * scrollPane is automatically scrolling down when display area is full. The displayUser box also shows each user name 
 * row by row and automatically scroll down if there is overflow in the displayUser box.
 * The clientGUI after the client connects to the server, it will display everything in the current state 
 * of the server textArea and the current users in the chatroom/editing (not including the ChatArea),
 * this also get tested by connecting to the server mutliple time and observe the state of the GUI.
 * All of this procedure has to be thread-safe by performing the transform operation to all of the user/client pairs
 * For concurrency, the gui can be tested by doing the same procedure and observe if they're 
 * reflected upon all users. 
 * For the ServerGUI, the server will update its userLoginPanel whenever a client connects to it, this has been
 * tested by connected multiple clients to the server and its show the updated clients list in the userLoginPanel.
 * When the user logs out, the server will reflect that in the userLoginPanel by removing the name as the specific
 * location. 
 * 
 * 
 * */

/**
 * Client Gui, the main gui of the collaborative editing This Implement the MVC
 * pattern where the view is what the user sees and can interact with (i.e The
 * Toolbar, main Document, who is viewing, chat box, chat display), the model is
 * where those information should be store when there is some changes in the
 * view after the user interact with it. Some models that implement in these
 * design are DefaultListModel to control who views/edit the document,
 * StyleDocuments is the model for the collab edit main document which will
 * store the current state of the shared documents). The controllers are all of
 * the Listeners that listen to changes in the view all and make the call to the
 * model to change the data (i.e if any user makes a change to the data, the
 * controller will listen to the changes in the documents and send message to
 * the StyleDocument Model to make the change to the shared document)
 * 
 * @author
 * 
 */
public class ClientGui extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    /** The initial text of the document */
    private String init = "Welcome to Collab Edit!";
    /** String represents new line */
    private String newline = "\n";
    /** For login page */
    protected static final String textFieldString = "JTextField";
    protected static final String passwordFieldString = "JPasswordField";
    protected static final String ftfString = "JFormattedTextField";
    protected static final String buttonString = "JButton";

    /** Main document of the collaborative editing */
    protected JEditorPane textArea;
    /** toggle Bold Button to bold text */
    protected JToggleButton boldButton;
    /** toggle Italic Button to italicize text */
    protected JToggleButton italicButton;
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
    protected JTextPane chatDisplay;

    /** the input of the chat */
    private JTextField chatInput;
    /** the pane that store the collab edit GUI */
    protected JPanel wholePane;

    protected DefaultListModel listModel;

    /**
     * EditGui sets up the GUI for the local client
     * 
     */
    public ClientGui(String init) {
        this.init = init;
        setLayout(new BorderLayout());

        FlowLayout toolBarLayout = new FlowLayout();
        toolBarLayout.setAlignment(FlowLayout.LEFT);
        JPanel toolBarPane = new JPanel();

        // Create all of the buttons
        ImageIcon bold = new ImageIcon("raw/bold.png");
        boldButton = new JToggleButton(bold);
        boldButton.setToolTipText("BOLD TEXT");
        boldButton.setMnemonic(KeyEvent.VK_B);
        boldButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setBoldTextAction();

            }

        });
        // make italic button
        ImageIcon italic = new ImageIcon("raw/italic.png");
        italicButton = new JToggleButton(italic);
        italicButton.setToolTipText("ITALICIZE TEXT");
        italicButton.setMnemonic(KeyEvent.VK_I);
        italicButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setItalicTextAction();

            }

        });

        // copy key
        ImageIcon copy = new ImageIcon("raw/copy.png");
        copyButton = new JButton(copy);
        copyButton.setToolTipText("Copy Text");
        copyButton.setMnemonic(KeyEvent.VK_C);
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
        pasteButton.setMnemonic(KeyEvent.VK_V);
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
        cutButton.setMnemonic(KeyEvent.VK_X);
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
        JToolBar toolBar = new JToolBar("ToolBar");

        // add all button to toolBar
        toolBar.add(boldButton);
        toolBar.add(italicButton);
        toolBar.add(copyButton);
        toolBar.add(pasteButton);
        toolBar.add(cutButton);
        toolBar.add(undoButton);
        toolBar.add(redoButton);
        toolBarPane.setLayout(toolBarLayout);
        toolBarPane.add(toolBar);

        // Create a text area.
        textArea = createTextPane();

        JScrollPane areaScrollPane = new JScrollPane(textArea);
        areaScrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(250, 250));
        areaScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Main Document"),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                areaScrollPane.getBorder()));

        /*
         * Create an userLogin pane. String[] mockData = { "Hanwen",
         * "Tran Nguyen", "Youyang" };
         */

        JList userLogin = createUserLogin(new String[] { "Name will be change" });

        JScrollPane userLoginScrollPane = new JScrollPane(userLogin);
        userLoginScrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        userLoginScrollPane.setPreferredSize(new Dimension(250, 145));
        userLoginScrollPane.setMinimumSize(new Dimension(10, 10));

        // Create a chat display
        JPanel chatAreaPane = new JPanel();
        GroupLayout chatGroupLayout = new GroupLayout(chatAreaPane);
        chatInput = new JTextField();
        chatInput.setToolTipText("Type your Chat Here");
        chatInput.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                displayChatMessage();

            }

        });
        // display the chat from all users
        chatDisplay = clientDisplayPanel();
        JLabel chatLabel = new JLabel("Chat Area");

        chatScrollPane = new JScrollPane(chatDisplay);
        chatScrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setPreferredSize(new Dimension(250, 155));
        chatScrollPane.setMinimumSize(new Dimension(10, 10));

        chatGroupLayout.setHorizontalGroup(chatGroupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(userLoginScrollPane).addComponent(chatLabel)
                .addComponent(chatScrollPane).addComponent(chatInput));

        chatGroupLayout.setVerticalGroup(chatGroupLayout
                .createSequentialGroup().addComponent(userLoginScrollPane)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chatLabel).addComponent(chatScrollPane)
                .addComponent(chatInput));
        chatAreaPane.setLayout(chatGroupLayout);

        // Put the editor pane and the text pane in a split pane.

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                userLoginScrollPane, chatAreaPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.5);
        JPanel rightPane = new JPanel(new GridLayout(1, 0));
        rightPane.add(chatAreaPane);
        rightPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Who is viewing"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Put everything together.
        JPanel leftPane = new JPanel(new BorderLayout());

        leftPane.add(toolBarPane, BorderLayout.PAGE_START);
        leftPane.add(areaScrollPane, BorderLayout.CENTER);

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
     * this is private void to display the chat message from each of the users.
     * 
     * 
     */
    protected void displayChatMessage() {
        try {
            if (chatInput.getText().length() > 0) {
                // get the document that chat window has
                DefaultStyledDocument doc = (DefaultStyledDocument) chatDisplay
                        .getDocument();

                // Get the current time of the system in nice format
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                // Mock data, should change to ID of the user in the future
                String home = "Tran [" + sdf.format(cal.getTime()) + "]: ";

                // Get default style to be use in insertString
                Style style = StyleContext.getDefaultStyleContext().getStyle(
                        StyleContext.DEFAULT_STYLE);

                // insertString is ThreadSafe, first get the name and current
                // time
                doc.insertString(doc.getLength(), home, style);
                // get the input in chat box and go to next line
                doc.insertString(doc.getLength(),
                        chatInput.getText() + newline, style);

                // update caret location
                chatDisplay.setCaretPosition(doc.getLength());

                // clear the chat box
                chatInput.setText(null);
            }

        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(this, "Application will now close. "
                    + newline + " A restart may cure the error!" + newline
                    + newline + e.getMessage(), "Fatal Error",
                    JOptionPane.WARNING_MESSAGE, null);

            System.exit(1);
        }
    }

    /**
     * Sets the selected text to italic if there is a selected text otherwise
     * the whole document will be italic
     */
    protected void setItalicTextAction() {
        if (italicButton.isSelected()) {
            // if there is no selected text, change font of whole document
            // otherwise, change the font of the selected text and document
            if (textArea.getSelectedText() != null) {
                changeFont("italicType", true);
            }
            textArea.setFont(textArea.getFont().deriveFont(Font.ITALIC));
        } else {
            if (textArea.getSelectedText() != null) {
                changeFont("italicType", false);
            }
            textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
        }
    }

    /**
     * Helper function to repeat the process of change font to Italic or Bold
     * for a selected text or the whole document
     * 
     * @param type
     *            can be boldType or italicType
     * @param enable
     *            whether to enable or disable the style
     */
    private void changeFont(String type, boolean enable) {
        int start = textArea.getSelectionStart() < textArea.getSelectionEnd() ? textArea
                .getSelectionStart() : textArea.getSelectionEnd();
        StyleContext sc = new StyleContext();
        Style style = sc.addStyle(type, null);
        StyledDocument doc = (StyledDocument) textArea.getDocument();
        if (type.equals("boldType"))
            StyleConstants.setBold(style, enable);
        else if (type.equals("italicType"))
            StyleConstants.setItalic(style, enable);
        doc.setCharacterAttributes(start, textArea.getSelectedText().length(),
                style, true);

    }

    /**
     * Sets the selected text to bold if there is a selected text otherwise the
     * whole document will be bold Modifies the selected text or the whole
     * document Font style
     */
    protected void setBoldTextAction() {
        if (boldButton.isSelected()) {
            if (textArea.getSelectedText() != null) {
                changeFont("boldType", true);
            }
            textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));

        } else {
            if (textArea.getSelectedText() != null) {
                changeFont("boldType", false);
            }
            textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
        }

    }

    /**
     * Private function that creates an JEditorPane. This creates the panel to
     * show the chat from other users modifying the document
     * 
     * @return JTextPanePanel uneditable JTextPane
     */
    private JTextPane clientDisplayPanel() {
        JTextPane chatPane = new JTextPane();
        chatPane.setEditable(false);

        return chatPane;
    }

    /**
     * Returns a list of people of who are already logged in.
     * 
     * @return JList
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
     * This creates the textArea where we can collab edit the document and add
     * style to it, the intial font is just regular text
     * 
     * @return JTextPane, set to editable. the server probalbly should be
     *         assigned to uneditable.
     */
    private JTextPane createTextPane() {
        String[] initString = { this.init };

        JTextPane textPane = new JTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        addStylesToDocument(doc);

        try {
            for (int i = 0; i < initString.length; i++) {
                doc.insertString(doc.getLength(), initString[i],
                        doc.getStyle("regular"));
            }
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
        return textArea.getText();
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
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("Collab Edit Demo");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add content to the window.
        frame.add(new ClientGui("Welcome to Collab Edit!"));

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }
}