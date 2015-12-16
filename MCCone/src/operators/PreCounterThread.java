package operators;

import information.ColorChannelVectors;
import information.ImageColorChannels;
import information.SharedVariables;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import managers.PreCountThreadManager;
import managers.TaskManager;
import math.geom2d.Point2D;
import gui.ProgressBallsDialog;

/**
 * The Class PreCounterThread. The cells of original image are counted by using image of picked cells. 
 * Colors or picked cell are split to each channels (alpha, red, green, blue). Each channel value is spread for wider color range and saved to colorList.
 * For example red value for some pixel is 200, then the value is spread for 3 values down and 3 values up (197,198,199,200,201,201,202).
 * When color channel values are determined the original image will be processed.
 *  Not all pixels will be determined. The gap value tells how many pixels are let as undetermined between determined pixels.
 *  If determined pixels match with colorList color -> collected. The collected pixel positions are grouped to form individual cell positions.
 *  
 */
public class PreCounterThread implements Runnable{
	
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The sub image. The image that user picks inside the rectangle. */
	private BufferedImage subImage;
	
	/** The original image. The image where cells will be precounted. */
	private BufferedImage originalImage;
	
	/** The counter thread. Thread doing the counting. */
	private Thread counterThread;
	
	/** The continue counting. Boolean to check has user stopped precounting or is precounting ready. */
	private boolean continueCounting=true;
	
	/** The cancelled inside. Boolean is precounting stopped by program. */
	private boolean cancelledInside=false;
	
	/** The color list. List of different colors that are tried to find from image.  */
	private ArrayList<Integer> colorList;
	
	/** The original image pixels. */
	private byte[] originalImagePixels;
	
	
	/** The sub image pixels. */
	private byte[] subImagePixels;
	
	/** The final centroid coordinates. These coordinates are the final results and markings are painted in this positions of image. */
	private ArrayList<Point> finalCentroidCoordinates;
	
	/** The task manger. */
	private TaskManager taskManger;
	
	/** The PreCountThreadManager. Manages outside this Thread and progress Thread. */
	private PreCountThreadManager pctm;
	
	/** The current_color list. Used for finding cells */
	private ArrayList<Integer> current_colorList;
	/** The current_final coordinates.  */
	private ArrayList<Point> current_finalCoordinates;
	
	private ArrayList<Point> copy_of_current_finalCoordinates;
	
	/** The current_final centroid coordinates. Results of this run of Thread. If everything will go ok -> there coordinates saved to finalCenteroidCoordinates.  */
	private ArrayList<Point> current_finalCentroidCoordinates;
	
	/** The current_gap. A gap between pixels that are evaluated.  */
	private int current_gap =2;
	
	/** The min_distance_between_cells_boundaries. */
	private int min_distance_between_cells_boundaries=5;
	
	/** The pixel_color_relaxation. How much each color channel of pixel will be spread. */
	private int pixel_color_relaxation=3; // from 0 - 5 is ok
	
	/** The max_coordinate_number_in_cell. */
	private int max_coordinate_number_in_cell=Integer.MIN_VALUE;
	
	/** The current_max_coordinate_number_in_cell. */
	private int current_max_coordinate_number_in_cell=Integer.MIN_VALUE;
	
	/** The global_min_cell_diameter. This is global minimum for cell diameter */
	private final int global_min_cell_diameter =SharedVariables.GLOBAL_MIN_CELL_DIAMETER; // 
	
	/** The global_min_coordinate_number_in_cell. This is global minimum for coordinate number. */
	private final int global_min_coordinate_number_in_cell =SharedVariables.GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL;  
	
	/** The max_cell_size. only initial value which is changed when user picks bigger cells */
	private int max_cell_size=0; 
	
	/** The current_max_cell_size. */
	private int current_max_cell_size;
	
	/** The current_min_cell_size. */
	private int current_min_cell_size;
	
	/** The min_cell_size. */
	private int min_cell_size=Integer.MAX_VALUE;
	
	/** The cell size min scaling factor. When user picks a cell -> all cell sizes that are half of picked one are taken to countings. */
	private double cellSizeMinScalingFactor=0.5;
	
	/** The cell size max scaling factor. When user picks a cell -> all cell sizes that are 50% bigger than picked one are taken to countings. */
	private double cellSizeMaxScalingFactor=1.5;




	/** The max cell number in cell group. */
	private int maxCellNumberInCellGroup=10;

	/**
	 * Instantiates a new PreCounterThread.
	 *
	 * @param subImage the sub image
	 * @param originalImage the original image
	 * @param taskManager the task manager
	 */
	public PreCounterThread(BufferedImage subImage, BufferedImage originalImage , TaskManager taskManager){
		this.taskManger=taskManager;
		this.subImage=subImage;
		this.originalImage=originalImage;
		this.counterThread=new Thread(this, "counter");
		this.colorList=new ArrayList<Integer>();
		this.finalCentroidCoordinates = new ArrayList<Point>();
	}

	/**
	 * Aborts the  execution.
	 *
	 * @param title the title
	 * @param message the message
	 */
	private void abortExecution(String title, String message){
		if(cancelledInside)
			this.taskManger.showMessageToUser(title, message);
		clean();
	}

	/**
	 * Adds the colors to colorList if identical color not found.
	 *
	 * @param newColorList the new color list
	 */
	private void addColorsIfNotFound(ArrayList<Integer> newColorList){

		for (Iterator<Integer> iterator = newColorList.iterator(); iterator.hasNext();) {
			int colorInt = (int) iterator.next();
			if(!this.current_colorList.contains(colorInt)){
				this.current_colorList.add(colorInt);
			}
		}

	}



	/**
	 * Calculates centroid of group of WeightPoints.
	 *
	 * @param weightPointList the weight point list
	 * @return the point
	 */
	private Point calculateCentroid(ArrayList<WeightPoint> weightPointList ){
		   if(weightPointList != null && weightPointList.size()>0){
			   double sum_Of_weights=0;
	
			   double sum_Of_X_distance_weights=0;
			   double sum_Of_Y_distance_weights=0;
	
			   Iterator<WeightPoint> wIterator= weightPointList.iterator();
			   while(wIterator.hasNext()){
				   WeightPoint wp = wIterator.next();
				   sum_Of_weights+= wp.getWeight();
				   sum_Of_X_distance_weights += wp.getPoint().x*wp.getWeight();
				   sum_Of_Y_distance_weights += wp.getPoint().y*wp.getWeight();
	
			   }
	
			   int x_coord= (int)(sum_Of_X_distance_weights/sum_Of_weights);
			   int y_coord = (int)(sum_Of_Y_distance_weights/sum_Of_weights);
			   return new Point(x_coord, y_coord);
		   }
		   return null;
	   }




	/**
	 * Determines colors channels into vectors. Alpha, red, green and blue are separated channels. 
	 * Results is smoothed with MovinAverage to remove dispersion of color values.
	 * 
	 *
	 * @param channels the channels of colors
	 * @param mod_x the horizontal direction change
	 * @param mod_y the vertical direction change
	 * @return the color channel vectors
	 * @throws Exception the exception
	 */
	private ColorChannelVectors calculateColorsForVectors(ImageColorChannels channels, int mod_x, int mod_y) throws Exception {
		int maxLength=((int)(channels.getHeight()/2));
 		ColorChannelVectors colorVectors=new ColorChannelVectors(maxLength );
		Point midPoint= new Point((int)(channels.getWidth()/2), (int)(channels.getHeight()/2));
		int[] alpha=new int[maxLength];
		int[] red=new int[maxLength];
		int[] green=new int[maxLength];
		int[] blue=new int[maxLength];
		double[] alpha_ma=new double[maxLength];
		double[] red_ma=new double[maxLength];
		double[] green_ma=new double[maxLength];
		double[] blue_ma=new double[maxLength];
		for(int x=midPoint.x, y=midPoint.y, i=0; x < channels.getWidth() && x >= 0 && y<channels.getHeight() && y >= 0 && i<colorVectors.getSize();x+=mod_x, y+=mod_y, i++){
			if(channels.useAlpha()){
				alpha[i]= channels.getAlpha(x,y);
				red[i]= channels.getRed(x,y);
				green[i] = channels.getGreen(x,y);
				blue[i] = channels.getBlue(x,y);
			}
			else{

				red[i]= channels.getRed(x,y);
				green[i] = channels.getGreen(x,y);
				blue[i] = channels.getBlue(x,y);
			}
			if(!continueCounting)
				return null;
		}

		// smooth data with moving average -> removes dispersion of color values

            MovingAverage ma = new MovingAverage(channels.getHeight()/10);
            int i=0;
            for (int x : red) {
                ma.newNum(x);
                red_ma[i]=ma.getAvg();
                i++;
                if(!continueCounting)
					return null;
            }

            i=0;
            ma = new MovingAverage(channels.getHeight()/10);
            for (int x : green) {
                ma.newNum(x);
        
                green_ma[i]=ma.getAvg();
                i++;
                if(!continueCounting)
					return null;
            }
  
            i=0;
            ma = new MovingAverage(channels.getHeight()/10);    
            for (int x : blue) {
                ma.newNum(x);	         
                blue_ma[i]=ma.getAvg();
                i++;
                if(!continueCounting)
					return null;
            }
		            
		    colorVectors.setOriginalData(alpha, red, green, blue);
		    colorVectors.setMoveAveragedData(alpha_ma, red_ma, green_ma, blue_ma);
		return colorVectors;
	}

	/**
	 * Goes trough the pixels of original image and checks is the color found from colorList. 
	 * If found -> coordinate of pixel saved to list.
	 *
	 * @throws Exception the exception
	 */
	private void calculateCoordinates() throws Exception{
		   getOriginalImageAsByteArray(this.originalImage);
	
		   this.current_finalCoordinates=new ArrayList<Point>();
		   int w= this.originalImage.getWidth();
		   int h = this.originalImage.getHeight();
	
		   // go through colorMatrix
		   for(int r =0;r<h;r+=current_gap){ // rows
			   for(int c =0;c<w;c+=current_gap){ // columns
				   if(!continueCounting){
					   return;
				   }
				   if(hasCellColor(r,c,w)){ // the pixels is same as some color in picked cell
					   Point p = new Point(c,r);
					   if(!this.current_finalCoordinates.contains(p))
					   {
	
						   this.current_finalCoordinates.add(p);
					   }
				   }
			   } // for columns
		   } // for rows
		   Collections.sort(this.current_finalCoordinates, new CoordinateComparator());
	   }

	/**
	 * Calculates outer radius area.
	 *
	 * @param countNumber the count number
	 * @param radius_bigger the radius_bigger
	 * @param radius_smaller the radius_smaller
	 * @return the double
	 */
	private double calculateOuterRadiusArea(int countNumber, int radius_bigger, int radius_smaller){
		   double areaBigger= Math.PI*(double)radius_bigger*(double)radius_bigger;
		   double areaSmaller= Math.PI*(double)radius_smaller*(double)radius_smaller;
		   return ((double)countNumber)/(areaBigger-areaSmaller);
	   }

	/**
	 * Cancels the counting. Mediates the canceling to PreCountThreadManager.
	 */
	public void cancelInside(){
		this.continueCounting=false;
		this.cancelledInside=true;

		if(this.pctm != null)
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					pctm.cancelCounting();

				}
			});
	}

	/**
	 * The counting is cancelled outside by user.
	 */
	public void cancelOutside(){
		this.continueCounting=false;

		if(this.pctm != null)
			this.pctm.cancelCounting();


	}
	

	/**
	 * Checks over bounds of color values. The range should be inside 0-255.
	 *
	 * @param colorValue the color value
	 * @return the int
	 */
	private int checkOverBounds(int colorValue){
		if(colorValue<0)
			return 0;
		if(colorValue>255)
			return 250;
		return colorValue;
	}

	/**
	 * Cleans the counting thread variables to start a fresh counting.
	 */
	private void clean(){
		this.cancelledInside=false;
		this.current_finalCoordinates=null;
		this.current_colorList=null;
		this.current_finalCentroidCoordinates=null;
		this.current_max_cell_size=0;
	}

	/**
	 * Cleans collect groups and cluster.
	 *
	 * @throws Exception the exception
	 */
	private void cleanCollectGroupsAndCluster() throws Exception{
		   this.copy_of_current_finalCoordinates=new ArrayList<Point>();
		   this.copy_of_current_finalCoordinates.addAll(this.current_finalCoordinates);
		   this.current_finalCentroidCoordinates=new ArrayList<Point>();
		   Collections.sort(this.copy_of_current_finalCoordinates, new CoordinateComparator());
		   while(this.continueCounting && copy_of_current_finalCoordinates.size()>0 ){ // Points are removed from list in method getNeighbourPoints
			   int randomIndex=(int)(Math.random()*copy_of_current_finalCoordinates.size());
			   Point firstPoint = copy_of_current_finalCoordinates.get(randomIndex);
			   Stack<Point> stack  = new Stack<Point>();
			   stack.push(firstPoint);
			   copy_of_current_finalCoordinates.remove(firstPoint);
	
			   ArrayList<Point> groupPoints = getPoints(stack, new ArrayList<Point>());
	
	
			   if(groupPoints != null && groupPoints.size() >= this.global_min_coordinate_number_in_cell ){//&& groupPoints.size() < this.current_max_coordinate_number_in_cell){
				   createWeightedPointGroup(groupPoints);
			   }
		   }
	   }

	/**
	 * Collects the inner cell colors from sub image that user has picked.
	 *
	 * @param radius the radius
	 * @throws Exception the exception
	 */
	private void collectInnerCellColorsFromSubImage(int radius) throws Exception{
		getSubImageAsByteArray(this.subImage);

		if(this.subImagePixels != null){
			int w=this.subImage.getWidth();
			int h=this.subImage.getHeight();
			Point midPoint=new Point((int)(w/2),(int)(h/2));
	
			for(int x=midPoint.x-radius; x<midPoint.x+radius;x++){
	
				int delta_y = (int)(Math.sqrt((Math.pow(radius, 2)) - Math.pow((x-midPoint.x),2)));
				int start_y= midPoint.y -delta_y;
				int end_y = midPoint.y +delta_y;
	
				for(int y=start_y;y<end_y;y++){
					int index = (y + x*w)*3;
					int argb =  ( 255 << 24) | ((int)(this.subImagePixels[index+2] & 0xFF) << 16) | ((int)(this.subImagePixels[index + 1] & 0xFF) << 8) | ((int)this.subImagePixels[index] & 0xFF);
					addColorsIfNotFound(getRelaxedColors(this.pixel_color_relaxation, argb));
				}
			}
		}
	}

	/**
	 * Convert the BufferedImage to image type for precounting.
	 *
	 * @param src the source BufferedImage
	 * @param bufImgType the image type
	 * @return the BufferedImage
	 */
	private BufferedImage convert(BufferedImage src, int bufImgType) {
		    BufferedImage img= new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
		    Graphics2D g2d= img.createGraphics();
		    g2d.drawImage(src, 0, 0, null);
		    g2d.dispose();
		    return img;
		}

	/**
	 * Converts image to channels for alpha, red, green and blue.
	 *
	 * @param imageIn the source image
	 * @return the image color channels
	 */
	private ImageColorChannels convertImageToChannels(BufferedImage imageIn) {
	
	      try {
	    	  BufferedImage image = convert(imageIn, BufferedImage.TYPE_3BYTE_BGR);
			final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			  final int width = image.getWidth();
			  final int height = image.getHeight();
			  ImageColorChannels channels=new ImageColorChannels(height, width);
			  final boolean hasAlphaChannel = image.getAlphaRaster() != null;
	
			  LOGGER.fine("types: " +pixels[0]+ " "+ pixels[1]+" "+pixels[2]);
	
			  // the image is not 4 bit bufferedImage -> make that way it is!!!!
			  if (hasAlphaChannel) {
				  LOGGER.fine("using alpha channel");
				  channels.setUseAlpha(true);
			     final int pixelLength = 4;
			     for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
			        channels.addAlpha(row,col,(((int) pixels[pixel] & 0xff) << 24)); // alpha
			        channels.addBlue(row,col,((int) pixels[pixel + 1] & 0xff)); // blue
			        channels.addGreen(row,col,(((int) pixels[pixel + 2] & 0xff) << 8)); // green
			        channels.addRed(row,col,(((int) pixels[pixel + 3] & 0xff) << 16)); // red
			        col++;
			        if (col == width) {
			           col = 0;
			           row++;
			        }
			        if(!continueCounting) // user has cancelled the precounting
			        	return null;
			     }
			  } else {
				  LOGGER.fine("No alpha channel");
			     final int pixelLength = 3;
			     for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
			        channels.addRed(row,col,((int) (pixels[pixel + 2] & 0xFF) ));  // red
			        channels.addGreen(row,col, (( (int) (pixels[pixel + 1]  & 0xFF) )) ); // green
			        channels.addBlue(row,col,((int) (pixels[pixel]  & 0xFF)));  // blue
			        col++;
			        if (col == width) {
			           col = 0;
			           row++;
			        }
			        if(!continueCounting) // user has cancelled the precounting
			        	return null;
			     }
			  }
	
			  return channels;
		} catch (Exception e) {
			LOGGER.severe("Error in transforming image to matrix");
			e.printStackTrace();
			this.cancelledInside=true;
			return null;
		}
	   }

	/**
	 * Counts number of coordinates that are inside the cell.
	 *
	 * @param radius the radius of cell
	 * @param gap_candidate the gap between inspected pixels
	 * @return the amount of found coordinates
	 */
	private int countCoordinatesInCell(double radius, int gap_candidate){
		double area = Math.PI*Math.pow(radius, 2);
		return (int)(area/Math.pow(gap_candidate,2));

	}

	/**
	 * Creates the weighted point group. First gets the list of weighted points. 
	 * Then calculates centroids of weighted points to get the middle point -> saved as the new final coordinate.
	 *
	 * @param pointGroups the point groups
	 * @throws Exception the exception
	 */
	private void createWeightedPointGroup(ArrayList<Point> pointGroups) throws Exception{
		   // create initial weighted list
		   ArrayList<WeightPoint> weightPointList = createWeightPointList(pointGroups);
	
		   if(weightPointList.size()>=this.global_min_coordinate_number_in_cell){
				int rounds =0;
				// sort list of weighted points
				Collections.sort(weightPointList, new WeightPointComparator());
				// go trough weight points
				outerLoop:
				while(weightPointList.size()>=global_min_coordinate_number_in_cell && rounds<=10){
					// calculate center of points
					 Point midPoint =calculateCentroid(weightPointList);
					 // count circular data
					 MaxDistancePoint[] maxDistanceValues=getMaxDistanceRoundValues(midPoint, this.current_max_cell_size, weightPointList);
					 // if amount of points is not exceeding maximum amount of point in cell and if user has selected strict precounting then check is cell circular
					 if(weightPointList.size()<= this.current_max_coordinate_number_in_cell && (!SharedVariables.useStrickSearch || SharedVariables.useStrickSearch && isCircular(maxDistanceValues))){
								// one cell
						   this.current_finalCentroidCoordinates.add(midPoint);
						   break outerLoop;
					   }
						else{// possible several cells
	
							WeightPoint w = getWeightPointWithBiggestDistance(maxDistanceValues);
							if(w==null)
								w = weightPointList.get(weightPointList.size()-1); // get last point of list
	
							WeightPoint b =null;
							if(w != null){
								// find nearest point with biggest weight
								int counter=0;
								do{
									counter++;
									if(b != null)
										w=b;
									b = getWeightPointWithBiggestWeightAtDistance(w, weightPointList, this.current_min_cell_size/2); // finds the biggest weight point at distance
								}while((w.x != b.x || w.y != b.y) && counter<100);
	
								if(w.x == b.x && w.y == b.y){ // w has the biggest weight -> near at center of cell
									int radius=this.current_min_cell_size/2;
									Point p = calculateCentroid(getPointsInside(w.getPoint(), radius, weightPointList));
									if(p == null)
										p=w.getPoint();
	
	
									ArrayList<WeightPoint> selectedPointsForCell=new ArrayList<WeightPoint>();
									ArrayList<WeightPoint> candidatePointList=getPointsInside(p, radius, weightPointList); // get points at distance of radius
									if(candidatePointList != null)
									if(candidatePointList != null && candidatePointList.size()>= 1 &&
											candidatePointList.size() <=this.current_max_coordinate_number_in_cell){
	
										if(candidatePointList.size() >=global_min_coordinate_number_in_cell && isCircular(p, candidatePointList)){ //
											selectedPointsForCell.addAll(candidatePointList);
										}
											double averagePointsPerArea = ((double)candidatePointList.size())/(Math.PI*(double)radius*(double)radius);
											double pointsPerArea=averagePointsPerArea;
											int pointsBefore= candidatePointList.size();
	
											// search the correct cell size from min size to max size
											candidateLoop:
											while(pointsPerArea*1.5 > averagePointsPerArea && radius <=this.current_max_cell_size/2){
												radius+=this.current_gap; // grow radius with gap value
												if(candidatePointList != null && candidatePointList.size()>=this.global_min_coordinate_number_in_cell)
													p=calculateCentroid(candidatePointList);
												candidatePointList=getPointsInside(p, radius, weightPointList);
												if(candidatePointList==null){
													break;
												}
												else{  // calculate how many points are at area grown in last loop.
													pointsPerArea=calculateOuterRadiusArea(candidatePointList.size()-pointsBefore, radius, radius-this.current_gap);
													if(pointsPerArea*5 > averagePointsPerArea){ // not reached to cell boundary yet because lot of cells
														averagePointsPerArea=((double)candidatePointList.size())/(Math.PI*(double)radius*(double)radius);
														pointsBefore=candidatePointList.size();
	
														if(candidatePointList.size() >=global_min_coordinate_number_in_cell &&
															candidatePointList.size() <= this.current_max_coordinate_number_in_cell && isCircular(p, candidatePointList)){ // is points in circle
															selectedPointsForCell.clear();
															selectedPointsForCell.addAll(candidatePointList);
	
														}
													}
													else{
														break candidateLoop;
													}
												}
											}
	
											if(selectedPointsForCell != null && selectedPointsForCell.size()>0 && 
											selectedPointsForCell.size()>=global_min_coordinate_number_in_cell &&
											selectedPointsForCell.size()<=current_max_coordinate_number_in_cell){
												// calculate the centroid of cells -> the final point to be saved.
												midPoint = calculateCentroid(selectedPointsForCell);
												this.current_finalCentroidCoordinates.add(midPoint);
											}
											weightPointList.removeAll(candidatePointList); // remove selected points
											Collections.sort(weightPointList, new WeightPointComparator());
	
									}
								} // not
						}
					}
					rounds++;
					
				}
	
	
		   }else{
			   LOGGER.fine("WeightPoint list too small");
		   }
	   }

	/**
	 * Creates the weighted points from given list of points. 
	 * The weight of point is calculated by amount of neighbor points and how far they are. 
	 * The weight of point is proportional to distance to other points. Lot of points close -> big weight.
	 * 
	 *
	 * @param pointGroups the point groups
	 * @return the array list
	 */
	private ArrayList<WeightPoint> createWeightPointList(ArrayList<Point> pointGroups){
		   ArrayList<WeightPoint> weightPointList = new ArrayList<WeightPoint>();
		   if(pointGroups != null && pointGroups.size()>0)
				 for (int j=0;j<pointGroups.size()-1;j++) {
						Point point = pointGroups.get(j);
						WeightPoint wpoint = new WeightPoint(point);
						// set boundaries for binary search
						int lowerBoundValue=Math.max(0,(int)wpoint.getPoint().x-this.current_max_cell_size/2);
						int upperBoundValue=(int)(wpoint.getPoint().x+this.current_max_cell_size/2);
						// find points close to wpoint in horizontal level
						int[] bounds=startEndBinarySearch(pointGroups, lowerBoundValue, upperBoundValue);
						if(bounds != null && bounds[0] >=0 && bounds[1] <pointGroups.size() && bounds[0] <= bounds[1]){
							for (int i = bounds[0]; i <= bounds[1]; i++) {
								// calculate distance
								double distance = wpoint.getPoint().distance((Point)pointGroups.get(i));
								if( distance < this.current_max_cell_size/2 && distance > 0){
									   wpoint.increaseWeight(1/distance); // increase weight by distance
								}
							}
							weightPointList.add(wpoint);
						}
		   }
		   return weightPointList;
	   }

	/**
	 * Finds colors from colorList collections using binary search.
	 *
	 * @param colorInt the color int
	 * @return true, if successful
	 */
	private boolean foundColor(int colorInt){
	
		  if(Collections.binarySearch(this.current_colorList, colorInt) >=0)
			  return true;
		  return false;
	   }


   /**
    * Returns the color vectors in different 8 angles starting from middle of picked cell and ending to 8 directions.
    *
    * @return the channel vectors for 8 angles
    * @throws Exception the exception
    */
   private ArrayList<ColorChannelVectors> get8Angles() throws Exception{
	ArrayList<ColorChannelVectors> angleVectors=new ArrayList<ColorChannelVectors>();
	ImageColorChannels channels = convertImageToChannels(this.subImage);
	if(channels != null){


		for(int i=-1;i<=1;i++){ // left-right direction
			for(int j=-1;j<=1;j++){ // up_down direction
				if(!(i==0 && j==0)){
					angleVectors.add(calculateColorsForVectors(channels, i, j));
				}

			if(!continueCounting)
				return null;
			}
		}

		return angleVectors;

	}
	continueCounting=false;
	return null;
}

   /**
    * Determines the different colors of picked cell. 
    * Goes through ColorChannelVectors and calculates the maximum sum of slopes value for each channel value at position i in vector.
    *
    * @param angleVectors the angle vectors
    * @return the different colors
    * @throws Exception the exception
    */
   private void getDifferentColors(ArrayList<ColorChannelVectors> angleVectors) throws Exception{
	this.current_colorList=new ArrayList<Integer>();
	if(this.colorList.size()>0)
		this.current_colorList.addAll(this.colorList);
	this.current_max_coordinate_number_in_cell=this.max_coordinate_number_in_cell;
	this.current_max_cell_size=this.max_cell_size;
	this.current_min_cell_size=this.min_cell_size;


	if(angleVectors != null && angleVectors.size()>0){
		int sumOfMaxKindexes = 0;
		int kNumber=0;
		int minCellSize =Integer.MAX_VALUE;
		Iterator<ColorChannelVectors> vIterator = angleVectors.iterator();
		while(vIterator.hasNext()){
			double maxK=0;
			int indexOfMaxK=0;
			ColorChannelVectors colorVector=vIterator.next();
			// go through vectors and for each color count k -> sum k of three colors -> save
			int startGap =(int)(colorVector.getSize()/20);
			if(startGap<3)
				startGap=3;
			int endGap=(int)(colorVector.getSize()/4);
			if(endGap<5)
				endGap=5;
			for(int gap = startGap;gap<endGap;gap++){
				for(int i=gap+1;i<colorVector.getSize()-gap-1;i++){
					int start_x=i-gap;
					int end_x =i+gap;
					double kValueSum= colorVector.getKvalueAt(start_x, end_x);
					if(kValueSum > maxK){
						maxK=kValueSum;
						indexOfMaxK=start_x;
					}
				}
			}
			// store int colors from 0 -> indexOfMax from vectors
			if(indexOfMaxK>0){
				sumOfMaxKindexes+=indexOfMaxK; // add for sum
				kNumber++;
				if(indexOfMaxK<minCellSize)
					minCellSize=indexOfMaxK; // stores the smallest cell size from middle to border of cell
				for(int i= 0;i<indexOfMaxK;i++){
					int colorInt = colorVector.getFullColorInt_original(i);
					addColorsIfNotFound(getRelaxedColors(this.pixel_color_relaxation, colorInt));

				}
			}
		}
		if(kNumber <1){
			cancelInside();
			return;
		}

		int cell_size=(int)(sumOfMaxKindexes*2/kNumber); // count average cell diameter kNumber
		if(cell_size < this.global_min_cell_diameter){
			LOGGER.warning("Too small cell picked in cell counting");
			cancelInside(); // too small cell -> stop
			return;
		}

		if(cell_size*this.cellSizeMaxScalingFactor > this.current_max_cell_size)
			this.current_max_cell_size= (int)(cell_size*this.cellSizeMaxScalingFactor);

		if(cell_size*this.cellSizeMinScalingFactor < this.current_min_cell_size)
			this.current_min_cell_size = Math.max((int)(cell_size*this.cellSizeMinScalingFactor), this.global_min_cell_diameter);



		this.current_gap=2;
		//initalize the current_max_coordinate_number_in_cell
		this.current_max_coordinate_number_in_cell=countCoordinatesInCell(((double)this.current_max_cell_size)/2.0, this.current_gap);

		int gap_candidate=0;
		doLoop:
		do{
			if(gap_candidate>=SharedVariables.MAX_GAP){
				
				break doLoop;
			}
			// count new gap by increasing by one
			gap_candidate++;
			
			int minCoordinatesInCell=countCoordinatesInCell(((double)this.current_min_cell_size)/2.0, gap_candidate);
			if(minCoordinatesInCell >= this.global_min_coordinate_number_in_cell){
				this.current_gap=gap_candidate;
				this.current_max_coordinate_number_in_cell=countCoordinatesInCell(((double)this.current_max_cell_size)/2.0, this.current_gap);

			}
			else{
				// don't change gap values
				break doLoop;
			}


		}while(gap_candidate<20);


		// collect colors from whole image by using minCellSize
		collectInnerCellColorsFromSubImage(minCellSize);

		// sort list
		Collections.sort(this.current_colorList);

	}
}

   private byte[] getImageAsByteArray(BufferedImage imageIn){
	   BufferedImage image = convert(imageIn, BufferedImage.TYPE_3BYTE_BGR); // convert to 3byte bgr image
		return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

   }

   /**
    * Returns the max distance round values. The points are separated
    *
    * @param mP the m p
    * @param circleSize the circle size
    * @param pList the list
    * @return the max distance round values
    */
   private MaxDistancePoint[] getMaxDistanceRoundValues(Point mP, int circleSize, ArrayList<WeightPoint> pList){

	   MaxDistancePoint[] maxPoints=new MaxDistancePoint[8];
	   for (Iterator<WeightPoint> iterator = pList.iterator(); iterator.hasNext();) {
			WeightPoint searchPoint = iterator.next();
			int sliceNumber= isInsideWhichSlice(mP, searchPoint, circleSize);
			if(sliceNumber>0 && sliceNumber <=8){
				double distance = mP.distance(searchPoint);
				if(maxPoints[sliceNumber-1] == null || maxPoints[sliceNumber-1].getDistance() < distance){
					maxPoints[sliceNumber-1] = new MaxDistancePoint(searchPoint, distance);
				}

			}
	   }


	   return maxPoints;
   }



   private ArrayList<Point> getNeighbourPoints(Point p) throws Exception{
	   ArrayList<Point> neighbours=new ArrayList<Point>();

	   int maxDistance=(int)(this.current_max_cell_size/this.min_distance_between_cells_boundaries);
	   int lowerBoundValue=Math.max(p.x-maxDistance,0);
	   int upperBoundValue=p.x+maxDistance;
	   int[] startEndIndexes=startEndBinarySearch(this.copy_of_current_finalCoordinates, lowerBoundValue, upperBoundValue);
	   if(startEndIndexes != null && startEndIndexes[0] >= 0
			   && startEndIndexes[1] <= this.copy_of_current_finalCoordinates.size()-1 && startEndIndexes[0] < startEndIndexes[1])


	   for (int i = startEndIndexes[0]; i <= startEndIndexes[1]; i++) {
		   Point point = this.copy_of_current_finalCoordinates.get(i);
		   if(point != null)
			if(point.distance(p) <maxDistance){
				neighbours.add(point);	
			}
	   }


		this.copy_of_current_finalCoordinates.removeAll(neighbours);

	   return neighbours;

   }

   private void getOriginalImageAsByteArray(BufferedImage imageIn){
		this.originalImagePixels = getImageAsByteArray(imageIn);

   }

   /**
    *
    * Recursively collects all neighbor Points for Points at stack
 * @param stack Stack containing Points
 * @param groupPoints ArrayList of Points which are
 * @return ArrayList of Points that has been collected in recursion
 */
private ArrayList<Point> getPoints(Stack<Point> stack, ArrayList<Point> groupPoints) throws Exception{

	   if(stack.isEmpty())
		   return groupPoints;

	   Point firstPoint = stack.pop();
	   groupPoints.add(firstPoint);

	   stack.addAll(getNeighbourPoints(firstPoint)); // adds Points to stack and removes from copy_of_current_finalCoordinates
	   if(stack.size()> this.current_max_coordinate_number_in_cell*this.maxCellNumberInCellGroup)
		   return null;
	   return getPoints(stack, groupPoints);
   }


   public ArrayList<WeightPoint> getPointsInside(Point mPoint, int distance, ArrayList<WeightPoint> weightPointList){
	Point2D midPoint = new Point2D(mPoint.x,mPoint.y);
	  ArrayList<WeightPoint> pointsInside=new ArrayList<WeightPoint>();
	  	//binarysearch
		int lowerBoundValue=Math.max(0,midPoint.getAsInt().x-distance);
		int upperBoundValue=midPoint.getAsInt().x+distance;
		int[] bounds=startEndBinarySearchWithWeightPoints(weightPointList, lowerBoundValue, upperBoundValue);
		if(bounds != null && bounds[0] >=0 && bounds[1] <weightPointList.size() && bounds[0] <= bounds[1]){
			for (int i = bounds[0]; i <= bounds[1]; i++) {
				WeightPoint wPoint=weightPointList.get(i);
				if(midPoint.distance(wPoint.getPoint2D()) <= distance*1.01){	// 1% bigger distance to be sure of collecting the midPoint to the result
					pointsInside.add(wPoint);
				}
			}
		}
	   return pointsInside;

}

   /**
    * Returns the reduced noise color. Checks that each color channel value is between 0-255.
    *
    * @param colorInt the color as Integer
    * @return the reduced noise color as Integer
    */
   private Integer getReducedNoiseColor(int colorInt){
	int red   = (colorInt >> 16) & 0xff;
	int green = (colorInt >>  8) & 0xff;
	int blue  = (colorInt      ) & 0xff;
	red = checkOverBounds(red - ( red % 10));
	green = checkOverBounds(green - ( green % 10 ));
	blue = checkOverBounds(blue - ( blue % 10 ) );

	return (255 << 24) | (red << 16) | ( green << 8) | blue;
}

   /**
    * Returns the reduced noise color with relaxation. 
    * Gets smaller or bigger Integer value of channel color.
    * Checks that channel values are between 0-255.
    *
    * @param colorInt the color int
    * @param multiply the multiply
    * @return the reduced noise color with relaxation
    */
   private Integer getReducedNoiseColorWithRelaxation(int colorInt, int multiply){

	colorInt-= colorInt % 10;
	colorInt += multiply*10;
	return checkOverBounds(colorInt);


}



   private ArrayList<Integer> getRelaxedColors(int relaxation, int colorInt){
	ArrayList<Integer> integerList= new ArrayList<Integer>();
	int red_relaxed;
	int green_relaxed;
	int blue_relaxed;

	int red   = (colorInt >> 16) & 0xff;
	int green = (colorInt >>  8) & 0xff;
	int blue  = (colorInt      ) & 0xff;

	for(int i=-relaxation;i<= relaxation;i++){
		red_relaxed=getReducedNoiseColorWithRelaxation(red, i);
		green_relaxed=getReducedNoiseColorWithRelaxation(green, i);
		blue_relaxed=getReducedNoiseColorWithRelaxation(blue, i);
		int argb =  (255 << 24) | (red_relaxed << 16) | ( green_relaxed << 8) | blue_relaxed;
		integerList.add(argb);
	}
	return integerList;
}

   private void getSubImageAsByteArray(BufferedImage imageIn){
		this.subImagePixels = getImageAsByteArray(imageIn);

  }



	public String getThreadStatus(){
		return this.counterThread.getState().toString();
	}

	private WeightPoint getWeightPointWithBiggestDistance(MaxDistancePoint[] maxDistanceList){
	//	   double maxDistance=Double.MIN_VALUE;
		   MaxDistancePoint maxDistancePoint=null;
		   for (int i = 0; i < maxDistanceList.length; i++) {
			if(maxDistanceList[i] != null && (maxDistancePoint == null ||
					(maxDistancePoint != null && maxDistanceList[i].getDistance() > maxDistancePoint.getDistance()))){
				maxDistancePoint=maxDistanceList[i];
	
			}
	
		}
		   return maxDistancePoint;
	   }

   private WeightPoint getWeightPointWithBiggestWeightAtDistance(WeightPoint midPoint, ArrayList<WeightPoint> weightPointList, int distance){
	   WeightPoint maxWeightPoint=null;
	   for (Iterator<WeightPoint> iterator = weightPointList.iterator(); iterator.hasNext();) {
		WeightPoint weightPoint = (WeightPoint) iterator.next();
		if(weightPoint != null && (maxWeightPoint == null || maxWeightPoint != null && weightPoint.getWeight() >= maxWeightPoint.getWeight())){
			if(midPoint.distance(weightPoint) <= distance)
				maxWeightPoint = weightPoint;
		}
	}
	   if(maxWeightPoint == null)
		   return getWeightPointWithBiggestWeightAtDistance(midPoint, weightPointList, (int)(distance*2));
	   return maxWeightPoint;
   }

   /**
    * Checks is color at image position found from colorList. The color value is reduced
    *
    * @param r the r
    * @param c the c
    * @param w the w
    * @return true, if found the color
    */
   private boolean hasCellColor(int r, int c, int w){

	   int index = (c + r*w)*3;

	   int argb =  ( 255 << 24) | ((int)(this.originalImagePixels[index+2] & 0xFF) << 16) | ((int)(this.originalImagePixels[index + 1] & 0xFF) << 8) | ((int)this.originalImagePixels[index] & 0xFF);
	   int reducedNoiseArgb = getReducedNoiseColor(argb);
	   if(foundColor(reducedNoiseArgb))
		   return true; // found color
	   else
		   return false; // color not found
   }


   public void initThread(){
	this.setContinueCounting(true);
	this.cancelledInside=false;
	this.counterThread=new Thread(this, "counter");
}

   /**
    * Calculates is given searchPoint inside a triangle composed from given points.
    *
    * @param midPoint the one corner for triangle
    * @param searchPoint the point to be searched
    * @param second the second triangle point
    * @param third the third triangle point
    * @return true, if found point inside triangle
    */
   private boolean inside(Point midPoint, Point searchPoint, Point second, Point third){
	   int[] xList= new int[] {midPoint.x, second.x, third.x};
	   int[] yList= new int[] {midPoint.y, second.y, third.y};

	   Polygon slicePolygon=new Polygon(xList,yList,3);

	   return slicePolygon.contains(searchPoint);
   }

   public boolean isCancelledInside() {
		return cancelledInside;
	}

   private boolean isCircular(MaxDistancePoint[] maxPoints){
	   double maxDistance = Double.MIN_VALUE;
	   double minDistance = Double.MAX_VALUE;

	   for (int i = 0; i < maxPoints.length; i++) {
		MaxDistancePoint mdp= maxPoints[i];
		if(mdp == null)
			return false;
		if(mdp.getDistance()> maxDistance){
			maxDistance=mdp.getDistance();
		}
		if(mdp.getDistance() < minDistance){
			minDistance=mdp.getDistance();
		}
	}

	   if(maxDistance > minDistance*1.75)
		   return false;
	   return true;
   }

   private boolean isCircular(Point midPoint, ArrayList<WeightPoint> weightPointList){
	   MaxDistancePoint[] maxDistanceValues=getMaxDistanceRoundValues(midPoint, this.current_max_cell_size,weightPointList);
	   return isCircular(maxDistanceValues);
   }

   public boolean isCounting() {
	return continueCounting;
}

   /**
    * Divides the circle given by midPoint and circle size to 8 slices and calculates in which slice the searched point is located.
    *
    * @param midPoint the mid point
    * @param searchPoint the search point
    * @param circleSize the circle size
    * @return the int
    */
   private int isInsideWhichSlice(Point midPoint, Point searchPoint, int circleSize){
	   int circlePointX=Integer.MAX_VALUE;
	   int circlePointY= Integer.MAX_VALUE;
	   int vPointX=midPoint.x;
	   int vPointY=Integer.MAX_VALUE;
	   int hPointX=Integer.MAX_VALUE;
	   int hPointY=midPoint.y;
	   int quarter=0;

	   // go through locations separated to four slices
	   if(searchPoint.x >= midPoint.x){ // on the right from midpoint
		   circlePointX= (int) (midPoint.x + Math.sin(45)*circleSize);
		   hPointX=midPoint.x +circleSize; // calculate third point for triangle
		   quarter=1;
	   }
	   else{ // on the left from midpoint
		   circlePointX= (int) (midPoint.x - Math.sin(45)*circleSize);
		   hPointX=midPoint.x - circleSize; // calculate third point for triangle
		   quarter=3;
	   }

	   if(searchPoint.y >= midPoint.y){ // over midpoint y
		   circlePointY= (int) (midPoint.y + Math.sin(45)*circleSize);
		   vPointY=midPoint.y + circleSize; // calculate third point for triangle
		   if(quarter!=1)
			   quarter=4;
	   }
	   else{ // below midpoint y
		   circlePointY= (int) (midPoint.y - Math.sin(45)*circleSize);
		   vPointY=midPoint.y - circleSize; // calculate third point for triangle
		   if(quarter ==1)
			   quarter=2;

	   }

	   // go through the locations separated to 8 slices
	   Point circlePoint=new Point(circlePointX,circlePointY);

	   switch (quarter) {
	case 1:
		// first quarter -> check when split the quarter to two triangles:
		Point thirdPoint =new Point(vPointX,vPointY); // the middle point in quarter slice
		// check is inside the first triangle
		if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
			return 1;
		}
		else{ // check is inside the second triangle
			thirdPoint= new Point(hPointX,hPointY);
			if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
				return 2;
			}
			else{
				return -1; // not found
			}
		}

	case 2:
		// second quarter -> check when split the quarter to two triangles:
		thirdPoint =new Point(hPointX,hPointY); // the middle point in quarter slice
		// check is inside the first triangle
		if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
			return 3;
		}
		else{ // check is inside the second triangle
			thirdPoint= new Point(vPointX,vPointY);
			if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
				return 4;
			}
			else{
				return -1; // not found
			}
		}

	case 3:
		// third quarter -> check when split the quarter to two triangles:
		thirdPoint =new Point(vPointX,vPointY); // the middle point in quarter slice
		// check is inside the first triangle
		if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
			return 5;
		}
		else{ // check is inside the second triangle
			thirdPoint= new Point(hPointX,hPointY);
			if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
				return 6;
			}
			else{
				return -1; // not found
			}
		}
	case 4:
		// fourth quarter -> check when split the quarter to two triangles:
		thirdPoint =new Point(hPointX,hPointY); // the middle point in quarter slice
		// check is inside the first triangle
		if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
			return 7;
		}
		else{ // check is inside the second triangle
			thirdPoint= new Point(vPointX,vPointY);
			if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
				return 8;
			}
			else{
				return -1; // not found
			}
		}
	}

	 return -1; // not found


   }

   @Override
public void run() {
	try {
		// get 8 angle data from midpoint to edge of subImage
		if(continueCounting){
			ArrayList<ColorChannelVectors> angleVectors = get8Angles();
			if(angleVectors != null && angleVectors.size()>0){
				// get the colors
				LOGGER.fine("start calculating differentColors: "+angleVectors.size()+ "Colors: "+this.colorList.size());
				getDifferentColors(angleVectors);

				if(!continueCounting){
					abortExecution("No Colors", "Couldn't determine colors of picked cell. try another cell.");
					return;
				}

				if(this.current_colorList != null && this.current_colorList.size()>0){
					LOGGER.fine("start calculating coordinates: colorlist: "+current_colorList.size()+ "using gap: " +this.current_gap+ "min:"+this.global_min_coordinate_number_in_cell+ " max: "+this.current_max_coordinate_number_in_cell);
					// go through the image pixels of original image

					long time = System.currentTimeMillis();
					calculateCoordinates();
					long time2 = System.currentTimeMillis()-time;
					LOGGER.fine("end of calculating coordinates time: "+time2);
					if(!continueCounting){
						abortExecution("No Coordinates", "Couldn't calculate pixel coordinates for cells. Try again.");
						return;
					}
					if(this.current_finalCoordinates != null && this.current_finalCoordinates.size()>=this.global_min_coordinate_number_in_cell){ // number of Points should be more than 10
						LOGGER.fine("start clustering: "+current_finalCoordinates.size());
						time = System.currentTimeMillis();
						cleanCollectGroupsAndCluster();
						time2 = System.currentTimeMillis()-time;
						LOGGER.fine("end of clustering time: "+time2);
						if(!continueCounting){
							abortExecution("No Cells", "No any cells found.");
							return;
						}
						if(this.current_finalCentroidCoordinates != null && this.current_finalCentroidCoordinates.size()>0){
							this.finalCentroidCoordinates=this.current_finalCentroidCoordinates;

							this.max_cell_size=this.current_max_cell_size;
							this.min_cell_size=this.current_min_cell_size;
							this.max_coordinate_number_in_cell=this.current_max_coordinate_number_in_cell;
							this.taskManger.setSelectedMarkingLayerCoordinates(this.finalCentroidCoordinates);

							this.taskManger.updateSelectedMarkingPanelAndImageLayerInfos();
							// saved found colors
							this.colorList.clear();
							this.colorList.addAll(this.current_colorList);
							cancelInside();
							clean();
						}
						else{
							cancelInside();
							// inform user that couldn't locate cells

							abortExecution("No Cells", "No any cells found.");


							return;
						}

					}
					else{
						// inform user that couldn't get colors
						abortExecution("No Cells", "No any cells found.");
						return;
					}

				}
				else{
					// inform user that couldn't get colors
					abortExecution("No Colors", "Couldn't determine colors of picked cell. try another cell.");
					return;

				}
			}
			else{
				// inform user that couldn't get colors
				abortExecution("No Colors", "Couldn't determine colors of picked cell. try another cell.");

				return;
			}

		}
		else{
			abortExecution("Counting not started", "Counting didn't start. Try again.");

			return;
		}
		LOGGER.fine("ended counter thread");
	} catch (Exception e) {
		cancelInside();
		e.printStackTrace();
	}

}
   public void setCancelledInside(boolean cancelledInside) {
	this.cancelledInside = cancelledInside;
}


   public void setContinueCounting(boolean continueCounting) {
	this.continueCounting = continueCounting;
}


/*
   private void initClusteringMethods(){
 this.current_finalCentroidCoordinates=new ArrayList<Point>();

	   opticsLoader = new OpticsDataLoader();

		optics = new OPTICS();
		optics.setMinPts(this.minPoints);

		  kmeans = new KMeans();
		  kmeans.setIterationLimit(1000);
   }




   private ArrayList<ArrayList<Point2D>> clusterData(ArrayList<Point> pointListIn, int type){
		ArrayList<ArrayList<Point2D>> rlist=new ArrayList<ArrayList<Point2D>>();
		DataSet dataset = opticsLoader.loadData(pointListIn);
		int[] ass;
		switch(type){

		case ID.CLUSTER_KMEANS:
			ass=kmeans.cluster(dataset, (int[])null);
			break;

		default:
			ass = optics.cluster(dataset, (int[])null);
			break;
		}

		if(continueCounting){
			List<List<DataPoint>> clusterings = ClustererBase.createClusterListFromAssignmentArray(ass,dataset);
			//ArrayList<Point> centerCoordinateList=new ArrayList<Point>();
			for (int i = 0; i < clusterings.size(); i++) {
				List<DataPoint> dataList =clusterings.get(i);
				ArrayList<Point2D> pointList=new ArrayList<Point2D>();
				for (int j = 0; j < dataList.size(); j++) {
					DataPoint dataPoint = dataList.get(j);
				//	System.out.println("cluster "+i+ " datapoint x: "+dataPoint.getNumericalValues().get(0)+ " y: " +dataPoint.getNumericalValues().get(1));
					Point2D p = new Point2D(dataPoint.getNumericalValues().get(0), dataPoint.getNumericalValues().get(1));
					pointList.add(p);

				}
				if(!continueCounting)
					return null;

				rlist.add(pointList);

			}
		}
		return rlist;
   }

   private Point2D countCenterPoint2D(ArrayList<Point2D> pointList){

		Point2D centerPoint = Point2D.centroid(pointList);
	//	System.out.println("center:x: "+centerPoint.x()+ " y:"+centerPoint.y());
		return centerPoint;
	}

*/

   public void setManager(PreCountThreadManager pctm){
	this.pctm=pctm;
}



	public void setProgressBallDialog(ProgressBallsDialog pbd){
	}

	public void setSubImage(BufferedImage subImage){
		this.subImage=subImage;
	}

	public void startCounting(){
		this.counterThread.start();
	}

	private int[] startEndBinarySearch(ArrayList<Point> pointList, int lowerBoundValue, int upperBoundValue){
			 int imin=0;
			 int imax=pointList.size()-1;
			 int imid=(int)(imax-imin)/2;
			 int[] indexes=new int[] {-1,-1};
	
			 lowerLoop:
			 while(imax>=imin){
				 imid=(int)imin+(imax-imin)/2; // get midpoint
			//	 System.out.println("lower1: "+imid+" value: "+pointList.get(imid).x);
				 if(pointList.get(imid).x == lowerBoundValue){ // found lower bound, but has the one index smaller value smaller or same
					 if(imid == 0 || pointList.get(imid-1).x < lowerBoundValue){
						 // found the lowerBound
						 indexes[0]=imid;
						 break lowerLoop;
					 }
					 else{ // there are same values in smaller indexes of list
						 imax=imid-1;
					 }
				 }
				 else{
				//	 System.out.println("lower2: "+imid+" value: "+pointList.get(imid).x);
					 if(pointList.get(imid).x < lowerBoundValue){
					 	if(imid < pointList.size()-1){
				//	 		System.out.println("lower2.1: "+(imid+1)+" value: "+pointList.get(imid+1).x);
					 		if(pointList.get(imid+1).x >= lowerBoundValue){
					 			// found the lowerbound
					 			indexes[0]=imid+1;
					 			break lowerLoop;
					 		}
					 		else{
					 			imin=imid+1;
					 		}
					 	}
					 	else{
					 		return null; // list not containing given values
					 	}
				 }
				 else{
				//	 System.out.println("lower3: "+imid+" value: "+pointList.get(imid).x);
					 // pointList.get(imid).x > lowerBoundValue
					 if(imid > 0){
				//		 System.out.println("lower3.1: "+(imid-1)+" value: "+pointList.get(imid-1).x);
						 if(pointList.get(imid-1).x < lowerBoundValue){
							 indexes[0]=imid;
					 			break lowerLoop;
						 }
						 else{
							 imax=imid-1;
						 }
					 }
					 else{
						 // in first index return it->
						 indexes[0]=imid;
						 break lowerLoop;
					 }
				 }}
			 } // while loop for finding lowerBound
	
			 if(indexes[0] <0 || indexes[0] > pointList.size()-1)
				 return null;
	
			 imin=indexes[0]+1;
			 imax=pointList.size()-1;
			 upperLoop:
			 while(imax>=imin){
				 imid=(int)imin+(imax-imin)/2; // get midpoint
			//	 System.out.println("upper1: "+imid+" value: "+pointList.get(imid).x);
				 if(pointList.get(imid).x == upperBoundValue){
	
						 if(imid == pointList.size()-1 || pointList.get(imid+1).x > upperBoundValue){
							 indexes[1]=imid;
							 break upperLoop;
						 }
						 else{
							 imin=imid+1;
						 }
				 }
				 else {
			//		 System.out.println("upper2: "+imid+" value: "+pointList.get(imid).x);
					 if(pointList.get(imid).x < upperBoundValue){
					 if(imid< pointList.size()-1){
						 if(pointList.get(imid+1).x > upperBoundValue){
							 indexes[1]=imid;
							 break upperLoop;
						 }
						 else{
							 imin=imid+1;
						 }
					 }
					 else{
						 // in last index
						 indexes[1]=imid;
						 break upperLoop;
					 }
	
				 }else{
			//		 System.out.println("upper3: "+imid+" value: "+pointList.get(imid).x);
					 // pointList.get(imid).x > upperBoundValue
					 if(imid > indexes[0]){
						if(pointList.get(imid-1).x <= upperBoundValue){
							indexes[1]=imid-1;
							break upperLoop;
						}
						else{
							imax=imid-1;
						}
					 }
					 else{
						 return null; // not found because index not bigger than lowerBound index
					 }
				 }
	
				 }
			 }
	
			 return indexes;
		}

private int[] startEndBinarySearchWithWeightPoints(ArrayList<WeightPoint> pointList, int lowerBoundValue, int upperBoundValue){
	   ArrayList<Point> basicPointList= new ArrayList<Point>();
	   if(pointList != null){
		   for (Iterator<WeightPoint> iterator = pointList.iterator(); iterator.hasNext();) {
			WeightPoint weightPoint = (WeightPoint) iterator.next();
			basicPointList.add((Point)weightPoint);

		}

		   return startEndBinarySearch(basicPointList, lowerBoundValue, upperBoundValue);
	   }

	   return null;
   }

private class MovingAverage {
    private final Queue<Double> window = new LinkedList<Double>();
    private final int period;
    private double sum;

    public MovingAverage(int period) {
        assert period > 0 : "Period must be a positive integer";
        this.period = period;
    }

    public double getAvg() {
        if (window.isEmpty()) return 0; // technically the average is undefined
        return sum / window.size();
    }

    public void newNum(double num) {
        sum += num;
        window.add(num);
        if (window.size() > period) {
            sum -= window.remove();
        }
    }


}
}
