package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import sun.awt.DefaultMouseInfoPeer;
import sun.awt.OrientableFlowLayout;

/*
 * Testing Strategy:
 * See ClientGui*/

/**
 * ServerGui inherit from Edit Gui, it includes some methods to update the
 * client list
 * 
 * @author
 * 
 */

public class ServerGui extends ClientGui {

    
    private static String init = "welcome to Collab Edit! Server edition.";

    /**
     * ServerGui sets up the GUI for the local client
     * 
     */
    public ServerGui(String init) {
        super(init);
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
                listModel.addElement((String) i);
        }
        wholePane.revalidate();
        wholePane.repaint();
    }

    /**
     * add a User to chat View , more useful to call when a user drop from the
     * network.
     * Modifies: listModel
     * 
     * @param users
     */
    public void addUsers(String users) {
        listModel.addElement(users);
        wholePane.revalidate();
        wholePane.repaint();
    }

    /**
     * delete User from chat View , more useful to call when a user drop from
     * the network.
     * Modifies: listModel
     * 
     * @param users
     */
    public void deleteUsers(String users) {
        listModel.removeElement(users);
        wholePane.revalidate();
        wholePane.repaint();
    }

}