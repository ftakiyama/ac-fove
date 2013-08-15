package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.EqualityConstraint;
import br.usp.poli.takiyama.utils.Lists;
import br.usp.poli.takiyama.utils.Sets;

/**
 * Operations for {@link Prv}.
 * 
 * @author Felipe Takiyama
 *
 */
public final class Prvs {
	

	public static Substitution mgu(Prv prv1, Prv prv2) throws IllegalArgumentException {
		
		if (!areUnifiable(prv1, prv2)) {
			throw new IllegalArgumentException();
		}
		
		Stack<Constraint> buffer = pushEquations(prv1, prv2);
		List<Binding> mgu = new ArrayList<Binding>();
		
		while (!buffer.isEmpty()) {
			EqualityConstraint equation = (EqualityConstraint) buffer.pop();
			if (hasIdenticalTerms(equation)) {
				// do nothing
			} else if (equation.firstTerm().isVariable()) {
				Binding b = equation.toBinding();
				buffer = apply(b, buffer);
				mgu.add(b);
			} else if (equation.secondTerm().isVariable()) {
				Binding b = equation.toInverseBinding();
				buffer = apply(b, buffer);
				mgu.add(b);
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		Substitution result = Substitution.getInstance(mgu);
		return result;
	}
	
	private static boolean areUnifiable(Prv prv1, Prv prv2) {
		boolean sameFunctor = prv1.name().equals(prv2.name());
		boolean sameNumberOfParam = (prv1.terms().size() == prv2.terms().size());
		return sameFunctor && sameNumberOfParam;
	}
	
	private static Stack<Constraint> pushEquations(Prv prv1, Prv prv2) {
		Stack<Constraint> result = new Stack<Constraint>();
		for (int i = 0; i < prv1.terms().size(); i++) {
			Term t1 = prv1.terms().get(i);
			Term t2 = prv2.terms().get(i);
			result.push(EqualityConstraint.getInstance(t1, t2));
		}
		return result;
	}
	
	private static boolean hasIdenticalTerms(Constraint c) {
		return c.firstTerm().equals(c.secondTerm());
	}
		
	private static Stack<Constraint> apply(Binding b, Stack<Constraint> buffer) {
		Substitution s = Substitution.getInstance(b);
		for (Constraint e : buffer) {
			e = (EqualityConstraint) e.apply(s);
		}
		return buffer;
	}
	
	/**
	 * Returns <code>true</code> if the specified PRVs represent disjoint sets
	 * of random variables.
	 * This method does not consider constraints associated with PRVs.
	 * @param prv1
	 * @param prv2
	 * @return
	 */
	public static boolean areDisjoint(Prv prv1, Prv prv2) {
		
		/*
		 * This algorithm is similar to the one used in Shatter.
		 * 
		 * rename all logical variables
		 * get the MGU between these two variables
		 * if MGU is empty OR error:
		 *     return true
		 * else
		 *     if MGU is not consistent with constraints
		 *     	   return false
		 *     else
		 *     	   return true;
		 */
		
		// Renames all logical variables in PRVs
		boolean areDisjoint = false;
		List<LogicalVariable> allVariables = Lists.union(
				prv1.getCanonicalForm().parameters(), 
				prv2.getCanonicalForm().parameters());
		Prv renamed1 = prv1.apply(NameGenerator.rename(allVariables));
		Prv renamed2 = prv2.apply(NameGenerator.rename(allVariables));
		
		try {
			Substitution mgu = Prvs.mgu(renamed1.getCanonicalForm(), renamed2.getCanonicalForm());
			Set<Constraint> constraints = Sets.union(renamed1.constraints(), renamed2.constraints());
			if (mgu.isEmpty()) {
				// each PRV is a single random variable
				areDisjoint = !(renamed1.equals(renamed2));
			} else {
				areDisjoint = !mgu.isConsistentWith(constraints);
			}
		} catch (IllegalArgumentException e) {
			areDisjoint = true;
		}
		
		return areDisjoint;
		
		
//		boolean areDisjoint = false;
//		try {
//			// weak check, does not verify the case f(A) = f(A)
//			areDisjoint = Prvs.mgu(prv1.getCanonicalForm(), prv2.getCanonicalForm()).isEmpty();
//		} catch (IllegalArgumentException e) {
//			// are not unifiable
//			areDisjoint = prv1.constraints().equals(prv2.constraints());
//		}
//		RandomVariableSet rvs1 = RandomVariableSet.getInstance(prv1.getCanonicalForm(), prv1.constraints());
//		RandomVariableSet rvs2 = RandomVariableSet.getInstance(prv2.getCanonicalForm(), prv2.constraints());
//		if (rvs1.equals(rvs2)) {
//			areDisjoint = false;
//		}
//		return areDisjoint;
	}
	
	/* *************************************************************************
	 *     Deprecated methods
	 * ************************************************************************/
	
	/**
	 * @deprecated
	 */
	public static Constant getConstant(String value) {
		return Constant.getInstance(value);
	}
	
	/**
	 * @deprecated
	 */
	public static LogicalVariable getLogicalVariable(
			String name, 
			String individualPrefix, 
			int populationSize) 
			throws IllegalArgumentException {
		
		ArrayList<Constant> population = new ArrayList<Constant>();
		for (int i = 0; i < populationSize; i++) {
			population.add(Constant.getInstance(individualPrefix + i));
		}
		Population pop = Population.getInstance(population);
		
		return StdLogicalVariable.getInstance(name, pop);
	}
	
	/**
	 * @deprecated
	 */
	public static ParameterizedRandomVariable getPrv(
			String functorName,
			List<String> range,
			List<String> parameters,
			List<Integer> populationSizes) 
			throws IllegalArgumentException {
		
		if (parameters.size() != populationSizes.size())
			throw new IllegalArgumentException("The list of parameters must have" +
					"the same size of the list of populationSizes.");
		
		PredicateSymbol functor = new PredicateSymbol(functorName, range.toArray(new String[range.size()]));
		ArrayList<Term> terms = new ArrayList<Term>();
		for (int i = 0; i < parameters.size(); i++) {
			terms.add(getLogicalVariable(parameters.get(i), 
										 parameters.get(i).toLowerCase() + i, 
										 populationSizes.get(i)));
		}
		
		return ParameterizedRandomVariable.getInstance(functor, terms);
	}
	
	/**
	 * @deprecated
	 */
	public static ParameterizedRandomVariable getBooleanPrv(
			String functorName,
			List<String> parameters,
			List<Integer> populationSizes) 
			throws IllegalArgumentException {
		
		ArrayList<String> range = new ArrayList<String>();
		range.add("false");
		range.add("true");
		
		return getPrv(functorName, range, parameters, populationSizes);
	}
	
	/**
	 * @deprecated
	 */
	public static ParameterizedRandomVariable getBooleanPrv(
			String functorName,
			StdLogicalVariable... parameters) 
			throws IllegalArgumentException {
		
		ArrayList<String> range = new ArrayList<String>();
		range.add("false");
		range.add("true");
		
		PredicateSymbol functor = new PredicateSymbol(functorName, range.toArray(new String[range.size()]));
		ArrayList<Term> terms = new ArrayList<Term>();
		for (StdLogicalVariable v : parameters) {
			terms.add(v);
		}

		return ParameterizedRandomVariable.getInstance(functor, terms);
		
	}
	
	

	/**
	 * @deprecated
	 * Returns a parameterized random variable with boolean range, with the
	 * specified name and terms.
	 * <br>
	 * This method supports creation of parameterized random variables with
	 * mixed parameters, that is, the specified parameters can be either 
	 * logical variables or constants.
	 * <br>
	 * Please be aware that, if a constant is specified as a parameter, there
	 * will be no information regarding the population to which it belongs.
	 * 
	 * @param functorName The name of the functor
	 * @param terms A list of terms, can be LogicalVaribles or Constants.
	 * @return A parameterized random variable with boolean range, with the
	 * specified name and terms.
	 */
	public static ParameterizedRandomVariable getBooleanPrv(
			String functorName,
			Term...terms) {
		String [] range = new String[2];
		range[0] = "false";
		range[1] = "true";
		PredicateSymbol functor = new PredicateSymbol(functorName, range);
		
		return ParameterizedRandomVariable.getInstance(functor, Arrays.asList(terms));
	}
	
	/**
	 * @deprecated
	 */
	public static ParameterizedRandomVariable getBooleanPrvWithoutParameter(
			String functorName) 
			throws IllegalArgumentException {
		
		ArrayList<String> parameters = new ArrayList<String>();
		ArrayList<Integer> populationSizes = new ArrayList<Integer>();
		
		return getBooleanPrv(functorName, parameters, populationSizes);
	}
	
	/**
	 * @deprecated
	 */
	public static ParameterizedRandomVariable getBooleanPrvWithOneParameter(
			String functorName,
			int populationSize) 
			throws IllegalArgumentException {
		
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add("X");
		ArrayList<Integer> populationSizes = new ArrayList<Integer>();
		populationSizes.add(populationSize);
		
		return getBooleanPrv(functorName, parameters, populationSizes);
	}
	
	/**
	 * @deprecated
	 */
	public static ParameterizedRandomVariable getBooleanPrvWithTwoParameters(
			String functorName,
			int populationSizeForFirstParameter,
			int populationSizeForSecondParameter) 
			throws IllegalArgumentException {
		
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add("X");
		parameters.add("Y");
		ArrayList<Integer> populationSizes = new ArrayList<Integer>();
		populationSizes.add(populationSizeForFirstParameter);
		populationSizes.add(populationSizeForSecondParameter);
		
		return getBooleanPrv(functorName, parameters, populationSizes);
	}

	/**
	 * @deprecated
	 */
	public static ParameterizedRandomVariable getBooleanPrvWithThreeParameters(
			String functorName,
			int populationSizeForFirstParameter,
			int populationSizeForSecondParameter,
			int populationSizeForThirdParameter) 
			throws IllegalArgumentException {
		
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add("X");
		parameters.add("Y");
		parameters.add("Z");
		ArrayList<Integer> populationSizes = new ArrayList<Integer>();
		populationSizes.add(populationSizeForFirstParameter);
		populationSizes.add(populationSizeForSecondParameter);
		populationSizes.add(populationSizeForThirdParameter);
		
		return getBooleanPrv(functorName, parameters, populationSizes);
	}
}
