package operators;

import java.awt.Point;

import math.geom2d.Point2D;



public class WeightPoint extends Point{
//	private Point point;
	private double weight;

	public WeightPoint(Point point){
		super(point);
		this.setWeight(1);
		setPoint(point);
	}

	public WeightPoint(WeightPoint wp){
		super(wp.getPoint());
		this.setWeight(wp.getWeight());
		//setPoint(point);
	}

	public void increaseWeight(double increase){
		this.setWeight(this.getWeight() + increase);
	}

	public Point getPoint() {
		return this;
	}

	public Point2D getPoint2D() {
		return new Point2D(this.x, this.y);
	}

	public void setPoint(Point p) {
	//	this.point = point;
		this.x=p.x;
		this.y=p.y;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double distance(WeightPoint p2){

		return this.getPoint().distance(p2.getPoint());
	}

	public void decreaseWeight(double d){
		this.weight=this.weight/d;
	}
}
