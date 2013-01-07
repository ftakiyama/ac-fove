package br.usp.poli.takiyama.prv;

import java.util.HashMap;

/**
 * A name generator for logical variables.
 * @author ftakiyama
 *
 */
public final class LogicalVariableNameGenerator {
	
	private static int count = 0;
	private static HashMap<String, String> mapping = new HashMap<String, String>();
	
	private LogicalVariableNameGenerator() {
		// enforces non-instantiability
	}
	
	/**
	 * Returns a new logical Variable name. Names generated have the following
	 * format: X{n}, where n is a number starting from 1.
	 * <br>
	 * The specified LogicalVariable is kept in a map so one can retrieve the
	 * old logical variable name later.
	 * 
	 * @param old The logical variable to be renamed.
	 * @return The specified logical variable renamed.
	 */
	public static LogicalVariable rename(LogicalVariable old) {
		
		/*
		 * Discussion
		 * ---------------
		 * I've made LogicalVariable mutable only to rename it and not create
		 * new instances every time renaming is necessary.
		 * The rename method is visible only inside the package, but still...
		 * The code below looks ugly to me. Sounds 'wrong'.
		 */
		count++;
		mapping.put(("X" + count), old.getValue());
		return old.rename(("X" + count)); 
	}
	
	/**
	 * Resets the count and the mapping of logical variables.
	 */
	public static void reset() {
		count = 0;
		mapping.clear();
	}
}
