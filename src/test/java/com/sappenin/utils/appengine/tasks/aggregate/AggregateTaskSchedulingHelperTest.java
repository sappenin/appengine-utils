package com.sappenin.utils.appengine.tasks.aggregate;

import com.sappenin.utils.appengine.tasks.aggregate.AggregateTaskSchedulingHelper.Impl;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests for {@link AggregateTaskSchedulingHelper}.
 */
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
		DateTime expected = new DateTime(DateTime.now(DateTimeZone.UTC)).plusHours(1).withMinuteOfHour(0)
				.withSecondOfMinute(0).withMillisOfSecond(0);

		DateTime actual = helper.getNextScheduledRunDateTime();

		assertThat(actual, is(expected));
	}

	@Test(expected = NullPointerException.class)
	public void testComputeDateTimeStampForNextSchedulingPeriod_NullDate() throws Exception
	{
		helper.computeDateTimeStampForNextSchedulingPeriod(null);
	}

	@Test
	public void testComputeDateTimeStampForNextSchedulingPeriod() throws Exception
	{
		DateTime now = DateTime.now(DateTimeZone.UTC);
		String expected = now.toString("yyyy-MM-dd'T'HH:mm:ss").replace("-","").replace(":", "").replace("T","").replace(".","");

		String actual = helper.computeDateTimeStampForNextSchedulingPeriod(now);

		assertThat(actual, is(expected));
	}
}