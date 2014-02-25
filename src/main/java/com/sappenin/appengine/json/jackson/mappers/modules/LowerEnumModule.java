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
package com.sappenin.appengine.json.jackson.mappers.modules;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sappenin.appengine.json.jackson.mappers.modules.serializers.LowerEnumDeserializer;
import com.sappenin.appengine.json.jackson.mappers.modules.serializers.LowerEnumSerializer;

/**
 * Module for using lower-case enum values when serialzing
 * 
 * @author ceefour
 * @author dfuelling
 * @see "https ://github.com/soluvas/soluvas-framework/blob/1276a77675e63d6cb0107907a4d5c405d085c490/json/src/main/java/org/soluvas/json/LowerEnumModule.java"
 */
public class LowerEnumModule extends SimpleModule
{
	private static final long serialVersionUID = 1L;

	public LowerEnumModule()
	{
		super("sappenin-json-enum", new Version(1, 0, 0, "", "com.sappenin", "appengine-utils"));
		addSerializer(Enum.class, new LowerEnumSerializer());
	}

	@Override
	public void setupModule(final SetupContext context)
	{
		super.setupModule(context);
		final Base deser = new Deserializers.Base()
		{
			@SuppressWarnings("unchecked")
			@Override
			public JsonDeserializer<?> findEnumDeserializer(final Class<?> type, final DeserializationConfig config,
					final BeanDescription beanDesc) throws JsonMappingException
			{
				return new LowerEnumDeserializer((Class<Enum<?>>) type);
			}
		};
		context.addDeserializers(deser);
	};

}