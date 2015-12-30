package information;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;



/**
 * One object of Gridproperties contains information of grid for one @see MarkingLayer.
 * Gridproperties determines lines of grid, visible and unsivible rectangles of grid.
 * @author Antti Kurronen
 *
 */
public class GridProperties {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The grid on. */
	private boolean gridON;
	
	/** The row line y. */
	private ArrayList<Integer> rowLineY;
	
	/** The column line x. */
	private ArrayList<Integer> columnLineX;
	
	/** The positioned rectangle list. */
	private ArrayList<PositionedRectangle> positionedRectangleList;
	
	/** The vertical line length. Used for restrict the vertical line length. */
	private int verticalLineLength=0; 
	
	/** The horizontal line length. Used for restrict the horizontal line length. */
	private int horizontalLineLength=0; 
	
	/** The present image dimension. */
	private Dimension presentImageDimension=null;
	
	/** The random percent. */
	private int randomPercent=50;


	/**
	 * Instantiates a new grid properties.
	 */
	public GridProperties(){
		setGridON(false);
		this.rowLineY = new ArrayList<Integer>();
		this.columnLineX = new ArrayList<Integer>();
		this.positionedRectangleList=new ArrayList<PositionedRectangle>();

	}

	/**
	 * Instantiates a new grid properties.
	 *
	 * @param iDimension the i dimension
	 */
	public GridProperties(Dimension iDimension){
		setGridON(false);
		this.rowLineY = new ArrayList<Integer>();
		this.columnLineX = new ArrayList<Integer>();
		this.positionedRectangleList=new ArrayList<PositionedRectangle>();
		if(iDimension != null){
			this.setPresentImageDimension(iDimension);
			this.horizontalLineLength=iDimension.width;
			this.verticalLineLength=iDimension.height;
		}
	}

	/**
	 * Adds the column line at horizonal position x.
	 *
	 * @param x the x
	 */
	public void addColumnLineX(int x){
		this.columnLineX.add(x);
	}


	/**
	 * Adds the row line at vertical position y.
	 *
	 * @param y the y
	 */
	public void addRowLineY(int y){
		this.rowLineY.add(y);
	}


/**
 * Adds the single positioned rectangle.
 *
 * @param positionedRectangle the positioned rectangle
 */
public void addSinglePositionedRectangle(PositionedRectangle positionedRectangle) {
		this.positionedRectangleList.add(positionedRectangle);
	}


	
	/**
	 * Calculates selected rectangle area relation.
	 *
	 * @return the double
	 */
	public double calculateSelectedRectangleAreaRelation(){
		
		try{
		if(this.positionedRectangleList != null && this.positionedRectangleList.size()>0){
			int count_selected=0;
			int count_unselected=0;
			Dimension pRectangleDimension=null;

			Iterator<PositionedRectangle> rIterator = this.positionedRectangleList.iterator();
			while(rIterator.hasNext()){
				PositionedRectangle pr=rIterator.next();
				if(pRectangleDimension == null)
					pRectangleDimension=pr.getSize();
				if(pr.isSelected()==ID.SELECTED)
					count_selected++;
				else if(pr.isSelected() == ID.UNSELECTED)
					count_unselected++;
			}
			
			if(this.presentImageDimension != null &&  pRectangleDimension != null && count_selected>0){
				return (((double)count_selected+(double)count_unselected)/(double)count_selected)*
						((this.presentImageDimension.getWidth()*this.presentImageDimension.getHeight())/
								(this.positionedRectangleList.size()*(pRectangleDimension.getHeight()*pRectangleDimension.getWidth())));
			}
			else
				return 0;
		}
		else
			return 0;
		}catch(Exception e){
			LOGGER.severe("Error in counting gridResults!" +e.getMessage());
			return 0;
		}
	}

	/**
	 * Changes the selection of rectangle at given imagePoint. If rectangle is originally unselected it will be selected vice versa.
	 * If there are only one selected rectangle in grid, it can't be unselected, because always has to be at least one rectangle selected.
	 * @param imagePoint Point at image
	 */
	public void changeSelectionOfPositionedRectangle(Point imagePoint){
		Iterator<PositionedRectangle> recIterator = positionedRectangleList.iterator();
		while(recIterator.hasNext()){
			PositionedRectangle rec =recIterator.next();
			if(rec.contains(imagePoint)){
				// set rectangle as selected or unselected
				if(rec.isSelected()==ID.UNSELECTED && countSelectedRectangles() > 1)
					rec.setSelected(ID.SELECTED);
				else if(rec.isSelected()==ID.SELECTED)
					rec.setSelected(ID.UNSELECTED);
			//	if(!rec.isSelected() || countSelectedRectangles() > 1)
			//	rec.setSelected(!rec.isSelected());
				
				//update the percentValue to round to the nearest multiple of 5
				this.randomPercent= (int)((Math.round((((double)countSelectedRectangles())/ ((double)this.positionedRectangleList.size())*100))/5)*5);				
			}
		}
	}

	/**
	 * Counts amount of selected rectangles.
	 *
	 * @return the int
	 */
	private int countSelectedRectangles(){
		if(this.positionedRectangleList != null && this.positionedRectangleList.size()>0){
			int counter=0;
			Iterator<PositionedRectangle> recIterator = positionedRectangleList.iterator();
			while(recIterator.hasNext()){
				PositionedRectangle rec =recIterator.next();
				if(rec.isSelected()==ID.SELECTED){
					counter++;

				}
			}
			return counter;
		}
		return 0;
	}

	/**
	 * Returns the column line xs.
	 *
	 * @return the column line xs
	 */
	public ArrayList<Integer> getColumnLineXs() {
		return columnLineX;
	}

	/**
	 * Returns the grid column count.
	 *
	 * @return the grid column count
	 */
	public int getGridColumnCount(){
		if(this.columnLineX != null && this.columnLineX.size()>0)
		return this.columnLineX.size()-1;
		else
			return 0;
	}

	/**
	 * Returns the grid row count.
	 *
	 * @return the grid row count
	 */
	public int getGridRowCount(){
		if(this.rowLineY != null && this.rowLineY.size()>0)
		return this.rowLineY.size()-1;
		else
			return 0;
	}


	/**
	 * Returns the horizontal line length.
	 *
	 * @return the horizontal line length
	 */
	public int getHorizontalLineLength() {
		return horizontalLineLength;
	}

	/**
	 * Returns the positioned rectangle list.
	 *
	 * @return the positioned rectangle list
	 */
	public ArrayList<PositionedRectangle> getPositionedRectangleList() {
		return positionedRectangleList;
	}

	/**
	 * Returns the present image dimension.
	 *
	 * @return the present image dimension
	 */
	public Dimension getPresentImageDimension() {
		return presentImageDimension;
	}
	
	/**
	 * Returns the random procent.
	 *
	 * @return the random procent
	 */
	public int getRandomProcent() {
		return randomPercent;
	}

	/**
	 * Returns the row line ys.
	 *
	 * @return the row line ys
	 */
	public ArrayList<Integer> getRowLineYs() {
		return rowLineY;
	}

	/**
	 * Returns the vertical line length.
	 *
	 * @return the vertical line length
	 */
	public int getVerticalLineLength() {
		return verticalLineLength;
	}

	/**
	 * Checks if is grid visible (ON).
	 *
	 * @return true, if is grid on
	 */
	public boolean isGridON() {
		return gridON;
	}
	
	/**
	 * Checks if is point inside any rectangle.
	 *
	 * @param p the Point
	 * @param searchSelected the search selected
	 * @return true, if is point inside any rectangle
	 */
	private boolean isPointInsideAnyRectangle(Point p, boolean searchSelected){
		if(this.positionedRectangleList != null && positionedRectangleList.size()>0){
			Iterator<PositionedRectangle> rIterator = this.positionedRectangleList.iterator();
			while(rIterator.hasNext()){
				PositionedRectangle pr=rIterator.next();
				if(pr.contains(p)){
					if(searchSelected){
						if(pr.isSelected()==ID.SELECTED)
							return true;
						else
							return false;
					}
					else{
						if(pr.isSelected()==ID.SELECTED)
							return false;
						else
							return true;
					}

				}
			}
		}
		return false;
	}

	/**
	 * Checks if is point inside selected rectangle.
	 *
	 * @param p the Point
	 * @return true, if is point inside selected rectangle
	 */
	public boolean isPointInsideSelectedRectangle(Point p){
		return isPointInsideAnyRectangle(p, true);
	}

	/**
	 * Checks if is point inside un selected rectangle.
	 *
	 * @param p the p
	 * @return true, if is point inside un selected rectangle
	 */
	public boolean isPointInsideUnSelectedRectangle(Point p){
		return isPointInsideAnyRectangle(p, false);
	}


	/**
	 * Checks if is selected, unselected or unchecked grid cell at position row, column.
	 *
	 * @param r the row
	 * @param c the column
	 * @return ID value, if selected, unselected or unchecked grid cell at position row, column.
	 */
	public int isSelectedGridCellAt(int r, int c){
		Iterator<PositionedRectangle> iIterator =this.positionedRectangleList.iterator();
		while(iIterator.hasNext()){
			PositionedRectangle pr= iIterator.next();
			if(pr.hasPosition(r, c))
				return pr.isSelected();
		}
		return ID.UNSELECTED;
	}

	/**
	 * Sets the column lines x list.
	 *
	 * @param xlist the new column lines x list
	 */
	public void setColumnLinesXList(ArrayList<Integer> xlist) {
		this.columnLineX = xlist;
	}

	/**
	 * Sets the grid on.
	 *
	 * @param gridON the new grid on
	 */
	public void setGridON(boolean gridON) {
		this.gridON = gridON;
	}
	

	/**
	 * Sets the horizontal line length.
	 *
	 * @param l the new horizontal line length
	 */
	public void setHorizontalLineLength(int l) {
		this.horizontalLineLength = l;
	}

	/**
	 * Sets the positioned rectangle list.
	 *
	 * @param positionedRectangleList the new positioned rectangle list
	 */
	public void setPositionedRectangleList(ArrayList<PositionedRectangle> positionedRectangleList) {
		this.positionedRectangleList = positionedRectangleList;
	}
	
	/**
	 * Sets the present image dimension.
	 *
	 * @param presentImageDimension the new present image dimension
	 */
	public void setPresentImageDimension(Dimension presentImageDimension) {
		this.presentImageDimension = presentImageDimension;

		this.horizontalLineLength=presentImageDimension.width;
		this.verticalLineLength=presentImageDimension.height;
	}

	/**
	 * Sets the random percent.
	 *
	 * @param randomProcent the new random percent
	 */
	public void setRandomPercent(int randomProcent) {
		this.randomPercent = randomProcent;
	}
	
	/**
	 * Sets the row lines y list.
	 *
	 * @param yList the new row lines y list
	 */
	public void setRowLinesYList(ArrayList<Integer> yList) {
		this.rowLineY = yList;
	}
	
	/**
	 * Sets the vertical line length.
	 *
	 * @param l the new vertical line length
	 */
	public void setVerticalLineLength(int l) {
		this.verticalLineLength = l;
	}

}
