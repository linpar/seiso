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
package com.expedia.seiso.web.controller.v2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.LoadBalancerRepo;
import com.expedia.seiso.domain.repo.PersonRepo;
import com.expedia.seiso.domain.repo.ServiceRepo;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.controller.delegate.RepoSearchDelegate;
import com.expedia.seiso.web.hateoas.Resource;
import com.expedia.seiso.web.hateoas.PagedResources;

/**
 * @author Willie Wheeler
 */
public class RepoSearchControllerV2Tests {
	private static final String NONPAGING_REPO_KEY = "my-nonpaging-repo";
	private static final String PAGING_REPO_KEY = "my-paging-repo";
	private static final Class<?> NONPAGING_ITEM_CLASS = RotationStatus.class;
	private static final Class<?> PAGING_ITEM_CLASS = Service.class;
	
	private static final String SEARCH = "my-search";
	private static final String SEARCH_WITH_UNIQUE_RESULT = "my-search-with-unique-result";
	private static final String VIEW_KEY = Projection.DEFAULT;
	
	// Class under test
	@InjectMocks RepoSearchControllerV2 controller;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private RepoSearchDelegate delegate;
	
	// Test data
	@Mock private ItemMeta nonPagingItemMeta, pagingItemMeta;
	@Mock private Pageable pageable;
	@Mock private MultiValueMap<String, String> params;
	@Mock private ProjectionNode projection;
	@Mock private PagedResources itemResourcePage;
	@Mock private Resource itemResource, propResource, searchListResource;
	@Mock private PagedResources searchResultResourcePage;
	
	@Before
	public void init() {
		this.controller = new RepoSearchControllerV2();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() {
		when(nonPagingItemMeta.isPagingRepo()).thenReturn(false);
		when(pagingItemMeta.isPagingRepo()).thenReturn(true);
		
		val searchMethodWithListResult =
				ReflectionUtils.findMethod(LoadBalancerRepo.class, "findByDataCenterKey", String.class);
		assert(searchMethodWithListResult != null);
		when(nonPagingItemMeta.getRepositorySearchMethod(SEARCH)).thenReturn(searchMethodWithListResult);
		
		val searchMethodWithPageResult =
				ReflectionUtils.findMethod(PersonRepo.class, "findByLastName", String.class, Pageable.class);
		assert(searchMethodWithPageResult != null);
		when(pagingItemMeta.getRepositorySearchMethod(SEARCH)).thenReturn(searchMethodWithPageResult);
		
		val searchMethodWithUniqueResult =
				ReflectionUtils.findMethod(ServiceRepo.class, "findByName", String.class);
		assert(searchMethodWithUniqueResult != null);
		when(pagingItemMeta.getRepositorySearchMethod(SEARCH_WITH_UNIQUE_RESULT))
				.thenReturn(searchMethodWithUniqueResult);
	}
	
	private void initDependencies() {
		when(itemMetaLookup.getItemClass(NONPAGING_REPO_KEY)).thenReturn(NONPAGING_ITEM_CLASS);
		when(itemMetaLookup.getItemMeta(NONPAGING_ITEM_CLASS)).thenReturn(nonPagingItemMeta);
		
		when(itemMetaLookup.getItemClass(PAGING_REPO_KEY)).thenReturn(PAGING_ITEM_CLASS);
		when(itemMetaLookup.getItemMeta(PAGING_ITEM_CLASS)).thenReturn(pagingItemMeta);
		
		when(delegate.getRepoSearchList(ApiVersion.V2, PAGING_REPO_KEY))
				.thenReturn(searchListResource);
		
		when(delegate.repoSearch(ApiVersion.V2, PAGING_REPO_KEY, SEARCH, VIEW_KEY, pageable, params))
				.thenReturn(searchResultResourcePage);
	}
	
	@Test
	public void getRepoSearchList() {
		val result = controller.getRepoSearchList(PAGING_REPO_KEY);
		assertNotNull(result);
		assertSame(searchListResource, result);
	}
	
	@Test
	public void repoSearch_paging() {
		val result = controller.repoSearch(PAGING_REPO_KEY, SEARCH, VIEW_KEY, pageable, params);
		assertNotNull(result);
		assertTrue(result instanceof PagedResources);
		assertSame(searchResultResourcePage, result);
		verify(delegate).repoSearch(ApiVersion.V2, PAGING_REPO_KEY, SEARCH, VIEW_KEY, pageable, params);
	}
}
