package operators;

import java.awt.Point;
import math.geom2d.Point2D;


/**
 * The Class WeightPoint. Contains a weight value for point.
 */
public class WeightPoint extends Point{
	
	/** The weight. */
	private double weight;

	/**
	 * Instantiates a new WeightPoint by given Point.
	 *
	 * @param point the Point
	 */
	public WeightPoint(Point point){
		super(point);
		this.setWeight(1);
		setPoint(point);
	}

	/**
	 * Instantiates a new WeightPoint by given WeightPoint
	 *
	 * @param wp the WeightPoint
	 */
	public WeightPoint(WeightPoint wp){
		super(wp.getPoint());
		this.setWeight(wp.getWeight());
		//setPoint(point);
	}

	/**
	 * Decreases weight by dividing with given double value.
	 *
	 * @param d the double weight
	 */
	public void decreaseWeight(double d){
		this.weight=this.weight/d;
	}

	/**
	 * Distance.
	 *
	 * @param p2 the p2
	 * @return the double
	 */
	public double distance(WeightPoint p2){

		return this.getPoint().distance(p2.getPoint());
	}

	/**
	 * Returns the point.
	 *
	 * @return the Point
	 */
	public Point getPoint() {
		return this;
	}

	/**
	 * Returns the point2 d.
	 *
	 * @return the point2 d
	 */
	public Point2D getPoint2D() {
		return new Point2D(this.x, this.y);
	}

	/**
	 * Returns the weight.
	 *
	 * @return the double weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Increases weight by summing with given double value.
	 *
	 * @param increase the increase
	 */
	public void increaseWeight(double increase){
		this.setWeight(this.getWeight() + increase);
	}

	/**
	 * Sets the point.
	 *
	 * @param p the new point
	 */
	public void setPoint(Point p) {
		this.x=p.x;
		this.y=p.y;
	}

	/**
	 * Sets the weight.
	 *
	 * @param weight the new weight
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
