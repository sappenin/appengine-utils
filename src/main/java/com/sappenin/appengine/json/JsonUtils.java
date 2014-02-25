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
package com.sappenin.appengine.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;

/**
 * Utilities for Dealing with JSON and Jackson.
 * 
 * @author dfuelling
 */
public interface JsonUtils
{
	/**
	 * Convert an object of type
	 * <P>
	 * into a Json String.
	 * 
	 * @param payload
	 * @return
	 * @throws JsonProcessingException
	 */
	public <P> String toJSON(P payload) throws JsonProcessingException;

	/**
	 * Convert the body of an {@link HttpServletRequest} into an of the proper
	 * type as found in the "@class" directive in the Json.
	 * 
	 * @param request
	 * @param payloadClass
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public <P> P fromJson(HttpServletRequest request) throws IOException, ClassNotFoundException;

	/**
	 * Convert a {@link JsonNode} into an object of the proper type as found in
	 * the "@class" directive in the JsonNode.
	 * 
	 * @param jsonNode
	 * @return
	 * @throws ClassNotFoundException
	 */
	public <P> P fromJson(JsonNode jsonNode) throws ClassNotFoundException;

	/**
	 * Retrieves the {@link JsonNode} from the {@code request}.
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public JsonNode getJsonContentFromRequest(HttpServletRequest request) throws IOException, ClassNotFoundException;

	/**
	 * An implementation of {@link JsonUtils}.
	 * 
	 * @author dfuelling
	 */
	public static class Impl implements JsonUtils
	{
		protected final static Logger logger = Logger.getLogger(Impl.class.getName());

		private final ObjectMapper objectMapper;

		/**
		 * Required-Args constructor
		 * 
		 * @param objectMapper
		 */
		public Impl(final ObjectMapper objectMapper)
		{
			this.objectMapper = objectMapper;
		}

		/**
		 * 
		 * @param payload
		 * @return
		 * @throws JsonProcessingException
		 */
		@Override
		public <T> String toJSON(final T payload) throws JsonProcessingException
		{
			Preconditions.checkNotNull(payload);

			final String jsonString = this.objectMapper.writeValueAsString(payload);
			return jsonString;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <P> P fromJson(final JsonNode jsonNode) throws ClassNotFoundException
		{
			Preconditions.checkNotNull(jsonNode);

			// The Payload is UTF-8.
			logger.info("Converting to Java Class from the following JsonNode: \"" + jsonNode + "\"");

			JsonNode classNameJsonNode = jsonNode.get("@class");
			Preconditions.checkNotNull(classNameJsonNode,
				"Cannot DeSerialize Json using this method without an @Class node!");
			Class<?> payloadClass = Class.forName(classNameJsonNode.asText().toString());
			final Object payload = this.objectMapper.convertValue(jsonNode, payloadClass);
			return (P) payload;
		}

		@Override
		public JsonNode getJsonContentFromRequest(final HttpServletRequest request) throws IOException,
				ClassNotFoundException
		{
			Preconditions.checkNotNull(request);

			// The Payload is UTF-8.
			String jsonPayload = null;
			try (final InputStream stream = request.getInputStream())
			{
				jsonPayload = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
			}
			Preconditions.checkNotNull(jsonPayload);
			logger.info("Retrieved the following JSON Payload from HttpServletRequest: \"" + jsonPayload + "\"");
			// since 2.1 use mapper.getFactory() instead
			final JsonParser jp = this.objectMapper.getFactory().createParser(jsonPayload);
			final JsonNode jsonNode = this.objectMapper.readTree(jp);
			return jsonNode;
		}

		@Override
		public <P> P fromJson(final HttpServletRequest request) throws IOException, ClassNotFoundException
		{
			Preconditions.checkNotNull(request);

			JsonNode jsonNode = this.getJsonContentFromRequest(request);

			return this.fromJson(jsonNode);
		}

	}
}
