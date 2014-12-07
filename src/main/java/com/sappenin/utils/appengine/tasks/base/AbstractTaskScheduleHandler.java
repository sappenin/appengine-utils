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
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Abstract implementation of {@link TaskHandler} for assisting with the handling of tasks using the taskqueue service
 * in Goolge AppEngine.
 *
 * @author David Fuelling
 */
@Getter
public abstract class AbstractTaskScheduleHandler<P> implements TaskHandler
{
	private static final String APPLICATION_JSON = "application/json";

	private final JsonUtils jsonUtils;

	/**
	 * Required Args Constructor.
	 */
	public AbstractTaskScheduleHandler(final JsonUtils jsonUtils)
	{
		Preconditions.checkNotNull(jsonUtils);
		this.jsonUtils = jsonUtils;
	}

	/**
	 * Implemented by subclasses to actually do something with a payload of type <P>.
	 *
	 * @param payload             An instance of type <P>.
	 * @param httpServletRequest  An instance of {@link HttpServletRequest}, provided for convenience.
	 * @param httpServletResponse An instance of {@link HttpServletResponse}, provided for convenience.
	 */
	protected abstract void handleHelper(final P payload, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse);

	/**
	 * Handles a particular task for the TaskQueue system on Google App Engine.
	 */
	@Override
	public final void handle(final HttpServletRequest request, final HttpServletResponse response) throws Exception
	{
		// It is expected that the JsonPayload for this Handler is an object of type SyntheticIndexPayload.
		String jsonPayload = this.jsonUtils.getJsonContentFromRequest(request);
		Preconditions.checkNotNull(jsonPayload);
		logger.entering(this.getClass().getName(), "handle", jsonPayload);

		P typedPayload = objectMapper.readValue(jsonPayload, SyntheticIndexPayloadJson.class);

		this.handleHelper(typedPayload, request, response);
	}

	// /////////////////////
	// Protected Helpers
	// /////////////////////

	protected abstract Logger getLogger();

	//	/**
	//	 * Helper method to grab a Json Payload from the InputStream of an {@link HttpServletRequest}.  Not used in
	// this
	//	 * class but used by sub-classes.
	//	 *
	//	 * @param httpServletRequest
	//	 *
	//	 * @return
	//	 */
	//	protected String getJsonPayloadFromRequest(final HttpServletRequest httpServletRequest) throws IOException
	//	{
	//		Preconditions.checkNotNull(httpServletRequest);
	//		return this.getJsonPayloadFromRequest(httpServletRequest.getInputStream());
	//	}

	//	/**
	//	 * Helper method to grab a Json Payload from an InputStream.  This is generally used in concert with an {@link
	//	 * HttpServletRequest}, but doesn't strictly need to be.   Not used in this class but used by sub-classes.
	//	 *
	//	 * @param inputStream
	//	 *
	//	 * @return
	//	 */
	//	protected String getJsonPayloadFromRequest(final InputStream inputStream) throws IOException
	//	{
	//		Preconditions.checkNotNull(inputStream);
	//		try (final InputStream stream = inputStream)
	//		{
	//			String jsonPayload = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
	//			getLogger().exiting(this.getClass().getName(), "getJsonPayloadFromRequest", jsonPayload);
	//			return jsonPayload;
	//		}
	//	}
}
