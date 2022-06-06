import java.awt.*;
import java.io.IOException;

import net.sf.jmorse.AudioMorseWriter;
		
public class tests {

	public static void main(String[] args) {
//		Toolkit.getDefaultToolkit().beep(); 
//		System.out.print("\007");
//	    System.out.flush();
//		System.out.print("\u0007");
//		System.out.flush();
//		Runtime runtime = Runtime.getRuntime();
//		try {
//			runtime.exec("rundll32 user32.dll,MessageBeep");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		AudioMorseWriter morse = new AudioMorseWriter();	  
		  morse.setWpm(15);
		  morse.setVolume(10);
		  morse.setTone(450);
		  while(true)
			  morse.write("S O S  ");
	}

}
