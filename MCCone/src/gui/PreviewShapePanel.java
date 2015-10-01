package gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import operators.ShapeDrawer;

public class PreviewShapePanel extends JPanel{
	private ShapeDrawer shapeDrawer;
	
//	private Color shapeColor;
	private Rectangle recOfBackPanel;
	
	
	public PreviewShapePanel(float thickness, float opacity, int shapeID, int shapeSize, Color color, Rectangle recOfBackPanel, Rectangle recOfVisibleWindow){		
		this.setOpaque(false); // layer has to be transparent
		this.setBackground(new Color(0,0,0,0));
		
		this.recOfBackPanel=recOfBackPanel;
		this.setBounds(new Rectangle((int)this.recOfBackPanel.getX()-300, (int)this.recOfBackPanel.getY(), 300,(int)this.recOfBackPanel.getHeight()));
		this.setLayout(null);	
		shapeDrawer=new ShapeDrawer(shapeID, shapeSize, thickness, opacity, color);
	
		
	}
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 1.0F));			// THIS WORKING IN LINUX	
		g2d.setPaint(Color_schema.dark_30);
		g2d.setStroke(new BasicStroke(shapeDrawer.getThickness())); // set thickness
		RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);
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
		this.shapeDrawer.setShapeColor(c);
	}
}
