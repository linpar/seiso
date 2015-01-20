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
import static org.mockito.Mockito.when;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.key.ServiceInstancePortKey;
import com.expedia.seiso.domain.repo.ServiceInstancePortRepo;

/**
 * @author Willie Wheeler
 */
public class ServiceInstancePortRepoAdapterTests {
	private static final String SERVICE_INSTANCE_KEY = "my-si";
	private static final int SERVICE_INSTANCE_PORT_NUMBER = 8080;
	private static final int BAD_SERVICE_INSTANCE_PORT_NUMBER = 8443;
	
	// Class under test
	private ServiceInstancePortRepoAdapter adapter;
	
	// Dependencies
	@Mock private ServiceInstancePortRepo repo;
	
	// Test data
	private ServiceInstancePortKey sipKey, nonExistentSipKey;
	@Mock private ServiceInstancePort sip;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
		this.adapter = new ServiceInstancePortRepoAdapter(repo);
	}
	
	private void setUpTestData() {
		this.sipKey = new ServiceInstancePortKey(SERVICE_INSTANCE_KEY, SERVICE_INSTANCE_PORT_NUMBER);
		this.nonExistentSipKey = new ServiceInstancePortKey(SERVICE_INSTANCE_KEY, BAD_SERVICE_INSTANCE_PORT_NUMBER);
	}
	
	private void setUpDependencies() {
		when(repo.findByServiceInstanceKeyAndNumber(SERVICE_INSTANCE_KEY, SERVICE_INSTANCE_PORT_NUMBER))
				.thenReturn(sip);
		when(repo.findByServiceInstanceKeyAndNumber(SERVICE_INSTANCE_KEY, BAD_SERVICE_INSTANCE_PORT_NUMBER))
				.thenReturn(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void init_nullRepo() {
		new ServiceInstancePortRepoAdapter(null);
	}
	
	@Test
	public void supports_true() {
		assertTrue(adapter.supports(ServiceInstancePort.class));
	}
	
	@Test
	public void supports_false() {
		assertFalse(adapter.supports(ServiceInstance.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void supports_null() {
		adapter.supports(null);
	}
	
	@Test
	public void find() {
		val result = adapter.find(sipKey);
		assertNotNull(result);
	}
	
	@Test
	public void find_nonExistent() {
		val result = adapter.find(nonExistentSipKey);
		assertNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void find_null() {
		adapter.find(null);
	}
	
	@Test
	public void delete() {
		adapter.delete(sipKey);
	}
	
	@Test
	public void delete_nonExistent() {
		adapter.delete(nonExistentSipKey);
	}
	
	@Test(expected = NullPointerException.class)
	public void delete_null() {
		adapter.delete(null);
	}
}
