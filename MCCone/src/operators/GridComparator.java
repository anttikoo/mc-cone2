package operators;

import gui.saving.ImageSet.SingleDrawImagePanel;
import java.util.Comparator;

/**
 * Class GridComparator compares two SingleDrawImagePanel by row and column number and which has smaller is returned to be smaller (before).
 * @author Antti Kurronen
 *
 */
public class GridComparator implements Comparator<SingleDrawImagePanel>{



	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
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
