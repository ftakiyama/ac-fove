package br.usp.poli.takiyama.acfove;

/**
 * Aggregation operator.
 * @author ftakiyama
 *
 */
final class Or implements Operator {

	/**
	 * The <code>Or</code> object.
	 */
	private static Or OR = new Or();
	
	/**
	 * Private constructor that enforces non-instantiability.
	 */
	private Or() { }
	
	/**
	 * Returns an instance of the <code>Or</code> object.
	 * @return An instance of the <code>Or</code> object.
	 */
	public static Or getInstance() {
		return OR;
	}
	

	@Override
	public String applyOn(String s1, String s2) throws IllegalArgumentException {
		if ((!s1.toLowerCase().equals("true") && !s1.toLowerCase().equals("false")) ||	
				(!s2.toLowerCase().equals("true") && !s2.toLowerCase().equals("false"))) {
			throw new IllegalArgumentException("Invalid booleans: " + s1 + ", " + s2);
		}
		
		return Boolean.toString((Boolean.parseBoolean(s1) || Boolean.parseBoolean(s2)));
	}
	

	@Override
	public String applyOn(String s1, String s2, String s3) throws IllegalArgumentException {
		if ((!s1.toLowerCase().equals("true") && !s1.toLowerCase().equals("false")) ||	
				(!s2.toLowerCase().equals("true") && !s2.toLowerCase().equals("false")) ||	
				(!s3.toLowerCase().equals("true") && !s3.toLowerCase().equals("false"))) {
			throw new IllegalArgumentException("Invalid booleans: " + s1 + ", " + s2  + ", " + s3);
		}
		
		return Boolean.toString(Boolean.parseBoolean(s1) || Boolean.parseBoolean(s2) || Boolean.parseBoolean(s3));
	}
	
	@Override
	public String toString() {
		return "OR";
	}
}
