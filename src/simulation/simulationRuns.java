package simulation;

import dataTypes.simulationOutput;
import dataTypes.simulationParameters;
import network.graph;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents results of simulation runs.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: November 16, 2020.
 */
public class simulationRuns
{
	/**
	 * A map from {@link dataTypes.simulationParameters} to {@link dataTypes.simulationOutput}.
	 * Each key is a set of parameters for a simulation.
	 * Each value is the simulation output (including simulation time) of the corresponding key.
	 */
	Map<simulationParameters, simulationOutput> mapParamToSamples;
	
	/**
	 * Constructor.
	 */
	public simulationRuns()
	{
		this.mapParamToSamples = new HashMap<>();
	}
	
	/**
	 * Constructor.
	 *
	 * @param mapParamToSamples a map from {@link dataTypes.simulationParameters} to {@link dataTypes.simulationOutput}.
	 */
	public simulationRuns(Map<simulationParameters, simulationOutput> mapParamToSamples)
	{
		this.mapParamToSamples = mapParamToSamples;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationRuns#mapParamToSamples}.
	 */
	public Map<simulationParameters, simulationOutput> getMapParamToSamples()
	{
		return mapParamToSamples;
	}
	
	/**
	 * Simulates infectious nodes at each time in a sample path.
	 * The initial infection is conditional binomial.
	 *
	 * @param originalGraph network graph
	 * @param listOfParams list of simulation parameters
	 * @param baseSeed an integer array of length 4 to act as a base seed for random number generation;
	 *                 actual seed is sum of base seed and hashcode of the parameters in {@code listOfParams};
	 *                 the first base seed is for selection the initial set of infectious nodes;
	 *                 the second base seed is for external infections in subsequent time steps;
	 *                 the third base seed is for internal infections;
	 *                 the fourth base seed is for choosing the number of initial infections.
	 *
	 * @throws Exception thrown if minimum node label in the graph {@code g} is less than 2;
	 *                      or if the length of {@code baseSeed} is not 4.
	 */
	public void simulationForConditionalProbabilityWithLatency(graph originalGraph,
	                                                           List<simulationParameters> listOfParams,
	                                                           int[] baseSeed) throws Exception
	{
		graph g = new graph(originalGraph.getG(), originalGraph.getNetworkName(), false);
		int s = g.getVertexSet().size();
		// check graph node labels >=2
		int minNodeLabel = g.getMinimumNodeLabel();
		if (minNodeLabel<2)
			throw new Exception("Node labels should be >=2!");
		if (baseSeed.length!=4)
			throw new Exception("Length of base seed array should be 4!");
		Set<Integer> actualNodeSet = g.getVertexSet();
		List<Integer> actualNodeList = new ArrayList<>(actualNodeSet);
		// add node 1 as super node to model outside infection
		int supernode = 1;
		g.addVertex(supernode);
		// add an edge from the super node to all other nodes
		g.addEdges(supernode, actualNodeSet);
		
		for (simulationParameters param: listOfParams)
		{
			if (!param.getNetworkName().equals(g.getNetworkName()))
			{
				System.out.println("Parameters are for a different network; skipping "+param.toString());
				continue;
			}
			int timeStep = param.getTimeStep();
			int reps = param.getNumberOfSimulationRepetitions();
			double transmissability = param.getTransmissability();
			int latency = param.getLatency();
			double externalInfectionRate = param.getExternalInfectionProbability();
			int hashcode = param.hashCode();
			
			// Random number generators
			SplittableRandom firstInfectedGen = new SplittableRandom(baseSeed[0]+hashcode);
			SplittableRandom outsideInfectionGen = new SplittableRandom(baseSeed[1]+hashcode);
			SplittableRandom transmissabilityGen = new SplittableRandom(baseSeed[2]+hashcode);
			SplittableRandom binomialGen = new SplittableRandom(baseSeed[3]+hashcode);
			
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
			
			List<Map<Integer, Set<Integer>>> samplesOfInfectiousNodes = new ArrayList<>();
			
			// simulation repetitions
			Set<Integer> infectedNodes = new HashSet<>(s);
			Set<Integer> infectiousNodes = new HashSet<>(s);
			Set<Integer> tmpInfectiousNodes = new HashSet<>(s);
			Set<Integer> tmpInfectedNodes = new HashSet<>(s);
			Map<Integer, Integer> timeSinceInfected = new HashMap<>(s);
			List<Integer> currentNeighbors;
			int initialNumberOfInfections;
			List<Integer> firstInfectiousNodes = new ArrayList<>(s);
			System.out.println("Starting simulation for: \n\t"+param.toString());
			Instant tic = Instant.now();
			for (int x=0; x<reps; x++)
			{
				//System.out.println("\t Simulation repetition "+(x+1));
				samplesOfInfectiousNodes.add(new HashMap<>());
				samplesOfInfectiousNodes.get(x).put(0, new HashSet<>());
				
				// TIME 0
				//System.out.println("\t\t Time 0");
				//System.out.println("\t\t\t Supernode: "+ supernode);
				infectiousNodes.add(supernode);
				samplesOfInfectiousNodes.get(x).get(0).add(supernode);
				initialNumberOfInfections = binomialChoices[x];
				firstInfectiousNodes.addAll(helper.combinatorics.selectRandomElements(actualNodeList,
						initialNumberOfInfections, firstInfectedGen));
				infectiousNodes.addAll(firstInfectiousNodes);
				samplesOfInfectiousNodes.get(x).get(0).addAll(infectiousNodes);
				//System.out.println("\t\t\t No. of nodes infectious at t=0: "+initialNumberOfInfections);
				//System.out.println("\t\t\t Nodes made infectious by super node: "+firstInfectiousNodes);
				//System.out.println("\t\t\t Infectious nodes at the end of day of t=0: "+infectiousNodes);
				
				for (int t=1; t<=timeStep; t++)
				{
					//System.out.println("\t\t Time "+t);
					
					// find nodes infected by super node in the morning of t
					currentNeighbors = g.getNeighborsOfNode(supernode).stream()
										.filter(e -> !infectiousNodes.contains(e))
										.collect(Collectors.toList());
					if (currentNeighbors.size()>0)
					{
						tmpInfectiousNodes.addAll(currentNeighbors.stream()
									.filter(currentNeighbor -> outsideInfectionGen.nextDouble()<=externalInfectionRate)
									.collect(Collectors.toList()));
						//System.out.println("\t\t\t Nodes "+tmpInfectiousNodes.toString()
						//					+" became externally infectious in the morning of t="+t);
						infectiousNodes.addAll(tmpInfectiousNodes);
						infectedNodes.removeAll(tmpInfectiousNodes);
						timeSinceInfected.keySet().removeAll(tmpInfectiousNodes);
						tmpInfectiousNodes.clear();
					}
					// find infected nodes which became infectious after being latent
					for (Integer infectedNode: infectedNodes)
					{
						//System.out.println("\t\t\t Time since infection for node "+infectedNode+" = "
						//					+timeSinceInfected.get(infectedNode));
						if (timeSinceInfected.get(infectedNode)==latency)
							tmpInfectiousNodes.add(infectedNode);
					}
					infectiousNodes.addAll(tmpInfectiousNodes);
					infectedNodes.removeAll(tmpInfectiousNodes);
					timeSinceInfected.keySet().removeAll(tmpInfectiousNodes);
					//System.out.println("\t\t\t Infected nodes "+tmpInfectiousNodes
					//					+" became infectious in the morning of t = "+t);
					tmpInfectiousNodes.clear();
					samplesOfInfectiousNodes.get(x).put(t, new HashSet<>());
					samplesOfInfectiousNodes.get(x).get(t).addAll(infectiousNodes);
					for (Integer infectiousNode: infectiousNodes)
					{
						if (infectiousNode!= supernode)
						{
							// find neighbors of the current infectious nodes which are not already infectious
							currentNeighbors = g.getNeighborsOfNode(infectiousNode).stream()
												.filter(e -> !infectiousNodes.contains(e))
												.collect(Collectors.toList());
							//System.out.println("\t\t\t Node "+infectiousNode+" can infect uninfectious node(s): "
							//					+currentNeighbors+"");
							if (currentNeighbors.size()>0)
							{
								tmpInfectedNodes.addAll(currentNeighbors.stream()
										.filter(currentNeighbor -> transmissabilityGen.nextDouble()<=transmissability)
										.collect(Collectors.toList()));
								// remove nodes which are already infectious
								tmpInfectedNodes.remove(infectiousNode);
								//System.out.println("\t\t\t Node "+infectiousNode+" infected nodes: "
								//					+tmpInfectedNodes.toString());
								
							}
							infectedNodes.addAll(tmpInfectedNodes);
							tmpInfectedNodes.clear();
						}
					}
					// update time since infection
					for (Integer infectedNode: infectedNodes)
					{
						timeSinceInfected.put(infectedNode,
								timeSinceInfected.getOrDefault(infectedNode, 0)+1);
					}
					//System.out.println("\t\t Infected nodes: "+infectedNodes.toString());
					//System.out.println("\t\t Time since infection: "+timeSinceInfected.toString());
					//System.out.println("\t\t Infectious nodes: "+infectiousNodes.toString());
				}
				
				infectedNodes.clear();
				infectiousNodes.clear();
				tmpInfectiousNodes.clear();
				timeSinceInfected.clear();
				firstInfectiousNodes.clear();
			}
			Instant toc = Instant.now();
			System.out.println("Ending simulation for: \n\t"+param.toString());
			double timeElapsedInSeconds = 1.0*Duration.between(tic, toc).toMillis()/1000.0;
			simulationOutput output = new simulationOutput(samplesOfInfectiousNodes, timeElapsedInSeconds);
			mapParamToSamples.put(param, output);
			//System.out.println("---------------------------");
			//System.out.println("Simulation time = "+timeElapsedInSeconds+" second.");
			//System.out.println(output);
			//System.out.println("---------------------------");
		}
	}
}
