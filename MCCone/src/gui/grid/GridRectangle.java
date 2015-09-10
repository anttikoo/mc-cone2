package gui.grid;

import gui.Color_schema;
import information.Fonts;
import information.SharedVariables;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GridRectangle extends JPanel {
 private Dimension grid_row_column;
 private int row;
 private int column;
 private boolean isShown=true;
private JLabel label;

 public GridRectangle(int r, int c, boolean used){
	 this.isShown=used;
	 this.setBounds(0,0,100, 100);
	 this.setRow(r);
	 this.setColumn(c);
//	 this.setGrid_row_column(new Dimension(r,c));
	 this.setBackground(Color_schema.orange_medium);
	 this.setLayout(new GridBagLayout());

	 label = new JLabel("+");
	 label.setFont(Fonts.b14);
	 label.setForeground(Color_schema.dark_30);
	 this.add(label);
	 this.setToolTipText("This grid cell is used.");

	 setUpMouseListener();
	// this.setBorder(BorderFactory.createLineBorder(Color_schema.white_230, 2));
	 updatePanel();
 }


/*

 @Override
 protected void paintComponent(Graphics g) {

     try {
			// Allow super to paint
			super.paintComponent(g);
			if(isShown){
				// Apply our own painting effect
				Graphics2D g2d = (Graphics2D) g.create();


				g2d.setComposite(AlphaComposite.getInstance(SharedVariables.transparencyMode, 0.8F));
			//	g2d.setComposite(AlphaComposite.SrcIn.derive(0.8f));			// THIS WORKING IN LINUX


				g2d.setColor(getBackground());
				g2d.fill(getBounds());

				g2d.dispose();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

 }
*/
 public boolean isShown() {
	return isShown;
}

public void setShown(boolean isShown) {
	this.isShown = isShown;
}



public Dimension getGrid_row_column() {
	return grid_row_column;
}





public void setGrid_row_column(Dimension grid_row_column) {
	this.grid_row_column = grid_row_column;
}


private void setUpMouseListener(){

	 this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if(isShown())
					setShown(false);
				else
					setShown(true);

				updatePanel();
			}
			@Override
			public void mousePressed(MouseEvent e) {


			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {


			}


		});
}



public void updatePanel(){
	if(isShown){
		this.setBackground(Color_schema.orange_medium);
		 label.setForeground(Color_schema.dark_30);
		 label.setText("+");
		 this.setToolTipText("This grid cell is used.");
	}
	else{
		this.setBackground(Color_schema.dark_30);
		label.setForeground(Color_schema.orange_medium);
		 label.setText("-");
		 this.setToolTipText("This grid cell is NOT used.");
	}
	this.repaint();
}


public int getColumn() {
	return column;
}


public void setColumn(int column) {
	this.column = column;
}


public int getRow() {
	return row;
}


public void setRow(int row) {
	this.row = row;
}


}
