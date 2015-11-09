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
package com.sappenin.utils.appengine.data.dao.exceptions;

/**
 * Thrown if an EntityLock is violated (Generally indicates that a particular field-value has already been locked).
 *
 * @author David Fuelling
 */
public class EntityLockCollision extends RuntimeException
{

	private static final long serialVersionUID = -7032656943142267322L;

	/**
	 * @param msg
	 */
	public EntityLockCollision(String msg)
	{
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public EntityLockCollision(String msg, Throwable cause)
	{
		super(msg, cause);
	}

}
