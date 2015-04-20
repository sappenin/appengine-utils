/**
 * Copyright (C) 2014 Sappenin Inc. (developers@sappenin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sappenin.utils.appengine.tasks.aggregate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Optional;
import com.sappenin.utils.appengine.tasks.TaskScheduler;
import com.sappenin.utils.appengine.tasks.base.AbstractTaskScheduler;
import com.sappenin.utils.json.JsonUtils;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.logging.Level;

/**
 * Abstract implementation of {@link TaskScheduler} that uses named-tasks to allow only a single task with a given name
 * to exist in the task queue at any given time.  The scheduler of this class operates as follows: <br/><br/> If a task
 * exists in the TaskQueue with a given name, then adding another task will fail silently. If no task exists in the
 * TaskQueue, then the given task will be scheduled at some point in the future, based upon the
 *
 * @author David Fuelling
 */
@Getter
public abstract class AbstractAggregatingTaskScheduler<T extends AggregatingTaskSchedulerPayload>
		extends AbstractTaskScheduler<T> implements TaskScheduler<T>
{
	/**
	 * Required Args Constructor.
	 */
	public AbstractAggregatingTaskScheduler(final JsonUtils jsonUtils)
	{
		super(jsonUtils);
	}

	@Override
	protected TaskOptions buildTaskOptions(final T aggregatingTaskPayload, final Optional<String> optTaskName)
			throws JsonProcessingException
	{
		TaskOptions taskOptions = super.buildTaskOptions(aggregatingTaskPayload, optTaskName);

		DateTime nextRunDateTime = aggregatingTaskPayload.getEtaScheduledDateTime();
		taskOptions.etaMillis(nextRunDateTime.getMillis());

		String taskName = aggregatingTaskPayload.getAggregatedTaskName();
		taskOptions.taskName(taskName);

		return taskOptions;
	}

	@Override
	public TaskHandle schedule(final T payload)
	{
		try
		{
			super.schedule(payload);
		}
		catch (TaskAlreadyExistsException e)
		{
			// Do nothing - eat this exception!  If the task already exists, it simply means we're aggregating
			// properly.
			getLogger().log(Level.WARNING,
					"Tried to schedule AggregateNotification Task with name: " + payload == null ?
							"unkown" :
							payload.getAggregatedTaskName(), e);

		}
		catch (RuntimeException re)
		{
			// Usually, the TaskAlreadyExistsException is inside of a RuntimeException
			if (TaskAlreadyExistsException.class.isAssignableFrom(re.getCause().getClass()))
			{
				// Do nothing - eat this exception!  If the task already exists, it simply means we're aggregating
				// properly.
				getLogger().log(Level.WARNING,
						"Tried to schedule AggregateNotification Task with name: " + payload == null ?
								"unkown" :
								payload.getAggregatedTaskName(), re);
			}
			else
			{
				throw re;
			}
		}
		return null;
	}

	/**
	 * Protected Getter for access by implementing classes.
	 *
	 * @return
	 */
	@Override
	protected JsonUtils getJsonUtils()
	{
		return super.getJsonUtils();
	}

}
