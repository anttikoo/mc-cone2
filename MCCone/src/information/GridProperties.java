package information;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
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
	private boolean gridON;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private ArrayList<Integer> rowLineY;

	private ArrayList<Integer> columnLineX;
	private ArrayList<PositionedRectangle> positionedRectangleList;
//	private ArrayList<Rectangle> selectedRectangles;
//	private ArrayList<Rectangle> unselectedRectangles;
	private int verticalLineLength=0; // used for restrict the vertical line length
	private int horizontalLineLength=0; // used for restrict the horizontal line length
	private Dimension presentImageDimension=null;
	private int randomProcent=50;
//	private ArrayList<Integer> unselectedGridCellNumbers;


	public int getRandomProcent() {
		return randomProcent;
	}

	public void setRandomProcent(int randomProcent) {
		this.randomProcent = randomProcent;
	}

	public GridProperties(Dimension iDimension){
		setGridON(false);
		this.rowLineY = new ArrayList<Integer>();
		this.columnLineX = new ArrayList<Integer>();
	//	this.selectedRectangles=new ArrayList<Rectangle>();
	//	this.unselectedRectangles=new ArrayList<Rectangle>();
	//	this.unselectedGridCellNumbers = new ArrayList<Integer>();
		this.positionedRectangleList=new ArrayList<PositionedRectangle>();
		if(iDimension != null){
			this.setPresentImageDimension(iDimension);
			this.horizontalLineLength=iDimension.width;
			this.verticalLineLength=iDimension.height;
		}
	}

	public GridProperties(){
		setGridON(false);
		this.rowLineY = new ArrayList<Integer>();
		this.columnLineX = new ArrayList<Integer>();
	//	this.selectedRectangles=new ArrayList<Rectangle>();
	//	this.unselectedRectangles=new ArrayList<Rectangle>();
	//	this.unselectedGridCellNumbers = new ArrayList<Integer>();
		this.positionedRectangleList=new ArrayList<PositionedRectangle>();

	}


/*
	public GridProperties(ArrayList<Integer> yc, ArrayList<Integer> xc, boolean on){
		setGridON(on);
		this.rowLineY=yc;
		this.columnLineX=xc;
	}

	public GridProperties(ArrayList<Integer> yc, ArrayList<Integer> xc, boolean on, ArrayList<Rectangle> selectedRectangles,ArrayList<Rectangle> unselectedRectangles){
		setGridON(on);
		this.rowLineY=yc;
		this.columnLineX=xc;
		this.selectedRectangles=selectedRectangles;
		this.unselectedRectangles=unselectedRectangles;
		this.unselectedGridCellNumbers=new ArrayList<Integer>();
	}
*/
	public void addRowLineY(int y){
		this.rowLineY.add(y);
	}

	public void addColumnLineX(int x){
		this.columnLineX.add(x);
	}

	public void setRowLinesYList(ArrayList<Integer> yList) {
		this.rowLineY = yList;
	}

	public ArrayList<Integer> getColumnLineXs() {
		return columnLineX;
	}

	public void setColumnLinesXList(ArrayList<Integer> xlist) {
		this.columnLineX = xlist;
	}

	public ArrayList<Integer> getRowLineYs() {
		return rowLineY;
	}

	public boolean isGridON() {
		return gridON;
	}

	public void setGridON(boolean gridON) {
		this.gridON = gridON;
	}

	public int getGridRowCount(){
		if(this.rowLineY != null && this.rowLineY.size()>0)
		return this.rowLineY.size()-1;
		else
			return 0;
	}

	public int getGridColumnCount(){
		if(this.columnLineX != null && this.columnLineX.size()>0)
		return this.columnLineX.size()-1;
		else
			return 0;
	}
/*
	public void addUnselectedRectangle(Rectangle rec){
		this.unselectedRectangles.add(rec);
	}

	public void addSelectedRectangle(Rectangle rec){
		this.selectedRectangles.add(rec);
	}

	public ArrayList<Rectangle> getUnselectedRectangles() {
		return unselectedRectangles;
	}

	public ArrayList<Rectangle> getselectedRectangles() {
		return selectedRectangles;
	}

	public void setUnselectedRectangles(ArrayList<Rectangle> unselectedRectangles) {
		this.unselectedRectangles = unselectedRectangles;
	}

	public void setSelectedRectangles(ArrayList<Rectangle> sRectangles) {
		this.selectedRectangles = sRectangles;
	}

	public void setUnselectedRectangleNumbers(ArrayList<Integer> unselectedRectangleNumbers) {
		this.unselectedGridCellNumbers = unselectedRectangleNumbers;
	}
*/
	public int getHorizontalLineLength() {
		return horizontalLineLength;
	}

	public void setHorizontalLineLength(int l) {
		this.horizontalLineLength = l;
	}

	public int getVerticalLineLength() {
		return verticalLineLength;
	}

	public void setVerticalLineLength(int l) {
		this.verticalLineLength = l;
	}
/*
	public ArrayList<Integer> getUnselectedGridCellNumbers() {
		return unselectedGridCellNumbers;
	}

	public void addUnselectedGridCellNumbers(int number) {
		this.unselectedGridCellNumbers.add(number);
	}

	public boolean hasUnselectedGridCellNumber(int n){
		Iterator<Integer> iIterator =this.unselectedGridCellNumbers.iterator();
		while(iIterator.hasNext()){
			if(iIterator.next() == n)
				return true;
		}
		return false;
	}
*/
	public boolean isSelectedGridCellAt(int r, int c){
		Iterator<PositionedRectangle> iIterator =this.positionedRectangleList.iterator();
		while(iIterator.hasNext()){
			PositionedRectangle pr= iIterator.next();
			if(pr.hasPosition(r, c))
				return pr.isSelected();
		}
		return false;

	}
/*
	public boolean isPointInsideSelectedRectangle(Point p){
		return isPointInsideAnyRectangle(p, this.selectedRectangles);
	}

	public boolean isPointInsideUnSelectedRectangle(Point p){
		return isPointInsideAnyRectangle(p, this.unselectedRectangles);
	}
*/
	public boolean isPointInsideSelectedRectangle(Point p){
		return isPointInsideAnyRectangle(p, true);
	}

	public boolean isPointInsideUnSelectedRectangle(Point p){
		return isPointInsideAnyRectangle(p, false);
	}

	private boolean isPointInsideAnyRectangle(Point p, boolean searchSelected){
		if(this.positionedRectangleList != null && positionedRectangleList.size()>0){
			Iterator<PositionedRectangle> rIterator = this.positionedRectangleList.iterator();
			while(rIterator.hasNext()){
				PositionedRectangle pr=rIterator.next();
				if(pr.contains(p)){
					if(searchSelected){
						if(pr.isSelected())
							return true;
						else
							return false;
					}
					else{
						if(pr.isSelected())
							return false;
						else
							return true;
					}

				}
			}
		}
		return false;

	}
/*
	private boolean isPointInsideAnyRectangle(Point p, ArrayList<Rectangle> recList){
		if(recList != null && recList.size()>0){
			Iterator<Rectangle> rIterator = recList.iterator();
			while(rIterator.hasNext()){
				if(rIterator.next().contains(p))
					return true;

			}
		}
		return false;

	}

	public double calculateSelectedAndUnselectedRectangleRelation(){
		if(this.selectedRectangles.size()>0)
			return ((double)this.selectedRectangles.size()+(double)this.unselectedRectangles.size())/(double)this.selectedRectangles.size();
		else
			return 0;
	}
*/
	/**
	 *  Counts
	 * @return double value multiplyer
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
				if(pr.isSelected())
					count_selected++;
				else
					count_unselected++;


			}
			/*
			if(count_selected>0)
				return ((double)count_selected+(double)count_unselected)/(double)count_selected;
			else
				return 0;
				*/
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
				if(!rec.isSelected() || countSelectedRectangles() > 1)
				rec.setSelected(!rec.isSelected());
				
				//update the percentValue to round to the nearest multiple of 5
				this.randomProcent= (int)((Math.round((((double)countSelectedRectangles())/ ((double)this.positionedRectangleList.size())*100))/5)*5);

				//this.randomProcent= (int)(Math.ceil(((double)countSelectedRectangles())/ ((double)this.positionedRectangleList.size())*100));

			}
		}
	}
	

	private int countSelectedRectangles(){
		if(this.positionedRectangleList != null && this.positionedRectangleList.size()>0){
			int counter=0;
			Iterator<PositionedRectangle> recIterator = positionedRectangleList.iterator();
			while(recIterator.hasNext()){
				PositionedRectangle rec =recIterator.next();
				if(rec.isSelected()){
					counter++;

				}
			}
			return counter;
		}
		return 0;
	}

	public ArrayList<PositionedRectangle> getPositionedRectangleList() {
		return positionedRectangleList;
	}
	public void setPositionedRectangleList(ArrayList<PositionedRectangle> positionedRectangleList) {
		this.positionedRectangleList = positionedRectangleList;
	}

	public void addSinglePositionedRectangle(PositionedRectangle positionedRectangle) {
		this.positionedRectangleList.add(positionedRectangle);
	}
	public Dimension getPresentImageDimension() {
		return presentImageDimension;
	}
	public void setPresentImageDimension(Dimension presentImageDimension) {
		this.presentImageDimension = presentImageDimension;

		this.horizontalLineLength=presentImageDimension.width;
		this.verticalLineLength=presentImageDimension.height;
	}




}
