package gui.panels;

import gui.Color_schema;
import information.ID;
import information.MarkingLayer;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import operators.ShapeDrawer;




/**
 * The Class HighlightPanel. Draws highlight shape on shape when mouse hovered over.
 */
public class HighlightPanel extends JPanel{
	
	/** The highlighted point. */
	private Point highlightPoint;
	
	/** The thickness. */
	private float thickness;
	
	/** The opacity. */
	private float opacity;
	
	/** The shape id. ID.SHAPE_CIRCLE, ID.SHAPE_CROSS, etc... */
	private int shapeID;
	
	/** The Graphics2D object. */
	private Graphics2D g2d;
	
	/** The shape drawer. */
	private ShapeDrawer shapeDrawer=null;

	/**
	 * Instantiates a new highlight panel.
	 */
	public HighlightPanel() {
		this.opacity= 0.7f;
		this.setOpaque(false);
	}

	/**
	 * Sets the MarkingLayer, which markings are viewed.
	 *
	 * @param layer the new layer
	 */
	public void setLayer(MarkingLayer layer){
		if(layer != null){
			this.shapeDrawer=new ShapeDrawer(layer, layer.getSize()+2, layer.getThickness(),this.opacity, layer.getColor());
			this.shapeID=layer.getShapeID();
		}
		else{
			this.shapeDrawer=null;
			this.shapeID=ID.UNDEFINED;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(this.highlightPoint != null && this.shapeDrawer !=null){
		//	System.out.println("drawing!!!");
			g2d = (Graphics2D) g.create();
			g2d.setPaint(Color_schema.orange_medium);
			g2d.setStroke(new BasicStroke(this.thickness*5)); // set thickness
			RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHints(rh);


				 switch(this.shapeID)
		         {
		             case ID.SHAPE_OVAL:
			     			this.shapeDrawer.drawOval(g2d,highlightPoint.x, highlightPoint.y);
			     			g2d.setPaint(Color_schema.white_230);
			     			this.shapeDrawer.increaseThickness();
			     			this.shapeDrawer.drawOval(g2d,highlightPoint.x, highlightPoint.y);
		                break;
		             case ID.SHAPE_DIAMOND:


			     			this.shapeDrawer.drawDiamond(g2d,highlightPoint.x, highlightPoint.y);
			     			g2d.setPaint(Color_schema.white_230);
			     			this.shapeDrawer.increaseThickness();
			     			this.shapeDrawer.drawDiamond(g2d,highlightPoint.x, highlightPoint.y);

		                 break;
		             case ID.SHAPE_PLUS:
			     			this.shapeDrawer.drawPlus(g2d,highlightPoint.x, highlightPoint.y);
			     			g2d.setPaint(Color_schema.white_230);
			     			this.shapeDrawer.increaseThickness();
			     			this.shapeDrawer.drawPlus(g2d,highlightPoint.x, highlightPoint.y);

		                 break;
		             case ID.SHAPE_CROSS:
			     			this.shapeDrawer.drawCross(g2d,highlightPoint.x, highlightPoint.y);
			     			g2d.setPaint(Color_schema.white_230);
			     			this.shapeDrawer.increaseThickness();
			     			this.shapeDrawer.drawCross(g2d,highlightPoint.x, highlightPoint.y);
		                 break;

		             case ID.SHAPE_SQUARE:
			     			this.shapeDrawer.drawSquare(g2d,highlightPoint.x, highlightPoint.y);
			     			g2d.setPaint(Color_schema.white_230);
			     			this.shapeDrawer.increaseThickness();
			     			this.shapeDrawer.drawSquare(g2d,highlightPoint.x, highlightPoint.y);
		                break;

		             case ID.SHAPE_TRIANGLE:
				     		this.shapeDrawer.drawTriangle(g2d,highlightPoint.x, highlightPoint.y);
				     		g2d.setPaint(Color_schema.white_230);
			     			this.shapeDrawer.increaseThickness();
			     			this.shapeDrawer.drawTriangle(g2d,highlightPoint.x, highlightPoint.y);
				     	break;

		             default : // circle
			     			this.shapeDrawer.drawCircle(g2d,highlightPoint.x, highlightPoint.y);
			     			g2d.setPaint(Color_schema.white_230);
			     			this.shapeDrawer.increaseThickness();
			     			this.shapeDrawer.drawCircle(g2d,highlightPoint.x, highlightPoint.y);
		             	break;
		         }

				 this.shapeDrawer.decreaseThickness();
			g2d.dispose();
		}
}


	/**
	 * Updates highlighted point.
	 *
	 * @param highlightPoint the highlight Point
	 */
	public void updateHighlightPoint(Point highlightPoint) {

		this.highlightPoint = highlightPoint;

	}


}
