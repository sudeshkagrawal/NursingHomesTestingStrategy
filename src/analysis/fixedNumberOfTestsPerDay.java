package analysis;

import dataTypes.simulationOutput;
import dataTypes.simulationParameters;
import network.graph;
import simulation.simulationRuns;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Contains methods for fixed number of tests per day testing strategy.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: November 16, 2020.
 */
public class fixedNumberOfTestsPerDay
{
	
	public void test(graph g, simulationRuns simulationResults, int k, double fnRate, int baseSeed)
	{
		int s = g.getVertexSet().size();
		List<Integer> nodeList = new ArrayList<>(g.getVertexSet());
		
		for (Map.Entry<simulationParameters, simulationOutput> result: simulationResults.getMapParamToSamples().entrySet())
		{
			simulationParameters param = result.getKey();
			int paramHashCode = param.hashCode();
			SplittableRandom reliabilityGen = new SplittableRandom(baseSeed+paramHashCode);
			
			System.out.println("Disease testing for \n\t"+param.toString()+"\n\t and k="+k);
			List<Map<Integer, Set<Integer>>> samples = result.getValue().getSamplesOfInfectiousNodesAtEachTime();
			
			// check k<max_k
			int timeStep = param.getTimeStep();
			int maxValueOfK = (int) Math.floor(1.0*s/timeStep); // max tests per day
			System.out.println("\t s="+s);
			System.out.println("\t Max allowed value of k="+maxValueOfK);
			if (k>maxValueOfK)
			{
				System.out.println("\t Maximum value of tests per day is "+maxValueOfK+"; skipping "+param.toString());
				continue;
			}
			// build the set of test nodes
			Map<Integer, Set<Integer>> testNodes = new LinkedHashMap<>();
			for (int t=1; t<=timeStep; t++)
			{
				testNodes.put(t, new HashSet<>());
				for (int j=k*(t-1); j<k*t; j++)
					testNodes.get(t).add(nodeList.get(j));
			}
			//System.out.println("\t Test Schedule each day:\n\t\t"+testNodes.toString());
			// disease testing
			int count = 0;
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
						count++;
						break;
					}
				}
			}
			double probability = 1.0*count/samples.size();
			System.out.println("Conditional probability of outbreak detection = "+probability);
		}
	}
}
