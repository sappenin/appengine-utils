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
package com.sappenin.utils.appengine.data.dao;

import com.google.appengine.api.datastore.Cursor;
import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.cmd.Query;
import com.sappenin.utils.appengine.data.model.ResultWithCursor;
import com.sappenin.utils.appengine.data.model.base.AbstractEntity;

import java.util.List;

/**
 * A DAO interface for finding entities by their Objectify Key.
 *
 * @author David Fuelling
 */
public interface ObjectifyDao<T extends AbstractEntity> extends Dao<T>
{
	/**
	 * Create an entity in the Datastore.
	 *
	 * @param entity
	 */
	void create(final T entity);

	/**
	 * Finds an entity from the database using a typed Objectify Key<T>.
	 *
	 * @return The optionally found entity from the datastore, or {@code null} if the entity doesn't exist.
	 */
	Optional<T> findByTypedKey(final Key<T> typedKey);

	/**
	 * Finds an entity from the database using a typed Objectify Key<T>
	 *
	 * @throws NotFoundException Thrown if an entity identified by {@code typedKey} is not found in the Datastore.
	 */
	T findByTypedKeySafe(final Key<T> typedKey) throws NotFoundException;

	/**
	 * Helper method to load entities from the Datastore using paging.
	 *
	 * @param query  A {@link Query} that has not been finalized.
	 * @param offset A {@link Cursor} for paging.
	 * @param limit  A limit on the number of query results to return.
	 *
	 * @return
	 */
	ResultWithCursor<List<T>> loadFromDatastoreWithCursor(final Query<T> query, final Cursor offset, int limit);

	/**
	 * Helper method to load entity Keys from the Datastore using paging.
	 *
	 * @param query  A {@link Query} that has not been finalized.
	 * @param offset A {@link Cursor} for paging.
	 * @param limit  A limit on the number of query results to return.
	 *
	 * @return
	 */
	ResultWithCursor<List<Key<T>>> loadKeysOnlyFromDatastoreWithCursor(final Query<T> query, final Cursor offset,
			final int limit);

	/**
	 * Determines if the entity indicated by {@code typedKey} exists in the datastore.  Note that this operation is not
	 * strongly consistent.  If consistency is desired, then prefer {@link #existsInDatastoreConsistent}.
	 *
	 * @param typedKey
	 *
	 * @return
	 */
	boolean existsInDatastore(final Key<T> typedKey);

	/**
	 * Determines if the entity indicated by {@code typedKey} exists in the datastore.  Note that this operation is
	 * strongly consistent, but is slightly slower than {@link #existsInDatastore(Key)}.
	 *
	 * @param typedKey
	 *
	 * @return
	 */
	boolean existsInDatastoreConsistent(final Key<T> typedKey);
}
