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

import java.util.logging.Logger;

import com.sappenin.appengine.data.dao.Dao;
import com.sappenin.appengine.data.model.base.AbstractEntity;

/**
 * An Abstract Dao class.
 * 
 * @author dfuelling
 */
public abstract class AbstractDao<T extends AbstractEntity> implements Dao<T>
{
	private final int maxObjectsPerDSList = 5000;

	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	// ////////////////////////////////////////
	// Abstract Functions
	// ////////////////////////////////////////

	@Override
	public abstract void save(final T entity);

	// //////////////////////////////////////////////
	// Helper code to load RIE list-based Entities (e.g., UserDaoObjectify).
	// //////////////////////////////////////////////

	/**
	 * A utility function that determines the pages to load. It takes a List of
	 * keys and, based upon the starting and ending page number, returns back a
	 * List of integers representing the first and last page.
	 * 
	 * @param pageKeys
	 * @param iStartingPageNumber
	 * @param iEndingPageNumber
	 */
	protected PageIndices determinePageIndices(final int iNumTotalEntries, final int iFirstResult, int iMaxResults)
	{
		// List 0 ==> [0]...[4999]
		// List 1 ==> [5000]....[9999]
		// etc.
		// So, to get the 5000th name, we need to be on list item 1.
		final PageIndices pageIndices = new PageIndices();

		// Adjust the maxResults number to not exceed the number of
		// page entries.
		if (iMaxResults > iNumTotalEntries)
		{
			iMaxResults = iNumTotalEntries;
		}
		// If the iFirstPage is greater than the number of pages in the Key
		// list, then
		if ((iFirstResult > iNumTotalEntries) || (iMaxResults == 0))
		{
			pageIndices.firstPage = -1;
			pageIndices.secondPage = -1;
			return pageIndices;
		}

		// E.g., 3 pages at 5,000 each ==> Max of 15,000 entries.
		// int maxNumEntries = iNumTotalEntries *
		// maxObjectsPerDSList;

		// If there are 15,000 entries and our first result is < 5000, then
		// we're in list 1 (e.g.). If we want item #5,000, then we're in
		// list #2.
		pageIndices.firstPage = (int) Math.floor(iFirstResult / (double) this.maxObjectsPerDSList);

		pageIndices.secondPage = (int) Math.floor((((iMaxResults + iFirstResult) - 1))
			/ (double) this.maxObjectsPerDSList);

		// We need to determine where to start/end in the first page, and
		// where to start/end in the second page.
		if (pageIndices.firstPage == pageIndices.secondPage)
		{
			// Calc the fistPageStartIdx
			pageIndices.firstPageStartIdx = iFirstResult % this.maxObjectsPerDSList;

			// Calc the firstPageEndIdx
			if (((iFirstResult + iMaxResults) > 0)
				&& (((iFirstResult + iMaxResults) % (this.maxObjectsPerDSList)) == 0))
			{
				pageIndices.firstPageEndIdx = this.maxObjectsPerDSList;
			}
			else
			{
				pageIndices.firstPageEndIdx = (iFirstResult + iMaxResults) % (this.maxObjectsPerDSList);
			}

			// Final Sanity check.
			final int cap = (iNumTotalEntries - (this.maxObjectsPerDSList * pageIndices.firstPage));
			if (pageIndices.firstPageEndIdx > cap)
			{
				pageIndices.firstPageEndIdx = cap;
			}
		}
		else
		{
			// This request spawns two or more pages...
			int iEntriesInFirstList = 0;
			int iEntriesInSecondList = 0;
			if (iMaxResults < 5001)
			{
				iEntriesInFirstList = (this.maxObjectsPerDSList * (pageIndices.firstPage + 1)) - iFirstResult;
				// This is simple once we know the number in the first
				// list...
				iEntriesInSecondList = iMaxResults - iEntriesInFirstList;

				// Final Sanity check.
				final int cap = (iNumTotalEntries - (this.maxObjectsPerDSList * pageIndices.secondPage));
				if (iEntriesInSecondList > cap)
				{
					iEntriesInSecondList = cap;
				}
			}
			else
			{
				throw new RuntimeException(
					"The Algorithm that this function uses does not support getting more than 5000 usernames at a time.  Please page this data instead in chunks of 5000 or less!");
			}

			// Get the ones from the firstPage.
			pageIndices.firstPageStartIdx = (iFirstResult - ((this.maxObjectsPerDSList * pageIndices.firstPage)));
			pageIndices.firstPageEndIdx = (iEntriesInFirstList + pageIndices.firstPageStartIdx);

			pageIndices.secondPageStartIdx = 0;
			pageIndices.secondPageEndIdx = iEntriesInSecondList;
		}

		return pageIndices;
	}

	// //////////////////////////////////
	// Helper Classes and Enums
	// //////////////////////////////////

	/**
	 * 
	 */
	public static enum SEARCH_SORT_DIRECTION
	{
		ASC, DESC;

		/**
		 * 
		 */
		@Override
		public String toString()
		{
			switch (this)
			{
				case ASC:
					return "ASC";
				case DESC:
					return "DESC";
				default:
					return "DESC";
			}
		}

		public SEARCH_SORT_DIRECTION fromString(final String enumString)
		{
			if ("ASC".equals(enumString))
			{
				return ASC;
			}
			else
			{
				return DESC;
			}
		}

	}

	/**
	 * An internal data structure that holds an index number for a series of two
	 * data pages to utilize.
	 */
	@lombok.Getter
	@lombok.Setter
	public class PageIndices
	{
		private int firstPage;
		private int secondPage;

		private int firstPageStartIdx;
		private int firstPageEndIdx;
		private int secondPageStartIdx;
		private int secondPageEndIdx;
	}

}
