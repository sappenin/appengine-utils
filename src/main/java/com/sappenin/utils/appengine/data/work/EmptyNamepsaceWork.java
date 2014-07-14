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
package com.sappenin.utils.appengine.data.work;

import com.google.appengine.api.NamespaceManager;
import com.googlecode.objectify.Work;

/**
 * A Work<Void> method that sets the NamespaceManager to null, performs some work, and then resets the Namespace.
 *
 * @author David Fuelling
 */
public abstract class EmptyNamepsaceWork<R> implements Work<R>
{
	/**
	 * Execute a unit of work in the empty namespace.
	 *
	 * @param work An implementation of {@link Work<R>}.
	 *
	 * @return A result of type <R>
	 */
	public static <R> R execute(Work<R> work)
	{
		// User Keys are created independent of any Namespace!
		String preNamespace = NamespaceManager.get();
		NamespaceManager.set(null);
		R result = work.run();
		NamespaceManager.set(preNamespace);
		return result;
	}
}
