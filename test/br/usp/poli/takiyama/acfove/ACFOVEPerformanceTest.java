package br.usp.poli.takiyama.acfove;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import br.usp.poli.takiyama.common.Constraint;
import br.usp.poli.takiyama.common.InequalityConstraint;
import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.prv.Constant;
import br.usp.poli.takiyama.prv.LogicalVariable;
import br.usp.poli.takiyama.prv.Prv;
import br.usp.poli.takiyama.prv.RandomVariableSet;
import br.usp.poli.takiyama.utils.Example;

/**
 * Tests comparing execution time for VE x C-FOVE x AC-FOVE. The following
 * networks are used:
 * <li> Sick Death
 * <li> Competing Workshops
 * <li> Water Sprinkler
 * <li> Big Jackpot
 * <p>
 * In each test, the number of individuals in the population is varied to
 * check the performance of each algorithm. A pre-defined query is used in
 * each network.
 * </p>
 */
public class ACFOVEPerformanceTest {
	
	/**
	 * Path for output files. The name of each file must be concatenated with
	 * this variable.
	 */
	private String OUT_PATH = "/Users/ftakiyama/Dropbox/Mestrado/Dissertação/img/";
	
	/**
	 * Encoding of output files
	 */
	private final String ENCODING = "UTF-8";
	
	/**
	 * Extension of output files with data
	 */
	private final String EXTENSION = ".dat";
	
	/**
	 * The number of times each test is run to get a mean time.
	 */
	private final int CYCLES = 10;
	
	/**
	 * Timeout for performance tests (in milliseconds)
	 */
//	@Rule
//    public Timeout globalTimeout = new Timeout(100000);
	
	private abstract class Problem {
		private Example network;
		private RandomVariableSet query;
		private Marginal input;
		private String name;
		
		private Problem() { };
		
		private String name() {
			return name;
		}
		
		abstract void setNetwork(int size);
		
		private ACFOVE propositionalizeAll() {
			input = network.getMarginal(query);
			return new VariableElimination(input);
		}
		
		private ACFOVE removeAggregation() {
			input = network.getMarginal(query);
			return new CFOVE(input);
		}
		
		private ACFOVE network() {
			input = network.getMarginal(query);
			return new ACFOVE(input);
		}
	}
	
	private class SickDeath extends Problem {
		private SickDeath() {
			super.name = "Sick Death";
		}
		@Override
		void setNetwork(int size) {
			super.network = Example.sickDeathNetwork(size);
			Prv someDeath = super.network.prv("someDeath ( )");
			super.query = RandomVariableSet.getInstance(someDeath, new HashSet<Constraint>(0));
		}
	}
	
	private class BigJackpot extends Problem {
		private BigJackpot() {
			super.name = "Big Jackpot";
		}
		@Override
		void setNetwork(int size) {
			super.network = Example.bigJackpotNetwork(size);
			Prv jackpotWon = super.network.prv("jackpot_won ( )");
			super.query = RandomVariableSet.getInstance(jackpotWon, new HashSet<Constraint>(0));
		}
	}
	
	private class CompetingWorkshopsWithFixedNumberOfWorkshops extends Problem {
		private int workshops;
		public CompetingWorkshopsWithFixedNumberOfWorkshops(int workshops) {
			this.workshops = workshops;
			super.name = "Competing Workshops With Fixed Number of Workshops";
		}
		@Override
		void setNetwork(int size) {
			super.network = Example.competingWorkshopsNetwork(workshops, size);
			Prv success = super.network.prv("success ( )");
			super.query = RandomVariableSet.getInstance(success, new HashSet<Constraint>(0));
		}
	}
	
	private class CompetingWorkshopsWithFixedNumberOfPeople extends Problem {
		private int people;
		public CompetingWorkshopsWithFixedNumberOfPeople(int people) {
			this.people = people;
			super.name = "Competing Workshops With Fixed Number of People";
		}
		@Override
		void setNetwork(int size) {
			super.network = Example.competingWorkshopsNetwork(size, people);
			Prv success = super.network.prv("success ( )");
			super.query = RandomVariableSet.getInstance(success, new HashSet<Constraint>(0));
		}
	}
	
	private class WaterSprinkler extends Problem {
		private WaterSprinkler() {
			super.name = "Water Sprinkler";
		}
		@Override
		void setNetwork(int size) {
			super.network = Example.waterSprinklerNetWork(size);
			Prv wetGrass = super.network.prv("wet_grass ( Lot )");
			Set<Constraint> constraints = new HashSet<Constraint>();
			LogicalVariable lot = super.network.lv("Lot");
			for (int i = 1; i < size; i++) {
				Constant loti = lot.population().individualAt(i);
				constraints.add(InequalityConstraint.getInstance(lot, loti));	
			}
			super.query = RandomVariableSet.getInstance(wetGrass, constraints);	
		}
	}
	
	private class PerformanceTest {
		private final int limit;
		private final String fileName;
		private final Experiment experiment;
		
		private final String title;
		private final String step = "Testing with %d individuals...";
		private final String end = "Done.";
		
		public PerformanceTest(Experiment experiment, int limit, String fileName) {
			this.experiment = experiment;
			this.limit = limit;
			this.fileName = fileName;
			this.title = experiment.problem.name() + " - " + experiment.name();
		}
		
		public void run() throws IOException {
			log(title);
			Writer out = new OutputStreamWriter(new FileOutputStream(fileName), ENCODING);
			for (int domainSize = 1; domainSize <= limit; domainSize = increment(domainSize)) {
				log(step, domainSize);
				double duration = experiment.getMeanExecutionTimeFor(domainSize);
				out.write(dataPoint(domainSize, duration));
			}
			out.close();
			log(end);
		}

		private int increment(int domainSize) {
			int increment = (int) Math.pow(10.0, (int) Math.log10(domainSize));
			return domainSize + increment;
		}
		
		private void log(String message, Object ... args) {
			System.out.format(message + "\n", args);
		}
		
		// Returns a string with the data point to be written in output file
		private String dataPoint(int x, double y) {
			return x + " " + y + "\n";
		}
	}
	
	private abstract class Experiment {

		private final Problem problem;
		private final int cycles;
		private final String name;
		
		public Experiment(Problem problem, int cycles, String name) {
			this.problem = problem;
			this.cycles = cycles;
			this.name = name;
		}
		
		private String name() {
			return name;
		}
		
		// returns the mean execution time in milliseconds
		private double getMeanExecutionTimeFor(int domainSize) {
			long sum = 0;
			long duration = 0;
			for (int i = 0; i < cycles; i++) {
				// ACFOVE is mutable, so I need to create a new instance in each iteration
				problem.setNetwork(domainSize);
				ACFOVE acfove = formatProblem();
				duration = getDurationInNanoseconds(acfove);
				sum = sum + duration;
			}
			double mean = sum / cycles / 1e6; 
			return mean;
		}
		
		// returns the execution time to run the specified algorithm
		private long getDurationInNanoseconds(ACFOVE algorithm) {
			long startTime = System.nanoTime();
			algorithm.run();
			long endTime = System.nanoTime();
			return (endTime - startTime);
		}
		
		abstract ACFOVE formatProblem();
	}
	
	private class VeTest extends Experiment {
		public VeTest(Problem problem, int cycles) {
			super(problem, cycles, "VE");
		}
		@Override
		public ACFOVE formatProblem() {
			return super.problem.propositionalizeAll();
		}
	}
	
	private class CfoveTest extends Experiment {
		public CfoveTest(Problem problem, int cycles) {
			super(problem, cycles, "CFOVE");
		}
		@Override
		public ACFOVE formatProblem() {
			return super.problem.removeAggregation();
		}
	}
	
	private class AcfoveTest extends Experiment {
		public AcfoveTest(Problem problem, int cycles) {
			super(problem, cycles, "ACFOVE");
		}
		@Override
		public ACFOVE formatProblem() {
			return super.problem.network();
		}
	}

	private final String SICK_DEATH = OUT_PATH + "experiment_sick_death_network_";
	
	@Test(timeout=800000)
	@Ignore("Test already done")
	public void SickDeathVe() throws IOException {
		Experiment experiment = new VeTest(new SickDeath(), CYCLES);
		int numberOfPoints = 5;
		String fileName = SICK_DEATH + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	@Test(timeout=500000)
	@Ignore("Test already done")
	public void SickDeathCfove() throws IOException {
		Experiment experiment = new CfoveTest(new SickDeath(), CYCLES);
		int numberOfPoints = 500;
		String fileName = SICK_DEATH + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	@Test(timeout=20000)
	@Ignore("Test already done")
	public void SickDeathAcfove() throws IOException {
		Experiment experiment = new AcfoveTest(new SickDeath(), CYCLES);
		int numberOfPoints = 10000;
		String fileName = SICK_DEATH + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}

	private final String WATER_SPRINKLER = OUT_PATH + "experiment_water_sprinkler_network_";
	
	@Test(timeout=10000000)
	@Ignore("Test already done")
	public void WaterSprinklerVe() throws IOException {
		Experiment experiment = new VeTest(new WaterSprinkler(), CYCLES);
		int numberOfPoints = 10;
		String fileName = WATER_SPRINKLER + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	@Test
	@Ignore("Test again")
	public void WaterSprinklerCfove() throws IOException {
		Experiment experiment = new CfoveTest(new WaterSprinkler(), CYCLES);
		int numberOfPoints = 500;
		String fileName = WATER_SPRINKLER + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	@Test
	@Ignore("Test again")
	public void WaterSprinklerAcfove() throws IOException {
		Experiment experiment = new AcfoveTest(new WaterSprinkler(), CYCLES);
		int numberOfPoints = 500;
		String fileName = WATER_SPRINKLER + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	private final String BIG_JACKPOT = OUT_PATH + "experiment_big_jackpot_network_";
	
	@Test(timeout=800000)
	@Ignore("Test already done")
	public void BigJackpotVe() throws IOException {
		Experiment experiment = new VeTest(new BigJackpot(), CYCLES);
		int numberOfPoints = 5;
		String fileName = BIG_JACKPOT + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}

	@Test(timeout=5200000)
	@Ignore("Test already done")
	public void BigJackpotCfove() throws IOException {
		Experiment experiment = new CfoveTest(new BigJackpot(), CYCLES);
		int numberOfPoints = 1000;
		String fileName = BIG_JACKPOT + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}

	@Test(timeout=20000)
	@Ignore("Test already done")
	public void BigJackpotAcfove() throws IOException {
		Experiment experiment = new AcfoveTest(new BigJackpot(), CYCLES);
		int numberOfPoints = 10000;
		String fileName = BIG_JACKPOT + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	private final String COMPETING_WORKSHOPS_PEOPLE = OUT_PATH + "experiment_competing_workshops_network_people_";
	
	@Test
	//@Ignore("correct propositionalization")
	public void CompetingWorkshopsWithFixedNumberOfPeopleVe() throws IOException {
		Experiment experiment = new VeTest(new CompetingWorkshopsWithFixedNumberOfPeople(5), CYCLES);
		int numberOfPoints = 5;
		String fileName = COMPETING_WORKSHOPS_PEOPLE + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	@Test(timeout=15000000)
	@Ignore("Test already done")
	public void CompetingWorkshopsWithFixedNumberOfPeopleCfove() throws IOException {
		Experiment experiment = new CfoveTest(new CompetingWorkshopsWithFixedNumberOfPeople(5), CYCLES);
		int numberOfPoints = 500;
		String fileName = COMPETING_WORKSHOPS_PEOPLE + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	@Test(timeout=15000)
	@Ignore("Test already done")
	public void CompetingWorkshopsWithFixedNumberOfPeopleAcfove() throws IOException {
		Experiment experiment = new AcfoveTest(new CompetingWorkshopsWithFixedNumberOfPeople(5), CYCLES);
		int numberOfPoints = 10000;
		String fileName = COMPETING_WORKSHOPS_PEOPLE + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	private final String COMPETING_WORKSHOPS_WORKSHOPS = OUT_PATH + "experiment_competing_workshops_network_workshops_";
	
	@Test
	//@Ignore
	public void CompetingWorkshopsWithFixedNumberOfWorkshopsVe() throws IOException {
		Experiment experiment = new VeTest(new CompetingWorkshopsWithFixedNumberOfWorkshops(3), CYCLES);
		int numberOfPoints = 10;
		String fileName = COMPETING_WORKSHOPS_WORKSHOPS + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}

	@Test // demora para menos de 10
	//@Ignore
	public void CompetingWorkshopsWithFixedNumberOfWorkshopsCfove() throws IOException {
		Experiment experiment = new CfoveTest(new CompetingWorkshopsWithFixedNumberOfWorkshops(3), CYCLES);
		int numberOfPoints = 100;
		String fileName = COMPETING_WORKSHOPS_WORKSHOPS + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
	@Test
	//@Ignore
	@Ignore("Test already done")
	public void CompetingWorkshopsWithFixedNumberOfWorkshopsAcfove() throws IOException {
		Experiment experiment = new AcfoveTest(new CompetingWorkshopsWithFixedNumberOfWorkshops(3), CYCLES);
		int numberOfPoints = 1000;
		String fileName = COMPETING_WORKSHOPS_WORKSHOPS + experiment.name().toLowerCase() + EXTENSION;
		new PerformanceTest(experiment, numberOfPoints, fileName).run();
	}
	
}