package gui;

import javax.swing.SwingUtilities;

/**
 * Contains the Main-method, which starts the Graphical user interface (GUI)
 * @author Antti Kurronen
 */
public class MCCone {
	/**
	 *  Main method starts the GUI
	 * @param args
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new GUI();
				
			}
		});
		
		

	}

}
