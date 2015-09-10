package operators;

import gui.ShadyMessageDialog;
import information.GridProperties;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import information.PositionedRectangle;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.security.sasl.SaslException;
import javax.swing.JFrame;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Handler for checking does xml file contains ImageLayer element and markingLayers related to it.
 * @author Antti Kurronen
 *
 */
public class MarkingsHandler extends DefaultHandler {

	private ArrayList<ImageLayer> imageLayerList = null;
	private ImageLayer singleImageLayer=null;
	private ImageLayer selectedImageLayer=null;
	private MarkingLayer selectedMarkingLayer=null;
	private GridProperties gridProperty=null;
//	private PositionedRectangle positionedRectangle=null;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * @param imageLayerList  ArrayList of ImageLayer objects which markings are searched
	 */
	public MarkingsHandler(ArrayList<ImageLayer> imageLayerList){
		this.imageLayerList=imageLayerList;
	}

	/**
	 * @param singleImageLayer One ImageLayer object which marking layers are searched
	 */
	public MarkingsHandler(ImageLayer singleImageLayer){
		this.singleImageLayer=singleImageLayer;
	}

	//private boolean isFoundImageLayer =false;
	//private boolean isImageName =false;
	//private boolean isMarkingLayer =false;
	private boolean isColor =false;
//	private boolean isCoordinate =false;
	private boolean isSingleCoordinate =false;
	private boolean isThickness =false;
	private boolean isOpacity =false;
	private boolean isSize =false;
	private boolean isShape =false;
//	private boolean isGrid =false;
	private boolean isGrid_on=false;
//	private boolean isLines =false;
	private boolean isX =false;
	private boolean isY =false;
//	private boolean isRectangles =false;
	private boolean isSelectedRec =false;
	private boolean isUnSelectedRec =false;
//	private boolean isUnSelectedRecNumbers =false;
//	private boolean isRec =false;
//	private boolean isNum=false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase(XMLtags.imagelayer)) {
            String i_name = attributes.getValue(XMLtags.imagename);
         // try to find image name from imageLayerList or single Imagelayer -> if found set as selectedImageLayer
            searchImageLayer(i_name);

        } else if ( this.selectedImageLayer != null) { // go only lower nodes if wanted ImageLayer was found and set as selected
            //set boolean values for fields, will be used in setting Employee variables

	         if (qName.equalsIgnoreCase(XMLtags.markinglayer)) {

	            //create new markinglayer to add information
	        	 this.selectedMarkingLayer = new MarkingLayer(attributes.getValue(XMLtags.markingname));
	          //  this.isMarkingLayer= true;

	        } else if (qName.equalsIgnoreCase(XMLtags.color)) {
	           isColor = true;
	        } else if (qName.equalsIgnoreCase(XMLtags.singlecoordinate)) {
	            isSingleCoordinate = true;
	        } else if (qName.equalsIgnoreCase(XMLtags.shape)) {
	            isShape = true;
	        }else if (qName.equalsIgnoreCase(XMLtags.size)) {
	            isSize = true;
	        }else if (qName.equalsIgnoreCase(XMLtags.opacity)) {
	            isOpacity = true;
	        }else if (qName.equalsIgnoreCase(XMLtags.thickness)) {
	            isThickness = true;
	        }else if (qName.equalsIgnoreCase(XMLtags.grid)) {
	        	this.gridProperty=new GridProperties(); // init GridProperties
	        }
	        else if (qName.equalsIgnoreCase(XMLtags.grid_on)) {
	            isGrid_on = true;
	        }
	        else if (qName.equalsIgnoreCase(XMLtags.x)) {
	            isX = true;
	        }
	        else if (qName.equalsIgnoreCase(XMLtags.y)) {
	            isY = true;
	        } /*
	        else if (qName.equalsIgnoreCase(XMLtags.selected_rec)) {
	            isSelectedRec = true;
	        }
	        else if (qName.equalsIgnoreCase(XMLtags.unselected_rec)) {
	            isUnSelectedRec = true;
	        }
*/
	        else if (qName.equalsIgnoreCase(XMLtags.rec)) {
	        	if(this.gridProperty != null){
	        		PositionedRectangle posRec=new PositionedRectangle(
        			getIntFromString(attributes.getValue(XMLtags.x)),
        			getIntFromString(attributes.getValue(XMLtags.y)),
        			getIntFromString(attributes.getValue(XMLtags.width)),
        			getIntFromString(attributes.getValue(XMLtags.height)),
        			getIntFromString(attributes.getValue(XMLtags.row)),
        			getIntFromString(attributes.getValue(XMLtags.column)),
        			getBooleanFromString(attributes.getValue(XMLtags.selected)));
	        		this.gridProperty.addSinglePositionedRectangle(posRec);
	        	}
	        }


        }
    }



    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(XMLtags.imagelayer)) {
            //Got all information for selectedImagelayer -> set null;
            this.selectedImageLayer = null;
          //  this.isFoundImageLayer=false;
        }
        else if (qName.equalsIgnoreCase(XMLtags.markinglayer)) {
        	 if(this.selectedImageLayer != null && this.selectedMarkingLayer != null){


             	this.selectedImageLayer.addMarkingLayer(this.selectedMarkingLayer);
             }

        	this.selectedMarkingLayer = null;
        }
        else if (qName.equalsIgnoreCase(XMLtags.grid)) {
       	 if(this.selectedMarkingLayer != null){
       		 this.selectedMarkingLayer.setGridProperties(this.gridProperty);
       		 this.gridProperty=null;
          }
        }/*
        else if (qName.equalsIgnoreCase(XMLtags.selected_rec)) {
          	 isSelectedRec=false;

           }
        else if (qName.equalsIgnoreCase(XMLtags.unselected_rec)) {
         	 isUnSelectedRec=false;
          }
*/
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (isColor) {
            //age element, set Employee age
            this.selectedMarkingLayer.setStringColor(new String(ch, start, length));
            isColor = false;
        } else if (isSingleCoordinate) {
            this.selectedMarkingLayer.addStringPoint(new String(ch, start, length));
            isSingleCoordinate = false;
        } else if (isShape) {
        	int shapeID = getIntFromString(new String(ch, start, length));
        	if(shapeID >0)
        		this.selectedMarkingLayer.setShapeID(shapeID);
            isShape = false;
        }else if (isSize) {
        	int size = getIntFromString(new String(ch, start, length));
        	if(size >0)
        		this.selectedMarkingLayer.setSize(size);
            isSize = false;
        }else if (isOpacity) {
        	float opacity = getFloatFromString(new String(ch, start, length));
        	if(opacity >0.0f)
        		this.selectedMarkingLayer.setOpacity(opacity);
            isOpacity = false;
        }else if (isThickness) {
        	int thickness= getIntFromString(new String(ch, start, length));
        	if(thickness > 0)
        		this.selectedMarkingLayer.setThickness(thickness);
            isThickness = false;
        }
        else if (isGrid_on) {
        	String value=new String(ch, start, length);
        	if(value.equalsIgnoreCase(XMLtags.value_true))
        		this.gridProperty.setGridON(true);
        	else if(value.equalsIgnoreCase(XMLtags.value_false))
        		this.gridProperty.setGridON(false);
            isGrid_on= false;
        }
        else if (isX) {
        	int x = getIntFromString(new String(ch, start, length));
        	this.gridProperty.addColumnLineX(x);
            isX= false;
        }
        else if (isY) {
        	int y = getIntFromString(new String(ch, start, length));
        	this.gridProperty.addRowLineY(y);
            isY= false;
        }
        /*
        else if (isRec) {
        		this.gridProperty.addSinglePositionedRectangle(getRectangleFromString(new String(ch, start, length)));

            isRec= false;
        }
         */

    }
/*
    private PositionedRectangle getRectangleFromString(String s){
    	try {
			if(s != null && s.length()>0 && s.contains(",")){
				String[] values=s.trim().split(",");
				if(values.length==6){
					int x=getIntFromString(values[0]);
					int y=getIntFromString(values[1]);
					int w=getIntFromString(values[2]);
					int h=getIntFromString(values[3]);
					int r=getIntFromString(values[4]);
					int c=getIntFromString(values[5]);
					if(x>=0 && y>=0 && w>0 && h>0 && r>0 && c>0)
						return new PositionedRectangle(x,y,w,h,r,c,isSelectedRec);
					else
						return null;

				}
				else
					return null;

			}
			else
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.warning("Rectangle not read from String");
			return null;
		}

    }
*/
    private boolean getBooleanFromString(String s){
    	if(s.equals(XMLtags.value_true))
    		return true;
    	else
    		return false;
    }

    private int getIntFromString(String s){
    	try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			LOGGER.fine("Error in converting string to int: " +e.getClass().toString() + " :" +e.getMessage());
			return -1;
		}
    }
    private Float getFloatFromString(String s){
    	try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			LOGGER.fine("Error in converting string to int: " +e.getClass().toString() + " :" +e.getMessage());
			return 1.0f; // if error happens 1.0 is good solution (gives 100 % opacity and 1.0 thickness)
		}
    }

    private void searchImageLayer(String i_name) throws SAXException{
    	try {
    		// selectedImageLayer should be already null,
    		// because in start it is null and after another ImageLayer node it will be set as null
    		selectedImageLayer=null;
			// go through imageLayerList
			if(imageLayerList != null && imageLayerList.size()>0){

				Iterator<ImageLayer> iIterator = this.imageLayerList.iterator();
				while(iIterator.hasNext()){
					ImageLayer il = (ImageLayer)iIterator.next();
					if(il.getImageFilePath().equals(i_name)){ // ImageLayerPath
						selectedImageLayer = il;
					}
				}
			}
			else{
				if(singleImageLayer.getImageFileName().equalsIgnoreCase(i_name)){ // ImageLayerPath
					LOGGER.fine("selected single imagelayer in markinghandler");
					selectedImageLayer=singleImageLayer;

				}
			}

		} catch (Exception e) {
			LOGGER.severe("Error in going trough imageLayerList " +e.getClass().toString() + " :" +e.getMessage());
			selectedImageLayer=null;
			throw new SAXException();
		}

    }

 /*
    /**
     * @param mName String MarkingLayer name which will be searched
     * @return boolean is name found from MarkingLayerlist of selectedImageLayer
     */
  /*  private boolean searchMarkingLayer(String mName){
    	try {
			if(this.selectedImageLayer != null && this.selectedImageLayer.getMarkingLayers() != null
					&& this.selectedImageLayer.getMarkingLayers().size()>0){
				Iterator<MarkingLayer> iIterator = this.selectedImageLayer.getMarkingLayers().iterator();
				while(iIterator.hasNext()){
					MarkingLayer ml = (MarkingLayer)iIterator.next();
					if(ml != null && ml.getLayerName().equalsIgnoreCase(mName)){
						return true;

					}
				}
			}
			return false;
		} catch (Exception e) {
			LOGGER.severe("Error in going trough MarkingLayerList: " +e.getClass().toString() + " :" +e.getMessage());
			return false;
		}

    }
 */
    public ArrayList<ImageLayer> getImageLayerList(){
    	return this.imageLayerList;
    }

    public ImageLayer getImageLayer(){
    	return this.singleImageLayer;
    }


}
