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
package com.expedia.seiso.web.hateoas.link;

import static org.junit.Assert.assertNotNull;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Environment;
import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.InfrastructureProvider;
import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.LoadBalancer;
import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.Region;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceGroup;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.ServiceType;
import com.expedia.seiso.domain.entity.StatusType;
import com.expedia.seiso.web.hateoas.link.ItemPaths;

/**
 * @author Willie Wheeler
 */
public class ItemPathsTests {
	private static final String KEY = "some-key";
	
	// Class under test
	@InjectMocks private ItemPaths itemPaths;

	// Dependencies

	// Test data
	private IpAddressRole ipAddressRole;
	private Node node;
	private NodeIpAddress nodeIpAddress;
	private ServiceInstance serviceInstance;
	private ServiceInstancePort serviceInstancePort;
	
	@Before
	public void init() {
		this.itemPaths = new ItemPaths();
		MockitoAnnotations.initMocks(this);
		initTestData();
	}
	
	private void initTestData() {
		this.serviceInstance = new ServiceInstance().setKey(KEY);
		this.ipAddressRole = new IpAddressRole().setServiceInstance(serviceInstance).setName(KEY);
		this.node = new Node().setName(KEY);
		this.nodeIpAddress = new NodeIpAddress().setNode(node).setIpAddress("1.2.3.4");
		this.serviceInstancePort = new ServiceInstancePort().setServiceInstance(serviceInstance).setNumber(8080);
	}

	@Test
	public void resolveDataCenter() {
		val item = new DataCenter().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveEndpoint() {
		val item = new Endpoint().setId(1L);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveEnvironment() {
		val item = new Environment().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveHealthStatus() {
		val item = new HealthStatus().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveInfrastructureProvider() {
		val item = new InfrastructureProvider().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveIpAddressRole() { assertNotNull(itemPaths.resolve(ipAddressRole)); }

	@Test
	public void resolveLoadBalancer() {
		val item = new LoadBalancer().setName(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveMachine() {
		val item = new Machine().setName(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveNode() { assertNotNull(itemPaths.resolve(node)); }
	
	@Test
	public void resolveNodeIpAddress() { assertNotNull(itemPaths.resolve(nodeIpAddress)); }
	
	@Test
	public void resolvePerson() {
		val item = new Person().setUsername(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveRegion() {
		val item = new Region().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveRotationStatus() {
		val item = new RotationStatus().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveService() {
		val item = new Service().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveServiceGroup() {
		val item = new ServiceGroup().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveServiceInstance() { assertNotNull(itemPaths.resolve(serviceInstance)); }
	
	@Test
	public void resolveServiceInstancePort() { assertNotNull(itemPaths.resolve(serviceInstancePort)); }
	
	@Test
	public void resolveServiceType() {
		val item = new ServiceType().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveStatusType() {
		val item = new StatusType().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test(expected = NullPointerException.class)
	public void resolve_null() { itemPaths.resolve(null); }
}
