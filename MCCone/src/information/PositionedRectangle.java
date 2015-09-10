package information;
import java.awt.Rectangle;
public class PositionedRectangle extends Rectangle{

	private int row;
	private int column;
	private boolean selected;
//	private int row;
	public PositionedRectangle(int x, int y, int width, int height, int r, int c, boolean selected){
		super(x,y,width,height);
		this.row=r;
		this.column=c;
		this.selected=selected;
	}



	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}



	public int getRow() {
		return row;
	}



	public void setRow(int row) {
		this.row = row;
	}



	public int getColumn() {
		return column;
	}



	public void setColumn(int column) {
		this.column = column;
	}

	public boolean hasPosition(int r, int c){
		if(r==this.row && c==this.column)
			return true;
		return false;
	}

}
