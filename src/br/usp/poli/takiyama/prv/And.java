package br.usp.poli.takiyama.prv;

import java.util.Set;

/**
 * The <code>AND</code> logical operator, applicable to {@link  Bool} elements.
 * 
 * @author Felipe Takiyama
 */
public final class And implements Operator<Bool> {
	
	/**
	 * The <code>And</code> operator. It is applicable to boolean arguments.
	 */
	public static final And AND = new And();
	
	
	/**
	 * Private constructor that enforces non-instantiability.
	 */
	private And() { }
	
	
	@Override
	public Bool applyOn(Bool e1, Bool e2) {
		return Bool.valueOf(e1.value() && e2.value());
	}

	
	@Override
	public Bool applyOn(Bool e1, Bool e2, Bool e3) {
		return Bool.valueOf(e1.value() && e2.value() && e3.value());
	}

	
	@Override
	public Bool applyOn(Set<Bool> s) throws IllegalArgumentException,
	  		NullPointerException {
		
		if (s == null) {
			throw new NullPointerException();
		}
		if (s.isEmpty()) {
			throw new IllegalArgumentException();
		}
		if (s.size() == 1) {
			return s.iterator().next();
		}
		boolean result = true;
		for (Bool b : s) {
			result = result && b.value();
		}
		return Bool.valueOf(result);
	}

	
	@Override
	public Bool apply(Bool e, int n) throws IllegalArgumentException,
			NullPointerException {
		
		if (e == null) {
			throw new NullPointerException();
		}
		if (n < 0) {
			throw new IllegalArgumentException();
		}
		return e; // trivial operation for boolean elements
	}
	
	
	@Override
	public Class<Bool> getTypeArgument() {
		return Bool.class;
	}
	
	
	@Override
	public String toString() {
		return "AND";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return (obj instanceof And);
	}
	
	/**
	 * Returns 2.
	 */
	@Override
	public int hashCode() {
		return 2; // singleton
	}
}
