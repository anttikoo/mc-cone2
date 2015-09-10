package operators;

import java.awt.Point;
import java.util.Comparator;

public class WeightPointComparator implements Comparator<WeightPoint>{


	@Override
	public int compare(WeightPoint p1, WeightPoint p2) {
		// compare weights and produces descending order
		if(p1.getWeight() > p2.getWeight()){ // first Point has bigger point
				return -1;
		}
		// 
		else{
			if(p1.getWeight() < p2.getWeight()) // first point has smaller weight
				return 1;
			
		}
		return 0; // equal point
	}

	

}