package br.usp.poli.takiyama.common;

import java.util.Iterator;

import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

/**
 * This class represent equations of the form ti = tj, where ti and tj are
 * parameters of a parameterized random variable.
 * <p>
 * This class is used in the algorithm to find the MGU between two 
 * parameterized random variables. 
 * </p>
 * 
 * @author Felipe Takiyama
 *
 */
public final class EqualityConstraint extends AbstractConstraint {

	/* ************************************************************************
	 *    Constructors
	 * ************************************************************************/
	
	/**
	 * Creates the equality constraint composed by the specified terms.
	 * 
	 * @param t1 The left-hand side of the constraint
	 * @param t2 The right-hand side of the constraint
	 */
	private EqualityConstraint(Term t1, Term t2) throws IllegalArgumentException {
		firstTerm = t1;
		secondTerm = t2;
	}
	
	
	/* ************************************************************************
	 *    Static factories
	 * ************************************************************************/
	
	/**
	 * Static factory of equality constraints
	 * 
	 * @param firstTerm The left-hand side of the constraint 
	 * @param secondTerm The right-hand side of the constraint
	 * @return An equality constraint
	 */
	public static Constraint getInstance(Term firstTerm, Term secondTerm) {
		return new EqualityConstraint(firstTerm, secondTerm);
	}
	
	
	/* ************************************************************************
	 *    Inherited methods
	 * ************************************************************************/
	
	@Override
	public Constraint apply(Substitution s) throws IllegalStateException {
		Term t1 = this.firstTerm;
		Term t2 = this.secondTerm;
		for (Iterator<LogicalVariable> it = s.getSubstitutedIterator(); it.hasNext(); ) {
			LogicalVariable replaced = it.next();
			if (replaced.equals(t1)) {
				t1 = s.getReplacement(replaced);
			} 
			if (replaced.equals(t2)) {
				t2 = s.getReplacement(replaced);
			}
		}
		return new EqualityConstraint(t1, t2);
	}
	

	/**
	 * Throws {@link UnsupportedOperationException}. This method is not 
	 * implemented for this class.
	 */
	@Override
	public boolean isConsistentWith(Binding b) {
		throw new UnsupportedOperationException();
	}

	
	/* ************************************************************************
	 *    hashCode, equals and toString
	 * ************************************************************************/

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EqualityConstraint)) {
			return false;
		}
		
		EqualityConstraint other = (EqualityConstraint) obj; 
		
		// tests A=B == A=B
		boolean direct = 
				(firstTerm == null) ? (other.firstTerm == null) 
									: (firstTerm.equals(other.firstTerm))
				&& 
				(secondTerm == null) ? (other.secondTerm == null) 
									 : (secondTerm.equals(other.secondTerm));
		
		// tests A=B == B=A
		boolean inverse = 
				(firstTerm == null) ? (other.secondTerm == null) 
								  	: (firstTerm.equals(other.secondTerm))
				&& 
				(secondTerm == null) ? (other.firstTerm == null) 
									 : (secondTerm.equals(other.firstTerm)); 
		
		return direct || inverse;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstTerm == null) ? 0 : firstTerm.hashCode());
		result = prime * result
				+ ((secondTerm == null) ? 0 : secondTerm.hashCode());
		result = prime * 7; // otherwise InequalityConstraint with same terms would have same hash
		return result;
	}
	
	
	@Override
	public String toString() {
		return firstTerm.toString() + " = " + secondTerm.toString();
	}
}
