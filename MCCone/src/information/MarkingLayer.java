package information;


import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;



import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;

import operators.CoordinateComparator;
import operators.MarkingShape;


public class MarkingLayer{


	private ArrayList<Point> coordinateList;
	private int layerID;
	private String layerName;
	private Color color;
	private int shapeID;
	private int size;
	private int thickness;
	private float opacity;
	private int removeDistance=7;
	private boolean isVisible=true;
	private boolean isSelected=false;
	private GridProperties gridProperties=null;
	private boolean drawGridToImage=false;

	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	//private String xmlFilePath;

	public MarkingLayer(String name){
		this.setLayerID(-1); // initialize the id to negative value
		this.setLayerName(name);
		this.setColor(new Color(100,255,0)); // should change to get color which is not used yet
		this.setShapeID(ID.SHAPE_CIRCLE); // initial value -> changed later
		coordinateList = new ArrayList<Point>();
		this.size=20;	// initial value -> changed later
		this.thickness=2; // initial value -> changed later
		this.opacity=1.0F; // initial value -> changed later
	}

	public MarkingLayer(String name, int shape, Color color){
		this.setLayerID(-1); // initialize the id to negative value
		this.setLayerName(name);
		this.setColor(color); // should change to get color which is not used yet
		this.setShapeID(shape); // initial value -> changed later
		coordinateList = new ArrayList<Point>();
		this.size=20;	// initial value -> changed later
		this.thickness=2; // initial value -> changed later
		this.opacity=1.0F; // initial value -> changed later
	}

	public String getLayerName() {
		return layerName;
	}
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	public ArrayList<Point> getCoordinateList() {
		if(this.gridProperties != null && this.gridProperties.isGridON()){
			ArrayList<Point> pointList = new ArrayList<Point>();
			Iterator<Point> pIterator = this.coordinateList.iterator();
			while(pIterator.hasNext()){
				Point p= pIterator.next();
				if(gridProperties.isPointInsideSelectedRectangle(p))
					pointList.add(p);
			}
			return pointList;

		}
		else
		return coordinateList;
	}

	public ArrayList<Point> getCoordinateListIgnoringGrid() {

		return coordinateList;
	}


	public void setCoordinateList(ArrayList<Point> coordinateList) {
		if(coordinateList != null && coordinateList.size()>0){
			this.coordinateList = makeCopyPoints(coordinateList);
			Collections.sort(this.coordinateList, new CoordinateComparator());
		}
	}

	public int getCounts(){
		if(this.gridProperties != null && this.gridProperties.isGridON()){
			double multiplyer = this.gridProperties.calculateSelectedRectangleAreaRelation();
			return (int)Math.round(((double)getCoordinateList().size()*multiplyer)); // round method returns closest long but don't worry: size of coordinatelist can't exceed max integer value
		}
		else
			return getCoordinateList().size();
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public int getShapeID() {
		return shapeID;
	}
	public void setShapeID(int shape) {
		this.shapeID = shape;
	}

	public String getStringColor(){
		return Integer.toString(color.getRGB());
	}

	public void setStringColor(String colorS){
		this.color = new Color(Integer.parseInt(colorS));
	}

	protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	public ArrayList<String> getStringPointList(){
		ArrayList<String> pointStringList = new ArrayList<String>();
		try {
			if(this.coordinateList != null && this.coordinateList.size() >0){
				Iterator<Point> iIterator = this.coordinateList.iterator();
				while(iIterator.hasNext()){
					Point p = (Point)iIterator.next();
					String ps= ""+p.x+","+p.y;
					pointStringList.add(ps);
				}

			}
			return pointStringList;
		} catch (Exception e) {
			LOGGER.severe("Error in creating StringPoint:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}

	}

	public void setStringPointList(ArrayList<String> stringPointList){
		int errors=0;
		try {

			if(stringPointList != null && stringPointList.size() >0){
				Iterator<String> iIterator = stringPointList.iterator();
				while(iIterator.hasNext()){

					String s = (String)iIterator.next();
					if(s != null && s.length()>2 && s.contains(",")){
						String[] sv = s.split(",");
						if(sv.length==2){
							try{
								Point p = new Point(Integer.parseInt(sv[0].trim()), Integer.parseInt(sv[1].trim()));
								this.coordinateList.add(p);
							}
							catch(Exception ex){
								// problem in converting from string to Point
								errors++;
							}
						}
					}

				}

				}
			LOGGER.warning(" MarkingLayer: Problems in converting String to Point. " + errors + " values not converted");

		} catch (Exception e) {
			LOGGER.severe("Error in creating PointList from StringList:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());

		}

	}

	public void addStringPoint(String stringPoint){

		try {
			LOGGER.fine("trying convert to Point:_"+stringPoint+"_");;
			if(stringPoint != null && stringPoint.length()>2 && stringPoint.contains(",")){
				String[] sv = stringPoint.split(",");
				if(sv.length==2){
					try{
						Point p = new Point(Integer.parseInt(sv[0].trim()), Integer.parseInt(sv[1].trim()));
						this.coordinateList.add(p);
					}
					catch(Exception ex){
						// problem in converting from string to Point
						LOGGER.warning(" MarkingLayer: Problems in converting String to Point. Value not converted");
					}
				}
			}



		} catch (Exception e) {
			LOGGER.severe("Error in creating Point from String:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());

		}

	}

	public boolean addSingleCoordinate(Point coordinate){
		// check is same coordinate already added to list
	/*	Iterator<Point> iIterator = this.coordinateList.iterator();
		while(iIterator.hasNext()){
			if(getManhattanDistance(iIterator.next(), coordinate) < SharedVariables.DISTANCE_TO_REMOVE){
				LOGGER.fine("Marking with same coordinate already in markings");
				return;
			}
		}
	*/	//not found same coordinate -> add
		if(this.gridProperties != null && this.gridProperties.isGridON()){

			if(!gridProperties.isPointInsideSelectedRectangle(coordinate) ){
				return false;
			}
		}
		this.coordinateList.add(coordinate);

		Collections.sort(this.coordinateList, new CoordinateComparator());
		return true;
	}


	public boolean removeSingleCoordinate(Point coordinate){

		try {
			// find the closest point of list
			Point candidatePoint=null; //=new Point(Integer.MAX_VALUE,Integer.MAX_VALUE);
			double candidateDistance=Integer.MAX_VALUE;
			Iterator<Point> iIterator = this.coordinateList.iterator();

			while(iIterator.hasNext()){
				Point presentPoint= iIterator.next();
				double presentDistance= getManhattanDistance(presentPoint, coordinate);
				if(presentDistance< candidateDistance){
					candidatePoint=presentPoint;
					candidateDistance=presentDistance;
				}
			}
			if(candidateDistance < SharedVariables.DISTANCE_TO_REMOVE){
				if(this.gridProperties != null && this.gridProperties.isGridON())
					if(!gridProperties.isPointInsideSelectedRectangle(candidatePoint) ){
						return false;
					}
					LOGGER.fine("Removing marking coordinate");
					this.coordinateList.remove(candidatePoint);
					// sort the coordinate list although the order shouldn't been changed
					Collections.sort(this.coordinateList, new CoordinateComparator());
					return true;


			}
			return false;

		} catch (Exception e) {
			LOGGER.severe("Error removing SingleCoordinate");
			return false;
		}

	}

	private double getDistance(Point p1, Point p2){

		return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
	}

	private double getManhattanDistance(Point p1, Point p2){

		int x_distance= p1.x-p2.x;
		if(x_distance <0)
			x_distance*=-1;

		int y_distance= p1.y-p2.y;
		if(y_distance < 0)
			y_distance*=-1;

			return x_distance+y_distance;

	}



/*
	private void sortCoordinates(){
		Collections.sort(this.coordinateList, new CoordinateComparator());
	}
*/
	public int getLayerID() {
		return layerID;
	}

	public void setLayerID(int layerID) {
		this.layerID = layerID;
	}

	public MarkingLayer makeCopy(){
		try {
			// create new object with same name as this one
			MarkingLayer copyMarkingLayer = new MarkingLayer(this.getLayerName());
			// set layerID
			copyMarkingLayer.setLayerID(this.getLayerID());
			// set color
			copyMarkingLayer.setColor(this.getColor());
			// set shape
			copyMarkingLayer.setShapeID(this.getShapeID());
			// set coordinatelist: go through list and create new Point of each

			if(this.coordinateList != null && this.coordinateList.size()>0){
			ArrayList<Point> copyPointList= makeCopyPoints(this.coordinateList);
			 copyMarkingLayer.setCoordinateList(copyPointList);
			}

			if(this.gridProperties != null)
				copyMarkingLayer.setGridProperties(this.getGridProperties());
			return copyMarkingLayer;
		} catch (Exception e) {
			LOGGER.severe("Error in creating copy of MarkingLayer:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}

	private ArrayList<Point> makeCopyPoints(ArrayList<Point> originalList){
		ArrayList<Point> copyPointList= new ArrayList<Point>();
		if(originalList != null && originalList.size()>0){
			Iterator<Point> iIterator = originalList.iterator();
			while(iIterator.hasNext()){
				Point oldPoint = (Point)iIterator.next();
				Point newPoint =new Point();
				newPoint.setLocation(oldPoint.getX(), oldPoint.getY());
				copyPointList.add(newPoint);
			}

			return copyPointList;
		}
		return null;
	}



	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getThickness() {
		return this.thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void clearCoordinateList(){
		this.coordinateList.clear();
	}

	public GridProperties getGridProperties() {
		return gridProperties;
	}

	public void setGridProperties(GridProperties gProperties) {
		if(gProperties != null){
			
			GridProperties gridProperty=new GridProperties(gProperties.getPresentImageDimension());
			
			gridProperty.setGridON(gProperties.isGridON());
			gridProperty.setRowLinesYList(gProperties.getRowLineYs());
			gridProperty.setColumnLinesXList(gProperties.getColumnLineXs());
		//	gridProperty.setSelectedRectangles(gProperties.getselectedRectangles());
		//	gridProperty.setUnselectedRectangles(gProperties.getUnselectedRectangles());
		//	gridProperty.setUnselectedRectangleNumbers(gProperties.getUnselectedGridCellNumbers());
			gridProperty.setPositionedRectangleList(gProperties.getPositionedRectangleList());
			gridProperty.setRandomProcent(gProperties.getRandomProcent());
	
			this.gridProperties = gridProperty;
		}
	}

	public boolean isGridON(){
		if(this.gridProperties != null && gridProperties.isGridON())
			return true;

		return false;
	}

	public boolean isDrawGridToImage() {
		return drawGridToImage;
	}

	public void setDrawGridToImage(boolean drawGridToImage) {
		this.drawGridToImage = drawGridToImage;
	}

/*	public String getXmlFilePath() {
		return xmlFilePath;
	}

	public void setXmlFilePath(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
	}
*/


}
