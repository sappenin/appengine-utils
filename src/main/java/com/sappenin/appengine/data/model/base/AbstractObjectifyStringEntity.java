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
package com.sappenin.appengine.data.model.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Id;
import com.sappenin.appengine.data.model.GaeTypedEntity;

/**
 * An abstract base-class for all entity objects that are stored into the GAE Datastore with a String identifier.
 * 
 * @author dfuelling
 */
@ToString
public abstract class AbstractObjectifyStringEntity<T extends AbstractEntity> extends AbstractObjectifyEntity<T>
		implements GaeTypedEntity<T>
{
	private static final long serialVersionUID = -2635333245859630019L;

	@Id
	@Getter
	@Setter
	private String id;

	/**
	 * No args constructor.
	 * 
	 * @deprecated Exists only for objectify. Utilize the Required Args constructor instead.
	 */
	public AbstractObjectifyStringEntity()
	{
	}

	/**
	 * Required-args constructor.
	 * 
	 * @param id
	 */
	public AbstractObjectifyStringEntity(String id)
	{
		Preconditions.checkArgument(!StringUtils.isBlank(id));
		Preconditions.checkArgument(id.length() < 501,
			"The String id of an App Engine Datastore entity in  may not exceed 500 characters!");
		this.id = id;
	}

	/**
	 * Override to assemble a Key with a String-type. Assembles the Key for this entity. If an Entity has a Parent Key,
	 * that key will be included in the returned Key hierarchy.
	 */
	@Override
	public Key getKey()
	{
		if (this.getId() == null)
		{
			return null;
		}
		else
		{
			return com.googlecode.objectify.Key.create(getParentKey(), this.getClass(), this.getId()).getRaw();
		}
	}

}
