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

import lombok.NoArgsConstructor;
import lombok.ToString;

import com.googlecode.objectify.Key;
import com.sappenin.appengine.data.model.GaeTypedEntity;

/**
 * An abstract base-class for all entity objects that are stored into the GAE
 * Datastore via Objectify.
 * 
 * @author dfuelling
 * 
 */
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class AbstractObjectifyEntity<T extends AbstractEntity> extends AbstractEntity implements
		GaeTypedEntity<T>
{
	private static final long serialVersionUID = 4434758494895022079L;

	@Override
	public com.googlecode.objectify.Key<T> getTypedKey()
	{
		final com.google.appengine.api.datastore.Key rawKey = getKey();
		if (rawKey == null)
		{
			return null;
		}

		return com.googlecode.objectify.Key.create(rawKey);
	}

	/**
	 * By default, Entities have a null parent Key. This is overridden by
	 * implementations if a Parent key exists.
	 */
	@Override
	public Key<?> getParentKey()
	{
		return null;
	}

}
