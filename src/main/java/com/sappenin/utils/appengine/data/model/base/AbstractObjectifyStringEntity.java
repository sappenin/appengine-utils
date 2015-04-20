/**
 * Copyright (C) 2014 Sappenin Inc. (developers@sappenin.com)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.sappenin.utils.appengine.data.model.base;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Id;
import com.sappenin.utils.appengine.data.model.GaeTypedEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * An abstract base-class for all entity objects that are stored into the GAE Datastore with a String identifier.
 *
 * @author David Fuelling
 */
@ToString
public abstract class AbstractObjectifyStringEntity<T extends AbstractObjectifyEntity> extends AbstractObjectifyEntity<T>
		implements GaeTypedEntity<T>
{
	private static final long serialVersionUID = -2635333245859630019L;

	@Id
	@NonNull
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
	 */
	public AbstractObjectifyStringEntity(final String id)
	{
		Preconditions.checkArgument(!StringUtils.isBlank(id));
		Preconditions.checkArgument(id.length() < 501,
				"The String id of an App Engine Datastore entity in  may not exceed 500 characters!");

		// Use the setter to allow sub-classes to override this behavior
		this.setId(id);
	}

	/**
	 * Required-args constructor.
	 */
	public AbstractObjectifyStringEntity(final com.googlecode.objectify.Key<T> entityKey)
	{
		this(entityKey.getName());
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
