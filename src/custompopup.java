import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class custompopup {

	private static final int FONT_SIZE = 24;
	private static final Font FONT = new Font("Terminal", Font.BOLD, FONT_SIZE);


	private static String[] hosts;
	private static ArrayList<String> hostsList = new ArrayList<>(2);

	private JFrame frame;
	private JPanel panel;
	private JPanel panelb;
	private JTextField a;
	private JTextField b;
	private JTextField c;
	private JTextField d;
	private JComboBox hostselect;
	private JButton gobutton;

	public custompopup() {

		// Create the frame
		frame = new JFrame("Custom Commands");

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Create the panel - all content goes in panels
		panel = new JPanel(new GridLayout(1,4));
		a = new JTextField();
		panel.add(a);
		b = new JTextField();
		panel.add(b);
		c = new JTextField();
		panel.add(c);
		d = new JTextField();
		panel.add(d);

		try {
			getHosts();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		panelb = new JPanel(new BorderLayout());

		hostselect = new JComboBox<String>(hosts);
		hostselect.setEditable(true);
		panelb.add(hostselect,BorderLayout.NORTH);

		panelb.add(panel,BorderLayout.CENTER);
		gobutton = new JButton("Send Command");
		gobutton.setActionCommand("go");
		gobutton.addActionListener(new sendListener());
		panelb.add(gobutton,BorderLayout.SOUTH);


		// Add the panel to the frame
		frame.add(panelb);

	}


	private class sendListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Client net = new Client((String) hostselect.getSelectedItem());
			String[] pass = {a.getText(),b.getText(),c.getText(),d.getText()};
			try{
				net.connectToServer();
				net.setupStreams();
				System.out.println(a.getText());
				System.out.println(b.getText());
				System.out.println(c.getText());
				System.out.println(d.getText());
				net.sendMessage(pass);
			}catch(EOFException eofException){
			}catch(IOException ioException){
				ioException.printStackTrace();
			}finally{
				net.closeConnection();
			}
			NewClient.logger.warning("Send Custom Command \"{"+pass[0]+","+pass[1]+","+pass[2]+","+pass[3]+"}\" to "+(String) hostselect.getSelectedItem());
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
		frame.setSize(300, (int)(250*(2.0/3.0)));
		frame.setResizable(false);
		frame.setLocation(100, 50);
		frame.setIconImage(new ImageIcon("ITLIcon.png").getImage());
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		custompopup gui = new custompopup();
		gui.display();
	}

}