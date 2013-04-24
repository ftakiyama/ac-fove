package br.usp.poli.takiyama.common;

/**
 * Interface to apply the 
 * <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder pattern</a>.
 * 
 * @author Felipe Takiyama
 *
 * @param <T> The type of object being built
 */
public interface Builder<T> {
	
	/**
	 * Returns an instance of the object being built.
	 * 
	 * @return an instance of the object being built.
	 */
	public T build();
}
