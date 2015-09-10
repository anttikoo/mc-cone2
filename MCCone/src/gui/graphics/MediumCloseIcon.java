package gui.graphics;


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

public class MediumCloseIcon implements Icon {
	private boolean selected=false;

	public MediumCloseIcon(boolean selected){
		this.selected=selected;

	}
	  public void paintIcon(Component component, Graphics g, int x, int y) {
		  x =0;y =0;
	    AbstractButton abstractButton = (AbstractButton)component;
	    ButtonModel buttonModel = abstractButton.getModel();
	    Graphics2D g2d = (Graphics2D)g.create();
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
	    if(this.selected)
	        g2d.drawImage(createImage("/images/close_medium_selected.png","info"), x, y, component);
	    else
	        g2d.drawImage(createImage("/images/close_medium.png","info"), x, y, component);

	    g2d.dispose();
	  }
	  public int getIconWidth() {
	    return 20;
	  }
	  public int getIconHeight() {
	    return 20;
	  }

	  private Image createImage(String path, String description) {
	        URL imageURL = SmallCloseIcon.class.getResource(path);
	        Image icn = null;


	        if (imageURL == null) {
	            if(icn==null){
	                //System.out.println("path: "+path);
	                icn = new ImageIcon (SmallCloseIcon.class.getResource(path.replace("..",""))).getImage();
	                if(icn != null)
	                    return icn;

	            }
	             return null;
	        } else {
	            return (new ImageIcon(imageURL, description)).getImage();
	        }
	    }

	}