package information;

import java.awt.Rectangle;

/**
 * The Class PositionedRectangle. Contains data of GRID rectangle: row, column, position and size.
 */
public class PositionedRectangle extends Rectangle{
	
	/** The row position. */
	private int row;
	
	/** The column position. */
	private int column;
	
	/** The id is this PositionedRectangle selected, unselected or unchecked. */
	private int selected;
	
	
	/**
	 * Instantiates a new PositionedRectangle.
	 * 
	 *
	 * @param x the position x
	 * @param y the postion y
	 * @param width the width
	 * @param height the height
	 * @param r the row
	 * @param c the column
	 * @param selected the is GRID selected
	 */
	public PositionedRectangle(int x, int y, int width, int height, int r, int c, int selected){
		super(x,y,width,height);
		this.row=r;
		this.column=c;
		this.selected=selected;
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
	 * Returns the row.
	 *
	 * @return the row
	 */
	public int getRow() {
		return row;
	}



	/**
	 * Checks is the position as given row and column parameters.
	 *
	 * @param r the r
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean hasPosition(int r, int c){
		if(r==this.row && c==this.column)
			return true;
		return false;
	}



	/**
	 * Returns is PositionedRectangle selected, unselected, unchecked.
	 *
	 * @return Integer selected, if is selected, unselected, unchecked.
	 */
	public int isSelected() {
		return this.selected;
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
	 * Sets the row.
	 *
	 * @param row the new row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Sets the rectangle as selected.
	 *
	 * @param selected the new selected
	 */
	public void setSelected(int selectedNew) {
		if(this.isSelected() != ID.UNCHECKED)
		this.selected = selectedNew;
		else if(selectedNew == ID.UNCHECKED)
			this.selected=selectedNew;
	}

}
