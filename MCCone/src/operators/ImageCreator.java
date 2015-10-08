package operators;

import gui.Color_schema;
import gui.GUI;
import gui.file.Utils;
import gui.panels.GridPanel;
import gui.panels.MarkingPanel;
import gui.saving.ImageSet.SingleDrawImagePanel;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import managers.TaskManager;
import com.sun.media.jai.codec.TIFFEncodeParam;

public class ImageCreator implements Runnable {

	private double sizeMultiplier;
	private TaskManager taskManager;

	//for creating ImageSet in thread
	private int gap;
	private int rows;
	private int columns;
	private ArrayList<SingleDrawImagePanel> sdpList;
	private Dimension imageDimension
	;private Dimension singleImageDimension;
	private String path;
	private boolean continueCreating=false;


	private boolean imageSetCreatedSuccessfully=false;
	private Thread imageSetCreatorThread;

	public ImageCreator(double sizeMultiply, TaskManager taskManager){
		this.taskManager=taskManager;
		this.sizeMultiplier=sizeMultiply;
	}

	public ArrayList<Integer> createImage(ImageLayer imageLayer, ArrayList<Integer> mLayerIDlist, String imagePath) throws Exception{
		try {
			ArrayList<Integer>  drawnLayers = new ArrayList<Integer>();
			if(imageLayer.getImageFilePath() != null){
				File imageFile=new File(imageLayer.getImageFilePath());

				BufferedImage bi=taskManager.createImageFile(imageFile);

				if(bi != null){
					singleImageDimension=new Dimension(bi.getWidth(),bi.getHeight());
					ArrayList<MarkingLayer> layersToDraw=getPrintingLayerList(imageLayer, mLayerIDlist);
					if(layersToDraw != null && layersToDraw.size()>0){
						int maxShapeSize=getMaxShapeSize(layersToDraw);
						BufferedImage biggerImage= new BufferedImage(bi.getWidth(),bi.getHeight()+maxShapeSize+15,BufferedImage.TYPE_INT_RGB);


						Graphics2D g2d = biggerImage.createGraphics();
						g2d.setColor(Color_schema.grey_100);
						g2d.fillRect(0, 0, biggerImage.getWidth(), biggerImage.getHeight());
						g2d.drawImage(bi,0,0,bi.getWidth(),bi.getHeight(),0,0,bi.getWidth(),bi.getHeight(),Color_schema.grey_100,null);



						int label_x =maxShapeSize;
						int label_y= (int) (bi.getHeight()+maxShapeSize/2+5);
						for (Iterator<MarkingLayer> iterator = layersToDraw.iterator(); iterator.hasNext();) {
							MarkingLayer markingLayer = (MarkingLayer) iterator.next();
							drawnLayers.add(drawSingleMarkingLayer(g2d, markingLayer, new Point(label_x,label_y)));
							double markingSize=(markingLayer.getSize()*sizeMultiplier);
							label_x+=(int)markingSize;
							label_x=drawLayerLabel(g2d, label_x, label_y, markingLayer.getLayerName(), markingLayer.getColor(), new Font("Consolas", Font.BOLD, 25));
							if(label_x>bi.getWidth()-100){
								label_x=maxShapeSize;
								label_y=(int)(bi.getHeight()-maxShapeSize/2-5);
							}
						}

						if(writeToFile(biggerImage, imagePath))
							return drawnLayers;
						else
							return null;


					}
					//g2d.dispose();
				}
			}
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public BufferedImage createBufferedImage(ImageLayer imageLayer, ArrayList<Integer> mLayerIDlist) {
		try {

			ArrayList<Integer>  drawnLayers = new ArrayList<Integer>();
			if(imageLayer.getImageFilePath() != null){
				File imageFile=new File(imageLayer.getImageFilePath());


				BufferedImage bi=ImageIO.read(imageFile);

				if(bi != null){

					ArrayList<MarkingLayer> layersToDraw=getPrintingLayerList(imageLayer, mLayerIDlist);
					if(layersToDraw != null && layersToDraw.size()>0){
						int maxShapeSize=getMaxShapeSize(layersToDraw);
						BufferedImage biggerImage= new BufferedImage(bi.getWidth(),bi.getHeight()+maxShapeSize+15,BufferedImage.TYPE_INT_ARGB);


						Graphics2D g2d = biggerImage.createGraphics();
						g2d.setColor(Color_schema.grey_100);
						g2d.fillRect(0, 0, biggerImage.getWidth(), biggerImage.getHeight());
						g2d.drawImage(bi,0,0,bi.getWidth(),bi.getHeight(),0,0,bi.getWidth(),bi.getHeight(),Color_schema.grey_100,null);



						int label_x =maxShapeSize;
						int label_y= (int) (bi.getHeight()+maxShapeSize/2+5);
						for (Iterator<MarkingLayer> iterator = layersToDraw.iterator(); iterator.hasNext();) {
							MarkingLayer markingLayer = (MarkingLayer) iterator.next();
							drawnLayers.add(drawSingleMarkingLayer(g2d, markingLayer, new Point(label_x,label_y)));
							double markingSize=(markingLayer.getSize()*sizeMultiplier);
							label_x+=(int)markingSize;
							label_x=drawLayerLabel(g2d, label_x, label_y, markingLayer.getLayerName(), markingLayer.getColor(), new Font("Consolas", Font.BOLD, 25));
							if(label_x>bi.getWidth()-100){
								label_x=maxShapeSize;
								label_y=(int)(bi.getHeight()-maxShapeSize/2-5);
							}
						}


						return biggerImage;

					}
					//g2d.dispose();



				}
			}
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	}

	public void initImageSetProperties(int gap, int rows, int columns, ArrayList<SingleDrawImagePanel> sdpList, Dimension imageDimension, String path){
		this.gap=gap;
		this.rows=rows;
		this.columns=columns;
		this.sdpList=sdpList;
		this.imageDimension=imageDimension;
		this.path=path;
		this.continueCreating=true;
		this.imageSetCreatorThread=new Thread(this, "imageset");
	}

	public boolean startImageSetCreatorThread(){
		this.setContinueCreating(true);
		this.imageSetCreatorThread.start();
		return isImageSetCreatedSuccessfully();
	}

	public boolean createImageSet(){

		try {
			gap =(int)(gap*this.sizeMultiplier); // gap scaled to resultsize


			BufferedImage biggerImage= new BufferedImage(imageDimension.width,imageDimension.height,BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = biggerImage.createGraphics();
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, biggerImage.getWidth(), biggerImage.getHeight());

			int x=gap, y=gap;
			for(int r = 1; r<= rows;r++){
				if(!continueCreating)
					return false;

				int maxHeightOfPanel=0, y_recent = y;
				for (int c = 1; c <= columns; c++) {
					if(!continueCreating)
						return false;
					SingleDrawImagePanel sdp = getSDPatPosition(r, c, sdpList);
					if(sdp.getImage() != null){
						int titlePaneHeight=(int)(sdp.getTitlePanelHeight()*this.sizeMultiplier);
				//		Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute,Object>();
				//		setFontAttributes(map, sdp);
				//		AttributedString aTitle = new AttributedString(sdp.getTitle(), map);
						Font drawingFont = new Font(sdp.getFont().getFamily(), sdp.getFont().getStyle(), (int)(sdp.getFont().getSize()*sizeMultiplier));
						// draw title
						drawImagetitle(g2d, x, y_recent,sdp.getTitle(), Color_schema.dark_20, drawingFont);
					//	g2d.drawString(aTitle.getIterator(), x, y_recent);
						g2d.setPaint(Color.white);
						y_recent+=titlePaneHeight;

						int imageWidth = (int)(sdp.getScaledImageDimension().getWidth()*sizeMultiplier);
						int imageHeight= (int)(sdp.getScaledImageDimension().getHeight()*sizeMultiplier);

						BufferedImage bi = sdp.getScaledImage(new Dimension(imageWidth, imageHeight));
					//	BufferedImage bi = sdp.getImage();
						g2d.drawImage(bi,x,y_recent,x+bi.getWidth(),y_recent+ bi.getHeight(),0,0,bi.getWidth(),bi.getHeight(),null);

						if(titlePaneHeight+bi.getHeight() > maxHeightOfPanel)
							maxHeightOfPanel=titlePaneHeight+bi.getHeight();

					}
					else{
						if(sdp.getPanelSize().getHeight()*sizeMultiplier > maxHeightOfPanel){
							maxHeightOfPanel=(int)(sdp.getPanelSize().getHeight()*sizeMultiplier);
						}

					}

					x+=(int)(sdp.getPanelSize().getWidth()*sizeMultiplier)+gap; // set position to next column
					y_recent=y; // set y position to upper side of the row
				}

				y+=maxHeightOfPanel+gap; // go one grid lower
				x=gap; // start in left side
			}
			if(continueCreating)
				return writeToFile(biggerImage, path);
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}





	}

	private void setFontAttributes(Hashtable<TextAttribute, Object> map, SingleDrawImagePanel sdp){
		map.put(TextAttribute.FAMILY, sdp.getFont().getFamily());
		map.put(TextAttribute.SIZE, (int)(sdp.getFont().getSize()*this.sizeMultiplier));

	}


	private SingleDrawImagePanel getSDPatPosition(int r, int c, ArrayList<SingleDrawImagePanel> sdpList){
		Iterator<SingleDrawImagePanel> sdpIterator=sdpList.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp=(SingleDrawImagePanel)sdpIterator.next();
			if(sdp.getGridPosition() != null && sdp.getGridPosition()[0] == r && sdp.getGridPosition()[1] == c)
				return sdp;
		}
		return null;
	}

	private boolean writeToFile(BufferedImage bi, String imagePath) {
		try {
			String extension=Utils.getExtension(imagePath);//getExtension(imagePath);
			if(extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("tif")){
				return writeToTIff(bi, imagePath);
			}
			else{
				return writeToNormalFile(bi, imagePath, extension);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}




	}

	private boolean writeToNormalFile(BufferedImage bi, String imagePath, String extension){

	    try {

			File outputfile = new File(imagePath);
			if(!outputfile.exists())
				outputfile.createNewFile();
			ImageIO.write(bi, extension, outputfile);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private boolean  writeToJPG(BufferedImage bi, String path){

		try {
			TIFFEncodeParam params = new TIFFEncodeParam();
			FileOutputStream fos = new FileOutputStream(path);
			JAI.create("encode", bi, fos, "TIFF",params);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private boolean  writeToTIff(BufferedImage bi, String path){

		try {
			TIFFEncodeParam params = new TIFFEncodeParam();
			FileOutputStream fos = new FileOutputStream(path);
			JAI.create("encode", bi, fos, "TIFF",params);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private int drawLayerLabel(Graphics2D g2d, int x, int y, String label, Color color, Font font){
	//	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
	   //   Font font = new Font("Consolas", Font.BOLD, 25);
	      g2d.setFont(font);
	 //     String label = mLayer.getLayerName();
	      FontMetrics fontMetrics = g2d.getFontMetrics();
	      int stringWidth = fontMetrics.stringWidth(label);
	      int stringHeight = fontMetrics.getAscent();
	      int y_b=(int)(y+stringHeight/2);
	      g2d.setPaint(color);
	      g2d.drawString(label, x,y_b);
	      return x+stringWidth+30;


	}

	private int drawImagetitle(Graphics2D g2d, int x, int y, String label, Color color, Font font){
	//	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
	   //   Font font = new Font("Consolas", Font.BOLD, 25);
	      g2d.setFont(font);
	 //     String label = mLayer.getLayerName();
	      FontMetrics fontMetrics = g2d.getFontMetrics();
	      int stringWidth = fontMetrics.stringWidth(label);
	      int stringHeight = fontMetrics.getAscent();
	      int y_b=(int)(y+stringHeight-5); // lowest characters are at least 5 pixels above the image
	      g2d.setPaint(color);
	      g2d.drawString(label, x,y_b);
	      return x+stringWidth+30;


	}

	private int getMaxShapeSize(ArrayList<MarkingLayer> mLayers){
		int max_size=0;
		for (Iterator<MarkingLayer> iterator = mLayers.iterator(); iterator.hasNext();) {
			MarkingLayer markingLayer = (MarkingLayer) iterator.next();
			if(markingLayer.getSize()>max_size)
				max_size=markingLayer.getSize();
		}
		return max_size;
	}

	private int drawSingleMarkingLayer(Graphics2D g2d, MarkingLayer ml, Point labelMarkPoint) throws Exception{
		if(ml.isDrawGridToImage())
			drawGrid(g2d, ml);
		int size=(int)(ml.getSize()*sizeMultiplier);
		int thickness= (int)(ml.getThickness()*sizeMultiplier);
		if(thickness<1)
			thickness=1;
		ShapeDrawer shapeDrawer=new ShapeDrawer(ml, size, thickness, ml.getOpacity(), ml.getColor());

	//	g2d.setPaint(ml.getColor()); // already set
		Iterator<Point> coordinateIterator=null;
		if(ml.isDrawGridToImage()){
			coordinateIterator =ml.getCoordinateList().iterator();
		}
		else
			coordinateIterator =ml.getCoordinateListIgnoringGrid().iterator();



		 switch(ml.getShapeID())
         {
             case ID.SHAPE_OVAL:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawOval(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawOval(g2d, labelMarkPoint.x, labelMarkPoint.y);
                break;
             case ID.SHAPE_DIAMOND:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawDiamond(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawDiamond(g2d, labelMarkPoint.x, labelMarkPoint.y);
                 break;
             case ID.SHAPE_PLUS:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawPlus(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawPlus(g2d, labelMarkPoint.x, labelMarkPoint.y);
                 break;
             case ID.SHAPE_CROSS:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawCross(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawCross(g2d, labelMarkPoint.x, labelMarkPoint.y);
                 break;
             case ID.SHAPE_SQUARE:
	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawSquare(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawSquare(g2d, labelMarkPoint.x, labelMarkPoint.y);
                break;

             case ID.SHAPE_TRIANGLE:
		     	while(coordinateIterator.hasNext()){
		     		Point c = coordinateIterator.next();
		     		shapeDrawer.drawTriangle(g2d, c.x, c.y);
		     	}
		     	shapeDrawer.drawTriangle(g2d, labelMarkPoint.x, labelMarkPoint.y);
		     	break;
             default : // circle

	     		while(coordinateIterator.hasNext()){
	     			Point c = coordinateIterator.next();
	     			shapeDrawer.drawCircle(g2d, c.x, c.y);
	     		}
	     		shapeDrawer.drawCircle(g2d, labelMarkPoint.x, labelMarkPoint.y);
             	break;
         }
		 return ml.getLayerID();
		// g2d.dispose();
	}

	private void drawGrid(Graphics2D gd, MarkingLayer mLayer) throws Exception{
		if(mLayer.isGridON() && singleImageDimension != null){
			GridPanel gridPanel=new GridPanel();
			gridPanel.setGridProperties(mLayer.getGridProperties());
			gridPanel.setBounds(0, 0, singleImageDimension.width, singleImageDimension.height);
			gridPanel.drawGrid(gd);

		}
	}


	private ArrayList<MarkingLayer> getPrintingLayerList(ImageLayer il, ArrayList<Integer> selected_mLayers){
		ArrayList<MarkingLayer> layersToDraw = new ArrayList<MarkingLayer>();
		for (Iterator<Integer> iterator = selected_mLayers.iterator(); iterator.hasNext();) {
			int selected_mLayerID = (int) iterator.next();

			Iterator<MarkingLayer> mIterator= il.getMarkingLayers().iterator();
			while(mIterator.hasNext()){
				MarkingLayer ml=mIterator.next();
				if(ml.getLayerID()==selected_mLayerID){
					layersToDraw.add(ml);
				}


			}

		}
		return layersToDraw;

	}

	  private String getExtension(String str) throws Exception{

			// Handle null case specially.
			if (str == null) return null;

			// Get position of last '.'.
			int pos = str.lastIndexOf(".");

			// If there wasn't any '.' just return the string as is.
			if (pos == -1) return null;

			// Otherwise return the string, up to the dot.
			return str.substring(pos+1, str.length()).trim();

	    }

	@Override
	public void run() {
		this.imageSetCreatedSuccessfully= createImageSet();
		this.setContinueCreating(false);

	}

	public boolean isImageSetCreatedSuccessfully() {
		return imageSetCreatedSuccessfully;
	}

	public boolean isContinueCreating() {
		return continueCreating;
	}

	public void setContinueCreating(boolean continueCreating) {
		this.continueCreating = continueCreating;
	}


}
