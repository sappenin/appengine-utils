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

import com.sappenin.utils.appengine.data.model.base.AbstractEntity;
import com.sappenin.utils.exceptions.data.DuplicateEntityException;

/**
 * A DAO interface for finding entities identifiers of type {@link String} by their Objectify Key.
 *
 * @author David Fuelling
 */
public interface ObjectifyStringDao<T extends AbstractEntity> extends ObjectifyDao<T>
{
	/**
	 * Create a new entity of type <T> in the Datastore, but only if one doesn't already exist. For
	 * String-identifier-based entities, this function will first check the Datastore to see if an entity exists. If it
	 * does, then a {@link DuplicateEntityException} will be thrown. If no entity with the same Key exists in the
	 * datastore, then this operation will succeed in the same fashion as {@link #save(AbstractEntity)}.
	 *
	 * @param entity An entity to persist into the Datastore.
	 *
	 * @throws {@link DuplicateEntityException} if the entity with this id already exists in the Datastore.
	 */
	void create(final T entity);
}
