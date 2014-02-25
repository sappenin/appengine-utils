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

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.sappenin.appengine.data.dao.ObjectifyStringDao;
import com.sappenin.appengine.data.model.base.AbstractObjectifyStringEntity;
import com.sappenin.exceptions.data.DuplicateEntityException;

/**
 * An abstract implementation of {@link ObjectifyStringDao}.
 * 
 * @author dfuelling
 */
public abstract class AbstractObjectifyStringDao<T extends AbstractObjectifyStringEntity<T>> extends
		AbstractObjectifyDao<T> implements ObjectifyStringDao<T>
{

	@Override
	public void create(final T entity)
	{
		Preconditions.checkNotNull(entity);

		// First check to see if the Entity exists. If it does, throw a
		// DuplicateEntity exception. Otherwise, create a new User in the
		// Datastore.
		ObjectifyService.ofy().transact(new VoidWork()
		{
			@Override
			public void vrun()
			{
				Optional<T> optExisting = findByTypedKey(entity.getTypedKey());
				if (optExisting.isPresent())
				{
					throw new DuplicateEntityException("Unable to Create a new " + entity.getClass().getSimpleName()
						+ " with id \"" + entity.getId() + "\" because it already exists!");
				}

				save(entity);
			}
		});
	}
}