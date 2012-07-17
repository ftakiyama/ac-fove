package br.usp.poli.takiyama.acfove;

import java.util.HashSet;
import java.util.Set;

import br.usp.poli.takiyama.cfove.Constraint;
import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.cfove.ParametricFactor;
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
public class AggregationParfactor implements ParametricFactor {
	
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

	
	
	public Set<ParametricFactor> sumOut() {
		
		/*
		 * INPUT: set of parfactors and aggregation parfactors Phi
		 *        aggregation parfactor gA from Phi
		 * OUTPUT: Phi \ {gA} U <C, {c}, Fm>
		 * 
		 * if conditions to sum out are met
		 *     m := floor( log2( |D(A):CA| ) )
		 *     bm....b0 := binary representation of |D(A):CA|
		 *     Fm := calculateF(m)
		 *     g := <C, {c}, Fm>
		 *     return Phi \ {gA} U g
		 * 
		 * procedure calculateF(String x, int k, bit[] binRepresentation) 
		 *     if k = 0
		 *         if x in range(p)
		 *             return Fp(x)
		 *         else
		 *             return 0
		 *     else
		 *         sum := 0 
		 *         if b_(m-k) = 0
		 *         	   for each y in range(c)
		 *                 for each z in range(c)
		 *                     if (y OPERATOR z == x)
		 *                         sum += calculateF(y, k-1, binRep) * calculateF(z, k-1, binRep)  
		 *             return sum
		 *         else
		 *             for each w in range(c)
		 *                 for each y in range(c)
		 *                     for each z in range(c)
		 *                         if (w OPERATOR y OPERATOR z == x)
		 *                             sum += Fp(w) * calculateF(y, k-1, binRep) * calculateF(z, k-1, binRep)  
		 *             return sum
		 * 
		 */
		
		// checks if C U CA is in normal form
		
		// checks if param(c) = param(p) \ A

		// checks if that no other parfactor or aggregation parfactor in Phi 
		// involves parameterized random variables that represent random 
		// variables from ground(p(...,A,...)).
		
		
		
		return null;
	}
	
	/**
	 * Reference: [Kisynski, 2010], page 89.
	 * <br>
	 * Calculates the resulting factor value when summing out the aggregation
	 * parfactor.
	 * @param x A value in range(c)
	 * @param k The index of the factor to calculate
	 * @param binaryRepresentation The binary representation of |D(A):C<sub>A</sub>|
	 * @return The value of F<sub>k</sub>(x).
	 */
	public Number getFactorValue(String x, int k, int[] binaryRepresentation) { // very ugly
		if (k == 0) {
			for (int i = 0; i < parent.getRangeSize(); i++) {
				if (x.equals(parent.getElementFromRange(i))) {
					return parentFactor.getTupleValue(i);
				}
			}
			return 0;
		} else {
			Double sum = Double.valueOf("0");
			if (binaryRepresentation[binaryRepresentation.length - k - 1] == 0) {
				for (int y = 0; y < child.getRangeSize(); y++) {
					for (int z = 0; z < child.getRangeSize(); z++) {
						if (operator.applyOn(child.getElementFromRange(y), child.getElementFromRange(z)).equals(x)) {
							double fy = getFactorValue(child.getElementFromRange(y), k - 1, binaryRepresentation).doubleValue();
							double fz = getFactorValue(child.getElementFromRange(z), k - 1, binaryRepresentation).doubleValue(); 
							sum +=  fy * fz; 
								   
						}
					}
				}
				return sum;
			} else {
				for (int w = 0; w < child.getRangeSize(); w++) {
					for (int y = 0; y < child.getRangeSize(); y++) {
						for (int z = 0; z < child.getRangeSize(); z++) {
							if (operator.applyOn(child.getElementFromRange(w), child.getElementFromRange(y), child.getElementFromRange(z)).equals(x)) {
								sum += parentFactor.getTupleValue(w) *
									   getFactorValue(child.getElementFromRange(y), k - 1, binaryRepresentation).doubleValue() *
									   getFactorValue(child.getElementFromRange(z), k - 1, binaryRepresentation).doubleValue();
							}
						}
					}
				}
				return sum;
				
			}
		}
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