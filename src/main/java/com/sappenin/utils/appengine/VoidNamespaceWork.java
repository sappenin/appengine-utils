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

/**
 * A class for performing work in a particular Namespace, and then resetting the {@link NamespaceManager} to the
 * previous namespace.
 */
public abstract class VoidNamespaceWork extends NamespaceWork<Void>
{
	/**
	 * No-args Constructor.
	 */
	public VoidNamespaceWork()
	{
	}

	/**
	 * Required-args Constructor
	 *
	 * @param namespace The namespace to perform this work in.
	 */
	public VoidNamespaceWork(final String namespace)
	{
		super(namespace);
	}

	/**
	 * An abstract method that allows implementations to perform work inside of a specified namespace.
	 *
	 * @return
	 */
	protected final Void runInNamespace()
	{
		this.vRunInNamespace();
		return null;
	}

	/**
	 * An abstract method that allows implementations to perform work inside of a specified namespace.
	 *
	 * @return
	 */
	protected abstract void vRunInNamespace();
}
