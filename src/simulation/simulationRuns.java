package simulation;

import dataTypes.simulationParameters;
import network.graph;
import org.jgrapht.alg.util.Pair;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents results of simulation runs.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: November 4, 2020.
 */
public class simulationRuns
{
	/**
	 * A map from {@link dataTypes.simulationParameters} to samples (as a list) of a simulation run.
	 * Each sample in the list is a dictionary which maps time to set
	 * of virtually detected infected nodes till that time (<b>cumulative virtual infections</b>).
	 */
	Map<simulationParameters, List<Map<Integer, Set<Integer>>>> mapParamToEffectiveSimulationRuns;
	
	/**
	 * A map from {@link dataTypes.simulationParameters} to samples (as a list) of a simulation run.
	 * Each sample in the list is a dictionary which maps time to set
	 * of infected nodes till that time (cumulative infection).
	 */
	Map<simulationParameters, List<Map<Integer, Set<Integer>>>> mapParamToSimulationRuns;
	
	/**
	 * A map from {@link dataTypes.simulationParameters} to simulation run time.
	 */
	Map<simulationParameters, Double> mapParamToSimulationWallTime;
	
	/**
	 * Constructor.
	 */
	public simulationRuns()
	{
		this.mapParamToEffectiveSimulationRuns = new HashMap<>();
		this.mapParamToSimulationRuns = new HashMap<>();
		this.mapParamToSimulationWallTime = new HashMap<>();
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationRuns#mapParamToEffectiveSimulationRuns}.
	 */
	public Map<simulationParameters, List<Map<Integer, Set<Integer>>>> getMapParamToEffectiveSimulationRuns()
	{
		return mapParamToEffectiveSimulationRuns;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationRuns#mapParamToSimulationRuns}.
	 */
	public Map<simulationParameters, List<Map<Integer, Set<Integer>>>> getMapParamToSimulationRuns()
	{
		return mapParamToSimulationRuns;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationRuns#mapParamToSimulationWallTime}.
	 */
	public Map<simulationParameters, Double> getMapParamToSimulationWallTime()
	{
		return mapParamToSimulationWallTime;
	}
	
	/**
	 *
	 * Graph {@code g} should have non-negative integer node labels starting from {@code >=2}
	 *
	 * @param g network graph
	 * @param listOfParams list of the set of parameters used to get simulation results
	 * @param seed an integer array of length 5;
	 *             the first seed is for randomly selecting the set of infected nodes in the first time step;
	 *             the second seed is for selecting externally infected nodes;
	 *             the third seed is for selecting neighbors for infection;
	 *             the fourth seed is for reliability in detection;
	 *             and the fifth seed is for finding the number of nodes to initially infect.
	 * @throws Exception thrown if {@link helper.combinatorics#nChoosek(int, int)} or
	 *  {@link helper.combinatorics#discreteProbabilityChoice(List, int[], double[])} or
	 *  {@link helper.combinatorics#discreteProbabilityChoice(double[], int[], double[])} throws an exception,
	 *  or if there is a node label <2.
	 */
	public void simulationForConditionalProbability(graph g,
                                                       List<simulationParameters> listOfParams,
                                                           int[] seed) throws Exception
	{
		int s = g.getVertexSet().size();
		// check graph node labels >=2
		int minNodeLabel = g.getMinimumNodeLabel();
		Set<Integer> actualNodeSet = g.getVertexSet();
		List<Integer> actualNodeList = new ArrayList<>(actualNodeSet);
		// graph remappedGraph;
		if (minNodeLabel<2)
		{
			throw new Exception("Node labels should be >=2!");
			// System.out.println("Minimum node label = "+minNodeLabel);
			// System.out.println("Temporarily changing node labels...");
			// remappedGraph = new graph(g.remapNodeLabels(2), g.getNetworkName(), false);
		}
		// else
		// {
		// 	remappedGraph = g;
		// }
		
		// add node 1 as super node to model outside infection
		int supernode = 1;
		g.addVertex(supernode);
		g.addEdges(supernode, actualNodeSet);
		
		for (simulationParameters param: listOfParams)
		{
			int timeSteps = param.getTimeStep();
			int reps = param.getNumberOfSimulationRepetitions();
			double fnrate = param.getFalseNegativeProbability();
			double transmissability = param.getTransmissability();
			double externalInfectionRate = param.getExternalInfectionProbability();
			int hashcode = param.hashCode();
			
			// Random number generators
			SplittableRandom firstInfectedGen = new SplittableRandom(seed[0]+hashcode);
			SplittableRandom outsideInfectionGen = new SplittableRandom(seed[1]+hashcode);
			SplittableRandom transmissabilityGen = new SplittableRandom(seed[2]+hashcode);
			SplittableRandom reliabilityGen = new SplittableRandom(seed[3]+hashcode);
			SplittableRandom binomialGen = new SplittableRandom(seed[4]+hashcode);
			
			// Finding truncated Bin(s, p)
			double q = 1-externalInfectionRate;
			double factor = 1.0/(1.0-Math.pow(q, s));
			double[] conditionalProb = new double[s];
			for (int k=1; k<=s; k++)
			{
				conditionalProb[k-1] = factor*Math.pow(externalInfectionRate, k)*Math.pow(q, s-k)
						*helper.combinatorics.nChoosek(s, k);
			}
			// Generate number of initial infections (at t=1) for each run using truncated binomial
			double[] binomialGenChoice = IntStream.range(0, reps).mapToDouble(x -> binomialGen.nextDouble()).toArray();
			int[] stateSpace = new int[s];
			for (int i=1; i<=s; i++)
				stateSpace[i-1] = i;
			int[] binomialChoices = helper.combinatorics.discreteProbabilityChoice(binomialGenChoice,
																					stateSpace, conditionalProb);
			
			
			List<Map<Integer, Set<Integer>>> timedSamplePathRuns = new ArrayList<>(reps);
			List<Map<Integer, Set<Integer>>> effectiveTimedSamplePathRuns = new ArrayList<>(reps);
			
			// simulation repetitions
			Set<Integer> infected = new HashSet<>();
			Set<Integer> tmpInfected = new HashSet<>();
			List<Integer> currentNeighbors;
			Set<Integer> currentInfected = new HashSet<>();
			Map<Integer, Set<Integer>> cumulativeInfected = new Hashtable<>();
			Map<Integer, Set<Integer>> effectiveCumulativeInfected = new Hashtable<>(); // virtually detected infections
			
			int[] binaryStateSpace = {0, 1};
			double[] binaryStateSpacePMF = {fnrate, 1.0-fnrate};
			List<Double> reliabilityGenRandomChoices = new ArrayList<>(s);
			int initialNumberOfInfections;
			List<Integer> firstInfectedNodes = new ArrayList<>(s);
			List<Integer> virtualDetection = new ArrayList<>(s);
			int initialLocation = supernode;
			System.out.println("Starting simulation for: \n\t"+param.toString());
			Instant tic = Instant.now();
			for (int x=0; x<reps; x++)
			{
				//System.out.println("\t Simulation run "+(x+1));
				infected.clear();
				cumulativeInfected.clear();
				effectiveCumulativeInfected.clear();
				firstInfectedNodes.clear();
				virtualDetection.clear();
				reliabilityGenRandomChoices.clear();
				
				// TIME 0
				//System.out.println("\t\t Time 0");
				//System.out.println("\t\t\t Supernode: "+initialLocation);
				infected.add(initialLocation);
				cumulativeInfected.put(0, new HashSet<>());
				//cumulativeInfected.get(0).add(initialLocation);
				effectiveCumulativeInfected.put(0, new HashSet<>());
				//effectiveCumulativeInfected.get(0).add(initialLocation);
				
				// TIME 1
				//System.out.println("\t\t Time 1");
				initialNumberOfInfections = binomialChoices[x];
				//System.out.println("\t\t\t No. of nodes infected at t=1: "+initialNumberOfInfections);
				firstInfectedNodes.addAll(helper.combinatorics.selectRandomElements(actualNodeList,
						initialNumberOfInfections, firstInfectedGen));
				IntStream.range(0, initialNumberOfInfections)
							.mapToObj(e -> reliabilityGen.nextDouble()).forEach(reliabilityGenRandomChoices::add);
				infected.addAll(firstInfectedNodes);
				cumulativeInfected.put(1, new HashSet<>(infected));
				cumulativeInfected.get(1).remove(supernode);
				// Flip a coin "initialNumberOfInfections"-times to check if test gave false negative results
				virtualDetection.addAll(helper.combinatorics.discreteProbabilityChoice(reliabilityGenRandomChoices,
						binaryStateSpace, binaryStateSpacePMF));
				//System.out.println("\t\t\t Node "+supernode+" infected node(s): "+firstInfectedNodes.toString());
				//System.out.println("\t\t\t Results of virtual_detection: "+virtualDetection.toString());
				
				// add virtually detected nodes from previous time step
				effectiveCumulativeInfected.put(1, new HashSet<>(effectiveCumulativeInfected.get(0)));
				// add virtually detected nodes from current time step
				IntStream.range(0, initialNumberOfInfections)
						.forEach(i -> effectiveCumulativeInfected.get(1)
								.add(firstInfectedNodes.get(i) * virtualDetection.get(i)));
				// Remove possible 0s
				effectiveCumulativeInfected.get(1).remove(0);
				//System.out.println("\t\t\t Cumulative infected nodes: "+cumulativeInfected.get(1));
				//System.out.println("\t\t\t Cumulative virtually detected infected node(s): "
				//		+effectiveCumulativeInfected.get(1));
				virtualDetection.clear();
				
				for (int t=2; t<=timeSteps; t++)
				{
					//System.out.println("\t\t Time: "+t);
					tmpInfected.clear();
					virtualDetection.clear();
					
					// iterate through infected nodes
					for (Integer node: infected)
					{
						currentInfected.clear();
						// finds neighbors of the current infected node which have not been infected so far
						currentNeighbors = g.getNeighborsOfNode(node).stream()
											.filter(obj -> !Stream.concat(infected.stream(), tmpInfected.stream())
											.collect(Collectors.toSet()).contains(obj))
											.collect(Collectors.toList());
//						System.out.println("\t\t\t Node "+node+" can infect uninfected node(s): "+currentNeighbors+"");
						if (currentNeighbors.size()>0)
						{
							// if spread is from super node
							if (node ==initialLocation)
							{
								getInfectedNeighbors(fnrate, externalInfectionRate, outsideInfectionGen, reliabilityGen,
										currentNeighbors, currentInfected, virtualDetection);
							}
							else
							{
								getInfectedNeighbors(fnrate, transmissability, transmissabilityGen, reliabilityGen,
										currentNeighbors, currentInfected, virtualDetection);
							}
							
							tmpInfected.addAll(currentInfected);
						}
					}
					infected.addAll(tmpInfected);
//					System.out.println("\t\t\t Infected nodes: "+infected);
					cumulativeInfected.put(t, new HashSet<>(infected));
					cumulativeInfected.get(t).remove(supernode);
					effectiveCumulativeInfected.put(t, new HashSet<>(effectiveCumulativeInfected.get(t-1)));
					effectiveCumulativeInfected.get(t).addAll(new HashSet<>(virtualDetection));
					//System.out.println("\t\t\t Cumulative infected nodes: "+cumulativeInfected.get(t));
					//System.out.println("\t\t\t Cumulative virtually detected infected node(s): "
					//		+effectiveCumulativeInfected.get(t));
				}
				timedSamplePathRuns.add(cumulativeInfected.entrySet()
									.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
				effectiveTimedSamplePathRuns.add(effectiveCumulativeInfected.entrySet()
									.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
			}
			Instant toc = Instant.now();
			System.out.println("Ending simulation for: \n\t"+param.toString());
			double timeElapsedInSeconds = 1.0*Duration.between(tic, toc).toMillis()/1000.0;
			mapParamToEffectiveSimulationRuns.put(param, effectiveTimedSamplePathRuns);
			mapParamToEffectiveSimulationRuns.put(param, timedSamplePathRuns);
			mapParamToSimulationWallTime.put(param, timeElapsedInSeconds);
			System.out.println("---------------------------");
			System.out.println("Simulation time = "+timeElapsedInSeconds+" second.");
			System.out.println(timedSamplePathRuns.toString());
			System.out.println(effectiveTimedSamplePathRuns.toString());
			System.out.println("---------------------------");
		}
	}
	
	/**
	 * Private method to get infected neighbors and virtually infected nodes.
	 * The list passed as parameters are updated.
	 *
	 * @param fnrate false negative probability
	 * @param infectionRate probability of infecting neighbors
	 * @param infectionGen an instance of {@link java.util.SplittableRandom} to randomly select neighbors to infect
	 * @param reliabilityGen an instance of {@link java.util.SplittableRandom} for virtual detections
	 * @param currentNeighbors list of current neighbors
	 * @param currentInfected set of currently infected nodes (may be updated)
	 * @param virtualDetection list of virtually detected infected nodes (may be updated)
	 */
	private void getInfectedNeighbors(double fnrate, double infectionRate,
	                                  SplittableRandom infectionGen, SplittableRandom reliabilityGen,
	                                  List<Integer> currentNeighbors, Set<Integer> currentInfected,
	                                  List<Integer> virtualDetection)
	{
		List<Integer> currentInfectedNeighbors = currentNeighbors.stream()
								.filter(currentNeighbor -> infectionGen.nextDouble() <= infectionRate)
								.collect(Collectors.toCollection(() -> new ArrayList<>(currentNeighbors.size())));
		// here virtual detection is a list of nodes NOT a list of binary values
		List<Integer> currentVirutalDetections = currentInfectedNeighbors.stream()
				.filter(currentInfectedNeighbor -> reliabilityGen.nextDouble() > fnrate).collect(Collectors.toList());
		//System.out.println("\t\t\t\t Current infected nodes "+currentInfectedNeighbors.toString());
		//System.out.println("\t\t\t\t Current virtually detected infected nodes "+currentVirutalDetections.toString());
		currentInfected.addAll(currentInfectedNeighbors);
		virtualDetection.addAll(currentVirutalDetections);
		
		// // OLDER CODE
		//for (Integer currentNeighbor : currentNeighbors)
		//{
		//	if (infectionGen.nextDouble() <= infectionRate)
		//	{
		//		currentInfected.add(currentNeighbor);
		//		// here virtual detection is a list of nodes NOT a list of binary values
		//		if (reliabilityGen.nextDouble() > fnrate)
		//		{
		//			virtualDetection.add(currentNeighbor);
		//			System.out.println("\t\t\t\t Infected node " +
		//					currentNeighbor + " was detected.");
		//		}
		//		else
		//		{
		//			System.out.println("\t\t\t\t Infected node " +
		//					currentNeighbor + " was NOT detected.");
		//		}
		//	}
		//}
	}
}
