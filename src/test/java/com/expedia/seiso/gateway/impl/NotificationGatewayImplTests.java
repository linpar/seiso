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
package com.expedia.seiso.gateway.impl;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.AmqpTemplate;

import com.expedia.seiso.core.config.CustomProperties;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.gateway.model.ItemNotification;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.seiso.web.hateoas.Resource;

public class NotificationGatewayImplTests {

	// Class under test
	@InjectMocks private NotificationGatewayImpl gateway;

	// Dependencies
	@Mock private AmqpTemplate amqpTemplate;
	@Mock private ResourceAssembler itemAssembler;
	@Mock private CustomProperties customProperties;
	
	// Test data
	private Service service;
	private NodeIpAddress nip;
	@Mock private Resource itemResource;

	@Before
	public void setUp() throws Exception {
		this.gateway = new NotificationGatewayImpl();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}
	
	private void setUpTestData() {
		
		// @formatter:off
		this.service = new Service()
				.setKey("some-service");
		this.nip = new NodeIpAddress()
				.setNode(new Node().setName("some-node"))
				.setIpAddress("1.2.3.4");
		// @formatter:on
	}
	
	private void setUpDependencies() {
		when(itemAssembler.toResource(eq(ApiVersion.V2), (Item) anyObject(), (ProjectionNode) anyObject()))
				.thenReturn(itemResource);
		when(customProperties.getChangeNotificationExchange()).thenReturn("seiso.notifications");
	}

	@Test
	public void notify_createService() {
		gateway.notify(service, ItemNotification.OP_CREATE);
	}
	
	@Test
	public void notify_createNodeIpAddress() {
		gateway.notify(nip, ItemNotification.OP_CREATE);
	}
	
	@Test
	public void notify_updateService() {
		gateway.notify(service, ItemNotification.OP_UPDATE);
	}
	
	@Test
	public void notify_updateNodeIpAddress() {
		gateway.notify(nip, ItemNotification.OP_UPDATE);
	}
	
	@Test
	public void notify_deleteService() {
		gateway.notify(service, ItemNotification.OP_DELETE);
	}
	
	@Test
	public void notify_deleteNodeIpAddress() {
		gateway.notify(nip, ItemNotification.OP_DELETE);
	}
	
	@Test(expected = NullPointerException.class)
	public void notify_nullItem() {
		gateway.notify(null, ItemNotification.OP_CREATE);
	}
	
	@Test(expected = NullPointerException.class)
	public void notify_nullItemNotification() {
		gateway.notify(service, null);
	}
}
