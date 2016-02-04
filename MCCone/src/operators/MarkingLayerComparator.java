package operators;


import java.util.Comparator;

import information.MarkingLayer;

public class MarkingLayerComparator implements Comparator<MarkingLayer>{

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	
	public int compare(MarkingLayer ml1, MarkingLayer ml2) {
		if(ml1.getOrder() >0 && ml2.getOrder()>0){
			//  is same ->
			if(ml1.getOrder() == ml2.getOrder()){
				return 0;
			}
			// 
			else{ // first order smaller
				if(ml1.getOrder() < ml2.getOrder()) // first point has smaller x 
					return -1;		
			}
			return 1; // first bigger
		}
		else return 0;
	}

	

}
