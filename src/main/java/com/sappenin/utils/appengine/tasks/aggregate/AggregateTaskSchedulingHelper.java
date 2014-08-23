package com.sappenin.utils.appengine.tasks.aggregate;

import org.joda.time.DateTime;

/**
 * A service used to determine when a task should be scheduled in the task queue.
 */
public interface AggregateTaskSchedulingHelper
{
	/**
	 * @return The future {@link DateTime} that a particular task should be run in the task queue.  Dates in the past
	 * will be treated as if they were actually the "present" time, meaning tasks will run as soon as the TaskQueue
	 * infrastructure can process them.
	 */
	DateTime getScheduledRunDateTime();

}
