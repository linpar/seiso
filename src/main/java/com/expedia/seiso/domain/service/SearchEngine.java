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
package com.expedia.seiso.domain.service;

import org.springframework.data.domain.Pageable;

import com.expedia.seiso.domain.service.search.SearchQuery;

/**
 * Simple search engine interface.
 * 
 * @author Ken Van Eyk
 * @author Willie Wheeler
 */
public interface SearchEngine {

	/**
	 * Search the item repositories for items containing the given keywords.
	 * 
	 * @param tokenizedSearch
	 *            Search keywords.
	 * @param pageable
	 *            Page parameters applied on a <em>per-type</em> basis. The result set contains all results pulled from
	 *            the individual type-specific queries.
	 * @return search engine results
	 */
	SearchResults search(SearchQuery tokenizedSearch, Pageable pageable);
}
