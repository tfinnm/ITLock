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


public class hostspopup implements ActionListener{

	private static String[] hosts;
	private static ArrayList<String> hostsList = new ArrayList<>(2);

	private JFrame frame;
	private JPanel panel;
	private JComboBox<String> hostselect;
	private JButton cngpswrdbutton;
	private JButton delbutton;

	public hostspopup() {

		GridLayout calcLayout = new GridLayout(2,2);

		// Create the frame
		frame = new JFrame("Manage Hosts");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Create the panel - all content goes in panels
		panel = new JPanel();

		panel.setLayout(calcLayout);


		panel.add(new JLabel("Host:"));

		try {
			getHosts();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hostselect = new JComboBox<String>(hosts);
		hostselect.setEditable(true);
		panel.add(hostselect);

		cngpswrdbutton = new JButton("Add Host");
		cngpswrdbutton.setActionCommand("pswrd");
		cngpswrdbutton.addActionListener(this);
		delbutton = new JButton("Remove Host");
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
			removeLines();
			replaceLines();
			NewClient.logger.info("Added Host "+(String) hostselect.getSelectedItem());
		} else if (e.getActionCommand().equals("del")) {
			NewClient.logger.info("Removed Host "+(String) hostselect.getSelectedItem());
			removeLines();
		}
	}

	private static void getHosts() throws IOException {
		File file = new File("hosts.ITL"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 

		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			hostsList.add(st);
			hosts = new String[hostsList.size()];
			for (int i = 0; i < hostsList.size(); i++) {
				System.out.println(hostsList.get(i));
				hosts[i] = hostsList.get(i);
				System.out.println(hosts[i]);
			}
		}
		br.close();
	}

	/**
	 * Customize and display the frame
	 */
	public void display() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, (int)(250*(2.0/5.0)));
		frame.setLocation(100, 50);
		frame.setIconImage(new ImageIcon("ITLIcon.png").getImage());
		frame.setVisible(true);
		frame.setResizable(false);
	}

	public static void main(String[] args) {
		hostspopup gui = new hostspopup();
		gui.display();
	}
	
	public void replaceLines() {
	    try {
	        BufferedReader file = new BufferedReader(new FileReader("hosts.ITL"));
	        StringBuffer inputBuffer = new StringBuffer();
	        String line;

	        while ((line = file.readLine()) != null) {
	            	inputBuffer.append(line);
	            	inputBuffer.append('\n');
	        }
	        line = (String) hostselect.getSelectedItem();
	        inputBuffer.append(line);
        	inputBuffer.append('\n');
	        file.close();

	        // write the new string with the replaced line OVER the same file
	        FileOutputStream fileOut = new FileOutputStream("hosts.ITL");
	        fileOut.write(inputBuffer.toString().getBytes());
	        fileOut.close();

	    } catch (Exception e) {
	        System.out.println("Problem reading file.");
	    }
	}
	
	public void removeLines() {
	    try {
	        BufferedReader file = new BufferedReader(new FileReader("hosts.ITL"));
	        StringBuffer inputBuffer = new StringBuffer();
	        String line;

	        while ((line = file.readLine()) != null) {
	        	if (!line.equals((String) hostselect.getSelectedItem())) {
	        		inputBuffer.append(line);
	            	inputBuffer.append('\n');
	        	}
	        }
	        file.close();

	        // write the new string with the replaced line OVER the same file
	        FileOutputStream fileOut = new FileOutputStream("hosts.ITL");
	        fileOut.write(inputBuffer.toString().getBytes());
	        fileOut.close();

	    } catch (Exception e) {
	        System.out.println("Problem reading file.");
	    }
	}

}