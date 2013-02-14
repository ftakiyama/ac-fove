package br.usp.poli.takiyama.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleLogger {
	
	static private ConsoleHandler console;
	
	public static void setup() {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.INFO);
		
		console = new ConsoleHandler();
		console.setFormatter(new ConsoleFormatter());
		
		logger.addHandler(console);
		
	}
}
