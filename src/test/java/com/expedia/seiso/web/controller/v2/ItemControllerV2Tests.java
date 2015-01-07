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

import java.util.ArrayList;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.LoadBalancerRepo;
import com.expedia.seiso.domain.repo.PersonRepo;
import com.expedia.seiso.domain.repo.ServiceRepo;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.controller.BasicItemDelegate;
import com.expedia.seiso.web.controller.ItemSearchDelegate;
import com.expedia.seiso.web.hateoas.BaseResource;
import com.expedia.seiso.web.hateoas.BaseResourcePage;

/**
 * @author Willie Wheeler
 */
public class ItemControllerV2Tests {
	private static final String NONPAGING_REPO_KEY = "my-nonpaging-repo";
	private static final String PAGING_REPO_KEY = "my-paging-repo";
	private static final Class<?> NONPAGING_ITEM_CLASS = RotationStatus.class;
	private static final Class<?> PAGING_ITEM_CLASS = Service.class;
	
	private static final String ITEM_KEY = "my-item";
	private static final String PROP_KEY = "my-prop";
	private static final String VIEW_KEY = "my-view";
	private static final String SEARCH = "my-search";
	private static final String SEARCH_WITH_UNIQUE_RESULT = "my-search-with-unique-result";
	
	// Class under test
	@InjectMocks private ItemControllerV2 controller;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private BasicItemDelegate basicItemDelegate;
	@Mock private ItemSearchDelegate itemSearchDelegate;
	
	// Test data
	@Mock private ItemMeta nonPagingItemMeta, pagingItemMeta;
	@Mock private Pageable pageable;
	@Mock private MultiValueMap<String, String> params;
	@Mock private ProjectionNode projection;
//	private List<BaseResource> itemDtoList;
	@Mock private BaseResourcePage itemDtoPage;
	@Mock private BaseResource itemDto, propDto, searchListDto;
	@Mock private BaseResource searchResultUnique;
	private List<BaseResource> searchResultDtoList;
	@Mock private BaseResourcePage searchResultDtoPage;
	
	@Before
	public void setUp() {
		this.controller = new ItemControllerV2();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() {
//		this.itemDtoList = new ArrayList<BaseResource>();
		this.searchResultDtoList = new ArrayList<BaseResource>();
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
//		when(basicItemDelegate.getAll(NONPAGING_REPO_KEY, VIEW_KEY)).thenReturn(itemDtoList);
		
		when(itemMetaLookup.getItemClass(PAGING_REPO_KEY)).thenReturn(PAGING_ITEM_CLASS);
		when(itemMetaLookup.getItemMeta(PAGING_ITEM_CLASS)).thenReturn(pagingItemMeta);
		when(basicItemDelegate.getAll(PAGING_REPO_KEY, VIEW_KEY, pageable, params)).thenReturn(itemDtoPage);
		
		when(basicItemDelegate.getOne(PAGING_REPO_KEY, ITEM_KEY, VIEW_KEY)).thenReturn(itemDto);
		when(basicItemDelegate.getProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, VIEW_KEY)).thenReturn(propDto);
		
		when(itemSearchDelegate.getRepoSearchList(PAGING_REPO_KEY)).thenReturn(searchListDto);
		
		when(itemSearchDelegate.repoSearch(NONPAGING_REPO_KEY, SEARCH)).thenReturn(searchResultDtoList);
		when(itemSearchDelegate.repoSearch(PAGING_REPO_KEY, SEARCH, pageable, params)).thenReturn(searchResultDtoPage);
		when(itemSearchDelegate.repoSearchUnique(PAGING_REPO_KEY, SEARCH_WITH_UNIQUE_RESULT))
				.thenReturn(searchResultUnique);
	}
	
//	@Test
//	public void getAll_nonpaging() {
//		val result = controller.getAll(NONPAGING_REPO_KEY, VIEW_KEY, null, null);
//		assertNotNull(result);
//		assertSame(itemDtoList, result);
//		verify(basicItemDelegate).getAll(NONPAGING_REPO_KEY, VIEW_KEY);
//	}
	
	@Test
	public void getAll_paging() {
		val result = controller.getAll(PAGING_REPO_KEY, VIEW_KEY, pageable, params);
		assertNotNull(result);
		assertSame(itemDtoPage, result);
		verify(basicItemDelegate).getAll(PAGING_REPO_KEY, VIEW_KEY, pageable, params);
	}
	
	@Test
	public void getOne() {
		val result = controller.getOne(PAGING_REPO_KEY, ITEM_KEY, VIEW_KEY);
		assertNotNull(result);
		assertSame(itemDto, result);
	}
	
	@Test
	public void getProperty() {
		val result = controller.getProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, VIEW_KEY);
		assertNotNull(result);
		assertSame(propDto, result);
	}
	
	@Test
	public void getSearchList() {
		val result = controller.getRepoSearchList(PAGING_REPO_KEY);
		assertNotNull(result);
		assertSame(searchListDto, result);
	}
	
	@Test
	public void search_uniqueResult() {
		val result = controller.repoSearch(PAGING_REPO_KEY, SEARCH_WITH_UNIQUE_RESULT, null, null);
		assertNotNull(result);
		assertTrue(result instanceof BaseResource);
		assertSame(searchResultUnique, result);
		verify(itemSearchDelegate).repoSearchUnique(PAGING_REPO_KEY, SEARCH_WITH_UNIQUE_RESULT);
	}
	
	@Test
	public void search_nonpaging() {
		val result = controller.repoSearch(NONPAGING_REPO_KEY, SEARCH, null, null);
		assertNotNull(result);
		assertTrue(result instanceof List);
		assertSame(searchResultDtoList, result);
		verify(itemSearchDelegate).repoSearch(NONPAGING_REPO_KEY, SEARCH);
	}
	
	@Test
	public void search_paging() {
		val result = controller.repoSearch(PAGING_REPO_KEY, SEARCH, pageable, params);
		assertNotNull(result);
		assertTrue(result instanceof BaseResourcePage);
		assertSame(searchResultDtoPage, result);
		verify(itemSearchDelegate).repoSearch(PAGING_REPO_KEY, SEARCH, pageable, params);
	}
}
