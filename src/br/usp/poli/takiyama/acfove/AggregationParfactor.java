package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.usp.poli.takiyama.acfove.operator.BooleanOperator;
import br.usp.poli.takiyama.cfove.ParameterizedFactor;
import br.usp.poli.takiyama.cfove.SimpleParfactor;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.Tuple;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.LogicalVariableNameGenerator;
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;


public class AggregationParfactor implements Parfactor {

	private final ParameterizedRandomVariable parent;
	private final ParameterizedRandomVariable child;
	
	private final ParameterizedFactor factor;
	
	private final BooleanOperator operator; // should think of something more generic
	 
	private final HashSet<Constraint> constraintsOnExtraVariable; 
	private final HashSet<Constraint> otherConstraints;
	
	private final LogicalVariable extraVariable;
	
	// Builder pattern
	public static class Builder {
		// required parameters
		private final ParameterizedRandomVariable p;
		private final ParameterizedRandomVariable c;
		private final BooleanOperator op;
		private final LogicalVariable lv;
		
		// optional parameters
		private ParameterizedFactor f; 
		private HashSet<Constraint> constraintsOnExtra; 
		private HashSet<Constraint> otherConstraints;
		
		public Builder (
				ParameterizedRandomVariable p,
				ParameterizedRandomVariable c,
				BooleanOperator op) 
				throws IllegalArgumentException {
			
			this.p = ParameterizedRandomVariable.getInstance(p);
			this.c = ParameterizedRandomVariable.getInstance(c);
			this.op = op;
			this.f = ParameterizedFactor.getConstantInstance(p);
			this.constraintsOnExtra = new HashSet<Constraint>();
			this.otherConstraints = new HashSet<Constraint>();
			
			HashSet<LogicalVariable> vars = 
					new HashSet<LogicalVariable>(p.getParameters());
			vars.removeAll(c.getParameters());
			if (vars.size() == 1) {
				this.lv = new LogicalVariable(vars.iterator().next());
			} else {
				throw new IllegalArgumentException(p 
						+ " does not have 1 extra logical variable: "
						+ vars);
			}
		}
		
		public Builder addConstraintsOnExtra(Set<Constraint> c) {
			this.constraintsOnExtra.addAll(c);
			return this;
		}
		
		public Builder addOtherConstraints(Set<Constraint> c) {
			this.otherConstraints.addAll(c);
			return this;
		}
		
		public Builder addConstraint(Constraint c) {
			if (c.contains(lv)) {
				this.constraintsOnExtra.add(c);
			} else {
				this.otherConstraints.add(c);
			}
			return this;
		}
		
		public Builder addConstraints(Set<Constraint> c) {
			for (Constraint constraint : c) {
				this.addConstraint(constraint);
			}
			return this;
		}
		
		public Builder factor(ParameterizedFactor f) {
			this.f = ParameterizedFactor.getInstance(f);
			return this;
		}
		
		public AggregationParfactor build() {
			return new AggregationParfactor(this);
		}
	}
	
	private AggregationParfactor(Builder builder) {
		this.parent = builder.p;
		this.child = builder.c;
		this.factor = builder.f;
		this.operator = builder.op;
		this.constraintsOnExtraVariable = builder.constraintsOnExtra;
		this.otherConstraints = builder.otherConstraints;
		this.extraVariable = builder.lv;
	}
	
	@Override
	public boolean contains(ParameterizedRandomVariable variable) {
		return (variable.equals(this.child) || variable.equals(this.parent));
	}

	@Override
	public ParameterizedFactor getFactor() {
		return ParameterizedFactor.getInstance(factor);
	}

	@Override
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables() {
		List<ParameterizedRandomVariable> vars = 
			new ArrayList<ParameterizedRandomVariable>();
		vars.add(child);
		vars.add(parent);
		return vars;
	}

	@Override
	public ParameterizedRandomVariable getChildVariable() {
		return ParameterizedRandomVariable.getInstance(child);
	}

	/**
	 * Returns the set of all constraints of this parfactors: the ones that
	 * involve the extra logical variable in the parent node and the ones that
	 * do not involve this variable.
	 * @return All constraints in the parfactor
	 */
	public Set<Constraint> getConstraints() {
		Set<Constraint> constraints = new HashSet<Constraint>();
		constraints.addAll(constraintsOnExtraVariable);
		constraints.addAll(otherConstraints);
		return constraints;
	}

	@Override
	public Set<LogicalVariable> getLogicalVariables() {
		Set<LogicalVariable> vars = new HashSet<LogicalVariable>();
		vars.addAll(child.getParameters());
		vars.addAll(parent.getParameters());
		return vars;
	}

	@Override
	public int size() {
		Set<LogicalVariable> vars = this.getLogicalVariables();
		vars.remove(extraVariable);
		int numFactors = 1;
		for (LogicalVariable lv : vars) {
			numFactors = numFactors * lv.getSizeOfPopulationSatisfying(otherConstraints);
		}
		
		return numFactors;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public Parfactor restoreLogicalVariableNames() {
		AggregationParfactor ap = this;
		for (LogicalVariable lv : this.getLogicalVariables()) {
			ap = applySubstitution(lv, LogicalVariableNameGenerator.restore(lv));
		}
		return ap;
	}
	
	private AggregationParfactor applySubstitution(LogicalVariable oldVar, 
			LogicalVariable newVar) {
		
		Binding sub = Binding.create(oldVar, newVar);
		
		HashSet<Constraint> constraintsOnExtra = new HashSet<Constraint>();
		HashSet<Constraint> constraintsRemaining = new HashSet<Constraint>();
		
		for (Constraint c : this.constraintsOnExtraVariable) {
			Constraint nc = c.applySubstitution(sub);
			if (nc != null)
				constraintsOnExtra.add(nc);
		}
		
		// TODO complete
		return null;
	}
	

	@Override
	public List<Parfactor> split(Binding s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor count(LogicalVariable lv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Parfactor> propositionalize(LogicalVariable lv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor expand(CountingFormula countingFormula, Term term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor fullExpand(CountingFormula countingFormula) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor multiply(Parfactor parfactor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor sumOut(ParameterizedRandomVariable prv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor replaceLogicalVariablesConstrainedToSingleConstant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor renameLogicalVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Parfactor> splitOnMgu(Substitution mgu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Parfactor> splitOnConstraints(Set<Constraint> constraints) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Parfactor> unify(Parfactor parfactor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/* ************************************************************************
	 * 
	 *   Class specific methods
	 * 
	 * ***********************************************************************/
	
	
	/* ====================================================================== */
	/*   Conversion to standard parfactors                                    */
	/* ====================================================================== */
	
	/**
	 * Converts this aggregation parfactor into standard parfactors.
	 * <br>
	 * <br>
	 * The conversion can be made only if the set of all constraints in
	 * this aggregation parfactor is in the normal form. One must check this
	 * condition before calling this method.
	 * <br>
	 * <br>
	 * The conversion results in two parfactors, one of them involving a 
	 * counting formula. This method returns a list of the resulting parfactors
	 * in the following order: the first does involves counting formulas and
	 * the second does not.
	 * <br>
	 * <b>The result is a list so I can retrieve the parfactors I want later,
	 * although I'm not sure if this will be necessary</b>
	 * 
	 * @return The result of converting this aggregation parfactor into 
	 * standard parfactors.
	 */
	public List<Parfactor> convertToParfactor() {
		
		CountingFormula cf = CountingFormula.getInstance(extraVariable, 
				constraintsOnExtraVariable, parent);
		
		List<ParameterizedRandomVariable> vars = 
				new ArrayList<ParameterizedRandomVariable>(2);
		vars.add(cf);
		vars.add(child);
		
		Iterator<Tuple> it = ParameterizedFactor.getIteratorOverTuples(vars);
		List<Number> values = new ArrayList<Number>();
		while (it.hasNext()) {
			Tuple current = it.next();
			if (aggregationIsConsistent(current, cf)) {
				values.add(1.0);
			} else {
				values.add(0.0);
			}
		}
		
		// first parfactor
		ParameterizedFactor factor = ParameterizedFactor.getInstance("", vars, values);
		Parfactor parfactor = SimpleParfactor.getInstance(this.otherConstraints, factor);
		factor = null; // object won't be used further
		
		List<Parfactor> result = new ArrayList<Parfactor>(2);
		result.add(parfactor);
		
		// second parfactor
		parfactor = SimpleParfactor.getInstance(getConstraints(), this.factor);
		result.add(parfactor);
		
		return result;
	}
	
	/**
	 * Calculates r = &otimes;(x &in; range(p), &otimes;(i = 1, H(v(cf), x), x))
	 * and returns true if r = v(c).
	 * <br>
	 * The function v is an assignment of values to PRVs. 
	 * @param t The tuple that represents the assignment of values
	 * @param cf The counting formula being analyzed
	 * @return True if the aggregation of parent nodes for the given 
	 * assignment of values is consistent with the child node value in 
	 * the tuple
	 */
	private boolean aggregationIsConsistent(Tuple t, CountingFormula cf) {
		
		int cfRangeIndex =  t.get(0);
		Set<Boolean> s = new HashSet<Boolean>(2 * this.parent.getRangeSize());

		for (String e : this.parent.getRange()) {
			int bucketIndex = this.parent.getRange().indexOf(e);
			int count = cf.getCount(cfRangeIndex, bucketIndex);
			
			if (count > 0) {
				Boolean eToBool = Boolean.valueOf(e);
				Boolean intermediate = this.operator.applyOn(eToBool, count); // <-- "returns the specified element" is wrong
				
				s.add(intermediate);
			}
		}
		
		Boolean b = this.operator.applyOn(s).booleanValue();
		int childRangeIndex = t.get(1);
		String childRangeElem = this.child.getElementFromRange(childRangeIndex);
		
		return (b.compareTo(Boolean.valueOf(childRangeElem)) == 0);
	}
	
	// TODO test conversion to parfactor
	
	/* ====================================================================== */
	/*   toString, hashCode and equals                                        */
	/* ====================================================================== */
	// TODO implement those
}

//package br.usp.poli.takiyama.acfove;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import br.usp.poli.takiyama.common.Constraint;
//import br.usp.poli.takiyama.cfove.ParameterizedFactor;
//import br.usp.poli.takiyama.cfove.SimpleParfactor;
//import br.usp.poli.takiyama.cfove.SimpleParfactor;
//import br.usp.poli.takiyama.prv.LogicalVariable;
//import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
//import br.usp.poli.takiyama.utils.Sets;
//
///**
// * An aggregation parfactor [Kisynski, 2010] is a hextuple &lang; C, 
// * p(...,A,...), c(...), F<sub>p</sub>, &otimes; , C<sub>A</sub> &rang;, 
// * where
// * <br>
// * <li> p(...,A,...) and c(...) are parameterized random variables 
// * <li> the range of p is a subset of the range of c 
// * <li> A is the only logical variable in p(...,A,...) that is not in c(...) 
// * <li> C is a set of inequality constraints not involving A 
// * <li> F<sub>p</sub> is a factor from the range of p to real numbers 
// * <li> &otimes; is a commutative and associative deterministic binary operator 
// * over the range of c 
// * <li> C<sub>A</sub> is a set of inequality constraints involving A, 
// * such that (D(A) : C<sub>A</sub>) &ne; &empty;.
// * @author ftakiyama
// *
// */
//public class AggregationParfactor implements SimpleParfactor {
//	
//	private ParameterizedRandomVariable parent;
//	private ParameterizedRandomVariable child;
//	
//	private ParameterizedFactor parentFactor;
//	
//	private Operator operator;
//	 
//	private HashSet<Constraint> constraintsInA; // I must guarantee that Constraint has a good hashCode() method
//	private HashSet<Constraint> constraintsNotInA;
//	
//	private LogicalVariable extraVariableInParent;
//	
//	private String name;
//	
//	private AggregationParfactor(
//			String name,
//			Set<Constraint> constraintsInA,
//			ParameterizedRandomVariable parent,
//			ParameterizedRandomVariable child,
//			ParameterizedFactor factor,
//			Operator operator,
//			Set<Constraint> constraintsNotInA) {
//		
//		this.name = name;
//		this.parent = parent;
//		this.child = child;
//		this.parentFactor = factor;
//		this.operator = operator;
//		this.constraintsInA = new HashSet<Constraint>(constraintsInA);
//		this.constraintsNotInA = new HashSet<Constraint>(constraintsNotInA);
//		Set<LogicalVariable> parameters = new HashSet<LogicalVariable>(parent.getParameters());
//		parameters.removeAll(child.getParameters());
//		LogicalVariable[] extraParameter = parameters.toArray(new LogicalVariable[1]);
//		this.extraVariableInParent = extraParameter[0];
//	}
//	
//	public static AggregationParfactor getInstance(
//			Set<Constraint> constraintsInA,
//			ParameterizedRandomVariable parent,
//			ParameterizedRandomVariable child,
//			ParameterizedFactor factor,
//			Operator operator,
//			Set<Constraint> constraintsNotInA) {
//		
//		return new AggregationParfactor("", constraintsInA, parent, child, factor, operator, constraintsNotInA);
//	}
//	
//	public static AggregationParfactor getInstance(
//			String name,
//			Set<Constraint> constraintsInA,
//			ParameterizedRandomVariable parent,
//			ParameterizedRandomVariable child,
//			ParameterizedFactor factor,
//			Operator operator,
//			Set<Constraint> constraintsNotInA) {
//		
//		return new AggregationParfactor(name, constraintsInA, parent, child, factor, operator, constraintsNotInA);
//	}
//	
//	public static AggregationParfactor getInstanceWithoutConstraints(
//			ParameterizedRandomVariable parent,
//			ParameterizedRandomVariable child,
//			ParameterizedFactor factor,
//			Operator operator) {
//
//		return new AggregationParfactor("", new HashSet<Constraint>(), parent, child, factor, operator, new HashSet<Constraint>());
//	}
//	
//	
//	public boolean contains(ParameterizedRandomVariable variable) {
//		return variable.equals(parent) || variable.equals(child);
//	}
//	
//	public ParameterizedFactor getFactor() {
//		return this.parentFactor;
//	}
//	
//	@Override
//	public List<ParameterizedRandomVariable> getParameterizedRandomVariables() {
//		return this.parentFactor.getParameterizedRandomVariables();
//	}
//	
//	@Override
//	public ParameterizedRandomVariable getChildVariable() {
//		return this.child;
//	}
//
//	@Override
//	public Set<Constraint> getConstraints() {
//		return this.constraintsInA; // TODO correct this
//	}
//	
//	
//	/*
//	 ***************************************************************************
//	 *
//	 * OPERATIONS
//	 * 
//	 ***************************************************************************
//	 */
//	
//	/**
//	 * Multiplies the aggregation parfactor by specified parfactor.
//	 * <br>
//	 * Let g<sub>A</sub> = 
//	 * &lang; C, p(...,A,...), c(...), F<sub>p</sub>, &otimes;, C<sub>A</sub> &rang; 
//	 * be an aggregation parfactor from &Phi; and g<sub>1</sub> = 
//	 * &lang; C &cup; C<sub>A</sub>, {p(...,A,...)}, F<sub>1</sub> &rang; 
//	 * be a parfactor from &Phi;. 
//	 * <br>
//	 * Then g<sub>A</sub> x g<sub>1</sub> =  
//	 * &lang; C, p(...,A,...), c(...), F<sub>p</sub> x F<sub>1</sub>, 
//	 * &otimes;, C<sub>A</sub> &rang;.
//	 * <br>
//	 * <br>
//	 * The specified parfactor must follow some rules:
//	 * <li> The set of constraints must be the union of the set of constraints
//	 * on A and the set of constraints not in A
//	 * <li> The only parameterized random variable must be equal to the parent 
//	 * variable in the aggregation parfactor
//	 * 
//	 * @param parfactor The parfactor to multiply for.
//	 * @return The product of the aggregation parfactor and the specified
//	 * parfactor.
//	 * @throws IllegalArgumentException If the parfactor provided does not
//	 * obey the constraints listed above.
//	 */
//	public Set<SimpleParfactor> multiply(Set<SimpleParfactor> setOfParfactors, SimpleParfactor parfactor) 
//			throws IllegalArgumentException {
//		
//		if (this.canBeMultipliedBy(parfactor)) {
//			
//			setOfParfactors.remove(parfactor);
//			setOfParfactors.add(
//					getInstance(this.constraintsInA,
//								this.parent,
//								this.child,
//								this.parentFactor.multiply(parfactor.getFactor()),
//								this.operator,
//								this.constraintsNotInA));
//			
//			return setOfParfactors;
//			
//		} else {
//			throw new IllegalArgumentException("Cannot multiply because " +
//					"pre-requisites are not met. " + this.toString() + "\n\n" + parfactor.toString());
//		}
//	}
//	
//	public boolean canBeMultipliedBy(SimpleParfactor parfactor) {
//		if (!(parfactor instanceof SimpleParfactor)) {
//			return false;
//		}
//		SimpleParfactor p = (SimpleParfactor) parfactor;
//		
//		return Sets.union(this.constraintsInA, 
//				   		  this.constraintsNotInA)
//				   .equals(new HashSet<Constraint>(p.getConstraints())) &&
//			   p.getParameterizedRandomVariables().size() == 1 &&
//			   p.getParameterizedRandomVariables().get(0).equals(this.parent);
//	}
//
//	
//	
//	public Set<SimpleParfactor> sumOut(Set<SimpleParfactor> setOfParfactors, ParameterizedRandomVariable variable) {
//		
//		/*
//		 * INPUT: set of parfactors and aggregation parfactors Phi
//		 *        aggregation parfactor gA from Phi
//		 * OUTPUT: Phi \ {gA} U <C, {c}, Fm>
//		 * 
//		 * if conditions to sum out are met
//		 *     m := floor( log2( |D(A):CA| ) )
//		 *     bm....b0 := binary representation of |D(A):CA|
//		 *     Fm := calculateF(m)
//		 *     g := <C, {c}, Fm>
//		 *     return Phi \ {gA} U g
//		 * 
//		 * procedure calculateF(String x, int k, bit[] binRepresentation) 
//		 *     if k = 0
//		 *         if x in range(p)
//		 *             return Fp(x)
//		 *         else
//		 *             return 0
//		 *     else
//		 *         sum := 0 
//		 *         if b_(m-k) = 0
//		 *         	   for each y in range(c)
//		 *                 for each z in range(c)
//		 *                     if (y OPERATOR z == x)
//		 *                         sum += calculateF(y, k-1, binRep) * calculateF(z, k-1, binRep)  
//		 *             return sum
//		 *         else
//		 *             for each w in range(c)
//		 *                 for each y in range(c)
//		 *                     for each z in range(c)
//		 *                         if (w OPERATOR y OPERATOR z == x)
//		 *                             sum += Fp(w) * calculateF(y, k-1, binRep) * calculateF(z, k-1, binRep)  
//		 *             return sum
//		 * 
//		 */
//			
//		if (conditionsToSumOutAreMet(setOfParfactors)) {
//			Integer[] sizeOfDomainAsBinary = getBinaryRepresentationOf(extraVariableInParent.getIndividualsSatisfying(constraintsInA).size());
//			
//			ArrayList<ParameterizedRandomVariable> variables = new ArrayList<ParameterizedRandomVariable>();
//			variables.add(child);
//			
//			ArrayList<Number> mapping = new ArrayList<Number>();
//			for (int i = 0; i < child.getRangeSize(); i++) {
//				mapping.add(getFactorValue(child.getElementFromRange(i), 
//										   sizeOfDomainAsBinary.length - 1, 
//										   sizeOfDomainAsBinary));
//			}
//			
//			setOfParfactors.remove(this);
//			setOfParfactors.add(
//					SimpleParfactor.getInstance(constraintsNotInA, 
//							              ParameterizedFactor.getInstance(this.name, 
//							            		                          variables, 
//							            		                          mapping)));
//			
//		}
//		
//		return setOfParfactors;
//	}
//	
//	//TODO: implement this
//	private boolean conditionsToSumOutAreMet(Set<SimpleParfactor> setOfParfactors) {
//		// checks if C U CA is in normal form
//		
//		// checks if param(c) = param(p) \ A
//
//		// checks if that no other parfactor or aggregation parfactor in Phi 
//		// involves parameterized random variables that represent random 
//		// variables from ground(p(...,A,...)).
//		return true;
//	}
//	
//	/**
//	 * Returns the binary representation of a positive integer as an array
//	 * of Integers.
//	 * @param number The number to be converted
//	 * @return The binary representation of a positive integer
//	 */
//	private Integer[] getBinaryRepresentationOf(int number) { //TODO: rewrite this ugly thing
//		ArrayList<Integer> binaryRepresentation = new ArrayList<Integer>();
//		binaryRepresentation.add(number % 2);
//		number = number / 2;
//		while (number != 0) {
//			binaryRepresentation.add(number % 2);
//			number = number / 2;
//		}
//		Collections.reverse(binaryRepresentation);
//		return binaryRepresentation.toArray(new Integer[binaryRepresentation.size()]);
//	}
//	
//	/**
//	 * Reference: [Kisynski, 2010], page 89.
//	 * <br>
//	 * Calculates the resulting factor value when summing out the aggregation
//	 * parfactor.
//	 * <br>
//	 * TODO: this method is recursive. Make it non-recursive. 
//	 * @param x A value in range(c)
//	 * @param k The index of the factor to calculate
//	 * @param binaryRepresentation The binary representation of |D(A):C<sub>A</sub>|
//	 * @return The value of F<sub>k</sub>(x).
//	 */
//	private Number getFactorValue(String x, int k, Integer[] binaryRepresentation) { //TODO: rewrite this ugly thing TODO: cache results
//		if (k == 0) {
//			for (int i = 0; i < parent.getRangeSize(); i++) {
//				if (x.equals(parent.getElementFromRange(i))) {
//					return parentFactor.getTupleValue(i);
//				}
//			}
//			return 0;
//		} else {
//			Double sum = Double.valueOf("0");
//			if (binaryRepresentation[binaryRepresentation.length - k - 1] == 0) {
//				for (int y = 0; y < child.getRangeSize(); y++) {
//					for (int z = 0; z < child.getRangeSize(); z++) {
//						if (operator.applyOn(child.getElementFromRange(y), child.getElementFromRange(z)).equals(x)) {
//							double fy = getFactorValue(child.getElementFromRange(y), k - 1, binaryRepresentation).doubleValue();
//							double fz = getFactorValue(child.getElementFromRange(z), k - 1, binaryRepresentation).doubleValue(); 
//							sum +=  fy * fz; 
//								   
//						}
//					}
//				}
//				return sum;
//			} else {
//				for (int w = 0; w < child.getRangeSize(); w++) {
//					for (int y = 0; y < child.getRangeSize(); y++) {
//						for (int z = 0; z < child.getRangeSize(); z++) {
//							if (operator.applyOn(child.getElementFromRange(w), child.getElementFromRange(y), child.getElementFromRange(z)).equals(x)) {
//								sum += parentFactor.getTupleValue(w) *
//									   getFactorValue(child.getElementFromRange(y), k - 1, binaryRepresentation).doubleValue() *
//									   getFactorValue(child.getElementFromRange(z), k - 1, binaryRepresentation).doubleValue();
//							}
//						}
//					}
//				}
//				return sum;
//				
//			}
//		}
//	}
//	
//	@Override
//	public String toString() {
//		return "\n<\n" + 
//			   constraintsNotInA.toString() + ",\n" +
//			   parent.toString() + ",\n" +
//			   child.toString() + ",\n" +
//			   parentFactor.toString() +
//			   operator.toString() + ",\n" +
//			   constraintsInA.toString() + ",\n" +
//			   ">\n";
//	}
//
//	
//}
