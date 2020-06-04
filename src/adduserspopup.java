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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class adduserspopup implements ActionListener{

//	private static final int FONT_SIZE = 24;
//	private static final Font FONT = new Font("Terminal", Font.BOLD, FONT_SIZE);



	private JFrame frame;
	private JPanel panel;
	private JTextField hostselect;
	private JTextField key;
	private JButton cngpswrdbutton;
	private static ArrayList<String> users = new ArrayList<>(2);

	public adduserspopup() {

		GridLayout calcLayout = new GridLayout(3,2);

		// Create the frame
		frame = new JFrame("New User");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Create the panel - all content goes in panels
		panel = new JPanel();

		panel.setLayout(calcLayout);


		panel.add(new JLabel("Username:"));

		try {
			getUsers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		hostselect = new JTextField();
		panel.add(hostselect);

		panel.add(new JLabel("Password:"));

		key = new JTextField();
		panel.add(key);

		cngpswrdbutton = new JButton("Create user");
		cngpswrdbutton.setActionCommand("pswrd");
		cngpswrdbutton.addActionListener(this);
		panel.add(cngpswrdbutton);


		// Add the panel to the frame
		frame.add(panel);

	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("pswrd")) {
			replaceLines();
			NewClient.logger.warning("Created User \""+(String) hostselect.getText()+"\"");
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
		adduserspopup gui = new adduserspopup();
		gui.display();
	}
	
	public void replaceLines() {
	    try {
	        BufferedReader file = new BufferedReader(new FileReader("users.ITL"));
	        StringBuffer inputBuffer = new StringBuffer();
	        String line;

	        while ((line = file.readLine()) != null) {
	        		inputBuffer.append(line);
	            	inputBuffer.append('\n');
	        }
    		line = (String) hostselect.getText()+"|"+key.getText();
        	inputBuffer.append(line);
        	inputBuffer.append('\n');
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