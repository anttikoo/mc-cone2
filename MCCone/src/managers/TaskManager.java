package managers;
import gui.GUI;
import gui.ProgressBallsDialog;
import gui.ShadyMessageDialog;
import gui.grid.SingleGridSize;
import information.GridProperties;
import information.ID;
import information.ImageLayer;
import information.InformationCenter;
import information.MarkingLayer;
import information.PositionedImage;
import information.ScreenCoordinatesOfMarkingLayer;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.imgscalr.Scalr;

import operators.PreCounterThread;
import operators.XMLreadManager;


/**
 * Mediates all commands and information between GUI and working classes.
 * @author Antti Kurronen
 */
public class TaskManager {
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private XMLreadManager xmlManager;
	private InformationCenter informationCenter;
	private LayerVisualManager layerVisualManager;
	private GUI gui; // object of graphical interface
	private PreCountThreadManager preCountThreadManager = null;
	private ProgressBallsDialog pbd;


	/**
	 * Class constructor
	 */
	public TaskManager(GUI gui) throws Exception{
		this.gui = gui;

		this.xmlManager = new XMLreadManager();
		this.informationCenter = new InformationCenter(this);
		this.layerVisualManager = new LayerVisualManager(this);


	}

	public double getShapeSizeMultiplier(){

		return this.layerVisualManager.getSizeMultiplier();
	}

	public String getPresentFolder(){
		return informationCenter.getPresentFolder();
	}

	public void setPresentFolder(String folder){
		this.informationCenter.setPresentFolder(folder);
	}

	public ImageLayer getMarkingsOfXML(File xmlFile, ImageLayer imageLayer){
		return xmlManager.getMarkingsOfXML(xmlFile, imageLayer);
	}
	public ArrayList<ImageLayer> getMarkingsOfXML(File xmlFile, ArrayList<ImageLayer> imageLayerList){
		return xmlManager.getMarkingsOfXML(xmlFile, imageLayerList);

	}

	public void setMarkingLayerName(int iLayerID, int mLayerID, String markingName){
		informationCenter.setMarkingLayerName(iLayerID, mLayerID, markingName);
		
	}

	public MarkingLayer getMarkingLayer(int iLayerID, int mLayerID){
		return this.informationCenter.getMarkingLayer(iLayerID, mLayerID);
	}

	public MarkingLayer getMarkingLayer(int mLayerID){
		return this.informationCenter.getMarkingLayer(mLayerID);
	}

	public void addImageLayers(ArrayList<ImageLayer> layers){
		this.informationCenter.addImageLayers(layers);
		setPresentImageDimensionFromImageLayerList(layers);
		this.informationCenter.updatePresentImageDimensionOfMarkingPanels();



	}
	public boolean isMadeChanges(){
		return this.informationCenter.isMadeChanges();
	}
	
	public void setMadeChange(){
		this.informationCenter.setMadeChanges(true);
	}

	private void setPresentImageDimensionFromImageLayerList(ArrayList<ImageLayer> layers){
		boolean added=false;
		if(layers != null && layers.size()>0){
			Iterator<ImageLayer> iIterator=layers.iterator();
			while(!added && iIterator.hasNext()){
				ImageLayer layer=iIterator.next();
				added=setPresentImageDimensionFromSingleImageLayer(layer);
			}
		}
	}

	private boolean setPresentImageDimensionFromSingleImageLayer(ImageLayer layer){
		if(layer != null && layer.getImageFilePath() != null && layer.getImageFilePath().length()>0){
			File imagefile = new File(layer.getImageFilePath());
			if(imagefile != null){
				try {
					this.informationCenter.setPresentImageDimension(this.layerVisualManager.getImageDimension(imagefile));
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}

	public void refreshLayersAndGUI(){
		this.gui.refreshLayersAndGUI();
	}

	public Dimension getPresentImageDimension() {
		return this.informationCenter.getPresentImageDimension();
	}

	public ArrayList<SingleGridSize> getSingleGridSizeList() {
		return this.informationCenter.getSingleGridSizeList();
	}

	public boolean setSelectedImageLayerIfNotExist(){
		return this.informationCenter.setSelectedImageLayerIfNotExist();
	}

	public void addImageLayer(ImageLayer layer){
		informationCenter.addImageLayer(layer);
		setPresentImageDimensionFromSingleImageLayer(layer);
	}

	public void setImageLayerList(ArrayList<ImageLayer> layers){
		this.informationCenter.setImageLayerList(layers);
		setPresentImageDimensionFromImageLayerList(layers);
		this.informationCenter.updatePresentImageDimensionOfMarkingPanels();

	}

	public ArrayList<ImageLayer> getImageLayerList() {
		return informationCenter.getImageLayerList();
	}

	/**
	 * Returns ImageLayer by given ID.
	 * @param iLayerID ID of ImageLayer
	 * @return
	 */
	public ImageLayer getImageLayerByID(int iLayerID) {
		return informationCenter.getImageLayerByID(iLayerID);
	}
	
	/**
	 * returns ImageLayer, which has given MarkingLayer (MarkingLayer ID is given).
	 * @param markingLayerID ID of MarkingLayer.
	 * @return
	 */
	public ImageLayer getImageLayerByMarkingLayerID(int markingLayerID) {
		return informationCenter.getImageLayerByMarkingLayerID(markingLayerID);
	}

	public void removeImageLayer(int layerID){
		informationCenter.removeImageLayer(layerID);
	}

	public void removeMarkingLayer(int imageLayerID, int markingLayerID){
		informationCenter.removeMarkingLayer(imageLayerID, markingLayerID);
	}

	public boolean imageNameAlreadyUsed(String imageName){
		return informationCenter.imageNameAlreadyUsed(imageName);
	}

	public MarkingLayer createNewMarkingLayer(int imageLayerID){
		return informationCenter.createNewMarkingLayer(imageLayerID);

	}

	public ArrayList<MarkingLayer> getAllMarkingLayers(){
		return informationCenter.getAllMarkingLayers();
	}

	public ArrayList<MarkingLayer> getVisibleMarkingLayers(){
		return informationCenter.getVisibleMarkingLayerList();
	}

	public String getSelectedImagePath(){
		ImageLayer layer=informationCenter.getSelectedImageLayer();
		if(layer != null)
			return layer.getImageFilePath();
		else
			return null;
	}

	/**
	 * Changes selectedImageLayer in InformationCenter and selected BufferedImage object of LayerVisualManager.
	 * @param iLayerID Identifier (int) of ImageLayer that is set as selected
	 */
	public void changeSelectedImageLayer(int iLayerID) throws Exception{

		this.informationCenter.updateSelectedImageLayer(iLayerID);
		setSelectedImage(this.informationCenter.getSelectedImageLayer().getImageFilePath());
	}

	/**
	 * Sets the selected image to layerVisualManager.
	 */
	public void updateImageOfSelectedImageLayer() throws Exception{
		ImageLayer selectedLayer = this.informationCenter.getSelectedImageLayer();
		if(selectedLayer != null)
			setSelectedImage(selectedLayer.getImageFilePath());
		else
			setSelectedImage(null);
	}


	public void setImagePanelDimension(Dimension vpd){
		this.layerVisualManager.setImagePanelDimension(vpd);
	}

	public PositionedImage getRefreshedImage(int processingID){
		//return this.layerVisualManager.getScaledImageFromImagePath(this.getSelectedImagePath());
		return this.layerVisualManager.getRefreshedImage(processingID);

	}

	public PositionedImage dragLayers(Point movement, int processingID){
		return this.layerVisualManager.dragLayers(movement, processingID);
	}


	public void setSelectedImage(String imagePath) throws Exception{
		this.layerVisualManager.setSelectedBufferedImage(imagePath);
	}

	public MarkingLayer setMarkingLayerSelected(int mLayerID){
		MarkingLayer sMarkingLayer=this.informationCenter.getMarkingLayer(mLayerID);
		if(sMarkingLayer != null){
			this.informationCenter.setSelectedMarkingLayer(sMarkingLayer);
			return sMarkingLayer;
		}
		else
			return null;
	}

	public int getSelectedImageLayerAtUpOrDown(int upOrDownID){
		if(upOrDownID == ID.MOVE_DOWN){
			return this.informationCenter.getSelectedImageLayerOneDown();
		}
		else{
			return this.informationCenter.getSelectedImageLayerOneUp();
		}
	}

	public int getSelectedMarkingLayerAtUpOrDown(int upOrDownID){
		if(upOrDownID == ID.MOVE_DOWN){
			return this.informationCenter.getSelectedMarkingLayerOneDown();
		}
		else{
			return this.informationCenter.getSelectedMarkingLayerOneUp();
		}
	}


	public boolean addSingleMarking(Point pointAtScreen){
		return this.layerVisualManager.addSingleMarkingCoordinate(pointAtScreen, this.informationCenter.getSelectedMarkingLayer());
	}

	public void setGridSelectedRectangle(){
		if(getSelectedMarkingLayer() != null && getSelectedMarkingLayer().getGridProperties() != null &&
				getSelectedMarkingLayer().getGridProperties().isGridON()){

		}
	}

	public boolean removeSingleMarking(Point pointAtScreen){
		return this.layerVisualManager.removeSingleMarkingCoordinate(pointAtScreen, this.informationCenter.getSelectedMarkingLayer());
	}

	public void setMarkingLayerUnSelected(int iLayerID, int mLayerID){
	//	this.informationCenter.removeMarkingLayerFromVisibleList(iLayerID, mLayerID);
	}

	public void setMarkingLayerVisibility(int mLayerID, boolean visible){
		this.informationCenter.setMarkingLayerVisibility(mLayerID, visible);
	}


	public PositionedImage getZoomedImage(Point midP, double zoomValue, int processingID ){
		return this.layerVisualManager.getZoomedImage(midP, zoomValue, processingID);
	}

	public MarkingLayer getSelectedMarkingLayer(){
		return this.informationCenter.getSelectedMarkingLayer();
	}

	public ImageLayer getSelectedImageLayer(){
		return this.informationCenter.getSelectedImageLayer();
	}

	public boolean isSelectedImageLayer(int iLayerID){
		return this.informationCenter.isSelectedImageLayer(iLayerID);
	}

	public boolean isSelectedMarkingLayer(int mLayerID){
		return this.informationCenter.isSelectedMarkingLayer(mLayerID);
	}

	public ArrayList<Point> getScreenCoordinatesOfSelectedMarkingLayer(){
		return this.layerVisualManager.getScreenPointsOfMarkingLayer(this.informationCenter.getSelectedMarkingLayer());
	}

	public ArrayList<ScreenCoordinatesOfMarkingLayer> getScreenCoordinatesOfVisibleMarkingLayers(){
		return this.layerVisualManager.getScreenPointsOfMarkingLayers(this.informationCenter.getVisibleMarkingLayerList());
	}

	public boolean isSelectedMarkingLayerVisible(){
		if(this.informationCenter.getSelectedMarkingLayer() == null)
			return false;
		return this.informationCenter.getSelectedMarkingLayer().isVisible();
	}

	public boolean isAllowedImageDimension(File file) throws Exception{
		Dimension dim=this.layerVisualManager.getImageDimension(file);
		if( dim != null)
		return this.informationCenter.isAllowedImageDimension(dim);
		else
			return false;
	}

	public Dimension getImageDimension(File file) throws Exception{
		return this.layerVisualManager.getImageDimension(file);
	}



	private BufferedImage getSubImage(Point middlepoint, int size){
		return this.layerVisualManager.getSubImage(middlepoint, size);
	}

	public void precountCells(Point middlePoint, int size, ProgressBallsDialog pbd){
		BufferedImage subImage = getSubImage(middlePoint, size);
		Point imagePoint=this.layerVisualManager.convertScreenPointToImagePoint(middlePoint);


		if(subImage != null && this.informationCenter.getSelectedImageLayer() != null && this.informationCenter.getSelectedMarkingLayer() != null){
			//LOGGER.fine("subimage size" +subImage.getWidth() +" height" +subImage.getHeight());
			int iLayerID= this.informationCenter.getSelectedImageLayer().getLayerID();
			int mLayerID= this.informationCenter.getSelectedMarkingLayer().getLayerID();

			if(preCountThreadManager != null){

				// set new ProgressBallDialog
				this.preCountThreadManager.setProgressBallDialog(pbd);

				// add new subimage (from cell)
				this.preCountThreadManager.setNewSubImage(subImage);


				this.preCountThreadManager.initPreCounterThread();

				// start counting
				this.preCountThreadManager.startCounting();


			}
			else{
				BufferedImage originalImage = this.layerVisualManager.getOriginalImage();
				if(originalImage != null){
					try{
					// create new PreCountThreadManager
					PreCounterThread preCountThread =new PreCounterThread(subImage, originalImage, this);
					this.preCountThreadManager=new PreCountThreadManager(preCountThread,pbd, iLayerID, mLayerID);
					// start the counting
					this.preCountThreadManager.startCounting();

					}catch(Exception e){

					}
				}

			}

		}
		else{
			LOGGER.warning("Couldn't create subimage from selection");
		}

	}

	public void cleanPrecoutingManager(){
		this.preCountThreadManager = null;
	}

	public  void showMessageToUser(String title, String message){
		ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), title, message, ID.OK, this.gui);
		dialog.showDialog();
		dialog=null;
	}

	public PreCountThreadManager getPrecountThreadManager(){
		return this.preCountThreadManager;
	}

	public void setSelectedMarkingLayerCoordinates(ArrayList<Point> coordinates){
		this.informationCenter.getSelectedMarkingLayer().setCoordinateList(coordinates);
	}

	public void updateSelectedMarkingPanelAndImageLayerInfos(){
		gui.updateCoordinatesOfSelectedMarkingPanel(); // updates selectedMarkingPanel and ImageLayerInfos

	}

	public void testADDCoordinatesToSecond(ArrayList<Point> coordinates){
		this.informationCenter.getAllMarkingLayers().get(1).setCoordinateList(coordinates);
	}

	public BufferedImage createImageFile(File imageFile) throws Exception{
		if(imageFile != null)
			return this.layerVisualManager.readImageFile(imageFile);
		else
			return null;
	}

	public GridProperties getConvertedGridProperties(){
		MarkingLayer ml =getSelectedMarkingLayer();
		if(ml != null)
			return this.layerVisualManager.convertLayerGridPropertiesToPanel(ml.getGridProperties());
		else
			return null;

	}

	public void changeGridCellSelection(Point p){
		if(this.informationCenter.getSelectedMarkingLayer() != null &&
				this.informationCenter.getSelectedMarkingLayer().getGridProperties() != null){
			this.layerVisualManager.changeSelectedRectangleOfGridProperty(this.informationCenter.getSelectedMarkingLayer().getGridProperties(), p);
		}
	}

	public Scalr.Mode getScalingMode(int image_width, int image_height, Dimension iPanelDimension){
		return this.layerVisualManager.getScalingMode(image_width, image_height, iPanelDimension);
	}



//	public BufferedImage getSelectedBufferedImage(Dimension visualPanelDimension){
		//return layerManager.
//	}

}
