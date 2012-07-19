package br.usp.poli.takiyama.acfove;

final class And implements Operator {

	/**
	 * The <code>And</code> object.
	 */
	private static And AND = new And();
	
	/**
	 * Private constructor that enforces non-instantiability.
	 */
	private And() { }
	
	/**
	 * Returns an instance of the <code>And</code> object.
	 * @return An instance of the <code>And</code> object.
	 */
	public static And getInstance() {
		return AND;
	}
	
	@Override
	public String applyOn(String s1, String s2) throws IllegalArgumentException {
		if ((!s1.toLowerCase().equals("true") && !s1.toLowerCase().equals("false")) ||	
				(!s2.toLowerCase().equals("true") && !s2.toLowerCase().equals("false"))) {
			throw new IllegalArgumentException("Invalid booleans: " + s1 + ", " + s2);
		}
		
		return Boolean.toString((Boolean.parseBoolean(s1) && Boolean.parseBoolean(s2)));
	}

	@Override
	public String applyOn(String s1, String s2, String s3) throws IllegalArgumentException {
		if ((!s1.toLowerCase().equals("true") && !s1.toLowerCase().equals("false")) ||	
				(!s2.toLowerCase().equals("true") && !s2.toLowerCase().equals("false")) ||	
				(!s3.toLowerCase().equals("true") && !s3.toLowerCase().equals("false"))) {
			throw new IllegalArgumentException("Invalid booleans: " + s1 + ", " + s2  + ", " + s3);
		}
		
		return Boolean.toString(Boolean.parseBoolean(s1) && Boolean.parseBoolean(s2) && Boolean.parseBoolean(s3));
	}
	
	@Override
	public String toString() {
		return "AND";
	}

}
