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
package com.expedia.seiso.web.controller.v1;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.controller.delegate.RepoSearchDelegate;
import com.expedia.serf.hypermedia.PagedResources;
import com.expedia.serf.hypermedia.Resource;

/**
 * @author Willie Wheeler
 */
public class RepoSearchControllerV1Tests {
	private static final String REPO_KEY = "foo";
	private static final String SEARCH = "bar";
	private static final String VIEW_KEY = Projection.DEFAULT;
	
	// Class under test
	@InjectMocks private RepoSearchControllerV1 controller;
	
	// Dependencies
	@Mock private RepoSearchDelegate delegate;
	@Mock private ResponseHeadersV1 responseHeaders;
	
	// Test data
	@Mock private Pageable pageable;
	@Mock private MultiValueMap<String, String> params;
	@Mock private Resource repoSearchListResource;
	@Mock private PagedResources repoSearchPageResource;
	@Mock private HttpHeaders httpHeaders;
	
	@Before
	public void setUp() {
		this.controller = new RepoSearchControllerV1();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}
	
	private void setUpTestData() {
	}
	
	private void setUpDependencies() {
		when(delegate.getRepoSearchList(ApiVersion.V1, REPO_KEY)).thenReturn(repoSearchListResource);
		when(delegate.repoSearch(ApiVersion.V1, REPO_KEY, SEARCH, VIEW_KEY, pageable, params))
				.thenReturn(repoSearchPageResource);
		when(responseHeaders.buildResponseHeaders(repoSearchPageResource)).thenReturn(httpHeaders);
	}
	
	@Test
	public void getRepoSearchList() {
		val result = controller.getRepoSearchList(REPO_KEY);
		assertNotNull(result);
		assertSame(repoSearchListResource, result);
		verify(delegate).getRepoSearchList(ApiVersion.V1, REPO_KEY);
	}
	
	@Test
	public void repoSearch() {
		val result = controller.repoSearch(REPO_KEY, SEARCH, VIEW_KEY, pageable, params);
		assertNotNull(result);
		verify(delegate).repoSearch(ApiVersion.V1, REPO_KEY, SEARCH, VIEW_KEY, pageable, params);
		verify(responseHeaders).buildResponseHeaders(repoSearchPageResource);
	}
}
