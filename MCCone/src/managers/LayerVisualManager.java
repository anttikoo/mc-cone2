package managers;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import org.imgscalr.Scalr;
import com.sun.media.jai.util.ImageUtil;
import gui.Color_schema;
import gui.file.Utils;
import information.GridProperties;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import information.PositionedImage;
import information.PositionedRectangle;
import information.ScreenCoordinatesOfMarkingLayer;
import information.VisualEvent;

/**
 * Contains methods for organizing and setting up ImageLayers and MarkingLayers
 * @author Antti Kurronen
 *
 */
public class LayerVisualManager {
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private int removeDistance=5;

	private TaskManager taskManager;
	//private BlockingQueue queue = new ArrayBlockingQueue(2014);
	//private VisualEventProducer veProducer;
	//private VisualEventExecuter veExecuter;
	private Dimension imagePanelDimension; // the dimension of actual size of ImagePanel at screen (the topleft position is always 0,0)

	//the relative size of ImagePanel referred to actual size of originalImage. Is the dimension of present view of original image
	// this dimension and relativeImageCoordinate are changed when wanted to get right part of original image shown.
	private Rectangle relativeViewRectangle;

	private BufferedImage originalImage;
	//private Point2D relativeImagePosition; // the topleft corner of image in relativeImagePanelDimension


	public LayerVisualManager(TaskManager taskManager){

		this.taskManager=taskManager;

	//	this.relativeImagePosition=new Point(0, 0); // in the start the image is drawn to top left corner of ImagePanel

	/*
		this.veExecuter=new VisualEventExecuter(queue);
		this.veProducer = new VisualEventProducer(queue);
		*/

	}

	public void setImagePanelDimension(Dimension ipd){
		this.imagePanelDimension=ipd;
	}

	public void setSelectedBufferedImage(String imagePath) throws Exception{
		File imageFile;
		try {
			if(imagePath != null){
			imageFile=new File(imagePath);
			this.setOriginalImageToShown(readImageFile(imageFile));
			}
			else{
				this.setOriginalImageToShown(null);
			}
		} catch (IOException e) {
			LOGGER.severe("Error in getting image from String path:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			this.setOriginalImageToShown(null);
		}
		finally{imageFile=null;}
	}

	public Dimension getImageDimension(File imageFile)throws Exception{
			BufferedImage image = readImageFile(imageFile);
			if(image != null){
				Dimension dim =new Dimension(image.getWidth(),image.getHeight());
				image=null;
				return dim;
			}
			throw new NullPointerException();

	}

	public BufferedImage readImageFile(File file) throws Exception{

		if(Utils.getExtension(file).equals(Utils.tif) || Utils.getExtension(file).equals(Utils.tiff)){
			PlanarImage pim=null;
			pim= JAI.create("fileload", file.getAbsolutePath());
			if(pim != null)
				return pim.getAsBufferedImage();
			else
				throw new Exception();
		}
		else{
		//	return ImageIO.read(file);
			BufferedImage in = ImageIO.read(file);
			BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);

			Graphics2D g = newImage.createGraphics();
			g.drawImage(in, 0, 0, null);
			g.dispose();
			in=null;
			return newImage;

		}


	}

	public PositionedImage getRefreshedImage(int processingID){
		// check than needed image and the panel size are initialized
			if(this.originalImage == null || this.imagePanelDimension == null)
				return null;
		// is relativeViewRectangleInitialized
		if(this.relativeViewRectangle == null){
			return initAndscaleToImagePanel();
		}
		else{
			return getZoomedImage(new Point2D.Double(this.imagePanelDimension.getWidth()/2,this.imagePanelDimension.getHeight()/2), 1.0, ID.IMAGE_PROCESSING_BEST_QUALITY);
			//return cropWithPredefinedSettings(processingID);
		}

	}

	public PositionedImage dragLayers(Point movement, int processingID){
		try {
			// check that all needed objects exist
			if(this.originalImage == null || this.relativeViewRectangle == null || this.imagePanelDimension == null)
			return null;

			Point2D movementAtImage= convertScreenPointToRelativePoint(movement);


			// calculate temperary relativeImagePanelDimension -> save it as relativeImagePanelDimension when image is created successfully
			Rectangle tempRelativeViewRectangle= moveRelativeViewRectangle(movementAtImage);

			// tempRelativeViewRectangle is proportional to ImagePanel size -> height or width may be bigger than originalImage -> calculate dimension of real cropped image
			Dimension imageDimension= getImageDimensionInsideRectangle(tempRelativeViewRectangle);
			// too small image
			if(imageDimension.getWidth() <20|| imageDimension.getHeight() <20)
				return new PositionedImage(null);

			// crop the image
			BufferedImage 	processedImage = Scalr.crop(this.originalImage, tempRelativeViewRectangle.x, tempRelativeViewRectangle.y, imageDimension.width, imageDimension.height, null);


			return scaleToImagePanel(processedImage, tempRelativeViewRectangle, processingID);

		} catch (Exception e) {
			LOGGER.severe("Error in dragging image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}


	/**
	 * @param focusPoint Point at screen where focus will be changed
	 * @param zoomValue double value how much will be zoomed in or out. eg. 0.8 or 1.2
	 * @param processingID ID for quality of returned image fast >-> best quality
	 * @return PositionedImage which contains the zoomed image and it's top left corner location
	 */
	public PositionedImage getZoomedImage(Point2D focusPoint, double zoomValue, int processingID){
		BufferedImage 	processedImage;
		Rectangle tempRelativeViewRectangle;
		try {
			// check that all needed objects exist
			if(this.originalImage == null || this.relativeViewRectangle == null || this.imagePanelDimension == null)
			return null;

		//  make own calculations for scaling either height or width

			// calculate temperary relativeImagePanelDimension -> save it as relativeImagePanelDimension when image is created successfully
			tempRelativeViewRectangle= calculateTempRelativeViewRectangleFocusedOnFocusPoint(focusPoint, zoomValue);

			// tempRelativeViewRectangle is proportional to ImagePanel size -> height or width may be bigger than originalImage -> calculate dimension of real cropped image
			Dimension imageDimension= getImageDimensionInsideRectangle(tempRelativeViewRectangle);
			if(imageDimension.getWidth() < 20 || imageDimension.getHeight() <20)
				return new PositionedImage(null);

			// crop the image
			processedImage = Scalr.crop(this.originalImage, tempRelativeViewRectangle.x, tempRelativeViewRectangle.y, imageDimension.width, imageDimension.height, null);


			return scaleToImagePanel(processedImage, tempRelativeViewRectangle, processingID);


		} catch (Exception e) {
			LOGGER.severe("Error in zooming image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
		finally{
			processedImage=null;
			tempRelativeViewRectangle=null;
		}



	}

	private Rectangle moveRelativeViewRectangle(Point2D movement){
		Rectangle tempRelativeViewRectangle = new Rectangle(this.relativeViewRectangle.x+(int)movement.getX(), this.relativeViewRectangle.y+(int)movement.getY(),
				this.relativeViewRectangle.width, this.relativeViewRectangle.height);

		if(tempRelativeViewRectangle.x + tempRelativeViewRectangle.width > this.originalImage.getWidth())
			tempRelativeViewRectangle.x = this.originalImage.getWidth()-tempRelativeViewRectangle.width;

		if(tempRelativeViewRectangle.y+tempRelativeViewRectangle.height > this.originalImage.getHeight())
			tempRelativeViewRectangle.y= this.originalImage.getHeight()-tempRelativeViewRectangle.height;
		if(tempRelativeViewRectangle.x<0)
			tempRelativeViewRectangle.x=0;
		if(tempRelativeViewRectangle.y<0)
			tempRelativeViewRectangle.y=0;

		return tempRelativeViewRectangle;

	}

	private Dimension getImageDimensionInsideRectangle(Rectangle rec){
		try {
			int h=rec.height;
			int w=rec.width;
			if(rec.height>this.originalImage.getHeight())
				h= this.originalImage.getHeight();
			if(rec.width>this.originalImage.getWidth())
				w=this.originalImage.getWidth();
			return new Dimension(w,h);
		} catch (Exception e) {
			LOGGER.severe("Error in zooming image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}

	}




	private PositionedImage scaleToImagePanel(BufferedImage processedImage, Rectangle tempRelativeView, int processingID){
		BufferedImage scaledImage;
		try {

			//  make own calculations should image being scaled either to height or width
			Scalr.Mode  scalingMode= (Scalr.Mode)(getScalingMode(processedImage.getWidth(), processedImage.getHeight(),this.imagePanelDimension));

			// set up the quality/speed constant -> AUTOMATIC seems to produce good quality images
			Scalr.Method processingQuality = Scalr.Method.AUTOMATIC;

		//	if(processingID == ID.IMAGE_PROCESSING_BEST_QUALITY)
		//		processingQuality = Scalr.Method.ULTRA_QUALITY;

			// get new image scaled to Dimension of ImagePanel
			scaledImage= Scalr.resize(processedImage, processingQuality, scalingMode, this.imagePanelDimension.width, this.imagePanelDimension.height, null);

			if(scaledImage != null){
				// save the used tempRelativeView as relativeViewRectangle
				this.setRelativeViewRectangle(tempRelativeView);
				// return PositionedImage with scaled image and position converted to screen
				return new PositionedImage(scaledImage, convertRelativePointToScreenPoint(new Point(tempRelativeView.x, tempRelativeView.y)));
			}
			else
				return null;

		} catch (IllegalArgumentException e) {
			LOGGER.severe("Error Arguments in scaling image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		} catch (ImagingOpException e) {
			LOGGER.severe("Error in imagingOP when scaling image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
		finally{scaledImage=null;}

	}

	private PositionedImage initAndscaleToImagePanel(){
		BufferedImage scaledImage;
		try {

			//  make own calculations for scaling either height or width
			Scalr.Mode  scalingMode= (Scalr.Mode)(getScalingMode(this.originalImage.getWidth(), this.originalImage.getHeight(),this.imagePanelDimension));

			// set up the quality/speed constant
			Scalr.Method processingQuality = Scalr.Method.ULTRA_QUALITY;

			// get new image scaled to Dimension of ImagePanel
			scaledImage= Scalr.resize(this.originalImage, processingQuality, scalingMode, this.imagePanelDimension.width, this.imagePanelDimension.height, null);

			if(scaledImage != null){
				// calculate the initial dimension of relativeViewRectangle where topleft corner coordinate is (0,0)
				this.setRelativeViewRectangle(calculateRelativeDimensionToImagePanel(scalingMode, this.originalImage.getWidth(), this.originalImage.getHeight()));
				// return PositionedImage with scaled image and default topleft corner position (0,0)
				return new PositionedImage(scaledImage);
			}
			else
				return null;

		} catch (IllegalArgumentException e) {
			LOGGER.severe("Error Arguments in scaling image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		} catch (ImagingOpException e) {
			LOGGER.severe("Error in imagingOP when scaling image:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
		finally{
			scaledImage=null;
		}

	}

	private Rectangle calculateRelativeDimensionToImagePanel(Scalr.Mode scalingMode, int original_width, int original_height){
		try {
			// at start the relativeImagePosition should be (0,0)
//	this.relativeImagePosition=new Point(0, 0);

			int relativeHeight;
			int relativeWidth;
//	Scalr.Mode  scalingMode= (Scalr.Mode)(getScalingMode(this.originalImage.getWidth(), this.originalImage.getHeight()));
			if(scalingMode == Scalr.Mode.FIT_TO_HEIGHT){
				relativeHeight=original_height;
				relativeWidth=(int)((double)this.imagePanelDimension.getWidth()/((double)this.imagePanelDimension.getHeight()/(double)relativeHeight));
			}
			else{
				relativeWidth=original_width;
				relativeHeight=(int)((double)this.imagePanelDimension.getHeight()/((double)this.imagePanelDimension.getWidth()/(double)relativeWidth));
			}
			// create and set the rectangle with calculated width and height and default topleft corner position (0,0)
			return new Rectangle(relativeWidth,relativeHeight);
		} catch (Exception e) {
			LOGGER.severe("Error in calculating RelatiViewRectangle dimension:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}


	}


	/**
	 * Calculates which scaling method is better to scale image with original orientation and fit to ImagePanel in best way.
	 * @param image_width the width of image which best scaling method is examined
	 * @param image_height the height of image which best scaling method is examined
	 * @param iPanelDimension the Dimension of ImagePanel.
	 * @return Scalr.Mode Enum object either FIT_TO_HEIGHT or FIT_TO_WIDTH
	 */
	public Scalr.Mode getScalingMode(int image_width, int image_height, Dimension iPanelDimension){

		try {
			if (image_width >0 && image_height> 0  && iPanelDimension != null && iPanelDimension.getWidth() >0) {

				double image_proportion = (double)image_width / (double)image_height;
				double panel_proportion = iPanelDimension.getWidth()/ iPanelDimension.getHeight();

				if (image_proportion > panel_proportion)
					return Scalr.Mode.FIT_TO_WIDTH;
				else
					return Scalr.Mode.FIT_TO_HEIGHT;
			}
			return Scalr.Mode.FIT_TO_WIDTH;
		} catch (Exception e) {
			LOGGER.severe("Error in calculating scale method:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return Scalr.Mode.FIT_TO_WIDTH;
		}

	}

	private Rectangle calculateTempRelativeViewRectangleFocusedOnFocusPoint(Point2D focusPoint, double zoomValue){
		try {

			// calculate relative location of fpoint from topleft corner of imagePanel
//			Double[] relativeFocusPoint = new Double[2];
			Point2D relativeFocusPoint = new Point2D.Double(focusPoint.getX()/this.imagePanelDimension.getWidth(),
					focusPoint.getY()/this.imagePanelDimension.getHeight());

			//convert the midpoint to relative coordinates
			focusPoint=convertScreenPointToRelativePoint(focusPoint);

			// convert midPoint as Point at the image
			focusPoint.setLocation(this.relativeViewRectangle.x+focusPoint.getX(), this.relativeViewRectangle.y+focusPoint.getY());

			/* calculate the new size of relativeImagePanelRectangle -> this is temporary object until processing can start*/

			// check the reasonable orientation
			Scalr.Mode  scalingMode= (Scalr.Mode)(getScalingMode(this.originalImage.getWidth(), this.originalImage.getHeight(),this.imagePanelDimension));
			int w;
			int h;
			if(scalingMode == Scalr.Mode.FIT_TO_HEIGHT){ // the new size will fit to height: maximum height is same as image height but width can be bigger
				h= (int)(this.relativeViewRectangle.getHeight()*(1.0/zoomValue));
				if(h > this.originalImage.getHeight())
					h = this.originalImage.getHeight();
				w= (int)((double)h*(this.imagePanelDimension.getWidth()/this.imagePanelDimension.getHeight()));
			}
			else{  // the new size will fit to width: maximum width is same as image width but height can be bigger
				w= (int)(this.relativeViewRectangle.getWidth()*(1.0/zoomValue));
				if(w > this.originalImage.getWidth())
					w= this.originalImage.getWidth();
				h = (int)((double)w*(this.imagePanelDimension.getHeight()/this.imagePanelDimension.getWidth()));
			}

			Rectangle tempRelativeViewRectangle=new Rectangle(w, h);

			/* calculate the top left corner of rectangle */

			int topLeft_x = (int)((focusPoint.getX()-tempRelativeViewRectangle.getWidth()*relativeFocusPoint.getX()));
			int topLeft_y = (int)(( focusPoint.getY()-tempRelativeViewRectangle.getHeight()*relativeFocusPoint.getY()));
			if(tempRelativeViewRectangle.width> this.originalImage.getWidth()){
				topLeft_x=0; // position of relativeView starts at left
			}
			else{ // the relativeView width is smaller than the image width
				// relativeView goes outside of image at left
				if(topLeft_x < 0)
					topLeft_x = 0;
				// relativeView goes outside of image at right side
				else if(topLeft_x +tempRelativeViewRectangle.width> this.originalImage.getWidth())
					topLeft_x = this.originalImage.getWidth()-tempRelativeViewRectangle.width;
				}
			if(tempRelativeViewRectangle.height > this.originalImage.getHeight()){
				topLeft_y=0;
			}
			else{ // relativeView height is smaller than image height
				// relativeView goes outside of image at up
				if(topLeft_y < 0){
					topLeft_y =0;
				}
				// relativeView goes outside of image at bottom
				else if(topLeft_y +tempRelativeViewRectangle.height > this.originalImage.getHeight())
					topLeft_y = this.originalImage.getHeight()-tempRelativeViewRectangle.height;

			}
			tempRelativeViewRectangle.setLocation(topLeft_x,topLeft_y);

			return tempRelativeViewRectangle;
		} catch (Exception e) {
			LOGGER.severe("Error in calculating temporary relativeImagePanelRectangle:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}

	}


private Point2D convertScreenPointToRelativePoint(Point2D screenPoint){

		AffineTransform scaleTransform = AffineTransform.getScaleInstance(this.relativeViewRectangle.getWidth()/this.imagePanelDimension.getWidth(), this.relativeViewRectangle.getHeight()/this.imagePanelDimension.getHeight());
	//	Point2D p = new Point2D.Double(screenPoint.getX(), screenPoint.getY());
		Point2D relativePoint =scaleTransform.transform(screenPoint, null);
	//	return (new Point((int)relativePoint.getX(), (int)relativePoint.getY()));
		return relativePoint;
	}

	public void setOriginalImageToShown(BufferedImage originalImageToShown) {

		if(this.originalImage != null && originalImageToShown != null && (this.originalImage.getWidth() != originalImageToShown.getWidth()
				|| this.originalImage.getHeight() != originalImageToShown.getHeight() ) ){
			// refresh the relativeViewRectangle because the image size has changed and no earlier calculations can be used
			this.setRelativeViewRectangle(null);
		}
		this.originalImage = originalImageToShown;
	}

	public Rectangle getRelativeToImagePanelRectangle() {
		return relativeViewRectangle;
	}

	public void setRelativeViewRectangle(Rectangle relativeToImagePanelRect) {
		this.relativeViewRectangle = relativeToImagePanelRect;
	}


	public BufferedImage getOriginalImage() {
		return this.originalImage;
	}

	private Point convertRelativePointToScreenPoint(Point screenPoint){

		try {
			AffineTransform scaleTransform = AffineTransform.getScaleInstance(this.imagePanelDimension.getWidth()/this.relativeViewRectangle.getWidth(), this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight());
			Point2D p = new Point2D.Double(screenPoint.getX(), screenPoint.getY());
			Point2D relativePoint =scaleTransform.transform(p, null);
			return new Point((int)relativePoint.getX(),(int)relativePoint.getY());
		} catch (Exception e) {
			LOGGER.severe("LayerVisualManager Error in converting relative point to screen point:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}


	public boolean addSingleMarkingCoordinate(Point screenPoint, MarkingLayer selectedMarkingLayer){
		if(selectedMarkingLayer != null ){
			Point pointAtImage= convertRelativePointAsPointAtImage(convertScreenPointToRelativePoint(screenPoint));
			if(pointAtImage != null && selectedMarkingLayer != null){
				return selectedMarkingLayer.addSingleCoordinate(pointAtImage);
			}
		}
		return false;

	}

	public boolean removeSingleMarkingCoordinate(Point screenPoint, MarkingLayer selectedMarkingLayer){
		if(selectedMarkingLayer != null ){
			Point pointAtImage= convertRelativePointAsPointAtImage(convertScreenPointToRelativePoint(screenPoint));
			if(pointAtImage != null && selectedMarkingLayer != null){
				return selectedMarkingLayer.removeSingleCoordinate(pointAtImage);

			}
		}
		return false;

	}

	public ArrayList<Point> getScreenPointsOfMarkingLayer(MarkingLayer mLayer){
		ArrayList<Point> tempPointList=new ArrayList<Point>(); // list for keeping points inside relativeViewRectangle
		Iterator<Point> pointIterator= mLayer.getCoordinateList().iterator();
		while(pointIterator.hasNext()){
			Point imagePoint=pointIterator.next();
			if(this.relativeViewRectangle.contains(imagePoint)){
				tempPointList.add(convertImagePointToScreenPoint(imagePoint));
			}


		}
		return tempPointList;

	}

	/**
	 * @param mLayers ArrayList of MarkingLayers which coordinates are converted
	 * @return
	 */
	public ArrayList<ScreenCoordinatesOfMarkingLayer> getScreenPointsOfMarkingLayers(ArrayList<MarkingLayer> mLayers){
		try {
			ArrayList<ScreenCoordinatesOfMarkingLayer> screenCoordinatesOfMarkingLayerList = new ArrayList<ScreenCoordinatesOfMarkingLayer>();
			if(mLayers != null && mLayers.size()>0){
				Iterator<MarkingLayer> mlayerIterator= mLayers.iterator();
				while(mlayerIterator.hasNext()){
					MarkingLayer ml= mlayerIterator.next();
					if(ml.getCoordinateList() != null && ml.getCoordinateList().size()>0){
						screenCoordinatesOfMarkingLayerList.add(new ScreenCoordinatesOfMarkingLayer(getScreenPointsOfMarkingLayer(ml), ml.getLayerID()));
					}

				}
				return screenCoordinatesOfMarkingLayerList;
			}
			return null;
		} catch (Exception e) {
			LOGGER.severe("LayerVisualManager Error in converting relative point to screen point of several MarkingLayers: " +e.getMessage());
			return null;
		}

	}

	private Point convertRelativePointAsPointAtImage(Point2D relativePoint){
		if(relativePoint != null)
			return new Point(this.relativeViewRectangle.x+(int)relativePoint.getX(), this.relativeViewRectangle.y+(int)relativePoint.getY());
		return null;
	}

	private Point convertImagePointToScreenPoint(Point imagePoint){
		if(imagePoint != null){
			Point relativePoint = convertImagePointToRelativePoint(imagePoint);
			if(relativePoint != null)
				return convertRelativePointToScreenPoint(relativePoint);
		}
		return null;
	}

	private Point convertImagePointToRelativePoint(Point imagePoint){
		if (imagePoint != null){
			Point relativePoint = new Point(imagePoint.x-this.relativeViewRectangle.x, imagePoint.y-this.relativeViewRectangle.y);
			if(relativePoint.x>=0 && relativePoint.y >= 0)
				return relativePoint;
		}
		return null;
	}

	private Rectangle convertRecAtImageToRecAtScreen(Rectangle rec) throws Exception{

		if(rec.intersects(this.relativeViewRectangle)){ // does rectangles overlap
			Rectangle2D recNew= rec.createIntersection(this.relativeViewRectangle);
		//	Rectangle2D.intersect(rec, this.relativeViewRectangle, rec);

			 return convertRelativeRectangleToScreenRectangle((Rectangle) recNew);
		//	return convertRelativeRectangleToScreenRectangle((Rectangle) rec.createIntersection(this.relativeViewRectangle));
			//return (Rectangle)rec_converted;
		//	return new Rectangle((int)rec_converted.getX(),(int)rec_converted.getY(),(int)rec_converted.getWidth(), (int)rec_converted.getHeight());
		}
		return null;

	}

	private Rectangle convertRelativeRectangleToScreenRectangle(Rectangle rectangle) throws Exception{

		try {

			if(rectangle != null && rectangle.getLocation() != null && rectangle.width>0 && rectangle.height>0)
				return new Rectangle(convertImagePointToScreenPoint(rectangle.getLocation()),
				new Dimension(convertRelativeLengthToPanelLength(rectangle.width),convertRelativeHeightToPanelHeight(rectangle.height)));
			else
				return null;
		} catch (Exception e) {
			LOGGER.severe("LayerVisualManager Error in converting relative point to screen point:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}

	private int convertRelativeLengthToPanelLength(int relativeLength){
		double d= this.imagePanelDimension.getWidth()/this.relativeViewRectangle.getWidth();// this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight());
		return (int)(relativeLength*d);
	}
	private int convertRelativeHeightToPanelHeight(int relativeHeight){
		double d= this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight();// this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight());
		return (int)(relativeHeight*d);
	}


	private int convertXinImageToXinScreen(int x){
		int relativeX= x-this.relativeViewRectangle.x;

			double d= this.imagePanelDimension.getWidth()/this.relativeViewRectangle.getWidth();// this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight());
		return (int)(relativeX*d);
	}

	private int convertYinImageToYinScreen(int y){
		int relativeY= y-this.relativeViewRectangle.y;

			double d= this.imagePanelDimension.getHeight()/this.relativeViewRectangle.getHeight();
		return (int)(relativeY*d);
	}

	public int getRemoveDistance() {
		return removeDistance;
	}

	public void setRemoveDistance(int removeDistance) {
		this.removeDistance = removeDistance;
	}

	private double convertScreenDistanceToImageDistance(int screenDistance){
		// from screen to relativeRectangel
		return (screenDistance/this.imagePanelDimension.getWidth()*this.relativeViewRectangle.getWidth());
	}

	private double convertImageDistanceToScreenDistance(int imageDistance){
		// from screen to relativeRectangel
		return (imageDistance/this.relativeViewRectangle.getWidth()*this.imagePanelDimension.getWidth());
	}

	public BufferedImage getSubImage(Point middlepoint, int size){
		try {
			if(this.originalImage != null){
				Point2D imagePoint = convertRelativePointAsPointAtImage(convertScreenPointToRelativePoint(middlepoint));
				double subImageSize = convertScreenDistanceToImageDistance(size);
				int x = (int)(imagePoint.getX() - subImageSize/2);
				int y = (int)(imagePoint.getY() - subImageSize/2);

				return this.originalImage.getSubimage(x, y, (int)subImageSize, (int)subImageSize);
			}
			else{
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in getting subImage from");
			e.printStackTrace();
			return null;
		}


	}

	public double getSizeMultiplier(){
	//	LOGGER.fine("widths: "+this.originalImage.getWidth()+ " "+this.imagePanelDimension.getWidth());
		return (((double)this.originalImage.getWidth())/this.imagePanelDimension.getWidth());
	}

	public Point convertScreenPointToImagePoint(Point sp){
		return convertRelativePointAsPointAtImage(convertScreenPointToRelativePoint(sp));
	}

	public void changeSelectedRectangleOfGridProperty(GridProperties gp, Point p){
		Point imagePoint = convertScreenPointToImagePoint(p);
		gp.changeSelectionOfPositionedRectangle(imagePoint);
		//move UnselectedRectangle to selected
	/*	Iterator<PositionedRectangle> recIterator = gp.getPositionedRectangleList().iterator();
		while(recIterator.hasNext()){
			PositionedRectangle rec =recIterator.next();
			if(rec.contains(imagePoint)){
				// move rectangle to selectedRectangles
				rec.setSelected(!rec.isSelected());

			}
		}
*/


	}

	public GridProperties convertLayerGridPropertiesToPanel(GridProperties gp){
		try {
			if(gp != null){
				GridProperties gp_converted=new GridProperties();
				gp_converted.setGridON(gp.isGridON());

				Iterator<Integer> rowIterator = gp.getRowLineYs().iterator();
				while(rowIterator.hasNext()){
					int y= rowIterator.next();
					gp_converted.addRowLineY(convertYinImageToYinScreen(y));

				}

				Iterator<Integer> columnIterator = gp.getColumnLineXs().iterator();
				while(columnIterator.hasNext()){
					int x= columnIterator.next();
					gp_converted.addColumnLineX(convertXinImageToXinScreen(x));
				}

				Iterator<PositionedRectangle> recIterator = gp.getPositionedRectangleList().iterator();
				while(recIterator.hasNext()){
					PositionedRectangle pr =recIterator.next();

					Rectangle rec2=convertRecAtImageToRecAtScreen(pr); // get the position and size for screen
					//pr.setBounds(rec2.x, rec2.y, rec2.width, rec2.height); // set the new position and size
					if(rec2 != null){
						PositionedRectangle screenPR=new PositionedRectangle(rec2.x, rec2.y, rec2.width, rec2.height, pr.getRow(), pr.getColumn(), pr.isSelected());

						gp_converted.addSinglePositionedRectangle(screenPR);
						screenPR=null;
					}
					rec2=null;

				}

				gp_converted.setHorizontalLineLength((int)convertImageDistanceToScreenDistance(this.originalImage.getWidth()));
				gp_converted.setVerticalLineLength((int)convertImageDistanceToScreenDistance(this.originalImage.getHeight()));

				return gp_converted;
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}






}
