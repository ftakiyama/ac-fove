package br.usp.poli.takiyama.prv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates objects from br.usp.poli.takiyama.cfove.prv for testing purposes.
 * @author ftakiyama
 *
 */
public final class PRV {
	
	public static Constant getConstant(String value) {
		return new Constant(value);
	}
	
	public static LogicalVariable getLogicalVariable(
			String name, 
			String individualPrefix, 
			int populationSize) 
			throws IllegalArgumentException {
		
		ArrayList<Constant> population = new ArrayList<Constant>();
		for (int i = 0; i < populationSize; i++) {
			population.add(new Constant(individualPrefix + i));
		}
		
		return new LogicalVariable(name, population);
	}
	
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
	
	public static ParameterizedRandomVariable getBooleanPrv(
			String functorName,
			LogicalVariable... parameters) 
			throws IllegalArgumentException {
		
		ArrayList<String> range = new ArrayList<String>();
		range.add("false");
		range.add("true");
		
		PredicateSymbol functor = new PredicateSymbol(functorName, range.toArray(new String[range.size()]));
		ArrayList<Term> terms = new ArrayList<Term>();
		for (LogicalVariable v : parameters) {
			terms.add(v);
		}

		return ParameterizedRandomVariable.getInstance(functor, terms);
		
	}
	
	/**
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
	
	public static ParameterizedRandomVariable getBooleanPrvWithoutParameter(
			String functorName) 
			throws IllegalArgumentException {
		
		ArrayList<String> parameters = new ArrayList<String>();
		ArrayList<Integer> populationSizes = new ArrayList<Integer>();
		
		return getBooleanPrv(functorName, parameters, populationSizes);
	}
	
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
