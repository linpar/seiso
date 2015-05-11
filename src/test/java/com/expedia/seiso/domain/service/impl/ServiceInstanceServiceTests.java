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
package com.expedia.seiso.domain.service.impl;

import static com.expedia.seiso.domain.entity.RotationStatus.DISABLED;
import static com.expedia.seiso.domain.entity.RotationStatus.ENABLED;
import static com.expedia.seiso.domain.entity.RotationStatus.EXCLUDED;
import static com.expedia.seiso.domain.entity.RotationStatus.NO_ENDPOINTS;
import static com.expedia.seiso.domain.entity.RotationStatus.PARTIAL;
import static com.expedia.seiso.domain.entity.RotationStatus.UNKNOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.dto.BreakdownItem;
import com.expedia.seiso.domain.dto.NodeSummary;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.domain.repo.ServiceInstanceRepo;

/**
 * @author Willie Wheeler
 */
public class ServiceInstanceServiceTests {
	
	// Class under test
	@InjectMocks private ServiceInstanceServiceImpl service;
	
	// Dependencies
	@Mock private ServiceInstanceRepo serviceInstanceRepo;
	@Mock private RotationStatusRepo rotationStatusRepo;
	
	// Test data
	@Mock private NodeSummary nodeSummary;
	private List<BreakdownItem> healthBreakdown;
	private List<BreakdownItem> rotationBreakdown;
	
	@Before
	public void setUp() {
		this.service = new ServiceInstanceServiceImpl();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}
	
	private void setUpTestData() {
		this.healthBreakdown = new ArrayList<>();
		this.rotationBreakdown = new ArrayList<>();
	}
	
	private void setUpDependencies() {
		setUpRotationStatus(RotationStatus.ENABLED);
		setUpRotationStatus(RotationStatus.DISABLED);
		setUpRotationStatus(RotationStatus.EXCLUDED);
		setUpRotationStatus(RotationStatus.NO_ENDPOINTS);
		setUpRotationStatus(RotationStatus.PARTIAL);
		setUpRotationStatus(RotationStatus.UNKNOWN);
		
		when(serviceInstanceRepo.getServiceInstanceNodeSummary(anyString()))
				.thenReturn(nodeSummary);
		when(serviceInstanceRepo.getServiceInstanceHealthBreakdown(anyString()))
				.thenReturn(healthBreakdown);
		when(serviceInstanceRepo.getServiceInstanceRotationBreakdown(anyString()))
				.thenReturn(rotationBreakdown);
	}
	
	private void setUpRotationStatus(RotationStatus status) {
		when(rotationStatusRepo.findByKey(status.getKey())).thenReturn(status);
	}
	
	@Test
	public void testGetNodeSummary() {
		val actualResult = service.getNodeSummary("foo");
		assertNotNull(actualResult);
		assertSame(nodeSummary, actualResult);
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetNodeSummary_nullKey() {
		service.getNodeSummary(null);
	}
	
	@Test
	public void testGetHealthBreakdown() {
		val actualResult = service.getHealthBreakdown("foo");
		assertNotNull(actualResult);
		assertSame(healthBreakdown, actualResult);
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetHealthBreakdown_nullKey() {
		service.getHealthBreakdown(null);
	}
	
	@Test
	public void testGetRotationBreakdown() {
		val actualResult = service.getRotationBreakdown("foo");
		assertNotNull(actualResult);
		assertSame(rotationBreakdown, actualResult);
	}
	
	@Test(expected = NullPointerException.class)
	public void testRotationBreakdown_nullKey() {
		service.getRotationBreakdown(null);
	}
	
	@Test
	public void testRecalculateAggregateRotationStatus_node() {
		doTestRecalcNode(NO_ENDPOINTS);
		doTestRecalcNode(NO_ENDPOINTS, NO_ENDPOINTS);
		doTestRecalcNode(NO_ENDPOINTS, NO_ENDPOINTS, NO_ENDPOINTS);
		doTestRecalcNode(NO_ENDPOINTS, NO_ENDPOINTS);
		doTestRecalcNode(NO_ENDPOINTS, NO_ENDPOINTS, NO_ENDPOINTS);
		
		doTestRecalcNode(ENABLED, ENABLED);
		doTestRecalcNode(ENABLED, ENABLED, ENABLED);
		
		doTestRecalcNode(DISABLED, DISABLED);
		doTestRecalcNode(DISABLED, DISABLED, DISABLED);
		
		doTestRecalcNode(EXCLUDED, EXCLUDED);
		doTestRecalcNode(EXCLUDED, EXCLUDED, EXCLUDED);
		
		doTestRecalcNode(PARTIAL, PARTIAL);
		doTestRecalcNode(PARTIAL, PARTIAL, PARTIAL);
		doTestRecalcNode(PARTIAL, ENABLED, DISABLED);
		doTestRecalcNode(PARTIAL, ENABLED, PARTIAL);
		doTestRecalcNode(PARTIAL, PARTIAL, UNKNOWN);
		
		doTestRecalcNode(UNKNOWN, UNKNOWN);
		doTestRecalcNode(UNKNOWN, UNKNOWN, UNKNOWN);
		doTestRecalcNode(UNKNOWN, DISABLED, UNKNOWN);
	}
	
	@Test(expected = NullPointerException.class)
	public void testRecalculateAggregateRotationStatus_nullNode() {
		service.recalculateAggregateRotationStatus((Node) null);
	}
	
	@Test
	public void testRecalculateAggregateRotationStatus_nip() {
		doTestRecalcNip(NO_ENDPOINTS, ENABLED);
		doTestRecalcNip(NO_ENDPOINTS, DISABLED);
		doTestRecalcNip(NO_ENDPOINTS, UNKNOWN);
		
		doTestRecalcNip(ENABLED, ENABLED, ENABLED);
		doTestRecalcNip(ENABLED, ENABLED, ENABLED, ENABLED);
		
		doTestRecalcNip(DISABLED, ENABLED, DISABLED);
		doTestRecalcNip(DISABLED, ENABLED, DISABLED, DISABLED);
		doTestRecalcNip(DISABLED, DISABLED, ENABLED);
		doTestRecalcNip(DISABLED, DISABLED, DISABLED);
		doTestRecalcNip(DISABLED, DISABLED, ENABLED, ENABLED);
		doTestRecalcNip(DISABLED, DISABLED, ENABLED, DISABLED);
		doTestRecalcNip(DISABLED, DISABLED, DISABLED, DISABLED);
		
		doTestRecalcNip(EXCLUDED, ENABLED, EXCLUDED);
		doTestRecalcNip(EXCLUDED, ENABLED, EXCLUDED, EXCLUDED);
		doTestRecalcNip(EXCLUDED, EXCLUDED, ENABLED);
		doTestRecalcNip(EXCLUDED, EXCLUDED, DISABLED);
		doTestRecalcNip(EXCLUDED, EXCLUDED, ENABLED, ENABLED);
		doTestRecalcNip(EXCLUDED, EXCLUDED, ENABLED, DISABLED);
		doTestRecalcNip(EXCLUDED, EXCLUDED, DISABLED, DISABLED);
		doTestRecalcNip(EXCLUDED, UNKNOWN, EXCLUDED);
		
		doTestRecalcNip(PARTIAL, ENABLED, ENABLED, DISABLED);
		doTestRecalcNip(PARTIAL, ENABLED, ENABLED, PARTIAL);
		
		doTestRecalcNip(UNKNOWN, UNKNOWN, UNKNOWN);
		doTestRecalcNip(UNKNOWN, UNKNOWN, ENABLED);
		doTestRecalcNip(UNKNOWN, UNKNOWN, ENABLED, DISABLED);
		doTestRecalcNip(UNKNOWN, UNKNOWN, DISABLED);
		doTestRecalcNip(UNKNOWN, ENABLED, UNKNOWN);
	}
	
	@Test(expected = NullPointerException.class)
	public void testRecalculateAggregateRotationStatus_nullNip() {
		service.recalculateAggregateRotationStatus((NodeIpAddress) null);
	}
	
	/**
	 * @param expectedStatus
	 *            Expected node aggregate rotation status, given the NIP statuses
	 * @param nipStatuses
	 *            Node IP address statuses
	 */
	private void doTestRecalcNode(RotationStatus expectedStatus, RotationStatus... nipStatuses) {
		Node node = node(nipStatuses);
		service.recalculateAggregateRotationStatus(node);
		assertEquals(expectedStatus, node.getAggregateRotationStatus());
	}
	
	private void doTestRecalcNip(
			RotationStatus expectedStatus,
			RotationStatus nipStatus,
			RotationStatus... endpointStatuses) {
		
		NodeIpAddress nip = nodeIpAddress(nipStatus, endpointStatuses);
		service.recalculateAggregateRotationStatus(nip);
		assertEquals(expectedStatus, nip.getAggregateRotationStatus());
	}
	
	private Node node(RotationStatus... nipStatuses) {
		Node node = new Node();
		
		List<NodeIpAddress> nips = new ArrayList<>();
		for (RotationStatus status : nipStatuses) {
			NodeIpAddress nip = new NodeIpAddress();
			nip.setAggregateRotationStatus(status);
			nips.add(nip);
		}
		
		node.setIpAddresses(nips);
		return node;
	}
	
	private NodeIpAddress nodeIpAddress(RotationStatus nipStatus, RotationStatus... endpointStatuses) {
		NodeIpAddress nip = new NodeIpAddress();
		nip.setRotationStatus(nipStatus);
		
		List<Endpoint> endpoints = new ArrayList<>();
		for (RotationStatus status : endpointStatuses) {
			Endpoint endpoint = new Endpoint();
			endpoint.setRotationStatus(status);
			endpoints.add(endpoint);
		}
		
		nip.setEndpoints(endpoints);
		return nip;
	}
}
