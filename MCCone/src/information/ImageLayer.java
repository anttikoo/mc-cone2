package information;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

public class ImageLayer {
	private int layerID;
	private String imageFilePath;
	private String markingsFilePath;
	private String exportImagePath;
	private ArrayList<MarkingLayer> markingLayerList;
	private boolean isSelected=false;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	public ImageLayer(String imagePath){
		this.setLayerID(-1); // initialize the layerID to negative
		this.setImageFilePath(imagePath);
		this.markingLayerList = new ArrayList<MarkingLayer>();
	}

	public String getImageFilePath() {
		return imageFilePath;
	}



	/**
	 * Method returns a last name of path sequence if file exists.
	 * @return the file name without full path of file.
	 */
	public String getImageFileName(String path){
		try {
			File file = new File(path);
			if(file.exists()){
			//	LOGGER.fine("givin filename: " +file.getName());
				return file.getName();
			}
			return "";
		} catch (Exception e) {
			LOGGER.severe("Error in getting Imagefile name from ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return "";
		}
	}

	public String getImageFileName(){
		return getImageFileName(this.imageFilePath);
	}

	public boolean hasSameImageName(String iPath){
		if(iPath != null && iPath.length() >0 )
			if(getImageFileName().equals(getImageFileName(iPath)))
				return true;
		return false;
	}

	public String getImageFileNameWithoutExtension(){
		try {
			File file = new File(this.imageFilePath);
			if(file.exists()){
			//	LOGGER.fine("givin filename: " +file.getName());
				return removeExtension(file.getName());
			}
			return "";
		} catch (Exception e) {
			LOGGER.severe("Error in getting Imagefile name from ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return "";
		}
	}

	  private String removeExtension (String str) {
	        try {
				// Handle null case specially.
				if (str == null) return null;

				// Get position of last '.'.
				int pos = str.lastIndexOf(".");

				// If there wasn't any '.' just return the string as is.
				if (pos == -1) return str;

				// Otherwise return the string, up to the dot.
				return str.substring(0, pos);
			} catch (Exception e) {
				LOGGER.severe("Error in removingExtension " +e.getClass().toString() + " :" +e.getMessage());
				return str;
			}
	    }
	public String getImageFullFilePathWithoutExtension(){
		try {
			File file = new File(this.imageFilePath);
			if(file.exists()){
			//	LOGGER.fine("givin filename: " +file.getName());
				return removeExtension(file.getAbsolutePath());
			}
			return "";
		} catch (Exception e) {
			LOGGER.severe("Error in getting Imagefile name from ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return "";
		}
	}

	public String getFolderOfImage(){
		try {
			File file = new File(this.imageFilePath);
			if(file.exists()){
			//	LOGGER.fine("givin filename: " +file.getName());
				return file.getParent();
			}
			return "";
		} catch (Exception e) {
			LOGGER.severe("Error in getting Imagefile name from ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return "";
		}
	}

	public void setImageFilePath(String imageFilePath) {
		this.imageFilePath = imageFilePath;
	}

	public void addMarkingLayer(MarkingLayer ml){

		try {

			// should create brand new Markinglayer and copy all values to it from ml because may be deleted elsewhere
			MarkingLayer mark = new MarkingLayer(ml.getLayerName());

			mark.setColor(ml.getColor());
			mark.setCoordinateList(ml.getCoordinateList());
			mark.setShapeID(ml.getShapeID());
			if(ml.getGridProperties() != null)
				mark.setGridProperties(ml.getGridProperties());
			if(ml.getLayerID()>0)
				mark.setLayerID(ml.getLayerID());

			this.markingLayerList.add(mark);
			LOGGER.fine("create marking 4 " + ml.getLayerID() + " " + ml.getLayerName() + " size" + this.markingLayerList.size());

		} catch (Exception e) {
			LOGGER.severe("Error in adding new MarkingLayer " +e.getClass().toString() + " :" +e.getMessage());

		}
	}

	public ArrayList<MarkingLayer> getMarkingLayers(){
		return this.markingLayerList;
	}

	public void setMarkingLayerList(ArrayList<MarkingLayer> markingLayerList) {
		this.markingLayerList = markingLayerList;
	}

	public void removeMarkingLayer(MarkingLayer markinglayer){
		if(markinglayer !=null && this.markingLayerList != null && this.markingLayerList.size()>0){
			Iterator<MarkingLayer> iIterator = this.markingLayerList.iterator();
			while(iIterator.hasNext()){
				MarkingLayer ml = (MarkingLayer)iIterator.next();
				if(ml.getLayerName().equals(markinglayer.getLayerName())){ // MarkingLayer name
					iIterator.remove();
				}

			}
		}
	}

	public void removeAllMarkingLayers(){
		this.markingLayerList.clear();
	}

	public String getMarkingsFilePath() {
		return markingsFilePath;
	}

	public void setMarkingsFilePath(String markingsFilePath) {
		this.markingsFilePath = markingsFilePath;
	}

	public int getLayerID() {
		return layerID;
	}

	public void setLayerID(int layerID) {
		this.layerID = layerID;
	}



	public ImageLayer makeCopy(){
		try {
			// create new ImageLayer
			ImageLayer copyImageLayer = new ImageLayer(this.getImageFilePath());
			// set filePath of Markings file
			if(markingsFilePath != null && markingsFilePath.length()>0)
				copyImageLayer.setMarkingsFilePath(this.getMarkingsFilePath());
			// create and set copy of MarkingLayer -list
			if(this.markingLayerList != null && this.markingLayerList.size()>0){
				Iterator<MarkingLayer> iIterator = this.markingLayerList.iterator();
				while(iIterator.hasNext()){
					MarkingLayer ml = (MarkingLayer)iIterator.next();
					if(ml != null){
						MarkingLayer copyMarkingLayer = ml.makeCopy();
						if(copyMarkingLayer != null)
							copyImageLayer.addMarkingLayer(copyMarkingLayer);
					}

				}
			}
			return copyImageLayer;
		} catch (Exception e) {
			LOGGER.severe("Error in creating copy of ImageLayer: " +e.getClass().toString() + " :" +e.getMessage());
			return null;

		}
	}

	public boolean isMarkingLayerInList(MarkingLayer checkMarkingLayer){
		try {
			if(this.markingLayerList != null && this.markingLayerList.size()>0){
				Iterator<MarkingLayer> iIterator = this.markingLayerList.iterator();
				while(iIterator.hasNext()){
					MarkingLayer ml = (MarkingLayer)iIterator.next();
					if(ml != null && ml.getLayerName().equalsIgnoreCase(checkMarkingLayer.getLayerName())){
						return true;

					}

				}
			}
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in searching MarkingLayer from ImageLayer: " +e.getClass().toString() + " :" +e.getMessage());
			return true; // to be on the safe side  return true when error happens
		}
	}

	public MarkingLayer getMarkingLayer(int mLayerID){
		try {
			// go through imageLayerList and return if layerIDs match
			if (this.markingLayerList != null && this.markingLayerList.size() > 0) {
				Iterator<MarkingLayer> iterator = this.markingLayerList.iterator();
				while (iterator.hasNext()) {
					MarkingLayer ma= (MarkingLayer) iterator.next();
					if (ma.getLayerID() == mLayerID){
						return ma;
					}

				}
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("Error in finding MarkingLayer " +e.getMessage());
			return null;
		}
	}



	/**
	 * Return true if found MaerkingLayer ID from markingLayerList of this ImageLayer.
	 * @param mLayerID ID of MarkingLayer
	 * @return
	 */
	public boolean hasMarkingLayer(int mLayerID){
		if(getMarkingLayer(mLayerID) != null)
			return true;
		return false;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public ArrayList<Integer> getAllMarkingLayerIDs(){
		ArrayList<Integer> mLayerIDlist= new ArrayList<Integer>();

		Iterator<MarkingLayer> mIterator = getMarkingLayers().iterator();
		while(mIterator.hasNext()){
			mLayerIDlist.add(((MarkingLayer)mIterator.next()).getLayerID());
		}

		return mLayerIDlist;




	}

	public String getExportImagePath() {
		return exportImagePath;
	}

	public void setExportImagePath(String exportImagePath) {
		this.exportImagePath = exportImagePath;
	}

	public boolean hasGridOn(){
		Iterator<MarkingLayer> mIterator = getMarkingLayers().iterator();
		while(mIterator.hasNext()){
			MarkingLayer layer=mIterator.next();
			if(layer.getGridProperties()!=null && layer.getGridProperties().isGridON())
				return true;
		}

		return false;
	}



}
