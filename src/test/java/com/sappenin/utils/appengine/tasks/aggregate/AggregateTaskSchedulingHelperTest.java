package com.sappenin.utils.appengine.tasks.aggregate;

import com.sappenin.utils.appengine.tasks.aggregate.AggregateTaskSchedulingHelper.Impl;
import org.junit.Before;
import org.junit.Test;

public class AggregateTaskSchedulingHelperTest
{

	private AggregateTaskSchedulingHelper helper;

	@Before
	public void before()
	{
		this.helper = new Impl();
	}

	@Test
	public void testGetNextScheduledRunDateTime() throws Exception
	{

	}

	@Test
	public void testComputeDateTimeStampForNextSchedulingPeriod() throws Exception
	{

	}
}