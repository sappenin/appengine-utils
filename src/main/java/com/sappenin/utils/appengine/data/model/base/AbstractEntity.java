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
package com.sappenin.utils.appengine.data.model.base;

import com.sappenin.utils.appengine.data.model.GaeEntity;
import com.sappenin.utils.appengine.data.model.TrackableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;

/**
 * An abstract base-class for all entity objects.
 *
 * @author David Fuelling
 */
@ToString
public abstract class AbstractEntity implements TrackableEntity, GaeEntity, Serializable
{
	private static final long serialVersionUID = -7325038507483787961L;

	@Getter
	@Setter
	private DateTime creationDateTime;

	@Getter
	@Setter
	private DateTime updateDateTime;

	/**
	 * Default Constructor (for Date initialization)
	 */
	public AbstractEntity()
	{
		// Use the Setter in case a particular class wants to override these
		// values for indexing
		setCreationDateTime(DateTime.now(DateTimeZone.UTC));
		setUpdateDateTime(DateTime.now(DateTimeZone.UTC));
	}

	/**
	 * A helper function to return the PrimaryKey Identifier of this object. This does not return a Typed or Raw Key,
	 * but instead returns a Long or a String that represents the Primary Key field for entities that implement this
	 * interface. <br/> <br/> NOTE: Implementations should override this function to return the proper Primary Key
	 * type.
	 *
	 * @return The PrimaryKey Id as a Long or a String.
	 */
	public abstract Object getId();

	@Override
	public int hashCode()
	{

		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getKey() == null) ? 0 : getKey().hashCode());
		return result;
	}

	/**
	 * Equals comparisons happen by comparing the Key.
	 */
	@Override
	public boolean equals(final Object obj)
	{

		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (this.getClass() != obj.getClass())
		{
			return false;
		}
		final AbstractEntity other = (AbstractEntity) obj;
		if (getKey() == null)
		{
			if (other.getKey() != null)
			{
				return false;
			}
		}
		else if (!getKey().equals(other.getKey()))
		{
			return false;
		}
		return true;
	}

}
