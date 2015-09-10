package managers;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import gui.LogFrame;
import operators.SystemConsoleHandler;
import operators.TextFileFormatter;
import operators.programConsoleHandler;

/**
 * Initializes Logger handler and formatter
 *
 *
 * @author Antti Kurronen
 *
 */
public class ProgramLogger {


  static private FileHandler fileHandler;
  static private FileHandler fileHandler2;
  static private ConsoleHandler consoleHandler;
  static private ConsoleHandler systemConsoleHandler;
  static private TextFileFormatter fileFormatter;
//  static private SimpleFormatter simpleFormatter;


  /**
   *  Creates Handler and Formatter for logging and adds them to logger.
   *  Log messages are saved to text file and in document of LogFrame where log messages can be read.
   *
   * @param logFrame Logwindow is needed for constructing the TextFileFormatter
   * @throws IOException
   */
  static public void setup(LogFrame logFrame, boolean saveToFile) throws IOException {

	//set the logger
    Logger logger = Logger.getLogger("MCCLogger");
  //  logger.setLevel(Level.INFO);

    consoleHandler=new programConsoleHandler(logFrame);
    systemConsoleHandler=new SystemConsoleHandler();
    // add handler to logger
    logger.addHandler(consoleHandler);
    logger.addHandler(systemConsoleHandler);

    // saving to file
    if(saveToFile){
    	fileHandler = new FileHandler("Logging.txt",8000,1,true);
	    // Create Formatter
	    fileFormatter = new TextFileFormatter();
	    //add formatter to handler
	    fileHandler.setFormatter(fileFormatter);
	    logger.addHandler(fileHandler);
    }
  }
}
