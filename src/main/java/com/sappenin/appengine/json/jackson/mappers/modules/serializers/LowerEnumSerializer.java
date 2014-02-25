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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * Lowercase serializer used for {@link java.lang.Enum} types.
 * <p>
 * Based on {@link StdScalarSerializer} since the JSON value is scalar (String).
 * 
 * @see "http://jira.codehaus.org/browse/JACKSON-861"
 * @see "https://github.com/soluvas/soluvas-framework/blob/1276a77675e63d6cb0107907a4d5c405d085c490/json/src/main/java/org/soluvas/json/LowerEnumSerializer.java"
 * 
 * @author ceefour
 */
@SuppressWarnings("rawtypes")
public class LowerEnumSerializer extends StdScalarSerializer<Enum>
{

	public LowerEnumSerializer()
	{
		super(Enum.class, false);
	}

	@Override
	public void serialize(final Enum value, final JsonGenerator jgen, final SerializerProvider provider)
			throws IOException, JsonGenerationException
	{
		jgen.writeString(value.name().toLowerCase());
	}
}