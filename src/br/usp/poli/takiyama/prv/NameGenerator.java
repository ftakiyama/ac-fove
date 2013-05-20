package br.usp.poli.takiyama.prv;

import java.util.HashMap;
import java.util.List;


/**
 * A name generator for logical variables
 * @author Felipe Takiyama
 */
public final class NameGenerator {
	
	private static int count = 0;
	//private static final HashMap<String, String> map = new HashMap<String, String>();
	
	/**
	 * Substitutions are in the form renamed/old
	 */
	private static Substitution map = Substitution.getInstance();
	
	private NameGenerator() {
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
		if (map.contains(old)) {
			return (LogicalVariable) map.getReplacement(old);
		}
		List<Binding> bindingList = map.asList();
		count++;
		String newName = "X" + count;
		LogicalVariable newVariable = old.rename(newName);
		Binding bind = Binding.getInstance(newVariable, old);
		bindingList.add(bind);
		map = Substitution.getInstance(bindingList);
		return newVariable; 
	}
	
	
	/**
	 * Resets the count and clears the mapping of logical variables.
	 */
	public static void reset() {
		count = 0;
		map = Substitution.getInstance();
	}
	
	
//	/**
//	 * Restore the old name of the specified logical variable.
//	 * If the specified logical variable does not exist in the mapping, then
//	 * returns the specified logical variable, unchanged.
//	 * 
//	 * @param lv The logical variable to restore.
//	 * @return A new instance of the specified logical variable with its name
//	 * changed to the old one, according to the internal mapping, or the 
//	 * specified logical variable if there is no mapping that matches it.
//	 */
//	public static LogicalVariable restore(LogicalVariable lv) {
//		if (map.containsKey(lv.value())) {
//			return lv.rename(map.get(lv.value()));
//		} else {
//			return lv;
//		}
//	}
	
	public static Substitution getOldNames() {
		return map;
	}
}
