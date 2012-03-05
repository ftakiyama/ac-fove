package br.usp.dml.takiyama.cfove.prv;

import java.util.HashMap;
import java.util.List;

/**
 * I am using this class?
 * I am casting the second term in each binding to make this a ground substitution.
 * But shoundn't I check whether the term CAN be converted to a Constant?
 * @author ftakiyama
 *
 */
class GroundSubstitution implements SubstitutionInterface {
	private HashMap<LogicalVariable, Constant> bindings;
	
	/**
	 * Static factory. Returns an empty set of bindings (or a empty
	 * substitution, if you prefer).
	 * @return An empty substitution.
	 */
	public static GroundSubstitution getInstance(List<Binding> bindings) {
		return new GroundSubstitution(bindings);
	}
	
	/**
	 * Constructor. Creates a substitution based on a list of bindings.
	 * @param bindings A {@link List} of {@link Binding}s. 
	 */
	private GroundSubstitution(List<Binding> bindings) {
		this.bindings = new HashMap<LogicalVariable, Constant>();
		for (Binding bind : bindings) {
			this.bindings.put(bind.getFirstTerm(), (Constant)bind.getSecondTerm());
		}
	}
	
	/**
	 * Adds a new binding to this set of substitutions.
	 * @param v A {@link LogicalVariable}
	 * @param t A {@link Term}
	 */
	public void add(LogicalVariable v, Term t) {
		if (t.isConstant()) {
			this.bindings.put(v, (Constant)t);
		}
	}
}
