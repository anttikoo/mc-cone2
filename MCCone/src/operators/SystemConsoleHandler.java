package operators;

import gui.LogFrame;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SystemConsoleHandler extends ConsoleHandler{


	public void publish(LogRecord record){
		if(record.getLevel() == Level.SEVERE){
			System.err.println(record.getLevel().toString()+": "+record.getMessage());
		}
		else {
			System.out.println(record.getLevel().toString()+": "+record.getMessage());
		}


	}


}
