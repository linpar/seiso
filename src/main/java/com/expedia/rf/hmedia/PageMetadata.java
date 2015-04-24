/* 
 * Copyright 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.rf.hmedia;

import static org.springframework.util.Assert.isTrue;
import lombok.Data;

/**
 * Resource page metadata, supporting user interface concerns such as pagination.
 * 
 * @author Willie Wheeler
 */
@Data
public class PageMetadata {
	private long pageSize;
	private long pageNumber;
	private long totalItems;
	
	/**
	 * Creates a new metadata object from the given arguments.
	 * 
	 * @param pageSize
	 *            maximum number of items per page
	 * @param pageNumber
	 *            0-indexed page number
	 * @param totalItems
	 *            total number of items in the result set
	 */
	public PageMetadata(long pageSize, long pageNumber, long totalItems) {
		isTrue(pageSize > 0L);
		isTrue(pageNumber >= 0L);
		isTrue(totalItems >= 0L);
		
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.totalItems = totalItems;
	}
	
	/**
	 * Calculates the total number of pages based on the page size and total number of items.
	 * 
	 * @return total number of pages
	 */
	public long getTotalPages() {
		long base = totalItems / pageSize;
		return (totalItems % pageSize == 0 ? base : base + 1);
	}
	
	public long getPageLowIndex() {
		long lowIndex = pageNumber * pageSize;
		return (lowIndex < totalItems ? lowIndex : -1);
	}
	
	public long getPageHighIndex() {
		long totalPages = getTotalPages();
		long lastPage = totalPages - 1;
		if (pageNumber < 0) {
			// This shouldn't happen, but just being paranoid.
			throw new IllegalStateException("pageNumber must be >= 0");
		} else if (pageNumber < lastPage) {
			return (pageNumber + 1) * pageSize - 1;
		} else if (pageNumber == lastPage) {
			long itemsOnLastPage = totalItems - lastPage * pageSize;
			return (pageNumber * pageSize) + (itemsOnLastPage - 1);
		} else {
			return -1;
		}
	}
}
