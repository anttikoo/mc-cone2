package gui;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import information.ID;
import information.SharedVariables;

public class WindowLocator {
//	private Rectangle parentRectangle=null;
	
	
	public WindowLocator(JComponent comp){
	//	this.parentRectangle=comp.getBounds();
	}
	
	public WindowLocator(JDialog d){
	//	this.parentRectangle=d.getBounds();
	}
	
	public static Rectangle getVisibleWindowBounds(Component comp){
		
		Rectangle parentRectangle=comp.getBounds();
		if(SharedVariables.operationSystem == ID.OS_LINUX_UNIX){ // in linux may the unity menu bar affect to painting of dimming 
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Rectangle windowBounds = ge.getMaximumWindowBounds();
			
			int windowLeftX = windowBounds.x;	
			/*	unused
		   	int windowRightX=windowBounds.x+windowBounds.width;	 
			int windowUpY=windowBounds.y;
			int windowDownY=windowBounds.y+windowBounds.height;
			*/
			int x=parentRectangle.x;		
			int y= parentRectangle.y;
			int width=parentRectangle.width;
			int height=parentRectangle.height;
			// GUI over window

			
			int guiX= parentRectangle.x;
		//	int guiY= parentRectangle.y;
			int guiWidth= parentRectangle.width;
		//	int guiHeight=this.getHeight();
			
			if(guiX<windowLeftX){
				x=windowLeftX;
				width = guiWidth-(windowLeftX-guiX);
			}
			
			
			
			return new Rectangle(x,y,width,height);
			
			
		}
		
		// normally 
		return parentRectangle;


	}

}
