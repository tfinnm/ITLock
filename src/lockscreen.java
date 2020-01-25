import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class lockscreen  extends JPanel{

	public static final int WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static final int HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();

	private BufferedImage image;
	private Timer timer;
	private Timer timer2;
	private Timer timer3;
	private Timer timer4;
	public Graphics g;
	public String msg;
	public static boolean locked = true;
	public static JFrame frame;
	public static boolean remoteUnlock = false;
	public Robot tron;
	private int time = 1;
	private boolean useTimer = false;
	private boolean shutdown;

	public void unlock() {
		if (!locked) {
			System.out.println("Closed lock screen.");
			stopTimers();
			Runtime run = Runtime.getRuntime();
			try {
				Process pro = run.exec("shutdown /a");
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			frame.dispose();
			//Server.main(new String[0]);
		}
	}

	/**
	 * calls all the required functions inside the buffered image
	 * @throws IOException 
	 */
	public lockscreen(String umsg, int seconds, boolean sd) throws IOException {

		remoteUnlock = false;
		locked = true;
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				unlock();
			}
		});

		if (seconds > 0) {
			useTimer = true;
			time = seconds;
		}

		shutdown = sd;

		try {
			tron = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH-mm-ss z");  
		Date date = new Date(System.currentTimeMillis());  
		String FName = String.valueOf(formatter.format(date));  
		System.out.print(FName);
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = tron.createScreenCapture(screenRect);
		Server.net.sendMessage(capture);
		ImageIO.write(capture, "png", new File(FName+".png"));

		msg = umsg;
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = image.getGraphics();

		if(msg.isEmpty()) {
			msg = "no information provided.";
		}
		g.setColor(Color.RED);
		g.setFont(new Font("Sans", Font.BOLD, 50));
		int swidth = g.getFontMetrics().stringWidth("System Locked!");
		g.drawString("System Locked!", (WIDTH-swidth)/2, 50);
		if (locked) {
			swidth = g.getFontMetrics().stringWidth("\uD83D\uDD12");
			g.drawString("\uD83D\uDD12", (WIDTH-swidth)/2, 125);
		} else {
			swidth = g.getFontMetrics().stringWidth("\uD83D\uDD13");
			g.drawString("\uD83D\uDD13", (WIDTH-swidth)/2, 125);
		}
		g.setFont(new Font("Sans", Font.BOLD, 25));
		swidth = g.getFontMetrics().stringWidth(msg);
		g.drawString(msg, (WIDTH-swidth)/2, 150);

		g.setFont(new Font("Sans", Font.BOLD, 15));
		g.setColor(Color.white);
		swidth = g.getFontMetrics().stringWidth("Powered By ITLock");
		g.drawString("Powered By ITLock", (WIDTH-swidth)/2, HEIGHT-50);

		timer = new Timer(5, new TimerListener());
		timer.setRepeats(true);
		timer.start();
		timer.setRepeats(true);
		timer2 = new Timer(5, new KeyCheckTimerListener());
		timer2.setRepeats(true);
		timer2.start();
		timer2.setRepeats(true);
		timer4 = new Timer(5, new RemoteCheckTimerListener());
		timer4.setRepeats(true);
		timer4.start();
		timer4.setRepeats(true);

		if (useTimer) {
			timer3 = new Timer(1000, new TimeKeeperTimerListener());
			timer3.setRepeats(true);
			timer3.start();
			timer3.setRepeats(true);
		}

		addMouseListener(new mousieboi());


	}

	private class mousieboi implements MouseListener {

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			tron.mouseMove(0, 0);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	private class TimeKeeperTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (time > 0) {
				time--;
			}
			if (time <= 0) {
				if (shutdown) {
					if (locked) {
						Runtime runtime = Runtime.getRuntime();
						try {
							Process proc = runtime.exec("shutdown -s -t 0");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					locked = false;
					unlock();
				}
			}

		}
	}

	private class KeyCheckTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			try {
				checkKey();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	private class RemoteCheckTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (remoteUnlock) {
				unlock();
			}

		}

	}

	private class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, WIDTH, HEIGHT);

			g.setColor(Color.RED);
			g.setFont(new Font("Sans", Font.BOLD, 50));
			int swidth = g.getFontMetrics().stringWidth("System Locked!");
			g.drawString("System Locked!", (WIDTH-swidth)/2, 50);
			if (locked) {
				swidth = g.getFontMetrics().stringWidth("\uD83D\uDD12");
				g.drawString("\uD83D\uDD12", (WIDTH-swidth)/2, 125);
			} else {
				swidth = g.getFontMetrics().stringWidth("\uD83D\uDD13");
				g.drawString("\uD83D\uDD13", (WIDTH-swidth)/2, 125);
			}
			g.setFont(new Font("Sans", Font.BOLD, 25));
			swidth = g.getFontMetrics().stringWidth(msg);
			g.drawString(msg, (WIDTH-swidth)/2, 150);

			int seconds = time % 60;
			int minutes = time/60;
			String timeStamp;
			String minutet;
			if (minutes < 10) {
				minutet = "0"+String.valueOf(minutes);
			} else {
				minutet = String.valueOf(minutes);
			}
			String secondt;
			if (seconds < 10) {
				secondt = "0"+String.valueOf(seconds);
			} else {
				secondt = String.valueOf(seconds);
			}
			if (minutes > 60) {
				int hours = minutes/60;
				minutes = minutes%60;
				String hourt = "";
				if (minutes < 10) {
					minutet = "0"+String.valueOf(minutes);
				} else {
					minutet = String.valueOf(minutes);
				}
				if (hours < 10) {
					hourt = "0"+String.valueOf(hours);
				} else {
					hourt = String.valueOf(hours);
				}
				timeStamp = hourt+":"+minutet+":"+secondt;
			} else {
				timeStamp = minutet+":"+secondt;
			}
			if (useTimer) {
				g.setFont(new Font("Sans", Font.BOLD, 25));
				swidth = g.getFontMetrics().stringWidth(timeStamp);
				g.drawString(timeStamp, (WIDTH-swidth)/2, 200);
			}

			g.setFont(new Font("Sans", Font.BOLD, 15));
			g.setColor(Color.white);
			swidth = g.getFontMetrics().stringWidth("Powered By ITLock");
			g.drawString("Powered By ITLock", (WIDTH-swidth)/2, HEIGHT-50);

			if (frame.getExtendedState() == 7) {
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			}

			// This is the last line of actionPerformed
			repaint();
		}
	}

	public void checkKey() throws FileNotFoundException {
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
							locked = false;
						}
						filesc.close();
					}
				}
			}
		}
		checksc.close();
	}

	/**
	 * draws the buffered image on screen
	 */
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}


	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				Toolkit.getDefaultToolkit().beep();
				frame = new JFrame("ITLock");
				ImageIcon logo = new ImageIcon("ITLIcon.png");
				frame.setIconImage(logo.getImage());
				frame.setSize(WIDTH, HEIGHT);
				frame.setLocation(0, 0);
				frame.setAlwaysOnTop(true);
				frame.setUndecorated(true);
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				frame.setResizable(false);
				frame.setCursor( Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ), new Point(), null ) );
				try {
					frame.setContentPane(new lockscreen(args[0],Integer.valueOf(args[1]),Boolean.valueOf(args[2])));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				frame.setFocusable(true);
				frame.setVisible(true);
				frame.setAutoRequestFocus(true);
			}
		}).start();
	}

	public void stopTimers() {
		timer.stop();
		timer2.stop();
		if (useTimer) {
			timer3.stop();
		}
		timer4.stop();
	}

}
