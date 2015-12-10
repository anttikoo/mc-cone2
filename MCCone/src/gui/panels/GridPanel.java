package gui.panels;

import gui.Color_schema;
import information.GridProperties;
import information.PositionedRectangle;
import information.SharedVariables;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Iterator;
import javax.swing.JPanel;

/**
 * The Class GridPanel. This Panel is drawn over image to show the grid. 
 * Draws lines of grid and grey boxes on unselected grid cells.
 */
public class GridPanel extends JPanel {
	private GridProperties gridProperty;
	private Graphics2D g2d;
	private final Color backGroundColor=Color_schema.dark_100;
	private final Color thinLineColor=Color_schema.white_230;
	private final Color boldLineColor=Color_schema.dark_30;
	private final BasicStroke thinStroke=new BasicStroke(2);
	private final BasicStroke boldStroke=new BasicStroke(4);
	private final float basic_transparency_hard=0.7f;
	private final float basic_transparency_soft=0.4f;
	private final float extra_dim_transparency_hard=0.2f;
	private final float extra_dim_transparency_soft=0.1f;
	private float used_transparency_hard=basic_transparency_hard;
	private float used_transparency_soft=basic_transparency_soft;
	private boolean showGrid=true; // is grid set as ON

	/**
	 * Instantiates a new grid panel.
	 */
	public GridPanel(){
		this.setOpaque(false);
		this.gridProperty=null;

	}

	/**
	 * Sets the extra dim transparency. Set the unselected grid cells to transparent.
	 */
	public void setExtraDimTransparency(){
		this.used_transparency_hard=extra_dim_transparency_hard;
		this.used_transparency_soft=extra_dim_transparency_soft;
	}
	
	/**
	 * Sets the basic transparency. Sets the transparency of unselected grid cell to normal state.
	 */
	public void setBasicTransparency(){
		this.used_transparency_hard=basic_transparency_hard;
		this.used_transparency_soft=basic_transparency_soft;
	}

	/**
	 * Sets the grid properties.
	 *
	 * @param gp the new GridProperties
	 */
	public void setGridProperties(GridProperties gp){
		this.gridProperty=gp;
		if(this.gridProperty==null){
			setBasicTransparency(); // set the transparency to normal
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(this.gridProperty != null && this.gridProperty.isGridON() && isShowGrid()){
		g2d = (Graphics2D) g.create();
			drawGrid(g2d);

			g2d.dispose();
		}
	}

	/**
	 * Draws a Grid lines and rectangles to Graphics2D object and return it.
	 * @param g2 Graphics2D object
	 */
	public void drawGrid(Graphics2D g2){

		g2.setPaint(thinLineColor);
		g2.setStroke(thinStroke); // set thickness
		RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_hard));

		// draw lines composing the grid.
		for(int r=0;r<this.gridProperty.getRowLineYs().size();r++){
			int y = this.gridProperty.getRowLineYs().get(r);
			if(r==0 || r== this.gridProperty.getRowLineYs().size()-1)
				drawEdgeLine(5, y, this.gridProperty.getHorizontalLineLength()-10, y,g2);
			else
				drawLine(5, y, this.gridProperty.getHorizontalLineLength()-10, y,g2);
		}
		for(int c=0;c<this.gridProperty.getColumnLineXs().size();c++){
			int x = this.gridProperty.getColumnLineXs().get(c);
			if(c==0 || c== this.gridProperty.getColumnLineXs().size()-1)
				drawEdgeLine(x, 5, x, this.gridProperty.getVerticalLineLength()-10,g2);
			else
				drawLine(x, 5, x, this.gridProperty.getVerticalLineLength()-10,g2);
		}
		
		// draw grey boxes on unselected grid cells.
		g2.setPaint(backGroundColor);
		Iterator<PositionedRectangle> recIterator = this.gridProperty.getPositionedRectangleList().iterator();
		while(recIterator.hasNext()){
			
			PositionedRectangle pr = recIterator.next();
			if(!pr.isSelected())
			drawRectangle(pr,g2);
		}
	}

	/**
	 *  Draws a line.
	 * @param x1 int start point vertical position
	 * @param y1 int start point horizontal position
	 * @param x2 int end point vertical position
	 * @param y2 int end point horizontal position
	 * @param g2 Graphics2D object
	 */
	private void drawLine(int x1, int y1, int x2, int y2, Graphics2D g2){

		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_soft));
		g2.setStroke(boldStroke);
		g2.setPaint(boldLineColor);
		g2.drawLine(x1, y1, x2, y2);
		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_hard));
		g2.setStroke(thinStroke);
		g2.setPaint(thinLineColor);
		g2.drawLine(x1, y1, x2, y2);
	}

	/**
	 *  Draws a edge line, which is closest to image edge.
	 * @param x1 int start point vertical position
	 * @param y1 int start point horizontal position
	 * @param x2 int end point vertical position
	 * @param y2 int end point horizontal position
	 * @param g2 Graphics2D object
	 */
private void drawEdgeLine(int x1, int y1, int x2, int y2, Graphics2D g2){

		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_hard));
		g2.setStroke(boldStroke);
		g2.setPaint(thinLineColor);
		g2.drawLine(x1, y1, x2, y2);


	}

	/**
	 * Draws Rectangle to GridPanel. Shows the grid rectangle, that is not selected.
	 * @param rec PositionedRectangle object
	 * @param g2 Graphics2D object
	 */
	private void drawRectangle(PositionedRectangle rec, Graphics2D g2){

		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_hard));

		int small_x= rec.x+20;
		int small_y= rec.y+20;
		int small_width=rec.width-40;
		int small_height=rec.height-40;
		g2.fillRect(small_x, small_y, small_width, small_height);
		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_soft));
		g2.setPaint(backGroundColor);
		g2.fillRect(rec.x, rec.y, rec.width, rec.height);


	}

	/**
	 * @return boolean is grid shown.
	 */
	public boolean isShowGrid() {
		return showGrid;
	}

	/**
	 * 
	 * @param showGrid boolean to set grid visible or invisible
	 */
	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}
}
