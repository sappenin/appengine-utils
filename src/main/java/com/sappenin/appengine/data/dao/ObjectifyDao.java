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
package com.sappenin.appengine.data.dao;

import java.util.List;

import com.google.appengine.api.datastore.Cursor;
import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.cmd.Query;
import com.sappenin.appengine.data.model.ResultWithCursor;
import com.sappenin.appengine.data.model.base.AbstractEntity;

/**
 * A DAO interface for finding entities by their Objectify Key.
 * 
 * @author dfuelling
 */
public interface ObjectifyDao<T extends AbstractEntity> extends Dao<T>
{
	/**
	 * Finds an entity from the database using a typed Objectify Key<T>.
	 * 
	 * @param typedKey
	 * @return The optionally found entity from the datastore, or {@code null} if the entity doesn't exist.
	 */
	public Optional<T> findByTypedKey(final Key<T> typedKey);

	/**
	 * Finds an entity from the database using a typed Objectify Key<T>
	 * 
	 * @param typedKey
	 * @throws NotFoundException Thrown if an entity identified by {@code typedKey} is not found in the Datastore.
	 */
	public T findByTypedKeySafe(final Key<T> typedKey) throws NotFoundException;

	/**
	 * Helper method to load data from the Datastore using paging.
	 * 
	 * @param finalizedQuery A {@link Query} that has been finalized.
	 * @param offset A {@link Cursor} for paging.
	 * @param limit A limit on the number of query results to return.
	 * @return
	 */
	public ResultWithCursor<List<T>> loadFromDatastoreWithCursor(Query<T> finalizedQuery, Cursor offset, int limit);
}
