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

import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.sappenin.utils.appengine.data.dao.exceptions.EntityLockCollision;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility service for helping with Objectify.
 *
 * @author David Fuelling
 */
@NoArgsConstructor
public class ObjectifyEntityLockHelper implements Serializable
{
	private static final long serialVersionUID = -7829184564473315853L;

	protected static final Logger logger = Logger.getLogger(ObjectifyEntityLockHelper.class.getName());

	// ////////////////////////////////////////////
	// String-based Locking Mechanism Functions
	// ////////////////////////////////////////////

	/**
	 * Checks for a Collision for a particular Lock Value. If none is found, this function will reserve a lock for a
	 * particular entity. Must be called inside of an existing Objectify transaction so that if a collision is
	 * detected,
	 * no parent caller will succeed accidentally. Additionally, if an exception is thrown, then the caller can
	 * rollback
	 * the entire transaction and no lock will be stored.
	 *
	 * @return
	 */
	public <L extends AbstractEntityStringLock> L reserveLockValue(final L lock) throws EntityLockCollision
	{
		Preconditions.checkNotNull(ObjectifyService.ofy().getTransaction(),
				"This function must be called inside an existing Objectify transaction.");
		Preconditions.checkArgument(ObjectifyService.ofy().getTransaction().isActive(),
				"This function must be called inside an existing Objectify transaction.");
		Preconditions.checkNotNull(lock);

		final Key<L> typedLockKey = com.googlecode.objectify.Key.<L>create(lock.getKey());
		try
		{
			if (ObjectifyService.ofy().load().key(typedLockKey).now() != null)
			{
				// There is a collision.
				throw new EntityLockCollision(String.format(
						"Unique Value Collision: The %s class does not allow two entities to share the same value "
								+ "(value= %s)", lock.getClass(), lock.getEntityFieldValue()));
			}
		}
		catch (final NotFoundException nfe)
		{
			// Do nothing - this is ok.
		}

		// Update the Lock
		ObjectifyService.ofy().save().entity(lock).now();

		return lock;
	}

	/**
	 * Safely deletes any AbstractEntityStringLock objects passed into this function.
	 */
	public void removeLocksSafely(final AbstractEntityStringLock... locks)
	{
		Preconditions.checkNotNull(ObjectifyService.ofy().getTransaction(),
				"This function must be called inside an existing Objectify transaction.");
		Preconditions.checkArgument(ObjectifyService.ofy().getTransaction().isActive(),
				"This function must be called inside an existing Objectify transaction.");
		Preconditions.checkNotNull(locks);

		try
		{
			// This can be async
			ObjectifyService.ofy().delete().entities(locks);
		}
		catch (final RuntimeException re)
		{
			logger.log(Level.SEVERE, String.format("Unable to safely remove locks: %s", locks), re);
		}
	}
}
