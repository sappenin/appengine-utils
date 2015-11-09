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
package com.sappenin.utils.appengine;

import com.google.appengine.api.NamespaceManager;
import com.google.common.base.Preconditions;

/**
 * A class for performing work in a particular Namespace, and then resetting the {@link NamespaceManager} to the
 * previous namespace.
 */
public abstract class NamespaceWork<R>
{
	public static final String NO_NAMESPACE = null;

	// The namespace to perform work in.
	protected final String namespace;

	/**
	 * No-args Constructor.
	 */
	public NamespaceWork()
	{
		this.namespace = NO_NAMESPACE;
	}

	/**
	 * Required-args Constructor
	 *
	 * @param namespace The namespace to perform this work in.
	 */
	public NamespaceWork(final String namespace)
	{
		this.namespace = Preconditions.checkNotNull(namespace);
	}

	/**
	 * Execute this unit of work in the new namespace, and then restore the previous namespace after everything has
	 * finished.
	 *
	 * @return
	 */
	public final R run()
	{
		final String originalNamespace = NamespaceManager.get();
		try
		{
			NamespaceManager.set(namespace);
			return runInNamespace();
		}
		finally
		{
			NamespaceManager.set(originalNamespace);
		}
	}

	/**
	 * An abstract method that allows implementations to perform work inside of a specified namespace.
	 *
	 * @return
	 */
	protected abstract R runInNamespace();
}
