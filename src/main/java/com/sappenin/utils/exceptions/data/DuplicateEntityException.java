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
package com.sappenin.utils.exceptions.data;

/**
 * Thrown by the Dao layer whenever an entity already exists in the Datastore.
 *
 * @author David Fuelling
 */
public class DuplicateEntityException extends RuntimeException
{
	private static final long serialVersionUID = -2955834497991066159L;

	public DuplicateEntityException()
	{

		super();
	}

	public DuplicateEntityException(String message, Throwable cause)
	{

		super(message, cause);
	}

	public DuplicateEntityException(String message)
	{

		super(message);
	}

	public DuplicateEntityException(Throwable cause)
	{

		super(cause);
	}

}
