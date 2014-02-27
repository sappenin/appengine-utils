/**
 * Copyright (c) 2013 Oodlemud Inc.
 */
package com.sappenin.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * A Google-Web-ToolKit (GWT) compilable utility class for doing BigDecimal Comparison. This class is extended by
 * com.sappenin.util.BDUtils with functionality that is not compatible with GWT.
 * 
 * @author David Fuelling <sappenin@gmail.com>
 * @since 02/04/2010
 * @deprecated This will eventually move to sappenin-utils or customer-notifications.
 */
@Deprecated
public class BDUtils
{
	// Used for comparison purposes...
	public static final BigDecimal bdZero = manufactureBigDecimal("0.00");
	public static final BigDecimal bdNegativeOne = manufactureBigDecimal("-1.00");

	/**
	 * Determines whether or not bd1 equals bd2.
	 * 
	 * @param bd1
	 * @param bd2
	 * @return true if bd1 equals bd2, false otherwise.
	 */
	public static final boolean equals(BigDecimal bd1, BigDecimal bd2)
	{
		if (bd1 == null && bd2 != null)
		{
			return false;
		}
		else if (bd2 == null && bd1 != null)
		{
			return false;
		}

		int comparator = bd1.compareTo(bd2);
		if (comparator == 1)
			return true;
		else
			return false;
	}

	/**
	 * Determines whether or not bd1 isLessThan (<) bd2.
	 * 
	 * @param bd1
	 * @param bd2
	 * @return true if bd1 isLessThan (<) bd2, false otherwise.
	 */
	public static final boolean lt(BigDecimal bd1, BigDecimal bd2)
	{
		if (bd1 == null || bd2 == null)
			return false;

		int comparator = bd1.compareTo(bd2);
		if (comparator == -1)
			return true;
		else
			return false;
	}

	/**
	 * Determines whether or not bd1 isGreaterThan (>) bd2.
	 * 
	 * @param bd1
	 * @param bd2
	 * @return true if bd1 isGreaterThan (>) bd2, false otherwise.
	 */
	public static final boolean gt(BigDecimal bd1, BigDecimal bd2)
	{
		if (bd1 == null || bd2 == null)
			return false;

		int comparator = bd1.compareTo(bd2);
		if (comparator == 1)
			return true;
		else
			return false;
	}

	/**
	 * Determines whether or not bd1 isLessThanOrEqual (<=) to bd2.
	 * 
	 * @param bd1
	 * @param bd2
	 * @return true if bd1 isLessThanOrEqual (<=) bd2, false otherwise.
	 */
	public static final boolean lteq(BigDecimal bd1, BigDecimal bd2)
	{
		if (bd1 == null || bd2 == null)
			return false;

		int comparator = bd1.compareTo(bd2);
		if (comparator == -1 || comparator == 0)
			return true;
		else
			return false;
	}

	/**
	 * Determines whether or not bd1 isGreaterThanOrEqual (>=) to bd2.
	 * 
	 * @param bd1
	 * @param bd2
	 * @return true if bd1 isGreaterThanOrEqual (>=) bd2, false otherwise.
	 */
	public static final boolean gteq(BigDecimal bd1, BigDecimal bd2)
	{
		if (bd1 == null || bd2 == null)
			return false;

		int comparator = bd1.compareTo(bd2);
		if (comparator == 0 || comparator == 1)
			return true;
		else
			return false;
	}

	/**
	 * This function turns the BigDecimal into a negative number if it is positive, and vice-versa.
	 * 
	 * @param bdInput
	 * @return The inverted BigDecimal input
	 */
	public static final BigDecimal invert(BigDecimal bdInput)
	{
		if (bdInput == null)
		{
			// if bdInput is null, return null.
			return null;
		}
		else if (bdInput.equals(bdZero))
		{
			// If bdInput is zero, then return bdInput.
			return bdInput;
		}
		else
		{
			// If bdInput is negative, make it positive.
			// If bdInput is positive, make it negative.
			return bdInput.multiply(bdNegativeOne);
		}
	}

	/**
	 * Manufactures a BigDecimal with at 2 decimal places ('.00' as the default).
	 */
	public static BigDecimal manufactureBigDecimal(String sInput)
	{
		return manufactureBigDecimal(sInput, 2);
	}

	/**
	 * Manufactures a BigDecimal with at least 'scale' decimal places.
	 */
	public static BigDecimal manufactureBigDecimal(String sInput, int scale)
	{
		if (sInput == null || "".equals(sInput))
		{
			return BDUtils.bdZero;
		}

		sInput = sInput.replaceAll("[^-0-9\\.]", "");

		if (sInput == null || "".equals(sInput))
		{
			return BDUtils.bdZero;
		}

		BigDecimal bdTemp = BDUtils.bdZero;
		try
		{
			bdTemp = new BigDecimal(sInput);
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			// do nothing.
		}
		bdTemp = bdTemp.setScale(scale, BigDecimal.ROUND_HALF_UP);
		return bdTemp;
	}

	public static BigDecimal manufactureBigDecimal(int input)
	{
		BigDecimal bdTemp = BigDecimal.valueOf(input);
		// Add 2 decimal points positions.
		bdTemp = bdTemp.setScale(2);
		return bdTemp;
	}

	/**
	 * Formats a BigDecimal like a dollar-formatted String, but without the dollar-sign. Thus, a number will have two
	 * decimal place digits, and commas separating every 3 digits.
	 */
	public static final String toFormattedString(BigDecimal bdInput)
	{
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(bdInput.doubleValue());
	}

	// We should never create a BigDecimal from a double or a float, because of
	// the anomolies and unexpected behavior of Java floating point numbers. Use
	// the String contructor if decimal values other than zero need to be
	// specified, otherwise use the integer constructor.

}
