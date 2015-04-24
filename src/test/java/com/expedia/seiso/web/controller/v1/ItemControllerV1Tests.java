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
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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

import com.expedia.rf.hmedia.PEResource;
import com.expedia.rf.hmedia.PEResources;
import com.expedia.rf.hmedia.PagedResources;
import com.expedia.rf.hmedia.Resource;
import com.expedia.rf.hmedia.Resources;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.SaveAllResponse;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;

/**
 * @author Willie Wheeler
 */
public class ItemControllerV1Tests {
	private static final String CRUD_REPO_KEY = "foo";
	private static final String PAGING_REPO_KEY = "bar";
	private static final String ITEM_KEY = "baz";
	private static final String PROP_KEY = "qux";
	private static final String VIEW_KEY = "quux";

	// Class under test
	@InjectMocks private ItemControllerV1 controller;

	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private BasicItemDelegate delegate;
	@Mock private ResponseHeadersV1 responseHeaders;

	// Test data - requests
	@Mock private Pageable pageable;
	@Mock private MultiValueMap<String, String> params;
	@Mock private PEResources peResourceList;
	@Mock private PEResource peResource;
	@Mock private Item item;
	
	// Test data - responses
	@Mock private Resources resources;
	@Mock private PagedResources pagedResources;
	@Mock private Resource resource;
	@Mock private SaveAllResponse saveAllResponse;
	
	@Before
	public void setUp() {
		this.controller = new ItemControllerV1();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}

	private void setUpTestData() {
		when(peResource.getItem()).thenReturn(item);
	}

	private void setUpDependencies() {
		when(itemMetaLookup.getItemClass(anyString())).thenReturn(Service.class);
		
		when(delegate.getAll(eq(ApiVersion.V1), eq(CRUD_REPO_KEY), anyString(), eq(pageable), eq(params)))
				.thenReturn(resources);
		when(delegate.getAll(eq(ApiVersion.V1), eq(PAGING_REPO_KEY), anyString(), eq(pageable), eq(params)))
				.thenReturn(pagedResources);
		when(delegate.getOne(eq(ApiVersion.V1), anyString(), anyString(), anyString()))
				.thenReturn(resource);
		when(delegate.getProperty(ApiVersion.V1, CRUD_REPO_KEY, ITEM_KEY, PROP_KEY, VIEW_KEY))
				.thenReturn(resource);
		when(delegate.postAll(Service.class, peResourceList, true)).thenReturn(saveAllResponse);
	}
	
	@Test
	public void getAll_crudRepo() {
		val result = controller.getAll(CRUD_REPO_KEY, VIEW_KEY, pageable, params);
		assertNotNull(result);
		verify(delegate).getAll(ApiVersion.V1, CRUD_REPO_KEY, VIEW_KEY, pageable, params);
	}
	
	@Test
	public void getAll_pagingRepo() {
		val result = controller.getAll(PAGING_REPO_KEY, VIEW_KEY, pageable, params);
		assertNotNull(result);
		verify(delegate).getAll(ApiVersion.V1, PAGING_REPO_KEY, VIEW_KEY, pageable, params);
	}
	
	@Test
	public void getOne() {
		val result = controller.getOne(CRUD_REPO_KEY, ITEM_KEY, VIEW_KEY);
		assertNotNull(result);
		assertSame(resource, result);
		verify(delegate).getOne(ApiVersion.V1, CRUD_REPO_KEY, ITEM_KEY, VIEW_KEY);
		
	}
	
	@Test
	public void getProperty() {
		val result = controller.getProperty(CRUD_REPO_KEY, ITEM_KEY, PROP_KEY, VIEW_KEY);
		assertNotNull(result);
		verify(delegate).getProperty(ApiVersion.V1, CRUD_REPO_KEY, ITEM_KEY, PROP_KEY, VIEW_KEY);
	}
	
	@Test
	public void postAll() {
		val result = controller.postAll(CRUD_REPO_KEY, peResourceList);
		assertNotNull(result);
		assertSame(saveAllResponse, result);
	}
	
	@Test
	public void put() {
		controller.put(CRUD_REPO_KEY, ITEM_KEY, peResource);
		verify(delegate).put(item, true);
	}
	
	@Test
	public void delete() {
		controller.delete(CRUD_REPO_KEY, ITEM_KEY);
		verify(delegate).delete((ItemKey) anyObject());
	}
}
