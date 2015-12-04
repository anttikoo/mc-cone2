package gui.grid;

/**
 * The Class SingleGridSize. Contains data for Grid sizes and amount of rows and columns in grid.
 */
public class SingleGridSize {
	
private int gridCellSize; // size in pixels in preview panel of GridPropertiesPanel
private int heightAlign;
private int widthAlign;
private int rows;
private int columns;

/**
 * Instantiates a new single grid size.
 *
 * @param size the size in pixels
 * @param hAlign the height align
 * @param wAlign the width align
 * @param r the rows
 * @param c the columns
 */
public SingleGridSize(int size, int hAlign, int wAlign, int r, int c){
	this.setGridCellSize(size);
	this.setHeightAlign(hAlign);
	this.setWidthAlign(wAlign);
	this.setRows(r);
	this.setColumns(c);
}

/**
 * Gets the grid cell size.
 *
 * @return the grid cell size in pixels
 */
public int getGridCellSize() {
	return gridCellSize;
}

/**
 * Sets the grid cell size.
 *
 * @param gridCellSize the new grid cell size
 */
public void setGridCellSize(int gridCellSize) {
	this.gridCellSize = gridCellSize;
}

/**
 * Gets the rows.
 *
 * @return the rows
 */
public int getRows() {
	return rows;
}

/**
 * Sets the rows.
 *
 * @param rows the new rows
 */
public void setRows(int rows) {
	this.rows = rows;
}

/**
 * Gets the width align.
 *
 * @return the width align
 */
public int getWidthAlign() {
	return widthAlign;
}

/**
 * Sets the width align.
 *
 * @param widthAlign the new width align
 */
public void setWidthAlign(int widthAlign) {
	this.widthAlign = widthAlign;
}

/**
 * Gets the height align.
 *
 * @return the height align
 */
public int getHeightAlign() {
	return heightAlign;
}

/**
 * Sets the height align.
 *
 * @param heightAlign the new height align
 */
public void setHeightAlign(int heightAlign) {
	this.heightAlign = heightAlign;
}

/**
 * Gets the columns.
 *
 * @return the columns
 */
public int getColumns() {
	return columns;
}

/**
 * Sets the columns.
 *
 * @param columns the new columns
 */
public void setColumns(int columns) {
	this.columns = columns;
}
}
