package gui.panels;

import information.ID;
import information.MarkingLayer;
import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.JPanel;

import operators.ShapeDrawer;

public class MarkingPanel extends JPanel {
	private String name;
	private int id;
	private Color paintColor;
	private float thickness;
	private float opacity;
	private int shapeID; // ID.SHAPE_CIRCLE, ID.SHAPE_CROSS, etc...
	private int shapeSize;
	private ArrayList<Point> coordinateList;
//	private BasicStroke strokeThick;
//	private BasicStroke strokeThin;
	private int rule_alpha;
	private boolean isVisible=true;
	private Graphics2D g2d;
	private ShapeDrawer shapeDrawer;



	private final static Logger LOGGER = Logger.getLogger("MCCLogger");



	public MarkingPanel(MarkingLayer mLayer){
		this.setOpaque(false); // layer has to be transparent
	//	this.setBackground(new Color(0,0,0,0));
		this.id=mLayer.getLayerID();
		this.paintColor=mLayer.getColor();
		this.thickness=mLayer.getThickness();
		this.opacity=mLayer.getOpacity();
		this.shapeID= mLayer.getShapeID();
		this.shapeSize=mLayer.getSize();
//		this.strokeThin=new BasicStroke(this.thickness);
//		this.strokeThick=new BasicStroke(this.thickness*3);
		setCoordinateList(new ArrayList<Point>());
		this.rule_alpha=AlphaComposite.SRC_OVER;
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		this.shapeDrawer=new ShapeDrawer(mLayer, mLayer.getSize(), mLayer.getThickness(), mLayer.getOpacity());


	}

	public void setNewCursor(Cursor cursor){
		this.setCursor(cursor);
	}

	public int getId() {
		return id;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void setMarkingPanelProperties(MarkingLayer mLayer){
		//this.id=mLayer.getLayerID();
		this.paintColor=mLayer.getColor();
		this.thickness=mLayer.getThickness();
		this.opacity=mLayer.getOpacity();
		this.shapeID= mLayer.getShapeID();
		this.shapeSize=mLayer.getSize();
	//	this.strokeThin=new BasicStroke(this.thickness);
	//	this.strokeThick=new BasicStroke(this.thickness*3);
		this.shapeDrawer=new ShapeDrawer(mLayer, mLayer.getSize(), mLayer.getThickness(), mLayer.getOpacity());
	}

/*
	public void setId(int id) {
		this.id = id;
	}
*/
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		if(isVisible){
			g2d = (Graphics2D) g.create();
			g2d.setPaint(this.paintColor);
			g2d.setStroke(new BasicStroke(this.thickness)); // set thickness
			RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHints(rh);
			Iterator<Point> coordinateIterator =this.coordinateList.iterator();

				 switch(this.shapeID)
		         {
		             case ID.SHAPE_OVAL:
			     		while(coordinateIterator.hasNext()){
			     			Point c = coordinateIterator.next();
			     			this.shapeDrawer.drawOval(g2d,c.x, c.y);
			     		}
		                break;
		             case ID.SHAPE_DIAMOND:
			     		while(coordinateIterator.hasNext()){
			     			Point c = coordinateIterator.next();
			     			this.shapeDrawer.drawDiamond(g2d,c.x, c.y);
			     		}
		                 break;
		             case ID.SHAPE_PLUS:
			     		while(coordinateIterator.hasNext()){
			     			Point c = coordinateIterator.next();
			     			this.shapeDrawer.drawPlus(g2d,c.x, c.y);
			     		}
		                 break;
		             case ID.SHAPE_CROSS:
			     		while(coordinateIterator.hasNext()){
			     			Point c = coordinateIterator.next();
			     			this.shapeDrawer.drawCross(g2d,c.x, c.y);
			     		}
		                 break;
		             case ID.SHAPE_SQUARE:
			     		while(coordinateIterator.hasNext()){
			     			Point c = coordinateIterator.next();
			     			this.shapeDrawer.drawSquare(g2d,c.x, c.y);
			     		}
		                break;

		             case ID.SHAPE_TRIANGLE:
				     	while(coordinateIterator.hasNext()){
				     		Point c = coordinateIterator.next();
				     		this.shapeDrawer.drawTriangle(g2d,c.x, c.y);
				     	}
				     	break;
		             default : // circle

			     		while(coordinateIterator.hasNext()){
			     			Point c = coordinateIterator.next();
			     			this.shapeDrawer.drawCircle(g2d,c.x, c.y);
			     		}
		             	break;
		         }
			}

			g2d.dispose();

}
/*
private void drawDiamond(int x, int y){
	Point[] middlePoints = getMiddlePoints(x, y);
	Point up =middlePoints[0];
	Point right = middlePoints[1];
	Point down = middlePoints[2];
	Point left = middlePoints[3];


 g2d.setStroke(strokeThick); // set thickness bigger

     g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));

     GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

    	polyline.moveTo (up.x, up.y);

    	polyline.lineTo(right.x, right.y);
    	polyline.lineTo(down.x, down.y);
    	polyline.lineTo(left.x, left.y);
    	polyline.lineTo(up.x, up.y);
    	// draw the line
    	g2d.draw(polyline);

		g2d.setStroke(strokeThin);
		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
		g2d.draw(polyline);;


}

private void drawPlus(int x, int y){
	Point[] middlePoints = getMiddlePoints(x, y);
	Point up =middlePoints[0];
	Point right = middlePoints[1];
	Point down = middlePoints[2];
	Point left = middlePoints[3];


 g2d.setStroke(strokeThick); // set thickness bigger

     g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));

     GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

    	polyline.moveTo (up.x, up.y);
    	polyline.lineTo(down.x, down.y);
    	polyline.moveTo (left.x, left.y);
    	polyline.lineTo(right.x, right.y);

    	// draw the line
    	g2d.draw(polyline);

		g2d.setStroke(strokeThin);
		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
		g2d.draw(polyline);;

}

private void drawCross(int x, int y){
	Point[] cornerPoints = getCornerPoints(x, y);
	Point upleft =cornerPoints[0];
	Point upright = cornerPoints[1];
	Point downright = cornerPoints[2];
	Point downleft = cornerPoints[3];


 g2d.setStroke(strokeThick); // set thickness bigger

     g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));

     GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

    	polyline.moveTo (upleft.x, upleft.y);
    	polyline.lineTo(downright.x, downright.y);
    	polyline.moveTo (downleft.x, downleft.y);
    	polyline.lineTo(upright.x, upright.y);

    	// draw the line
    	g2d.draw(polyline);

		g2d.setStroke(strokeThin);
		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
		g2d.draw(polyline);;
}

private void drawOval(int x, int y){
	x=(int)((double)x-((double)shapeSize)/2);
	y=(int)((double)y-((double)shapeSize)/2);
	g2d.setStroke(strokeThick); // set thickness bigger
    g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));
     // draw thick ellipse
	g2d.draw(new Ellipse2D.Double(x, y+shapeSize*0.2, shapeSize, shapeSize*0.6));

	g2d.setStroke(strokeThin);
	g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
	// draw transparent thin rectangle
	g2d.draw(new Ellipse2D.Double(x, y+shapeSize*0.2, shapeSize, shapeSize*0.6));
}

private void drawSquare(int x, int y){
	x=(int)((double)x-((double)shapeSize)/2);
	y=(int)((double)y-((double)shapeSize)/2);
	g2d.setStroke(strokeThick); // set thickness bigger
    g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));
	// draw thick rectangle
    g2d.draw(new Rectangle2D.Double(x, y, shapeSize, shapeSize));

	g2d.setStroke(strokeThin);
	g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
	// draw transparent thin rectangle
	g2d.draw(new Rectangle2D.Double(x, y, shapeSize, shapeSize));
}

private void drawCircle(int x, int y){
	x=(int)((double)x-((double)shapeSize)/2);
	y=(int)((double)y-((double)shapeSize)/2);
	 g2d.setStroke(strokeThick); // set thickness bigger
     g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));
	 // draw transparent thick circle
	 g2d.draw(new Ellipse2D.Double(x, y, shapeSize, shapeSize));
	 //draw thin circle

	 g2d.setStroke(strokeThin);
	 g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
	 g2d.draw(new Ellipse2D.Double(x, y, shapeSize, shapeSize));
}

private void drawTriangle(int x, int y){
	Point up = new Point((x),(int)((double)y-((double)shapeSize)/2));
	Point downright = new Point((int)((double)x+((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));
	Point downleft = new Point((int)((double)x-((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));


 g2d.setStroke(strokeThick); // set thickness bigger

     g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));

     GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

    	polyline.moveTo (up.x, up.y);
    	polyline.lineTo(downright.x, downright.y);

    	polyline.lineTo(downleft.x, downleft.y);
    	polyline.closePath();
    //	polyline.lineTo(up.x, up.y);

    	// draw the line
    	g2d.draw(polyline);

		g2d.setStroke(strokeThin);
		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
		g2d.draw(polyline);;
}


private Point[] getMiddlePoints(int x,int y){
	Point up = new Point((x),(int)((double)y-((double)shapeSize)/2));
	Point right = new Point((int)((double)x+((double)shapeSize)/2),y);
	Point down = new Point((x),(int)((double)y+((double)shapeSize)/2));
	Point left = new Point((int)((double)x-((double)shapeSize)/2),y);
	return new Point[]{up,right,down,left};
}

private Point[] getCornerPoints(int x,int y){
	Point upLeft = new Point((int)((double)x-((double)shapeSize)/2),(int)((double)y-((double)shapeSize)/2));
	Point upright = new Point((int)((double)x+((double)shapeSize)/2),(int)((double)y-((double)shapeSize)/2));
	Point downright = new Point((int)((double)x+((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));
	Point downleft = new Point((int)((double)x-((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));
	return new Point[]{upLeft,upright,downright,downleft};
}
	//alculates closest Point from coordinatelist to given point. Calculates Manhattan distance.
*/ //p Point where coordinatelist Points are compared
	/**
	 *  Calculates closest Point from coordinatelist to given point. Calculates Manhattan distance.
	 * @param p Point where coordinatelist Points are compared
	 * @return return the closest point from coordinateList if the distance is small enough: @see information.SharedVariables.DISTANCE_TO_REMOVE.
	 */
	public Point getClosestMarkingPoint(Point p, int distanceParameter){
		if(isVisible){ // return null if not visible
			int min_distance=Integer.MAX_VALUE;
			Point min_point=null;
			Iterator<Point> pIterator = this.coordinateList.iterator();
			while(pIterator.hasNext()){
				Point i= pIterator.next();
				int x_distance= p.x-i.x;
				if(x_distance <0)
					x_distance*=-1;

				int y_distance= p.y-i.y;
				if(y_distance < 0)
					y_distance*=-1;
				if(x_distance+y_distance < min_distance){
					min_distance=x_distance+y_distance;
					if(min_distance <= distanceParameter)
						min_point=i;
				}


			}
			if(min_point != null)
		//	System.out.println("given point: "+p.x +" " +p.y + " found: " +min_point.x+ " "+ min_point.y);
			return min_point;
			}
		return null;
	}

	public ArrayList<Point> getCoordinateList() {
		return coordinateList;
	}

	public void setCoordinateList(ArrayList<Point> coordinateList) {
		this.coordinateList = coordinateList;
	}
}
