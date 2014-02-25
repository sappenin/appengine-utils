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
package com.sappenin.appengine.json.jackson.mappers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.googlecode.objectify.util.jackson.ObjectifyJacksonModule;
import com.sappenin.appengine.json.jackson.mappers.modules.LowerEnumModule;

/**
 * An extension of {@link ObjectMapper} that registers Amiss-specific mappers.
 */
public class JsonObjectMapper extends ObjectMapper
{
	private static final long serialVersionUID = -2676273160104775612L;

	/**
	 * No Args constructor
	 */
	public JsonObjectMapper()
	{
		// Enables Joda Searialization/Deserialization
		// See https://github.com/FasterXML/jackson-datatype-joda
		registerModule(new JodaModule());
		registerModule(new LowerEnumModule());
		registerModule(new ObjectifyJacksonModule());

		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		setDateFormat(new ISO8601DateFormat());

		// Add more here ...
		// registerModule(module);
	}

	/**
	 * @param jf
	 */
	public JsonObjectMapper(final JsonFactory jf)
	{
		super(jf);
	}

	/**
	 * @param src
	 */
	public JsonObjectMapper(final ObjectMapper src)
	{
		super(src);
	}

	/**
	 * @param jf
	 * @param sp
	 * @param dc
	 */
	public JsonObjectMapper(final JsonFactory jf, final DefaultSerializerProvider sp,
			final DefaultDeserializationContext dc)
	{
		super(jf, sp, dc);
	}

}
