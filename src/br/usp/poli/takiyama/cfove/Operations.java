package br.usp.poli.takiyama.cfove;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import br.usp.poli.takiyama.cfove.prv.LogicalVariable;
import br.usp.poli.takiyama.cfove.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.ve.Factor;
import br.usp.poli.takiyama.ve.FactorOperation;
import br.usp.poli.takiyama.ve.RandomVariable;

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
			
			ArrayList<ParameterizedRandomVariable> newSetOfParameterizedRandomVariables = 
				new ArrayList<ParameterizedRandomVariable>(parfactor.getParameterizedRandomVariables());
		
			newSetOfParameterizedRandomVariables.remove(variable);	
			
			Factor newFactor = FactorOperation
					.sumOut(parfactor.getFactor(), 
							variable.getRandomVariableRepresetantion());
			
			parfactors.remove(parfactor);
			
			Parfactor newParfactor = new Parfactor(
					parfactor.getConstraints(), 
					newSetOfParameterizedRandomVariables,
					newFactor);
			
			newParfactor = new Parfactor(
					parfactor.getConstraints(), 
					newSetOfParameterizedRandomVariables,
					FactorOperation.pow(newFactor, 
							new BigDecimal(parfactor.size())
									.divide(new BigDecimal(newFactor.size()))));
			
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
				.getGroundInstancesSatisfyingConstraints(parfactor.getConstraints());
			
			for (ParameterizedRandomVariable prv : currentParfactor.getParameterizedRandomVariables()) {
				
				List<RandomVariable> currentGroundInstances = prv
					.getGroundInstancesSatisfyingConstraints(currentParfactor
						.getConstraints());
				
				if (Sets.intersection(
						ImmutableSet
						.copyOf(targetGroundInstances
							.toArray(new RandomVariable[targetGroundInstances
							                            .size()])), 
					    ImmutableSet
						.copyOf(currentGroundInstances
							.toArray(new RandomVariable[currentGroundInstances
							                            .size()])))
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
		
		ImmutableSet<LogicalVariable> allLogicalVariables = ImmutableSet.of();
		
		for (ParameterizedRandomVariable prv : setOfParameterizedRandomVariablesWithoutTarget) {
			allLogicalVariables = Sets
				.union(prv.getParameters(), allLogicalVariables)
				.immutableCopy();
		}
		
		return variable.getParameters().containsAll(allLogicalVariables);		
	}
	
 
}
