package dataTypes;

import java.util.Objects;

/**
 * Represents parameters needed for simulation.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: November 12, 2020.
 */
public class simulationParameters
{
	/**
	 * Name of the network.
	 */
	String networkName;
	/**
	 * Time step of each simulation run.
	 */
	int timeStep;
	/**
	 * Number of times simulation is repeated.
	 * Each repetition represents a sample path.
	 */
	int numberOfSimulationRepetitions;
	/**
	 * False negative probability of a disease test.
	 * Assuming it is the same for all detectors.
	 */
	double falseNegativeProbability;
	/**
	 * Daily probability of infection between neighbors.
	 */
	double transmissability;
	
	/**
	 * The number of days to move from the exposed state to the infected state.
	 * Once a node is exposed to an infected neighboring node, it moves to an infected state after {@code latency} days.
	 */
	int latency;
	
	/**
	 * Daily probability of infection from outside world.
	 */
	double externalInfectionProbability;
	
	/**
	 * Constructor.
	 *
	 * @param networkName name of the network
	 * @param timeStep time step of each simulation run
	 * @param numberOfSimulationRepetitions number of times simulation is repeated
	 * @param falseNegativeProbability false negative probability of testing kit
	 * @param transmissability daily probability of transmission
	 * @param latency number of days to move from the exposed state to the infected state
	 * @param externalInfectionProbability daily probability of infection from outside world.
	 */
	public simulationParameters(String networkName, int timeStep, int numberOfSimulationRepetitions,
	                            double falseNegativeProbability, double transmissability, int latency,
	                            double externalInfectionProbability)
	{
		this.networkName = networkName;
		this.timeStep = timeStep;
		this.numberOfSimulationRepetitions = numberOfSimulationRepetitions;
		this.falseNegativeProbability = falseNegativeProbability;
		this.transmissability = transmissability;
		this.latency = latency;
		this.externalInfectionProbability = externalInfectionProbability;
	}
	
	/**
	 * Copy constructor.
	 *
	 * @param param an instance of {@link simulationParameters}.
	 */
	public simulationParameters(simulationParameters param)
	{
		this.networkName = param.networkName;
		this.timeStep = param.timeStep;
		this.numberOfSimulationRepetitions = param.numberOfSimulationRepetitions;
		this.falseNegativeProbability = param.falseNegativeProbability;
		this.transmissability = param.transmissability;
		this.latency = param.latency;
		this.externalInfectionProbability = param.externalInfectionProbability;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationParameters#networkName}.
	 */
	public String getNetworkName()
	{
		return networkName;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationParameters#timeStep}.
	 */
	public int getTimeStep()
	{
		return timeStep;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationParameters#numberOfSimulationRepetitions}.
	 */
	public int getNumberOfSimulationRepetitions()
	{
		return numberOfSimulationRepetitions;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationParameters#falseNegativeProbability}.
	 */
	public double getFalseNegativeProbability()
	{
		return falseNegativeProbability;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationParameters#transmissability}.
	 */
	public double getTransmissability()
	{
		return transmissability;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationParameters#latency}.
	 */
	public int getLatency()
	{
		return latency;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationParameters#externalInfectionProbability}.
	 */
	public double getExternalInfectionProbability()
	{
		return externalInfectionProbability;
	}
	
	/**
	 * Setter.
	 *
	 * @param networkName name of the network.
	 */
	public void setNetworkName(String networkName)
	{
		this.networkName = networkName;
	}
	
	/**
	 * Setter.
	 *
	 * @param timeStep time step of each simulation run.
	 */
	public void setTimeStep(int timeStep)
	{
		this.timeStep = timeStep;
	}
	
	/**
	 * Setter.
	 *
	 * @param numberOfSimulationRepetitions number of times simulation is repeated.
	 */
	public void setNumberOfSimulationRepetitions(int numberOfSimulationRepetitions)
	{
		this.numberOfSimulationRepetitions = numberOfSimulationRepetitions;
	}
	
	/**
	 * Setter.
	 *
	 * @param falseNegativeProbability false negative probability of testing kit.
	 */
	public void setFalseNegativeProbability(double falseNegativeProbability)
	{
		this.falseNegativeProbability = falseNegativeProbability;
	}
	
	/**
	 * Setter.
	 *
	 * @param transmissability daily probability of transmission.
	 */
	public void setTransmissability(double transmissability)
	{
		this.transmissability = transmissability;
	}
	
	/**
	 * Setter.
	 *
	 * @param latency number of days to move from the exposed state to the infected state.
	 */
	public void setLatency(int latency)
	{
		this.latency = latency;
	}
	
	/**
	 * Setter.
	 *
	 * @param externalInfectionProbability daily probability of infection from outside world.
	 */
	public void setExternalInfectionProbability(double externalInfectionProbability)
	{
		this.externalInfectionProbability = externalInfectionProbability;
	}
	
	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString()
	{
		return "Simulation parameters: "
				+"network --- "+networkName+"; "
				+"time step = "+timeStep+"; "
				+"simulation repetitions = "+numberOfSimulationRepetitions+"; "
				+"false negative probability = "+falseNegativeProbability+"; "
				+"transmissability = "+transmissability+"; "
				+"latency = "+latency+"; "
				+"external infection probability = "+externalInfectionProbability+".";
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * Used guidelines at <a href="http://www.technofundo.com/tech/java/equalhash.html" target="_blank">
	 *     "Equals and Hash Code"</a>.
	 *
	 * @param o the reference object with which to compare.
	 * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		// this instance check
		if (this == o) return true;
		// null check
		if (o == null || getClass() != o.getClass()) return false;
		simulationParameters that = (simulationParameters) o;
		return timeStep == that.timeStep &&
				numberOfSimulationRepetitions == that.numberOfSimulationRepetitions &&
				Double.compare(that.falseNegativeProbability, falseNegativeProbability) == 0 &&
				Double.compare(that.transmissability, transmissability) == 0 &&
				latency == that.latency &&
				Double.compare(that.externalInfectionProbability, externalInfectionProbability) == 0 &&
				networkName.equals(that.networkName);
	}
	
	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode()
	{
		return Objects.hash(networkName, timeStep, numberOfSimulationRepetitions, falseNegativeProbability,
								transmissability, latency, externalInfectionProbability);
	}
}
