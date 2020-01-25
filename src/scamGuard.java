import javax.swing.JOptionPane;

public class scamGuard {
	public static boolean wlcheck() {
		int bin = JOptionPane.showConfirmDialog(null, "A connection from a non-whitelisted device has been attempted.\nPlease contact your system administrator or IT help desk now.\n\nIf you are confident this connection is legitimate or if you have been instructed to do so, press OK to continue, otherwise, wait for further instructions.", "ITLScamGuard", JOptionPane.WARNING_MESSAGE);
		System.out.print(bin);
		if (bin == 0) {
			return false;
		} else if (bin == 2) {
			return true;
		} else if (bin == -1) {
			return wlcheck();
		}
		return true;
	}
}
