package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A population is a set of individuals. Individuals instances of the class
 * {@link Constant}. 
 * This class is mutable.
 * TODO Check duplicity of individuals
 * @author ftakiyama
 *
 */
public class Population {
	private ArrayList<Constant> individuals;
	
	/**
	 * Creates a population. Individuals are inserted in the given order.
	 * @param individuals The individuals of the population. 
	 */
	public Population(List<Constant> individuals) {
		this.individuals = new ArrayList<Constant>(individuals);
	}
	
	/**
	 * Copies a population. The original population is not modified.
	 * @param p The population to be copied.
	 * @return The copy of the specified population.
	 */
	public static Population copyOf(Population p) {
		return new Population(p.individuals);
	}
	
	/**
	 * Returns the number of individuals in the population.
	 * @return The number of individuals in the population.
	 */
	public int size() {
		return this.individuals.size();
	}
	
	/**
	 * Returns true if the population contains the specified individual.
	 * @param individual The individual whose presence in the population is
	 * to be tested
	 * @return True if the population contains the specified individual, false
	 * otherwise.
	 */
	public boolean contains(Constant individual) {
		return this.individuals.contains(individual);
	}
	
	/**
	 * Returns a copy of an individual from the population. 
	 * @param index The index of the individual in the population.
	 * @return A copy of an individual from the population.
	 */
	public Constant getIndividual(int index) {
		return new Constant(individuals.get(index).getValue());
	}
	
	/**
	 * Removes the individual specified from the population.
	 * @param individual The individual to be removed.
	 */
	public void removeIndividual(Constant individual) {
		this.individuals.remove(individual);
	}
	
	@Override
	public String toString() {
		return this.individuals.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Population))
			return false;
		Population target = (Population) other;
		return (this.individuals == null) ? 
				(target.individuals == null) : 
				(this.individuals.equals(target.individuals));
		
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = result * 31 + Arrays.hashCode((Constant[]) this.individuals.toArray());
		return result;
	}
	
}
