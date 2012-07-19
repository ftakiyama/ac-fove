package br.usp.poli.takiyama.acfove;

/**
 * A commutative and associative binary operator, such as AND and OR.
 * @author ftakiyama
 *
 */
interface Operator {
	
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
	public String applyOn(String s1, String s2);
	
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
	public String applyOn(String s1, String s2, String s3);
}
