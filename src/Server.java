

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.swing.*;


@SuppressWarnings("serial")
public class Server extends JFrame {

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private boolean end = false;
	private static String mode;
	public static Server net;
	private static Logger logger;

	public static void main(String[] args) {
		try {
			if (Integer.parseInt(Updater.getLatestVersion()) > 0) {
				new UpdateInfo(Updater.getWhatsNew());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		startlog();
		while (true) {
			net = new Server();
			net.startRunning();
		}
	}

	/**
	 * constructor
	 */
	public Server(){
	}

	/**
	 * runs the server and launches other parts of the server when needed
	 */
	public void startRunning(){
		try{
			server = new ServerSocket(0223, 100);
			//AILocker tron = new AILocker();
			//Thread skyNet = new Thread(tron);
			//skyNet.start();
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileConnected();

				}catch(EOFException eofException){
				} finally{
					closeConnection();
				}
			}
		} catch (IOException ioException){
			ioException.printStackTrace();
		}
	}
	/**
	 * waits for connection, then connects
	 * @throws IOException
	 */
	private void waitForConnection() throws IOException{
		connection = server.accept();
	}

	/**
	 * sets up streams to send and receive data
	 * @throws IOException
	 */
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}

	private static String[] getBlacklist() throws IOException {
		ArrayList<String> bl = new ArrayList<String>();
		File file = new File("blacklist.ITL"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 

		String st; 
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			bl.add(st);
		}
		String[] blarr = new String[bl.size()];
		for (int i = 1; i < bl.size(); i++) {
			System.out.println(bl.get(i));
			blarr[i] = bl.get(i);
			System.out.println(blarr[i]);
		}
		br.close();
		return blarr;
	}

	private static String[] getWhitelist() throws IOException {
		ArrayList<String> wl = new ArrayList<String>();
		File file = new File("whitelist.ITL"); 

		BufferedReader br = new BufferedReader(new FileReader(file)); 

		String st; 
		while ((st = br.readLine()) != null) {
			if(st.startsWith("wlmode=")) {
				mode = st.split("=")[1];
			} else {
				System.out.println(st);
				wl.add(st);
			}
		}
		System.out.println(wl);
		String[] wlarr = new String[wl.size()];
		for (int i = 0; i < wl.size(); i++) {
			System.out.println(wl.get(i));
			wlarr[i] = wl.get(i);
			System.out.println(wlarr[i]);
		}
		br.close();
		return wlarr;
	}

	/**
	 * receives and proccesses data.
	 * @throws IOException
	 */
	private void whileConnected() throws IOException{
		for (String host: getBlacklist()) {
			if (host.equals(connection.getInetAddress().toString())) {
				logger.info("Blocked attempted connection from "+connection.getInetAddress()+" [Blacklisted]");
				return;
			}
		}
		boolean wl = false;
		for (String wlhostname: getWhitelist()) {
			System.out.println(wlhostname);
			System.out.println(connection.getInetAddress().toString());
			if (wlhostname.equals(connection.getInetAddress().toString())) {
				wl = true;
			}
		}
		if (!wl && mode.equals("private")) {
			logger.info("Blocked attempted connection from "+connection.getInetAddress()+" [Not Whitelisted]");
			return;
		} else if (!wl && mode.equals("protected")) {
			if (scamGuard.wlcheck()) {
				logger.info("ITLScamGuard blocked attempted connection from "+connection.getInetAddress()+" [Not Whitelisted|Protected]");
				return;
			}
		}
		do{
			try{
				String[] message = (String[]) input.readObject();
				System.out.print(message);
				//see client for what each section does.
				if (message[0].equals("lk")) {
					String[] pass = {message[1],"0","false"};
					lockscreen.main(pass);
					logger.info("Locked by "+connection.getInetAddress()+" for reason: "+message[1]);
				} else if (message[0].equals("lt")) {
					String[] pass = {message[1],message[2],"false"};
					lockscreen.main(pass);
					logger.info("Locked by "+connection.getInetAddress()+" for " +message[2]+" seconds for reason: "+message[1]);
				} else if (message[0].equals("sd")) {
					String[] pass = {message[1],message[2],"true"};
					lockscreen.main(pass);
					logger.info("Shutdown by "+connection.getInetAddress()+" for reason: "+message[1]);
				} else if (message[0].equals("un")) {
					lockscreen.locked = false;
					lockscreen.remoteUnlock = true;
					logger.info("Unlocked by "+connection.getInetAddress());
				} else if (message[0].equals("cmd")) {
					ArrayList<String> stringnames = new ArrayList<String>();
					ArrayList<String> strings = new ArrayList<String>();
					boolean comment = false;

					String[] cmds = message[1].split("\n");
					for (int i=0; i < cmds.length; i++) {
						System.out.print(i);
						String[] subc = cmds[i].split("<<");
						if (cmds[i].startsWith("//") || cmds[i].startsWith("/*") || cmds[i].startsWith("#") || cmds[i].equals("") || (cmds[i] == null) || comment) {
							if (cmds[i].startsWith("/*")) {
								comment = true;
							}
							if (cmds[i].startsWith("*/")) {
								comment = false;
							} 
						} else if ((cmds[i].indexOf("<<") < cmds[i].indexOf(">>"))) {
							String[] subsubc = subc[1].split(">>");
							String subsubcmd = subsubc[0];
							if (subsubcmd.equals("closeWindows")) {
								try {
									Robot CMDoer = new Robot();
									CMDoer.keyPress(KeyEvent.VK_ALT);
									CMDoer.keyPress(KeyEvent.VK_F4);
									CMDoer.keyRelease(KeyEvent.VK_F4);
									CMDoer.keyRelease(KeyEvent.VK_ALT);
									logger.info(connection.getInetAddress()+" Closed Active Window.");
								} catch (AWTException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else if(subsubcmd.equals("goto")) {
								i = Math.abs(Integer.parseInt(subsubc[1].trim()))-2;

								if (i < 0) i = -1;	
							} else if(subsubcmd.equals("gotoif")) {
								String[] args = subsubc[1].split(";");
								System.out.print(args[1]);
								System.out.print(stringnames.indexOf(args[1]));
								System.out.print(Boolean.parseBoolean(strings.get(stringnames.indexOf(args[1].trim()))));
								if (Boolean.parseBoolean(strings.get(stringnames.indexOf(args[1].trim())))) {
									i = Math.abs(Integer.parseInt(args[0].trim()))-2;
								}
								if (i < 0) i = -1;
							} else if(subsubcmd.equals("out")) {
								JOptionPane.showMessageDialog(null, subsubc[1], "ITLScript", JOptionPane.INFORMATION_MESSAGE);
							} else if(subsubcmd.equals("ring")) {
								Toolkit.getDefaultToolkit().beep();
							} else if (subsubcmd.equals("wait")) {
								try {
									TimeUnit.MILLISECONDS.sleep(Integer.parseInt(subsubc[1].trim()));
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else if(subsubcmd.equals("break") || subsubcmd.equals("exit")) {
								i = cmds.length;
							} else if(subsubcmd.equals("confirm")) {
								String[] args = subsubc[1].split(";");
								boolean in = JOptionPane.showConfirmDialog(null, args[1], "ITLScript", JOptionPane.YES_NO_OPTION) == 0;
								if(stringnames.indexOf(args[0]) != -1) {
									strings.set(stringnames.indexOf(args[0]), String.valueOf(in));
								} else {
									stringnames.add(args[0]);
									strings.add(String.valueOf(in));
								}
								System.out.print(stringnames);
								System.out.println(strings);
							} else if(subsubcmd.equals("prompt")) {
								String[] args = subsubc[1].split(";");
								String in = JOptionPane.showInputDialog(null, args[1], "ITLScript", JOptionPane.QUESTION_MESSAGE);
								if(stringnames.indexOf(args[0]) != -1) {
									strings.set(stringnames.indexOf(args[0]), in);
								} else {
									stringnames.add(args[0]);
									strings.add(in);
								}
								System.out.print(stringnames);
								System.out.println(strings);
							} else if(subsubcmd.equals("compare")) {
								String[] args = subsubc[1].split(";");
								String var1 = strings.get(stringnames.indexOf(args[1]));
								String var2 = strings.get(stringnames.indexOf(args[2]));
								boolean compared = var1.equals(var2);
								if(stringnames.indexOf(args[0]) != -1) {
									strings.set(stringnames.indexOf(args[0]), String.valueOf(compared));
								} else {
									stringnames.add(args[0]);
									strings.add(String.valueOf(compared));
								}
								System.out.print(stringnames);
								System.out.println(strings);
							} else if(subsubcmd.equals("setvar")) {
								String[] args = subsubc[1].split(";");
								if(stringnames.indexOf(args[0]) != -1) {
									strings.set(stringnames.indexOf(args[0]), args[1]);
								} else {
									stringnames.add(args[0]);
									strings.add(args[1]);
								}
								System.out.print(stringnames);
								System.out.println(strings);
							} else if(subsubcmd.equals("increment")) {
								String[] args = subsubc[1].split(";");
								if(stringnames.indexOf(args[0]) != -1) {
									int value = Integer.parseInt(strings.get(stringnames.indexOf(args[0])))+1;
									strings.set(stringnames.indexOf(args[0]), String.valueOf(value));
								} else {
									stringnames.add(args[0]);
									strings.add("1");
								}
								System.out.print(stringnames);
								System.out.println(strings);
							} else if(subsubcmd.equals("decrement")) {
								String[] args = subsubc[1].split(";");
								if(stringnames.indexOf(args[0]) != -1) {
									int value = Integer.parseInt(strings.get(stringnames.indexOf(args[0])))-1;
									strings.set(stringnames.indexOf(args[0]), String.valueOf(value));
								} else {
									stringnames.add(args[0]);
									strings.add("-1");
								}
								System.out.print(stringnames);
								System.out.println(strings);
							} else if (subsubcmd.equals("screencap")) {
								try {
									SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH-mm-ss z");  
									Date date = new Date(System.currentTimeMillis());  
									String FName = String.valueOf(formatter.format(date));  
									System.out.print(FName);
									Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
									BufferedImage capture = new Robot().createScreenCapture(screenRect);
									ImageIO.write(capture, "png", new File(FName+".png"));
								} catch (AWTException e) {
									e.printStackTrace();
								}
							} else if(subsubcmd.equals("shutdown")) {

								String[] args = subsubc[1].split(";");
								if (args.length < 2) {
									String temp = args[0];
									args = new String[2];
									args[0] = temp;
									args[1] = "Locked by ITLScript; No reason Provided.";
								}
								String[] pass = {args[1],args[0],"true"};
								lockscreen.main(pass);
								logger.info("Shutdown by "+connection.getInetAddress()+" (via ITLScript) for reason: "+args[1]);
							} else if(subsubcmd.equals("timelock")) {
								String[] args = subsubc[1].split(";");
								if (args.length < 2) {
									String temp = args[0];
									args = new String[2];
									args[0] = temp;
									args[1] = "Locked by ITLScript; No reason Provided.";
								}
								String[] pass = {args[1],args[0],"false"};
								lockscreen.main(pass);
								logger.info("Locked by "+connection.getInetAddress()+" (via ITLScript) for "+args[0]+" seconds for reason: "+args[1]);
							} else if(subsubcmd.equals("lock")) {
								String[] args = subsubc[1].split(";");
								if (args.length < 2) {
									args = new String[1];
									args[0] = "Locked by ITLScript; No reason Provided.";
								}
								String[] pass = {args[0],"0","false"};
								lockscreen.main(pass);
								logger.info("Locked by "+connection.getInetAddress()+" (via ITLScript) for "+args[0]+" seconds for reason: "+args[1]);
							} else if(subsubcmd.equals("unlock")) {
								String[] args = subsubc[1].split(";");
								if (args.length < 2) {
									args = new String[1];
									args[0] = "unlocked by ITLScript; No reason Provided.";
								}
								String[] pass = {args[1],args[0],"false"};
								lockscreen.main(pass);
								logger.info("Unlocked by "+connection.getInetAddress()+" (via ITLScript) for reason: "+args[1]);
							}  else if(subsubcmd.equals("exec")) {
								Runtime runtime = Runtime.getRuntime();
								try {
									runtime.exec(subsubc[1]);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								logger.info(connection.getInetAddress()+" executed \""+subsubc[1]+"\"");
							}

						} else {
							Runtime runtime = Runtime.getRuntime();
							try {
								runtime.exec(cmds[i]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							logger.info(connection.getInetAddress()+" executed \""+cmds[i]+"\"");
						}
					}
				} else if (message[0].equals("uk")) {
					try {
						File fileToSet = new File("check.key");
						FileWriter fw;
						fw = new FileWriter(fileToSet);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(message[1]);
						bw.flush();
						bw.close();
						logger.warning("Key Updated By "+connection.getInetAddress());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}


			}catch(ClassNotFoundException classNotFoundException){
				System.out.print("The user has sent an unknown object!");
			}
		}while(!end);
	}

	/**
	 * cleanly closes the connection
	 */
	public void closeConnection(){
		System.out.print("\n Closing Connections... \n");
		try{
			output.close(); //Closes the output path to the client
			input.close(); //Closes the input path to the server, from the client.
			connection.close(); //Closes the connection between you can the client
			//startRunning();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	public void sendMessage(BufferedImage message){
		try{
			System.out.print(message);
			output.writeObject(message);
			output.flush();
		}catch(IOException ioException){
		}
	}

	private static void startlog() {
		logger = Logger.getLogger("MyLog");  
		FileHandler fh;  

		logger.addHandler(new Handler() {

			@Override
			public void close() throws SecurityException {
				// TODO Auto-generated method stub

			}

			@Override
			public void flush() {
				// TODO Auto-generated method stub

			}

			@Override
			public void publish(LogRecord arg0) {
				// TODO Auto-generated method stub

			}

		});

		try {  

			// This block configure the logger with handler and formatter  
			fh = new FileHandler("ITLServer.log",true);  
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


	/**
	 * not used
	 * TODO: remove
	 */
	public void endConnection() {
		end = true;
	}
}