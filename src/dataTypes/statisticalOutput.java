package dataTypes;

import java.util.Objects;

/**
 * Represents output (mean, CI width, etc.) of a statistical analysis.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: November 23, 2020.
 */
public class statisticalOutput
{
	/**
	 * Mean (average).
	 */
	double mean;
	
	/**
	 * Standard deviation.
	 */
	double stDev;
	
	/**
	 * Alpha value used to generate {@code CIWidth}.
	 */
	double alpha;
	
	/**
	 * Statical test (z-test, t-test, etc.) used to generate confidence intervals.
	 */
	String nameOfStatisticalTest;
	
	/**
	 * Width of confidence interval.
	 */
	double CIWidth;
	
	/**
	 * Sample size.
	 */
	int sampleSize;
	
	/**
	 * Batch size.
	 */
	int batchSize;
	
	/**
	 * Constructor.
	 *
	 * @param mean mean (average)
	 * @param stDev standard deviation
	 * @param alpha alpha value used to generate {@code CIWidth}
	 * @param nameOfStatisticalTest statistial test used to generate CIs
	 * @param CIWidth width of confidence interval
	 * @param sampleSize sample size
	 * @param batchSize batch size.
	 */
	public statisticalOutput(double mean, double stDev, double alpha, String nameOfStatisticalTest, double CIWidth,
	                         int sampleSize, int batchSize)
	{
		this.mean = mean;
		this.stDev = stDev;
		this.alpha = alpha;
		this.nameOfStatisticalTest = nameOfStatisticalTest;
		this.CIWidth = CIWidth;
		this.sampleSize = sampleSize;
		this.batchSize = batchSize;
	}
	
	/**
	 * Copy constructor.
	 *
	 * @param output an instance of {@link statisticalOutput}.
	 */
	public statisticalOutput(statisticalOutput output)
	{
		this.mean = output.mean;
		this.stDev = output.stDev;
		this.alpha = output.alpha;
		this.nameOfStatisticalTest = output.nameOfStatisticalTest;
		this.CIWidth = output.CIWidth;
		this.sampleSize = output.sampleSize;
		this.batchSize = output.batchSize;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link statisticalOutput#mean}.
	 */
	public double getMean()
	{
		return mean;
	}
	
	/**
	 * Setter.
	 *
	 * @param mean mean (average).
	 */
	public void setMean(double mean)
	{
		this.mean = mean;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link statisticalOutput#stDev}.
	 */
	public double getStDev()
	{
		return stDev;
	}
	
	/**
	 * Setter.
	 *
	 * @param stDev standard deviation.
	 */
	public void setStDev(double stDev)
	{
		this.stDev = stDev;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link statisticalOutput#alpha}.
	 */
	public double getAlpha()
	{
		return alpha;
	}
	
	/**
	 * Setter.
	 *
	 * @param alpha alpha value used to generate {@code CIwidth}.
	 */
	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link statisticalOutput#nameOfStatisticalTest}.
	 */
	public String getNameOfStatisticalTest()
	{
		return nameOfStatisticalTest;
	}
	
	/**
	 * Setter.
	 *
	 * @param nameOfStatisticalTest statistical test used to generate CIs.
	 */
	public void setNameOfStatisticalTest(String nameOfStatisticalTest)
	{
		this.nameOfStatisticalTest = nameOfStatisticalTest;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link statisticalOutput#CIWidth}.
	 */
	public double getCIWidth()
	{
		return CIWidth;
	}
	
	/**
	 * Setter.
	 *
	 * @param CIWidth width of confidence interval.
	 */
	public void setCIWidth(double CIWidth)
	{
		this.CIWidth = CIWidth;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link statisticalOutput#sampleSize}.
	 */
	public int getSampleSize()
	{
		return sampleSize;
	}
	
	/**
	 * Setter.
	 *
	 * @param sampleSize sample size.
	 */
	public void setSampleSize(int sampleSize)
	{
		this.sampleSize = sampleSize;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link statisticalOutput#batchSize}.
	 */
	public int getBatchSize()
	{
		return batchSize;
	}
	
	/**
	 * Setter.
	 *
	 * @param batchSize batch size.
	 */
	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}
	
	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString()
	{
		return "Statistical output: "
				+"Batch size = "+this.batchSize +"; "
				+"sample size = "+this.sampleSize+"; "
				+"mean = "+this.mean+"; "
				+"std. dev. = "+this.stDev+"; "
				+"CI width (alpha = "+this.alpha+"; "+this.nameOfStatisticalTest+" ) = "+this.CIWidth+".";
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
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		statisticalOutput output = (statisticalOutput) o;
		return Double.compare(output.mean, mean) == 0 &&
				Double.compare(output.stDev, stDev) == 0 &&
				Double.compare(output.alpha, alpha) == 0 &&
				Double.compare(output.CIWidth, CIWidth) == 0 &&
				sampleSize == output.sampleSize &&
				batchSize == output.batchSize &&
				Objects.equals(nameOfStatisticalTest, output.nameOfStatisticalTest);
	}
	
	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode()
	{
		return Objects.hash(mean, stDev, alpha, nameOfStatisticalTest, CIWidth, sampleSize, batchSize);
	}
}
