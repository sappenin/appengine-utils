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
package com.sappenin.appengine.data.dao.base;

import com.sappenin.appengine.data.dao.ObjectifyStringDao;
import com.sappenin.appengine.data.model.base.AbstractObjectifyStringEntity;

/**
 * An abstract implementation of {@link ObjectifyStringDao}.
 * 
 * @author dfuelling
 */
public abstract class AbstractObjectifyStringDao<T extends AbstractObjectifyStringEntity<T>> extends
		AbstractObjectifyDao<T> implements ObjectifyStringDao<T>
{

	// ////////////////////////////////////////
	// Overrides
	// ////////////////////////////////////////

}