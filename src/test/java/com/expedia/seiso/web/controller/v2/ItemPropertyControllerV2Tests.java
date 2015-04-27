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
import com.expedia.seiso.web.PEResource;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.serf.hypermedia.PagedResources;
import com.expedia.serf.hypermedia.Resource;

/**
 * @author Willie Wheeler
 */
public class ItemPropertyControllerV2Tests {
	private static final String NONPAGING_REPO_KEY = "my-nonpaging-repo";
	private static final String PAGING_REPO_KEY = "my-paging-repo";
	private static final Class<?> NONPAGING_ITEM_CLASS = RotationStatus.class;
	private static final Class<?> PAGING_ITEM_CLASS = Service.class;
	
	private static final String ITEM_KEY = "my-item";
	private static final String PROP_KEY = "my-prop";
	private static final String VIEW_KEY = "my-view";
	
	// Class under test
	@InjectMocks private ItemPropertyControllerV2 controller;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private BasicItemDelegate delegate;
	
	// Test data
	@Mock private ItemMeta nonPagingItemMeta, pagingItemMeta;
	@Mock private Pageable pageable;
	@Mock private MultiValueMap<String, String> params;
	@Mock private PagedResources itemBaseResourcePage;
	@Mock private Resource itemBaseResource, propBaseResource;
	@Mock private PEResource itemPEResource;
	@Mock private Service service;
	@Mock private ItemKey itemKey;
	
	@Before
	public void setUp() {
		this.controller = new ItemPropertyControllerV2();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}
	
	private void setUpTestData() {
		when(nonPagingItemMeta.isPagingRepo()).thenReturn(false);
		when(pagingItemMeta.isPagingRepo()).thenReturn(true);
	}
	
	private void setUpDependencies() {
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
	
	@Test
	public void getProperty() {
		val result = controller.getProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, VIEW_KEY);
		assertNotNull(result);
		assertSame(propBaseResource, result);
	}
	
//	@Test
//	public void putProperty() {
//		controller.putProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, itemKey);
//		verify(delegate).putProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, itemKey);
//	}
//	
//	@Test
//	public void putProperty_null() {
//		controller.putProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, null);
//		verify(delegate).putProperty(PAGING_REPO_KEY, ITEM_KEY, PROP_KEY, null);
//	}
}
