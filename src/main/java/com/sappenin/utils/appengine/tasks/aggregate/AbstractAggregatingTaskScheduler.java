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

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.common.base.Preconditions;
import com.sappenin.utils.appengine.tasks.TaskScheduler;
import com.sappenin.utils.appengine.tasks.base.AbstractTaskScheduler;
import com.sappenin.utils.json.JsonUtils;
import lombok.Getter;

/**
 * Abstract implementation of {@link TaskScheduler} that uses named-tasks to allow only a single task with a given name
 * to exist in the task queue at any given time.  The scheduler of this class operates as follows: <br/><br/> If a task
 * exists in the TaskQueue with a given name, then adding another task will fail silently. If no task exists in the
 * TaskQueue, then the given task will be scheduled at some point in the future, based upon the
 *
 * @author David Fuelling
 */
@Getter
public abstract class AbstractAggregatingTaskScheduler<T> extends AbstractTaskScheduler<T> implements TaskScheduler<T>
{

	/**
	 * Required Args Constructor.
	 */
	public AbstractAggregatingTaskScheduler(final JsonUtils jsonUtils)
	{
		super(jsonUtils);
	}

	@Override
	public void schedule(T payload)
	{

		Preconditions.checkNotNull(payload);
		this.getLogger().info("Scheduling Task (Namespace=\"" + NamespaceManager.get() + "\"): " + payload);

		try
		{
			final Queue queue = QueueFactory.getQueue(getProcessingQueueName());

			// Enqueue this task
			TaskOptions taskOptions = TaskOptions.Builder.withDefaults();

			// Convert the Payload into JSON. We use JSON instead of a
			// DeferredTask because JSON is less brittle when
			// the payload class structure changes than a Serialized class.
			final String jsonPayload = this.getJsonUtils().toJSON(payload);
			taskOptions = taskOptions.payload(jsonPayload);
			taskOptions = taskOptions.url(getProcessingQueueUrlPath());
			taskOptions = taskOptions.method(Method.POST);
			taskOptions = taskOptions.header("Accept", "application/json");
			taskOptions = taskOptions.header("Content-Type", "application/json");
			// taskOptions = taskOptions.header("X-Appengine-Current-Namespace",
			// NamespaceManager.get());

			// Kick off a Task to handle callbacks
			queue.add(taskOptions);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		this.getLogger().info("Task Scheduled (Namespace=\"" + NamespaceManager.get() + "\"): " + payload);

	}

	// /////////////////////
	// Protected Helpers
	// /////////////////////

	protected abstract String getTaskName();

}
