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
package com.sappenin.utils.appengine.data.dao.base;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.sappenin.utils.appengine.data.dao.ObjectifyStringDao;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyStringEntity;
import com.sappenin.utils.exceptions.data.DuplicateEntityException;

/**
 * An abstract implementation of {@link ObjectifyStringDao}.
 *
 * @author David Fuelling
 */
public abstract class AbstractObjectifyStringDao<T extends AbstractObjectifyStringEntity<T>>
		extends AbstractObjectifyDao<T> implements ObjectifyStringDao<T>
{

	@Override
	public void create(final T entity)
	{
		Preconditions.checkNotNull(entity);
		Preconditions.checkArgument(entity.getKey() != null, "Cannot #createNew an Entity that has no Key.");

		// First check to see if the Entity exists. If it does, throw a DuplicateEntity exception. Otherwise, create a
		// new User in the Datastore.
		ObjectifyService.ofy().transact(new VoidWork()
		{
			@Override
			public void vrun()
			{
				final Optional<T> optExisting = findByTypedKey(entity.getTypedKey());
				if (optExisting.isPresent())
				{
					throw new DuplicateEntityException(
							"Unable to Create a new " + entity.getClass().getSimpleName() + " with id \"" + entity
									.getId() + "\" because it already exists in the Datastore!");
				}

				ObjectifyService.ofy().save().entity(entity).now();
			}
		});
	}

}
