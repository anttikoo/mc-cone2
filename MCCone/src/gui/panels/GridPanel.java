package gui.panels;

import gui.Color_schema;
import information.GridProperties;
import information.ID;
import information.PositionedRectangle;
import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Iterator;

import javax.swing.JPanel;

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
	private boolean showGrid=true;

	public GridPanel(){
		this.setOpaque(false);
		this.gridProperty=null;


	}

	public void setExtraDimTransparency(){
		this.used_transparency_hard=extra_dim_transparency_hard;
		this.used_transparency_soft=extra_dim_transparency_soft;
	}
	public void setBasicTransparency(){
		this.used_transparency_hard=basic_transparency_hard;
		this.used_transparency_soft=basic_transparency_soft;
	}

	public void setGridProperties(GridProperties gp){
		this.gridProperty=gp;
		if(this.gridProperty==null){
			setBasicTransparency(); // set the transparency to normal
		}
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(this.gridProperty != null && this.gridProperty.isGridON() && isShowGrid()){
		g2d = (Graphics2D) g.create();
			drawGrid(g2d);

			g2d.dispose();
		}
	}

	public void drawGrid(Graphics2D g2){

		g2.setPaint(thinLineColor);
		g2.setStroke(thinStroke); // set thickness
		RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_hard));
/*
		Iterator<Integer> rowIterator = this.gridProperty.getRowLineYs().iterator();
		while(rowIterator.hasNext()){
			int y= rowIterator.next();
		//	g2.drawLine(5, y, this.getBounds().width-5, y);
			drawLine(5, y, this.gridProperty.getHorizontalLineLength()-5, y);

		}
		*/
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
		/*
		Iterator<Integer> columnIterator = this.gridProperty.getColumnLineXs().iterator();
		while(columnIterator.hasNext()){
			int x= columnIterator.next();
		//	g2.drawLine(x, 5, x, this.getBounds().height-5);
			drawLine(x, 5, x, this.gridProperty.getVerticalLineLength()-5);
		}
		*/

		g2.setPaint(backGroundColor);
		Iterator<PositionedRectangle> recIterator = this.gridProperty.getPositionedRectangleList().iterator();
		while(recIterator.hasNext()){
			/*
			g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,0.7f));
			Rectangle rec= recIterator.next();
			g2.fillRect(rec.x, rec.y, rec.width, rec.height);
			*/
			PositionedRectangle pr = recIterator.next();
			if(!pr.isSelected())
			drawRectangle(pr,g2);
		}
	}

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

private void drawEdgeLine(int x1, int y1, int x2, int y2, Graphics2D g2){

	//	g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,0.4f));
	//	g2.setStroke(boldStroke);
	//	g2.setPaint(boldLineColor);
	//	g2.drawLine(x1, y1, x2, y2);
		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_hard));
		g2.setStroke(boldStroke);
		g2.setPaint(thinLineColor);
		g2.drawLine(x1, y1, x2, y2);


	}

	private void drawRectangle(PositionedRectangle rec, Graphics2D g2){

		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_hard));

		int small_x= rec.x+50;
		int small_y= rec.y+50;
		int small_width=rec.width-100;
		int small_height=rec.height-100;
		g2.fillRect(small_x, small_y, small_width, small_height);
		g2.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyModeOVER,used_transparency_soft));
	/*	g2.setPaint(Color.red);
		g2.drawLine(small_x,small_y, small_x+small_width, small_y+small_height);
		g2.drawLine(small_x+small_width,small_y, small_x, small_y+small_height);
	*/
		g2.setPaint(backGroundColor);
		g2.fillRect(rec.x, rec.y, rec.width, rec.height);


	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}
}
