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
/**
 *
 */
package com.sappenin.utils.appengine.data.model;

import com.googlecode.objectify.Key;
import com.sappenin.utils.appengine.data.model.base.AbstractEntity;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyEntity;

/**
 * An interface that defines functionality required for model objects to be compatible with the GAE Datastore.
 *
 * @author David Fuelling
 */
public interface GaeTypedEntity<T extends AbstractEntity> extends GaeEntity
{
	/**
	 * Returns the {@link Key} of Type <T> for this Entity.
	 *
	 * @return
	 */
	Key<T> getTypedKey();

	/**
	 * Returns the {@link Key<?> parentKey} of this Entity, if one exists. Otherwise returns null.
	 *
	 * @return
	 */
	Key<?> getParentKey();
}
