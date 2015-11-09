/**
 * Copyright (C) 2014-2015 Sappenin Inc. (developers@sappenin.com)
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

import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.common.base.Preconditions;
import com.sappenin.utils.appengine.tasks.TaskHandler;
import com.sappenin.utils.appengine.tasks.TaskScheduler;
import com.sappenin.utils.json.JsonUtils;
import com.sappenin.utils.json.JsonUtilsClassTypeMapper;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Abstract implementation of {@link TaskHandler} for assisting with the handling of tasks using the taskqueue service
 * in Google AppEngine.
 *
 * @author David Fuelling
 */
@Getter
public abstract class AbstractTaskScheduleHandler<P> implements TaskHandler, TaskScheduler<P>
{
	private final AbstractTaskHandler<P> abstractTaskHandler;

	private final AbstractTaskScheduler<P> abstractTaskScheduler;

	/**
	 * Required Args Constructor.
	 *
	 * @param jsonUtils                An instance of {@link JsonUtils} for deserializing JSON payloads from the
	 *                                 TaskQueue.
	 * @param jsonUtilsClassTypeMapper An instance of {@link JsonUtilsClassTypeMapper}.
	 */
	public AbstractTaskScheduleHandler(final JsonUtils jsonUtils,
			final JsonUtilsClassTypeMapper jsonUtilsClassTypeMapper)
	{
		Preconditions.checkNotNull(jsonUtils);

		// Delegate these methods to the potential overrides in AbstractTaskScheduleHandler so that we can re-use
		// AbstractTaskHandler.
		this.abstractTaskHandler = new AbstractTaskHandler<P>(jsonUtils, jsonUtilsClassTypeMapper)
		{
			@Override
			protected void handleHelper(final P payload)
			{
				AbstractTaskScheduleHandler.this.handleHelper(payload);
			}

			@Override
			protected void handleHelper(final P payload, final HttpServletRequest httpServletRequest,
					final HttpServletResponse httpServletResponse)
			{
				AbstractTaskScheduleHandler.this.handleHelper(payload, httpServletRequest, httpServletResponse);
			}

			@Override
			protected Logger getLogger()
			{
				return AbstractTaskScheduleHandler.this.getLogger();
			}

		};

		// Delegate these methods to the potential overrides in AbstractTaskScheduleHandler so that we can re-use
		// AbstractTaskScheduler.
		this.abstractTaskScheduler = new AbstractTaskScheduler<P>(jsonUtils)
		{
			@Override
			protected Logger getLogger()
			{
				return AbstractTaskScheduleHandler.this.getLogger();
			}

			@Override
			protected String getProcessingQueueName()
			{
				return AbstractTaskScheduleHandler.this.getProcessingQueueName();
			}

			@Override
			protected String getProcessingQueueUrlPath()
			{
				return AbstractTaskScheduleHandler.this.getProcessingQueueUrlPath();
			}
		};

	}

	//////////////////////////////
	// TaskHanlder
	//////////////////////////////

	/**
	 * Implemented by subclasses to actually do something with a payload of type <P>.
	 *
	 * @param payload An instance of type <P>.
	 */
	protected abstract void handleHelper(final P payload);

	/**
	 * Implemented by subclasses to actually do something with a payload of type <P>.  This method provides access to
	 * the servlet request and response, but is generally not necessary (prefer {@link #handleHelper(P)} instead.)
	 *
	 * @param payload             An instance of type <P>.
	 * @param httpServletRequest  An instance of {@link HttpServletRequest}, provided for convenience.
	 * @param httpServletResponse An instance of {@link HttpServletResponse}, provided for convenience.
	 */
	protected void handleHelper(final P payload, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse)
	{
		// Delegate to the Abstract pseudo super-class.
		this.handleHelper(payload);
	}

	//////////////////////////////
	// TaskScheduler
	//////////////////////////////

	@Override
	public TaskHandle schedule(P payload)
	{
		// Delegate to the Abstract pseudo super-class.
		return this.abstractTaskScheduler.schedule(payload);
	}

	@Override
	public TaskHandle schedule(P payload, final String taskName)
	{
		// Delegate to the Abstract pseudo super-class.
		return this.abstractTaskScheduler.schedule(payload, taskName);
	}

	// /////////////////////
	// Protected Helpers
	// /////////////////////

	/**
	 * @return
	 */
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

}
