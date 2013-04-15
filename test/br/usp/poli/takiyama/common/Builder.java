package br.usp.poli.takiyama.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.usp.poli.takiyama.prv.Bool;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Population;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RangeElement;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Term;

public class Builder {
	
	public static LogicalVariable getLogicalVariable(String name, String prefix, 
			int size) {
		List<Constant> individuals = new ArrayList<Constant>(size);
		for (int i = 1; i <= size; i++) {
			individuals.add(Constant.getInstance(prefix + i));
		}
		Population population = Population.getInstance(individuals);
		return StdLogicalVariable.getInstance(name, population);
	}
	
	
	public static Prv getStdPrv(String functor, List<RangeElement> range, 
			Term ... vars) {
		List<Term> lv = Arrays.asList(vars);
		return StdPrv.getInstance(functor, range, lv);
	}

	
	public static Prv getStdPrv(String functor, Term ... vars) {
		List<RangeElement> range = new ArrayList<RangeElement>(2);
		range.add(Bool.valueOf(false));
		range.add(Bool.valueOf(true));
		return Builder.getStdPrv(functor, range, vars);
	}

	
}
