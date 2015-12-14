package operators;

/**
 * The Class MaxDistancePoint.
 */
public class MaxDistancePoint extends WeightPoint{
	
	/** The distance. */
	private double distance=-1;
	
	/**
	 * Instantiates a new max distance point.
	 *
	 * @param wpoint the WieghtPoint
	 * @param distance the distance
	 */
	public MaxDistancePoint(WeightPoint wpoint, double distance) {
		super(wpoint);
		this.setDistance(distance);
	}
	
	/**
	 * Returns the distance.
	 *
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}
	
	/**
	 * Sets the distance.
	 *
	 * @param distance the new distance
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

}
