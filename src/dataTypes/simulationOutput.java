package dataTypes;

import java.util.*;

/**
 * Represents simulation output.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: November 14, 2020.
 */
public class simulationOutput
{
	/**
	 * Each element in the list is a sample path.
	 * Each sample path maps time to set of infected nodes by that time.
	 */
	List<Map<Integer, Set<Integer>>>  samplesOfInfectedNodesAtEachTime;
	
	/**
	 * Simulation run time.
	 */
	double simulationWallTime;
	
	/**
	 * Constructor.
	 */
	public simulationOutput()
	{
		this.samplesOfInfectedNodesAtEachTime = new ArrayList<>();
	}
	
	/**
	 * Constructor.
	 *
	 * @param samplesOfInfectedNodesAtEachTime sample paths
	 * @param simulationWallTime simulation run time.
	 */
	public simulationOutput(List<Map<Integer, Set<Integer>>> samplesOfInfectedNodesAtEachTime,
	                        double simulationWallTime)
	{
		this.samplesOfInfectedNodesAtEachTime = samplesOfInfectedNodesAtEachTime;
		this.simulationWallTime = simulationWallTime;
	}
	
	/**
	 * Copy constructor (references copied instead of value, for non-primitive data types).
	 *
	 * @param output an instance of {@link simulationOutput}.
	 */
	public simulationOutput(simulationOutput output)
	{
		this.samplesOfInfectedNodesAtEachTime = output.samplesOfInfectedNodesAtEachTime;
		this.simulationWallTime = output.simulationWallTime;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationOutput#getSamplesOfInfectedNodesAtEachTime()}.
	 */
	public List<Map<Integer, Set<Integer>>> getSamplesOfInfectedNodesAtEachTime()
	{
		return samplesOfInfectedNodesAtEachTime;
	}
	
	/**
	 * Setter.
	 *
	 * @param samplesOfInfectedNodesAtEachTime sample paths.
	 */
	public void setSamplesOfInfectedNodesAtEachTime(List<Map<Integer, Set<Integer>>> samplesOfInfectedNodesAtEachTime)
	{
		this.samplesOfInfectedNodesAtEachTime = samplesOfInfectedNodesAtEachTime;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link simulationOutput#simulationWallTime}.
	 */
	public double getSimulationWallTime()
	{
		return simulationWallTime;
	}
	
	/**
	 * Setter.
	 *
	 * @param simulationWallTime simulation wall time.
	 */
	public void setSimulationWallTime(double simulationWallTime)
	{
		this.simulationWallTime = simulationWallTime;
	}
	
	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString()
	{
		return "Simulation output: "
				+"sample paths: "+this.samplesOfInfectedNodesAtEachTime+"; "
				+"simulation wall time = "+this.simulationWallTime+".";
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
		simulationOutput that = (simulationOutput) o;
		return Double.compare(that.simulationWallTime, simulationWallTime) == 0 &&
				Objects.equals(samplesOfInfectedNodesAtEachTime, that.samplesOfInfectedNodesAtEachTime);
	}
	
	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode()
	{
		return Objects.hash(samplesOfInfectedNodesAtEachTime, simulationWallTime);
	}
}
