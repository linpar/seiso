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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import org.springframework.context.ApplicationContext;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.listener.ServiceInstancePortListener;
import com.expedia.seiso.domain.repo.EndpointRepo;

/**
 * @author Willie Wheeler
 */
public class ServiceInstancePortListenerTests {
	private static final int NUM_IP_ADDRESSES = 2;

	// Class under test
	@InjectMocks private ServiceInstancePortListener listener;

	// Dependencies
	@Mock private ApplicationContext appContext;
	@Mock private EndpointRepo endpointRepo;

	// Test data
	private List<Node> nodes;
	private ServiceInstancePort port;

	@Before
	public void init() throws Exception {
		this.listener = new ServiceInstancePortListener();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}

	private void initTestData() {
		this.nodes = new ArrayList<>();
		nodes.add(buildNode());
		nodes.add(buildNode());
		nodes.add(buildNode());

		this.port = new ServiceInstancePort().setServiceInstance(new ServiceInstance().setNodes(nodes));
	}

	private Node buildNode() {
		val ipAddresses = new ArrayList<NodeIpAddress>();
		for (int i = 0; i < NUM_IP_ADDRESSES; i++) {
			ipAddresses.add(new NodeIpAddress());
		}
		return new Node().setIpAddresses(ipAddresses);
	}

	private void initDependencies() {
		when(appContext.getBean(EndpointRepo.class)).thenReturn(endpointRepo);
	}

	@Test
	public void postPersist() {
		listener.postPersist(port);
		final int expectedNumEndpoints = nodes.size() * NUM_IP_ADDRESSES;
		verify(endpointRepo, times(expectedNumEndpoints)).save((Endpoint) anyObject());
	}

	@Test(expected = NullPointerException.class)
	public void postPersist_null() {
		listener.postPersist(null);
	}

	@Test
	public void postPersist_noNodes() {
		port.getServiceInstance().setNodes(new ArrayList<Node>());
		verify(endpointRepo, never()).save((Endpoint) anyObject());
	}
}
