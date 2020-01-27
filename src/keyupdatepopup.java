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


public class keyupdatepopup {



	private static String[] hosts;
	private static ArrayList<String> hostsList = new ArrayList<>(2);

	private JFrame frame;
	private JPanel panel;
	private JPanel panelb;
	private JComboBox hostselect;
	private JTextField key;
	private JCheckBox randombox;
	private JCheckBox savebox;
	private JButton gobutton;

	public keyupdatepopup() {

		GridLayout calcLayout = new GridLayout(3,2);

		// Create the frame
		frame = new JFrame("Key Updater");

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

		panel.add(new JLabel("New Key:"));

		key = new JTextField();
		panel.add(key);

		randombox = new JCheckBox("Random Key");
		randombox.setActionCommand("rnd");
		randombox.addActionListener(new randomListener());
		savebox = new JCheckBox("Save Key Locally");
		panel.add(randombox);
		panel.add(savebox);

		panelb = new JPanel(new BorderLayout());

		gobutton = new JButton("Set New Key");
		gobutton.setActionCommand("go");
		gobutton.addActionListener(new sendListener());
		panelb.add(gobutton,BorderLayout.SOUTH);

		panelb.add(panel,BorderLayout.CENTER);

		// Add the panel to the frame
		frame.add(panelb);

	}

	private class randomListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			key.setEnabled(!randombox.isSelected());
		}

	}

	private class sendListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!hostselect.getSelectedItem().equals("This System")) {
				Client net = new Client((String) hostselect.getSelectedItem());
				try{
					net.connectToServer();
					net.setupStreams();
					String[] pass = new String[3];
					pass[0] = "uk";
					if (randombox.isSelected()) {
						pass[1] = String.valueOf(Math.random()*1000000000);
					} else {
						pass[1] = key.getText();
					}
					net.sendMessage(pass);
				}catch(EOFException eofException){
				}catch(IOException ioException){
					ioException.printStackTrace();
				}finally{
					net.closeConnection();
				}
				NewClient.logger.warning("Updated key for "+(String) hostselect.getSelectedItem());
			} else {
				try {
					File fileToSet = new File("check.key");
					FileWriter fw;
					fw = new FileWriter(fileToSet);
					BufferedWriter bw = new BufferedWriter(fw);
					if (randombox.isSelected()) {
						bw.write(String.valueOf(Math.random()*1000000000));
					} else {
						bw.write(key.getText());
					}
					bw.flush();
					bw.close();
					NewClient.logger.warning("Updated key");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (savebox.isSelected()) {
				try {
					File fileToSaveLocal = saveDialog();
					FileWriter fw;
					fw = new FileWriter(fileToSaveLocal);
					BufferedWriter bw = new BufferedWriter(fw);
					if (randombox.isSelected()) {
						bw.write(String.valueOf(Math.random()*1000000000));
					} else {
						bw.write(key.getText());
					}
					bw.flush();
					bw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	
	private static File saveDialog() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(fc);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileToSaveLocal = fc.getSelectedFile();
            System.out.print(fileToSaveLocal.getAbsolutePath());
            String extension = "";

            int i = fileToSaveLocal.getAbsolutePath().lastIndexOf('.');
            int p = Math.max(fileToSaveLocal.getAbsolutePath().lastIndexOf('/'), fileToSaveLocal.getAbsolutePath().lastIndexOf('\\'));

            if (i > p) {
                extension = fileToSaveLocal.getAbsolutePath().substring(i+1);
            }
            if (!extension.equals("key")) {
            	fileToSaveLocal = new File(fileToSaveLocal.getAbsolutePath() + ".key");
            }
            return fileToSaveLocal;
        }
        return new File("");
	}

	private static void getHosts() throws IOException {
		File file = new File("hosts.ITL"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 

		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			hostsList.add(st);
			hosts = new String[hostsList.size()+1];
			hosts[0] = "This System";
			for (int i = 1; i < hostsList.size()+1; i++) {
				System.out.println(hostsList.get(i-1));
				hosts[i] = hostsList.get(i-1);
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
		keyupdatepopup gui = new keyupdatepopup();
		gui.display();
	}

}