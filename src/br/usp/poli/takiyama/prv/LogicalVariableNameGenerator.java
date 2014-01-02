/*******************************************************************************
 * Copyright 2014 Felipe Takiyama
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
	public static LogicalVariable rename(StdLogicalVariable old) {
		
		/*
		 * Discussion
		 * ---------------
		 * I've made LogicalVariable mutable only to rename it and not create
		 * new instances every time renaming is necessary.
		 * The rename method is visible only inside the package, but still...
		 * The code below looks ugly to me. Sounds 'wrong'.
		 */
		count++;
		mapping.put(("X" + count), old.value());
		return old.rename(("X" + count)); 
	}
	
	/**
	 * Resets the count and the mapping of logical variables.
	 */
	public static void reset() {
		count = 0;
		mapping.clear();
	}
	
	/**
	 * Restore the old name of the specified logical variable.
	 * If the specified logical variable does not exist in the mapping, then
	 * returns the specified logical variable, unchanged.
	 * @param lv The logical variable to restore.
	 * @return A new instance of the specified logical variable with its name
	 * changed to the old one, according to the internal mapping, or the 
	 * specified logical variable if there is no mapping that matches it.
	 */
	public static LogicalVariable restore(StdLogicalVariable lv) {
		if (mapping.containsKey(lv.value())) {
			return lv.rename(mapping.get(lv.value()));
		} else {
			return lv;
		}
	}
}
