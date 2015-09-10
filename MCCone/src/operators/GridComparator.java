package operators;

import gui.saving.ImageSet.SingleDrawImagePanel;

import java.awt.Point;
import java.util.Comparator;

public class GridComparator implements Comparator<SingleDrawImagePanel>{



	@Override
	public int compare(SingleDrawImagePanel o1, SingleDrawImagePanel o2) {
		if(o1.getGridPosition() != null && o2.getGridPosition() != null){
			if(o1.getGridPosition()[0]==o2.getGridPosition()[0]){
				if(o1.getGridPosition()[1] < o2.getGridPosition()[1]){
					return -1;
				}
				else
					return 1;
			}
			else if(o1.getGridPosition()[0] < o2.getGridPosition()[0]){
				return -1;
			}
			else return 1;


		}
		else
			return 0;
	}



}
