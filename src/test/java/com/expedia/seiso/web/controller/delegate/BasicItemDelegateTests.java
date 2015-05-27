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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Validator;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.seiso.web.dto.v1.PEResources;
import com.expedia.serf.hypermedia.PagedResources;
import com.expedia.serf.hypermedia.Resource;
import com.expedia.serf.hypermedia.Resources;
import com.expedia.serf.util.SaveAllResult;

/**
 * @author Willie Wheeler
 */
public class BasicItemDelegateTests {
	private static final Class<?> NONPAGING_ITEM_CLASS = RotationStatus.class;
	private static final String NONPAGING_REPO_KEY = "my-nonpaging-repo";
	
	private static final Class<?> PAGING_ITEM_CLASS = Person.class;
	private static final String PAGING_REPO_KEY = "my-paging-repo";
	private static final String PAGING_ITEM_PROPERTY_KEY = "manager";
	private static final String PAGING_ITEM_PROPERTY_NAME = "manager";
	private static final String PAGING_LIST_PROPERTY_KEY = "direct-reports";
	private static final String PAGING_LIST_PROPERTY_NAME = "directReports";
	
	private static final String ITEM_KEY = "my-item";
	private static final String VIEW_KEY = "my-view";
	
	// Class under test
	@InjectMocks private BasicItemDelegate delegate;
	
	// Dependencies
	@Mock private Validator validator;
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private ItemService itemService;
	@Mock private ResourceAssembler resourceAssembler;
	
	// Test data
	@Mock private Pageable pageable;
	@Mock private MultiValueMap<String, String> params;
	@Mock private ItemMeta nonPagingRepoMeta;
	@Mock private ItemMeta pagingRepoMeta;
	@Mock private ProjectionNode projection;
	private Person socrates, plato, aristotle;
	@Mock private List<?> itemList;
	@Mock private Page<?> itemPage;
	@Mock private Resource resource;
	@Mock private Resources resources;
	@Mock private PagedResources pagedResources;
	@Mock private PEResources peResources;
	@Mock private ItemKey itemKey;
	@Mock private SaveAllResult saveAllResult;
	
	@Before
	public void setUp() {
		this.delegate = new BasicItemDelegate();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}
	
	private void setUpTestData() {
		when(nonPagingRepoMeta.isPagingRepo()).thenReturn(false);
		when(nonPagingRepoMeta.getProjectionNode(ApiVersion.V2, Projection.Cardinality.COLLECTION, VIEW_KEY)).thenReturn(projection);
		
		when(pagingRepoMeta.isPagingRepo()).thenReturn(true);
		when(pagingRepoMeta.getProjectionNode(ApiVersion.V2, Projection.Cardinality.COLLECTION, VIEW_KEY)).thenReturn(projection);
		when(pagingRepoMeta.getProjectionNode(ApiVersion.V2, Projection.Cardinality.SINGLE, VIEW_KEY)).thenReturn(projection);
		when(pagingRepoMeta.getPropertyName(PAGING_ITEM_PROPERTY_KEY)).thenReturn(PAGING_ITEM_PROPERTY_NAME);
		when(pagingRepoMeta.getPropertyName(PAGING_LIST_PROPERTY_KEY)).thenReturn(PAGING_LIST_PROPERTY_NAME);
		
		when(saveAllResult.getNumErrors()).thenReturn(0);
		
		// @formatter:off
		this.socrates = new Person()
				.setUsername("socrates");
		this.aristotle = new Person()
				.setUsername("aristotle");
		this.plato = new Person()
				.setUsername("plato")
				.setManager(socrates)
				.setDirectReports(Arrays.asList(aristotle));
		// @formatter:on
	}
	
	private void setUpDependencies() {
		when(itemMetaLookup.getItemClass(NONPAGING_REPO_KEY)).thenReturn(NONPAGING_ITEM_CLASS);
		when(itemMetaLookup.getItemClass(PAGING_REPO_KEY)).thenReturn(PAGING_ITEM_CLASS);
		when(itemMetaLookup.getItemMeta(NONPAGING_ITEM_CLASS)).thenReturn(nonPagingRepoMeta);
		when(itemMetaLookup.getItemMeta(PAGING_ITEM_CLASS)).thenReturn(pagingRepoMeta);
		
		when(itemService.saveAll((Class) anyObject(), (PEResources) anyObject(), anyBoolean()))
				.thenReturn(saveAllResult);
		when(itemService.findAll(NONPAGING_ITEM_CLASS)).thenReturn(itemList);
		when(itemService.findAll(PAGING_ITEM_CLASS, pageable)).thenReturn(itemPage);
		when(itemService.find((SimpleItemKey) anyObject())).thenReturn(plato);
		
		when(resourceAssembler.toResources(ApiVersion.V2, NONPAGING_ITEM_CLASS, itemList, projection))
				.thenReturn(resources);
		when(resourceAssembler.toPagedResources(ApiVersion.V2, PAGING_ITEM_CLASS, itemPage, projection, params))
				.thenReturn(pagedResources);
		when(resourceAssembler.toResource(ApiVersion.V2, socrates, projection)).thenReturn(resource);
		when(resourceAssembler.toResource(ApiVersion.V2, plato, projection, true)).thenReturn(resource);
	}
	
	@Test
	public void getAll_pageable() {
		val result = delegate.getAll(ApiVersion.V2, PAGING_REPO_KEY, VIEW_KEY, pageable, params);
		assertNotNull(result);
		assertSame(pagedResources, result);
	}
	
	@Test(expected = NullPointerException.class)
	public void getAll_pageable_nullRepo() {
		delegate.getAll(ApiVersion.V2, null, VIEW_KEY, pageable, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void getAll_pageable_nullView() {
		delegate.getAll(ApiVersion.V2, PAGING_REPO_KEY, null, pageable, params);
	}
	
	@Test
	public void getOne() {
		val result = delegate.getOne(ApiVersion.V2, PAGING_REPO_KEY, ITEM_KEY, VIEW_KEY);
		assertNotNull(result);
		assertSame(resource, result);
	}
	
	@Test(expected = NullPointerException.class)
	public void getOne_nullRepo() {
		delegate.getOne(ApiVersion.V2, null, ITEM_KEY, VIEW_KEY);
	}
	
	@Test(expected = NullPointerException.class)
	public void getOne_nullView() {
		delegate.getOne(ApiVersion.V2, PAGING_REPO_KEY, null, VIEW_KEY);
	}
	
	@Test
	public void getProperty_item() {
		val result = delegate.getProperty(ApiVersion.V2, PAGING_REPO_KEY, ITEM_KEY, PAGING_ITEM_PROPERTY_KEY, VIEW_KEY);
		assertNotNull(result);
	}
	
	@Test
	public void getProperty_list() {
		val result = delegate.getProperty(ApiVersion.V2, PAGING_REPO_KEY, ITEM_KEY, PAGING_LIST_PROPERTY_KEY, VIEW_KEY);
		assertNotNull(result);
	}
	
	@Test
	public void postAll() {
		delegate.postAll(Person.class, peResources, false);
		verify(itemService).saveAll(Person.class, peResources, false);
	}
	
	@Test
	public void put() {
		delegate.put(aristotle, false);
		verify(itemService).save(aristotle, false);
	}
	
//	@Test
//	public void postUriList() {
//		List<String> uriList = new ArrayList<>();
//		delegate.postUriList(
//				PAGING_REPO_KEY,
//				ITEM_KEY,
//				PAGING_ITEM_PROPERTY_KEY,
//				uriList);
//		// TODO
//	}
	
	@Test
	public void delete() {
		delegate.delete(itemKey);
		verify(itemService).delete(itemKey);
	}
	
	@Test(expected = NullPointerException.class)
	public void delete_null() {
		delegate.delete(null);
	}
}
