package br.usp.poli.takiyama.cfove;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.SplitResult;
import br.usp.poli.takiyama.common.StdSplitResult;
import br.usp.poli.takiyama.prv.Binding;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Substitution;


/**
 * Unit tests for {@link StdParfactor}s.
 * 
 * @author Felipe Takiyama
 */
public class StdParfactorTest {
	
	/**
	 * Example 2.15 from [Kisynski,2010].
	 * <p>
	 * Splits parfactor <C,V,F> on substitution {B/x1}, where C = {A &ne; B},
	 * V = {f(A, B), h(B)} and F is given by
	 * </p>
	 * <p>
	 * <table border="1">
	 * <tr><th>f(A,B)</th><th>h(B)</th><th>values</th></tr>
	 * <tr><td>false</td><td>false</td><td>0.2</td></tr>
	 * <tr><td>false</td><td>true</td><td>0.3</td></tr>
	 * <tr><td>true</td><td>false</td><td>0.5</td></tr>
	 * <tr><td>true</td><td>true</td><td>0.7</td></tr>
	 * </table>
	 * </p>
	 * <p>
	 * The result of this operation is parfactor g[B/x1], where C[B/x1] = 
	 * {A &ne; x1}, V[B/x1] = {f(A, x1), h(x1)} and F[B/x1] has the same values
	 * as F. The residue is parfactor g', where C' = {A &ne; B. B &ne; x1}, 
	 * V' = V and F' = F.
	 * </p>
	 */
	@Test
	public void testSplit() {
		
		LogicalVariable a = StdLogicalVariable.getInstance("A", "x", 10);
		LogicalVariable b = StdLogicalVariable.getInstance("B", "x", 10);
		
		Constant x1 = Constant.getInstance("x1");
		
		Prv f = StdPrv.getBooleanInstance("f", a, b);
		Prv h = StdPrv.getBooleanInstance("h", b);
		Prv f1 = StdPrv.getBooleanInstance("f", a, x1);
		Prv h1 = StdPrv.getBooleanInstance("h", x1);
		
		Constraint ab = InequalityConstraint.getInstance(a, b);
		Constraint a0 = InequalityConstraint.getInstance(a, x1);
		Constraint b0 = InequalityConstraint.getInstance(b, x1);
		
		double [] vals = {0.2, 0.3, 0.5, 0.7};
		
		Parfactor input = new StdParfactorBuilder().constraints(ab)
				.variables(f, h).values(vals).build();
		
		Binding binding = Binding.getInstance(b, x1);
		Substitution sub = Substitution.getInstance(binding);
		SplitResult output = input.splitOn(sub);
		
		Parfactor result = new StdParfactorBuilder().constraints(a0)
				.variables(f1, h1).values(vals).build();
		
		Parfactor residue = new StdParfactorBuilder().constraints(ab, b0)
				.variables(f, h).values(vals).build();
		
		SplitResult answer = StdSplitResult.getInstance(result, residue);
		
		assertTrue(output.equals(answer));
	}
}
