package br.usp.poli.takiyama.acfove.operator;

import java.util.Set;

final public class And implements BooleanOperator {

	/**
	 * The <code>And</code> operator. It is applicable to boolean arguments.
	 */
	public static And AND = new And();
	
	/**
	 * Private constructor that enforces non-instantiability.
	 */
	private And() { }
	
	@Override
	public Boolean applyOn(Boolean a, Boolean b) {
		boolean b1 = a;
		boolean b2 = b;
		return Boolean.valueOf(b1 && b2);
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
			result = result && temp;
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
			result = result && b;
		}
		return Boolean.valueOf(result);
	}

	@Override
	public boolean getIdentity() {
		return true;
	}
	
	@Override
	public String toString() {
		return "AND";
	}
}
