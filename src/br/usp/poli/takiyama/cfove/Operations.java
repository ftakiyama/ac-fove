package br.usp.poli.takiyama.cfove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//import com.google.common.collect.ImmutableSet;
//import com.google.common.collect.Sets;

import br.usp.poli.takiyama.cfove.prv.LogicalVariable;
import br.usp.poli.takiyama.cfove.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.ve.RandomVariable;
import br.usp.poli.takiyama.utils.Sets;

/**
 * Static utility methods pertaining to {@link Parfactor} instances.
 * @author ftakiyama
 *
 */
public final class Operations {
	
	public static List<Parfactor> liftedElimination(
			List<Parfactor> setOfParfactors, 
			Parfactor parfactor, 
			ParameterizedRandomVariable variable) {
		
		ArrayList<Parfactor> parfactors = new ArrayList<Parfactor>(setOfParfactors);
		
		if (allPreConditionsForLiftedEliminationAreOk(setOfParfactors, parfactor, variable)
				&& checkFirstConditionForLiftedElimination(setOfParfactors, parfactor, variable)
				&& checkSecondConditionForLiftedElimination(parfactor, variable)) {
				
			ParameterizedFactor newFactor = parfactor.getFactor().sumOut(variable);
			
			parfactors.remove(parfactor);
			
			Parfactor newParfactor = Parfactor.getInstance(
					parfactor.getConstraints(), 
					newFactor);
			
			double size1 = (double) parfactor.size();
			double size2 = (double) newParfactor.size();
			double exponent = size1 / size2;
			
			newParfactor = Parfactor.getInstance(
					parfactor.getConstraints(), 
					newFactor.pow(exponent));
			
			parfactors.add(newParfactor);
			
			return parfactors; 
		} 
		
		return parfactors;
		
	}
	
	
	private static boolean allPreConditionsForLiftedEliminationAreOk(
			List<Parfactor> setOfParfactors, 
			Parfactor parfactor, 
			ParameterizedRandomVariable variable) {
		
		/*
		 * TODO: check if parfactor is in normal form
		 * TODO: check if variable is not a counting formula  
		 */
		
		return setOfParfactors.contains(parfactor);
	}
	
	/**
	 * Returns true if no other parfactor in <code>setOfParfactors</code> 
	 * includes parameterized random variables that represent random variables 
	 * represented by <code>variable</code>.
	 * @param setOfParfactors A set of parfactors
	 * @param parfactor A normal form parfactor from <code>setOfParfactors</code>
	 * @param variable A parameterized random variable from <code>parfactor</code> 
	 * @return True if no other parfactor in <code>setOfParfactors</code> 
	 * includes parameterized random variables that represent random variables 
	 * represented by <code>variable</code>, false otherwise.
	 */
	private static boolean checkFirstConditionForLiftedElimination(
			List<Parfactor> setOfParfactors, 
			Parfactor parfactor, 
			ParameterizedRandomVariable variable) {
		
		ArrayList<Parfactor> setOfParfactorsWithoutTarget = 
				new ArrayList<Parfactor>(setOfParfactors);
		
		setOfParfactorsWithoutTarget.remove(parfactor);
		
		for (Parfactor currentParfactor : setOfParfactorsWithoutTarget) {
			
			List<RandomVariable> targetGroundInstances = variable
				.getGroundInstancesSatisfying(parfactor.getConstraints());
			
			for (ParameterizedRandomVariable prv : currentParfactor.getParameterizedRandomVariables()) {
				
				List<RandomVariable> currentGroundInstances = prv
					.getGroundInstancesSatisfying(currentParfactor
						.getConstraints());
				
				if (Sets.intersection(targetGroundInstances,
									  currentGroundInstances)
                    .isEmpty() == false) return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the set of logical variables in <code>variable</code> is a 
	 * superset of the union of logical variables in other parameterized 
	 * random variables from <code>parfactor</code>.
	 * @param parfactor The parfactor to check
	 * @param variable The variable to check
	 * @return True if the set of logical variables in <code>variable</code> is a 
	 * superset of the union of logical variables in other parameterized 
	 * random variables from <code>parfactor</code>, false otherwise.
	 */
	private static boolean checkSecondConditionForLiftedElimination(
			Parfactor parfactor, 
			ParameterizedRandomVariable variable) {
		
		ArrayList<ParameterizedRandomVariable> setOfParameterizedRandomVariablesWithoutTarget = 
			new ArrayList<ParameterizedRandomVariable>(parfactor.getParameterizedRandomVariables());
	
		setOfParameterizedRandomVariablesWithoutTarget.remove(variable);	
		
		HashSet<LogicalVariable> allLogicalVariables = new HashSet<LogicalVariable>();
		
		for (ParameterizedRandomVariable prv : setOfParameterizedRandomVariablesWithoutTarget) {
			allLogicalVariables = new HashSet<LogicalVariable>(
					Sets.union(prv.getParameters(), allLogicalVariables)); // not good
		}
		
		return variable.getParameters().containsAll(allLogicalVariables);		
	}
	
	
	/* ************************************************************************
	 *    MULTIPLICATION
	 * ************************************************************************/
	
	public static List<Parfactor> multiplication(
			List<Parfactor> setOfParfactors, 
			Parfactor firstParfactor,
			Parfactor secondParfactor) {
		/*
		 * if conditions are met
		 *     calculate g = <Ci U Cj, Vi U Vj, Fi x Fj>
		 *     ri := |gi| / |g|
		 *     rj := |gj| / |g|
		 *     remove gi and gj from set of parfactors
		 *     calculate g' = <Ci U Cj, Vi U Vj, Fi^ri x Fj^rj>
		 *     insert g' in the set of parfactors
		 * return set of parfactors    
		 */
		
		ArrayList<Parfactor> newSetOfParfactors = new ArrayList<Parfactor>(setOfParfactors);
		if (conditionsForMultiplicationAreSatisfied(firstParfactor, secondParfactor)) {
			Parfactor g = Parfactor.getInstance(
					Sets.union(firstParfactor.getConstraints(), secondParfactor.getConstraints()),
					firstParfactor.getFactor().multiply(secondParfactor.getFactor()));
			
			double firstExponent = ((double) firstParfactor.size()) / g.size();
			double secondExponent = ((double) secondParfactor.size()) / g.size();
			
			newSetOfParfactors.remove(firstParfactor);
			newSetOfParfactors.remove(secondParfactor);
			
			Parfactor product = Parfactor.getInstance(
					Sets.union(firstParfactor.getConstraints(), secondParfactor.getConstraints()),
					firstParfactor.getFactor().pow(firstExponent).multiply(secondParfactor.getFactor().pow(secondExponent)));
			
			newSetOfParfactors.add(product);
		}
		return newSetOfParfactors;
	}
	
	private static boolean conditionsForMultiplicationAreSatisfied( 
			Parfactor firstParfactor,
			Parfactor secondParfactor) {
		
		return checkFirstConditionForMultiplication(firstParfactor, secondParfactor) &&
			   checkSecondConditionForMultiplication(firstParfactor, secondParfactor);
	}
	
	private static boolean checkFirstConditionForMultiplication(
			Parfactor firstParfactor,
			Parfactor secondParfactor) {

		/* First condition: sets of random variables represented by
		 * parameterized random variables from each parfactor are identical 
		 * or disjoint 
		 */
		for (ParameterizedRandomVariable v1 : firstParfactor.getParameterizedRandomVariables()) {
			for (ParameterizedRandomVariable v2 : secondParfactor.getParameterizedRandomVariables()) {
				if (!Sets
					.intersection(v1
								  .getGroundInstancesSatisfying(
										  firstParfactor.getConstraints()),
								  v2
								  .getGroundInstancesSatisfying(
										  secondParfactor.getConstraints()))
					.isEmpty() &&
					!v1.getGroundInstancesSatisfying(
							firstParfactor.getConstraints())
					.equals(
					v2.getGroundInstancesSatisfying(
							secondParfactor.getConstraints()))) {
					
					return false; // this is horrible and unreadable
				}
			}
		}
		
		return true;
	}
	
	private static boolean checkSecondConditionForMultiplication(
			Parfactor firstParfactor,
			Parfactor secondParfactor) {
		
		ArrayList<LogicalVariable> logicalVariablesFromFirstParfactor = 
			new ArrayList<LogicalVariable>(firstParfactor.getLogicalVariables());
		
		for (ParameterizedRandomVariable v1 : firstParfactor.getParameterizedRandomVariables()) {
			for (ParameterizedRandomVariable v2 : secondParfactor.getParameterizedRandomVariables()) {
				if (v1.getGroundInstancesSatisfying(
							firstParfactor.getConstraints())
					.equals(
					v2.getGroundInstancesSatisfying(
							secondParfactor.getConstraints()))) {
					
					logicalVariablesFromFirstParfactor.removeAll(v1.getParameters());
					
					if (!v1.getParameters().equals(v2.getParameters()))
						return false;
				}
			}			
		}
		
		if (Sets.intersection(logicalVariablesFromFirstParfactor,
							  new ArrayList<LogicalVariable>(secondParfactor.getLogicalVariables()))
				.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
