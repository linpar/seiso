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
package com.expedia.seiso.web.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.core.exception.ResourceNotFoundException;
import com.expedia.seiso.domain.entity.LoadBalancer;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.key.IpAddressRoleKey;
import com.expedia.seiso.domain.entity.key.NodeIpAddressKey;
import com.expedia.seiso.domain.entity.key.ServiceInstancePortKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.RepoKeys;

/**
 * @author Willie Wheeler
 */
public class UriToItemKeyConverterTests {
	private static final String VERSION_URI = "https://seiso.example.com/v2";
	
	// Class under test
	@InjectMocks private UriToItemKeyConverter converter;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	
	@Before
	public void setUp() {
		this.converter = new UriToItemKeyConverter(VERSION_URI);
		MockitoAnnotations.initMocks(this);
		setUpDependencies();
		converter.postConstruct();
	}
	
	private void setUpDependencies() {
		when(itemMetaLookup.getItemClass(RepoKeys.LOAD_BALANCERS)).thenReturn(LoadBalancer.class);
		when(itemMetaLookup.getItemClass(RepoKeys.SERVICE_INSTANCES)).thenReturn(ServiceInstance.class);
	}
	
	@Test(expected = NullPointerException.class)
	public void init_nullVersionUri() {
		new UriToItemKeyConverter(null);
	}
	
	@Test
	public void convert_simple() {
		val uri = VERSION_URI + "/load-balancers/lb-1234";
		val result = converter.convert(uri);
		assertNotNull(result);
	}
	
	@Test
	public void convert_nodeIpAddressUri() {
		val uri = VERSION_URI + "/nodes/my-node/ip-addresses/1.2.3.4";
		val result = (NodeIpAddressKey) converter.convert(uri);
		assertNotNull(result);
		assertEquals("my-node", result.getNodeName());
		assertEquals("1.2.3.4", result.getIpAddress());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void convert_badNodeUri() {
		val uri = VERSION_URI + "/nodes/foo/bad-path/foo";
		converter.convert(uri);
	}
	
	@Test
	public void convert_ipAddressRoleUri() {
		val uri = VERSION_URI + "/service-instances/my-service-instance/ip-address-roles/default";
		val result = (IpAddressRoleKey) converter.convert(uri);
		assertNotNull(result);
		assertEquals("my-service-instance", result.getServiceInstanceKey());
		assertEquals("default", result.getName());
	}
	
	@Test
	public void convert_serviceInstancePortUri() {
		val uri = VERSION_URI + "/service-instances/my-service-instance/ports/8080";
		val result = (ServiceInstancePortKey) converter.convert(uri);
		assertNotNull(result);
		assertEquals("my-service-instance", result.getServiceInstanceKey());
		assertEquals(8080, result.getNumber().intValue());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void convert_badServiceInstanceUri() {
		val uri = VERSION_URI + "/service-instances/foo/bad-path/bar";
		converter.convert(uri);
	}
	
	@Test
	public void convert_null() {
		val result = converter.convert(null);
		assertNull(result);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void convert_noMatch() {
		converter.convert("foo");
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void convert_compound_noMatch() {
		val uri = VERSION_URI + "/bad-repo/foo/bar/baz";
		converter.convert(uri);
	}
}
