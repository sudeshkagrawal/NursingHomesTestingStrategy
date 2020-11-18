import analysis.fixedNumberOfTestsPerDay;
import dataTypes.simulationParameters;
import network.graph;
import simulation.simulationRuns;

import java.util.ArrayList;
import java.util.List;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		String outputFolder = "./out/production/NursingHomesTestingStrategy/";
		// create nursing network
		int s = 100;
		String networkName = "completegraph_staff"+s;
		graph network = new graph(networkName);
		network.initializeAsCompleteGraph(s, 2);
		
		// simulation
		int reps = 50000;
		int timeStep = 3;
		double falseNegProb = 0.3;
		double transmissability = 0.05;
		int latency = 1;
		double externalInfectionProb = 0.0001;
		int[] seed = {2507, 2507, 2101, 1308};
		List<simulationParameters> listOfParams = new ArrayList<>();
		simulationParameters p1 = new simulationParameters(networkName, timeStep,
				reps, falseNegProb, transmissability, latency, externalInfectionProb);
		listOfParams.add(p1);
		simulationRuns simulationResults = new simulationRuns();
		simulationResults.simulationForConditionalProbabilityWithLatency(network, listOfParams, seed);
		
		
		// disease testing
		String nursingTestResultsFile = outputFolder + "nursingtestresults.csv";
		boolean append = true;
		//int[] k = {3};
		int k_max = (int) Math.floor(s/timeStep);
		double r = 0.3;
		double alpha = 0.05;
		int baseSeed = 3567;
		int randomOrderBaseSeed = 1118;
		fixedNumberOfTestsPerDay testResults = new fixedNumberOfTestsPerDay();
		for (int i=1; i<=k_max; i++)
		{
			testResults.testWithRandomOrder(network, simulationResults, i, r, alpha, baseSeed, randomOrderBaseSeed);
		}
		//System.out.println(testResults.toString());
		testResults.writeToCSV(nursingTestResultsFile, append);
	}
}
