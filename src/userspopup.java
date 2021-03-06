import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class userspopup implements ActionListener{

	private JFrame frame;
	private JPanel panel;
	private JComboBox<String> hostselect;
	private JTextField key;
	private JButton cngpswrdbutton;
	private JButton delbutton;
	private static ArrayList<String> users = new ArrayList<>(2);

	public userspopup() {

		GridLayout calcLayout = new GridLayout(3,2);

		// Create the frame
		frame = new JFrame("Manage Users");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Create the panel - all content goes in panels
		panel = new JPanel();

		panel.setLayout(calcLayout);


		panel.add(new JLabel("User:"));

		try {
			getUsers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] usersarray = users.toArray(new String[users.size()]);
		hostselect = new JComboBox<String>(usersarray);
		panel.add(hostselect);

		panel.add(new JLabel("Password:"));

		key = new JTextField();
		panel.add(key);

		cngpswrdbutton = new JButton("Change Password");
		cngpswrdbutton.setActionCommand("pswrd");
		cngpswrdbutton.addActionListener(this);
		delbutton = new JButton("Delete User");
		delbutton.setActionCommand("del");
		delbutton.addActionListener(this);
		panel.add(cngpswrdbutton);
		panel.add(delbutton);


		// Add the panel to the frame
		frame.add(panel);

	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("pswrd")) {
			replaceLines();
			NewClient.logger.warning("Changed Password For User \""+(String) hostselect.getSelectedItem()+"\"");
		} else if (e.getActionCommand().equals("del")) {
			removeLines();
			NewClient.logger.warning("Removed User \""+(String) hostselect.getSelectedItem()+"\"");
		}
	}

	private static void getUsers() throws IOException {
		File file = new File("users.ITL"); 
		  
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		  
		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			String[] stDataSplit = st.split("\\|");
			users.add(stDataSplit[0]);
			System.out.println(stDataSplit[0]);
		}
		br.close();
	}

	/**
	 * Customize and display the frame
	 */
	public void display() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, (int)(250*(1.0/2.0)));
		frame.setLocation(100, 50);
		frame.setIconImage(new ImageIcon("ITLIcon.png").getImage());
		frame.setVisible(true);
		frame.setResizable(false);
	}

	public static void main(String[] args) {
		userspopup gui = new userspopup();
		gui.display();
	}
	
	public void replaceLines() {
	    try {
	        BufferedReader file = new BufferedReader(new FileReader("users.ITL"));
	        StringBuffer inputBuffer = new StringBuffer();
	        String line;

	        while ((line = file.readLine()) != null) {
	        	if (line.split("\\|")[0].equals((String) hostselect.getSelectedItem())) {
	        		line = (String) hostselect.getSelectedItem()+"|"+key.getText();
	            	inputBuffer.append(line);
	            	inputBuffer.append('\n');
	        	} else {
	        		inputBuffer.append(line);
	            	inputBuffer.append('\n');
	        	}
	        }
	        file.close();

	        // write the new string with the replaced line OVER the same file
	        FileOutputStream fileOut = new FileOutputStream("users.ITL");
	        fileOut.write(inputBuffer.toString().getBytes());
	        fileOut.close();

	    } catch (Exception e) {
	        System.out.println("Problem reading file.");
	    }
	}
	
	public void removeLines() {
	    try {
	        BufferedReader file = new BufferedReader(new FileReader("users.ITL"));
	        StringBuffer inputBuffer = new StringBuffer();
	        String line;

	        while ((line = file.readLine()) != null) {
	        	if (!line.split("\\|")[0].equals((String) hostselect.getSelectedItem())) {
	        		inputBuffer.append(line);
	            	inputBuffer.append('\n');
	        	}
	        }
	        file.close();

	        // write the new string with the replaced line OVER the same file
	        FileOutputStream fileOut = new FileOutputStream("users.ITL");
	        fileOut.write(inputBuffer.toString().getBytes());
	        fileOut.close();

	    } catch (Exception e) {
	        System.out.println("Problem reading file.");
	    }
	}

}