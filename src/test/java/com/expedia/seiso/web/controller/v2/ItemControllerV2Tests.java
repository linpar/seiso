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
import static org.mockito.Matchers.anyObject;
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

import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.seiso.web.hateoas.PEResource;
import com.expedia.seiso.web.hateoas.Resource;
import com.expedia.seiso.web.hateoas.PagedResources;

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
	
	// Class under test
	@InjectMocks private ItemControllerV2 controller;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private BasicItemDelegate delegate;
	
	// Test data
	@Mock private ItemMeta nonPagingItemMeta, pagingItemMeta;
	@Mock private Pageable pageable;
	@Mock private MultiValueMap<String, String> params;
	@Mock private ProjectionNode projection;
	@Mock private PagedResources itemBaseResourcePage;
	@Mock private Resource itemBaseResource, propBaseResource, searchListBaseResource;
	@Mock private PEResource itemPEResource;
	@Mock private Service service;
	@Mock private ItemKey itemKey;
	
	@Before
	public void init() {
		this.controller = new ItemControllerV2();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() {
		when(nonPagingItemMeta.isPagingRepo()).thenReturn(false);
		when(pagingItemMeta.isPagingRepo()).thenReturn(true);
	}
	
	private void initDependencies() {
		when(itemMetaLookup.getItemClass(NONPAGING_REPO_KEY)).thenReturn(NONPAGING_ITEM_CLASS);
		when(itemMetaLookup.getItemMeta(NONPAGING_ITEM_CLASS)).thenReturn(nonPagingItemMeta);
//		when(delegate.getAll(NONPAGING_REPO_KEY, VIEW_KEY)).thenReturn(itemResourceList);
		
		when(itemMetaLookup.getItemClass(PAGING_REPO_KEY)).thenReturn(PAGING_ITEM_CLASS);
		when(itemMetaLookup.getItemMeta(PAGING_ITEM_CLASS)).thenReturn(pagingItemMeta);
		when(delegate.getAll(ApiVersion.V2, PAGING_REPO_KEY, VIEW_KEY, pageable, params))
				.thenReturn(itemBaseResourcePage);
		
		when(delegate.getOne(ApiVersion.V2, PAGING_REPO_KEY, ITEM_KEY, VIEW_KEY))
				.thenReturn(itemBaseResource);
		when(delegate.getProperty(ApiVersion.V2, PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, VIEW_KEY))
				.thenReturn(propBaseResource);
		
		when(itemPEResource.getItem()).thenReturn(service);
	}
	
//	@Test
//	public void getAll_nonpaging() {
//		val result = controller.getAll(NONPAGING_REPO_KEY, VIEW_KEY, null, null);
//		assertNotNull(result);
//		assertSame(itemResourceList, result);
//		verify(delegate).getAll(NONPAGING_REPO_KEY, VIEW_KEY);
//	}
	
	@Test
	public void getAll_paging() {
		val result = controller.getAll(PAGING_REPO_KEY, VIEW_KEY, pageable, params);
		assertNotNull(result);
		assertSame(itemBaseResourcePage, result);
		verify(delegate).getAll(ApiVersion.V2, PAGING_REPO_KEY, VIEW_KEY, pageable, params);
	}
	
	@Test
	public void getOne() {
		val result = controller.getOne(PAGING_REPO_KEY, ITEM_KEY, VIEW_KEY);
		assertNotNull(result);
		assertSame(itemBaseResource, result);
	}
	
	@Test
	public void getProperty() {
		val result = controller.getProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, VIEW_KEY);
		assertNotNull(result);
		assertSame(propBaseResource, result);
	}
	
	@Test
	public void put() {
		controller.put(PAGING_REPO_KEY, ITEM_KEY, itemPEResource);
		verify(delegate).put(service, false);
	}
	
	@Test
	public void putProperty() {
		controller.putProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, itemKey);
		verify(delegate).putProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, itemKey);
	}
	
	@Test
	public void putProperty_null() {
		controller.putProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, null);
		verify(delegate).putProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, null);
	}
	
	@Test
	public void delete() {
		controller.delete(PAGING_REPO_KEY, ITEM_KEY);
		verify(delegate).delete((ItemKey) anyObject());
	}
}
