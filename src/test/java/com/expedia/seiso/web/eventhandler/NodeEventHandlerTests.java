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
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.Domain;
import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.repo.HealthStatusRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;

/**
 * @author Willie Wheeler
 */
public class NodeEventHandlerTests {
	@InjectMocks private NodeEventHandler handler;
	
	// Dependencies
	@Mock private HealthStatusRepo healthStatusRepo;
	@Mock private RotationStatusRepo rotationStatusRepo;
	
	// Test data
	@Mock private HealthStatus unknownHealthStatus;
	@Mock private HealthStatus someHealthStatus;
	@Mock private RotationStatus unknownRotationStatus;
	@Mock private RotationStatus someRotationStatus;
	
	private Node nodeWithNullStatuses;
	private Node nodeWithNonNullStatuses;
	
	@Before
	public void setUp() {
		this.handler = new NodeEventHandler();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
		handler.postConstruct();
	}
	
	private void initTestData() {
		// @formatter:off
		this.nodeWithNullStatuses = Node.builder().build();
		this.nodeWithNonNullStatuses = Node.builder()
				.healthStatus(someHealthStatus)
				.aggregateRotationStatus(someRotationStatus)
				.build();
		// @formatter:on
	}
	
	private void initDependencies() {
		when(healthStatusRepo.findByKey(Domain.UNKNOWN_HEALTH_STATUS_KEY)).thenReturn(unknownHealthStatus);
		when(rotationStatusRepo.findByKey(Domain.UNKNOWN_ROTATION_STATUS_KEY)).thenReturn(unknownRotationStatus);
	}
	
	@Test
	public void testHandleBeforeCreate_nullStatuses() {
		handler.handleBeforeCreate(nodeWithNullStatuses);
		assertStatuses(nodeWithNullStatuses, unknownHealthStatus, unknownRotationStatus);
	}
	
	@Test
	public void testHandleBeforeCreate_nonNullStatuses() {
		handler.handleBeforeCreate(nodeWithNonNullStatuses);
		assertStatuses(nodeWithNonNullStatuses, someHealthStatus, someRotationStatus);
	}
	
	@Test
	public void testHandleBeforeSave_nullStatuses() {
		handler.handleBeforeSave(nodeWithNullStatuses);
		assertStatuses(nodeWithNullStatuses, unknownHealthStatus, unknownRotationStatus);
	}
	
	@Test
	public void testHandleBeforeSave_nonNullStatuses() {
		handler.handleBeforeSave(nodeWithNonNullStatuses);
		assertStatuses(nodeWithNonNullStatuses, someHealthStatus, someRotationStatus);
	}
	
	private void assertStatuses(Node node, HealthStatus healthStatus, RotationStatus rotationStatus) {
		assertEquals(healthStatus, node.getHealthStatus());
		assertEquals(rotationStatus, node.getAggregateRotationStatus());		
	}
}
