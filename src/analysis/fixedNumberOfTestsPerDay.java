package analysis;

import com.opencsv.CSVWriter;
import dataTypes.simulationOutput;
import dataTypes.simulationParameters;
import dataTypes.statisticalOutput;
import network.graph;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.Pair;
import simulation.simulationRuns;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Contains methods for fixed number of tests per day testing strategy.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: November 18, 2020.
 */
public class fixedNumberOfTestsPerDay
{
	/**
	 * A map from a {@link org.apache.commons.math3.util.Pair} of {@link dataTypes.simulationParameters} and
	 * number of tests per day to the results of disease testing as {@link dataTypes.statisticalOutput}.
	 */
	Map<Pair<simulationParameters, Integer>, statisticalOutput> testResults;
	
	/**
	 * A map from a {@link org.apache.commons.math3.util.Pair} of {@link dataTypes.simulationParameters} and
	 * number of tests per day to a boolean value indicating whether a random testing order was used.
	 */
	Map<Pair<simulationParameters, Integer>, Boolean> randomTestingOrder;
	
	/**
	 * Constructor.
	 *
	 * @param testResults disease testing results
	 * @param randomTestingOrder boolean value to indicate whether a random testing order was used.
	 */
	public fixedNumberOfTestsPerDay(Map<Pair<simulationParameters, Integer>, statisticalOutput> testResults,
	                                Map<Pair<simulationParameters, Integer>, Boolean> randomTestingOrder)
	{
		this.testResults = testResults;
		this.randomTestingOrder = randomTestingOrder;
	}
	
	/**
	 * Constructor.
	 */
	public fixedNumberOfTestsPerDay()
	{
		this.testResults = new HashMap<>();
		this.randomTestingOrder = new HashMap<>();
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link fixedNumberOfTestsPerDay#testResults}.
	 */
	public Map<Pair<simulationParameters, Integer>, statisticalOutput> getTestResults()
	{
		return testResults;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link fixedNumberOfTestsPerDay#randomTestingOrder}.
	 */
	public Map<Pair<simulationParameters, Integer>, Boolean> getRandomTestingOrder()
	{
		return randomTestingOrder;
	}
	
	/**
	 * Finds detection probability for a sequential circular testing order.
	 *
	 * @param g network graph
	 * @param simulationResults simulation results as an instance of {@link simulationRuns}
	 * @param k number of tests per day
	 * @param alpha significance level of CI on detection probability
	 * @param baseSeed base seed for simulating false negative results.
	 */
	public void test(graph g, simulationRuns simulationResults, int k, double alpha, int baseSeed)
	{
		int s = g.getVertexSet().size();
		//System.out.println("s="+s);
		// build the set of test nodes
		List<Integer> nodeList = new ArrayList<>(g.getVertexSet());
		Collections.sort(nodeList);
		
		NormalDistribution mynormdist = new NormalDistribution(0, 1);
		double zValue = mynormdist.inverseCumulativeProbability(1-0.5*alpha);
		
		for (Map.Entry<simulationParameters, simulationOutput> result:
																simulationResults.getMapParamToSamples().entrySet())
		{
			simulationParameters param = result.getKey();
			if (!param.getNetworkName().equals(g.getNetworkName()))
			{
				System.out.println("Network name mismatch in simulation results and graph provided as input, skipping!");
			}
			int timeStep = param.getTimeStep();
			double fnRate = param.getFalseNegativeProbability();
			int paramHashCode = param.hashCode();
			SplittableRandom reliabilityGen = new SplittableRandom(baseSeed+paramHashCode+k);
			
			System.out.println("Disease testing for \n\t"+param.toString()+"\n\t and k="+k);
			List<Map<Integer, Set<Integer>>> samples = result.getValue().getSamplesOfInfectiousNodesAtEachTime();
			
			Map<Integer, Set<Integer>> testNodes = getTestNodes(k, nodeList, timeStep);
			//System.out.println("\t Test Schedule each day:\n\t\t"+testNodes.toString());
			// disease testing
			int countDetectedSamples = 0;
			for (Map<Integer, Set<Integer>> sample: samples)
			{
				//System.out.println("\t Sample:\n\t\t"+sample.toString());
				for (int t=1; t<=timeStep; t++)
				{
					//System.out.println("\t\t Time "+t);
					// generate virtual detection sample
					List<Integer> virtualDetectionSample = IntStream.range(0, sample.get(t).size())
														.mapToObj(i -> reliabilityGen.nextDouble() <= fnRate ? 0 : 1)
														.collect(Collectors.toList());
					List<Integer> sampleNodes = new ArrayList<>(sample.get(t));
					List<Integer> effectiveSample = IntStream.range(0, sample.get(t).size())
												.mapToObj(i -> sampleNodes.get(i) * virtualDetectionSample.get(i))
												.collect(Collectors.toList());
					//System.out.println("\t\t\t Effective sample at t="+t+": "+effectiveSample.toString());
					// set intersection of sample and test nodes for the given t
					Set<Integer> detectedNodes = effectiveSample
														.stream()
														.filter(testNodes.get(t)::contains).collect(Collectors.toSet());
					//System.out.println("\t\t\t Detected nodes at t="+t+": "+detectedNodes.toString());
					if (detectedNodes.size()>0)
					{
						countDetectedSamples++;
						break;
					}
				}
			}
			int sampleSize = samples.size();
			double probability = 1.0* countDetectedSamples /sampleSize;
			double standardError = Math.sqrt(probability*(1.0-probability)/sampleSize);
			String nameOfStatisticalTest = "normal approximation for binomial proportion";
			double CIWidth = 2*zValue*standardError;
			int replicationSize = 1;
			statisticalOutput output = new statisticalOutput(probability, standardError, alpha,
										nameOfStatisticalTest, CIWidth, sampleSize, replicationSize);
			Pair<simulationParameters, Integer> key = new Pair<>(param, k);
			testResults.put(key, output);
			randomTestingOrder.put(key, false);
			System.out.println("Conditional probability of outbreak detection = "+probability+"+-"+0.5*CIWidth);
		}
	}
	
	/**
	 * Get test nodes for each day.
	 * <br> Overloaded function: {@link fixedNumberOfTestsPerDay#getTestNodes(int, List, int, Random)}.
	 *
	 * @param k number of tests per day
	 * @param nodeList list of nodes to be tested
	 * @param timeStep number of days for which test nodes are needed.
	 * @return nodes to be tested at each time.
	 */
	@org.jetbrains.annotations.NotNull
	private Map<Integer, Set<Integer>> getTestNodes(int k, List<Integer> nodeList, int timeStep)
	{
		Map<Integer, Set<Integer>> testNodes = new LinkedHashMap<>();
		int size = nodeList.size();
		for (int t = 1; t<= timeStep; t++)
		{
			testNodes.put(t, new HashSet<>());
			for (int j = k*(t-1); j< k*t; j++)
				testNodes.get(t).add(nodeList.get(j%size));
		}
		return testNodes;
	}
	
	/**
	 * Get random test nodes for each day.
	 * <br> Overloaded function: {@link fixedNumberOfTestsPerDay#getTestNodes(int, List, int)}.
	 *
	 * @param k number of tests per day
	 * @param nodeList list of nodes to be tested
	 * @param timeStep number of days for which test nodes are needed
	 * @param randomOrderGen an instance of {@link java.util.Random} for generating random order of test nodes.
	 * @return nodes to be tested at each time.
	 */
	@org.jetbrains.annotations.NotNull
	private Map<Integer, Set<Integer>> getTestNodes(int k, List<Integer> nodeList, int timeStep,
	                                                Random randomOrderGen)
	{
		List<Integer> newNodeList = new ArrayList<>(nodeList);
		Collections.shuffle(newNodeList, randomOrderGen);
		return getTestNodes(k, newNodeList, timeStep);
	}
	
	/**
	 * Finds detection probability for a sequential circular testing order.
	 *
	 * @param g network graph
	 * @param simulationResults simulation results as an instance of {@link simulationRuns}
	 * @param k number of tests per day
	 * @param alpha significance level of CI on detection probability
	 * @param baseSeed base seed for simulating false negative results
	 * @param randomOrderBaseSeed base seed for generating random testing order.
	 */
	public void testWithRandomOrder(graph g, simulationRuns simulationResults, int k, double alpha,
	                                int baseSeed, int randomOrderBaseSeed)
	{
		int s = g.getVertexSet().size();
		//System.out.println("s="+s);
		List<Integer> nodeList = new ArrayList<>(g.getVertexSet());
		Collections.sort(nodeList);
		
		NormalDistribution mynormdist = new NormalDistribution(0, 1);
		double zValue = mynormdist.inverseCumulativeProbability(1-0.5*alpha);
		
		for (Map.Entry<simulationParameters, simulationOutput> result:
																simulationResults.getMapParamToSamples().entrySet())
		{
			simulationParameters param = result.getKey();
			if (!param.getNetworkName().equals(g.getNetworkName()))
			{
				System.out.println("Network name mismatch in simulation results and graph provided as input, skipping!");
			}
			int timeStep = param.getTimeStep();
			double fnRate = param.getFalseNegativeProbability();
			int paramHashCode = param.hashCode();
			SplittableRandom reliabilityGen = new SplittableRandom(baseSeed+paramHashCode+k);
			Random randomOrderGen = new Random(randomOrderBaseSeed +paramHashCode+k);
			
			System.out.println("Disease testing with RANDOM ORDER for \n\t"+param.toString()+"\n\t and k="+k);
			List<Map<Integer, Set<Integer>>> samples = result.getValue().getSamplesOfInfectiousNodesAtEachTime();
			
			// build the set of test nodes
			Map<Integer, Set<Integer>> testNodes = getTestNodes(k, nodeList, timeStep, randomOrderGen);
			//System.out.println("\t Test Schedule each day:\n\t\t"+testNodes.toString());
			// disease testing
			int countDetectedSamples = 0;
			for (Map<Integer, Set<Integer>> sample: samples)
			{
				//System.out.println("\t Sample:\n\t\t"+sample.toString());
				for (int t=1; t<=timeStep; t++)
				{
					//System.out.println("\t\t Time "+t);
					// generate virtual detection sample
					List<Integer> virtualDetectionSample = IntStream.range(0, sample.get(t).size())
							.mapToObj(i -> reliabilityGen.nextDouble() <= fnRate ? 0 : 1)
							.collect(Collectors.toList());
					List<Integer> sampleNodes = new ArrayList<>(sample.get(t));
					List<Integer> effectiveSample = IntStream.range(0, sample.get(t).size())
							.mapToObj(i -> sampleNodes.get(i) * virtualDetectionSample.get(i))
							.collect(Collectors.toList());
					//System.out.println("\t\t\t Effective sample at t="+t+": "+effectiveSample.toString());
					// set intersection of sample and test nodes for the given t
					Set<Integer> detectedNodes = effectiveSample
							.stream()
							.filter(testNodes.get(t)::contains).collect(Collectors.toSet());
					//System.out.println("\t\t\t Detected nodes at t="+t+": "+detectedNodes.toString());
					if (detectedNodes.size()>0)
					{
						countDetectedSamples++;
						break;
					}
				}
			}
			int sampleSize = samples.size();
			double probability = 1.0* countDetectedSamples /sampleSize;
			double standardError = Math.sqrt(probability*(1.0-probability)/sampleSize);
			String nameOfStatisticalTest = "normal approximation for binomial proportion";
			double CIWidth = 2*zValue*standardError;
			int replicationSize = 1;
			statisticalOutput output = new statisticalOutput(probability, standardError, alpha,
					nameOfStatisticalTest, CIWidth, sampleSize, replicationSize);
			Pair<simulationParameters, Integer> key = new Pair<>(param, k);
			testResults.put(key, output);
			randomTestingOrder.put(key, true);
			System.out.println("Conditional probability of outbreak detection (with random order) = "
									+probability+"+-"+0.5*CIWidth);
		}
	}
	
	/**
	 * Writes disease testing results to csv file.
	 *
	 * @param filePath path to output file
	 * @param append {@code true}, if you wish to append to existing file; {@code false}, otherwise.
	 * @throws IOException thrown if error in input-output operation.
	 */
	public void writeToCSV(String filePath, boolean append) throws IOException
	{
		File fileObj = new File(filePath);
		String[] header = {"Network name", "simulation reps", "t", "latency", "ext. infection prob",
							"int. infection prob", "fnrate", "no of tests per day", "random test order",
							"outbreak conditional prob", "CI width", "lower CI", "upper CI",
							"name of statistical test", "replication size", "alpha", "UTC"};
		boolean writeHeader = false;
		if (!fileObj.exists())
			writeHeader = true;
		else if (!append)
			writeHeader = true;
		CSVWriter writer = new CSVWriter(new FileWriter(filePath, append));
		if (writeHeader)
		{
			writer.writeNext(header);
			writer.flush();
		}
		String now = Instant.now().toString();
		for (Map.Entry<Pair<simulationParameters, Integer>, statisticalOutput> e: this.testResults.entrySet())
		{
			String[] line = new String[17];
			line[0] = e.getKey().getFirst().getNetworkName();
			line[1] = String.valueOf(e.getKey().getFirst().getNumberOfSimulationRepetitions());
			line[2] = String.valueOf(e.getKey().getFirst().getTimeStep());
			line[3] = String.valueOf(e.getKey().getFirst().getLatency());
			line[4] = String.valueOf(e.getKey().getFirst().getExternalInfectionProbability());
			line[5] = String.valueOf(e.getKey().getFirst().getTransmissability());
			line[6] = String.valueOf(e.getKey().getFirst().getFalseNegativeProbability());
			line[7] = String.valueOf(e.getKey().getSecond());
			line[8] = String.valueOf(this.randomTestingOrder.get(e.getKey()));
			double prob = e.getValue().getMean();
			line[9] = String.valueOf(prob);
			double width = e.getValue().getCIWidth();
			double halfwidth = 0.5*width;
			line[10] = String.valueOf(width);
			line[11] = String.valueOf(prob-halfwidth);
			line[12] = String.valueOf(prob+halfwidth);
			line[13] = e.getValue().getNameOfStatisticalTest();
			line[14] = String.valueOf(e.getValue().getReplicationSize());
			line[15] = String.valueOf(e.getValue().getAlpha());
			line[16] = now;
			writer.writeNext(line);
		}
		writer.flush();
		writer.close();
		System.out.println("Disease testing results successfully written to \""+filePath+"\".");
	}
	
	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder(1000);
		str.append("Results for Fixed Number Of Tests Per Day:");
		for(Pair<simulationParameters, Integer> e: testResults.keySet())
		{
			str.append("\n\t<");
			str.append("\n\t\t ").append(e.getFirst().toString()).append(",");
			str.append("\n\t\t no. of tests per day = ").append(e.getSecond());
			str.append("\n\t\t random testing order: ").append(randomTestingOrder.get(e));
			str.append("\n\t\t detection probability: ").append(testResults.get(e).getMean());
			str.append("\n\t\t statistical test: ").append(testResults.get(e).getNameOfStatisticalTest());
			str.append("\n\t\t replication size = ").append(testResults.get(e).getReplicationSize());
			str.append("\n\t\t sample size = ").append(testResults.get(e).getSampleSize());
			str.append("\n\t\t alpha = ").append(testResults.get(e).getAlpha());
			str.append("\n\t\t half-width: ").append(0.5*testResults.get(e).getCIWidth());
			str.append("\n\t>");
		}
		return str.toString();
	}
}
