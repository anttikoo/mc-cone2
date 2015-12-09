package information;


import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;
import operators.CoordinateComparator;


/**
 * The Class MarkingLayer. 
 * Contains data: 
 * coordinates of markings, 
 * shape of markings
 * size of markings
 * color of markings
 * type (id) of markings
 * thickness of markings
 * opacity of markings
 * visibility of markings
 * selected MarkingLayer
 * GRID of MarkingLayer
 * GRID visibility.
 * Contains methods to manipulate that data.
 */
public class MarkingLayer{

	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private ArrayList<Point> coordinateList;
	private int layerID;
	private String layerName;
	private Color color;
	private int shapeID;
	private int size;
	private int thickness;
	private float opacity;
	private boolean isVisible=true;
	private boolean isSelected=false;
	private GridProperties gridProperties=null;
	private boolean drawGridToImage=false;


	/**
	 * Instantiates a new MarkingLayer.
	 *
	 * @param name the name of MarkingLayer
	 */
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

	/**
	 * Instantiates a new MarkingLayer.
	 *
	 * @param name the name of MarkingLayer
	 * @param shape the shape type of marking
	 * @param color the color of marking
	 */
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

	/**
	 * Adds the single coordinate.
	 *
	 * @param coordinate the coordinate
	 * @return true, if successful
	 */
	public boolean addSingleCoordinate(Point coordinate){
		
		if(this.gridProperties != null && this.gridProperties.isGridON()){

			if(!gridProperties.isPointInsideSelectedRectangle(coordinate) ){
				return false;
			}
		}
		this.coordinateList.add(coordinate);

		Collections.sort(this.coordinateList, new CoordinateComparator());
		return true;
	}
	
	/**
	 * Adds a coordinate given by string.
	 *
	 * @param stringPoint the string point
	 */
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
	
	/**
	 * Clears coordinate list.
	 */
	public void clearCoordinateList(){
		this.coordinateList.clear();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


	/**
	 * Returns the color.
	 *
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns the coordinate list. Checks is GRID active and returns only coordinates in selected GRID cells.
	 *
	 * @return the coordinate list
	 */
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
	
	/**
	 * Returns the coordinate list independent is GRID ON.
	 *
	 * @return the coordinate list independent is GRID ON.
	 */
	public ArrayList<Point> getCoordinateListIgnoringGrid() {

		return coordinateList;
	}
	
	/**
	 * Counts and returns the amount of coordinates. If GRID is active -> counts proportional number of countings.
	 *
	 * @return the Integer counts
	 */
	public int getCounts(){
		if(this.gridProperties != null && this.gridProperties.isGridON()){
			double multiplyer = this.gridProperties.calculateSelectedRectangleAreaRelation();
			return (int)Math.round(((double)getCoordinateList().size()*multiplyer)); // round method returns closest long but don't worry: size of coordinatelist can't exceed max integer value
		}
		else
			return getCoordinateList().size();
	}
	
	/**
	 * Returns the grid properties.
	 *
	 * @return the grid properties
	 */
	public GridProperties getGridProperties() {
		return gridProperties;
	}


	/**
	 * Returns the ID of MarkingLayer.
	 *
	 * @return the ID of MarkingLayer
	 */
	public int getLayerID() {
		return layerID;
	}

	/**
	 * Returns the name of MarkingLayer.
	 *
	 * @return the name of MarkingLayer
	 */
	public String getLayerName() {
		return layerName;
	}

	/**
	 * Returns the Manhattan distance between two point. Faster than euclidean distanse.
	 *
	 * @param p1 the Point 1
	 * @param p2 the point 2
	 * @return the mManhattan distance
	 */
	private double getManhattanDistance(Point p1, Point p2){

		int x_distance= p1.x-p2.x;
		if(x_distance <0)
			x_distance*=-1;

		int y_distance= p1.y-p2.y;
		if(y_distance < 0)
			y_distance*=-1;

			return x_distance+y_distance;

	}

	/**
	 * Returns the opacity.
	 *
	 * @return the opacity
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Returns the shape id.
	 *
	 * @return the shape id
	 */
	public int getShapeID() {
		return shapeID;
	}

	/**
	 * Returns the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the string color.
	 *
	 * @return the string color
	 */
	public String getStringColor(){
		return Integer.toString(color.getRGB());
	}


	/**
	 * Returns the list of Points as String.
	 *
	 * @return the string point list
	 */
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

	/**
	 * Returns the thickness.
	 *
	 * @return the thickness
	 */
	public int getThickness() {
		return this.thickness;
	}

	/**
	 * Checks if is draw grid to image.
	 *
	 * @return true, if is draw grid to image
	 */
	public boolean isDrawGridToImage() {
		return drawGridToImage;
	}



/**
 * Checks if is grid on (active).
 *
 * @return true, if is grid on (active)
 */
public boolean isGridON(){
		if(this.gridProperties != null && gridProperties.isGridON())
			return true;

		return false;
	}

	/**
	 * Checks if is this MarkingLayer selected.
	 *
	 * @return true, if is selected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * Checks if is this MarkingLayer visible.
	 *
	 * @return true, if is visible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Creates and returns a copy of this MarkingLayer.
	 *
	 * @return the marking layer
	 */
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



	/**
	 * Make and returns a copy of list of coordinates.
	 *
	 * @param originalList the original list
	 * @return the array list
	 */
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

	/**
	 * Removes the single coordinate from list of coordinates.
	 *
	 * @param coordinate the coordinate to be removed
	 * @return true, if successful
	 */
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

	/**
	 * Sets the marking color.
	 *
	 * @param color the new color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Sets the coordinate list.
	 *
	 * @param coordinateList the new coordinate list
	 */
	public void setCoordinateList(ArrayList<Point> coordinateList) {
		if(coordinateList != null && coordinateList.size()>0){
			this.coordinateList = makeCopyPoints(coordinateList);
			Collections.sort(this.coordinateList, new CoordinateComparator());
		}
	}

	/**
	 * Sets the draw grid to image.
	 *
	 * @param drawGridToImage the new draw grid to image
	 */
	public void setDrawGridToImage(boolean drawGridToImage) {
		this.drawGridToImage = drawGridToImage;
	}

	/**
	 * Sets the grid properties.
	 *
	 * @param gProperties the new grid properties
	 */
	public void setGridProperties(GridProperties gProperties) {
		if(gProperties != null){
			
			GridProperties gridProperty=new GridProperties(gProperties.getPresentImageDimension());
			
			gridProperty.setGridON(gProperties.isGridON());
			gridProperty.setRowLinesYList(gProperties.getRowLineYs());
			gridProperty.setColumnLinesXList(gProperties.getColumnLineXs());
			gridProperty.setPositionedRectangleList(gProperties.getPositionedRectangleList());
			gridProperty.setRandomProcent(gProperties.getRandomProcent());
	
			this.gridProperties = gridProperty;
		}
	}

	/**
	 * Sets the ID of this MarkingLayer.
	 *
	 * @param layerID the new layer id
	 */
	public void setLayerID(int layerID) {
		this.layerID = layerID;
	}

	/**
	 * Sets the name of this MarkingLayer.
	 *
	 * @param layerName the name of this MarkingLayer
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	/**
	 * Sets the opacity.
	 *
	 * @param opacity the new opacity
	 */
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	/**
	 * Sets the MarkingLayer as selected/unselected.
	 *
	 * @param selected the new state is MarkingLayer selected
	 */
	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}

	/**
	 * Sets the shape id.
	 *
	 * @param shape the new shape id
	 */
	public void setShapeID(int shape) {
		this.shapeID = shape;
	}

	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Sets the string color.
	 *
	 * @param colorS the new string color
	 */
	public void setStringColor(String colorS){
		this.color = new Color(Integer.parseInt(colorS));
	}

	/**
	 * Sets the string point list.
	 *
	 * @param stringPointList the new string point list
	 */
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

	/**
	 * Sets the thickness.
	 *
	 * @param thickness the new thickness
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	/**
	 * Sets the visibility of markings.
	 * @param isVisible the new visible
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}



}
