package gui.grid;

public class SingleGridSize {
private int gridCellSize;
private int heightAlign;
private int widthAlign;
private int rows;
private int columns;

public SingleGridSize(int size, int hAlign, int wAlign, int r, int c){
	this.setGridCellSize(size);
	this.setHeightAlign(hAlign);
	this.setWidthAlign(wAlign);
	this.setRows(r);
	this.setColumns(c);
}

public int getGridCellSize() {
	return gridCellSize;
}

public void setGridCellSize(int gridCellSize) {
	this.gridCellSize = gridCellSize;
}

public int getRows() {
	return rows;
}

public void setRows(int rows) {
	this.rows = rows;
}

public int getWidthAlign() {
	return widthAlign;
}

public void setWidthAlign(int widthAlign) {
	this.widthAlign = widthAlign;
}

public int getHeightAlign() {
	return heightAlign;
}

public void setHeightAlign(int heightAlign) {
	this.heightAlign = heightAlign;
}

public int getColumns() {
	return columns;
}

public void setColumns(int columns) {
	this.columns = columns;
}
}
