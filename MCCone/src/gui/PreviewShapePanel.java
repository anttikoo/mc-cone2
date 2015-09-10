package gui;

import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.text.RuleBasedCollator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import operators.ShapeDrawer;

public class PreviewShapePanel extends JPanel{
	private ShapeDrawer shapeDrawer;
	private Color shapeColor;
	private Rectangle recOfBackPanel;
	
	
	public PreviewShapePanel(float thickness, float opacity, int shapeID, int shapeSize, Color color, Rectangle recOfBackPanel, Rectangle recOfVisibleWindow){		
		this.setOpaque(false); // layer has to be transparent
		this.setBackground(Color_schema.dark_30);
		this.setBounds(new Rectangle(recOfBackPanel.x-300, recOfBackPanel.y, 300,recOfBackPanel.height));
	//	this.setBorder(BorderFactory.createLineBorder(Color_schema.white_230, 2));
		this.shapeColor=color;
		this.recOfBackPanel=recOfBackPanel;
		shapeDrawer=new ShapeDrawer(shapeID, shapeSize, thickness, opacity);
		//shapeDrawer.setRule_alpha(AlphaComposite.SRC_OVER);
		
	}
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));			// THIS WORKING IN LINUX
//		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.7f));		
		/*	
		g2d.setColor(Color.black);
		//	LOGGER.fine("bachground "+this.getBackground());
		g2d.fill(new Rectangle(0,0,this.getBounds().width, this.getBounds().height));
	*/	
		g2d.setPaint(this.shapeColor);
		g2d.setStroke(new BasicStroke(shapeDrawer.getThickness())); // set thickness
		RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);

	//	this.shapeDrawer.drawShape(g2d,this.recOfBackPanel.x-shapeDrawer.getShapeSize()-50, this.recOfBackPanel.y+this.shapeDrawer.getShapeSize());
		this.shapeDrawer.drawShape(g2d,this.shapeDrawer.getShapeSize()+50, this.shapeDrawer.getShapeSize()+50);
		
		
		g2d.dispose();
		
	}
	
	public void setShapeThickness(float thickness){
		this.shapeDrawer.setThickness(thickness);
	}
	
	public void setShapeSize(int size){
		this.shapeDrawer.setShapeSize(size);
	}
	
	public void setShapeOpacity(float opacity){
		this.shapeDrawer.setOpacity(opacity);
	}
	
	public void setShapeID(int shapeID){
		this.shapeDrawer.setShapeID(shapeID);
	}
	
	public void setShapeColor(Color c){
		this.shapeColor=c;
	}
}
