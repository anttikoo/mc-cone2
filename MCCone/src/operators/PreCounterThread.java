package operators;

import information.ColorChannelVectors;
import information.ImageColorChannels;

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

public class PreCounterThread implements Runnable{
//	private ProgressBallsDialog progressBalls;
	private BufferedImage subImage;
	private BufferedImage originalImage;

	private Thread counterThread;
	private boolean continueCounting=true;
	private boolean cancelledInside=false;


	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private ArrayList<Integer> colorList;
	private byte[] originalImagePixels;
	private byte[] subImagePixels;
	private ArrayList<Point> finalCoordinates;
	private ArrayList<Point> finalCentroidCoordinates;
	private TaskManager taskManger;


	private PreCountThreadManager pctm;
	private ArrayList<Integer> current_colorList;

	private ArrayList<Point> current_finalCoordinates;
	private ArrayList<Point> copy_of_current_finalCoordinates;
	private ArrayList<Point> current_finalCentroidCoordinates;



	private int current_gap =2;
	private int min_distance_between_cells_boundaries=5;
	//private int minPoints=5;
	private int pixel_color_relaxation=3; // from 0 - 5 is ok
	private int max_coordinate_number_in_cell=Integer.MIN_VALUE;
//	private int current_min_coordinate_number_in_cell=Integer.MAX_VALUE;
	private int current_max_coordinate_number_in_cell=Integer.MIN_VALUE;
	private final int global_min_cell_diameter =10; // this is global minimum for cell diameter: 10 pixels
	private final int global_min_coordinate_number_in_cell =10; // this is global minimum for coordinate number
	private int max_cell_size=0; // only initial value which is changed when user picks bigger cells
	private int current_max_cell_size;
	private int current_min_cell_size;
	private int min_cell_size=Integer.MAX_VALUE;
	private double cellSizeMinScalingFactor=0.5;
	private double cellSizeMaxScalingFactor=1.5;
	private int maxCellNumberInCellGroup=5;




	public PreCounterThread(BufferedImage subImage, BufferedImage originalImage , TaskManager taskManager){
		this.taskManger=taskManager;

	//	this.progressBalls=pbd;
	//	this.progressBalls.initPreCounterThread(this);
		this.subImage=subImage;
		this.originalImage=originalImage;
		this.counterThread=new Thread(this, "counter");
		this.colorList=new ArrayList<Integer>();
		this.finalCoordinates = new ArrayList<Point>();
		this.finalCentroidCoordinates = new ArrayList<Point>();
	}

	public void initThread(){
		this.setContinueCounting(true);
		this.cancelledInside=false;
		this.counterThread=new Thread(this, "counter");
	}

	public String getThreadStatus(){
		return this.counterThread.getState().toString();
	}

	@Override
	public void run() {
		try {
			// get 8 angle data from midpoint to edge of subImage
			if(continueCounting){
				ArrayList<ColorChannelVectors> angleVectors = get8Angles();
				if(angleVectors != null && angleVectors.size()>0){
					// get the colors
					LOGGER.fine("start calculating differentColors: "+angleVectors.size());
					getDifferentColors(angleVectors);

					if(!continueCounting){
						abortExecution("No Colors", "Couldn't determine colors of picked cell. try another cell.");
						return;
					}

					if(this.current_colorList != null && this.current_colorList.size()>0){
						LOGGER.fine("start calculating coordinates: colorlist: "+current_colorList.size()+ "using gap: " +this.current_gap+ "min:"+this.global_min_coordinate_number_in_cell+ " max: "+this.current_max_coordinate_number_in_cell);
						// go through the image pixels of original image
						//calculateCoordinatesStrict();

						long time = System.currentTimeMillis();
						calculateCoordinatesFlexible();
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
							//stop();
							//clusterDataWithOPTICS();
							if(!continueCounting){
								abortExecution("No Cells", "No any cells found.");
								return;
							}
							if(this.current_finalCentroidCoordinates != null && this.current_finalCentroidCoordinates.size()>0){
								LOGGER.fine("finalCentroids: "+this.current_finalCentroidCoordinates.size());
							//	this.colorList=this.current_colorList;
		//						this.finalCoordinates=this.current_finalCoordinates;
								this.finalCentroidCoordinates=this.current_finalCentroidCoordinates;

								this.max_cell_size=this.current_max_cell_size;
								this.min_cell_size=this.current_min_cell_size;
							//	this.min_coordinate_number_in_cell=this.current_min_coordinate_number_in_cell;
								this.max_coordinate_number_in_cell=this.current_max_coordinate_number_in_cell;
								this.taskManger.setSelectedMarkingLayerCoordinates(this.finalCentroidCoordinates);
							//	this.taskManger.setSelectedMarkingLayerCoordinates(this.current_finalCoordinates);

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
			// TODO Auto-generated catch block
			cancelInside();
			e.printStackTrace();
		}

	}



	private void abortExecution(String title, String message){
		if(cancelledInside)
			this.taskManger.showMessageToUser(title, message);


		clean();
	}




	public void startCounting(){
	//	progressBalls.showDialog();

		this.counterThread.start();

	}

	public void setManager(PreCountThreadManager pctm){
		this.pctm=pctm;
	}

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

	public void cancelOutside(){
		this.continueCounting=false;

		if(this.pctm != null)
			this.pctm.cancelCounting();


	}

	public void setProgressBallDialog(ProgressBallsDialog pbd){
	}
/*
	private void calculateCentroids(){
		if(this.finalCoordinates != null && this.finalCoordinates.size()>0){
			ArrayList<Point> pickedPointList=new ArrayList<Point>();
			LOGGER.fine("before pick "+this.finalCoordinates.size());
			Point pickedPoint = this.finalCoordinates.get(0);
			pickedPointList.add(pickedPoint);
			this.finalCoordinates.remove(pickedPoint);
			LOGGER.fine("after pick "+this.finalCoordinates.size());

			// calculate point at maximum distance of cell size


		}


	}
*/
	public void setSubImage(BufferedImage subImage){
		this.subImage=subImage;
	}

	private void clean(){
		this.cancelledInside=false;
	//	this.finalCoordinates=null;
		this.current_finalCoordinates=null;
		this.current_colorList=null;
		this.current_finalCentroidCoordinates=null;
		this.current_max_cell_size=0;
	//	this.current_min_coordinate_number_in_cell=Integer.MAX_VALUE;
	//	this.current_max_coordinate_number_in_cell=Integer.MIN_VALUE;
	}

	public boolean isCounting() {
		return continueCounting;
	}

	public void setContinueCounting(boolean continueCounting) {
		this.continueCounting = continueCounting;
	}

	private ArrayList<ColorChannelVectors> get8Angles() throws Exception{
		ArrayList<ColorChannelVectors> angleVectors=new ArrayList<ColorChannelVectors>();
		ImageColorChannels channels = convertImageToChannels(this.subImage);
		if(channels != null){


			for(int i=-1;i<=1;i++){ // left-right direction
				for(int j=-1;j<=1;j++){ // up_down direction
					if(!(i==0 && j==0)){
						angleVectors.add(calculateColorsForVectors(channels, i, j));
						//calculateColorsForVectors(channels, i, j);
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
					//	colorVectors.setHasAlpha(true);
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

				// smooth data with moving average

		            MovingAverage ma = new MovingAverage(channels.getHeight()/10);
		            int i=0;
		        //    System.out.println("red");
		            for (int x : red) {
		                ma.newNum(x);
		              //  System.out.println(""+i+" \t" + x + "\t" + ma.getAvg());
		                red_ma[i]=ma.getAvg();
		                i++;
		                if(!continueCounting)
							return null;
		            }
		        //    System.out.println();

		            i=0;
		            ma = new MovingAverage(channels.getHeight()/10);
		        //    System.out.println("green");
		            for (int x : green) {
		                ma.newNum(x);
		            //    System.out.println(""+i+" \t" + x + "\t" + ma.getAvg());
		                green_ma[i]=ma.getAvg();
		                i++;
		                if(!continueCounting)
							return null;
		            }
		      //      System.out.println();

		            i=0;
		            ma = new MovingAverage(channels.getHeight()/10);
		        //    System.out.println("blue");
		            for (int x : blue) {
		                ma.newNum(x);
		             //   System.out.println(""+i+" \t" + x + "\t" + ma.getAvg());
		                blue_ma[i]=ma.getAvg();
		                i++;
		                if(!continueCounting)
							return null;
		            }
		      //      System.out.println();
		    colorVectors.setOriginalData(alpha, red, green, blue);
		    colorVectors.setMoveAveragedData(alpha_ma, red_ma, green_ma, blue_ma);
		return colorVectors;
	}

	private void getDifferentColors(ArrayList<ColorChannelVectors> angleVectors) throws Exception{
		this.current_colorList=new ArrayList<Integer>();
		if(this.colorList.size()>0)
			this.current_colorList.addAll(this.colorList);
	//	this.current_min_coordinate_number_in_cell=this.min_coordinate_number_in_cell;
		this.current_max_coordinate_number_in_cell=this.max_coordinate_number_in_cell;
		this.current_max_cell_size=this.max_cell_size;
		this.current_min_cell_size=this.min_cell_size;


		if(angleVectors != null && angleVectors.size()>0){
		//	ArrayList<ColorCounts> colorCountList=new ArrayList<ColorCounts>();
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
					//	collectColors(colorCountList, colorInt);
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

			int gap_candidate=1;
			doLoop:
			do{
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
/*
	private int countGap(){
		double area = Math.PI*Math.pow(this.current_min_cell_size/2, 2); // count area of cell with minimum size
		return (int) Math.sqrt(area/this.global_min_coordinate_number_in_cell); // count gap that the smallest cell will have pixel checking enough
	}
*/
	private int countCoordinatesInCell(double radius, int gap_candidate){
		double area = Math.PI*Math.pow(radius, 2);
		return (int)(area/Math.pow(gap_candidate,2));

	}

	private void addColorsIfNotFound(ArrayList<Integer> newColorList){

		for (Iterator<Integer> iterator = newColorList.iterator(); iterator.hasNext();) {
			int colorInt = (int) iterator.next();
			if(!this.current_colorList.contains(colorInt)){
				this.current_colorList.add(colorInt);
			}
		}

	}

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
				//addSingleColorIfNotFound(argb);

			}

		}
	}

	}



	/*
	private void collectColors(ArrayList<ColorCounts> colorCountList, int colorInt) throws Exception{
		boolean addedColor=false;
		Iterator<ColorCounts> cIterator= colorCountList.iterator();
		while(cIterator.hasNext()){
			ColorCounts singleColorCounts=cIterator.next();
			if(colorInt == singleColorCounts.getColorInt()){
				// found the color
				singleColorCounts.increaseCount();
				addedColor=true;

			}
		}

		if(!addedColor){
			ColorCounts newColorCounts =new ColorCounts(colorInt);
			colorCountList.add(newColorCounts);
		}
	}
	*/



   private ImageColorChannels convertImageToChannels(BufferedImage imageIn) {

      try {
    	  BufferedImage image = convert(imageIn, BufferedImage.TYPE_3BYTE_BGR);
		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	//	final byte[] pixels = (byte[])image.getRaster().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
		  final int width = image.getWidth();
		  final int height = image.getHeight();
		  ImageColorChannels channels=new ImageColorChannels(height, width);
		  final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		  LOGGER.fine("types: " +pixels[0]+ " "+ pixels[1]+" "+pixels[2]);

		  // the image is not 4 bit bufferedImage -> make that way it is!!!!

		//  int[][] result = new int[height][width];
		  if (hasAlphaChannel) {
			  LOGGER.fine("using alpha channel");
			  channels.setUseAlpha(true);
		     final int pixelLength = 4;
		     for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
		       // int argb = 0;

		        channels.addAlpha(row,col,(((int) pixels[pixel] & 0xff) << 24)); // alpha
		        channels.addBlue(row,col,((int) pixels[pixel + 1] & 0xff)); // blue
		        channels.addGreen(row,col,(((int) pixels[pixel + 2] & 0xff) << 8)); // green
		        channels.addRed(row,col,(((int) pixels[pixel + 3] & 0xff) << 16)); // red
		       // result[row][col] = argb;
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
		       // int argb = 0;
		       // argb += -16777216; // 255 alpha
		    	 /*   	 int b=((int) (pixels[pixel + 2] & 0xFF) ) ;
		    	 int g=(( (int) (pixels[pixel + 1]  & 0xFF)));
		    	 int r=((int) (pixels[pixel]  & 0xFF)) ;

		    	 int r=((int) (pixels[pixel + 2] & 0xFF) ) ;
		    	 int g=(( (int) (pixels[pixel + 1]  & 0xFF)));
		    	 int b=((int) (pixels[pixel]  & 0xFF)) ;
		    	 */
		   // 	 LOGGER.fine("rgb "+r+ " " +g+ " "+b);
		   /*     channels.addBlue(row,col,((int) pixels[pixel] & 0xff)); // blue
		        channels.addGreen(row,col,(((int) pixels[pixel + 1] & 0xff) << 8)); // green
		        channels.addRed(row,col,(((int) pixels[pixel + 2] & 0xff) << 16)); // red
		     */
		        channels.addRed(row,col,((int) (pixels[pixel + 2] & 0xFF) ));  // red
		        channels.addGreen(row,col, (( (int) (pixels[pixel + 1]  & 0xFF) )) ); // green
		        channels.addBlue(row,col,((int) (pixels[pixel]  & 0xFF)));  // blue
		       // result[row][col] = argb;
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

   private void getOriginalImageAsByteArray(BufferedImage imageIn){
		this.originalImagePixels = getImageAsByteArray(imageIn);

   }

   private void getSubImageAsByteArray(BufferedImage imageIn){
		this.subImagePixels = getImageAsByteArray(imageIn);

  }

   private byte[] getImageAsByteArray(BufferedImage imageIn){
	   BufferedImage image = convert(imageIn, BufferedImage.TYPE_3BYTE_BGR); // convert to 3byte bgr image
		return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

   }



   private void calculateCoordinatesFlexible() throws Exception{
	   getOriginalImageAsByteArray(this.originalImage);

	   this.current_finalCoordinates=new ArrayList<Point>();
	   if(this.finalCoordinates != null && this.finalCoordinates.size()>0)
		   this.current_finalCoordinates.addAll(this.finalCoordinates);

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

   private boolean hasCellColor(int r, int c, int w){

	   int index = (c + r*w)*3;
	 // int argb2 =  (255 << 24) | ((int)(pixels[index+2] & 0xFF) << 16)|((int)(pixels[index+1] & 0xFF)<<8)|((int)pixels[index] & 0xFF);

	   int argb =  ( 255 << 24) | ((int)(this.originalImagePixels[index+2] & 0xFF) << 16) | ((int)(this.originalImagePixels[index + 1] & 0xFF) << 8) | ((int)this.originalImagePixels[index] & 0xFF);
	   int reducedNoiseArgb = getReducedNoiseColor(argb);
	 //  if(Collections.binarySearch(this.colorList, argb) >=0)
	   if(foundColor(reducedNoiseArgb))
		   return true; // found color
	   else
		   return false; // color not found
   }

   private boolean foundColor(int colorInt){

	  if(Collections.binarySearch(this.current_colorList, colorInt) >=0)
		  return true;
	  return false;
   }

   private void cleanCollectGroupsAndCluster() throws Exception{
	   this.copy_of_current_finalCoordinates=new ArrayList<Point>();
	   this.copy_of_current_finalCoordinates.addAll(this.current_finalCoordinates);
	   this.current_finalCentroidCoordinates=new ArrayList<Point>();
	   Collections.sort(this.copy_of_current_finalCoordinates, new CoordinateComparator());
	 //  initClusteringMethods();
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

   private void createWeightedPointGroup(ArrayList<Point> pointGroups) throws Exception{
	 //  ArrayList<Point> collectedPoints=new ArrayList<Point>();
	   // create initial weighted list
	   ArrayList<WeightPoint> weightPointList = createWeightPointList(pointGroups);

	   if(weightPointList.size()>=this.global_min_coordinate_number_in_cell){
		   System.out.println("Max cell coordinates: "+current_max_coordinate_number_in_cell);
			int rounds =0;
			Collections.sort(weightPointList, new WeightPointComparator());
			outerLoop:
			while(weightPointList.size()>=global_min_coordinate_number_in_cell && rounds<=5){

				System.out.print("round: " +rounds);
				 Point midPoint =calculateCentroid(weightPointList);
				 // count circular data
				 MaxDistancePoint[] maxDistanceValues=getMaxDistanceRoundValues(midPoint, this.current_max_cell_size, weightPointList);
				 if(weightPointList.size()<= this.current_max_coordinate_number_in_cell && isCircular(maxDistanceValues)){
							// one cell
					 System.out.println(" - one cell- ");
					   this.current_finalCentroidCoordinates.add(midPoint);
					   break outerLoop;
				   }
					else{// possible several cells

						WeightPoint w = getWeightPointWithBiggestDistance(maxDistanceValues);
						if(w==null)
						w = weightPointList.get(weightPointList.size()-1);

						WeightPoint b =null;
						if(w != null){
							// find nearest point with biggest weight
							int counter=0;
							do{
								counter++;
								if(b != null)
									w=b;
								b = getWeightPointWithBiggestWeightAtDistance(w, weightPointList, this.current_min_cell_size/2);
							}while((w.x != b.x || w.y != b.y) && counter<100);

						//	WeightPoint w = weightPointList.get(weightPointList.size()-1);
							if(w.x == b.x && w.y == b.y){ // p has the biggest weight -> near at center of cell
						//		if(w != null && w.getWeight()>0){ // p has the biggest weight -> near at center of cell
								int radius=this.current_min_cell_size/2;
								Point p = calculateCentroid(getPointsInside(w.getPoint(), radius, weightPointList));
								if(p == null)
									p=w.getPoint();


								ArrayList<WeightPoint> selectedPointsForCell=new ArrayList<WeightPoint>();
								ArrayList<WeightPoint> candidatePointList=getPointsInside(p, radius, weightPointList);
								if(candidatePointList != null)
								System.out.println(" cand1: " +candidatePointList.size());
								if(candidatePointList != null && candidatePointList.size()>= 1 &&
										candidatePointList.size() <=this.current_max_coordinate_number_in_cell){

									if(candidatePointList.size() >=global_min_coordinate_number_in_cell && isCircular(p, candidatePointList)){ //
										selectedPointsForCell.addAll(candidatePointList);
									}

										double averagePointsPerArea = ((double)candidatePointList.size())/(Math.PI*(double)radius*(double)radius);
										double pointsPerArea=averagePointsPerArea;
										int pointsBefore= candidatePointList.size();


										// search the correct cell size from min size to max size
							//			while(earlierPointsPerRadius < pointsPerRadius*2 && radius <=this.current_max_cell_size/2){
										candidateLoop:
										while(pointsPerArea*1.5 > averagePointsPerArea && radius <=this.current_max_cell_size/2){
										//	averagePointsPerArea = ((double)candidatePointList.size())/(Math.PI*(double)radius*(double)radius);
										//	int radiusBefore=radius;
											radius+=this.current_gap;
											if(candidatePointList != null && candidatePointList.size()>=this.global_min_coordinate_number_in_cell)
												p=calculateCentroid(candidatePointList);
											candidatePointList=getPointsInside(p, radius, weightPointList);
											if(candidatePointList==null){
												System.out.println("cand2: empty");
												break;
											}
											else{
												pointsPerArea=calculateOuterRadiusArea(candidatePointList.size()-pointsBefore, radius, radius-this.current_gap);
												if(pointsPerArea*5 > averagePointsPerArea){ // not reached to cell boundary yet
													averagePointsPerArea=((double)candidatePointList.size())/(Math.PI*(double)radius*(double)radius);
													pointsBefore=candidatePointList.size();
													System.out.println(" cand2: " +candidatePointList.size());

													if(candidatePointList.size() >=global_min_coordinate_number_in_cell &&
														candidatePointList.size() <= this.current_max_coordinate_number_in_cell && isCircular(p, candidatePointList)){ // is points in circle
														System.out.println("adding to selectedPoints: "+candidatePointList.size());
														selectedPointsForCell.clear();
														selectedPointsForCell.addAll(candidatePointList);

													}
												}
												else{
													break candidateLoop;
												}
											}
										}

										if(selectedPointsForCell != null && selectedPointsForCell.size()>0)
											System.out.println("selected point size: " +selectedPointsForCell.size());

										if(selectedPointsForCell != null && selectedPointsForCell.size()>=global_min_coordinate_number_in_cell &&
										selectedPointsForCell.size()<=current_max_coordinate_number_in_cell){

											midPoint = calculateCentroid(selectedPointsForCell);
											this.current_finalCentroidCoordinates.add(midPoint);
										}
										weightPointList.removeAll(candidatePointList); // remove selected points
										Collections.sort(weightPointList, new WeightPointComparator());



								}
							}

					}
				}
				rounds++;
				System.out.println();
			}


	   }else{
		   LOGGER.fine("WeightPoint list too small");
	   }
	 //  return collectedPoints;
   }

   private double calculateOuterRadiusArea(int countNumber, int radius_bigger, int radius_smaller){
	   double areaBigger= Math.PI*(double)radius_bigger*(double)radius_bigger;
	   double areaSmaller= Math.PI*(double)radius_smaller*(double)radius_smaller;
	   return ((double)countNumber)/(areaBigger-areaSmaller);
   }



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

   public boolean isCancelledInside() {
		return cancelledInside;
	}



	public void setCancelledInside(boolean cancelledInside) {
		this.cancelledInside = cancelledInside;
	}

	public ArrayList<WeightPoint> getPointsInside(Point mPoint, int distance, ArrayList<WeightPoint> weightPointList){
		Point2D midPoint = new Point2D(mPoint.x,mPoint.y);
	//	Circle2D circle = new Circle2D(midPoint, r);
		  ArrayList<WeightPoint> pointsInside=new ArrayList<WeightPoint>();
		  	//binarysearch
			int lowerBoundValue=Math.max(0,midPoint.getAsInt().x-distance);
			int upperBoundValue=midPoint.getAsInt().x+distance;
			int[] bounds=startEndBinarySearchWithWeightPoints(weightPointList, lowerBoundValue, upperBoundValue);
			if(bounds != null && bounds[0] >=0 && bounds[1] <weightPointList.size() && bounds[0] <= bounds[1]){
				for (int i = bounds[0]; i <= bounds[1]; i++) {
					WeightPoint wPoint=weightPointList.get(i);
				//	if(circle.contains(wPoint.getPoint2D())){
					if(midPoint.distance(wPoint.getPoint2D()) <= distance*1.01){	// 1% bigger distance to be sure of collecting the midPoint to the result
						pointsInside.add(wPoint);
					}
				}
			}
				/*
		   for (Iterator<WeightPoint> iterator = weightPointList.iterator(); iterator.hasNext();) {
			WeightPoint weightPoint = (WeightPoint) iterator.next();
			if(weightPoint != null ){

			}
		} */

		   return pointsInside;

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


   private ArrayList<WeightPoint> createWeightPointList(ArrayList<Point> pointGroups){
	   ArrayList<WeightPoint> weightPointList = new ArrayList<WeightPoint>();
	   if(pointGroups != null && pointGroups.size()>0)
		   /*
		  for (Iterator<Point> iterator = pointGroups.iterator(); iterator.hasNext();) {
			Point point = (Point) iterator.next();
			weightPointList.add(new WeightPoint(point));
			*/
			 for (int j=0;j<pointGroups.size()-1;j++) {
					Point point = pointGroups.get(j);
					WeightPoint wpoint = new WeightPoint(point);
					int lowerBoundValue=Math.max(0,(int)wpoint.getPoint().x-this.current_max_cell_size/2);
					int upperBoundValue=(int)(wpoint.getPoint().x+this.current_max_cell_size/2);
					int[] bounds=startEndBinarySearch(pointGroups, lowerBoundValue, upperBoundValue);
					if(bounds != null && bounds[0] >=0 && bounds[1] <pointGroups.size() && bounds[0] <= bounds[1]){
						for (int i = bounds[0]; i <= bounds[1]; i++) {
							double distance = wpoint.getPoint().distance((Point)pointGroups.get(i));
							if( distance < this.current_max_cell_size/2 && distance > 0){
								   wpoint.increaseWeight(1/distance);

							}
						}

					weightPointList.add(wpoint);
					}
	   }

	   return weightPointList;
   }

   private boolean isCircular(Point midPoint, ArrayList<WeightPoint> weightPointList){
	   MaxDistancePoint[] maxDistanceValues=getMaxDistanceRoundValues(midPoint, this.current_max_cell_size,weightPointList);
	   return isCircular(maxDistanceValues);
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

   private int isInsideWhichSlice(Point midPoint, Point searchPoint, int circleSize){
	   int circlePointX=Integer.MAX_VALUE;
	   int circlePointY= Integer.MAX_VALUE;
	   int vPointX=midPoint.x;
	   int vPointY=Integer.MAX_VALUE;
	   int hPointX=Integer.MAX_VALUE;
	   int hPointY=midPoint.y;
	   int quarter=0;

	   if(searchPoint.x >= midPoint.x){ // on the right from midpoint
		   circlePointX= (int) (midPoint.x + Math.sin(45)*circleSize);
		   hPointX=midPoint.x +circleSize;
		   quarter=1;
	   }
	   else{ // on the left from midpoint
		   circlePointX= (int) (midPoint.x - Math.sin(45)*circleSize);
		   hPointX=midPoint.x - circleSize;
		   quarter=3;
	   }

	   if(searchPoint.y >= midPoint.y){ // over midpoint y
		   circlePointY= (int) (midPoint.y + Math.sin(45)*circleSize);
		   vPointY=midPoint.y + circleSize;
		   if(quarter!=1)
			   quarter=4;
	   }
	   else{ // below midpoint y
		   circlePointY= (int) (midPoint.y - Math.sin(45)*circleSize);
		   vPointY=midPoint.y - circleSize;
		   if(quarter ==1)
			   quarter=2;

	   }

	   Point circlePoint=new Point(circlePointX,circlePointY);

	   switch (quarter) {
	case 1:

		Point thirdPoint =new Point(vPointX,vPointY);
		if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
			return 1;
		}
		else{
			thirdPoint= new Point(hPointX,hPointY);
			if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
				return 2;
			}
			else{
				return -1;
			}
		}

	case 2:
		thirdPoint =new Point(hPointX,hPointY);
		if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
			return 3;
		}
		else{
			thirdPoint= new Point(vPointX,vPointY);
			if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
				return 4;
			}
			else{
				return -1;
			}
		}

	case 3:
		thirdPoint =new Point(vPointX,vPointY);
		if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
			return 5;
		}
		else{
			thirdPoint= new Point(hPointX,hPointY);
			if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
				return 6;
			}
			else{
				return -1;
			}
		}
	case 4:
		thirdPoint =new Point(hPointX,hPointY);
		if(inside(midPoint,searchPoint,circlePoint, thirdPoint)){
			return 7;
		}
		else{
			thirdPoint= new Point(vPointX,vPointY);
			if(inside(midPoint,searchPoint,circlePoint,thirdPoint)){
				return 8;
			}
			else{
				return -1;
			}
		}
	}


	 return -1;


   }

   private boolean inside(Point midPoint, Point searchPoint, Point second, Point third){
	   int[] xList= new int[] {midPoint.x, second.x, third.x};
	   int[] yList= new int[] {midPoint.y, second.y, third.y};

	   Polygon slicePolygon=new Polygon(xList,yList,3);

	   return slicePolygon.contains(searchPoint);
   }

/*
   private void createWeightedPointGroup2(ArrayList<Point> pointGroups) throws Exception{
	   ArrayList<Point> collectedPoints=new ArrayList<Point>();
	   // create initial weighted list
	   ArrayList<WeightPoint> weightPointList = new ArrayList<WeightPoint>();
	   if(pointGroups != null && pointGroups.size()>0){

		  for (Iterator<Point> iterator = pointGroups.iterator(); iterator.hasNext();) {
			Point point = (Point) iterator.next();
			weightPointList.add(new WeightPoint(point));
		}
	   }
	   if(weightPointList.size()>1){

		   // calculate weights 100 times
		   for(int i=0;i<weightIterations*weightPointList.size();i++){
			   // randomize list
			   double maxWeight=0;
			   Collections.shuffle(weightPointList);

			   WeightPoint wpoint = weightPointList.get(0);


			   for(int j=1;j< weightPointList.size(); j++){
				   WeightPoint p2=weightPointList.get(j);
				   if(p2.getWeight()>maxWeight)
					   maxWeight=p2.getWeight();
				   double distance = wpoint.distance(p2);
				   if( distance < this.current_max_cell_size/2){
					   wpoint.increaseWeight(p2.getWeight()/distance);
				   }
			   }
			   if(wpoint.getWeight()>maxWeight)
				   maxWeight=wpoint.getWeight();

			   for (Iterator<WeightPoint> iterator = weightPointList.iterator(); iterator.hasNext();) {
				WeightPoint weightPoint = (WeightPoint) iterator.next();
				weightPoint.decreaseWeight(maxWeight);

			}
		   }

		   if(weightPointList.size()>0){
			   // sortList
			   Collections.sort(weightPointList, new WeightPointComparator());
			   double weightAtMinPointIndex=0;
			   // calculate weight at index minPoints
			   if(weightPointList.size()>=minPoints*3)
				   weightAtMinPointIndex = weightPointList.get(minPoints*3-1).getWeight();
			   else
				   weightAtMinPointIndex = weightPointList.get(weightPointList.size()-1).getWeight();

			   // collect points which have big weight and are far enough from each other
			   double weightAtPoint=0;


			   do{
				   WeightPoint wp= weightPointList.get(0);
				   weightAtPoint=wp.getWeight();
				   if(weightAtPoint >= weightAtMinPointIndex){
					   // remove all neighbors that inside cell size
					   ArrayList<WeightPoint> removePointList=new ArrayList<WeightPoint>();
					   weightPointList.remove(wp);
					   for (Iterator<WeightPoint> iterator = weightPointList.iterator(); iterator.hasNext();) {
						WeightPoint wp2=iterator.next();
						if(wp.distance(wp2)< this.current_max_cell_size){
							//weightPointList.remove(wp2);
							removePointList.add(wp2);
						}
					}
					   weightPointList.removeAll(removePointList);
					   //collectedPoints.add(wp.getPoint());
					   this.current_finalCentroidCoordinates.add(wp.getPoint());
				   }
			   }while(weightAtPoint >= weightAtMinPointIndex && weightPointList.size()>0);
		   }
	   }else{
		   LOGGER.fine("WeightPoint list too small");
	   }
	 //  return collectedPoints;
   }

   private boolean checkAndApproveMidPoints(ArrayList<ArrayList<Point2D>> pointLists){
	   boolean allApproved=true;

	   ArrayList<Point2D> midPointList = new ArrayList<Point2D>();
	   for (Iterator<ArrayList<Point2D>> iterator = pointLists.iterator(); iterator.hasNext();) {
			ArrayList<Point2D> pList = (ArrayList<Point2D>) iterator.next();

			Point2D midP = countCenterPoint2D(pList);
			midPointList.add(midP);
		//	this.current_finalCentroidCoordinates.add(midP);
			/*
			for (Iterator<Point2D> iterator2 = pList.iterator(); iterator2.hasNext();) {
				Point2D point = (Point2D) iterator2.next();

			}
			*/
/*	   }

	   if(midPointList.size()>1){
		   Point2D middlePoint= countCenterPoint2D(midPointList);

		   for (Iterator iterator = midPointList.iterator(); iterator.hasNext();) {
			Point2D point2d = (Point2D) iterator.next();
			if(point2d.distance(middlePoint) > this.current_max_cell_size*0.75){
				allApproved=false;
			}

		}
		   if(allApproved)
			   this.current_finalCentroidCoordinates.add(middlePoint.getAsInt());

	   }


	   return allApproved;
   }
*/

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
			/*	if(neighbours.size() > this.current_max_coordinate_number_in_cell*2){ // too many coordinates -> too big cell or not cell at all
			//		this.copy_of_current_finalCoordinates.removeAll(neighbours);
			//		return new ArrayList<Point>();
				}
		*/	}
	   }

/*
	   for (Iterator<Point> iterator = this.copy_of_current_finalCoordinates.iterator(); iterator.hasNext();) {
			Point point = (Point) iterator.next();
			if(point.distance(p) <this.current_max_cell_size/this.min_distance_between_cells_boundaries){
				neighbours.add(point);
				if(neighbours.size() > this.current_max_coordinate_number_in_cell*2){
					this.copy_of_current_finalCoordinates.removeAll(neighbours);
					return new ArrayList<Point>();
				}
			}
		}
*/
	 //  long endtime = System.currentTimeMillis()-start;
	//	LOGGER.fine("elapsed in time: "+endtime);

	// remove found Points -> not trying to find them later
		this.copy_of_current_finalCoordinates.removeAll(neighbours);
	//	if(neighbours.size()>0)
	  //LOGGER.fine("neighbors: "+neighbours.size());
	   return neighbours;

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

   private BufferedImage convert(BufferedImage src, int bufImgType) {
	    BufferedImage img= new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
	    Graphics2D g2d= img.createGraphics();
	    g2d.drawImage(src, 0, 0, null);
	    g2d.dispose();
	    return img;
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

	private Integer getReducedNoiseColorWithRelaxation(int colorInt, int multiply){

		colorInt-= colorInt % 10;
		colorInt += multiply*10;
		return checkOverBounds(colorInt);


	}

	private Integer getReducedNoiseColor(int colorInt){
		int red   = (colorInt >> 16) & 0xff;
		int green = (colorInt >>  8) & 0xff;
		int blue  = (colorInt      ) & 0xff;
		red = checkOverBounds(red - ( red % 10));
		green = checkOverBounds(green - ( green % 10 ));
		blue = checkOverBounds(blue - ( blue % 10 ) );

		return (255 << 24) | (red << 16) | ( green << 8) | blue;
	}

	private int checkOverBounds(int colorValue){
		if(colorValue<0)
			return 0;
		if(colorValue>255)
			return 250;
		return colorValue;
	}

private class MovingAverage {
	    private final Queue<Double> window = new LinkedList<Double>();
	    private final int period;
	    private double sum;

	    public MovingAverage(int period) {
	        assert period > 0 : "Period must be a positive integer";
	        this.period = period;
	    }

	    public void newNum(double num) {
	        sum += num;
	        window.add(num);
	        if (window.size() > period) {
	            sum -= window.remove();
	        }
	    }

	    public double getAvg() {
	        if (window.isEmpty()) return 0; // technically the average is undefined
	        return sum / window.size();
	    }


	}
/*
   private class ColorCounts{
	   private int colorInt;
	   private int count;
	   private ColorCounts(int color){
		   this.setColor(color);
		   setCount(1);

	   }
	public void setColor(int color) {
		this.colorInt= color;
	}
	public void setCount(int count) {
		this.count = count;
	}

	private int checkOverBounds(int colorValue){
		if(colorValue<0)
			return 0;
		if(colorValue>255)
			return 255;
		return colorValue;
	}


   }
   */
}
