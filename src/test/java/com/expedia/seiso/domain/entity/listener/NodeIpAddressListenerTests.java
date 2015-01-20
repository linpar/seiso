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
package com.expedia.seiso.domain.entity.listener;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.listener.NodeIpAddressListener;
import com.expedia.seiso.domain.repo.EndpointRepo;

public class NodeIpAddressListenerTests {

	// Class under test
	@InjectMocks
	private NodeIpAddressListener listener;

	// Dependencies
	@Mock
	private ApplicationContext appContext;
	@Mock
	private EndpointRepo endpointRepo;

	// Test data
	private List<ServiceInstancePort> ports;
	private NodeIpAddress nodeIpAddress;

	@Before
	public void setUp() throws Exception {
		this.listener = new NodeIpAddressListener();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}

	private void setUpTestData() {
		this.ports = new ArrayList<>();
		ports.add(new ServiceInstancePort());
		ports.add(new ServiceInstancePort());

		this.nodeIpAddress = new NodeIpAddress().setIpAddressRole(new IpAddressRole())
				.setNode(new Node().setServiceInstance(new ServiceInstance().setPorts(ports))).setIpAddress("1.1.1.1");
	}

	private void setUpDependencies() {
		when(appContext.getBean(EndpointRepo.class)).thenReturn(endpointRepo);
	}

	@Test
	public void postPersist() {
		listener.postPersist(nodeIpAddress);
		verify(endpointRepo, times(ports.size())).save((Endpoint) anyObject());
	}

	@Test(expected = NullPointerException.class)
	public void postPersist_null() {
		listener.postPersist(null);
	}
}
