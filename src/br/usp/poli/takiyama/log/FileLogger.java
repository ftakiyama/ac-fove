package br.usp.poli.takiyama.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileLogger {
	private static Handler handler;
	
	public static void setup() {
		Logger globalLogger = Logger.getLogger("");
		Handler[] handlers = globalLogger.getHandlers();
		for(Handler handler : handlers) {
		    globalLogger.removeHandler(handler);
		}
		
		try {
			handler = new FileHandler("log/acfove.log");	
		} catch (IOException e) {
			System.err.println("Could not write to log file.");
			System.exit(-1);
		}
		
		handler.setFormatter(new ConsoleFormatter());
		
		globalLogger.addHandler(handler);
		globalLogger.setLevel(Level.INFO);
	}
}
