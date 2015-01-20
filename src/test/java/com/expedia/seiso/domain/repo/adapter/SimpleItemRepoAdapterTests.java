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
package com.expedia.seiso.domain.repo.adapter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.ReflectionUtils;

import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.ServiceRepo;

/**
 * @author Willie Wheeler
 */
public class SimpleItemRepoAdapterTests {
	private static final String SERVICE_KEY = "my-service";
	private static final String NONEXISTENT_SERVICE_KEY = "nonexistent-service";
	
	// Class under test
	private SimpleItemRepoAdapter adapter;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private Repositories repositories;
	@Mock private ServiceRepo serviceRepo;
	
	// Test data
	private SimpleItemKey serviceKey, nonExistentServiceKey;
	@Mock private ItemMeta serviceItemMeta;
	@Mock private ItemMeta serviceInstancePortItemMeta;
	private Method findByKeyMethod;
	@Mock private Service service;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
		this.adapter = new SimpleItemRepoAdapter(itemMetaLookup, repositories);
	}
	
	private void setUpTestData() {
		this.serviceKey = new SimpleItemKey(Service.class, SERVICE_KEY);
		this.nonExistentServiceKey = new SimpleItemKey(Service.class, NONEXISTENT_SERVICE_KEY);
		
		this.findByKeyMethod = ReflectionUtils.findMethod(ServiceRepo.class, "findByKey", String.class);
		
		when(serviceItemMeta.getRepositoryFindByKeyMethod()).thenReturn(findByKeyMethod);
		when(serviceInstancePortItemMeta.getRepositoryFindByKeyMethod()).thenReturn(null);
	}
	
	private void setUpDependencies() {
		when(itemMetaLookup.getItemMeta(Service.class)).thenReturn(serviceItemMeta);
		when(itemMetaLookup.getItemMeta(ServiceInstancePort.class)).thenReturn(serviceInstancePortItemMeta);
		
		when(repositories.getRepositoryFor(Service.class)).thenReturn(serviceRepo);
		
		when(serviceRepo.findByKey(SERVICE_KEY)).thenReturn(service);
		when(serviceRepo.findByKey(NONEXISTENT_SERVICE_KEY)).thenReturn(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void init_nullItemMetaLookup() {
		new SimpleItemRepoAdapter(null, repositories);
	}
	
	@Test(expected = NullPointerException.class)
	public void init_nullRepositories() {
		new SimpleItemRepoAdapter(itemMetaLookup, null);
	}
	
	@Test
	public void supports_true() {
		val result = adapter.supports(Service.class);
		assertTrue(result);
	}
	
	@Test
	public void supports_false() {
		val result = adapter.supports(ServiceInstancePort.class);
		assertFalse(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void supports_nullItemClass() {
		adapter.supports(null);
	}
	
	@Test
	public void find() {
		val result = adapter.find(serviceKey);
		assertNotNull(result);
		
	}
	
	@Test
	public void find_nonExistent() {
		val result = adapter.find(nonExistentServiceKey);
		assertNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void find_nullItemKey() {
		adapter.find(null);
	}
	
	@Test
	public void delete() {
		adapter.delete(serviceKey);
		verify(serviceRepo).delete(service);
	}
	
	@Test
	public void delete_nonExistent() {
		adapter.delete(nonExistentServiceKey);
	}
	
	@Test(expected = NullPointerException.class)
	public void delete_nullItemKey() {
		adapter.delete(null);
	}
}
