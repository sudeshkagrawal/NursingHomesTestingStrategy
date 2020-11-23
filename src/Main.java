import analysis.fixedNumberOfTestsPerDay;
import dataTypes.simulationParameters;
import network.graph;
import simulation.simulationRuns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		String outputFolder = "./out/production/NursingHomesTestingStrategy/";
		// create nursing network
		int s = 100;
		
//		// complete graph
//		String networkName = "completegraph_staff"+s;
//		String graphOutputFile = outputFolder + "completegraph_staff"+s+"_graph.txt";
//		graph network = new graph(networkName);
//		network.initializeAsCompleteGraph(s, 2);
//		network.writeNetworkToFile(graphOutputFile, false);
		
		// neighboring graph
		int degree = 20;
		String networkName = "neighboringgraph_staff"+s+"_degree"+degree;
		String graphOutputFile = outputFolder + "neighboringgraph_staff"+s+"_degree"+degree+"_graph.txt";
		graph network = new graph(networkName);
		int m = (int) (0.5*degree);
		int[] offsets = new int[m];
		for (int i=0; i<offsets.length; i++)
			offsets[i] = i+1;
		network.initializeAsCirculantGraph(s, offsets,2);
		//network.writeNetworkToFile(graphOutputFile, false);
		
//		// crossing graph
//		int degree = 20;
//		String networkName = "crossinggraph_staff"+s+"_degree"+degree;
//		String graphOutputFile = outputFolder + "crossinggraph_staff"+s+"_degree"+degree+"_graph.txt";
//		graph network = new graph(networkName);
//		int m = (int) (0.5*degree);
//		int constant = (int) Math.floor(0.5*s-1);
//		int[] offsets = new int[m];
//		for (int i=m-1; i>=0; i--)
//			offsets[i] = constant-i;
//		network.initializeAsCirculantGraph(s, offsets, 2);
//		network.writeNetworkToFile(graphOutputFile, true);
		
		// simulation
		int reps = 50000;
		int timeStep = 6;
		double falseNegProb = 0.21;
		double transmissability = 0.05;
		int latency = 3;
		double externalInfectionProb = 0.0001;
		int[] simulationBaseSeeds = {2507, 2507, 2101, 1308};
		int numberOfBatches = 30;
		List<simulationParameters> listOfParams = new ArrayList<>();
		simulationParameters p1 = new simulationParameters(networkName, timeStep,
				reps, falseNegProb, transmissability, latency, externalInfectionProb);
		listOfParams.add(p1);
		simulationRuns[] simulationResultsOfBatches = new simulationRuns[numberOfBatches];
		for (int i=0; i<simulationResultsOfBatches.length; i++)
		{
			simulationResultsOfBatches[i] = new simulationRuns();
			int[] newSeeds = new int[simulationBaseSeeds.length];
			for (int j=0; j<newSeeds.length; j++)
				newSeeds[j] = simulationBaseSeeds[j]+i;
			simulationResultsOfBatches[i].simulationForConditionalProbabilityWithLatency(network,
																							listOfParams, newSeeds);
		}
		
		// disease testing
		String testingOrder = "random";
		String nursingTestResultsFile = outputFolder + "nursingtestresults_"+ networkName + "_" + testingOrder
										+ "_reps" + reps + ".csv";
		
		//int[] k = {3};
		double alpha = 0.05;
		int testReliabilityBaseSeed = 3567;
		int randomOrderBaseSeed = 1118;
		fixedNumberOfTestsPerDay testResults = new fixedNumberOfTestsPerDay();
		for (int i=1; i<=50; i++)
		{
			//testResults.testWithRandomOrder(network, simulationResults, i, r, alpha, baseSeed, randomOrderBaseSeed);
			testResults.test(network, simulationResultsOfBatches, i, alpha, testingOrder,
								testReliabilityBaseSeed, randomOrderBaseSeed);
		}
		//System.out.println(testResults.toString());
		testResults.writeToCSV(nursingTestResultsFile, true);
	}
}
