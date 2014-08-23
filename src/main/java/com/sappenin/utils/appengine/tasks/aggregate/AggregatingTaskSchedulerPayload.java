package com.sappenin.utils.appengine.tasks.aggregate;

/**
 * An interface for defining payloads that the {@link AbstractAggregatingTaskScheduler} can operate with.
 */
public interface AggregatingTaskSchedulerPayload<T>
{
	/**
	 * @return An instance of <T> that represents the actual payload to be scheduled.
	 */
	T getSchedulablePayload();

	/**
	 * @return A {@link String} representing the name of the tasks that will be created.  This value should be unique
	 * for the payload above so that when it is used by the App Engine TaskQueue infrastructure,
	 * only one of these tasks
	 * will be schedulable at a time.
	 */
	String getTaskName();
}
