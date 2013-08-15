package br.usp.poli.takiyama.acfove.operator;

import java.util.Set;

/**
 * Aggregation operator.
 * @author ftakiyama
 *@Deprecated
 */
final public class Or implements BooleanOperator {

	/**
	 * The <code>Or</code> operator. It is applicable to boolean arguments.
	 */
	public static Or OR = new Or();
	
	/**
	 * Private constructor that enforces non-instantiability.
	 */
	private Or() { }
	
	@Override
	public Boolean applyOn(Boolean a, Boolean b) {
		boolean b1 = a;
		boolean b2 = b;
		return Boolean.valueOf((b1 || b2));
	}
	
	@Override
	public Boolean applyOn(Boolean a, Boolean b, Boolean c) {
		boolean b1 = a.booleanValue();
		boolean b2 = b.booleanValue();
		boolean b3 = c.booleanValue();
		return Boolean.valueOf(b1 || b2 || b3);
	}
	
	@Override
	public Boolean applyOn(Set<Boolean> s) throws IllegalArgumentException,
												  NullPointerException {
		if (s == null) {
			throw new NullPointerException();
		}
		if (s.isEmpty()) {
			throw new IllegalArgumentException("The specified set is empty.");
		}
		boolean result = false;
		for (Boolean b : s) {
			boolean temp = b.booleanValue();
			result = result || temp;
		}
		return result;
	}
	
	@Override
	public Boolean applyOn(Boolean a, int n) throws IllegalArgumentException,
	    											NullPointerException {
		if (a == null) {
			throw new NullPointerException();
		}
		if (n < 1) {
			throw new IllegalArgumentException();
		}
		boolean b = a.booleanValue();
		boolean result = b;
		for (int i = 0; i < n; i++) {
			result = result || b;
		}
		return Boolean.valueOf(result);
	}

	@Override
	public boolean getIdentity() {
		return false;
	}
	
	@Override
	public String toString() {
		return "OR";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return (obj instanceof Or);
	}
	
	/**
	 * Returns 1, since this is a singleton.
	 */
	@Override
	public int hashCode() {
		return 1; // singleton
	}
}
