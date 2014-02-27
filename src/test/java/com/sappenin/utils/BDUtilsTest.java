package com.sappenin.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.sappenin.utils.BDUtils;

/**
 * Tests the BDUtils class for operational correctness.
 * 
 * @author David Fuelling
 * 
 */
@SuppressWarnings("deprecation")
public class BDUtilsTest
{
	BigDecimal bd1 = null;
	BigDecimal bd2 = null;

	@Before
	public void setUp() throws Exception
	{
		bd1 = new BigDecimal(5);
		bd2 = new BigDecimal(15);
	}

	@Test
	public void testLt()
	{
		assertTrue(BDUtils.lt(bd1, bd2));
		assertFalse(BDUtils.lt(bd2, bd1));
		assertFalse(BDUtils.lt(bd1, bd1));
		assertFalse(BDUtils.lt(bd2, bd2));
	}

	@Test
	public void testGt()
	{
		assertTrue(BDUtils.gt(bd2, bd1));
		assertFalse(BDUtils.gt(bd1, bd2));
		assertFalse(BDUtils.gt(bd1, bd1));
		assertFalse(BDUtils.gt(bd2, bd2));
	}

	@Test
	public void testLteq()
	{
		assertTrue(BDUtils.lteq(bd1, bd2));
		assertFalse(BDUtils.lteq(bd2, bd1));
		assertTrue(BDUtils.lteq(bd1, bd1));
		assertTrue(BDUtils.lteq(bd2, bd2));
	}

	@Test
	public void testGteq()
	{
		assertFalse(BDUtils.gteq(bd1, bd2));
		assertTrue(BDUtils.gteq(bd2, bd1));
		assertTrue(BDUtils.gteq(bd1, bd1));
		assertTrue(BDUtils.gteq(bd2, bd2));
	}

	@Test
	public void testInvert()
	{
		BigDecimal tempBD1 = BDUtils.invert(bd1);
		assertTrue(BDUtils.lt(tempBD1, BDUtils.bdZero));

		tempBD1 = BDUtils.invert(tempBD1);
		assertTrue(BDUtils.gt(tempBD1, BDUtils.bdZero));

		BigDecimal tempBD2 = BDUtils.invert(bd2);
		assertTrue(BDUtils.lt(tempBD2, BDUtils.bdZero));

		tempBD2 = BDUtils.invert(tempBD2);
		assertTrue(BDUtils.gt(tempBD2, BDUtils.bdZero));

		// Invert both, and ensure that they're both < 0 and that bd2 < bd1.
		tempBD1 = BDUtils.invert(bd1);
		tempBD2 = BDUtils.invert(bd2);

		assertTrue(BDUtils.lt(tempBD1, BDUtils.bdZero));
		assertTrue(BDUtils.lt(tempBD2, BDUtils.bdZero));

		assertTrue(BDUtils.lt(tempBD2, tempBD1));
		assertFalse(BDUtils.lt(tempBD1, tempBD2));

		assertFalse(BDUtils.gt(tempBD2, tempBD1));
		assertTrue(BDUtils.gt(tempBD1, tempBD2));
	}

	@Test
	public void testBDCreation()
	{
		// int constructor parallel
		bd1 = BigDecimal.valueOf(5);
		bd1 = bd1.setScale(2);
		bd2 = new BigDecimal("5.00");
		bd2 = bd1.setScale(2);
		assertTrue(bd1.equals(bd2));

		bd1 = BigDecimal.valueOf(15);
		bd1 = bd1.setScale(2);
		bd2 = new BigDecimal("15.00");
		bd2 = bd1.setScale(2);
		assertTrue(bd1.equals(bd2));

		bd1 = BigDecimal.valueOf(35423);
		bd1 = bd1.setScale(2);
		bd2 = new BigDecimal("35423.00");
		bd2 = bd1.setScale(2);
		assertTrue(bd1.equals(bd2));

		bd1 = BigDecimal.valueOf(2000000000);
		bd1 = bd1.setScale(2);
		bd2 = new BigDecimal("2000000000.00");
		bd2 = bd1.setScale(2);
		assertTrue(bd1.equals(bd2));

		// int constructor parallel
		bd1 = BigDecimal.valueOf(5);
		bd1 = bd1.setScale(2);
		bd2 = new BigDecimal("5");
		bd2 = bd1.setScale(2);
		assertTrue(bd1.equals(bd2));

		bd1 = BigDecimal.valueOf(15);
		bd1 = bd1.setScale(2);
		bd2 = new BigDecimal("15");
		bd2 = bd1.setScale(2);
		assertTrue(bd1.equals(bd2));

		bd1 = BigDecimal.valueOf(35423);
		bd1 = bd1.setScale(2);
		bd2 = new BigDecimal("35423");
		bd2 = bd1.setScale(2);
		assertTrue(bd1.equals(bd2));

		bd1 = BigDecimal.valueOf(2000000000);
		bd1 = bd1.setScale(2);
		bd2 = new BigDecimal("2000000000");
		bd2 = bd1.setScale(2);
		assertTrue(bd1.equals(bd2));

	}

	@Test
	public void testBDStringFormatting()
	{
		// int constructor parallel
		bd1 = BigDecimal.valueOf(5);
		assertTrue(BDUtils.toFormattedString(bd1).equals("5.00"));

		bd1 = new BigDecimal("5.00");
		assertTrue(BDUtils.toFormattedString(bd1).equals("5.00"));

		bd1 = BigDecimal.valueOf(15);
		assertTrue(BDUtils.toFormattedString(bd1).equals("15.00"));

		bd1 = new BigDecimal("15.00");
		assertTrue(BDUtils.toFormattedString(bd1).equals("15.00"));

		bd1 = BigDecimal.valueOf(35423);
		assertTrue(BDUtils.toFormattedString(bd1).equals("35,423.00"));

		bd1 = new BigDecimal("35423.00");
		assertTrue(BDUtils.toFormattedString(bd1).equals("35,423.00"));

		bd1 = BigDecimal.valueOf(2000000000);
		assertTrue(BDUtils.toFormattedString(bd1).equals("2,000,000,000.00"));

		bd1 = new BigDecimal("2000000000.00");
		assertTrue(BDUtils.toFormattedString(bd1).equals("2,000,000,000.00"));

		// False Assertions
		bd1 = BigDecimal.valueOf(5);
		assertFalse(BDUtils.toFormattedString(bd1).equals("5"));

		bd1 = new BigDecimal("5.00");
		assertFalse(BDUtils.toFormattedString(bd1).equals("5"));

		bd1 = BigDecimal.valueOf(15);
		assertFalse(BDUtils.toFormattedString(bd1).equals("15"));

		bd1 = new BigDecimal("15.00");
		assertFalse(BDUtils.toFormattedString(bd1).equals("15"));

		bd1 = BigDecimal.valueOf(35423);
		assertFalse(BDUtils.toFormattedString(bd1).equals("35,423"));

		bd1 = new BigDecimal("35423.00");
		assertFalse(BDUtils.toFormattedString(bd1).equals("35,423"));

		bd1 = BigDecimal.valueOf(2000000000);
		assertFalse(BDUtils.toFormattedString(bd1).equals("2,000,000,000"));

		bd1 = new BigDecimal("2000000000.00");
		assertFalse(BDUtils.toFormattedString(bd1).equals("2,000,000,000"));
	}
}
