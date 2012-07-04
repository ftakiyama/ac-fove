package br.usp.poli.takiyama.cfove.prv;

/**
 * Individuals are things in the world that is being described (e.g., a 
 * particular house or a particular booking may be individuals) [Poole, 2010]
 * 
 * @author ftakiyama
 *
 */
final class Individual {
	private final String name;
	
	/**
	 * Static factory. Use this method if you want a new Individual.
	 * @param name The name of the individual
	 * @return An individual whose name is 'name'
	 */
	static Individual getInstance(String name) {
		return new Individual(name);
	}
	
	/**
	 * Constructor. Creates a new individual. 
	 * @param name The name of the individual
	 */
	private Individual(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of this individual.
	 * @return The name of this individual.
	 */
	public String getName() {
		return this.name;
	}
}
