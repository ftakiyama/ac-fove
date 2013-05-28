package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A name generator for logical variables
 * @author Felipe Takiyama
 */
public final class NameGenerator {
	
	private static int count = 0;
	
	// Substitutions are in the form renamed/old
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
	 * Returns a substitution that replaces the specified collection of 
	 * logical variables with new names.
	 * 
	 * @param oldVariables The logical variables to replace
	 * @return a substitution that replaces the specified collection of 
	 * logical variables with new names.
	 */
	public static Substitution rename(Collection<LogicalVariable> oldVariables) {
		List<Binding> toRename = new ArrayList<Binding>(oldVariables.size());
		List<Binding> toRestore = map.asList();
		for (LogicalVariable old : oldVariables) {
			String newName = getNewName();
			LogicalVariable newVariable = old.rename(newName);
			toRename.add(Binding.getInstance(old, newVariable));
			toRestore.add(Binding.getInstance(newVariable, old));
		}
		map = Substitution.getInstance(toRestore);
		return Substitution.getInstance(toRename);
	}
	
	/**
	 * Returns a new logical variable name.
	 */
	private static String getNewName() {
		count++;
		return "X" + count;
	}
	
	
	/**
	 * Resets the count and clears the mapping of logical variables.
	 */
	public static void reset() {
		count = 0;
		map = Substitution.getInstance();
	}
	
	/**
	 * Returns the substitution that restores logical variable old names.
	 * @return the substitution that restores logical variable old names.
	 */
	public static Substitution getOldNames() {
		return map;
	}
}
