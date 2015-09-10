package operators;

import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLerrorHandler implements ErrorHandler {
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");	
	private boolean foundErrors=false;

    public void warning(SAXParseException e) throws SAXException {
    	LOGGER.warning("Warning at  " +e.getClass().toString() + " :" +e.getMessage()+ " line: " + e.getLineNumber());
    	
    }

    public void error(SAXParseException e) throws SAXException {
    	LOGGER.severe("Error in  " +e.getClass().toString() + " :" +e.getMessage()+ " line: " + e.getLineNumber());
    	this.foundErrors=true;
    }

    public void fatalError(SAXParseException e) throws SAXException {
    	LOGGER.severe("Error in  " +e.getClass().toString() + " :" +e.getMessage()+ " line: " + e.getLineNumber());
    	this.foundErrors=true;
    }

	public boolean isErrorsFound() {
		return foundErrors;
	}

	
}