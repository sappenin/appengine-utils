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

import com.sappenin.utils.annotations.Idempotent;
import com.sappenin.utils.appengine.data.model.base.AbstractEntity;

/**
 * An interface that defines common DAO functionality.
 *
 * @author David Fuelling
 */
public interface Dao<T extends AbstractEntity>
{
	/**
	 * Saves an entity of type <T> to the datastore. By definition, this operation is idempotent, meaning a repetition
	 * of this operation will always produce the same result if completed successfully.
	 *
	 * @param entity The entity to save in the datastore.
	 */
	@Idempotent
	public void save(final T entity);

	/**
	 * Saves an entity of type <T> to the datastore. By definition, this operation is idempotent if {@code
	 * touchUpdateDateTime} is set to {@code false}.
	 *
	 * @param entity              The entity to save in the datastore.
	 * @param touchUpdateDateTime A boolean to indicate if the updateDateTime should be incremented to "now".
	 */
	public void save(T entity, boolean touchUpdateDateTime);

}
