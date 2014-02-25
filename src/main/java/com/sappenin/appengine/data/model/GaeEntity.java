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
/**
 * 
 */
package com.sappenin.appengine.data.model;

import java.io.Serializable;

import com.google.appengine.api.datastore.Key;

/**
 * An interface that defines functionality required for model objects to be compatible with the GAE Datastore.
 * 
 * @author dfuelling
 * 
 */
public interface GaeEntity extends Serializable
{
	/**
	 * Returns the {@link com.google.appengine.api.datastore.Key} for this Entity.
	 * 
	 * @return
	 */
	public Key getKey();
}
