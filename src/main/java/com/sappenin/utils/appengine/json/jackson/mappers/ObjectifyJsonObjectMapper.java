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
package com.sappenin.utils.appengine.json.jackson.mappers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.googlecode.objectify.util.jackson.ObjectifyJacksonModule;
import com.sappenin.utils.json.jackson.mappers.SappeninJsonObjectMapper;

/**
 * An extension of {@link SappeninJsonObjectMapper} that registers
 * {@link ObjectifyJacksonModule}.
 *
 * @author David Fuelling
 */
public class ObjectifyJsonObjectMapper extends SappeninJsonObjectMapper
{
	private static final long serialVersionUID = -2676273160104775612L;

	/**
	 * No Args constructor
	 */
	public ObjectifyJsonObjectMapper()
	{
		// Enables Joda Searialization/Deserialization
		// See https://github.com/FasterXML/jackson-datatype-joda
		super();

		registerModule(new ObjectifyJacksonModule());

		// Add more here ...
		// registerModule(module);
	}

	/**
	 * @param jf
	 */
	public ObjectifyJsonObjectMapper(final JsonFactory jf)
	{
		super(jf);
	}

	/**
	 * @param src
	 */
	public ObjectifyJsonObjectMapper(final ObjectMapper src)
	{
		super(src);
	}

	/**
	 * @param jf
	 * @param sp
	 * @param dc
	 */
	public ObjectifyJsonObjectMapper(final JsonFactory jf, final DefaultSerializerProvider sp,
			final DefaultDeserializationContext dc)
	{
		super(jf, sp, dc);
	}

}
