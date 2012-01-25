/**
 * 
 */
package br.usp.dml.takiyama.cfove;

/**
 * This class implements the VE algorithm.
 * @author ftakiyama
 *
 */
public class VariableEliminationAlgorithm {
	/**
	 * This is the main code that will work someday, hopefully.
	 * 
	public Factor VariableElimination(Variable[] v, Factor[] f, Assignment[] vo, Variable q, Heuristic h) {
		Variable[] e = Set.subtract(v, Set.union(o,q));
		while (f.contains(e)) {
			Variable y = h.selectVariable(e);
			f = eliminate(f, y);
			e = Set.subtract(e,y);
		}
		f = multiply(f);
		normalizingConstant = sumOut(q,f);
		return f/normalizingConstant;
	}
	
	private Factor[] eliminate(y, f) {
		fy = partition(f, y);
		fwithouty = Set.subtract(f, fy);
		return Set.union(fwithouty, sumOut(y, multiply(fy, f)));
	} 
	*/
}
