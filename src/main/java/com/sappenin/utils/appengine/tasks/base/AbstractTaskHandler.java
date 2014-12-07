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

import com.google.common.base.Preconditions;
import com.sappenin.utils.appengine.tasks.TaskHandler;
import com.sappenin.utils.json.JsonUtils;
import com.sappenin.utils.json.JsonUtilsClassTypeMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Abstract implementation of {@link TaskHandler} for assisting with the handling of tasks using the TaskQueue service
 * in Google AppEngine.
 *
 * @author David Fuelling
 */
public abstract class AbstractTaskHandler<P> implements TaskHandler
{
	private final JsonUtils jsonUtils;

	private final JsonUtilsClassTypeMapper jsonUtilsClassTypeMapper;

	/**
	 * Required Args Constructor.
	 *
	 * @param jsonUtils                An instance of {@link JsonUtils} for deserializing JSON payloads from the
	 *                                 TaskQueue.
	 * @param jsonUtilsClassTypeMapper An instance of {@link JsonUtilsClassTypeMapper} for providing runtime Class-type
	 *                                 information for deserialization via {@link JsonUtils}.
	 */
	public AbstractTaskHandler(final JsonUtils jsonUtils, final JsonUtilsClassTypeMapper jsonUtilsClassTypeMapper)
	{
		Preconditions.checkNotNull(jsonUtils);
		this.jsonUtils = jsonUtils;

		Preconditions.checkNotNull(jsonUtilsClassTypeMapper);
		this.jsonUtilsClassTypeMapper = jsonUtilsClassTypeMapper;
	}

	/**
	 * Implemented by subclasses to actually do something with a payload of type <P>.
	 *
	 * @param payload An instance of type <P>.
	 */
	protected abstract void handleHelper(final P payload) throws Exception;

	/**
	 * Implemented by subclasses to actually do something with a payload of type <P>.  This method provides access to
	 * the servlet request and response, but is generally not necessary (prefer {@link #handleHelper(Object)} instead.)
	 *
	 * @param payload             An instance of type <P>.
	 * @param httpServletRequest  An instance of {@link HttpServletRequest}, provided for convenience.
	 * @param httpServletResponse An instance of {@link HttpServletResponse}, provided for convenience.
	 */
	protected void handleHelper(final P payload, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) throws Exception
	{
		Preconditions.checkNotNull(payload);
		Preconditions.checkNotNull(httpServletRequest);
		Preconditions.checkNotNull(httpServletResponse);

		this.handleHelper(payload);
	}

	/**
	 * Handles a particular task for the TaskQueue system on Google App Engine.
	 */
	@Override
	public final void handle(final HttpServletRequest request, final HttpServletResponse response) throws Exception
	{
		final P typedPayload = this.jsonUtils.fromJson(request, this.jsonUtilsClassTypeMapper);
		Preconditions.checkNotNull(typedPayload);

		this.getLogger().entering(this.getClass().getName(), "handle", typedPayload);
		this.handleHelper(typedPayload, request, response);
		this.getLogger().exiting(this.getClass().getName(), "handle");
	}

	/**
	 * Abstract method to return the logger of the implementing class.
	 *
	 * @return
	 */
	protected abstract Logger getLogger();

	/**
	 * Protected Getter for access by implementing classes.
	 *
	 * @return
	 */
	protected JsonUtils getJsonUtils()
	{
		return this.jsonUtils;
	}

}
