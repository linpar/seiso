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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.AmqpTemplate;

import com.expedia.seiso.gateway.model.BulkNodeActionRequest;

/**
 * @author Willie Wheeler
 */
public class ActionGatewayImplTests {

	// Class under test
	@InjectMocks
	private ActionGatewayImpl gateway;

	// Dependencies
	@Mock
	private AmqpTemplate amqpTemplate;

	// Test data
	@Mock
	private BulkNodeActionRequest request;

	@Before
	public void setUp() throws Exception {
		this.gateway = new ActionGatewayImpl();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}

	private void setUpTestData() {
	}

	private void setUpDependencies() {
	}

	@Test
	public void publish() {
		gateway.publish(request);
		verify(amqpTemplate).convertAndSend(anyString(), anyString(), eq(request));
	}

	@Test(expected = NullPointerException.class)
	public void publish_null() {
		gateway.publish(null);
	}
}
