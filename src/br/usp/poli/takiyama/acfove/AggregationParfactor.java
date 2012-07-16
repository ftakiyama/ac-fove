package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.cfove.Constraint;
import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.cfove.Parfactor;
import br.usp.poli.takiyama.cfove.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.utils.Sets;

/**
 * An aggregation parfactor [Kisynski, 2010] is a hextuple &lang; C, 
 * p(...,A,...), c(...), F<sub>p</sub>, &otimes; , C<sub>A</sub> &rang;, 
 * where
 * <br>
 * <li> p(...,A,...) and c(...) are parameterized random variables 
 * <li> the range of p is a subset of the range of c 
 * <li> A is the only logical variable in p(...,A,...) that is not in c(...) 
 * <li> C is a set of inequality constraints not involving A 
 * <li> F<sub>p</sub> is a factor from the range of p to real numbers 
 * <li> &otimes; is a commutative and associative deterministic binary operator 
 * over the range of c 
 * <li> C<sub>A</sub> is a set of inequality constraints involving A, 
 * such that (D(A) : C<sub>A</sub>) &ne; &empty;.
 * @author ftakiyama
 *
 */
public class AggregationParfactor {
	
	ParameterizedRandomVariable parent;
	ParameterizedRandomVariable child;
	
	ParameterizedFactor parentFactor;
	
	Operator operator;
	 
	HashSet<Constraint> constraintsInA; // I must guarantee that Constraint has a good hashCode() method
	HashSet<Constraint> constraintsNotInA;
	
	
	private AggregationParfactor(
			Set<Constraint> constraintsInA,
			ParameterizedRandomVariable parent,
			ParameterizedRandomVariable child,
			ParameterizedFactor factor,
			Operator operator,
			Set<Constraint> constraintsNotInA) {
		
		this.parent = parent;
		this.child = child;
		this.parentFactor = factor;
		this.operator = operator;
		this.constraintsInA = new HashSet<Constraint>(constraintsInA);
		this.constraintsNotInA = new HashSet<Constraint>(constraintsNotInA);
	}
	
	public static AggregationParfactor getInstance(
			Set<Constraint> constraintsInA,
			ParameterizedRandomVariable parent,
			ParameterizedRandomVariable child,
			ParameterizedFactor factor,
			Operator operator,
			Set<Constraint> constraintsNotInA) {
		
		return new AggregationParfactor(constraintsInA, parent, child, factor, operator, constraintsNotInA);
	}
	
	
	/**
	 * Multiplies the aggregation parfactor by specified parfactor.
	 * <br>
	 * Let g<sub>A</sub> = 
	 * &lang; C, p(...,A,...), c(...), F<sub>p</sub>, &otimes;, C<sub>A</sub> &rang; 
	 * be an aggregation parfactor from &Phi; and g<sub>1</sub> = 
	 * &lang; C &cup; C<sub>A</sub>, {p(...,A,...)}, F<sub>1</sub> &rang; 
	 * be a parfactor from &Phi;. 
	 * <br>
	 * Then g<sub>A</sub> x g<sub>1</sub> =  
	 * &lang; C, p(...,A,...), c(...), F<sub>p</sub> x F<sub>1</sub>, 
	 * &otimes;, C<sub>A</sub> &rang;.
	 * <br>
	 * <br>
	 * The specified parfactor must follow some rules:
	 * <li> The set of constraints must be the union of the set of constraints
	 * on A and the set of constraints not in A
	 * <li> The only parameterized random variable must be equal to the parent 
	 * variable in the aggregation parfactor
	 * 
	 * @param parfactor The parfactor to multiply for.
	 * @return The product of the aggregation parfactor and the specified
	 * parfactor.
	 * @throws IllegalArgumentException If the parfactor provided does not
	 * obey the constraints listed above.
	 */
	public AggregationParfactor multiply(Parfactor parfactor) 
			throws IllegalArgumentException {
		
		if (Sets.union(this.constraintsInA, 
					   this.constraintsNotInA)
				.equals(new HashSet<Constraint>(parfactor.getConstraints())) &&
			parfactor.getParameterizedRandomVariables().size() == 1 &&
			parfactor.getParameterizedRandomVariables().get(0).equals(this.parent)) {
		
			return getInstance(this.constraintsInA,
							   this.parent,
							   this.child,
							   this.parentFactor.multiply(parfactor.getFactor()),
							   this.operator,
							   this.constraintsNotInA);
		} else {
			throw new IllegalArgumentException("Cannot multiply because " +
					"pre-requisites are not met.");
		}
		
		
	}

	public AggregationParfactor sumOut() {
		return null;
	}
	
	@Override
	public String toString() {
		return "\n<\n" + 
			   constraintsNotInA.toString() + ",\n" +
			   parent.toString() + ",\n" +
			   child.toString() + ",\n" +
			   parentFactor.toString() +
			   operator.toString() + ",\n" +
			   constraintsInA.toString() + ",\n" +
			   ">\n";
	}
}
