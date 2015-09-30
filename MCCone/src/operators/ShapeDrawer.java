package operators;

import information.ID;
import information.MarkingLayer;
import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public class ShapeDrawer {
	private BasicStroke strokeThick;
	private BasicStroke strokeThin;
	private int rule_alpha;
	

	private float thickness;
	private float opacity;
	private int shapeID; // ID.SHAPE_CIRCLE, ID.SHAPE_CROSS, etc...
	private int shapeSize;


	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}
	
	public int getShapeSize() {
		return shapeSize;
	}

	public void setShapeSize(int shapeSize) {
		this.shapeSize = shapeSize;
	}
	
	public float getThickness() {
		return thickness;
	}
	
	public void setThickness(float thickness) {
		this.thickness = thickness;
		this.strokeThin=new BasicStroke(this.thickness);
		this.strokeThick=new BasicStroke(this.thickness*3);
	}

	public ShapeDrawer(MarkingLayer ml, int size, int thick, float opacity){
//		this.rule_alpha=AlphaComposite.SRC_OVER;
		this.rule_alpha=SharedVariables.transparencyModeOVER;
		this.thickness=thick;
		this.opacity= opacity;
		this.setShapeID(ml.getShapeID());
		this.shapeSize=size;
		this.strokeThin=new BasicStroke(this.thickness);
		this.strokeThick=new BasicStroke(this.thickness*3);

	}
	
	public ShapeDrawer(int shapeID, int size, float thick, float opa){
	//	this.rule_alpha=AlphaComposite.SRC_OVER;
		this.rule_alpha=SharedVariables.transparencyModeOVER;
		this.thickness=thick;
		this.opacity= opa;
		this.setShapeID(shapeID);
		this.shapeSize=size;
		this.strokeThin=new BasicStroke(this.thickness);
		this.strokeThick=new BasicStroke(this.thickness*3);
	}

	public void increaseThickness(){
		this.thickness+=2;
		this.strokeThick=new BasicStroke((thickness*3));
		this.strokeThin = new BasicStroke((this.thickness));
	}

	public void decreaseThickness(){
		this.thickness-=2;
		this.strokeThick=new BasicStroke((thickness*3));
		this.strokeThin = new BasicStroke((this.thickness));
	}
	
	public int getRule_alpha() {
		return rule_alpha;
	}

	public void setRule_alpha(int rule_alpha) {
		this.rule_alpha = rule_alpha;
	}
	
	public Graphics2D drawShape(Graphics2D g2d, int x, int y){
		switch (shapeID){
			case ID.SHAPE_CIRCLE:
				return drawCircle(g2d,x, y);
				
			case ID.SHAPE_CROSS:
				return drawCross(g2d,x, y);
				
			case ID.SHAPE_DIAMOND:
				return drawDiamond(g2d,x, y);	
				
			case ID.SHAPE_OVAL:
				return drawOval(g2d,x, y);	
				
			case ID.SHAPE_PLUS:
				return drawPlus(g2d,x, y);	
				
			case ID.SHAPE_SQUARE:
				return drawSquare(g2d,x, y);
				
			case ID.SHAPE_TRIANGLE:
				return drawTriangle(g2d,x, y);	
				
			
			
		}
		
		return g2d; // should newer be reached here
	}

	/**
	 * Draw diamond.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 */
	public Graphics2D drawDiamond(Graphics2D g2d, int x, int y){

		Point[] middlePoints = getMiddlePoints(x, y);
		Point up =middlePoints[0];
		Point right = middlePoints[1];
		Point down = middlePoints[2];
		Point left = middlePoints[3];

		GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

    	polyline.moveTo (up.x, up.y);
    	polyline.lineTo(right.x, right.y);
    	polyline.lineTo(down.x, down.y);
    	polyline.lineTo(left.x, left.y);
    	polyline.lineTo(up.x, up.y);

    	g2d.setStroke(strokeThick); // set thickness bigger
    	g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity/3));

    	// draw the line
    	g2d.draw(polyline);

		g2d.setStroke(strokeThin);
		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
		g2d.draw(polyline);

		return g2d;


	}

	/**
	 * Draws a plus.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 */
	public Graphics2D drawPlus(Graphics2D g2d, int x, int y){
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
			g2d.draw(polyline);

			return g2d;

	}

	/**
	 * Draws a cross.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 */
	public Graphics2D drawCross(Graphics2D g2d, int x, int y){
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
			g2d.draw(polyline);

			return g2d;
	}

	/**
	 * Draws a oval.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 */
	public Graphics2D drawOval(Graphics2D g2d, int x, int y){
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

		return g2d;
	}

	/**
	 * Draws a square.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 */
	public Graphics2D drawSquare(Graphics2D g2d, int x, int y){
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

		return g2d;
	}

	/**
	 * Draw circle.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.
	 */
	public Graphics2D drawCircle(Graphics2D g2d, int x, int y){
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

		 return g2d;
	}

	/**
	 * Draw triangle.
	 *
	 * @param g2d the Graphics2D object.
	 * @param x the top left horizontal point of shape
	 * @param y the y top left vertical point of shape
	 * @return the Graphics2D object.	 
	 */
	public Graphics2D drawTriangle(Graphics2D g2d, int x, int y){
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
			g2d.draw(polyline);

			return g2d;
	}


	/**
	 * Gets the middle points.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the middle points
	 */
	private Point[] getMiddlePoints(int x,int y){
		Point up = new Point((x),(int)((double)y-((double)shapeSize)/2));
		Point right = new Point((int)((double)x+((double)shapeSize)/2),y);
		Point down = new Point((x),(int)((double)y+((double)shapeSize)/2));
		Point left = new Point((int)((double)x-((double)shapeSize)/2),y);
		return new Point[]{up,right,down,left};
	}

	/**
	 * Gets the corner points.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the corner points
	 */
	private Point[] getCornerPoints(int x,int y){
		Point upLeft = new Point((int)((double)x-((double)shapeSize)/2),(int)((double)y-((double)shapeSize)/2));
		Point upright = new Point((int)((double)x+((double)shapeSize)/2),(int)((double)y-((double)shapeSize)/2));
		Point downright = new Point((int)((double)x+((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));
		Point downleft = new Point((int)((double)x-((double)shapeSize)/2),(int)((double)y+((double)shapeSize)/2));
		return new Point[]{upLeft,upright,downright,downleft};
	}

	/**
	 * Gets the shape id.
	 *
	 * @return the shape id
	 */
	public int getShapeID() {
		return shapeID;
	}

	/**
	 * Sets the shape id.
	 *
	 * @param shapeID the new shape id
	 */
	public void setShapeID(int shapeID) {
		this.shapeID = shapeID;
	}
}
