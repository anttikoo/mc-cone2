package operators;

import java.awt.Point;
import java.util.Comparator;

public class CoordinateComparator implements Comparator<Point>{

	@Override
	public int compare(Point p1, Point p2) {
		// x-position is same -> compare with y position
		if(p1.x == p2.x){
			if(p1.y < p2.y) // first point has smaller y
				return -1;
		}
		// 
		else{
			if(p1.x < p2.x) // first point has smaller x 
				return -1;
			
		}
		return 1;
	}

	

}
