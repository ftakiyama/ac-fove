package br.usp.poli.takiyama.common;

import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;

/**
 * @deprecated
 * This class represents the factor equal to the product of all factors
 * represented by elements of a set of parfactors.
 * @author ftakiyama
 *
 */
public class Product {

	private final Set<Parfactor> parfactors;
	private final RandomVariableSet rvSet;
	
	private Product(RandomVariableSet rvSet, Set<Parfactor> parfactors) {
		this.rvSet = RandomVariableSet.getInstance(rvSet);
		this.parfactors = new HashSet<Parfactor>(parfactors);
	}
	
	private Product(Set<Parfactor> parfactors) {
		ParameterizedRandomVariable prv = ParameterizedRandomVariable.getEmptyInstance();
		Set<Constraint> constraints = new HashSet<Constraint>(0);
		this.rvSet = RandomVariableSet.getInstance(prv, constraints);
		this.parfactors = new HashSet<Parfactor>(parfactors);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) 
			return true;
		if (!(other instanceof Product))
			return false;
		Product o = (Product) other;
		return ((this.rvSet == null) 
						? (o.rvSet == null) 
						: (this.rvSet.equals(o.rvSet)))
				&& ((this.parfactors == null) 
						? (o.parfactors == null) 
						: (this.parfactors.equals(o.parfactors)));
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(this.rvSet.toString());
		for (Parfactor p : this.parfactors) {
			result.append(p.toString()).append("\n");
		}
		return result.toString();
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 + result + rvSet.hashCode();
		result = 31 + result + parfactors.hashCode();
		return result;
	}
}