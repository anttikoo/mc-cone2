package gui;

import information.ID;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.RuleBasedCollator;

import javax.swing.Icon;

public class ShapeIcon implements Icon {
	private int shapeType;
	private int width;
	private int height;
	private Color color;
	private float thickness;
	private float opacity;
	private  BasicStroke strokeBig;
    private BasicStroke strokeThin;
    private int rule_alpha;
    private Color backgroundColor;

	public ShapeIcon(int type){
		super();
		this.setShapeType(type);
	}

	public ShapeIcon(int type, int width, int height, Color color, Color bg){
		super();
		this.setShapeType(type);
		this.setWidth(width);
		this.setHeight(height);
		this.setColor(color);
		this.thickness=1.0f;
		this.opacity= 1.0f; // by now the opacity parts can't be painted right
		strokeBig = new BasicStroke(3.0f);
	    strokeThin= new BasicStroke(1.0f);
	    this.backgroundColor=bg;


	}


	/* Paints the icon depending on type of shape
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();
	rule_alpha=AlphaComposite.SRC_OVER;
	int rule_back = AlphaComposite.SRC;

	      //  BasicStroke strokeBig = new BasicStroke(3.0F);
	       // BasicStroke strokeThin= new BasicStroke(1.0F);
	        g2d.setStroke(strokeThin); // set thickness



	            x = 3; // set margin
	            y = 3;
	            int shapeWidth=getIconWidth()-6;
	            int shapeHeight = getIconHeight()-6;
	         //   Shape s = null;

	            g2d.setPaint(this.backgroundColor);
	            g2d.setComposite(AlphaComposite.getInstance(rule_back, this.opacity));
	            g2d.fillRect(0,0 , getIconWidth(), getIconHeight());
	            g2d.setPaint(this.getColor()); // set color

	            switch(this.shapeType)
	            {
	                case ID.SHAPE_OVAL:
	                    drawOval(x, y, shapeWidth, shapeHeight, g2d);
	                   break;
	                case ID.SHAPE_DIAMOND:
	                    //s = new Ellipse2D.Double(x, y, shapeWidth, shapeHeight);
	                	drawDiamond(x, y, shapeWidth, shapeHeight, g2d);
	                    break;
	                case ID.SHAPE_PLUS:
	                    drawPlus(x, y, shapeWidth, shapeHeight, g2d);
	                    break;
	                case ID.SHAPE_CROSS:
	                    drawCross(x, y, shapeWidth, shapeHeight, g2d);
	                    break;
	                case ID.SHAPE_SQUARE:
	                	//  s = new Rectangle2D.Double(x, y, shapeWidth, shapeHeight);
	                	drawSquare(x, y, shapeWidth, shapeHeight, g2d);
	                   break;
	                case ID.SHAPE_TRIANGLE:
	                	//  s = new Rectangle2D.Double(x, y, shapeWidth, shapeHeight);
	                	drawTriangle(x, y, shapeWidth, shapeHeight, g2d);
	                   break;

	                default : // circle
	                   // s = new Ellipse2D.Double(x, y, shapeWidth, shapeHeight);
	                	drawCircle(x, y, shapeWidth, shapeHeight, g2d);
	                	break;
	            }
	       //     if(shapeType > -1)
	        //    {}
	          //      g2d.draw(s);

        g2d.dispose();




	}

	private void drawDiamond(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d){
		Point up = new Point((int)shapeWidth/2+x,y);
		Point right = new Point(shapeWidth+x,(int)shapeHeight/2+y);
		Point down = new Point((int)shapeWidth/2+x, shapeHeight+y);
		Point left = new Point(x,(int)shapeHeight/2+y);


	    g2d.setStroke(strokeBig); // set thickness bigger

	        g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
	        // draw transparent fat circle
			g2d.draw(new Line2D.Double(up, right));
			g2d.draw(new Line2D.Double(right, down));
			g2d.draw(new Line2D.Double(down, left));
			g2d.draw(new Line2D.Double(left, up));

			g2d.setStroke(strokeThin);
			 g2d.setComposite(AlphaComposite.getInstance(rule_alpha, 1.0f));
			g2d.draw(new Line2D.Double(up, right));
			g2d.draw(new Line2D.Double(right, down));
			g2d.draw(new Line2D.Double(down, left));
			g2d.draw(new Line2D.Double(left, up));


	}

	private void drawPlus(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d){
		Point up = new Point((int)shapeWidth/2+x,y);
		Point right = new Point(shapeWidth+x,(int)shapeHeight/2+y);
		Point down = new Point((int)shapeWidth/2+x, shapeHeight+y);
		Point left = new Point(x,(int)shapeHeight/2+y);



	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));
        // draw transparent fat circle
    	g2d.draw(new Line2D.Double(up,down));
		g2d.draw(new Line2D.Double(right, left));

		g2d.setComposite(AlphaComposite.getInstance(rule_alpha, 1.0f));
		g2d.setStroke(strokeThin);
		g2d.draw(new Line2D.Double(up,down));
		g2d.draw(new Line2D.Double(right, left));

	}

	private void drawCross(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d){
		Point upleft = new Point(x,y);
		Point upright = new Point(shapeWidth+x,y);
		Point downleft = new Point(x, shapeHeight+y);
		Point downright = new Point(shapeWidth+x,shapeHeight+y);
		g2d.setStroke(strokeThin);
		g2d.draw(new Line2D.Double(upleft, downright));
		g2d.draw(new Line2D.Double(upright, downleft));

	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(this.rule_alpha, this.opacity));
        // draw transparent fat circle
		g2d.draw(new Line2D.Double(upleft, downright));
		g2d.draw(new Line2D.Double(upright, downleft));
	}

	private void drawOval(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d){

		g2d.draw(new Ellipse2D.Double(x, y+shapeHeight*0.2, shapeWidth, shapeHeight*0.6));
	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(this.rule_alpha, this.opacity));
        // draw transparent fat rectangle
        g2d.draw(new Ellipse2D.Double(x, y+shapeHeight*0.2, shapeWidth, shapeHeight*0.6));
	}

	private void drawSquare(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d){

		// draw thin rectangle
        g2d.draw(new Rectangle2D.Double(x, y, shapeWidth, shapeHeight));
	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(this.rule_alpha, this.opacity));
        // draw transparent fat rectangle
        g2d.draw(new Rectangle2D.Double(x, y, shapeWidth, shapeHeight));
	}

	private void drawCircle(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d){


		// draw transparent thick circle
	    g2d.setStroke(strokeBig); // set thickness bigger
        g2d.setComposite(AlphaComposite.getInstance(this.rule_alpha, this.opacity));

        g2d.draw(new Ellipse2D.Double(x, y, shapeWidth, shapeHeight));
      //draw thin circle

        g2d.setStroke(strokeThin);
        g2d.setComposite(AlphaComposite.getInstance(rule_alpha, 1.0f));
        g2d.draw(new Ellipse2D.Double(x, y, shapeWidth, shapeHeight));
	}

	private void drawTriangle(int x, int y, int shapeWidth, int shapeHeight ,Graphics2D g2d){
		Point up = new Point((int)((double)x+((double)shapeWidth)/2),y);
		Point downright = new Point((int)((double)x+((double)shapeWidth)),(int)((double)y+((double)shapeHeight)));
		Point downleft = new Point(x,(int)((double)y+((double)shapeHeight)));


	 g2d.setStroke(strokeBig); // set thickness bigger

	     g2d.setComposite(AlphaComposite.getInstance(rule_alpha, this.opacity));

	     GeneralPath polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

	    	polyline.moveTo (up.x, up.y);
	    	polyline.lineTo(downright.x, downright.y);

	    	polyline.lineTo(downleft.x, downleft.y);
	    	polyline.closePath();
	    //	polyline.lineTo(up.x, up.y);

	    	// draw the line
	    	g2d.draw(polyline);

			g2d.setStroke(strokeThin);
			g2d.setComposite(AlphaComposite.getInstance(rule_alpha, 1.0f));
			g2d.draw(polyline);;
	}


	@Override
	public int getIconWidth() {
		// TODO Auto-generated method stub
		return this.width;
	}

	@Override
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return this.height;
	}



	public void setWidth(int width) {
		this.width = width;
	}

	public int getShapeType() {
		return shapeType;
	}

	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}


	public void setHeight(int height) {
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}



}
