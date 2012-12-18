package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.Host;

// Refer to README on how to run this

/*GUI testing strategy:
 * The login page has two button for the user to select client and server
 * There are a couple of situation to test:
 * 	if the user selects client button:
 * 		if the IP and Port of the server are not available, there will 
 *  	be a error Dialog popup or SocketTimeout, the user has to start again manually
 *  	if the IP or Port is incorrect (formatting and length): there is a dialog pop
 *  		up to notice them about the error and the prompt screen will
 *  		reappear
 *  	if the userName is blank, it will be Anonymous by default. The user name can 
 *  		be of anylength and of any format. The end result is a string without 
 *  		trailing space. There will be no action occurs if the user hit any key.
 *  	if the IP and Port are both correct, they will be direct to a new screen
 *  		for them to select the document to edit or create a new one.
 * 	if the user selects server button:
 * 		the page will ask for the uesr name , IP and Port as for the client, but only the
 *  	name is recorded , IP and Port are default localhost and 4444. So when user types 
 *  	in anything in IP and Port, it still records as localhost for IP and 4444 for port.
 *  	The Server can set the name to anything and it will have the same effect as the client
 *      (String without trailing and leading spaces). 
 *  If the user clicks on close button during the process, it will terminate any available connection.
 *  
 */
/**
 * This class create a main login where the user can choose between being a
 * client and being a server. It also contains another GUI that will ask the
 * user to fill in some information such name, IP , port , etc.
 * 
 * 
 * Thread-safe argument: This is thread safe it only runs on the SwingUtilities
 * thread which won't interfere in the operation in the main thread. It will be
 * disposed after the user finish with it, and it doesn't leak any information
 * out.
 * 
 * 
 * @author viettran
 * 
 */
public class LoginPage extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4468772870634275098L;
	/** Welcome message for user */
	private static final String WELCOME = "WELCOME TO COLLAB EDIT PLEASE SELECT CLIENT OR SERVER";
	/** Wecome message font */
	private static final String WELCOME_FONT = "Serif";
	/** Wecome message attribute */
	private static final int WELCOME_ATT = Font.BOLD;
	/** Wecome message size */
	private static final int WELCOME_SIZE = 24;
	/** Wecome message color */
	private static final Color WELCOME_COLOR = Color.red;

	/** Client text */
	private static final String CLIENT = "CLIENT";
	/** Server text */
	private static final String SERVER = "SERVER";
	/** Authors' name */
	private static final String ACCREDIT = "@HANWEN_TRAN_YOUYANG";
	/** Accredit line font constant */
	private static final String ACCREDIT_FONT = "Serif";
	/** Accredit line attribute constant */
	private static final int ACCREDIT_ATT = Font.ITALIC;
	/** Accredit line size constant */
	private static final int ACCREDIT_SIZE = 24;
	/** Accredit line color constant */
	private static final Color ACCREDIT_COLOR = Color.red;
	/** Prompt quesiont for IP adress */
	private static final String IP = "Server IP";
	/** Prompt quesiont for PORT */
	private static final String PORT = "Server Port";
	/** Default local IP address */
	private static final String LOCAL_IP = "127.0.0.1";
	/** Default server port to connect */
	private static final int DEFAULT_PORT = 4444;
	/** Prompt to the user */
	private static final String QUESTION = "Please Enter user name, IP address"
			+ " and Port or Default for local connection";

	/** The default width of the Window Frame */
	private static final int WIDTH = 800;
	/** The default height of the Window Frame */
	private static final int HEIGHT = 400;
	/** The constant for JLabel in Provided Information Page */
	private static final String USER_NAME = "User Name";
	/** Default user name if the user doesn't supply one */
	private static final String DEFAULT_USERNAME = "Anonymous";

	/** Path to the image of the client */
	private static final String CLIENT_IMAGE = "raw/clientIcon.png";

	/** Path to the image of the server */
	private static final String SERVER_IMAGE = "raw/serverIcon.png";
	/** contants represents string for default button */
	private static final String DEFAULT_BUTTON = "Default";
	/** contants represents string for submit button */
	private static final String SUBMIT_BUTTOn = "Submit";
	/** notify user when they enter wrong port number */
	protected static final String WRONG_PORT_NUMBER = "Please enter correct port number (xx.xxx.xx.xx)";
	/** notify user when they enter wrong IP or port number */
	private static final String WRONG_IP_OR_PORT = "Please correct your IP and Port number";

	/** User defined ip */
	private JTextField ipInput;
	/** Client or Server string */
	private String type;
	/** User defined port */
	private JTextField portInput;

	/** user names of the user connects */
	private JTextField userNameInput;
	/** port number of the server */
	private int portNo;
	/** An instance to host */
	private Host host;

	/** This constructor calls a function to create GUI */
	public LoginPage() {
		initGui();
	}

	/**
	 * Create the main gui for this login page
	 */
	private void initGui() {
		setLayout(new BorderLayout());
		// create the pane to store everything in this GUI
		JPanel wholePane = new JPanel();
		GroupLayout wholeGroupLayout = new GroupLayout(wholePane);

		// Welcome message and some of the attribute
		JTextField welcome = new JTextField(WELCOME);
		welcome.setEditable(false);
		welcome.setBackground(Color.BLUE);
		welcome.setForeground(WELCOME_COLOR);
		welcome.setFont(new Font(WELCOME_FONT, WELCOME_ATT, WELCOME_SIZE));

		// Left Pane that contain the client image
		JPanel leftPane = new JPanel();
		JLabel clientLabel = new JLabel(CLIENT);
		GridBagLayout leftLayout = new GridBagLayout();
		leftPane.setLayout(leftLayout);
		ImageIcon clientIcon = new ImageIcon(CLIENT_IMAGE);
		JButton clientButton = new JButton(clientIcon);
		// direct the user to next screen and close the frame
		clientButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				type = "client";
				dispose();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						LoginPage.this.promptForIPAndPort(QUESTION);

					}
				});

			}
		});
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		leftPane.add(clientLabel, c);
		c.gridx = 0;
		c.gridy = 1;
		leftPane.add(clientButton, c);

		// rightPane contains server icon
		JPanel rightPane = new JPanel();
		JLabel serverLabel = new JLabel(SERVER);
		GridBagLayout rightLayout = new GridBagLayout();
		ImageIcon serverIcon = new ImageIcon(SERVER_IMAGE);
		JButton serverButton = new JButton(serverIcon);
		rightPane.setLayout(rightLayout);
		// direct the user to the server side and close this frame
		serverButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				type = "server";
				dispose();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						LoginPage.this.promptForIPAndPort(QUESTION);

					}
				});

			}
		});

		c.gridx = 0;
		c.gridy = 0;
		rightPane.add(serverLabel, c);
		c.gridx = 0;
		c.gridy = 1;
		rightPane.add(serverButton, c);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPane, rightPane);

		splitPane.setResizeWeight(0.5);

		JLabel copyRight = new JLabel(ACCREDIT);
		copyRight.setForeground(ACCREDIT_COLOR);
		copyRight.setFont(new Font(ACCREDIT_FONT, ACCREDIT_ATT, ACCREDIT_SIZE));

		copyRight.setForeground(Color.red);
		wholeGroupLayout.setHorizontalGroup(wholeGroupLayout
				.createParallelGroup(Alignment.CENTER).addComponent(welcome)
				.addComponent(splitPane).addComponent(copyRight));
		wholeGroupLayout.setVerticalGroup(wholeGroupLayout
				.createSequentialGroup().addComponent(welcome)
				.addComponent(splitPane).addComponent(copyRight));
		wholePane.setLayout(wholeGroupLayout);

		add(wholePane);

	}

	/*
	 * Testing strategy: See the main document
	 */

	/**
	 * This Gui will pop up a window to ask for the ip and port of the remote
	 * server that the client wants to connect to. It will dispose the screen
	 * when it's done. Thread-safe argument: This is thread safe because it's
	 * called in seperate SwingUtilities thread and is disposed after the user
	 * performs the appropriate click, and all of its operations are done upon
	 * dispoing.
	 * 
	 * @param Prompt
	 *            String that represents the prompt (Instruction for user to
	 *            type IP)
	 */
	void promptForIPAndPort(String Prompt) {
		final JFrame frame = new JFrame("Provide information");

		JPanel pane = new JPanel(new BorderLayout());

		JLabel prompt = new JLabel(Prompt);
		JLabel userName = new JLabel(USER_NAME);
		userNameInput = new JTextField();
		userNameInput.setToolTipText("Enter your user name");

		JLabel ip = new JLabel(IP);
		ipInput = new JTextField();

		ipInput.setToolTipText("Enter the ip address");

		JLabel port = new JLabel(PORT);
		portInput = new JTextField();
		portInput.setToolTipText("Enter the port number");
		JButton defaultVal = new JButton(DEFAULT_BUTTON);
		JButton submit = new JButton(SUBMIT_BUTTOn);
		// connect the user to localhost

		defaultVal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String userName = userNameInput.getText();// get the Text From
															// the user
				if (userName.length() <= 0) {
					userName = DEFAULT_USERNAME;
				} else {
					userName = userNameInput.getText().trim();
				}
				frame.dispose();
				host = new Host(type, LOCAL_IP, DEFAULT_PORT, userName);

				Thread thread = new Thread() {
					@Override
					public void run() {
						host.start();
					}
				};

				thread.start();
			}
		});

		// connect to remote server
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String userName = userNameInput.getText();// get the Text From
															// the user
				if (userName.length() <= 0) {
					userName = DEFAULT_USERNAME;
				} else {
					userName = userNameInput.getText().trim();
				}
				String ip = ipInput.getText().trim();
				String portInputString = portInput.getText().trim();
				try {
					if (ip.length() > 0 && portInputString.length() > 0) {
						try {
							// take the port number input
							portNo = Integer.parseInt(portInputString);

						} catch (NumberFormatException exception) {
							frame.dispose();
							new ErrorDialog(WRONG_PORT_NUMBER);
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									LoginPage.this.promptForIPAndPort(QUESTION);

								}
							});

						}
						frame.dispose();
						host = new Host(type, ip, portNo, userName);
						Thread thread = new Thread() {
							@Override
							public void run() {
								host.start();
							}
						};

						thread.start();
					} else {
						frame.dispose();
						new ErrorDialog(WRONG_IP_OR_PORT);
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								LoginPage.this.promptForIPAndPort(QUESTION);

							}
						});

					}
				} catch (IllegalArgumentException exception) {
					frame.dispose();
					new ErrorDialog(WRONG_IP_OR_PORT);
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							LoginPage.this.promptForIPAndPort(QUESTION);

						}
					});

				}
			}
		});

		pane.add(prompt, BorderLayout.PAGE_START);
		JPanel ipPortPane = new JPanel();
		GroupLayout ipPortLayout = new GroupLayout(ipPortPane);
		ipPortPane.setLayout(ipPortLayout);
		ipPortLayout.setAutoCreateGaps(true);

		ipPortLayout.setAutoCreateContainerGaps(true);

		// Create a sequential group for the horizontal axis.

		GroupLayout.SequentialGroup hGroup = ipPortLayout
				.createSequentialGroup();
		hGroup.addGroup(ipPortLayout.createParallelGroup()
				.addComponent(userName).addComponent(ip).addComponent(port));
		hGroup.addGroup(ipPortLayout.createParallelGroup()
				.addComponent(userNameInput).addComponent(ipInput)
				.addComponent(portInput));
		ipPortLayout.setHorizontalGroup(hGroup);

		// Create a sequential group for the vertical axis.
		GroupLayout.SequentialGroup vGroup = ipPortLayout
				.createSequentialGroup();
		vGroup.addGroup(ipPortLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(userName).addComponent(userNameInput));
		vGroup.addGroup(ipPortLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(ip).addComponent(ipInput));
		vGroup.addGroup(ipPortLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(port).addComponent(portInput));
		ipPortLayout.setVerticalGroup(vGroup);
		ipPortPane.setPreferredSize(new Dimension(200, 100));
		pane.add(ipPortPane, BorderLayout.CENTER);

		JPanel clickPane = new JPanel();
		clickPane.add(defaultVal);
		clickPane.add(submit);
		pane.add(clickPane, BorderLayout.PAGE_END);
		frame.setContentPane(pane);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * Just to create and show the gui
	 * 
	 * 
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new LoginPage();
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * The main thread that starting the program To make sure it's thread safe
	 * we needs to run it in seperate SwingUtilities thread
	 * 
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
}
