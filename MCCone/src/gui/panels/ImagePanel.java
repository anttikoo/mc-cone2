package gui.panels;

import gui.Color_schema;
import information.PositionedImage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.jai.*;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;














import javax.swing.KeyStroke;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * Contains the image and the painting methods for it
 * @author Antti Kurronen
 *
 */
public class ImagePanel extends JPanel {
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private BufferedImage image_to_shown;
	private int layer_ID;
	private Rectangle imagePanelBounds;
	private Point location; // the top left corner of image to be drawn


	/**
	 *  Class constructor
	 */
	public ImagePanel(BufferedImage im, int id){
		this.image_to_shown = im;
		this.setBackground(Color_schema.dark_40);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.layer_ID=id;
		this.setLayout(null);
		this.location=new Point(0,0);


		setUpInPutMap();
	}

	/**
	 *  Class constructor
	 */
	public ImagePanel(){
		this.image_to_shown=null;
		this.setBackground(Color_schema.dark_40);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setLayout(null);
		this.location=new Point(0,0);



	}

	public void setBufferedImage(BufferedImage im){
		this.image_to_shown = im;

	}

	public void setUpInPutMap(){
		InputMap inputMap= this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(""),new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {


			}
		});
	}
	/*
	public void setImageWithImagePath(String imagePath){
		try {
			File imageFile=new File(imagePath);
			this.image_to_shown = ImageIO.read(imageFile);
		} catch (IOException e) {
			LOGGER.severe("Error in setting image from String path:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());

		}

	}
	*/


	public void setImageLocation(Point p){
		this.location=p;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(this.image_to_shown != null)
			//g.drawImage(this.image_to_shown,0,0,this.getWidth(),this.getHeight(),null);
		g.drawImage(this.image_to_shown, (int)this.location.getX(), (int)this.location.getY(), Color_schema.dark_40, null);
		g.dispose();
	}

	public void setImageAndPosition(PositionedImage pi){
		if(pi != null){
			this.setImageLocation(pi.getPosition());
			this.setBufferedImage(pi.getImage());
			pi=null;
		}
	}

	public void setImage(PositionedImage pi){
		if(pi != null){
			this.setBufferedImage(pi.getImage());
			pi=null;
		}
		else{
			this.setBufferedImage(null);
		}
	}



}
