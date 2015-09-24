package gui;

import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import javax.swing.JPanel;

/**
 * Class ContentPane is used as ContentPane for dialogs to get black dimming outside of window.
 * @author Antti Kurronen
 *
 */
public class ContentPane extends JPanel{
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	 /**
	 * Class constructor.
	 */
	public ContentPane() {
	        setOpaque(false);
	    }

	    @Override
	    protected void paintComponent(Graphics g) {

	        try {
				// Allow super to paint
				super.paintComponent(g);

				// Apply our own painting effect
				Graphics2D g2d = (Graphics2D) g.create();

				// 50% transparent Alpha
				//  g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F));		// THIS WORKING ONLY IN WINDOWS
				//	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.7F));			// THIS WORKING IN LINUX
				g2d.setComposite(AlphaComposite.getInstance(SharedVariables.usedDimmingMode, 0.7F)); // use SharedVarible
			//	g2d.setComposite(AlphaComposite.SrcIn.derive(0.8f));			// THIS WORKING IN LINUX
	
				g2d.setColor(getBackground());
				g2d.fill(getBounds());

				g2d.dispose();
			} catch (Exception e) {
				LOGGER.severe("Error in painting black bachground of Dialog" + e.getClass().toString() + " : " +e.getMessage());
			}

	    }
	    
	    
}
