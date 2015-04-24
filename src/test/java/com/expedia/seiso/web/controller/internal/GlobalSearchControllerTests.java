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

import com.expedia.rf.hmedia.Resource;
import com.expedia.seiso.domain.service.search.SearchQuery;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.controller.delegate.GlobalSearchDelegate;

/**
 * @author Willie Wheeler
 */
public class GlobalSearchControllerTests {
	
	// Class under test
	@InjectMocks private GlobalSearchController controller;
	
	// Dependencies
	@Mock private GlobalSearchDelegate delegate;
	
	// Test data
	@Mock private Pageable pageable;
	@Mock private Resource searchResults;
	
	@Before
	public void setUp() {
		this.controller = new GlobalSearchController();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}
	
	private void setUpTestData() {
	}
	
	private void setUpDependencies() {
		when(delegate.globalSearch(eq(ApiVersion.V2), (SearchQuery) anyObject(), eq(pageable)))
				.thenReturn(searchResults);
	}
	
	@Test
	public void globalSearch() {
		val result = controller.globalSearch("seiso", pageable);
		assertNotNull(result);
		assertSame(searchResults, result);
	}
}
