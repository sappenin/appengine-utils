/**
 * Copyright (c) 2013 Oodlemud Inc.
 */
package com.sappenin.utils;

/**
 * Utilities for Converting Exceptions into String for display purposes.
 * 
 * @author sappenin
 * @deprecated This will eventually move to sappenin-utils or customer-notifications.
 */
@Deprecated
public class StringUtils
{
	/**
	 * Transforms an Exception's StackTrace into a Printable format.
	 * 
	 * @param e
	 * @return
	 */
	public static StringBuilder getStackTrace(Exception e)
	{
		return StringUtils.getStackTrace(e, 0);
	}

	/**
	 * Transforms an Exception's StackTrace into a Printable format.
	 * 
	 * @param e
	 * @return
	 */
	public static StringBuilder getStackTrace(Throwable t)
	{
		return StringUtils.getStackTrace(t, 0);
	}

	/**
	 * Transforms an Exception's StackTrace into a Printable format.
	 * 
	 * @param e
	 * @return
	 */
	public static StringBuilder getStackTrace(Throwable t, int infiniteLoopProtector)
	{
		// Get the StackTrace as a StringBuilder.
		StringBuilder sb = new StringBuilder();

		if (t != null)
		{
			StackTraceElement[] stacks = t.getStackTrace();
			if (stacks != null)
			{
				StackTraceElement tempSTE = null;
				for (StackTraceElement stack : stacks)
				{
					tempSTE = stack;
					if (tempSTE != null)
					{
						sb.append(tempSTE.toString());
						sb.append("\n");
					}
				}
			}

			// Get StackTrace for all embedded Cause Exceptions/Errors.
			t = t.getCause();
			if (infiniteLoopProtector < 10)
			{
				sb.append(StringUtils.getStackTrace(t, infiniteLoopProtector++));
			}
		}

		return sb;
	}
}
