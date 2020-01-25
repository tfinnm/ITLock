import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class Client extends JFrame{

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String serverIP;
	private Socket connection;
	private boolean received;

	public static void main(String[] args) {

		int time = 30;
		String[] pass = new String[3];
		String reason = "";

		if (auth()) {
			ImageIcon logo = new ImageIcon("ITLIcons.png");
			String[] Ops = {"Lock System","Lock System w/ Timer","Shutdown System","Unlock System"};
			String Op = (String) JOptionPane.showInputDialog(null, "Please Select The Action To Perform.", "IT Lock", 0, logo, Ops, Ops[0]);
			if ((Op == null)) {
				System.exit(0);
			}
			String ip = (String) JOptionPane.showInputDialog(null, "Please Enter/Select The IP Adress, ILD number, or host name of the target computer.", "IT Lock", JOptionPane.QUESTION_MESSAGE, logo, null, null);
			if ((ip == null) || (ip.equals(""))) {
				System.exit(0);
			}
			if (!(Op.equals(Ops[3]))) {
				reason = (String) JOptionPane.showInputDialog(null, "Please Enter The Reason For This Lock. \n This will be displayed to the user.", "IT Lock", JOptionPane.QUESTION_MESSAGE, logo, null, null);
				if ((reason == null)) {
					System.exit(0);
				}
			}
			if (Op.equals(Ops[1])) {
				String timeStr = (String) JOptionPane.showInputDialog(null, "Please Enter The Lock Out Time In Minutes.", "IT Lock", 0, logo, null, "5");
				time = Integer.parseInt(timeStr);
				if (time <= 0) {
					System.exit(0);
				} else {
					time = time*60;
				}
			}
			Client net = new Client(ip);
			try{
				net.connectToServer();
				net.setupStreams();
				if (Op.equals(Ops[0])) {
					pass[0] = "lk";
					pass[1] = reason;
				} else if (Op.equals(Ops[1])) {
					pass[0] = "lt";
					pass[1] = reason;
					pass[2] = String.valueOf(time);
				} else if (Op.equals(Ops[2])) {
					pass[0] = "sd";
					pass[1] = reason;
					pass[2] = String.valueOf(time);
				} else if (Op.equals(Ops[3])) {
					pass[0] = "un";
				}

				net.sendMessage(pass);
			}catch(EOFException eofException){
			}catch(IOException ioException){
				ioException.printStackTrace();
			}finally{
				net.closeConnection();
			}
		}
	}

	public static boolean auth() {
		boolean authenticated = false;

		String uPass = JOptionPane.showInputDialog(null, "Enter The Tool Admin Password or \n Insert The Key.", "IT Lock", JOptionPane.WARNING_MESSAGE);

		if (uPass == null) {
			System.exit(0);
		}

		if (uPass.equals("despacito")) {
			authenticated = true;
		}

		if (authenticated) {
			return true;
		} else {
			return auth();
		}
	}

	/*
	 * connects to a user defined host
	 * @param host is the server
	 */
	public Client(String host) {
		serverIP = host;
	}

	/**
	 * main client controller,
	 * launches the other methods here when needed.
	 */
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileConnected();
		}catch(EOFException eofException){
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			closeConnection();
		}
	}
	
	/**
	 * receives and proccesses data.
	 * @throws IOException
	 */
	private void whileConnected() throws IOException{
		do{
			try{
				BufferedImage message = (BufferedImage) input.readObject();
				
				NewClient.remoteImage = message;
				System.out.print(message);
				
				received = true;
			}catch(ClassNotFoundException classNotFoundException){
				System.out.print("The user has sent an unknown object!");
			}
		}while(!received);
	}

	/**
	 * connects to server
	 * @throws IOException
	 */
	void connectToServer() throws IOException{
		connection = new Socket(InetAddress.getByName(serverIP), 0223);
	}

	/**
	 * sets up the streams
	 * @throws IOException
	 */
	void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}


	/**
	 * Closes the connection cleanly
	 */
	void closeConnection(){
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	/**
	 * send message to server
	 * @param message is the data to send
	 */
	public void sendMessage(String[] message){
		try{
			System.out.print(message);
			output.writeObject(message);
			output.flush();
		}catch(IOException ioException){
		}
	}

}