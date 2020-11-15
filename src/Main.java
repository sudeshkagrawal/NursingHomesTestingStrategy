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
		int s = 20;
		String networkName = "completegraph_staff"+s;
		graph network = new graph(networkName);
		network.initializeAsCompleteGraph(s, 2);

		// simulation
		int reps = 5;
		int timeStep = 3;
		double falseNegProb = 0.25;
		double transmissability = 0.1;
		int latency = 2;
		double externalInfectionProb = 0.1;
		int[] seed = {2507, 2507, 2101, 1308};
		List<simulationParameters> listOfParams = new ArrayList<>();
		simulationParameters p1 = new simulationParameters(networkName, timeStep,
				reps, falseNegProb, transmissability, latency, externalInfectionProb);
		listOfParams.add(p1);
		simulationRuns simulationResults = new simulationRuns();
		simulationResults.simulationForConditionalProbabilityWithLatency(network, listOfParams, seed);
	}
}
