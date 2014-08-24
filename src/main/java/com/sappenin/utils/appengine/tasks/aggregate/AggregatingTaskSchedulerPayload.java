package com.sappenin.utils.appengine.tasks.aggregate;

/**
 * An interface for defining payloads that the {@link AbstractAggregatingTaskScheduler} can operate with.
 */
public interface AggregatingTaskSchedulerPayload
{
	/**
	 * Computes and returns a {@link String} representing the name of the task that will be created.  This value should
	 * be unique for the payload returned from scheduledPayload (see above) so that when it is used by the App Engine
	 * TaskQueue infrastructure, only one of these tasks will be scheduled at a time.<br/><br/> Note that this task
	 * name
	 * must have the following characteristics:<br/> <ul> <li>Must be a combination of one or more digits, letters a–z,
	 * underscores, and/or dashes, satisfying the following regular expression: [0-9a-zA-Z\-\_]+</li> <li>May be up to
	 * 500 characters long.</li>
	 * <p/>
	 * </ul> Finally, from the appengine javadoc, it appears that a named task will hang around and "reserve" its name
	 * for up to 7 days after the task executes.  Because of this, the task name must include a DateTime stamp that
	 * uniquely identifies the task for the period that it is expected to be reserved for.
	 *
	 * @return
	 */
	String getAggregatedTaskName();
}
