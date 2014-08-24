package com.sappenin.utils.appengine.tasks.aggregate;

import com.google.common.base.Preconditions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * A service used to determine when a task should be scheduled in the task queue.
 */
public interface AggregateTaskSchedulingHelper
{
	// The default number of milliseconds to wait until computing the next synthetic index for a given entity
	// (currently 1 hour in milliseconds)
	public static final int DEFAULT_DELTA_MILLIS = 1000 * 60 * 60;

	/**
	 * Computes and returns the future {@link DateTime} that a particular task should be run in the task queue.  This
	 * method is used in cases where there is no existing task in the task queue, and we want to govern the number of
	 * times that a particular aggregate task can run (e.g., run, at most, once per hour).
	 *
	 * @return the next scheduled run {@link DateTime}.
	 */
	DateTime getNextScheduledRunDateTime();

	/**
	 * Computes and returns a DateTime stamp that can be appended to a Task name in order to ensure that the
	 * aggregating
	 * task scheduler can function properly. <br/><br/> From the Appengine javadoc, it appears that a named task will
	 * hang around and "reserve" its name for up to 7 days after the task executes.  Because of this,
	 * the task name must
	 * include a DateTime stamp that uniquely identifies the task for the period that it is expected to be reserved
	 * for.
	 *
	 * @param nextRunDateTime The DateTime to use as the next-run DateTime when computing this unique timestamp.
	 *
	 * @return
	 * @see "https://developers.google.com/appengine/docs/java/taskqueue/"
	 */
	String computeDateTimeStampForNextSchedulingPeriod(final DateTime nextRunDateTime);

	/**
	 * A default implementation of {@link AggregateTaskSchedulingHelper}.
	 */
	public static class Impl implements AggregateTaskSchedulingHelper
	{
		@Override
		public DateTime getNextScheduledRunDateTime()
		{
			// Return a DateTime DEFAULT_DELTA_MILLIS (i.e., one hour) in the future,
			// which is when the next task should be eligible to run.
			DateTime dateTimeAtNextHour = DateTime.now(DateTimeZone.UTC).withMinuteOfHour(0).withSecondOfMinute(0)
					.withMillisOfSecond(0).plusHours(1);
			return dateTimeAtNextHour;
		}

		@Override
		public String computeDateTimeStampForNextSchedulingPeriod(final DateTime nextRunDateTime)
		{
			Preconditions.checkNotNull(nextRunDateTime);
			String dateTimeString= nextRunDateTime.toString("yyyy-MM-dd'T'HH:mm:ss");
			dateTimeString = dateTimeString.replace("-", "");
			dateTimeString = dateTimeString.replace("'", "");
			dateTimeString = dateTimeString.replace(":", "");
			dateTimeString = dateTimeString.replace("T", "");
			return dateTimeString;
		}
	}
}
