package br.usp.poli.takiyama.prv;

/**
 * Class that represents objects over which {@link Substitution}
 * is applicable.
 */
public interface Replaceable<T> {
	
	/**
	 * Applies the specified substitution to the Replaceable object.
	 * <p>
	 * If the specified substitution does not apply to the object, then 
	 * returns the object unchanged. If the result of applying the 
	 * substitution in this object has an invalid state, this method
	 * should throw an {@link IllegalArgumentException}.
	 * </p>
	 * 
	 * @param s The substitution to apply
	 * @return The object that results from the application of the
	 * specified substitution
	 */
	public T apply(Substitution s); 
}
