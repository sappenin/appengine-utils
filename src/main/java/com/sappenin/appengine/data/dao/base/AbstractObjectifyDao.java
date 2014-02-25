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

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.sappenin.appengine.data.dao.ObjectifyDao;
import com.sappenin.appengine.data.model.ResultWithCursor;
import com.sappenin.appengine.data.model.base.AbstractEntity;

/**
 * An abstract implementation of {@link ObjectifyDao}.
 * 
 * @author dfuelling
 */
public abstract class AbstractObjectifyDao<T extends AbstractEntity> extends AbstractDao<T> implements ObjectifyDao<T>
{

	// ////////////////////////////////////////
	// Abstract Overides
	// ////////////////////////////////////////

	/**
	 * Doesn't allow an entity with a non-null Key<T> to be saved, and updates the updatedDateTime to be "now".
	 * 
	 * @param entity
	 */
	@Override
	public void save(final T entity)
	{
		Preconditions.checkNotNull(entity);
		Preconditions.checkArgument(entity.getKey() != null,
			"Cannot #save an Entity that has no Key.  Call the Dao's #createNew function instead.");

		entity.setUpdateDateTime(DateTime.now(DateTimeZone.UTC));

		ObjectifyService.ofy().save().entity(entity).now();
	}

	// ////////////////////////////
	// Helper Functions
	// ////////////////////////////

	@Override
	public Optional<T> findByTypedKey(final Key<T> typedKey)
	{
		Preconditions.checkNotNull(typedKey);
		// #now will return null if the entity isn't found, which Optional can handle.
		return Optional.<T> fromNullable(ObjectifyService.ofy().load().key(typedKey).now());
	}

	@Override
	public T findByTypedKeySafe(final Key<T> typedKey) throws NotFoundException
	{
		Preconditions.checkNotNull(typedKey);
		// Prefer #safe over #now because it will throw a NotFoundException if
		// the entity is not found.
		return ObjectifyService.ofy().load().key(typedKey).safe();
	}

	@Override
	public ResultWithCursor<List<T>> loadFromDatastoreWithCursor(Query<T> finalizedQuery, final Cursor offset, int limit)
	{
		Preconditions.checkNotNull(finalizedQuery);
		// TODO Allow external callers to udpate the number of allowed items to
		// return...
		// See
		if ((limit <= 0) || (limit > 50))
		{
			limit = 10;
		}

		final List<T> resultCollection = Lists.newLinkedList();
		final ResultWithCursor<List<T>> resultWithCursor = new ResultWithCursor<List<T>>(resultCollection);

		// See
		// http://stackoverflow.com/questions/14088808/query-cursor-with-app-engine-java-jdo
		// This will load 11 InboxEntry objects, if applicable. If there are
		// more than 10, we need to store the cursor
		// as it was at the 10th item. Otherwise, we need to omit the cursor
		// (the cursor updates on every iterator bump)

		// Load limit + 1 to get a Cursor to more results, if any
		finalizedQuery = finalizedQuery.limit(limit + 1);
		finalizedQuery = finalizedQuery.startAt(offset);

		final QueryResultIterator<T> entitiesIterator = finalizedQuery.iterable().iterator();
		Cursor lastRetrievedResultCursor = null;
		// Set to true to populate the cursor, indicating there are more
		// results. This can be gleaned from teh value of
		// numProcessedInboxEntries, but this variable is used to be more overt.
		boolean returnCursor = false;
		int numProcessedEntities = 0;
		while (entitiesIterator.hasNext())
		{
			final T entity = entitiesIterator.next();

			// We load up to 11 items, but only want to return a maximum of
			// limit (generally 10).
			if (numProcessedEntities < limit)
			{
				// Only assign the cursor if we're going to display the Idea.
				// Otherwise, the cursor will remain
				// at the position of the last entity that was displayed to the
				// UI. Since the Cursor is
				// start_inclusive=false, the next item will be loaded properly.
				lastRetrievedResultCursor = entitiesIterator.getCursor();
				// Add the Entity to the Return Set.
				resultWithCursor.getResult().add(entity);
			}
			else
			{
				returnCursor = true;
				break;
			}

			// Even if the Entity isn't added to the return list, increment this
			// number. Occasionally, for deleted
			// entries, only 9 or 8 will come back to the UI, and the more
			// button will display, but this is ok.
			numProcessedEntities++;
		}

		// Capture the cursor position for future calls.
		if (returnCursor)
		{
			// Add the cursor - there are more entries to be loaded and
			// displayed on the UI.
			resultWithCursor.setCursor(lastRetrievedResultCursor);
		}

		return resultWithCursor;
	}
}