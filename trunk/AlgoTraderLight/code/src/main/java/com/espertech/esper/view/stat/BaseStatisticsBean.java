// add prodX functionality
/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved. *
 * http://esper.codehaus.org *
 * http://www.espertech.com *
 * ----------------------------------------------------------------------------
 * ------ *
 * The software in this package is published under the terms of the GPL license
 * *
 * a copy of which has been included with this distribution in the license.txt
 * file. *
 **************************************************************************************/
package com.espertech.esper.view.stat;

import java.io.Serializable;

import com.espertech.esper.client.EPException;

/**
 * Bean for performing statistical calculations. The bean keeps sums of X and Y
 * datapoints and sums on squares that can be reused by subclasses. The bean
 * calculates standard deviation (sample and population), variance, average and
 * sum.
 */
public class BaseStatisticsBean implements Cloneable, Serializable {
	private double	          sumX;
	private double	          sumXSq;
	private double	          prodX	             = 1;
	private double	          sumY;
	private double	          sumYSq;
	private double	          sumXY;
	private long	          dataPoints;
	private static final long	serialVersionUID	= 7985193760056277184L;
	
	private void initialize() {
		sumX = 0;
		sumXSq = 0;
		prodX = 1;
		sumY = 0;
		sumYSq = 0;
		sumXY = 0;
		dataPoints = 0;
	}
	
	/**
	 * Add a data point for the X data set only.
	 * 
	 * @param x
	 *            is the X data point to add.
	 */
	public final void addPoint(final double x) {
		dataPoints++;
		sumX += x;
		sumXSq += x * x;
		prodX *= 1 + x;
	}
	
	/**
	 * Add a data point.
	 * 
	 * @param x
	 *            is the X data point to add.
	 * @param y
	 *            is the Y data point to add.
	 */
	public final void addPoint(final double x, final double y) {
		dataPoints++;
		sumX += x;
		sumXSq += x * x;
		prodX *= 1 + x;
		sumY += y;
		sumYSq += y * y;
		sumXY += x * y;
	}
	
	/**
	 * Remove a X data point only.
	 * 
	 * @param x
	 *            is the X data point to remove.
	 */
	public final void removePoint(final double x) {
		dataPoints--;
		if (dataPoints <= 0) {
			initialize();
		} else {
			sumX -= x;
			sumXSq -= x * x;
			prodX /= 1 + x;
		}
	}
	
	/**
	 * Remove a data point.
	 * 
	 * @param x
	 *            is the X data point to remove.
	 * @param y
	 *            is the Y data point to remove.
	 */
	public final void removePoint(final double x, final double y) {
		dataPoints--;
		if (dataPoints <= 0) {
			initialize();
		} else {
			sumX -= x;
			sumXSq -= x * x;
			prodX /= 1 + x;
			sumY -= y;
			sumYSq -= y * y;
			sumXY -= x * y;
		}
	}
	
	/**
	 * Calculates standard deviation for X based on the entire population given
	 * as arguments. Equivalent to Microsoft Excel formula STDEVPA.
	 * 
	 * @return standard deviation assuming population for X
	 */
	public final double getXStandardDeviationPop() {
		if (dataPoints == 0) { return Double.NaN; }
		
		final double temp = (sumXSq - sumX * sumX / dataPoints) / dataPoints;
		return Math.sqrt(temp);
	}
	
	/**
	 * Calculates standard deviation for Y based on the entire population given
	 * as arguments. Equivalent to Microsoft Excel formula STDEVPA.
	 * 
	 * @return standard deviation assuming population for Y
	 */
	public final double getYStandardDeviationPop() {
		if (dataPoints == 0) { return Double.NaN; }
		
		final double temp = (sumYSq - sumY * sumY / dataPoints) / dataPoints;
		return Math.sqrt(temp);
	}
	
	/**
	 * Calculates standard deviation for X based on the sample data points
	 * supplied. Equivalent to Microsoft Excel formula STDEV.
	 * 
	 * @return standard deviation assuming sample for X
	 */
	public final double getXStandardDeviationSample() {
		if (dataPoints < 2) { return Double.NaN; }
		
		final double variance = getXVariance();
		return Math.sqrt(variance);
	}
	
	/**
	 * Calculates standard deviation for Y based on the sample data points
	 * supplied. Equivalent to Microsoft Excel formula STDEV.
	 * 
	 * @return standard deviation assuming sample for Y
	 */
	public final double getYStandardDeviationSample() {
		if (dataPoints < 2) { return Double.NaN; }
		
		final double variance = getYVariance();
		return Math.sqrt(variance);
	}
	
	/**
	 * Calculates standard deviation for X based on the sample data points
	 * supplied. Equivalent to Microsoft Excel formula STDEV.
	 * 
	 * @return variance as the square of the sample standard deviation for X
	 */
	public final double getXVariance() {
		if (dataPoints < 2) { return Double.NaN; }
		
		return (sumXSq - sumX * sumX / dataPoints) / (dataPoints - 1);
	}
	
	/**
	 * Calculates standard deviation for Y based on the sample data points
	 * supplied. Equivalent to Microsoft Excel formula STDEV.
	 * 
	 * @return variance as the square of the sample standard deviation for Y
	 */
	public final double getYVariance() {
		if (dataPoints < 2) { return Double.NaN; }
		
		return (sumYSq - sumY * sumY / dataPoints) / (dataPoints - 1);
	}
	
	/**
	 * Returns the number of data points.
	 * 
	 * @return number of data points
	 */
	public final long getN() {
		return dataPoints;
	}
	
	/**
	 * Returns the sum of all X data points.
	 * 
	 * @return sum of X data points
	 */
	public final double getXSum() {
		return sumX;
	}
	
	/**
	 * Returns the sum of all Y data points.
	 * 
	 * @return sum of Y data points
	 */
	public final double getYSum() {
		return sumY;
	}
	
	/**
	 * Returns the average of all X data points.
	 * 
	 * @return average of X data points
	 */
	public final double getXAverage() {
		if (dataPoints == 0) { return Double.NaN; }
		
		return sumX / dataPoints;
	}
	
	/**
	 * Returns the average of all Y data points.
	 * 
	 * @return average of Y data points
	 */
	public final double getYAverage() {
		if (dataPoints == 0) { return Double.NaN; }
		
		return sumY / dataPoints;
	}
	
	/**
	 * For use by subclasses, returns sum (X * X).
	 * 
	 * @return sum of X squared
	 */
	public final double getSumXSq() {
		return sumXSq;
	}
	
	/**
	 * For use by subclasses, returns sum (Y * Y).
	 * 
	 * @return sum of Y squared
	 */
	public final double getSumYSq() {
		return sumYSq;
	}
	
	/**
	 * For use by subclasses, returns sum (X * Y).
	 * 
	 * @return sum of X times Y
	 */
	public final double getSumXY() {
		return sumXY;
	}
	
	public final double getProdX() {
		return Math.pow(prodX, (1.0 / dataPoints)) - 1.0;
	}
	
	@Override
	public final Object clone() {
		try {
			return super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new EPException(e);
		}
	}
	
	@Override
	public final String toString() {
		return "datapoints=" + dataPoints + "  sumX=" + sumX + "  sumXSq=" +
		        sumXSq + "  sumY=" + sumY + "  sumYSq=" + sumYSq + "  sumXY=" +
		        sumXY;
	}
	
	/**
	 * Sets the sum X.
	 * 
	 * @param sumX
	 *            to set
	 */
	public void setSumX(final double sumX) {
		this.sumX = sumX;
	}
	
	/**
	 * Sets the sum X square.
	 * 
	 * @param sumXSq
	 *            to set
	 */
	public void setSumXSq(final double sumXSq) {
		this.sumXSq = sumXSq;
	}
	
	public void setProdX(final double prodX) {
		this.prodX = prodX;
	}
	
	/**
	 * Sets the sum Y.
	 * 
	 * @param sumY
	 *            to set
	 */
	public void setSumY(final double sumY) {
		this.sumY = sumY;
	}
	
	/**
	 * Sets the sum Y square.
	 * 
	 * @param sumYSq
	 *            to set
	 */
	public void setSumYSq(final double sumYSq) {
		this.sumYSq = sumYSq;
	}
	
	/**
	 * Sets the sum of x times y.
	 * 
	 * @param sumXY
	 *            sum of x times y.
	 */
	public void setSumXY(final double sumXY) {
		this.sumXY = sumXY;
	}
	
	/**
	 * Sets the number of datapoints
	 * 
	 * @param dataPoints
	 *            to set
	 */
	public void setDataPoints(final long dataPoints) {
		this.dataPoints = dataPoints;
	}
	
	/**
	 * Returns sum of x.
	 * 
	 * @return sum of x
	 */
	public double getSumX() {
		return sumX;
	}
	
	/**
	 * Returns sum of y.
	 * 
	 * @return sum of y
	 */
	public double getSumY() {
		return sumY;
	}
	
	/**
	 * Returns the number of datapoints.
	 * 
	 * @return datapoints
	 */
	public long getDataPoints() {
		return dataPoints;
	}
	
	/**
	 * Returns the Y intercept.
	 * 
	 * @return Y intercept
	 */
	public double getYIntercept() {
		final double slope = getSlope();
		
		if (Double.isNaN(slope)) { return Double.NaN; }
		
		return getYSum() / getN() - getSlope() * getXSum() / getN();
	}
	
	/**
	 * Returns the slope.
	 * 
	 * @return regression slope
	 */
	public double getSlope() {
		if (getN() == 0) { return Double.NaN; }
		
		final double ssx = getSumXSq() - getXSum() * getXSum() / getN();
		
		if (ssx == 0) { return Double.NaN; }
		
		final double sp = getSumXY() - getXSum() * getYSum() / getN();
		
		return sp / ssx;
	}
	
	/**
	 * Return the correlation value for the two data series (Microsoft Excel
	 * function CORREL).
	 * 
	 * @return correlation value
	 */
	public final double getCorrelation() {
		if (getN() == 0) { return Double.NaN; }
		
		final double dx = getSumXSq() - getXSum() * getXSum() / getN();
		final double dy = getSumYSq() - getYSum() * getYSum() / getN();
		
		if (dx == 0 || dy == 0) { return Double.NaN; }
		
		final double sp = getSumXY() - getXSum() * getYSum() / getN();
		return sp / Math.sqrt(dx * dy);
	}
}
