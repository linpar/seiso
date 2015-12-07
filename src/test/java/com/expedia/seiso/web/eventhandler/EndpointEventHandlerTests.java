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

import static org.mockito.Mockito.verify;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.web.assembler.ServiceInstanceService;

/**
 * @author Willie Wheeler
 */
public class EndpointEventHandlerTests {
	@InjectMocks private EndpointEventHandler handler;
	
	// Dependencies
	@Mock private NodeRepo nodeRepo;
	@Mock private NodeIpAddressRepo nodeIpAddressRepo;
	@Mock private ServiceInstanceService serviceInstanceService;
	
	@Before
	public void setUp() {
		this.handler = new EndpointEventHandler();
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testHandleAfterSave() {
		val node = Node.builder().build();
		val nip = NodeIpAddress.builder().node(node).build();
		val endpoint = Endpoint.builder().ipAddress(nip).build();
		handler.handleAfterSave(endpoint);
		
		verify(serviceInstanceService).recalculateAggregateRotationStatus(nip);
		verify(nodeIpAddressRepo).save(nip);
		
		verify(serviceInstanceService).recalculateAggregateRotationStatus(node);
		verify(nodeRepo).save(node);
	}
}
