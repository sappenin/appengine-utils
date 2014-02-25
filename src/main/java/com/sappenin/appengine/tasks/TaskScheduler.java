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
package com.sappenin.appengine.tasks;

/**
 * Interface for task scheduling logic for Google Appengine. Type
 * <P>
 * defines a particular Payload object that the schedule() function can use.
 */
public interface TaskScheduler<P>
{
	/**
	 * Schedules a particular task for the TaskQueue system on Google Appengine.
	 * 
	 * @param req
	 * @param payload
	 * @throws Exception
	 */
	public void schedule(final P payload) throws Exception;

}
