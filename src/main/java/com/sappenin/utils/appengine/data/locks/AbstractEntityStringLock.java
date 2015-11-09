/**
 * Copyright (C) 2014-2015 Sappenin Inc. (developers@sappenin.com)
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
package com.sappenin.utils.appengine.data.locks;

import com.googlecode.objectify.annotation.Unindex;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyStringEntity;

/**
 * An abstract class that provides the nuts & bolts for a unique-String locking mechanism in Appengine. See this thread
 * here for more details: https://groups. google.com/forum/#!searchin/objectify-appengine/usernamelock/objectify
 * -appengine /Jamr_Ib4vlE/WBvzZ_xAjDwJ <BR> <BR> One difference between this class and the class outlined in the forum
 * is that this class does not remove the Lock because it assumes the Entity is not keyed by the entityFieldValue. For
 * example, a UsernameLock would want to Lock a User by its Username, but the actual entity is identified by a Long Id.
 * <BR> <BR> In a particular Entity's Dao, we do the following to ensure a unique entity: <ol> <li>Use an entity that
 * looks like this: class UsernameLock { @Id String username; }</li> <li>Start a transaction</li> <li>get() the
 * UsernameLock - if one exists, you have a collision</li> <li>put() the UsernameLock</li> <li>Save Entity (e.g.,
 * User)</li> <li>commit() the transaction - if TX fails, you have a collision, otherwise the entity is saved and no
 * other entity can be created with the same "lock"</li> </ol> <br/> <br/> Note that the above method guarantees that
 * the entity and the Lock entity will be saved in the same transaction. This is not strictly necessary since the
 * EntityLock "get" operation in #3 above is "strongly-consistent". Because of this fact, the Entity does not need to be
 * stored in the same Datastore Transaction as the EntityLock (this is because we can use the App Engine Datastore as a
 * Mutex for any threads across multiple JVM's to guarantee that only one thread will succeed in storing an entity lock
 * across all App Engine server instances).<br/> <br/> Even though storing the Entity and the EntityLock in the same
 * Transaction is not required, it is preferable in the event that the Entity save fails. In this case, if Entity is
 * stored outside of the EntityLock transaction, it's possible to become stuck -- a retry of the Entity "save" will fail
 * because the EntityLock still exists.
 *
 * @author David Fuelling
 */
@com.googlecode.objectify.annotation.Entity @Unindex
// Required for Objectify Persistence
@lombok.Getter @lombok.Setter @lombok.ToString
public abstract class AbstractEntityStringLock extends AbstractObjectifyStringEntity<AbstractEntityStringLock>
{
	private static final long serialVersionUID = -7278141059335076657L;

	/**
	 * No-args constructor for Objectify
	 *
	 * @deprecated Use the constructor that takes a fieldValue instead.
	 */
	@Deprecated
	public AbstractEntityStringLock()
	{
	}

	/**
	 * Default Constructor.
	 *
	 * @param entityFieldValue The actual value (lower-case) of a Lock (e.g., "dfuelling" for a UsernameLock).
	 */
	public AbstractEntityStringLock(final String entityFieldValue)
	{
		super(entityFieldValue == null ? null : entityFieldValue.toLowerCase());
	}

	/**
	 * Returns the EntityFieldValue.
	 *
	 * @return
	 */
	public String getEntityFieldValue()
	{
		return getId();
	}

	/**
	 * Sets the LockValue for this String Lock (e.g., "dfuelling" for a UsernameLock).
	 */
	public void setEntityFieldValue(String entityFieldValue)
	{
		setId(entityFieldValue);
	}

}
