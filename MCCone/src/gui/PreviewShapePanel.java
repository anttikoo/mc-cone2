package gui;

import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import operators.ShapeDrawer;

/**
 * The Class PreviewShapePanel. Draws a preview of style of marking to left of given parent position. 
 */
public class PreviewShapePanel extends JPanel{
	private ShapeDrawer shapeDrawer;
	private Rectangle recOfBackPanel;
	
	
	/**
	 * Instantiates a new preview shape panel. Height will be taken from given Rectangle recOfBackPanel.
	 *
	 * @param thickness the thickness of the shape
	 * @param opacity the opacity of the shape
	 * @param shapeID the id of the shape
	 * @param shapeSize the size of the shape
	 * @param color the color of the shape
	 * @param recOfBackPanel the Rectangle of visible dialog
	 * @param recOfVisibleWindow the Rectangle of the whole visible window
	 */
	public PreviewShapePanel(float thickness, float opacity, int shapeID, int shapeSize, Color color, Rectangle recOfBackPanel, Rectangle recOfVisibleWindow){		
		this.setOpaque(false); // layer has to be transparent
		this.setBackground(new Color(0,0,0,0));
		
		this.recOfBackPanel=recOfBackPanel;
		this.setBounds(new Rectangle((int)this.recOfBackPanel.getX()-300, (int)this.recOfBackPanel.getY(), 300,(int)this.recOfBackPanel.getHeight()));
		this.setLayout(null);	
		shapeDrawer=new ShapeDrawer(shapeID, shapeSize, thickness, opacity, color);
	
		
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER, 1.0F));			// THIS WORKING IN LINUX	
		g2d.setPaint(Color_schema.dark_30);
		g2d.setStroke(new BasicStroke(shapeDrawer.getThickness())); // set thickness
		RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);
		this.shapeDrawer.drawShape(g2d,this.shapeDrawer.getShapeSize()+50, this.shapeDrawer.getShapeSize()+50);
		
		g2d.dispose();
		
	}
	
	/**
	 * Sets the shape thickness.
	 *
	 * @param thickness Float the new shape thickness
	 */
	public void setShapeThickness(float thickness){
		this.shapeDrawer.setThickness(thickness);
	}
	
	/**
	 * Sets the shape size.
	 *
	 * @param size the new shape size
	 */
	public void setShapeSize(int size){
		this.shapeDrawer.setShapeSize(size);
	}
	
	/**
	 * Sets the shape opacity.
	 *
	 * @param opacity the new shape opacity
	 */
	public void setShapeOpacity(float opacity){
		this.shapeDrawer.setOpacity(opacity);
	}
	
	/**
	 * Sets the shape id.
	 *
	 * @param shapeID the new shape id
	 */
	public void setShapeID(int shapeID){
		this.shapeDrawer.setShapeID(shapeID);
	}
	
	/**
	 * Sets the shape color.
	 *
	 * @param c the new shape color
	 */
	public void setShapeColor(Color c){
		this.shapeDrawer.setShapeColor(c);
	}
}
