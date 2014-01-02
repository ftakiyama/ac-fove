/*******************************************************************************
 * Copyright 2014 Felipe Takiyama
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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
public class Population implements Iterable<Constant> {
	
	// Retrieving individuals from a list is easier.
	private List<Constant> individuals;
	
	
	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * Creates an empty population.
	 */
	private Population(int size) {
		individuals = new ArrayList<Constant>(size);
	}
	
	private Population(Population p) {
		individuals = new ArrayList<Constant>(p.individuals);
	}
	
	/**
	 * Creates a population. All specified individuals that are repeated are
	 * inserted only once. 
	 * @param individuals The individuals of the population. 
	 */
	private Population(List<Constant> individuals) {
		this(individuals.size());
		for (Constant c : individuals) {
			if (!this.individuals.contains(c)) {
				this.individuals.add(c);
			}
		}
	}

	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/

	/**
	 * Returns an empty population.
	 * @return An empty population.
	 */
	public static Population getInstance() {
		return new Population(0);
	}
	
	
	/**
	 * Returns a new instance of Population that contains the same individuals
	 * from the specified population.
	 * 
	 * @param p The population to be copied.
	 * @return The copy of the specified population.
	 */
	public static Population getInstance(Population p) {
		return new Population(p);
	}
	
	
	/**
	 * Creates a population. All specified individuals that are repeated are
	 * inserted only once. 
	 * @param individuals The individuals of the population. 
	 */
	public static Population getInstance(List<Constant> individuals) {
		return new Population(individuals);
	}
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/
	
	/**
	 * Returns a copy of an individual from the population. 
	 * @param index The index of the individual in the population.
	 * @return A copy of an individual from the population.
	 */
	public Constant individualAt(int index) {
		return Constant.getInstance(individuals.get(index));
	}
	
		
	/**
	 * Returns the number of individuals in the population.
	 * @return The number of individuals in the population.
	 */
	public int size() {
		return individuals.size();
	}
	
	
	/**
	 * Returns true if the population contains the specified individual.
	 * @param individual The individual whose presence in the population is
	 * to be tested
	 * @return True if the population contains the specified individual, false
	 * otherwise.
	 */
	public boolean contains(Constant individual) {
		return individuals.contains(individual);
	}
	
	
	/**
	 * Returns the individuals of the population as a set.
	 * <p>
	 * The order of the individuals in the set is not guaranteed to be preserved.
	 * </p>
	 * @return A set of containing all individuals of the population.
	 */
	public Set<Constant> toSet() {
		return new HashSet<Constant>(individuals);
	}
	
	
	@Override
	public Iterator<Constant> iterator() {
		return individuals.iterator();
	}
	
	/* ************************************************************************
	 *    Setters
	 * ************************************************************************/
	
	/**
	 * Removes the individual specified from the population. If the individual
	 * does not exist, the population remains unchanged.
	 * @param individual The individual to be removed.
	 */
	public void remove(Constant individual) {
		individuals.remove(individual);
	}
	
	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

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
