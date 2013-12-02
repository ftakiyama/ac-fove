package br.usp.poli.takiyama.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import br.usp.poli.takiyama.acfove.AggParfactor;
import br.usp.poli.takiyama.acfove.AggParfactor.AggParfactorBuilder;
import br.usp.poli.takiyama.acfove.ConvertToStdParfactors;
import br.usp.poli.takiyama.acfove.MacroOperation;
import br.usp.poli.takiyama.acfove.Propositionalize;
import br.usp.poli.takiyama.cfove.StdParfactor.StdParfactorBuilder;
import br.usp.poli.takiyama.common.Factor;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;
import br.usp.poli.takiyama.common.StdFactor;
import br.usp.poli.takiyama.common.StdMarginal.StdMarginalBuilder;
import br.usp.poli.takiyama.prv.And;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.CountingFormula;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Or;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.prv.StdLogicalVariable;
import br.usp.poli.takiyama.prv.StdPrv;
import br.usp.poli.takiyama.prv.Term;
import br.usp.poli.takiyama.prv.Xor;

/**
 * Stores parfactors, logical variables, parameterized random variables (PRV)
 * and other data structures used in AC-FOVE tests.
 * 
 * @author ftakiyama
 *
 */
public class Example {
	
	private final Map<String, LogicalVariable> logicalVariable;
	private final Map<String, Prv> parameterizedRandomVariable;
	private final Map<String, Factor> factor;
	private final Map<String, Parfactor> parfactor;
	private Parfactor expected;

	private Example() {
		logicalVariable = new LinkedHashMap<String, LogicalVariable>();
		parameterizedRandomVariable = new LinkedHashMap<String, Prv>();
		factor = new LinkedHashMap<String, Factor>();
		parfactor = new LinkedHashMap<String, Parfactor>();
		expected = null;
	}
	
	// Setters
	
	private LogicalVariable putLogicalVariable(String name, String prefix, int populationSize) {
		LogicalVariable lv = StdLogicalVariable.getInstance(name, prefix, populationSize);
		logicalVariable.put(name, lv);
		return lv;
	}
	
	private Prv putPrv(String name, Term ... parameters) {
		Prv prv = StdPrv.getBooleanInstance(name, parameters);
		parameterizedRandomVariable.put(prv.toString(), prv);
		return prv;
	}
	
	private Prv putCountingFormula(LogicalVariable bound, Prv counted) {
		Prv cf = CountingFormula.getInstance(bound, counted);
		String name = "#." + bound.value() + " " + counted.name();
		parameterizedRandomVariable.put(name, cf);
		return cf;
	}
	
	private Parfactor putParfactor(String name, Parfactor parfactor) {
		this.parfactor.put(name, parfactor);
		return parfactor;
	}
	
	private Parfactor putExpected(Parfactor e) {
		this.expected = e;
		return e;
	}
	
	private Factor putFactor(String name, List<Prv> prvs, List<BigDecimal> values) {
		Factor f = StdFactor.getInstance(name, prvs, values);
		factor.put(name, f);
		return f;
	}

	private Factor putFactor(String name, Factor f) {
		factor.put(name, f);
		return f;
	}
	
	// Getters
	
	public LogicalVariable lv(String name) throws NoSuchElementException {
		if (logicalVariable.containsKey(name)) {
			return logicalVariable.get(name);
		} else {
			throw new NoSuchElementException();
		}
	}
	
	public Prv prv(String name) throws NoSuchElementException {
		if (parameterizedRandomVariable.containsKey(name)) {
			return parameterizedRandomVariable.get(name);
		} else {
			throw new NoSuchElementException();
		}
	}
	
	public Set<Prv> allPrvs() {
		return new HashSet<Prv>(parameterizedRandomVariable.values());
	}
	
	public Parfactor parfactor(String name) throws NoSuchElementException {
		if (parfactor.containsKey(name)) {
			return parfactor.get(name);
		} else {
			throw new NoSuchElementException();
		}
	}
	
	public Parfactor expected() {
		return expected;
	}
	
	public Factor factor(String name) throws NoSuchElementException {
		if (factor.containsKey(name)) {
			return factor.get(name);
		} else {
			throw new NoSuchElementException();
		}
	}
	
	public Set<Factor> allFactors() {
		return new HashSet<Factor>(factor.values());
	}
	
	public Marginal marginal(RandomVariableSet query) {
		int capacity = parfactor.size();
		Set<Parfactor> p = new HashSet<Parfactor>(parfactor.values());
		Marginal marginal = new StdMarginalBuilder(capacity).parfactors(p)
				.preservable(query).build();
		return marginal;
	}
	
	/**
	 * Returns data structures corresponding to CRALC expression &exist;r.b(X).
	 * 
	 * @param domainSize The number of individuals of X.
	 * @return data structures corresponding to CRALC expression &exist;r.b(X).
	 */
	public static Example existsNetwork(int domainSize) {
		
		Example network = new Example();
		
		LogicalVariable x = network.putLogicalVariable("X", "x", domainSize);
		LogicalVariable y = network.putLogicalVariable("Y", "y", domainSize);
		
		Prv b = network.putPrv("b", y);
		Prv r = network.putPrv("r", x, y);
		Prv a = network.putPrv("and", x, y);
		Prv e = network.putPrv("exists", x);
		Prv ex = network.putCountingFormula(x, e);
		
		List<BigDecimal> fb = TestUtils.toBigDecimalList(0.1, 0.9);
		List<BigDecimal> fr = TestUtils.toBigDecimalList(0.2, 0.8);
		List<BigDecimal> fand = TestUtils.toBigDecimalList(1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0);
		List<BigDecimal> fexp = new ArrayList<BigDecimal>();
		
		// populates fexp
		BigDecimal vFalse = BigDecimal.valueOf(0.28).pow(domainSize, MathUtils.CONTEXT);
		BigDecimal vTrue = BigDecimal.ONE.subtract(vFalse, MathUtils.CONTEXT);
		for (int n = domainSize; n >= 0; n--) {
			fexp.add(vFalse.pow(n, MathUtils.CONTEXT).multiply(vTrue.pow(domainSize - n, MathUtils.CONTEXT), MathUtils.CONTEXT));
		}
		
		Parfactor g1 = new StdParfactorBuilder().variables(b).values(fb).build();
		Parfactor g2 = new StdParfactorBuilder().variables(r).values(fr).build();
		Parfactor g3 = new StdParfactorBuilder().variables(r, b, a).values(fand).build();
		Parfactor g4 = new AggParfactorBuilder(a, e, Or.OR).context(b).build();
		Parfactor g5 = new StdParfactorBuilder().variables(ex).values(fexp).build();
		
		network.putParfactor("gb", g1);
		network.putParfactor("gr", g2);
		network.putParfactor("gand", g3);
		network.putParfactor("gexists", g4);
		network.putExpected(g5);
		
		return network;
	}
	
	/**
	 * Returns data structures corresponding to CRALC expression &exist;r.b(X).
	 * The network is propositionalized.
	 * 
	 * @param domainSize The number of individuals of X.
	 * @return data structures corresponding to CRALC expression &exist;r.b(X).
	 */
	public static Example existsNetworkPropositionalized(int domainSize) {
		// List of constants
		List<Constant> x = new ArrayList<Constant>(domainSize);
		List<Constant> y = new ArrayList<Constant>(domainSize);
		for (int i = 0; i < domainSize; i++) {
			x.add(Constant.getInstance("x" + i));
			y.add(Constant.getInstance("y" + i));
		}
		
		Example network = new Example();
		
		// Creates random variables
		for (int i = 0; i < domainSize; i++) {
			network.putPrv("b", y.get(i));
			network.putPrv("exists", x.get(i));
			for (int j = 0; j < domainSize; j++) {
				network.putPrv("r", x.get(i), y.get(j));
				network.putPrv("and", x.get(i), y.get(j));
			}
		}
		
		// Creates factors on b(Y)
		List<BigDecimal> vb = TestUtils.toBigDecimalList(0.1, 0.9);
		for (int i = 0; i < domainSize; i++) {
			String b = "b ( " + y.get(i) + " )";
			List<Prv> rvs = Lists.listOf(network.prv(b));
			network.putFactor(b, rvs, vb);
		}
		
		// Creates factors on r(X,Y)
		List<BigDecimal> vr = TestUtils.toBigDecimalList(0.2, 0.8);
		for (int i = 0; i < domainSize; i++) {
			for (int j = 0; j < domainSize; j++) {
				String r = "r ( " + x.get(i) + " " + y.get(j) + " )";
				List<Prv> rvs = Lists.listOf(network.prv(r));
				network.putFactor(r, rvs, vr);
			}
		}
		
		// Creates factors on r(X,Y), b(Y), and(X,Y)
		List<BigDecimal> vand = TestUtils.toBigDecimalList(1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0);
		for (int i = 0; i < domainSize; i++) {
			for (int j = 0; j < domainSize; j++) {
				String b = "b ( " + y.get(j) + " )";
				String r = "r ( " + x.get(i) + " " + y.get(j) + " )";
				String and = "and ( " + x.get(i) + " " + y.get(j) + " )";
				List<Prv> rvs = Lists.listOf(network.prv(b), network.prv(r), network.prv(and));
				network.putFactor(and, rvs, vand);
			}
		}
		
		// Creates factors on and(X,y1), ..., and(X,yn), exists(X)
		int vexistsSize = (int) Math.pow(2, domainSize);
		List<BigDecimal> vexists = new ArrayList<BigDecimal>(vexistsSize); 
		for (int i = 0; i < vexistsSize; i++) {
			vexists.add(BigDecimal.ZERO);
			vexists.add(BigDecimal.ONE);
		}
		vexists.set(0, BigDecimal.ONE);
		vexists.set(1, BigDecimal.ZERO);
		for (int i = 0; i < domainSize; i++) {
			List<Prv> rvs = new ArrayList<Prv>(domainSize + 1);
			for (int j = 0; j < domainSize; j++) {
				String and = "and ( " + x.get(i) + " " + y.get(j) + " )";
				rvs.add(network.prv(and));
			}
			String exists = "exists ( " + x.get(i) + " )";
			rvs.add(network.prv(exists));
			network.putFactor(exists, rvs, vexists);
		}
		
		return network;
	}
	
	
	/**
	 * Returns data structures corresponding to CRALC expression &exist;r.b(X).
	 * It uses b(X) instead of b(Y) to test if context variables can be
	 * parameterized by the extra logical variable.
	 * 
	 * @param domainSize The number of individuals of X.
	 * @return data structures corresponding to CRALC expression &exist;r.b(X).
	 */
	public static Example existsNetworkWithBX(int domainSize) {
		
		Example network = new Example();
		
		LogicalVariable x = network.putLogicalVariable("X", "x", domainSize);
		LogicalVariable y = network.putLogicalVariable("Y", "y", domainSize);
		
		Prv b = network.putPrv("b", x);
		Prv r = network.putPrv("r", x, y);
		Prv a = network.putPrv("and", x, y);
		Prv e = network.putPrv("exists", x);
		
		List<BigDecimal> fb = TestUtils.toBigDecimalList(0.1, 0.9);
		List<BigDecimal> fr = TestUtils.toBigDecimalList(0.2, 0.8);
		List<BigDecimal> fand = TestUtils.toBigDecimalList(1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0);
		
		Parfactor g1 = new StdParfactorBuilder().variables(b).values(fb).build();
		Parfactor g2 = new StdParfactorBuilder().variables(r).values(fr).build();
		Parfactor g3 = new StdParfactorBuilder().variables(r, b, a).values(fand).build();
		Parfactor g4 = new AggParfactorBuilder(a, e, Or.OR).context(b).build();
		
		network.putParfactor("gb", g1);
		network.putParfactor("gr", g2);
		network.putParfactor("gand", g3);
		network.putParfactor("gexists", g4);
		
		return network;
	}
	
	
	/**
	 * Returns data structures corresponding to CRALC expression &exist;r.b(X).
	 * The network is propositionalized. 
	 * It uses b(X) instead of b(Y) to test if context variables can be
	 * parameterized by the extra logical variable.
	 * 
	 * @param domainSize The number of individuals of X.
	 * @return data structures corresponding to CRALC expression &exist;r.b(X).
	 */
	public static Example existsNetworkPropositionalizedWithBX(int domainSize) {
		// List of constants
		List<Constant> x = new ArrayList<Constant>(domainSize);
		List<Constant> y = new ArrayList<Constant>(domainSize);
		for (int i = 0; i < domainSize; i++) {
			x.add(Constant.getInstance("x" + i));
			y.add(Constant.getInstance("y" + i));
		}
		
		Example network = new Example();
		
		// Creates random variables
		for (int i = 0; i < domainSize; i++) {
			network.putPrv("b", x.get(i));
			network.putPrv("exists", x.get(i));
			for (int j = 0; j < domainSize; j++) {
				network.putPrv("r", x.get(i), y.get(j));
				network.putPrv("and", x.get(i), y.get(j));
			}
		}
		
		// Creates factors on b(X)
		List<BigDecimal> vb = TestUtils.toBigDecimalList(0.1, 0.9);
		for (int i = 0; i < domainSize; i++) {
			String b = "b ( " + x.get(i) + " )";
			List<Prv> rvs = Lists.listOf(network.prv(b));
			network.putFactor(b, rvs, vb);
		}
		
		// Creates factors on r(X,Y)
		List<BigDecimal> vr = TestUtils.toBigDecimalList(0.2, 0.8);
		for (int i = 0; i < domainSize; i++) {
			for (int j = 0; j < domainSize; j++) {
				String r = "r ( " + x.get(i) + " " + y.get(j) + " )";
				List<Prv> rvs = Lists.listOf(network.prv(r));
				network.putFactor(r, rvs, vr);
			}
		}
		
		// Creates factors on r(X,Y), b(X), and(X,Y)
		List<BigDecimal> vand = TestUtils.toBigDecimalList(1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0);
		for (int i = 0; i < domainSize; i++) {
			for (int j = 0; j < domainSize; j++) {
				String b = "b ( " + x.get(i) + " )";
				String r = "r ( " + x.get(i) + " " + y.get(j) + " )";
				String and = "and ( " + x.get(i) + " " + y.get(j) + " )";
				List<Prv> rvs = Lists.listOf(network.prv(b), network.prv(r), network.prv(and));
				network.putFactor(and, rvs, vand);
			}
		}
		
		// Creates factors on and(X,y1), ..., and(X,yn), exists(X)
		int vexistsSize = (int) Math.pow(2, domainSize);
		List<BigDecimal> vexists = new ArrayList<BigDecimal>(vexistsSize); 
		for (int i = 0; i < vexistsSize; i++) {
			vexists.add(BigDecimal.ZERO);
			vexists.add(BigDecimal.ONE);
		}
		vexists.set(0, BigDecimal.ONE);
		vexists.set(1, BigDecimal.ZERO);
		for (int i = 0; i < domainSize; i++) {
			List<Prv> rvs = new ArrayList<Prv>(domainSize + 1);
			for (int j = 0; j < domainSize; j++) {
				String and = "and ( " + x.get(i) + " " + y.get(j) + " )";
				rvs.add(network.prv(and));
			}
			String exists = "exists ( " + x.get(i) + " )";
			rvs.add(network.prv(exists));
			network.putFactor(exists, rvs, vexists);
		}
		
		return network;
	}
	
	
	/**
	 * Returns data structures corresponding to CRALC expression &exist;r.b(X).
	 * It uses b(Y) and r(X,Y) as context variables.
	 * 
	 * @param domainSize The number of individuals of X.
	 * @return data structures corresponding to CRALC expression &exist;r.b(X).
	 */
	public static Example existsNetworkWithMultipleContext(int domainSize) {
		
		Example network = Example.existsNetwork(domainSize);
		
		Prv b = network.prv("b ( Y )");
		Prv r = network.prv("r ( X Y )");
		Prv a = network.prv("and ( X Y )");
		Prv e = network.prv("exists ( X )");
		
		Parfactor g4 = new AggParfactorBuilder(a, e, Or.OR).context(b, r).build();
		
		network.putParfactor("gexists", g4);
		
		return network;
	}
	
	
	/**
	 * Returns data structures corresponding to example 3.14 of Kisynski (2010)
	 * 
	 * @param domainSize The number of individuals in logical variable Person.
	 * @return data structures corresponding to example 3.14 of Kisynski (2010)
	 */
	public static Example bigJackpotNetwork(int domainSize) {
		
		Example network = new Example();
		
		LogicalVariable person = network.putLogicalVariable("Person", "x", domainSize);
		
		Prv big_jackpot = network.putPrv("big_jackpot");
		Prv played = network.putPrv("played", person);
		Prv matched_6 = network.putPrv("matched_6", person);
		Prv jackpot_won = network.putPrv("jackpot_won");
		
		List<BigDecimal> fBigJackpot = TestUtils.toBigDecimalList(0.8, 0.2);
		List<BigDecimal> fPlayed = TestUtils.toBigDecimalList(0.95, 0.05, 0.85, 0.15);
		List<BigDecimal> fMatched6 = TestUtils.toBigDecimalList(1.0, 0.0, 0.99999993, 0.00000007);
		
		Parfactor g1 = new StdParfactorBuilder().variables(big_jackpot).values(fBigJackpot).build();
		Parfactor g2 = new StdParfactorBuilder().variables(big_jackpot, played).values(fPlayed).build();
		Parfactor g3 = new StdParfactorBuilder().variables(played, matched_6).values(fMatched6).build();
		Parfactor g4 = new AggParfactorBuilder(matched_6, jackpot_won, Or.OR).context(big_jackpot).build();

		Parfactor g2xg3 = g2.multiply(g3);
		Parfactor afterEliminatingPlayed = g2xg3.sumOut(played);
		Parfactor g4xg5 = g4.multiply(afterEliminatingPlayed);
		Parfactor afterEliminatingMatched6 = g4xg5.sumOut(matched_6);
		Parfactor g6xg1 = g1.multiply(afterEliminatingMatched6);
		Parfactor afterEliminatingBigJackpot = g6xg1.sumOut(big_jackpot);
		
		network.putParfactor("gbigjackpot", g1);
		network.putParfactor("gplayed", g2);
		network.putParfactor("gmatched6", g3);
		network.putParfactor("gjackpotwon", g4);
		network.putExpected(afterEliminatingBigJackpot);
		
		return network;
	}
	
	/**
	 * Returns data structures corresponding to example 3.14 of Kisynski (2010),
	 * without using context variables (generalized aggregation parfactors)
	 * 
	 * @param domainSize The number of individuals in logical variable Person.
	 * @return data structures corresponding to example 3.14 of Kisynski (2010)
	 */
	public static Example bigJackpotNetworkNoContext(int domainSize) {
		
		Example network = new Example();
		
		LogicalVariable person = network.putLogicalVariable("Person", "x", domainSize);
		
		Prv big_jackpot = network.putPrv("big_jackpot");
		Prv played = network.putPrv("played", person);
		Prv matched_6 = network.putPrv("matched_6", person);
		Prv jackpot_won = network.putPrv("jackpot_won");
		
		List<BigDecimal> fBigJackpot = TestUtils.toBigDecimalList(0.8, 0.2);
		List<BigDecimal> fPlayed = TestUtils.toBigDecimalList(0.95, 0.05, 0.85, 0.15);
		List<BigDecimal> fMatched6 = TestUtils.toBigDecimalList(1.0, 0.0, 0.99999993, 0.00000007);
		
		Parfactor g1 = new StdParfactorBuilder().variables(big_jackpot).values(fBigJackpot).build();
		Parfactor g2 = new StdParfactorBuilder().variables(big_jackpot, played).values(fPlayed).build();
		Parfactor g3 = new StdParfactorBuilder().variables(played, matched_6).values(fMatched6).build();
		Parfactor g4 = new AggParfactorBuilder(matched_6, jackpot_won, Or.OR).build();

		network.putParfactor("gbigjackpot", g1);
		network.putParfactor("gplayed", g2);
		network.putParfactor("gmatched6", g3);
		network.putParfactor("gjackpotwon", g4);
		
		Example correct = Example.bigJackpotNetwork(domainSize);
		network.putExpected(correct.expected());
		
		return network;
	}
	
	
	/**
	 * Water sprinkler network proposed by Kevin Murphy. The network was 
	 * adapted to a lifted version: sprinkler and wet_grass are random 
	 * variables parameterized on a logical variable Lot.
	 * 
	 * The original network is shown here:
	 * http://www.cs.ubc.ca/~murphyk/Bayes/bnintro.html
	 * 
	 * The number of nodes in this network is 2 * (domainSize + 1)
	 * 
	 * @param domainSize
	 * @return
	 */
	public static Example waterSprinklerNetWork(int domainSize) {
		
		Example network = new Example();
		
		LogicalVariable lot = network.putLogicalVariable("Lot", "lot", domainSize);
		
		Prv cloudy = network.putPrv("cloudy");
		Prv rain = network.putPrv("rain");
		Prv sprinkler = network.putPrv("sprinkler", lot);
		Prv wetGrass = network.putPrv("wet_grass", lot);
		
		List<BigDecimal> fCloudy = TestUtils.toBigDecimalList(0.5, 0.5);
		List<BigDecimal> fRain = TestUtils.toBigDecimalList(0.8, 0.2, 0.2, 0.8);
		List<BigDecimal> fSprinkler = TestUtils.toBigDecimalList(0.5, 0.5, 0.9, 0.1);
		List<BigDecimal> fWetGrass = TestUtils.toBigDecimalList(1.0, 0.0, 0.1, 0.9, 0.1, 0.9, 0.01, 0.99);
		
		Parfactor g1 = new StdParfactorBuilder().variables(cloudy).values(fCloudy).build();
		Parfactor g2 = new StdParfactorBuilder().variables(cloudy, rain).values(fRain).build();
		Parfactor g3 = new StdParfactorBuilder().variables(cloudy, sprinkler).values(fSprinkler).build();
		Parfactor g4 = new StdParfactorBuilder().variables(sprinkler, rain, wetGrass).values(fWetGrass).build();
		
		network.putParfactor("gcloudy", g1);
		network.putParfactor("grain", g2);
		network.putParfactor("gsprinkler", g3);
		network.putParfactor("gwetgrass", g4);
		
		network.putFactor("fcloudy", g1.factor());
		network.putFactor("frain", g2.factor());
		network.putFactor("fsprinkler", g3.factor());
		network.putFactor("fwetgrass", g4.factor());
		
		return network;
	}
	
	/**
	 * The Sick and Death network used as example by Rodrigo de Salvo Braz in 
	 * his IJCAI-05 paper. The network has been adapted to use aggregation 
	 * parfactors by adding a 'someDeath' node. It also uses a directed graph
	 * to represent cause/consequence relations (the original is a Markov
	 * network).
	 * <p>
	 * The number of nodes in this network is 2 * (domainSize + 1)
	 * </p>
	 * 
	 * @param domainSize
	 * @return
	 */
	public static Example sickDeathNetwork(int domainSize) {
		
		Example network = new Example();
		
		LogicalVariable person = network.putLogicalVariable("Person", "person", domainSize);
		
		Prv epidemic = network.putPrv("epidemic");
		Prv sick = network.putPrv("sick", person);
		Prv death = network.putPrv("death", person);
		Prv someDeath = network.putPrv("someDeath");
		
		List<BigDecimal> fEpidemic = TestUtils.toBigDecimalList(0.45, 0.55);
		List<BigDecimal> fSick = TestUtils.toBigDecimalList(0.9, 0.1, 0.3, 0.7);
		List<BigDecimal> fDeath = TestUtils.toBigDecimalList(1, 0, 0.45, 0.55);
		
		Parfactor g1 = new StdParfactorBuilder().variables(epidemic).values(fEpidemic).build();
		Parfactor g2 = new StdParfactorBuilder().variables(epidemic, sick).values(fSick).build();
		Parfactor g3 = new StdParfactorBuilder().variables(sick, death).values(fDeath).build();
		Parfactor g4 = new AggParfactorBuilder(death, someDeath, Or.OR).context(epidemic).build();
		
		network.putParfactor("gepidemic", g1);
		network.putParfactor("gsick", g2);
		network.putParfactor("gdeath", g3);
		network.putParfactor("gsomedeath", g4);
		
		return network;
	}
	
	public static Example competingWorkshopsNetwork(int numberOfWorkshops, int numberOfPeople) {
		
		Example network = new Example();
		
		LogicalVariable workshop = network.putLogicalVariable("Workshop", "w", numberOfWorkshops);
		LogicalVariable person = network.putLogicalVariable("Person", "p", numberOfPeople);
		
		Prv hot = network.putPrv("hot", workshop);
		Prv attends = network.putPrv("attends", person);
		Prv success = network.putPrv("success");
		
		List<BigDecimal> fHot = TestUtils.toBigDecimalList(0.3, 0.7);
		
		Parfactor g1 = new StdParfactorBuilder().variables(hot).values(fHot).build();
		Parfactor g2 = new AggParfactorBuilder(hot, attends, Xor.XOR).build();
		Parfactor g3 = new AggParfactorBuilder(attends, success, And.AND).build();
		
		network.putParfactor("ghot", g1);
		network.putParfactor("gattends", g2);
		network.putParfactor("gsuccess", g3);
		
		return network;
	}
	
	/**
	 * Returns a marginal containing all parfactors in this network and the 
	 * specified query.
	 * @param query The query in the marginal
	 * @return A marginal containing all parfactors in this network and the 
	 * specified query.
	 */
	public Marginal getMarginal(RandomVariableSet query) {
		Set<Parfactor> parfactors = new HashSet<Parfactor>(this.parfactor.values());
		return new StdMarginalBuilder().parfactors(parfactors).preservable(query).build();
	}
	
	/**
	 * Returns the specified network completely propositionalized. The resulting
	 * network can be used with VE algorithms.
	 * <p>
	 * The resulting network will not have logical variables and factors (space
	 * constraint).
	 * </p>
	 * 
	 * @param network The network to propositionalize.
	 * @return the specified network completely propositionalized.
	 */
	public Marginal propositionalizeAll(Marginal marginal) {
		
		Set<LogicalVariable> logicalVariables = new HashSet<LogicalVariable>();
		
		// get all logical variables and builds list of parfactors
		for (Parfactor parfactor : marginal) {
			logicalVariables.addAll(parfactor.logicalVariables());
		}
		
		// Auxiliary set of parfactors
		Set<Parfactor> parfactors = marginal.distribution().toSet();
		
		for (LogicalVariable lv : logicalVariables) {
			// propositionalizes all parfactors in the set containing the current logical variable
			for (Parfactor parfactor : parfactors) {
				if (parfactor.logicalVariables().contains(lv)) {
					MacroOperation propositionalize = new Propositionalize(marginal, parfactor, lv);
					marginal = propositionalize.run();
				}
			}
			// updates the set of parfactors
			parfactors = marginal.distribution().toSet();
		}
		
		return marginal;
	}
	
	/**
	 * Returns the specified network with aggregation parfactors converted to
	 * standard parfactors. The resulting network can be used with the C-FOVE
	 * algorithm. 
	 * 
	 * @param network The network where aggregation parfactors will be converted
	 * @return The specified network with aggregation parfactors converted to
	 * standard parfactors. 
	 */
	public Marginal removeAggregation(Marginal marginal) {
		for (Parfactor parfactor : marginal) {
			if (parfactor instanceof AggParfactor) {
				MacroOperation convert = new ConvertToStdParfactors(marginal, parfactor);
				marginal = convert.run();
			}
		}
		return marginal;
	}
}
