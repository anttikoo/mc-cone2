package information;

import java.awt.Point;
import java.util.ArrayList;

public class ScreenCoordinatesOfMarkingLayer {
	private ArrayList<Point> coordinates;
	private int id;
	
	public ScreenCoordinatesOfMarkingLayer(ArrayList<Point> points, int id){
		this.coordinates=points;
		this.id=id;
	}

	public ArrayList<Point> getCoordinates() {
		return coordinates;
	}
/*
	public void setCoordinates(ArrayList<Point> coordinates) {
		this.coordinates = coordinates;
	}
*/
	public int getId() {
		return id;
	}
	
	public void addCoordinate(Point point){
		if(point != null)
			this.coordinates.add(point);
	}
/*
	public void setId(int id) {
		this.id = id;
	}
*/
}
