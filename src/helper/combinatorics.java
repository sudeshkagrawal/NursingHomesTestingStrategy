package helper;

import java.math.BigInteger;
import java.util.*;

/**
 * Contains methods for some combinatorial operations.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: November 26, 2020.
 */
public class combinatorics
{
	/**
	 * Finds {@code n}-choose-{@code k (nCk)}.
	 *
	 * @param n number of objects
	 * @param k number of selections.
	 * @return {@code n}-choose-{@code k (nCk)}.
	 * @throws Exception thrown if any of the input is a negative integer, or if {@code n<k}.
	 */
	public static long nChoosek(int n, int k) throws Exception
	{
		if ((n<0) || (k<0))
			throw new Exception("Inputs should be non-negative integers!");
		if (n<k)
			throw new Exception("'n<k' is not allowed!");
		
		BigInteger numerator = new BigInteger("1");
		BigInteger denominator = new BigInteger("1");
		for (int i=1; i<=k; i++)
		{
			denominator = denominator.multiply(BigInteger.valueOf(i));
			numerator = numerator.multiply(BigInteger.valueOf(n-i+1));
		}
		BigInteger result = numerator.divide(denominator);
		return result.longValue();
	}
	
	/**
	 * Chooses elements from the state space of a distribution with probability mass function {@code pmf} based on
	 * random stream {@code randomchoices}.
	 * <br>
	 * Overloaded function: see {@link combinatorics#discreteProbabilityChoice(List, int[], double[])}.
	 *
	 * @param randomChoices an array of random numbers in {@code [0, 1)}
	 * @param stateSpace an array representing the state space of the probability distribution
	 * @param pmf an array representing the probability mass function corresponding to elements in {@code stateSpace}.
	 * @return an integer array of choices corresponding to elements in {@code randomchoices}.
	 * @throws Exception thrown if elements of pmf do not sum up to 1.
	 */
	public static int[] discreteProbabilityChoice(double[] randomChoices, int[] stateSpace, double[] pmf) throws Exception
	{
		double[] cdf = new double[pmf.length];
		int[] binomialChoices = new int[randomChoices.length];
		double sum = 0.0;
		for (int i=0; i<pmf.length; i++)
		{
			sum += pmf[i];
			cdf[i] = sum;
		}
		if (!equalsWithinTolerances(1, cdf[pmf.length-1], 0.0001, 1))
		{
			if (!equalsWithinTolerances(1, cdf[pmf.length-1], 0.000001, 0.01))
				throw new Exception("'pmf' is not a probability mass function!");
			else
				System.out.println("WARNING: 'pmf' might not be a probability mass function!");
		}
		
		for (int i=0; i<randomChoices.length; i++)
			if (randomChoices[i]<=cdf[0])
				binomialChoices[i] = stateSpace[0];
			else
				for (int j=1; j<cdf.length; j++)
					if ((randomChoices[i]>cdf[j-1]) && (randomChoices[i]<=cdf[j]))
						binomialChoices[i] = stateSpace[j];
		return binomialChoices;
	}
	
	/**
	 * Test if two numbers are equal within an absolute or relative tolerance, whichever is larger.
	 * The relative tolerance is given by a percentage and calculated from the absolute maximum of the input numbers.
	 * Credit:
	 * <a href="http://www.java2s.com/example/java/java.lang/test-if-two-numbers-are-equal-within-an-absolute-or-relative-tolerance.html" target="_blank">.
	 *
	 * @param num1 first number to be compared for equality
	 * @param num2 second number to be compared for equality
	 * @param absTol absolute tolerance
	 * @param relTol relative tolerance.
	 * @return {@code true}, if {@code num1} and {@code num2} are equal within tolerances.
	 */
	public static boolean equalsWithinTolerances(Number num1, Number num2, Number absTol, Number relTol)
	{
		final double a = num1.doubleValue();
		final double b = num2.doubleValue();
		final double t = absTol.doubleValue();
		final double p = relTol.doubleValue();
		
		// relative tolerance
		double r = p * Math.max(Math.abs(a), Math.abs(b)) / 100.;
		// compare relative and absolute tolerance
		if (r>t)
			return Math.abs(a-b) <= r;
		return Math.abs(a-b) <= t;
	}
	
	/**
	 * Chooses elements from the state space of a distribution with probability mass function {@code pmf} based on
	 * random stream {@code randomchoices}.
	 * <br>
	 * Overloaded function: see {@link combinatorics#discreteProbabilityChoice(double[], int[], double[])}.
	 *
	 * @param randomChoices an {@link java.util.ArrayList} of random numbers in {@code [0, 1)}
	 * @param stateSpace an array representing the state space of the probability distribution
	 * @param pmf an array representing the probability mass function corresponding to elements in {@code stateSpace}
	 * @return an {@link java.util.ArrayList} of choices corresponding to elements in {@code randomchoices}.
	 * @throws Exception thrown if elements of pmf do not sum up to 1.
	 */
	public static List<Integer> discreteProbabilityChoice(List<Double> randomChoices,
	                                                           int[] stateSpace, double[] pmf) throws Exception
	{
		double[] cdf = new double[pmf.length];
		ArrayList<Integer> binomialChoices = new ArrayList<>(randomChoices.size());
		for (int i=0; i<randomChoices.size(); i++)
			binomialChoices.add(-1);
		double sum = 0.0;
		for (int i=0; i<pmf.length; i++)
		{
			sum += pmf[i];
			cdf[i] = sum;
		}
		if (!equalsWithinTolerances(1, cdf[pmf.length-1], 0.0001, 1))
		{
			if (!equalsWithinTolerances(1, cdf[pmf.length-1], 0.000001, 0.01))
				throw new Exception("'pmf' is not a probability mass function!");
			else
				System.out.println("WARNING: 'pmf' might not be a probability mass function!");
		}
		
		for (int i=0; i<randomChoices.size(); i++)
			if (randomChoices.get(i)<=cdf[0])
				binomialChoices.set(i, stateSpace[0]);
			else
				for (int j=1; j<cdf.length; j++)
					if ((randomChoices.get(i)>cdf[j-1]) && (randomChoices.get(i)<=cdf[j]))
						binomialChoices.set(i, stateSpace[j]);
		return binomialChoices;
	}
	
	/**
	 * Returns {@code n} elements randomly selected from list {@code a}.
	 *
	 * @param a a list to make selection from
	 * @param n number of elements in list {@code a} to be selected
	 * @param randGenerator an instance of {@link java.util.SplittableRandom}.
	 * @return a list of @code n} randomly selected elements from {@code a}.
	 */
	public static List<Integer> selectRandomElements(List<Integer> a, int n, SplittableRandom randGenerator)
	{
		List<Integer> acopy = new ArrayList<>(a);
		List<Integer> outputList = new ArrayList<>(n);
		for (int i=0; i<n; i++)
		{
			int randIndex = randGenerator.nextInt(acopy.size());
			outputList.add(acopy.get(randIndex));
			acopy.remove(randIndex);
		}
		return outputList;
	}
}
