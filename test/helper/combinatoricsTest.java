package helper;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link combinatorics}.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: November 26, 2020.
 */
class combinatoricsTest
{
	
	/**
	 * Unite test for {@link combinatorics#nChoosek(int, int)}.
	 *
	 * @throws Exception thrown if {@link combinatorics#nChoosek(int, int)} throws an exception.
	 */
	@Test
	void nChoosek() throws Exception
	{
		assert combinatorics.nChoosek(4, 0)==1;
		assert combinatorics.nChoosek(45, 9)==886163135;
		assert combinatorics.nChoosek(100, 7)==16007560800L;
		
		Exception exception = assertThrows(Exception.class, () -> combinatorics.nChoosek(-1, 2));
		String expectedMessage = "Inputs should be non-negative integers!";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		exception = assertThrows(Exception.class, () -> combinatorics.nChoosek(2, -1));
		expectedMessage = "Inputs should be non-negative integers!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		exception = assertThrows(Exception.class, () -> combinatorics.nChoosek(-2, -1));
		expectedMessage = "Inputs should be non-negative integers!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		exception = assertThrows(Exception.class, () -> combinatorics.nChoosek(4, 10));
		expectedMessage = "'n<k' is not allowed!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
	
	/**
	 * Unit test for {@link combinatorics#discreteProbabilityChoice(double[], int[], double[])}.
	 */
	@Test
	void discreteProbabilityChoice() throws Exception
	{
		double[] randomChoices = {0.1, 0.9, 0.5, 0.05, 0.75, 0.7};
		int[] stateSpace = {1, 2, 3};
		double[] pmf = {0.3, 0.4, 0.3};
		
		int[] choices = combinatorics.discreteProbabilityChoice(randomChoices, stateSpace, pmf);
		assert choices[0]==1;
		assert choices[1]==3;
		assert choices[2]==2;
		assert choices[3]==1;
		assert choices[4]==3;
		assert choices[5]==2;
		
		pmf[1] = 0.3;
		Exception exception = assertThrows(Exception.class,
									() -> combinatorics.discreteProbabilityChoice(randomChoices, stateSpace, pmf));
		String expectedMessage = "'pmf' is not a probability mass function!";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		pmf[2] = 0.5;
		exception = assertThrows(Exception.class,
				() -> combinatorics.discreteProbabilityChoice(randomChoices, stateSpace, pmf));
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
	
	/**
	 * Unit test for {@link combinatorics#discreteProbabilityChoice(List, int[], double[])}.
	 */
	@Test
	void testDiscreteProbabilityChoice() throws Exception
	{
		ArrayList<Double> randomChoices = new ArrayList<>(6);
		randomChoices.add(0.1);
		randomChoices.add(0.9);
		randomChoices.add(0.5);
		randomChoices.add(0.05);
		randomChoices.add(0.75);
		randomChoices.add(0.7);
		int[] stateSpace = {1, 2, 3};
		double[] pmf = {0.3, 0.4, 0.3};
		
		List<Integer> choices = combinatorics.discreteProbabilityChoice(randomChoices, stateSpace, pmf);
		assert choices.get(0)==1;
		assert choices.get(1)==3;
		assert choices.get(2)==2;
		assert choices.get(3)==1;
		assert choices.get(4)==3;
		assert choices.get(5)==2;
		
		pmf[1] = 0.3;
		Exception exception = assertThrows(Exception.class,
				() -> combinatorics.discreteProbabilityChoice(randomChoices, stateSpace, pmf));
		String expectedMessage = "'pmf' is not a probability mass function!";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		pmf[2] = 0.5;
		exception = assertThrows(Exception.class,
				() -> combinatorics.discreteProbabilityChoice(randomChoices, stateSpace, pmf));
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
}