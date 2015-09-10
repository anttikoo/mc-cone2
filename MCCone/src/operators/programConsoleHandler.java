package operators;

import gui.LogFrame;

import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;

public class programConsoleHandler extends ConsoleHandler{
	private LogFrame logFrame;
	public programConsoleHandler(LogFrame logFrame){
		this.logFrame=logFrame;
	}

	public void publish(LogRecord record){
		this.logFrame.printMessage(record);
	}


}
