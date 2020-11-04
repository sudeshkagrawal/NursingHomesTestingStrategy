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
		double externalInfectionProb = 0.1;
		String simulationsSerialFilename = outputFolder+"nursing_conditionalsim_"+networkName+reps+"reps_s"+s+"_t"
				+timeStep+"_ext"+(int)100*externalInfectionProb
				+"_int"+(int)100*transmissability+"_fn"+falseNegProb+".ser";
		int[] seed = {2507, 2507, 2101, 3567, 1308};
		List<simulationParameters> listOfParams = new ArrayList<>();
		simulationParameters p1 = new simulationParameters(networkName, timeStep,
				reps, falseNegProb, transmissability, externalInfectionProb);
		listOfParams.add(p1);
		simulationRuns simulationResults = new simulationRuns();
		simulationResults.simulationForConditionalProbability(network, listOfParams, seed);
	}
}
