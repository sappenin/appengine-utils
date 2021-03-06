/**
 * Copyright (C) 2014-2015 Sappenin Inc. (developers@sappenin.com)
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

/**
 * An abstract base-class for all entity objects that are stored into the GAE Datastore with a Long identifier.
 *
 * @author David Fuelling
 */
@ToString(callSuper = true)
public abstract class AbstractObjectifyLongEntity<T extends AbstractObjectifyEntity> extends AbstractObjectifyEntity<T>
		implements GaeTypedEntity<T>
{
	private static final long serialVersionUID = 4434758494895022079L;

	// For entities like Preferences, where there's only ever 1 of them per User, and the Key is created based upon an
	// @Parent relationship, the id should always be 1.
	public static final long SINGLE_VALUE_UNIQUE_IDENTIFIER = 1L;

	@Id
	@NonNull
	@Getter
	@Setter
	private Long id;

	/**
	 * No args constructor.
	 *
	 * @deprecated Exists only for Objectify. Utilize the Required-Args constructor instead.
	 */
	public AbstractObjectifyLongEntity()
	{
	}

	/**
	 * Required-args constructor.
	 */
	public AbstractObjectifyLongEntity(final Long id)
	{
		Preconditions.checkNotNull(id);
		Preconditions.checkArgument(id.longValue() > 0);

		// Use the setter to allow sub-classes to override this behavior
		this.setId(id);
	}

	/**
	 * Required-args constructor.
	 */
	public AbstractObjectifyLongEntity(final com.googlecode.objectify.Key<T> entityKey)
	{
		this(entityKey.getId());
	}

	/**
	 * Override to assemble a Key with a long-type. Assembles the Key for this entity. If an Entity has a Parent Key,
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
