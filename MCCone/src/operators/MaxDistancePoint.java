package operators;

import java.awt.Point;

public class MaxDistancePoint extends WeightPoint{
	private double distance=-1;
	public MaxDistancePoint(WeightPoint wpoint, double distance) {
		super(wpoint);
		this.setDistance(distance);
		// TODO Auto-generated constructor stub
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}

}
