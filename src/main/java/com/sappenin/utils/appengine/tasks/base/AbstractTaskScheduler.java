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
package com.sappenin.utils.appengine.tasks.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.sappenin.utils.appengine.tasks.TaskScheduler;
import com.sappenin.utils.json.JsonUtils;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Abstract implementation of {@link TaskScheduler} for assisting with the scheduling of tasks with the taskqueue
 * service in App Engine.
 *
 * @author David Fuelling
 */
@Getter
public abstract class AbstractTaskScheduler<T> implements TaskScheduler<T>
{
	private static final String APPLICATION_JSON = "application/json";

	private final JsonUtils jsonUtils;

	/**
	 * Required Args Constructor.
	 */
	public AbstractTaskScheduler(final JsonUtils jsonUtils)
	{
		Preconditions.checkNotNull(jsonUtils);
		this.jsonUtils = jsonUtils;
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
			TaskOptions taskOptions = this.buildTaskOptions(payload);

			// Kick off a Task to handle callbacks
			queue.add(taskOptions);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		this.getLogger().info("Task Scheduled (Namespace=\"" + NamespaceManager.get() + "\"): " + payload);
	}

	/**
	 * Helper method to build a {@link TaskOptions} from a {@code payload} of type <T>.
	 *
	 * @param payload An instance of type <T>.
	 *
	 * @return
	 */
	protected TaskOptions buildTaskOptions(final T payload) throws JsonProcessingException
	{
		// Enqueue this task
		TaskOptions taskOptions = TaskOptions.Builder.withDefaults();

		// Convert the Payload into JSON. We use JSON instead of a
		// DeferredTask because JSON is less brittle when
		// the payload class structure changes than a Serialized class.
		final String jsonPayload = this.getJsonUtils().toJson(payload);
		taskOptions = taskOptions.payload(jsonPayload);
		taskOptions = taskOptions.url(getProcessingQueueUrlPath());
		taskOptions = taskOptions.method(Method.POST);
		taskOptions = taskOptions.header("Accept", APPLICATION_JSON);
		taskOptions = taskOptions.header("Content-Type", APPLICATION_JSON);
		//taskOptions = taskOptions.header("Host", this.getHost());

		return taskOptions;
	}

	// /////////////////////
	// Protected Helpers
	// /////////////////////

	protected abstract Logger getLogger();

	/**
	 * @return the name of the Queue that this payload should be scheduled on.
	 */
	protected abstract String getProcessingQueueName();

	/**
	 * @return the url path that this application will process taskqueues on. For example,
	 * "/tasks/callbacks/processCallback".
	 */
	protected abstract String getProcessingQueueUrlPath();

	/**
	 * @return A {@link String} representing the host that a particular aggregate task should be scheduled onto.
	 */
	//protected abstract String getHost();

	/**
	 * Helper method to grab a Json Payload from the InputStream of an {@link HttpServletRequest}.  Not used in this
	 * class but used by sub-classes.
	 *
	 * @param httpServletRequest
	 *
	 * @return
	 */
	protected String getJsonPayloadFromRequest(final HttpServletRequest httpServletRequest) throws IOException
	{
		Preconditions.checkNotNull(httpServletRequest);
		return this.getJsonPayloadFromRequest(httpServletRequest.getInputStream());
	}

	/**
	 * Helper method to grab a Json Payload from an InputStream.  This is generally used in concert with an {@link
	 * HttpServletRequest}, but doesn't strictly need to be.   Not used in this class but used by sub-classes.
	 *
	 * @param inputStream
	 *
	 * @return
	 */
	protected String getJsonPayloadFromRequest(final InputStream inputStream) throws IOException
	{
		Preconditions.checkNotNull(inputStream);
		try (final InputStream stream = inputStream)
		{
			String jsonPayload = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
			getLogger().exiting(this.getClass().getName(), "getJsonPayloadFromRequest", jsonPayload);
			return jsonPayload;
		}
	}
}
