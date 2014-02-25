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
package com.sappenin.appengine.data.dao.base;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.sappenin.appengine.data.dao.ObjectifyLongDao;
import com.sappenin.appengine.data.model.base.AbstractObjectifyLongEntity;

/**
 * An abstract implementation of {@link ObjectifyLongDao}.
 * 
 * @author dfuelling
 */
public abstract class AbstractObjectifyLongDao<T extends AbstractObjectifyLongEntity<T>> extends
		AbstractObjectifyDao<T> implements ObjectifyLongDao<T>
{

	// /////////////////////////////////////////////
	// SAVE METHODS
	// /////////////////////////////////////////////

	@Override
	public void create(final T entity)
	{
		// Assert that the Id is null. If it is not null, then #save should be
		// used instead.
		Preconditions.checkNotNull(entity);
		Preconditions.checkArgument(entity.getId() == null,
			"Cannot #createNew an Entity that has an existing Key.  Call the Dao's #save function instead.");

		// Set the id here so that the call to #save below is idempotent
		@SuppressWarnings("rawtypes")
		Key<? extends AbstractObjectifyLongEntity> allocatedId = ObjectifyService.ofy().factory()
			.allocateId(entity.getClass());
		entity.setId(allocatedId.getId());

		ObjectifyService.ofy().save().entity(entity).now();
	}

}