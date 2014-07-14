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
package com.sappenin.utils.appengine.data.dao;

import com.sappenin.utils.appengine.data.model.base.AbstractEntity;

/**
 * A DAO interface for finding entities identifiers of type {@link Long} by their Objectify Key.
 *
 * @author David Fuelling
 */
public interface ObjectifyLongDao<T extends AbstractEntity> extends ObjectifyDao<T>
{
	/**
	 * Creates a new entity of type <T> in the datastore. This function enforces that {@link
	 * com.google.appengine.api.datastore.Entity#getKey()} is null, making this function non-idempotent. If the Key/id
	 * of the entity is not null, then #save should be used instead. <br/> <br/> This function is used to help
	 * developers clarify that they're calling the proper persistence method, since by nature the App Engine Datastore
	 * does not have any native overwrite checking. For example, if an entity has an id already, it would be a mistake
	 * to call {@link #create(AbstractEntity)} since the logic around creating and saving is potentially different
	 * based
	 * upon the entity's business rules.
	 */
	public void create(final T entity);
}
