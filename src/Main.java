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
//		network.writeNetworkToFile(graphOutputFile, true);
		
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
		network.writeNetworkToFile(graphOutputFile, true);
		
//		// crossing graph
//		String networkName = "crossinggraph_staff"+s;
//		graph network = new graph(networkName);
//		network.initializeAsCompleteGraph(s, 2);
		
		// simulation
		int reps = 50000;
		int timeStep = 6;
		double falseNegProb = 0.21;
		double transmissability = 0.05;
		int latency = 3;
		double externalInfectionProb = 0.0001;
		int[] seed = {2507, 2507, 2101, 1308};
		List<simulationParameters> listOfParams = new ArrayList<>();
		simulationParameters p1 = new simulationParameters(networkName, timeStep,
				reps, falseNegProb, transmissability, latency, externalInfectionProb);
		listOfParams.add(p1);
		simulationRuns simulationResults = new simulationRuns();
		simulationResults.simulationForConditionalProbabilityWithLatency(network, listOfParams, seed);


		// disease testing
		String nursingTestResultsFile = outputFolder + "nursingtestresults_"+ networkName + "_reps" + reps + ".csv";
		boolean append = true;
		//int[] k = {3};
		double alpha = 0.05;
		int baseSeed = 3567;
		int randomOrderBaseSeed = 1118;
		fixedNumberOfTestsPerDay testResults = new fixedNumberOfTestsPerDay();
		for (int i=1; i<=50; i++)
		{
			//testResults.testWithRandomOrder(network, simulationResults, i, r, alpha, baseSeed, randomOrderBaseSeed);
			testResults.test(network, simulationResults, i, alpha, baseSeed);
		}
		//System.out.println(testResults.toString());
		testResults.writeToCSV(nursingTestResultsFile, append);
	}
}
