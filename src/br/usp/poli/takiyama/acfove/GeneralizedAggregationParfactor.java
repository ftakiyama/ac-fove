package br.usp.poli.takiyama.acfove;

import java.util.ArrayList;
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
import br.usp.poli.takiyama.prv.ParameterizedRandomVariable;
import br.usp.poli.takiyama.prv.Substitution;
import br.usp.poli.takiyama.prv.Term;

public class GeneralizedAggregationParfactor implements Parfactor {

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
						+ vars + " " + c);
			}
		}
		
		public Builder(GeneralizedAggregationParfactor ap) {
			this.p = ParameterizedRandomVariable.getInstance(ap.parent);
			this.c = ParameterizedRandomVariable.getInstance(ap.child);
			this.op = ap.operator;
			this.f = ParameterizedFactor.getInstance(ap.factor);
			this.constraintsOnExtra = new HashSet<Constraint>(ap.constraintsOnExtraVariable);
			this.otherConstraints = new HashSet<Constraint>(ap.otherConstraints);
			this.lv = new LogicalVariable(ap.extraVariable);
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
		
		public GeneralizedAggregationParfactor build() {
			return new GeneralizedAggregationParfactor(this);
		}
	}
	
	private GeneralizedAggregationParfactor(Builder builder) {
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParameterizedFactor getFactor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParameterizedRandomVariable> getParameterizedRandomVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParameterizedRandomVariable getChildVariable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Constraint> getConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<LogicalVariable> getLogicalVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConstant() {
		// TODO Auto-generated method stub
		return false;
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
	public List<Parfactor> splitOnConstraints(Set<Constraint> constraints) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parfactor restoreLogicalVariableNames() {
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
	public Set<Parfactor> unify(Parfactor parfactor) {
		// TODO Auto-generated method stub
		return null;
	}

	
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
		
		return null;
	}
	
}
