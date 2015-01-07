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
package com.expedia.seiso.web.controller.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import com.expedia.seiso.domain.service.search.SearchQuery;
import com.expedia.seiso.web.controller.ItemSearchDelegate;
import com.expedia.seiso.web.hateoas.BaseResource;

/**
 * @author Willie Wheeler
 */
public class SearchControllerTests {
	
	// Class under test
	@InjectMocks private SearchController controller;
	
	// Dependencies
	@Mock private ItemSearchDelegate itemSearchDelegate;
	
	// Test data
	@Mock private Pageable pageable;
	@Mock private BaseResource searchResults;
	
	@Before
	public void setUp() {
		this.controller = new SearchController();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() {
	}
	
	private void initDependencies() {
		when(itemSearchDelegate.globalSearch((SearchQuery) anyObject(), eq(pageable))).thenReturn(searchResults);
	}
	
	@Test
	public void globalSearch() {
		val result = controller.globalSearch("seiso", pageable);
		assertNotNull(result);
		assertSame(searchResults, result);
	}
}
