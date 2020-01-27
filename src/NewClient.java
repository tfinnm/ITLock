import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;              //for layout managers and more
import java.awt.event.*;        //for action events
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.*;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class NewClient extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1796011690233971461L;
	protected static final String textFieldString = "Username";
	protected static final String passwordFieldString = "Password";
	protected static final String ftfString = "Date";
	protected static final String buttonString = "JButton";
	private static JPasswordField passwordField;
	private static JTextField usrnmField;
	private static JTabbedPane tabbedPane;
	private static JRadioButton unLockButton;
	private static JRadioButton lockButton;
	private JCheckBox restartButton;
	private JCheckBox timerButton;
	private JComboBox<String> hostList;
	private JComboBox<String> rHostList;
	private JProgressBar advance;
	private JTextField timeField;
	private JTextField reasonField;
	private JTextPane CMDField;
	private JButton goButton;
	private JButton rGoButton;
	private static Timer timer;
	private static String[] hosts;
	private static ArrayList<String> users = new ArrayList<>(2);
	private static ArrayList<String> users2 = new ArrayList<>(2);
	private static ArrayList<String> hostsList = new ArrayList<>(2);
	public static BufferedImage remoteImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	public static JPanel panel2;
	static Logger logger;

	protected JLabel actionLabel;

	public NewClient() {
		setLayout(new BorderLayout());

		//Create a regular text field.
		usrnmField = new JTextField(10);
		usrnmField.setActionCommand(textFieldString);
		usrnmField.addActionListener(this);

		//Create a password field.
		passwordField = new JPasswordField(10);
		passwordField.setActionCommand(passwordFieldString);
		passwordField.addActionListener(this);

		//Create a formatted text field.
		JFormattedTextField ftf = new JFormattedTextField(
				java.util.Calendar.getInstance().getTime());
		ftf.setActionCommand(textFieldString);
		ftf.addActionListener(this);
		ftf.setEditable(false);

		//Create some labels for the fields.
		JLabel textFieldLabel = new JLabel(textFieldString + ": ");
		textFieldLabel.setLabelFor(usrnmField);
		JLabel passwordFieldLabel = new JLabel(passwordFieldString + ": ");
		passwordFieldLabel.setLabelFor(passwordField);
		JLabel ftfLabel = new JLabel(ftfString + ": ");
		ftfLabel.setLabelFor(ftf);

		//Create a label to put messages during an action event.
		actionLabel = new JLabel("Insert Key or Login and Press Enter");
		actionLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

		//Lay out the text controls and the labels.
		JPanel textControlsPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		textControlsPane.setLayout(gridbag);

		JLabel[] labels = {textFieldLabel, passwordFieldLabel, ftfLabel};
		JTextField[] textFields = {usrnmField, passwordField, ftf};
		addLabelTextRows(labels, textFields, gridbag, textControlsPane);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		textControlsPane.add(actionLabel, c);
		textControlsPane.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Login"),
						BorderFactory.createEmptyBorder(5,5,5,5)));

		//Create a text area.
		JLabel textArea = new JLabel();
		textArea.setIcon(new ImageIcon("ITLIcon.png"));
		JScrollPane areaScrollPane = new JScrollPane(textArea);



		tabbedPane = new JTabbedPane();
		ImageIcon icon = new ImageIcon("");

		JPanel panel1 = new JPanel();
		tabbedPane.addTab("Lock", icon, panel1,
				"ITLock");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_L);

		BorderLayout panel1layout = new BorderLayout(50,50);
		panel1.setLayout(panel1layout);

		JPanel panel1s1 = new JPanel();

		lockButton = new JRadioButton("Lock");
		lockButton.setMnemonic(KeyEvent.VK_L);
		lockButton.setActionCommand("Lock");

		unLockButton = new JRadioButton("Unlock");
		unLockButton.setMnemonic(KeyEvent.VK_U);
		unLockButton.setActionCommand("Unlock");

		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(lockButton);
		group.add(unLockButton);

		//Register a listener for the radio buttons.
		lockButton.addActionListener(this);
		unLockButton.addActionListener(this);

		panel1s1.add(lockButton);
		panel1s1.add(unLockButton);
		lockButton.setEnabled(false);
		unLockButton.setEnabled(false);

		timerButton = new JCheckBox("Use Timer");
		timerButton.setMnemonic(KeyEvent.VK_T);

		restartButton = new JCheckBox("Shutdown");
		restartButton.setMnemonic(KeyEvent.VK_G);

		//Register a listener for the check boxes.
		timerButton.addActionListener(this);
		restartButton.addActionListener(this);

		panel1s1.add(timerButton);
		panel1s1.add(restartButton);
		timerButton.setEnabled(false);
		restartButton.setEnabled(false);

		panel1.add(panel1s1, BorderLayout.NORTH);

		JPanel panel1s2 = new JPanel();

		panel1.add(panel1s2, BorderLayout.CENTER);

		advance = new JProgressBar();

		advance.setMinimum(0);
		advance.setMaximum(4);
		advance.setValue(0);
		panel1.add(advance, BorderLayout.SOUTH);

		hostList = new JComboBox<String>(hosts); //data has type Object[]
		hostList.setEditable(true);
		hostList.setEnabled(false);
		panel1s1.add(hostList);

		//Create a regular text field.
		reasonField = new JTextField(10);

		//Create a password field.
		timeField = new JTextField(10);

		//Create some labels for the fields.
		JLabel reasonLabel = new JLabel("Reason" + ": ");
		reasonLabel.setLabelFor(reasonField);
		JLabel timeLabel = new JLabel("Lockout" + ": ");
		timeLabel.setLabelFor(timeField);

		//Lay out the text controls and the labels.
		JPanel textControlsPane2 = new JPanel();
		GridBagLayout gridbag2 = new GridBagLayout();
		GridBagConstraints c2 = new GridBagConstraints();

		textControlsPane2.setLayout(gridbag2);

		JLabel[] locklabels = {reasonLabel, timeLabel};
		JTextField[] locktextFields = {reasonField, timeField};
		addLabelTextRows(locklabels, locktextFields, gridbag2, textControlsPane2);

		c2.gridwidth = GridBagConstraints.REMAINDER; //last
		c2.anchor = GridBagConstraints.WEST;
		c2.weightx = 1.0;
		textControlsPane2.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Lock Controls"),
						BorderFactory.createEmptyBorder(5,5,5,5)));



		timeField.setEnabled(false);
		reasonField.setEnabled(false);
		hostList.setEnabled(false);
		panel1s2.add(textControlsPane2, BorderLayout.CENTER);

		goButton = new JButton("Send");
		goButton.addActionListener(this);
		goButton.setEnabled(false);
		panel1s2.add(goButton, BorderLayout.SOUTH);

		panel2 = new JPanel();
		tabbedPane.addTab("Remote", icon, panel2,
				"Remote Control");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_R);

		panel2.setLayout(new BorderLayout());
		JPanel panel2t = new JPanel(new BorderLayout());
		rHostList = new JComboBox<String>(hosts); //data has type Object[]
		rHostList.setEditable(true);
		panel2.add(rHostList, BorderLayout.NORTH);
		panel2.add(panel2t, BorderLayout.CENTER);
		panel2t.add(new JLabel("\t\t"), BorderLayout.EAST);
		panel2t.add(new JLabel(" "), BorderLayout.NORTH);
		CMDField = new JTextPane();
		CMDField.setToolTipText("<html>Each line is a new command.<br>All native terminal commands for the host are supported.<br>Additional Commands currectly available:<br><ul><li>&#60;&#60;&#60;closeWindows&#62;&#62;&#62;</li></ul></html>");
		panel2t.add(CMDField, BorderLayout.CENTER);
		panel2t.add(new JLabel("\t\t"), BorderLayout.WEST);
		panel2t.add(new JLabel("  "), BorderLayout.SOUTH);
		rGoButton = new JButton("Send Command");
		rGoButton.setActionCommand("rgo");
		rGoButton.addActionListener(this);
		panel2.add(rGoButton, BorderLayout.SOUTH);


		JPanel panel3 = new JPanel();
		tabbedPane.addTab("Settings", icon, panel3,
				"Settings");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_S);

		panel3.setLayout(new BorderLayout());

		JPanel compsToExperiment = new JPanel();
		compsToExperiment.setLayout(new GridLayout(4,2));
		JPanel controls = new JPanel();

		//Add buttons to experiment with Grid Layout
		JButton newuserbutton = new JButton("New User");
		newuserbutton.setActionCommand("adduser");
		newuserbutton.addActionListener(this);
		compsToExperiment.add(newuserbutton);
		JButton userbutton = new JButton("Manage Users");
		userbutton.setActionCommand("users");
		userbutton.addActionListener(this);
		compsToExperiment.add(userbutton);
		JButton keybutton = new JButton("Manage Keys");
		keybutton.setActionCommand("keys");
		keybutton.addActionListener(this);
		compsToExperiment.add(keybutton);
		JButton hostsbutton = new JButton("Manage Hosts");
		hostsbutton.setActionCommand("hosts");
		hostsbutton.addActionListener(this);
		compsToExperiment.add(hostsbutton);
		JButton testbutton = new JButton("Test Connection");
		testbutton.setActionCommand("test");
		testbutton.addActionListener(this);
		compsToExperiment.add(testbutton);

		JButton custombutton = new JButton("Custom Command");
		custombutton.setActionCommand("custom");
		custombutton.addActionListener(this);
		compsToExperiment.add(custombutton);

		JButton blbutton = new JButton("Manage Blacklist");
		blbutton.setActionCommand("bl");
		blbutton.addActionListener(this);
		compsToExperiment.add(blbutton);

		JButton wlbutton = new JButton("Manage Whitelist");
		wlbutton.setActionCommand("wl");
		wlbutton.addActionListener(this);
		compsToExperiment.add(wlbutton);


		//Add controls to set up horizontal and vertical gaps
		controls.add(new Label("ITLock Settings"));

		panel3.add(compsToExperiment, BorderLayout.SOUTH);
		panel3.add(new JSeparator(), BorderLayout.CENTER);
		panel3.add(controls, BorderLayout.NORTH);



		JPanel panel4 = new JPanel(new BorderLayout());
		panel4.setPreferredSize(new Dimension(410, 50));
		tabbedPane.addTab("About", icon, panel4,
				"About ITLock");
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_A);
		JLabel aboutText = new JLabel();
		aboutText.setText("ITLock (C) 2019 Toby McDonald");
		panel4.add(aboutText, BorderLayout.NORTH);
		JButton website = new JButton("Visit Official Website");
		website.setActionCommand("website");
		website.addActionListener(this);
		panel4.add(website, BorderLayout.SOUTH);
		tabbedPane.setEnabled(false);
		//Create an editor pane.
		//JEditorPane editorPane = createEditorPane();
		//JScrollPane editorScrollPane = new JScrollPane(editorPane);
		//editorScrollPane.setVerticalScrollBarPolicy(
		//                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//editorScrollPane.setPreferredSize(new Dimension(250, 145));
		//editorScrollPane.setMinimumSize(new Dimension(10, 10));

		//Create a text pane.
		//JTextPane textPane = createTextPane();
		//JScrollPane paneScrollPane = new JScrollPane(textPane);
		//paneScrollPane.setVerticalScrollBarPolicy(
		//                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//paneScrollPane.setPreferredSize(new Dimension(250, 155));
		//paneScrollPane.setMinimumSize(new Dimension(10, 10));

		//Put the editor pane and the text pane in a split pane.
		//JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
		//                                      editorScrollPane,
		//                                      paneScrollPane);
		//splitPane.setOneTouchExpandable(true);
		//splitPane.setResizeWeight(0.5);
		JPanel rightPane = new JPanel(new GridLayout(1,0));
		rightPane.add(tabbedPane);
		rightPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Controls"),
				BorderFactory.createEmptyBorder(5,5,5,5)));


		//Put everything together.
		JPanel leftPane = new JPanel(new BorderLayout());
		leftPane.add(textControlsPane, 
				BorderLayout.PAGE_START);
		leftPane.add(areaScrollPane,
				BorderLayout.CENTER);

		add(leftPane, BorderLayout.LINE_START);
		add(rightPane, BorderLayout.LINE_END);
	}

	private void addLabelTextRows(JLabel[] labels,
			JTextField[] textFields,
			GridBagLayout gridbag,
			Container container) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		int numLabels = labels.length;

		for (int i = 0; i < numLabels; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
			c.fill = GridBagConstraints.NONE;      //reset to default
			c.weightx = 0.0;                       //reset to default
			container.add(labels[i], c);

			c.gridwidth = GridBagConstraints.REMAINDER;     //end row
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			container.add(textFields[i], c);
		}
	}

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("Username") || e.getActionCommand().equals("Password")) {
			if (passwordField.getText().equals(users2.get(users.indexOf(usrnmField.getText())))) {
				passwordField.setEditable(false);
				usrnmField.setEditable(false);
				tabbedPane.setEnabled(true);
				lockButton.setEnabled(true);
				unLockButton.setEnabled(true);
				timer.stop();
				logger.info("Logged in as "+usrnmField.getText());
			}else {
				logger.warning("Failed Loggin Attempt");
			}
		} else if (e.getActionCommand().equals("Lock")) {
			restartButton.setEnabled(true);
			timerButton.setEnabled(true);
			hostList.setEnabled(true);
			goButton.setEnabled(true);
			reasonField.setEnabled(true);
		} else if (e.getActionCommand().equals("Unlock")) {
			restartButton.setEnabled(false);
			timerButton.setEnabled(false);
			hostList.setEnabled(true);
			reasonField.setEnabled(true);
			goButton.setEnabled(true);
		} else if (e.getActionCommand().equals("Use Timer")) {
			restartButton.setEnabled(!timerButton.isSelected());
			timeField.setEnabled(timerButton.isSelected());
		} else if (e.getActionCommand().equals("keys")) {
			new keyupdatepopup().display();
		} else if (e.getActionCommand().equals("users")) {
			new userspopup().display();
		} else if (e.getActionCommand().equals("adduser")) {
			new adduserspopup().display();
		} else if (e.getActionCommand().equals("hosts")) {
			new hostspopup().display();
		} else if (e.getActionCommand().equals("Shutdown")) {
			timerButton.setEnabled(!restartButton.isSelected());
			timeField.setEnabled(false);
		} else if (e.getActionCommand().equals("Send") || e.getActionCommand().equals("test")) {
			advance.setIndeterminate(true);
			advance.setString("Connecting...");
			advance.setStringPainted(true);
			Client net = new Client((String) hostList.getSelectedItem());
			try{
				net.connectToServer();
				advance.setIndeterminate(false);
				advance.setValue(1);
				advance.setString("Preparing to send...");
				advance.setStringPainted(true);
				net.setupStreams();
				String[] pass = new String[3];
				String action ="";
				String info ="";
				if (e.getActionCommand().equals("test")) {
					pass[0] = "na";
				} else if (lockButton.isSelected()) {
					if (timerButton.isSelected()) {
						pass[0] = "lt";
						pass[1] = reasonField.getText();
						pass[2] = String.valueOf(60*Integer.parseInt(timeField.getText()));
						action = "locked ";
						info = " for "+pass[2]+" seconds for reason: "+pass[1];
					} else if (restartButton.isSelected()) {
						pass[0] = "sd";
						pass[1] = reasonField.getText();
						pass[2] = "30";
						action = "shutdown ";
						info = " for reason: "+pass[1];
					} else {
						pass[0] = "lk";
						pass[1] = reasonField.getText();
						action = "locked ";
						info = " for reason: "+pass[1];
					}
				} else if (unLockButton.isSelected()) {
					pass[0] = "un";
					action = "unlocked ";
				}
				advance.setValue(2);
				advance.setString("Sending...");
				advance.setStringPainted(true);

				net.sendMessage(pass);
				logger.info(action+(String) hostList.getSelectedItem()+info);
				advance.setValue(3);
				advance.setString("Sent, closing connection...");
				advance.setStringPainted(true);
			}catch(EOFException eofException){
			}catch(IOException ioException){
				ioException.printStackTrace();
			}finally{
				net.closeConnection();
			}
			advance.setValue(4);
			advance.setString("Done.");
			advance.setStringPainted(true);
			if (e.getActionCommand().equals("test")) {
				JOptionPane.showMessageDialog(null, "Test Finished Successfully", "ITLock", JOptionPane.INFORMATION_MESSAGE);
			}

		} else if (e.getActionCommand().equals("rgo")) {
			String execCMD = CMDField.getText();
			Client net = new Client((String) rHostList.getSelectedItem());
			try{
				net.connectToServer();
				net.setupStreams();
				String[] pass = {"cmd",execCMD,""};
				net.sendMessage(pass);
				logger.info("Executed \""+execCMD+"\" on "+(String) hostList.getSelectedItem());
			}catch(EOFException eofException){
			}catch(IOException ioException){
				ioException.printStackTrace();
			}finally{
				net.closeConnection();
				JOptionPane.showMessageDialog(null, "Successfully Sent Command", "ITLock", JOptionPane.INFORMATION_MESSAGE);
			}	
		} else if (e.getActionCommand().equals("website")) {
			try {
				Desktop.getDesktop().browse(new URI("http://tfinnm.tk/ITLock"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("custom")) {
			new custompopup().display();
		}
		
	}



	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("ITLock");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add content to the window.
		frame.add(new NewClient());

		//Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setIconImage(new ImageIcon("ITLIcon.png").getImage());
		frame.setResizable(false);
		timer = new Timer(5, new timerListener());
		timer.setRepeats(true);
		timer.start();
		timer.setRepeats(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event dispatching thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
		try {
			startlog();
			getUsers();
			getHosts();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class timerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				checkKey();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	public static void checkKey() throws FileNotFoundException {
		File[] dirs = File.listRoots();
		File check = new File("check.key");
		Scanner checksc = new Scanner(check);
		String checkstr = checksc.nextLine();
		for (int j = 0; j < dirs.length; j++) {
			//System.out.println(dirs[j]);
			if (!(dirs[j].getAbsolutePath() == "C:\\")) {
				System.out.println(dirs[j].getAbsolutePath());
				if (dirs[j].exists()) {
					File[] files = dirs[j].listFiles((d, name) -> name.endsWith(".key"));
					for (int i = 0; i < files.length; i++) {
						Scanner filesc = new Scanner(files[i]);
						if (filesc.nextLine().equals(checkstr))	{
							passwordField.setEditable(false);
							usrnmField.setEditable(false);
							tabbedPane.setEnabled(true);
							lockButton.setEnabled(true);
							unLockButton.setEnabled(true);
							timer.stop();
							usrnmField.setText("ITLock");
							passwordField.setText("password");
						}
						filesc.close();
					}
				}
			}
		}
		checksc.close();
	}

	private static void getUsers() throws IOException {
		File file = new File("users.ITL"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 

		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			String[] stDataSplit = st.split("\\|");
			users.add(stDataSplit[0]);
			users2.add(stDataSplit[1]);
			System.out.println(stDataSplit[0]);
			System.out.println(stDataSplit[1]);
		}
		br.close();
	}

	private static void startlog() {
		logger = Logger.getLogger("MyLog");  
		FileHandler fh;  

		try {  

			// This block configure the logger with handler and formatter  
			fh = new FileHandler("ITLClient.log",true);  
			logger.addHandler(fh);
			fh.setFormatter(new SimpleFormatter() {
				private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

				@Override
				public synchronized String format(LogRecord lr) {
					return String.format(format,
							new Date(lr.getMillis()),
							lr.getLevel().getLocalizedName(),
							lr.getMessage()
							);
				}
			});

		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  

	}

	private static void getHosts() throws IOException {
		File file = new File("hosts.ITL"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 

		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			hostsList.add(st);
			hosts = new String[hostsList.size()+1];
			hosts[0] = "Select Host";
			for (int i = 1; i < hostsList.size()+1; i++) {
				System.out.println(hostsList.get(i-1));
				hosts[i] = hostsList.get(i-1);
				System.out.println(hosts[i]);
			}
		}
		br.close();
	}

}