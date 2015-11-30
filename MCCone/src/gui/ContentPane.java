package gui;

import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Class ContentPane is used as ContentPane for dialogs to get black dimming outside of window.
 * @author Antti Kurronen
 *
 */
public class ContentPane extends JPanel{
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	

	 /**
	 * Class constructor
	 */
	public ContentPane() {
	        setOpaque(false);
	    }
	
	 /**
	 * Class constructor.s
	 */
	public ContentPane(GridBagLayout layout) {
		this.setLayout(layout);
	    setOpaque(false);
	    this.setBackground(Color_schema.dark_30);
	}
	

	    @Override
	    protected void paintComponent(Graphics g) {

	        try {
	        	
	        	
				// Allow super to paint
				super.paintComponent(g);
				
				// Apply our own painting effect
				Graphics2D g2d = (Graphics2D) g.create();
				
				
				// 70% transparent Alpha
			
				Composite com = AlphaComposite.getInstance(SharedVariables.usedDimmingMode, 0.8f);
		
				g2d.setComposite(com);
		
							
				g2d.setColor(this.getBackground());
		
				g2d.fill(getBounds());
		
				
				g2d.dispose();
			} catch (Exception e) {
				LOGGER.severe("Error in painting black background of Dialog" + e.getClass().toString() + " : " +e.getMessage());
			}

	    }
	    
	    
}
