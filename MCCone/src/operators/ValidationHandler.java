package operators;

import gui.ShadyMessageDialog;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.security.sasl.SaslException;
import javax.swing.JFrame;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Handler for validating xml file. Parses the file, but doesn't save the information anywhere.
 * @author Antti Kurronen
 *
 */
public class ValidationHandler extends DefaultHandler {

	private final static Logger LOGGER = Logger.getLogger("MCCLogger");	
	
	/**
	 * @param Empty constructor
	 */
	public ValidationHandler(){
		
	}

	private boolean isColor =false;
	private boolean isCoordinate =false;
	private boolean isSingleCoordinate =false;
	private boolean isShape =false;
	
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
 
        if (qName.equalsIgnoreCase("imagelayer")) {
            String i_name = attributes.getValue("imagename");     
         // try to find image name from imageLayerList or single Imagelayer -> if found set as selectedImageLayer
         //   searchImageLayer(i_name); 

        } else  { // go only lower nodes if wanted ImageLayer was found and set as selected
            //set boolean values for fields, will be used in setting Employee variables
           
	         if (qName.equalsIgnoreCase("markinglayer")) { 
	        	       	 
	            //create new markinglayer to add information
	        	 String mn= (attributes.getValue("markingname"));	// just get attribute and don't do anything with it        	
	          //  this.isMarkingLayer= true;
	            
	        } else if (qName.equalsIgnoreCase("color")) {
	           isColor = true;
	        } else if (qName.equalsIgnoreCase("singlecoordinate")) {
	            isSingleCoordinate = true;
	        } else if (qName.equalsIgnoreCase("shape")) {
	            isShape = true;
	        }
        }
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("imagelayer")) {
            //do nothing
           
        }
        else if (qName.equalsIgnoreCase("markinglayer")) {
        	 // do nothing
             }      	
        }
    
    
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
 
        if (isColor) {
            //age element, set Employee age
            String color=(new String(ch, start, length));
            isColor = false;
        } else if (isSingleCoordinate) {
            String Point=(new String(ch, start, length));
            isSingleCoordinate = false;
        } else if (isShape) {
        	int shapeID = getIntFromString(new String(ch, start, length));
        	
            isShape = false;
        }
    }
    
    private int getIntFromString(String s){
    	try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			LOGGER.warning("Error in converting string to int: " +e.getClass().toString() + " :" +e.getMessage());
			return -1;
		}
    }
	
}
