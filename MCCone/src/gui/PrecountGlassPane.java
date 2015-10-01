package gui;


import information.ID;
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
 */
public class PrecountGlassPane extends JComponent{
	private GUI gui;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private int rectangleSize=100;
	private JSplitPane doublePanel;
	private Point centerPoint=null;
	private Graphics2D g2d;
	private JMenuBar menubar;

	public PrecountGlassPane(GUI gui, JSplitPane doublePanel, JMenuBar menubar, GUIListener guiListener){
		this.gui=gui;
//		this.rightPanel=rightPanel;
		this.doublePanel=doublePanel;
		this.menubar=menubar;
		this.setBackground(Color_schema.dark_50);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		this.setOpaque(false);


		addMouseListener(guiListener);
		addMouseMotionListener(guiListener);
		addMouseWheelListener(guiListener);
		guiListener.addKeyInputMap(this, ID.GLASS_PANE);

	}

	 protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
		// Apply our own painting effect
		Graphics2D g2d = (Graphics2D) g.create();

		// 50% transparent Alpha
//     g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9F));		// THIS WORKING ONLY IN WINDOWS
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F));			// THIS WORKING IN LINUX


		g2d.setColor(Color_schema.dark_40);
	//	LOGGER.fine("bachground "+this.getBackground());
		g2d.fill(determineRightPanelBounds());
		g2d.fill(this.menubar.getBounds());

		g2d.dispose();
	 /*
	    		 g.setColor(Color_schema.color_dark_50_bg);

	    		Rectangle r = determineRightPanelBounds();
	    		g.drawRect(r.x, r.y, r.width, r.height);
	   */

           if(centerPoint != null){

        	   g.setColor(Color.red);
        	   //inner circle
        	   g.drawRoundRect(centerPoint.x-rectangleSize/2, centerPoint.y-rectangleSize/2, rectangleSize, rectangleSize,rectangleSize/10,rectangleSize/10);

        	   //outer rectangle
        	   g.drawOval(centerPoint.x-rectangleSize/4, centerPoint.y-rectangleSize/4, rectangleSize/2, rectangleSize/2);
           }
           g.dispose();
	    }

	 private Rectangle determineRightPanelBounds(){
		 Point dp = doublePanel.getLocation();
		 dp.setLocation(dp.getLocation().x+doublePanel.getDividerLocation(),dp.getLocation().y+menubar.getHeight());
		 return new Rectangle(dp, new Dimension(doublePanel.getRightComponent().getWidth()+ doublePanel.getDividerSize(), doublePanel.getHeight()));
	 }



	public int getRectangleSize() {
		return rectangleSize;
	}

	public void setRectangleSize(int circleSize) {
		this.rectangleSize = circleSize;
	}

	public Point getCenterPoint() {
		return centerPoint;
	}

	public void setCenterPoint(Point circleCenterPoint) {
		this.centerPoint = circleCenterPoint;
	}




}
