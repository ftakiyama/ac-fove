package br.usp.dml.takiyama.cfove.prv;

import java.util.TreeSet;

/**
 * A population is a set of {@link Individual}s.
 * @author ftakiyama
 *
 */
public class Population {
	private TreeSet<Individual> population;
	
	/**
	 * Constructor. Creates an empty population.
	 */
	public Population() {
		this.population = new TreeSet<Individual>();
	}
	
	/**
	 * Adds an individual to this population, if not present.
	 * @param ind Individual to be added.
	 */
	public void add(Individual ind) {
		this.population.add(ind);
	}
	
}
