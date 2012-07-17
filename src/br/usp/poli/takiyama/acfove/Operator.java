package br.usp.poli.takiyama.acfove;

/**
 * Aggregation operator.
 * @author ftakiyama
 *
 */
class Operator {
	
	public final static Operator OR = new Operator(0);
	public final static Operator AND = new Operator(1);
	
	private final int operation;
	
	private Operator(int operation) {
		this.operation = operation;
	}
	
	public static Operator valueOf(String operator) {
		return (operator.toLowerCase().equals("or") ? OR : AND);
	}
	
	/**
	 * Applies the operator to the specified arguments.
	 * The arguments must be String representations of the boolean type, that is,
	 * they must be either 'true' or 'false'.
	 * @param s1
	 * @param s2
	 * @return A string representation of the boolean result from applying
	 * this operator to the specified arguments.
	 * @throw IllegalArgumentException if the arguments are not String 
	 * representations of booleans.
	 */
	public String applyOn(String s1, String s2) throws IllegalArgumentException {
		if ((!s1.toLowerCase().equals("true") && !s1.toLowerCase().equals("false")) ||	
				(!s2.toLowerCase().equals("true") && !s2.toLowerCase().equals("false"))) {
			throw new IllegalArgumentException("Invalid booleans: " + s1 + ", " + s2);
		}
		switch (operation) { //horrible
		case 0:
			return Boolean.toString((Boolean.parseBoolean(s1) || Boolean.parseBoolean(s2)));
		case 1:
			return Boolean.toString((Boolean.parseBoolean(s1) && Boolean.parseBoolean(s2)));
		default:
			throw new IllegalArgumentException("Invalid booleans: " + s1 + ", " + s2);
		}
	}
	
	/**
	 * Applies the operator to the specified arguments.
	 * The arguments must be String representations of the boolean type, that is,
	 * they must be either 'true' or 'false'.
	 * @param s1
	 * @param s2
	 * @return A string representation of the boolean result from applying
	 * this operator to the specified arguments.
	 * @throw IllegalArgumentException if the arguments are not String 
	 * representations of booleans.
	 */
	public String applyOn(String s1, String s2, String s3) throws IllegalArgumentException {
		if ((!s1.toLowerCase().equals("true") && !s1.toLowerCase().equals("false")) ||	
				(!s2.toLowerCase().equals("true") && !s2.toLowerCase().equals("false")) ||	
				(!s3.toLowerCase().equals("true") && !s3.toLowerCase().equals("false"))) {
			throw new IllegalArgumentException("Invalid booleans: " + s1 + ", " + s2  + ", " + s3);
		}
		switch (operation) { //horrible
		case 0:
			return Boolean.toString(Boolean.parseBoolean(s1) || Boolean.parseBoolean(s2) || Boolean.parseBoolean(s3));
		case 1:
			return Boolean.toString(Boolean.parseBoolean(s1) && Boolean.parseBoolean(s2) && Boolean.parseBoolean(s3));
		default:
			throw new IllegalArgumentException("Invalid booleans: " + s1 + ", " + s2  + ", " + s3 + " or invalid operation: " + this.operation);
		}
	}
	
	@Override
	public String toString() {
		return "x";
	}
}
