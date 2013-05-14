package br.usp.poli.takiyama.common;

import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Term;

/**
 * This class represents constraints of the form X ? Y, where
 * X and Y are a Terms and '?' is either '=' or '&ne;'. 
 * <p>
 * Constraints with two constants, like t &ne; q, are invalid. If a method
 * detects a invalid constraint, it returns null.
 * </p>
 *  
 * @author Felipe Takiyama
 *
 */
abstract class AbstractConstraint implements Constraint {
	
	Term firstTerm;
	Term secondTerm;
	
	
	/* ************************************************************************
	 *    Getters
	 * ************************************************************************/

	@Override
	public Term firstTerm() {
		return this.firstTerm;
	}

	
	@Override
	public Term secondTerm() {
		return this.secondTerm;
	}
	
	
	/* ************************************************************************
	 *    Inherited methods
	 * ************************************************************************/
		
	@Override
	public boolean contains(Term term) {
		return (firstTerm.equals(term) || secondTerm.equals(term));
	}
	
	
	@Override
	public boolean hasCommonTerm(Constraint constraint) {
		return (this.firstTerm().equals(constraint.firstTerm())) 
			|| (this.firstTerm().equals(constraint.secondTerm()))
			|| (this.secondTerm().equals(constraint.firstTerm()))
			|| (this.secondTerm().equals(constraint.secondTerm()));
	}
	
	
	@Override
	public Binding toBinding() throws IllegalStateException {
		return getBinding(firstTerm, secondTerm);
	}
	
	
	@Override
	public Binding toInverseBinding() throws IllegalStateException {
		return getBinding(secondTerm, firstTerm);
	}
	
	
	/**
	 * Creates a {@link Binding} with the specified terms.
	 * @param t1 The first term (the one being replaced)
	 * @param t2 The second term (the replacement)
	 * @return The binding t1/t2
	 * @throws IllegalStateException If t1 is not a {@link LogicalVariable}
	 */
	private Binding getBinding(Term t1, Term t2) throws IllegalStateException {
		if (t1 instanceof LogicalVariable) {
			LogicalVariable lv = (LogicalVariable) t1;
			return Binding.getInstance(lv, t2);
		} else {
			throw new IllegalStateException();
		}
	}
	
	
	@Override
	public boolean isUnary() {
		return ((firstTerm.isVariable() && secondTerm.isConstant()) 
				|| (firstTerm.isConstant() && secondTerm.isVariable())); 
	}
	
	

//	/**
//	 * Creates a inequality constraint based on substitution. For instance,
//	 * if substitution is X/t, then the corresponding constraint is
//	 * X &ne; t.
//	 * @param substitution
//	 * @return
//	 */
//	public static Constraint getInequalityConstraintFromBinding(Binding substitution) {
//		if (substitution.secondTerm() instanceof Constant) { // ugly
//			return new Constraint(substitution.firstTerm(), (Constant) substitution.secondTerm());
//		} else if (substitution.secondTerm() instanceof StdLogicalVariable) { // argh
//			return new Constraint(substitution.firstTerm(), (StdLogicalVariable) substitution.secondTerm());
//		} else {
//			return null; // it should never get here
//		}
//	}
	
//	/**
//	 * Returns true if the second term is Constant, false otherwise.
//	 * @return True if the second term is Constant, false otherwise.
//	 */
//	public boolean secondTermIsConstant() {
//		return (this.secondTerm instanceof Constant);
//	}
//	
//	/**
//	 * Returns true if the second term is a LogicalVariable, false otherwise.
//	 * @return True if the second term is a LogicalVariable, false otherwise.
//	 */
//	public boolean secondTermIsLogicalVariable() {
//		return (this.secondTerm instanceof StdLogicalVariable);
//	}
	
	
//	@Override
//	public boolean equals(Object other) {
//		// Tests if both refer to the same object
//		if (this == other)
//	    	return true;
//		// Tests if the Object is an instance of this class
//	    if (!(other instanceof Constraint))
//	    	return false;
//	    // Tests if both have the same attributes
//	    Constraint targetObject = (Constraint) other;
//	    if (this.secondTerm instanceof StdLogicalVariable 
//	    		&& targetObject.secondTerm instanceof StdLogicalVariable)  { // tests the case (A!=B).equals(B!=A) 
//	    	StdLogicalVariable thisSt = (StdLogicalVariable) this.secondTerm;
//	    	StdLogicalVariable otherSt = (StdLogicalVariable) targetObject.secondTerm;
//	    	
//	    	// direct
//	    	boolean directCompare = 
//	    		((this.firstTerm == null) 
//	    				? (targetObject.firstTerm == null) 
//	    				: (this.firstTerm.equals(targetObject.firstTerm))) 
//	    		&&
//	    		((this.secondTerm == null) 
//	    				? (targetObject.secondTerm == null) 
//	    				: (this.secondTerm.equals(targetObject.secondTerm)));
//	    	
//	    	// inverse
//	    	boolean inverseCompare = 
//	    		((this.firstTerm == null) 
//	    				? (otherSt == null) 
//	    				: (this.firstTerm.equals(otherSt))) 
//	    		&&
//	    		((thisSt == null) 
//	    				? (targetObject.firstTerm == null) 
//	    				: (thisSt.equals(targetObject.firstTerm)));
//	    	
//	    	return (directCompare || inverseCompare);
//	    }
//	    return ((this.firstTerm == null) ? 
//	    		 targetObject.firstTerm == null : 
//		    		 this.firstTerm.equals(targetObject.firstTerm)) &&
//    		   ((this.secondTerm == null) ? 
//    		     targetObject.secondTerm == null : 
//    		     this.secondTerm.equals(targetObject.secondTerm));	    		
//	}
}
