package gui.saving.ImageSet;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class GreyIcon implements Icon{
private int width;
private int height;
	
	public GreyIcon(int width, int height){
		this.width=width;
		this.height=height;
	}
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIconWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
