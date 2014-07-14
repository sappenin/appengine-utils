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
package com.sappenin.utils.appengine.data.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.google.appengine.api.datastore.Cursor;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * A Class that holds the results of any operation as well as a {@link Cursor}
 * object that callers can use for future interaction with the appengine
 * datastore, if any.
 */
@Data
@RequiredArgsConstructor
public class ResultWithCursor<R>
{
	// Can be of any type - single object, List, etc.
	@NonNull
	private R result;

	private Optional<Cursor> optCursor = Optional.absent();

	/**
	 * Constructor with extra params.
	 * 
	 * @param result
	 * @param optCursor
	 */
	public ResultWithCursor(final R result, final Optional<Cursor> optCursor)
	{
		Preconditions.checkNotNull(result);
		Preconditions.checkNotNull(optCursor);

		this.setResult(result);
		this.setOptCursor(optCursor);
	}

	/**
	 * Constructor with extra params.
	 * 
	 * @param result
	 * @param cursor
	 */
	public ResultWithCursor(final R result, final Cursor cursor)
	{
		this(result, Optional.fromNullable(cursor));
	}

	/**
	 * Helper function to set Cursors into this object.
	 * 
	 * @param cursor
	 */
	public void setCursor(Cursor cursor)
	{
		this.setOptCursor(Optional.fromNullable(cursor));
	}

}
