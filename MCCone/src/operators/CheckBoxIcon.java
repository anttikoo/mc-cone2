package operators;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * The Class CheckBoxIcon paints Icon.
 */
public class CheckBoxIcon implements Icon {
	
	  /**
  	 * Creates the image.
  	 *
  	 * @param path the path of image
  	 * @param description the description
  	 * @return the Image
  	 */
  	private Image createImage(String path, String description) {
	        URL imageURL = CheckBoxIcon.class.getResource(path);
	        Image icn = null;


	        if (imageURL == null) {
	            if(null==icn){
	                //System.out.println("path: "+path);
	                icn = new ImageIcon (CheckBoxIcon.class.getResource(path.replace("..",""))).getImage();
	                if(null!=icn)
	                    return icn;

	            }
	             return null;
	        } else {
	            return (new ImageIcon(imageURL, description)).getImage();
	        }
	    }
	  
  	/* (non-Javadoc)
  	 * @see javax.swing.Icon#getIconHeight()
  	 */
  	public int getIconHeight() {
	    return 18;
	  }
	  
  	/* (non-Javadoc)
  	 * @see javax.swing.Icon#getIconWidth()
  	 */
  	public int getIconWidth() {
	    return 18;
	  }

	  /* (non-Javadoc)
  	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
  	 */
  	public void paintIcon(Component component, Graphics g, int x, int y) {
	    AbstractButton abstractButton = (AbstractButton)component;
	    ButtonModel buttonModel = abstractButton.getModel();
	    Graphics2D g2d = (Graphics2D)g.create();
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
	    if(buttonModel.isSelected())
	        g2d.drawImage(createImage("/images/checkBox_selected.png","info"), x, y, component);
	    else
	        g2d.drawImage(createImage("/images/checkBox_unselected.png","info"), x, y, component);

	    g2d.dispose();
	  }

	}
