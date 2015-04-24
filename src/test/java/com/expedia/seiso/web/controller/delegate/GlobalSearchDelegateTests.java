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
package com.expedia.seiso.web.controller.delegate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import com.expedia.rf.hmedia.Resource;
import com.expedia.seiso.domain.service.SearchEngine;
import com.expedia.seiso.domain.service.SearchResults;
import com.expedia.seiso.domain.service.search.SearchQuery;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ResourceAssembler;

/**
 * @author Willie Wheeler
 */
public class GlobalSearchDelegateTests {
	
	// Class under test
	@InjectMocks private GlobalSearchDelegate delegate;
	
	// Dependencies
	@Mock private SearchEngine searchEngine;
	@Mock private ResourceAssembler resourceAssembler;
	
	// Test data
	@Mock private SearchQuery query;
	@Mock private Pageable pageable;
	@Mock private SearchResults searchResults;
	@Mock private Resource searchResultsResource;
	
	@Before
	public void setUp() {
		this.delegate = new GlobalSearchDelegate();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}
	
	private void setUpTestData() {
	}
	
	private void setUpDependencies() {
		when(searchEngine.search(query, pageable)).thenReturn(searchResults);
		when(resourceAssembler.toGlobalSearchResource(ApiVersion.V2, searchResults)).thenReturn(searchResultsResource);
	}
	
	@Test
	public void globalSearch() {
		val result = delegate.globalSearch(ApiVersion.V2, query, pageable);
		assertNotNull(result);
		assertSame(searchResultsResource, result);
	}
	
	@Test(expected = NullPointerException.class)
	public void globalSearch_nullQuery() {
		delegate.globalSearch(ApiVersion.V2, null, pageable);
	}
	
	@Test(expected = NullPointerException.class)
	public void globalSearch_nullPageable() {
		delegate.globalSearch(ApiVersion.V2, query, null);
	}
}
