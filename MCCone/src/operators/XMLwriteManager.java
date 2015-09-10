package operators;

import gui.file.XMLfilter;
import information.GridProperties;
import information.ID;
import information.ImageLayer;
import information.LayersOfPath;
import information.MarkingLayer;
import information.PositionedRectangle;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ReplicateScaleFilter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;



/**
 * Writes brand new or modifies existing xml-file. Uses StaX for reading + writing the file and validation is checked by SAX
 * @author Antti Kurronen
 *
 */
public class XMLwriteManager {



	//private final String layers ="layers";
/*	private final String imagelayer ="imagelayer";
	private final String markinglayer ="markinglayer";
	private final String markingname ="markingname";
	private final String imagename ="imagename";
	private final String coordinates ="coordinates";
	private final String color ="color";
	private final String singlecoordinate ="singlecoordinate";
	private final String shape ="shape";
	private final String opacity ="opacity";
	private final String thickness ="thickness";
	private final String size ="size";
	private final QName qnameLayers=new QName(XMLtags.layers);
	private final QName qnameImageLayer=new QName(imagelayer);
	private final QName qnameImageName=new QName(imagename);
	private final QName qnameMarkingName=new QName(markingname);
	private final QName qnameMarkingLayer=new QName(markinglayer);
	private final QName qnameCoordinates=new QName(coordinates);
	private final QName qnameColor=new QName(color);
	private final QName qnameSingleCoordinate=new QName(singlecoordinate);
	private final QName qnameShape=new QName(shape);
	private final QName qnameOpacity=new QName(opacity);
	private final QName qnameThickness=new QName(thickness);
	private final QName qnameSize=new QName(size);
*/	private StartElement se;
	private EndElement ee;
	private Characters ce;


	private XMLEventReader xer;
	private XMLEventWriter xew;
	private XMLEvent end;
	private XMLEvent tab;
	private XMLEventFactory xef;
	private LayersOfPath layersOfPath;
	private String tempFileName ="temp.xml";
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private Attribute att;
	private int savingType;
	private ArrayList<Integer>unsavedMarkingLayers;
	private ArrayList<Integer>successfullySavedMarkingLayers;
	private ByteArrayOutputStream byteStream;
	private XMLreadManager rm;





	/**
	 * Constructor for modifying existing file.
	 * @param xmlFile the File to be modified.
	 */
	public XMLwriteManager(){
		this.layersOfPath=null;
		this.savingType=ID.UNDEFINED;
		this.unsavedMarkingLayers=new ArrayList<Integer>();
		this.successfullySavedMarkingLayers=new ArrayList<Integer>();
		rm = new XMLreadManager();

	}

	/**
	 * Starts the writing of given ImageLayer- objects into given XML-file. If file exist, the information is append or overWritten depending of
	 * existence of ImageLayers and MarkingLayers in XML-file.
	 * @param layersOfPathIn LayersOfPath, which contains the XML-file path where from read xml-data and where to save the data.
	 * @return Boolean value true if saving was made, false otherwise.
	 */
	public boolean startWritingProcess(LayersOfPath layersOfPathIn){
		try {
			this.layersOfPath =layersOfPathIn;
			this.unsavedMarkingLayers=new ArrayList<Integer>();
			if(this.layersOfPath != null && this.layersOfPath.getXmlpath() != null){
				File f= new File(this.layersOfPath.getXmlpath());


				// file already exists and it is right format -> update this file
				if(f.exists() && layersOfPath.getFileState() == ID.FILE_OK ){ // file exist and it is writable+valid
					initInput(this.layersOfPath.getXmlpath());
					initOutput();
					readAndWrite(); // saves as stream to byteStream
					closeInput();
					closeOutput();
				}
				else{ // file doesn't exist
					LOGGER.warning("Creating file "+this.layersOfPath.getXmlpath());
					if(layersOfPath.getFileState() == ID.FILE_NEW_FILE)
						f.createNewFile();
					// init only output
					initOutput();
					// start writing
					writeNewDocument();
					closeOutput();
				}
				// save Stream to file
				this.layersOfPath=null;
				return writeStreamToFile(f);


			}
			this.layersOfPath=null;
			return false; // nothing saved

		} catch (Exception e) {
			LOGGER.severe("Error in writing Markings! "+ e.getMessage() );
			e.printStackTrace();
			this.layersOfPath=null;
			this.successfullySavedMarkingLayers.clear(); // the writing was not successfull
			return false;
		}
	}


	private boolean writeStreamToFile(File file){
		try {
			System.out.println(byteStream.toString());
			rm=new XMLreadManager();
			if(rm.isStreamValid(byteStream)){
			/*	TransformerFactory tFactory=TransformerFactory.newInstance();

				Transformer transformer = tFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT,"yes");
			//	transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "layers.dtd");
				transformer.setURIResolver(null); // disable validation

				ByteArrayInputStream byteIn = new ByteArrayInputStream(byteStream.toByteArray());
				StreamSource source = new StreamSource(byteIn);

				StreamResult out = new StreamResult(file);
				transformer.transform(source, out);
				*/
				FileOutputStream fStreamIn = new FileOutputStream(file);
				fStreamIn.write(byteStream.toByteArray());
				fStreamIn.flush();
				fStreamIn.close();
				byteStream.close();
			//	byteIn.close();

				return true;
			}
			else{
				byteStream.close();
				return false;

			}



		}
		 catch (Exception e) {
			 LOGGER.severe("Error in writingStream to file: " +e.getMessage());
			return false;

		}


	}
/*
	public int checkFile(File file){
		if(file != null){
			if(file.exists()){
				if(file.isFile()){
					XMLfilter filter = new XMLfilter();
					if(filter.accept(file)){

						if(file.canRead()){
							if(file.length()>0){ // is just created file
								XMLreadManager rm=new XMLreadManager();
								if(rm.isFileValid(file)){
									if(file.canWrite()){
										return ID.FILE_OK; // can update the file
									}
									else
										return ID.FILE_CANT_WRITE;
								}else // if is not valid file
									return ID.FILE_NOT_VALID;
							}else
								return ID.FILE_NEW_FILE;
						}else // if can't read
							return ID.FILE_CANT_READ;
					}else // if is not  XML file
						return ID.FILE_NOT_XML;
				}else //if file is folder
					return ID.FILE_IS_NOT_FILE;
			}else // if file not exist
				return ID.FILE_NOT_EXISTS;
		}else
			return ID.FILE_CANT_READ;
	}
*/

	/**
	 *  Writes all ImageLayer-objects of layersOfPath to XML-file.
	 */
	private void writeNewDocument() throws Exception{
		// create
		StartDocument startDocument = xef.createStartDocument();
	   xew.add(startDocument);
	   xew.add(end);
	   xew.add(getDTDevent());
	   xew.add(end);
	   xew.add(xef.createStartElement("", "", XMLtags.layers));
	   xew.add(end);

	   // add inserting ImageLayers

	   Iterator<ImageLayer> iterator = this.layersOfPath.getImageLayerList().iterator();
	   while(iterator.hasNext()){
		   ImageLayer iLayer = iterator.next();
		  writeImageLayerFromImageLayer(iLayer);
		//	iterator.remove();  // when saved -> remove   (not necessary)
	   }

	   xew.add(xef.createEndElement("", "", XMLtags.layers));
	   xew.add(end);
	   EndDocument endDocument=xef.createEndDocument();
	   xew.add(endDocument);



	}

	/**
	 *  Writes the information of single ImageLayer-object to XML file inside <imagelayer> tags.
	 * @param iLayer The ImageLayer-object, which information is written to xml-file.
	 * @throws Exception
	 */
	private void writeImageLayerFromImageLayer(ImageLayer iLayer){

		try {
			se= xef.createStartElement("", "", XMLtags.imagelayer);
			xew.add(se);
			att = xef.createAttribute(XMLtags.imagename, iLayer.getImageFileName());
			xew.add(att);
			xew.add(end);

			if(iLayer.getMarkingLayers() != null && iLayer.getMarkingLayers().size()>0){

				Iterator<MarkingLayer> mIterator= iLayer.getMarkingLayers().iterator();
				while(mIterator.hasNext()){
					MarkingLayer ml=mIterator.next();
					if(writeMarkingLayer(ml)){
						mIterator.remove(); // saved the xml -> remove from list
						successfullySavedMarkingLayers.add(ml.getLayerID());
					}
					else{ // error in saving
						unsavedMarkingLayers.add(ml.getLayerID());
					}
				}
			}

			//  add endElement of Imagelayer
			ee=xef.createEndElement("", "", XMLtags.imagelayer);
			xew.add(ee);
			xew.add(end);


		} catch (XMLStreamException e) {
			LOGGER.warning("Error in saving markings of ImageLayer "+iLayer.getImageFileName()+ " to xml-file");

		}

	}

	/** Writes all information of MarkingLayer-object to XML-file inside <markinglayer> tags.
	 * @param mLayer MarkingLayer-object, which information is saved to XML-file.
	 * @throws Exception
	 */
	private boolean writeMarkingLayer(MarkingLayer mLayer){
		try {
			//xew.add(tab);
			se= xef.createStartElement("", "", XMLtags.markinglayer);
			xew.add(se);
			att = xef.createAttribute(XMLtags.markingname, mLayer.getLayerName());
			xew.add(att);
			xew.add(end);


			// add color
			if(mLayer.getColor() != null){
				xew.add(xef.createStartElement("", "", XMLtags.color));
				xew.add(xef.createCharacters(mLayer.getStringColor()));
				xew.add(xef.createEndElement("", "", XMLtags.color));
				xew.add(end);
			}
			// add opacity
			if(mLayer.getOpacity() >0){
				xew.add(xef.createStartElement("", "", XMLtags.opacity));
				xew.add(xef.createCharacters(""+mLayer.getOpacity()));
				xew.add(xef.createEndElement("", "", XMLtags.opacity));
				xew.add(end);
			}
			// add size
			if(mLayer.getSize() >0){
				xew.add(xef.createStartElement("", "", XMLtags.size));
				xew.add(xef.createCharacters(""+mLayer.getSize()));
				xew.add(xef.createEndElement("", "", XMLtags.size));
				xew.add(end);
			}

			// add shape
			if(mLayer.getShapeID() >0){
				xew.add(xef.createStartElement("", "", XMLtags.shape));
				xew.add(xef.createCharacters(""+mLayer.getShapeID()));
				xew.add(xef.createEndElement("", "", XMLtags.shape));
				xew.add(end);
			}


			// add thickness
			if(mLayer.getThickness()>0){
				xew.add(xef.createStartElement("", "", XMLtags.thickness));
				xew.add(xef.createCharacters(""+mLayer.getThickness()));
				xew.add(xef.createEndElement("", "", XMLtags.thickness));
				xew.add(end);
			}

			// add coordinates
			if(mLayer.getCoordinateList() != null && mLayer.getCoordinateList().size()>0){
				xew.add(xef.createStartElement("", "", XMLtags.coordinates));
				xew.add(end);

				Iterator<Point> cIterator = mLayer.getCoordinateList().iterator();
				while(cIterator.hasNext()){
					Point p = cIterator.next();
					xew.add(xef.createStartElement("", "", XMLtags.singlecoordinate));
					xew.add(xef.createCharacters(""+p.x+","+p.y));
					xew.add(xef.createEndElement("", "", XMLtags.singlecoordinate));
					xew.add(end);
				}
				xew.add(xef.createEndElement("", "", XMLtags.coordinates));
				xew.add(end);
			}
			// add GridProperties
			if(mLayer.getGridProperties() != null){
				writeGridToMarkingLayer(mLayer.getGridProperties());
			}



//  add endElement of MarkingLayer
				ee=xef.createEndElement("", "", XMLtags.markinglayer);
				xew.add(ee);
				xew.add(end);

				return true;
		} catch (Exception e) {
			LOGGER.warning("Error in saving markings of "+mLayer.getLayerName()+ " to xml-file");
			return false;
		}

	}

	private void writeGridToMarkingLayer(GridProperties gridProperty) throws Exception{
			xew.add(xef.createStartElement("", "", XMLtags.grid));
			xew.add(end);

			xew.add(xef.createStartElement("", "", XMLtags.grid_on));
			if(gridProperty.isGridON())
			xew.add(xef.createCharacters(XMLtags.value_true));
			else
				xew.add(xef.createCharacters(XMLtags.value_false));
			xew.add(xef.createEndElement("", "", XMLtags.grid_on));
			xew.add(end);

			if(gridProperty.getRowLineYs() != null && gridProperty.getRowLineYs().size()>0 &&
					gridProperty.getColumnLineXs() != null && gridProperty.getColumnLineXs().size()>0){

				xew.add(xef.createStartElement("", "", XMLtags.lines));
				xew.add(end);

				for (Iterator<Integer> iterator = gridProperty.getColumnLineXs().iterator(); iterator.hasNext();) {
					int x = (int) iterator.next();
					xew.add(xef.createStartElement("", "", XMLtags.x));
					xew.add(xef.createCharacters(""+x));
					xew.add(xef.createEndElement("", "", XMLtags.x));
					xew.add(end);
				}
				for (Iterator<Integer> iterator = gridProperty.getRowLineYs().iterator(); iterator.hasNext();) {
					int y = (int) iterator.next();
					xew.add(xef.createStartElement("", "", XMLtags.y));
					xew.add(xef.createCharacters(""+y));
					xew.add(xef.createEndElement("", "", XMLtags.y));
					xew.add(end);
				}

				xew.add(xef.createEndElement("", "", XMLtags.lines));
				xew.add(end);
			}

			if(gridProperty.getPositionedRectangleList() != null && gridProperty.getPositionedRectangleList().size()>0){

				xew.add(xef.createStartElement("", "", XMLtags.rectangles));
				xew.add(end);


				//	xew.add(xef.createStartElement("", "", XMLtags.selected_rec));
				//	xew.add(end);
					for (Iterator<PositionedRectangle> iterator = gridProperty.getPositionedRectangleList().iterator(); iterator.hasNext();) {
						PositionedRectangle pRectangle = iterator.next();
					//	String recString= pRectangle.x+","+pRectangle.y+","+pRectangle.width+","+pRectangle.height;
						String selection=XMLtags.value_false;
						if(pRectangle.isSelected())
							selection=XMLtags.value_true;
						xew.add(xef.createStartElement("", "", XMLtags.rec));

					//	xew.add(xef.createAttribute(XMLtags.selected, selection));
						att = xef.createAttribute(XMLtags.selected, selection);
						xew.add(att);
						xew.add(xef.createAttribute(XMLtags.column, ""+pRectangle.getColumn()));
						xew.add(xef.createAttribute(XMLtags.row, ""+pRectangle.getRow()));
						xew.add(xef.createAttribute(XMLtags.x, ""+pRectangle.x));
						xew.add(xef.createAttribute(XMLtags.y, ""+pRectangle.y));
						xew.add(xef.createAttribute(XMLtags.width, ""+pRectangle.width));
						xew.add(xef.createAttribute(XMLtags.height, ""+pRectangle.height));
					//	xew.add(xef.createCharacters(recString));
						xew.add(xef.createEndElement("", "", XMLtags.rec));
						xew.add(end);
					}
				//	xew.add(xef.createEndElement("", "", XMLtags.selected_rec));
				//	xew.add(end);

/*
				if(gridProperty.getUnselectedRectangles() != null && gridProperty.getUnselectedRectangles().size()>0){
					xew.add(xef.createStartElement("", "", XMLtags.unselected_rec));
					xew.add(end);
					for (Iterator<Rectangle> iterator = gridProperty.getUnselectedRectangles().iterator(); iterator.hasNext();) {
						Rectangle rectangle = iterator.next();
						String recString= rectangle.x+","+rectangle.y+","+rectangle.width+","+rectangle.height;
						xew.add(xef.createStartElement("", "", XMLtags.rec));
						xew.add(xef.createCharacters(recString));
						xew.add(xef.createEndElement("", "", XMLtags.rec));
						xew.add(end);
					}
					xew.add(xef.createEndElement("", "", XMLtags.unselected_rec));
					xew.add(end);
				}

				if(gridProperty.getUnselectedGridCellNumbers() != null && gridProperty.getUnselectedGridCellNumbers().size()>0){
					xew.add(xef.createStartElement("", "", XMLtags.unselected_rec_numbers));
					xew.add(end);
					for (Iterator<Integer> iterator = gridProperty.getUnselectedGridCellNumbers().iterator(); iterator.hasNext();) {
						int num = iterator.next();

						xew.add(xef.createStartElement("", "", XMLtags.num));
						xew.add(xef.createCharacters(""+num));
						xew.add(xef.createEndElement("", "", XMLtags.num));
						xew.add(end);
					}
					xew.add(xef.createEndElement("", "", XMLtags.unselected_rec_numbers));
					xew.add(end);
				}
*/

				xew.add(xef.createEndElement("", "", XMLtags.rectangles));
				xew.add(end);
			}


			xew.add(xef.createEndElement("", "", XMLtags.grid));
			xew.add(end);

	}

	private DTD getDTDevent(){
		try {
			InputStream in= getClass().getResourceAsStream("/information/dtd/layers.dtd");
			 Scanner s = new Scanner(in).useDelimiter("\\A");
			   String dtdFIleString= s.hasNext() ? s.next() : "";

		String fileStr = "<!DOCTYPE layers SYSTEM \"layers.dtd\">";
			//String dtdFIleString= new String(Files.readAllBytes(Paths.get("/information/dtd/layers.dtd")));
		in.close();s.close();
			return xef.createDTD(fileStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private void readAndWrite() throws Exception{


		while(xer.hasNext()){
			XMLEvent event= xer.nextEvent();

		//	LOGGER.fine(event.toString()+ " et:" + event.getEventType() + " ee:"+ XMLEvent.END_ELEMENT+ " ed: "+XMLEvent.END_DOCUMENT);
			if(event.isStartElement()){
				se= event.asStartElement();
				if(se.getName().equals(XMLtags.qnameImageLayer)){
					String iLayerName= se.getAttributeByName(XMLtags.qnameImageName).getValue();
					ImageLayer iLayer=getImageLayerByName(iLayerName);
					if(iLayer != null){ // ImageLayer found -> save StartElement and go for markinglayers
						xew.add(se); // write StartElement
						xew.add(end);
					//	xew.add(tab);
						combineImageLayerContentWithXMLfile(iLayer);
						removeImageLayer(iLayer); // saved ImageLayer -> remove from list


					}else{ // ImageLayer not found -> write only the StartElement to file
						xew.add(se);
					//	xew.add(tab);
					}

				}
				else{// if(se.getName().equals(qnameLayers)){ // layers
					xew.add(se);
				//	xew.add(tab);
				}

			}
			else
				if(event.isEndDocument()){
					// write rest imageLayers from LayersOfPath object


					// write enddocument tag to end
					xew.add(event);
					return;

				}
				else if(event.getEventType() == XMLEvent.DTD){
					LOGGER.fine("XML: DTD");
					xew.add(event);
					xew.add(end);

				}
				else // end elements
					if(event.isEndElement()){

					EndElement ee = event.asEndElement();

					if(ee.getName().equals(XMLtags.qnameLayers)){
						if(this.layersOfPath.getImageLayerList()!= null && this.layersOfPath.getImageLayerList().size()>0){
							Iterator<ImageLayer> iIterator=this.layersOfPath.getImageLayerList().iterator();
							while(iIterator.hasNext()){
								ImageLayer iLayer=iIterator.next();
								writeImageLayerFromImageLayer(iLayer);
								//iIterator.remove(); // remove when saved (this is not necessary)

							}

						}
					}


					xew.add(ee);
				//	xew.add(tab);

					}
					else{// write all other events identically to file
						if(event.isStartDocument())
						{
							xew.add(event);
							xew.add(end);
						}
						else
							xew.add(event);
					//	xew.add(tab);

					}



		}
		LOGGER.fine("XML data written to memory successfully");
	}

	private void combineImageLayerContentWithXMLfile(ImageLayer iLayer){

		try {
			// go through markingLayers of xml file
			while(xer.hasNext()){
				XMLEvent event= xer.nextEvent();
				if(event.isStartElement() && event.asStartElement().getName().equals(XMLtags.qnameMarkingLayer)){
					se=event.asStartElement();
					String mLayerName=se.getAttributeByName(XMLtags.qnameMarkingName).getValue();
					MarkingLayer mLayer = getMarkingLayerByName(mLayerName, iLayer);
					if(mLayer != null){ // MarkingLayer found -> either overwrite or skip
						int overwriteOrSkip= ID.UNDEFINED;
						if(savingType != ID.UNDEFINED) // remembering the rule from previous selection
							overwriteOrSkip=savingType;
						else{ // undefined -> ask from user
						// ask from user should overwrite MarkingLayer
						overwriteOrSkip=inquireSavingType(); // construct later
						if(overwriteOrSkip == ID.OVERWRITE_REMEMBER_SELECTION || overwriteOrSkip == ID.SKIP_REMEMBER_SELECTION)
							this.savingType=overwriteOrSkip; // remembers next time
						}
						if(overwriteOrSkip==ID.OVERWRITE || overwriteOrSkip == ID.OVERWRITE_REMEMBER_SELECTION){
							// overwrite -> write from MarkingLayer-object
							if(writeMarkingLayer(mLayer)){
								iLayer.removeMarkingLayer(mLayer); // written successfully -> remove
								successfullySavedMarkingLayers.add(mLayer.getLayerID());
							}
							else{ // error in saving MarkingLayer
								unsavedMarkingLayers.add(mLayer.getLayerID());
							}
							skipReadingMarkingLayer(); // XMLreader goes to endElement of markinglayer
						}
						else{ // skip -> Write from XML-file
							writeMarkingLayerFromXMLfile(se);
						}


					}
					else{// not found the MarkingLayer -> write from file
						writeMarkingLayerFromXMLfile(se);
					}
				}else // has read the whole imageLayer content from xml-file
					if(event.isEndElement() && event.asEndElement().getName().equals(XMLtags.qnameImageLayer)){

						// write the rest MarkingLayers of iLayer
						Iterator<MarkingLayer> mIterator =iLayer.getMarkingLayers().iterator();
						while(mIterator.hasNext()){
							MarkingLayer markingLayer= mIterator.next();
							if(writeMarkingLayer(markingLayer)){
								mIterator.remove(); // written successfully -> remove
								successfullySavedMarkingLayers.add(markingLayer.getLayerID());
							}
							else{ // error in saving MarkingLayer
								unsavedMarkingLayers.add(markingLayer.getLayerID());
							}
						}
						xew.add(event); // write endElement </imagelayer>
						return; // go back for reading imagelayers
					}


			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private int inquireSavingType(){
		return ID.OVERWRITE;

	}

	private void skipReadingMarkingLayer(){
		try {
			while(xer.hasNext()){
				XMLEvent event=xer.nextEvent();
				if(event.isEndElement() && event.asEndElement().getName().equals(XMLtags.qnameMarkingLayer))
					return;
			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in writing XML file: skipping Marking Layer produced error: " +e.getMessage());
		}

	}

	private void writeMarkingLayerFromXMLfile(StartElement firstElement){

		try {
			xew.add(firstElement); // write the startelement
			while(xer.hasNext()){
				XMLEvent event=xer.nextEvent();
				xew.add(event);
				if(event.isEndElement() && event.asEndElement().getName().equals(XMLtags.qnameMarkingLayer))
					return;
			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in writing XML file: copying markinglayer from file produced error: " +e.getMessage());
		}
	}


	private void initInput(String filePath){

		try {
			XMLInputFactory inputFactory=XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(filePath);

			inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

			inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);


			xer = inputFactory.createXMLEventReader(in);


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (XMLStreamException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}

	private void initOutput(){
		 try {
			// create an XMLOutputFactory
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			byteStream = new ByteArrayOutputStream();
			// create eventWriter
			xew = outputFactory.createXMLEventWriter(byteStream);


			xef = XMLEventFactory.newInstance();
			end = xef.createDTD("\n");
			tab = xef.createDTD("\t");



		 } catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private void closeInput() throws Exception{
		xer.close();
	}

	private void closeOutput() throws Exception{
		xew.flush();
		xew.close();
		byteStream.flush();
	}

	private void renameTo(String oldFileName, String newFileName) throws IOException{
		Path path = Paths.get(oldFileName);
		Path path2 = Paths.get(newFileName);

		if((new File(newFileName)).canWrite()){
			Files.move(path,path2,StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private ImageLayer getImageLayerByName(String iName) throws Exception{
		if(iName != null && iName.length()>0 && this.layersOfPath.getImageLayerList() != null
				&& this.layersOfPath.getImageLayerList().size()>0){
			Iterator<ImageLayer> iIterator = this.layersOfPath.getImageLayerList().iterator();
			while(iIterator.hasNext()){
				ImageLayer iLayer= iIterator.next();
				if(iName.equals(iLayer.getImageFileName()))
					return iLayer;
			}
		}
		return null;


	}

	private void removeImageLayer(ImageLayer iLayer) throws Exception{
		if(iLayer != null && this.layersOfPath.getImageLayerList() != null
				&& this.layersOfPath.getImageLayerList().size()>0){
			this.layersOfPath.getImageLayerList().remove(iLayer);

		}
	}

	private MarkingLayer getMarkingLayerByName(String mName, ImageLayer iLayer) throws Exception{
		if(mName != null && mName.length()>0 && iLayer.getMarkingLayers() != null && iLayer.getMarkingLayers().size()>0){
			Iterator<MarkingLayer> mIterator = iLayer.getMarkingLayers().iterator();
			while(mIterator.hasNext()){
				MarkingLayer ml= mIterator.next();
				if(mName.equals(ml.getLayerName()))
					return ml;
			}
		}
		return null;

	}

	public ArrayList<Integer> getUnsavedMarkingLayers() {
		return unsavedMarkingLayers;
	}

	public ArrayList<Integer> getSuccessfullySavedMarkingLayers() {
		return successfullySavedMarkingLayers;
	}



}
