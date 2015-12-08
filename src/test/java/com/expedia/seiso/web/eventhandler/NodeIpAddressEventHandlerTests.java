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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

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
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.web.assembler.ServiceInstanceService;

/**
 * @author Willie Wheeler
 */
public class NodeIpAddressEventHandlerTests {
	@InjectMocks private NodeIpAddressEventHandler handler;
	
	// Dependencies
	@Mock private NodeRepo nodeRepo;
	@Mock private NodeIpAddressRepo nodeIpAddressRepo;
	@Mock private EndpointRepo endpointRepo;
	@Mock private RotationStatusRepo rotationStatusRepo;
	@Mock private ServiceInstanceService serviceInstanceService;
	
	// Test data
	@Mock private RotationStatus unknownRotationStatus;
	@Mock private RotationStatus someRotationStatus;
	
	private NodeIpAddress nip;
	private NodeIpAddress nipNullStatuses;
	private NodeIpAddress nipNonNullStatuses;
	
	@Before
	public void setUp() {
		this.handler = new NodeIpAddressEventHandler();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
		handler.postConstruct();
	}
	
	private void initTestData() {
		// @formatter:off
		val sips = new ArrayList<ServiceInstancePort>();
		sips.add(ServiceInstancePort.builder().build());
		sips.add(ServiceInstancePort.builder().build());
		sips.add(ServiceInstancePort.builder().build());
		
		val serviceInstance = ServiceInstance.builder()
				.ports(sips)
				.build();
		val node = Node.builder()
				.serviceInstance(serviceInstance)
				.build();
		
		this.nip = NodeIpAddress.builder()
				.node(node)
				.ipAddress("1.2.3.4")
				.endpoints(new ArrayList<Endpoint>())
				.build();
		this.nipNullStatuses = NodeIpAddress.builder().build();
		this.nipNonNullStatuses = NodeIpAddress.builder()
				.rotationStatus(someRotationStatus)
				.aggregateRotationStatus(someRotationStatus)
				.build();
		// @formatter:on
	}
	
	private void initDependencies() {
		when(rotationStatusRepo.findByKey(Domain.UNKNOWN_ROTATION_STATUS_KEY)).thenReturn(unknownRotationStatus);
	}
	
	@Test
	public void testHandleBeforeCreate_nullStatuses() {
		handler.handleBeforeCreate(nipNullStatuses);
		assertStatuses(nipNullStatuses, unknownRotationStatus);
	}
	
	@Test
	public void testHandleBeforeCreate_nonNullStatuses() {
		handler.handleBeforeCreate(nipNonNullStatuses);
		assertStatuses(nipNonNullStatuses, someRotationStatus);
	}
	
	@Test
	public void testHandleAfterCreate() {
		handler.handleAfterCreate(nip);
		
		val node = nip.getNode();
		val serviceInstance = node.getServiceInstance();
		val sips = serviceInstance.getPorts();
		
		verify(endpointRepo, times(sips.size())).save(any(Endpoint.class));
		
		verify(serviceInstanceService).recalculateAggregateRotationStatus(nip);
		verify(nodeIpAddressRepo).save(nip);
		
		verify(serviceInstanceService).recalculateAggregateRotationStatus(node);
		verify(nodeRepo).save(node);
	}
	
	@Test
	public void testHandleBeforeSave_nullStatuses() {
		handler.handleBeforeSave(nipNullStatuses);
		assertStatuses(nipNullStatuses, unknownRotationStatus);
	}
	
	@Test
	public void testHandleBeforeSave_nonNullStatuses() {
		handler.handleBeforeSave(nipNonNullStatuses);
		assertStatuses(nipNonNullStatuses, someRotationStatus);
	}
	
	@Test
	public void testHandleAfterSave() {		
		handler.handleAfterSave(nip);
		
		verify(serviceInstanceService).recalculateAggregateRotationStatus(nip);
		verify(nodeIpAddressRepo).save(nip);
		
		val node = nip.getNode();
		verify(serviceInstanceService).recalculateAggregateRotationStatus(node);
		verify(nodeRepo).save(node);
	}
	
	private void assertStatuses(NodeIpAddress nip, RotationStatus rotationStatus) {
		assertEquals(rotationStatus, nip.getRotationStatus());
		assertEquals(rotationStatus, nip.getAggregateRotationStatus());		
	}
}
