package gui.grid;

import gui.Color_schema;
import gui.ContentPane;
import information.Fonts;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * The Class GridRectangle. Contains data of single Grid cell: position and is selected.
 */
public class GridRectangle extends JPanel {
 
 /** The grid row and column number. */
 private Dimension grid_row_column;
 
 /** The row. */
 private int row;
 
 /** The column. */
 private int column;
 
 /** The is selected. */
 private boolean isSelected=true;

/** The label. */
private JLabel label;

 /**
  * Instantiates a new grid rectangle.
  *
  * @param r the row position
  * @param c the column position
  * @param selected boolean is grid cell selected
  */
 public GridRectangle(int r, int c, boolean selected){
	 this.isSelected=selected;
	 this.setBounds(0,0,100, 100);
	 this.setRow(r);
	 this.setColumn(c);
	 this.setBackground(Color_schema.orange_medium);
	 this.setLayout(new GridBagLayout());

	 label = new JLabel("+");
	 label.setFont(Fonts.b14);
	 label.setForeground(Color_schema.dark_30);
	 this.add(label);
	 this.setToolTipText("This grid cell is used.");

	 setUpMouseListener();
	 updatePanel();
 }


 /**
  * Checks if is grid selected.
  *
  * @return true, if is selected
  */
 public boolean isSelected() {
	return isSelected;
}

/**
 * Sets the shown.
 *
 * @param selected boolean is grid cell selected
 */
public void setSelected(boolean selected) {
	this.isSelected = selected;
}


/**
 * Gets the grid_row_column.
 *
 * @return the Dimension grid_row_column
 */
public Dimension getGrid_row_column() {
	return grid_row_column;
}


/**
 * Sets the grid_row_column.
 *
 * @param grid_row_column the new grid_row_column
 */
public void setGrid_row_column(Dimension grid_row_column) {
	this.grid_row_column = grid_row_column;
}


/**
 * Sets up the mouseListener.
 */
private void setUpMouseListener(){

	 this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if(isSelected()){
					int counter=0;
					// check that at least one selected cell will be remain				
					JPanel p = ((JPanel)((GridRectangle)e.getSource()).getParent()); // get owner of GridRectangles
					if(p != null){
						Component[] c=p.getComponents();
						if(c != null && c.length>0){
							System.out.println(c.length);
							
							for (int i = 0; i < c.length; i++) { // go through GridRectangles
								Component sc = c[i];
								if(sc != null){
									if(sc instanceof GridRectangle){
									GridRectangle gr = (GridRectangle)sc;
									if (gr != null){
										if(gr.isSelected)
											counter++;
									}
									
									}
								}
							}
							
						}	
					}
					if(counter>1) // still remains other selected cells
						setSelected(false);
				}
				else
					setSelected(true);

				updatePanel();
			}
			@Override
			public void mousePressed(MouseEvent e) {


			}

			@Override
			public void mouseExited(MouseEvent e) {
				

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				

			}

			@Override
			public void mouseClicked(MouseEvent e) {


			}


		});
}



/**
 * Updates the color and tooltiptext of JPanel of this grid cell.
 */
public void updatePanel(){
	if(isSelected){
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


/**
 * Returns the column.
 *
 * @return the column
 */
public int getColumn() {
	return column;
}


/**
 * Sets the column.
 *
 * @param column the new column
 */
public void setColumn(int column) {
	this.column = column;
}


/**
 * returns the row in grid.
 *
 * @return the row
 */
public int getRow() {
	return row;
}


/**
 * Sets the row in grid.
 *
 * @param row the new row
 */
public void setRow(int row) {
	this.row = row;
}


}
