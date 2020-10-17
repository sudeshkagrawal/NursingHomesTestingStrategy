package helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class combinatoricsTest
{
	
	@Test
	void nChoosek() throws Exception
	{
		assert combinatorics.nChoosek(4, 0)==1;
		assert combinatorics.nChoosek(45, 9)==886163135;
		
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
	
	@Test
	void discreteProbabilityChoice()
	{
	}
	
	@Test
	void testDiscreteProbabilityChoice()
	{
	}
}