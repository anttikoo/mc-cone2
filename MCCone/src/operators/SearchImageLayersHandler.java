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
 * XML Handler for checking does xml file contains ImageLayer element and markingLayers related to it.
 * @author Antti Kurronen
 *
 */
public class SearchImageLayersHandler extends DefaultHandler {

	private ArrayList<String> imageLayerNameList = null;
	private ImageLayer singleImageLayer=null;
	private ImageLayer selectedImageLayer=null;
	private MarkingLayer selectedMarkingLayer=null;
	private boolean foundImageLayer=false;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * @param imageLayerList  ArrayList of ImageLayer objects which markings are searched
	 */
	public SearchImageLayersHandler(ArrayList<String> imageLayerNameList){
		this.imageLayerNameList=imageLayerNameList;
	}

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("imagelayer")) {
            String i_name = attributes.getValue("imagename").trim();
         // try to find image name from imageLayerList or single Imagelayer -> if found set as selectedImageLayer
            searchImageLayer(i_name);

        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	//do nothing
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
    	// do nothing
    }



    private void searchImageLayer(String i_name) throws SAXException{
    	try {
			// go through imageLayerList
			if(imageLayerNameList != null && imageLayerNameList.size()>0){

				Iterator<String> iIterator = this.imageLayerNameList.iterator();
				while(iIterator.hasNext()){
					String layerName = (String)iIterator.next();
					if(layerName.equals(i_name)){ // ImageLayerPath
						this.setFoundImageLayer(true);
					}
				}
			}


		} catch (Exception e) {
			LOGGER.severe("Error in going trough imageLayerList " +e.getClass().toString() + " :" +e.getMessage());
			selectedImageLayer=null;
			throw new SAXException();
		}

    }

	public boolean isFoundImageLayer() {
		return foundImageLayer;
	}

	public void setFoundImageLayer(boolean foundImageLayer) {
		this.foundImageLayer = foundImageLayer;
	}

}
