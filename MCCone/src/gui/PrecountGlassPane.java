package gui;


import information.ID;
import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;


/**
 * The Class PrecountGlassPane is used to show the precounting selector. 
 * Selector contains circle where the cell should be fitted in and around the circle is a rectangle which shouldn't contain other cells.
 */
public class PrecountGlassPane extends JComponent{
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private int rectangleSize=100;
	private JSplitPane doublePanel;
	private Point centerPoint=null;
	private JMenuBar menubar;

	public PrecountGlassPane(GUI gui, JSplitPane doublePanel, JMenuBar menubar, GUIListener guiListener){
		this.doublePanel=doublePanel;
		this.menubar=menubar;
		this.setBackground(Color_schema.dark_50);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		this.setOpaque(false);

		//add listeners
		addMouseListener(guiListener);
		addMouseMotionListener(guiListener);
		addMouseWheelListener(guiListener);
		guiListener.addKeyInputMap(this, ID.GLASS_PANE);

	}

	 protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
		// Apply our own painting effect
		Graphics2D g2d = (Graphics2D) g.create();

		// 70% transparent Alpha
		g2d.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER, 0.7F));	
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9F));		// THIS WORKING ONLY IN WINDOWS
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F));			// THIS WORKING IN LINUX

		g2d.setColor(Color_schema.dark_40);
		g2d.fill(determineRightPanelBounds());
		g2d.fill(this.menubar.getBounds());

		g2d.dispose();
	
          if(centerPoint != null){
        	  g.setColor(Color.red);
        	   //inner circle
        	  g.drawRoundRect(centerPoint.x-rectangleSize/2, centerPoint.y-rectangleSize/2, rectangleSize, rectangleSize,rectangleSize/10,rectangleSize/10);

        	  //outer rectangle
        	  g.drawOval(centerPoint.x-rectangleSize/4, centerPoint.y-rectangleSize/4, rectangleSize/2, rectangleSize/2);
           }
           g.dispose();
	    }

	 /**
 	 * Determine right panel size and location
 	 *
 	 * @return the rectangle Rectangle containing position and size of right panel (Info of ImageLayers).
 	 */
 	private Rectangle determineRightPanelBounds(){
		 Point dp = doublePanel.getLocation();
		 dp.setLocation(dp.getLocation().x+doublePanel.getDividerLocation(),dp.getLocation().y+menubar.getHeight());
		 return new Rectangle(dp, new Dimension(doublePanel.getRightComponent().getWidth()+ doublePanel.getDividerSize(), doublePanel.getHeight()));
	 }



	/**
	 * Gets the rectangle size.
	 *
	 * @return the rectangle size
	 */
	public int getRectangleSize() {
		return rectangleSize;
	}

	/**
	 * Sets the rectangle size.
	 *
	 * @param circleSize the new rectangle size
	 */
	public void setRectangleSize(int circleSize) {
		this.rectangleSize = circleSize;
	}

	/**
	 * Gets the center point.
	 *
	 * @return the center point
	 */
	public Point getCenterPoint() {
		return centerPoint;
	}

	/**
	 * Sets the center point.
	 *
	 * @param circleCenterPoint the new center point
	 */
	public void setCenterPoint(Point circleCenterPoint) {
		this.centerPoint = circleCenterPoint;
	}




}
