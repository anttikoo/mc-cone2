package operators;

import gui.GUI;
import information.ImageLayer;
import information.InformationCenter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLreadManager {
private SAXParserFactory saxParserFactory;
private SAXParser saxParser;
private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	public XMLreadManager(){
		initSAXparser();
	}

	private void initSAXparser(){
		saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(true);
		saxParserFactory.setNamespaceAware(false);
		try{
		saxParser = saxParserFactory.newSAXParser();

		} catch (ParserConfigurationException | SAXException e) {
	        e.printStackTrace();
	    }
	}

	public ImageLayer getMarkingsOfXML(File xmlFile, ImageLayer imageLayer){
		//SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	    try {
	     //   SAXParser saxParser = saxParserFactory.newSAXParser();
	        MarkingsHandler handler = new MarkingsHandler(imageLayer);
	        XMLReader reader = saxParser.getXMLReader();
	        reader.setEntityResolver(new EntityResolver() {
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
    	LOGGER.fine("publicID: "+publicId + " systemID: "+systemId);
        if (systemId.contains("layers.dtd")) {
        //	LOGGER.fine("went to get inputsource dtd");
        	return new InputSource(GUI.class.getResourceAsStream("/information/dtd/layers.dtd"));

        }
        else{
        	LOGGER.warning("Reading Markings, but not found dtd file:");
        	return null;
        }
    }
});
	        reader.setErrorHandler(new XMLerrorHandler());
	        reader.setContentHandler(handler);
	        reader.parse(new InputSource(xmlFile.getAbsolutePath()));
	        //saxParser.parse(xmlFile, handler);
	        //Get processed ImageLayer -> may contain markings imported from xml file
	       return  handler.getImageLayer();

	    } catch (SAXException | IOException e) {
	    	LOGGER.severe("Error in importing markings for single ImageLayers " +e.getClass().toString() + " :" +e.getMessage());
	        return null;
	    }
	}

public ArrayList<ImageLayer> getMarkingsOfXML(File xmlFile, ArrayList<ImageLayer> imageLayerList){
	  //SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	    try {
	     //   SAXParser saxParser = saxParserFactory.newSAXParser();
	        MarkingsHandler handler = new MarkingsHandler(imageLayerList);
	        XMLReader reader = saxParser.getXMLReader();
	        XMLerrorHandler errorHandler=new XMLerrorHandler();
	        reader.setEntityResolver(new EntityResolver() {
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
    	LOGGER.fine("publicID: "+publicId + " systemID: "+systemId);
        if (systemId.contains("layers.dtd")) {
        //	LOGGER.fine("went to get inputsource dtd");
        	return new InputSource(GUI.class.getResourceAsStream("/information/dtd/layers.dtd"));

        }
        else{
        	return null;
        }
    }
});
	        reader.setErrorHandler(errorHandler);
	        reader.setContentHandler(handler);
	        reader.parse(new InputSource(xmlFile.getAbsolutePath()));
	        // saxParser.parse(xmlFile, handler);
	        //Get list of ImageLayers which may contain imported markings
	        if(!errorHandler.isErrorsFound())
	        	return handler.getImageLayerList();
	        else
	        	return null;

	    } catch (SAXException | IOException e) {
	    	LOGGER.severe("Error in importing markings for array of ImageLayers " +e.getClass().toString() + " :" +e.getMessage());
	    	return null;
	    }
}
	    /**
	     * Method for validate xml file.
	     * @param xmlFile The File which is validated.
	     * @return true if file is valid, false otherwise.
	     */
	    public boolean isFileValid(File xmlFile){
	  	  //SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	  	    try {
	  	     //   SAXParser saxParser = saxParserFactory.newSAXParser();
	  	        ValidationHandler handler = new ValidationHandler();
	  	        XMLReader reader = saxParser.getXMLReader();

	  	        XMLerrorHandler errorHandler=new XMLerrorHandler();
		        reader.setEntityResolver(new EntityResolver() {
		            @Override
		            public InputSource resolveEntity(String publicId, String systemId)
		                    throws SAXException, IOException {
		            	LOGGER.fine("publicID: "+publicId + " systemID: "+systemId);
		                if (systemId.contains("layers.dtd")) {
		                //	LOGGER.fine("went to get inputsource dtd");
		                	return new InputSource(GUI.class.getResourceAsStream("/information/dtd/layers.dtd"));

		                }
		                else{
		                	LOGGER.fine("not found dtd");
		                	return null;
		                }
		            }
		        });
	  	        reader.setErrorHandler(errorHandler);
	  	        reader.setContentHandler(handler);
	  	        reader.parse(new InputSource(xmlFile.getAbsolutePath()));

	  	        // saxParser.parse(xmlFile, handler);
	  	        //return boolean is valid file
	  	        LOGGER.fine("validated the file, found errors: "+errorHandler.isErrorsFound());
	  	       return !errorHandler.isErrorsFound();

	  	    } catch (SAXException | IOException e) {
	  	    	LOGGER.severe("Error in validating xml-file" +e.getClass().toString() + " :" +e.getMessage());
	  	    	return false;
	  	    }

	}

	    public boolean isStreamValid(ByteArrayOutputStream bStream){
		  	  //SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		  	    try {
		  	     //   SAXParser saxParser = saxParserFactory.newSAXParser();
		  	        ValidationHandler handler = new ValidationHandler();
		  	        XMLReader reader = saxParser.getXMLReader();

		  	        XMLerrorHandler errorHandler=new XMLerrorHandler();


			        reader.setEntityResolver(new EntityResolver() {
			            @Override
			            public InputSource resolveEntity(String publicId, String systemId)
			                    throws SAXException, IOException {

			                if (systemId.contains("layers.dtd")) {

			                	return new InputSource(getClass().getResourceAsStream("/information/dtd/layers.dtd"));

			                }
			                else{
			                	return null;
			                }
			            }
			        });

		  	        reader.setErrorHandler(errorHandler);
		  	        reader.setContentHandler(handler);
		  	        reader.parse(new InputSource(new ByteArrayInputStream(bStream.toByteArray())));
		  	        // saxParser.parse(xmlFile, handler);
		  	        //return boolean is valid file
		  	      LOGGER.fine("validated the stream, found errors: "+errorHandler.isErrorsFound());
		  	       return !errorHandler.isErrorsFound();

		  	    } catch (SAXException | IOException e) {
		  	    	LOGGER.severe("Error in Checking validation of stream: " +e.getClass().toString() + " :" +e.getMessage());
		  	    	return false;
		  	    }

		}


	    public boolean foundImageLayer(String xmlFileName, ArrayList<String> imageLayerNameList){
	    	  //SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		    try {
		     //   SAXParser saxParser = saxParserFactory.newSAXParser();
		        SearchImageLayersHandler searchHandler = new SearchImageLayersHandler(imageLayerNameList);
		        XMLReader reader = saxParser.getXMLReader();
		        XMLerrorHandler errorHandler=new XMLerrorHandler();
		        reader.setEntityResolver(new EntityResolver() {
	    @Override
	    public InputSource resolveEntity(String publicId, String systemId)
	            throws SAXException, IOException {
	    	LOGGER.fine("publicID: "+publicId + " systemID: "+systemId);
	        if (systemId.contains("layers.dtd")) {
	        //	LOGGER.fine("went to get inputsource dtd");
	        	return new InputSource(GUI.class.getResourceAsStream("/information/dtd/layers.dtd"));

	        }
	        else{
	        	return null;
	        }
	    }
	});
		        reader.setErrorHandler(errorHandler);
		        reader.setContentHandler(searchHandler);
		        reader.parse(new InputSource(xmlFileName));
		        // saxParser.parse(xmlFile, handler);
		        //Get list of ImageLayers which may contain imported markings
		        if(!errorHandler.isErrorsFound())
		        	return searchHandler.isFoundImageLayer();
		        else
		        	return false;

		    } catch (SAXException | IOException e) {
		    	LOGGER.severe("Error in searching ImageLayers!" + e.getMessage());
		    	return false;
		    }
	    }

}
