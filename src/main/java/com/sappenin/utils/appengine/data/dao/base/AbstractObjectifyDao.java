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
package com.sappenin.utils.appengine.data.dao.base;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.Query;
import com.sappenin.utils.annotations.Idempotent;
import com.sappenin.utils.appengine.data.dao.ObjectifyDao;
import com.sappenin.utils.appengine.data.model.ResultWithCursor;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyEntity;
import com.sappenin.utils.exceptions.data.DuplicateEntityException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

/**
 * An abstract implementation of {@link ObjectifyDao}.
 *
 * @author David Fuelling
 */
public abstract class AbstractObjectifyDao<T extends AbstractObjectifyEntity<T>> extends AbstractDao<T>
		implements ObjectifyDao<T>
{

	// ////////////////////////////////////////
	// Abstract Overides
	// ////////////////////////////////////////

	/**
	 * Doesn't allow an entity with a non-null Key<T> to be saved, and updates the updatedDateTime to be "now".
	 */
	@Override
	@Idempotent
	public void save(final T entity)
	{
		this.save(entity, false);
	}

	/**
	 * Doesn't allow an entity with a non-null Key<T> to be saved, and optinoally updates the updatedDateTime to be
	 * "now" if {@code touchUpdateDateTime} is set to {@code true}.
	 */
	@Override
	public void save(final T entity, final boolean touchUpdateDateTime)
	{
		Preconditions.checkNotNull(entity);
		Preconditions.checkArgument(entity.getKey() != null,
				"Cannot #save an Entity that has no Key.  Call the Dao's #createNew function instead.");

		ObjectifyService.ofy().transact(new VoidWork()
		{
			@Override
			public void vrun()
			{
				final Optional<T> optExisting = findByTypedKey(entity.getTypedKey());
				if (!optExisting.isPresent())
				{
					throw new DuplicateEntityException(
							"Unable to save an existing " + entity.getClass().getSimpleName() + " with id \"" + entity
									.getId() + "\" because it does not exist in the Datastore!");
				}

				if (touchUpdateDateTime)
				{
					entity.setUpdateDateTime(DateTime.now(DateTimeZone.UTC));
				}

				ObjectifyService.ofy().save().entity(entity).now();
			}
		});

	}

	// ////////////////////////////
	// Helper Functions
	// ////////////////////////////

	@Override
	public boolean existsInDatastore(final Key<T> typedKey)
	{
		Preconditions.checkNotNull(typedKey);

		// See "https://groups.google.com/forum/#!searchin/objectify-appengine/exist/objectify-appengine/zFI2YWP5DTI
		// /BpwFNlVQo1UJ".  This methodolody will be faster than a get-by-key because the Datastore merely does an
		// index-walk, and has minimal protobuf overhead.  However, this will be slightly costlier in the case where
		// an entity exists (but equivalent when an entity doesn't exist).
		return ObjectifyService.ofy().load().filterKey(typedKey).limit(1).count() == 1;
	}

	@Override
	public boolean existsInDatastoreConsistent(final Key<T> typedKey)
	{
		Preconditions.checkNotNull(typedKey);

		// See "https://groups.google.com/forum/#!searchin/objectify-appengine/exist/objectify-appengine/zFI2YWP5DTI
		// /BpwFNlVQo1UJ".  This methodolody will be less expensive, and strongly-consistent than the
		// existsInDatastore, but will be slower.
		return ObjectifyService.ofy().load().key(typedKey).now() != null;
	}

	@Override
	public Optional<T> findByTypedKey(final Key<T> typedKey)
	{
		Preconditions.checkNotNull(typedKey);
		// #now will return null if the entity isn't found, which Optional can
		// handle.
		return Optional.fromNullable(ObjectifyService.ofy().load().key(typedKey).now());
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
	public ResultWithCursor<List<T>> loadFromDatastoreWithCursor(final Query<T> query, final Cursor offset, int limit)
	{
		final int adjustedLimit = adjustLimit(limit);
		final Query<T> actualQuery = this.massageQuery(query, offset, adjustedLimit);
		final QueryResultIterator<T> iterator = actualQuery.iterator();
		return this.assembleResultWithCursor(iterator, adjustedLimit);
	}

	@Override
	public ResultWithCursor<List<Key<T>>> loadKeysOnlyFromDatastoreWithCursor(final Query<T> query, final Cursor
			offset,
			final int limit)
	{
		final int adjustedLimit = adjustLimit(limit);
		final Query<T> actualQuery = this.massageQuery(query, offset, adjustedLimit);
		final QueryResultIterator<Key<T>> iterator = actualQuery.keys().iterator();
		return this.assembleResultWithCursor(iterator, adjustedLimit);
	}

	//////////////////
	// Private Helpers
	//////////////////

	/**
	 * Adjusts a limit value to be within the parameters of this class.<br/> <br/>TODO: See appengine-utils #1.
	 *
	 * @param limit An integer representing the number of items to return via this query.
	 *
	 * @return the adjusted limit
	 * @see "https://github.com/sappenin/appengine-utils/issues/1"
	 */
	@VisibleForTesting
	int adjustLimit(int limit)
	{
		if ((limit <= 0) || (limit > 50))
		{
			limit = 10;
		}
		return limit;
	}

	/**
	 * Helper method to massage a {@link Query} object to have the proper limit and offset values.
	 *
	 * @param finalizedQuery An instance of {@link Query} of type {@link T}.
	 * @param offset         An instance of {@link Cursor} that represents the offset to begin this query at.
	 * @param limit          An integer representing the number of items to return via this query.
	 *
	 * @return A massaged query.
	 */
	@VisibleForTesting
	Query<T> massageQuery(Query<T> finalizedQuery, final Cursor offset, int limit)
	{
		Preconditions.checkNotNull(finalizedQuery);
		if ((limit <= 0) || (limit > 50))
		{
			limit = 10;
		}

		// See
		// http://stackoverflow.com/questions/14088808/query-cursor-with-app-engine-java-jdo
		// This will load 11 InboxEntry objects, if applicable. If there are
		// more than 10, we need to store the cursor
		// as it was at the 10th item. Otherwise, we need to omit the cursor
		// (the cursor updates on every iterator bump)

		// Load limit + 1 to get a Cursor to more results, if any
		Query<T> returnableUpdatedQuery = finalizedQuery.limit(limit + 1);
		returnableUpdatedQuery = returnableUpdatedQuery.startAt(offset);

		return returnableUpdatedQuery;

	}

	/**
	 * Assembles a {@link ResultWithCursor} that holds a List of objects of type <Z>.
	 *
	 * @param entitiesIterator An instance of {@link QueryResultIterator} that has entities to operate upon.
	 * @param limit            An integer representing the number of results to return from this method.
	 * @param <Z>              The type of entity that should be returned in the {@link ResultWithCursor}.  This allows
	 *                         both entities and entity keys to be returned, thus supporting keys-only queries.
	 *
	 * @return A {@link ResultWithCursor} of type {@link List} of type {@link Z}.
	 */
	@VisibleForTesting
	<Z> ResultWithCursor<List<Z>> assembleResultWithCursor(
			final com.google.appengine.api.datastore.QueryResultIterator<Z> entitiesIterator, final int limit)
	{
		// The thing to return...
		Cursor lastRetrievedResultCursor = null;

		final List<Z> resultCollection = Lists.newLinkedList();
		final ResultWithCursor<List<Z>> resultWithCursor = new ResultWithCursor<>(resultCollection);

		// Set to true to populate the cursor, indicating there are more
		// results. This can be gleaned from teh value of
		// numProcessedInboxEntries, but this variable is used to be more overt.
		boolean returnCursor = false;
		int numProcessedEntities = 0;
		while (entitiesIterator.hasNext())
		{
			final Z entity = entitiesIterator.next();

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
