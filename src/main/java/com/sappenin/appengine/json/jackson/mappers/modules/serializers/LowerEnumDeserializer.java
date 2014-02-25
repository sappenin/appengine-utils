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
package com.sappenin.appengine.json.jackson.mappers.modules.serializers;

import java.io.IOException;
import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

/**
 * Deserializer class that can deserialize instances of specified Enum class
 * from Strings and Integers, uppercasing before deserialization.
 * 
 * @author ceefour
 * @see "https://github.com/soluvas/soluvas-framework/blob/1276a77675e63d6cb0107907a4d5c405d085c490/json/src/main/java/org/soluvas/json/LowerEnumDeserializer.java"
 */
public class LowerEnumDeserializer extends StdScalarDeserializer<Enum<?>>
{

	private static final long serialVersionUID = 1L;

	public LowerEnumDeserializer(final Class<Enum<?>> clazz)
	{
		super(clazz);
	}

	@Override
	public Enum<?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException,
			JsonProcessingException
	{
		final String text = jp.getText().toUpperCase();
		try
		{
			final Method valueOfMethod = handledType().getDeclaredMethod("valueOf", String.class);
			return (Enum<?>) valueOfMethod.invoke(null, text);
		}
		catch (final Exception e)
		{
			throw new RuntimeException("Cannot deserialize enum " + handledType().getName() + " from " + text, e);
		}
	}

}
