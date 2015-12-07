/* 
 * Copyright 2013-2016 the original author or authors.
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
package com.expedia.seiso.web.eventhandler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.Domain;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.repo.EndpointRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.web.assembler.ServiceInstanceService;

/**
 * @author Willie Wheeler
 */
public class ServiceInstancePortEventHandlerTests {
	@InjectMocks private ServiceInstancePortEventHandler handler;
	
	// Dependencies
	@Mock private EndpointRepo endpointRepo;
	@Mock private RotationStatusRepo rotationStatusRepo;
	@Mock private ServiceInstanceService serviceInstanceService;
	
	// Test data
	@Mock private RotationStatus unknownRotationStatus;
	
	private Node node;
	private ServiceInstancePort sip;
	
	@Before
	public void setUp() {
		this.handler = new ServiceInstancePortEventHandler();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
		handler.postConstruct();
	}
	
	private void initTestData() {
		val nip = NodeIpAddress.builder().build();
		val nips = Collections.singletonList(nip);
		val node = Node.builder().ipAddresses(nips).build();
		val nodes = Collections.singletonList(node);
		val serviceInstance = ServiceInstance.builder().nodes(nodes).build();
		val sip = ServiceInstancePort.builder().serviceInstance(serviceInstance).build();
		
		this.node = node;
		this.sip = sip;
	}
	
	private void initDependencies() {
		when(rotationStatusRepo.findByKey(Domain.UNKNOWN_ROTATION_STATUS_KEY)).thenReturn(unknownRotationStatus);
	}
	
	@Test
	public void testHandleAfterCreate() {
		handler.handleAfterCreate(sip);
		verify(endpointRepo).save(any(Endpoint.class));
		verify(serviceInstanceService).recalculateAggregateRotationStatus(node);
	}
}
