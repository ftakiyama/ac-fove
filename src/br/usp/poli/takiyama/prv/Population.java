package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A population is a set of individuals. Individuals instances of the class
 * {@link Constant}.
 * This class is mutable.
 * I am considering if it is worth maintaining this class.
 * @author ftakiyama
 *
 */
public class Population {
	private ArrayList<Constant> individuals; //why am I using List instead of Set?
	
	/**
	 * Creates a population. All specified individuals that are repeated are
	 * inserted only once. 
	 * @param individuals The individuals of the population. 
	 */
	public Population(List<Constant> individuals) {
		this.individuals = new ArrayList<Constant>();
		for (Constant c : individuals) {
			if (!this.individuals.contains(c)) {
				this.individuals.add(c);
			}
		}
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
	 * Removes the individual specified from the population. If the individual
	 * does not exist, the population remains unchanged.
	 * @param individual The individual to be removed.
	 */
	public void removeIndividual(Constant individual) {
		this.individuals.remove(individual);
	}
	
	/**
	 * Returns the individuals of the population as a set.
	 * <b>Attention!</b> If there are repeated individuals, one of them will
	 * be lost! I am assuming that all individuals are unique. 
	 * <br>
	 * The order of the individuals in the set is not guaranteed to be the same
	 * as the one returned by the getIndividual method.
	 * @return A set of containing all individuals of the population.
	 */
	public Set<Constant> toSet() {
		return new HashSet<Constant>(this.individuals);
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
