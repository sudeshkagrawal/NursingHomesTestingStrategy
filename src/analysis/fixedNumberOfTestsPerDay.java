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
 * Last Updated: November 23, 2020.
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
	 * Finds detection probability by calling the relevant methods.
	 *
	 * @param g network graph
	 * @param simulationResultsOfBatches simulation results for different batches as an array,
	 *                                   where each element is an instance of {@link simulationRuns}
	 * @param k number of tests per day
	 * @param alpha significance level of the CI on detection probability
	 * @param testingOrder a string denoting testing order ("circular" or "random")
	 * @param testReliabilityBaseSeed base seed for simulating false negative results
	 * @param randomOrderBaseSeed base seed for generating random testing order.
	 */
	public void test(graph g, simulationRuns[] simulationResultsOfBatches, int k, double alpha, String testingOrder,
	                 int testReliabilityBaseSeed, int randomOrderBaseSeed)
	{
		if (testingOrder.equals("circular"))
		{
			testWithCircularOrder(g, simulationResultsOfBatches, k, alpha, testReliabilityBaseSeed);
		}
		else
		{
			if (testingOrder.equals("random"))
			{
				testWithRandomOrder(g, simulationResultsOfBatches, k, alpha,
									testReliabilityBaseSeed, randomOrderBaseSeed);
			}
			else
			{
				System.out.println("Testing order not implemented!");
			}
		}
	}
	
	/**
	 * Finds detection probability for a sequential circular testing order.
	 *
	 * @param g network graph
	 * @param simulationResultsOfBatches simulation results for different batches as an array,
	 *                                   where each element is an instance of {@link simulationRuns}
	 * @param k number of tests per day
	 * @param alpha significance level of the CI on detection probability
	 * @param testReliabilityBaseSeed base seed for simulating false negative results.
	 */
	public void testWithCircularOrder(graph g, simulationRuns[] simulationResultsOfBatches, int k, double alpha,
	                                  int testReliabilityBaseSeed)
	{
		int numberOfBatches = simulationResultsOfBatches.length;
		
		// testing
		//int s = g.getVertexSet().size();
		//System.out.println("s="+s);
		// build the set of test nodes
		List<Integer> nodeList = new ArrayList<>(g.getVertexSet());
		Collections.sort(nodeList);
		
		NormalDistribution myNormDist = new NormalDistribution(0, 1);
		double zValue = myNormDist.inverseCumulativeProbability(1-0.5*alpha);
		
		List<simulationParameters> listOfParams = new ArrayList<>(simulationResultsOfBatches[0]
														.getMapParamToSamples().keySet());
		for (simulationParameters param: listOfParams)
		{
			if (!param.getNetworkName().equals(g.getNetworkName()))
			{
				System.out.println("Network name mismatch in simulation params and graph provided as input, skipping!");
				continue;
			}
			int timeStep = param.getTimeStep();
			double fnRate = param.getFalseNegativeProbability();
			int paramHashCode = param.hashCode();
			System.out.println("Disease testing for \n\t"+param.toString()+"\n\t and k="+k);
			
			int[] sampleSizes = new int[numberOfBatches];
			double[] probabilities = new double[numberOfBatches];
			
			for (int i=0; i<simulationResultsOfBatches.length; i++)
			{
				//System.out.println("\t BATCH "+(i+1));
				SplittableRandom reliabilityGen = new SplittableRandom(testReliabilityBaseSeed+paramHashCode+k+i);
				List<Map<Integer, Set<Integer>>> samples =
														simulationResultsOfBatches[i].getMapParamToSamples().get(param)
																			.getSamplesOfInfectiousNodesAtEachTime();
				
				Map<Integer, Set<Integer>> testNodes = getTestNodes(k, nodeList, timeStep);
				//System.out.println("\t\t Test Schedule each day:\n\t\t\t"+testNodes.toString());
				// disease testing
				int countDetectedSamples = 0;
				for (Map<Integer, Set<Integer>> sample: samples)
				{
					//System.out.println("\t\t Sample:\n\t\t\t"+sample.toString());
					for (int t=1; t<=timeStep; t++)
					{
						//System.out.println("\t\t\t Time "+t);
						// generate virtual detection sample
						List<Integer> virtualDetectionSample = IntStream.range(0, sample.get(t).size())
														.mapToObj(e -> reliabilityGen.nextDouble() <= fnRate ? 0 : 1)
														.collect(Collectors.toList());
						List<Integer> sampleNodes = new ArrayList<>(sample.get(t));
						List<Integer> effectiveSample = IntStream.range(0, sample.get(t).size())
													.mapToObj(e -> sampleNodes.get(e) * virtualDetectionSample.get(e))
													.collect(Collectors.toList());
						//System.out.println("\t\t\t\t Effective sample at t="+t+": "+effectiveSample.toString());
						// set intersection of sample and test nodes for the given t
						Set<Integer> detectedNodes = effectiveSample
								.stream()
								.filter(testNodes.get(t)::contains).collect(Collectors.toSet());
						//System.out.println("\t\t\t\t Detected nodes at t="+t+": "+detectedNodes.toString());
						if (detectedNodes.size()>0)
						{
							countDetectedSamples++;
							break;
						}
					}
				}
				sampleSizes[i] = samples.size();
				probabilities[i] = 1.0* countDetectedSamples /sampleSizes[i];
			}
			String nameOfStatisticalTest;
			statisticalOutput output;
			if (numberOfBatches==1)
			{
				nameOfStatisticalTest = "normal approximation for binomial proportion";
				double standardError = Math.sqrt(probabilities[0]*(1.0-probabilities[0])/sampleSizes[0]);
				double CIWidth = 2*zValue*standardError;
				output = new statisticalOutput(probabilities[0], standardError, alpha,
												nameOfStatisticalTest, CIWidth, sampleSizes[0], 1);
			}
			else
			{
				nameOfStatisticalTest = "normal population test";
				double probability = Arrays.stream(probabilities).sum()/numberOfBatches;
				double standardError = 1.0/Math.sqrt(sampleSizes[0]);
				double CIWidth = 2*zValue*standardError;
				output = new statisticalOutput(probability, standardError, alpha,
												nameOfStatisticalTest, CIWidth, sampleSizes[0], numberOfBatches);
			}
			Pair<simulationParameters, Integer> key = new Pair<>(param, k);
			testResults.put(key, output);
			randomTestingOrder.put(key, false);
			System.out.println("Conditional probability of outbreak detection = "
								+output.getMean()+"+-"+0.5*output.getCIWidth());
		}
	}
	
	/**
	 * Finds detection probability for a sequential random testing order.
	 *
	 * @param g network graph
	 * @param simulationResultsOfBatches simulation results for different batches as an array,
	 *                                   where each element is an instance of {@link simulationRuns}
	 * @param k number of tests per day
	 * @param alpha significance level of the CI on detection probability
	 * @param testReliabilityBaseSeed base seed for simulating false negative results
	 * @param randomOrderBaseSeed base seed for generating random testing order.
	 */
	public void testWithRandomOrder(graph g, simulationRuns[] simulationResultsOfBatches, int k, double alpha,
	                                int testReliabilityBaseSeed, int randomOrderBaseSeed)
	{
		int numberOfBatches = simulationResultsOfBatches.length;
		if (numberOfBatches<2)
		{
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("WARNING: For random testing order number of batches should be larger!");
			System.out.println("---------------------------------------------------------------------------------");
		}
		
		// testing
		//int s = g.getVertexSet().size();
		//System.out.println("s="+s);
		// build the set of test nodes
		List<Integer> nodeList = new ArrayList<>(g.getVertexSet());
		Collections.sort(nodeList);
		
		NormalDistribution myNormDist = new NormalDistribution(0, 1);
		double zValue = myNormDist.inverseCumulativeProbability(1-0.5*alpha);
		
		List<simulationParameters> listOfParams = new ArrayList<>(simulationResultsOfBatches[0]
				.getMapParamToSamples().keySet());
		for (simulationParameters param: listOfParams)
		{
			if (!param.getNetworkName().equals(g.getNetworkName()))
			{
				System.out.println("Network name mismatch in simulation params and graph provided as input, skipping!");
				continue;
			}
			int timeStep = param.getTimeStep();
			double fnRate = param.getFalseNegativeProbability();
			int paramHashCode = param.hashCode();
			System.out.println("Disease testing for \n\t"+param.toString()+"\n\t and k="+k);
			
			int[] sampleSizes = new int[numberOfBatches];
			double[] probabilities = new double[numberOfBatches];
			
			for (int i=0; i<simulationResultsOfBatches.length; i++)
			{
				//System.out.println("\t BATCH "+(i+1));
				SplittableRandom reliabilityGen = new SplittableRandom(testReliabilityBaseSeed+paramHashCode+k+i);
				Random randomOrderGen = new Random(randomOrderBaseSeed +paramHashCode+k+i);
				List<Map<Integer, Set<Integer>>> samples =
						simulationResultsOfBatches[i].getMapParamToSamples().get(param)
								.getSamplesOfInfectiousNodesAtEachTime();
				
				Map<Integer, Set<Integer>> testNodes = getTestNodes(k, nodeList, timeStep, randomOrderGen);
				//System.out.println("\t\t Test Schedule each day:\n\t\t\t"+testNodes.toString());
				// disease testing
				int countDetectedSamples = 0;
				for (Map<Integer, Set<Integer>> sample: samples)
				{
					//System.out.println("\t\t Sample:\n\t\t\t"+sample.toString());
					for (int t=1; t<=timeStep; t++)
					{
						//System.out.println("\t\t\t Time "+t);
						// generate virtual detection sample
						List<Integer> virtualDetectionSample = IntStream.range(0, sample.get(t).size())
								.mapToObj(e -> reliabilityGen.nextDouble() <= fnRate ? 0 : 1)
								.collect(Collectors.toList());
						List<Integer> sampleNodes = new ArrayList<>(sample.get(t));
						List<Integer> effectiveSample = IntStream.range(0, sample.get(t).size())
								.mapToObj(e -> sampleNodes.get(e) * virtualDetectionSample.get(e))
								.collect(Collectors.toList());
						//System.out.println("\t\t\t\t Effective sample at t="+t+": "+effectiveSample.toString());
						// set intersection of sample and test nodes for the given t
						Set<Integer> detectedNodes = effectiveSample
								.stream()
								.filter(testNodes.get(t)::contains).collect(Collectors.toSet());
						//System.out.println("\t\t\t\t Detected nodes at t="+t+": "+detectedNodes.toString());
						if (detectedNodes.size()>0)
						{
							countDetectedSamples++;
							break;
						}
					}
				}
				sampleSizes[i] = samples.size();
				probabilities[i] = 1.0* countDetectedSamples /sampleSizes[i];
			}
			String nameOfStatisticalTest;
			statisticalOutput output;
			if (numberOfBatches==1)
			{
				nameOfStatisticalTest = "normal approximation for binomial proportion";
				double standardError = Math.sqrt(probabilities[0]*(1.0-probabilities[0])/sampleSizes[0]);
				double CIWidth = 2*zValue*standardError;
				output = new statisticalOutput(probabilities[0], standardError, alpha,
						nameOfStatisticalTest, CIWidth, sampleSizes[0], 1);
			}
			else
			{
				nameOfStatisticalTest = "normal population test";
				double probability = Arrays.stream(probabilities).sum()/numberOfBatches;
				double standardError = 1.0/Math.sqrt(sampleSizes[0]);
				double CIWidth = 2*zValue*standardError;
				output = new statisticalOutput(probability, standardError, alpha,
						nameOfStatisticalTest, CIWidth, sampleSizes[0], numberOfBatches);
			}
			Pair<simulationParameters, Integer> key = new Pair<>(param, k);
			testResults.put(key, output);
			randomTestingOrder.put(key, true);
			System.out.println("Conditional probability of outbreak detection (with random order) = "
					+output.getMean()+"+-"+0.5*output.getCIWidth());
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
							"name of statistical test", "batch size", "alpha", "UTC"};
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
			line[11] = String.valueOf(Math.min(0, prob-halfwidth));
			line[12] = String.valueOf(Math.max(prob+halfwidth, 1));
			line[13] = e.getValue().getNameOfStatisticalTest();
			line[14] = String.valueOf(e.getValue().getBatchSize());
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
			str.append("\n\t\t batch size = ").append(testResults.get(e).getBatchSize());
			str.append("\n\t\t sample size = ").append(testResults.get(e).getSampleSize());
			str.append("\n\t\t alpha = ").append(testResults.get(e).getAlpha());
			str.append("\n\t\t half-width: ").append(0.5*testResults.get(e).getCIWidth());
			str.append("\n\t>");
		}
		return str.toString();
	}
}
