package gui.panels;

import gui.Color_schema;
import information.ID;
import information.MarkingLayer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import operators.ShapeDrawer;




public class HighlightPanel extends JPanel{
	private Point highlightPoint;
	private float thickness;
	private float opacity;
	private int shapeID; // ID.SHAPE_CIRCLE, ID.SHAPE_CROSS, etc...
	private Graphics2D g2d;
	private ShapeDrawer shapeDrawer=null;

	public HighlightPanel() {
		this.opacity= 0.7f;
		this.setOpaque(false);
	}

	public void setLayer(MarkingLayer layer){
		if(layer != null){
			this.shapeDrawer=new ShapeDrawer(layer, layer.getSize()+2, layer.getThickness(),this.opacity );
			this.shapeID=layer.getShapeID();
		}
		else{
			this.shapeDrawer=null;
			this.shapeID=ID.UNDEFINED;
		}
	}

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


	public void updateHighlightPoint(Point highlightPoint) {

		this.highlightPoint = highlightPoint;

	}


}
