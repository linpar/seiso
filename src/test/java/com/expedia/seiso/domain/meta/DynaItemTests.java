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
package com.expedia.seiso.domain.meta;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import lombok.val;

import org.junit.Before;
import org.junit.Test;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;

/**
 * @author Willie Wheeler
 */
public class DynaItemTests {

	// Class under test
	private DynaItem dataCenterWrapper;
	private DynaItem nodeWrapper;
	private DynaItem serviceInstanceWrapper;

	// Test data
	private DataCenter dataCenter;
	private Node node;
	private ServiceInstance serviceInstance;

	@Before
	public void setUp() throws Exception {
		setUpTestData();
		this.dataCenterWrapper = new DynaItem(dataCenter);
		this.nodeWrapper = new DynaItem(node);
		this.serviceInstanceWrapper = new DynaItem(serviceInstance);
	}

	private void setUpTestData() {
		this.dataCenter = new DataCenter().setKey("switch-supernap");
		this.node = new Node().setName("mm01").setIpAddresses(new ArrayList<NodeIpAddress>());
		this.serviceInstance = new ServiceInstance().setKey("airint-shopping-prod")
				.setPorts(new ArrayList<ServiceInstancePort>()).setNodes(new ArrayList<Node>());
	}

	@Test(expected = NullPointerException.class)
	public void construct_null() {
		new DynaItem(null);
	}

	@Test
	public void getMetaKey_dataCenter() {
		val result = dataCenterWrapper.getMetaKey();
		assertEquals(dataCenter.getKey(), result);
	}

	@Test
	public void getMetaKey_node() {
		val result = nodeWrapper.getMetaKey();
		assertEquals(node.getName(), result);
	}

	@Test
	public void getMetaKey_serviceInstance() {
		val result = serviceInstanceWrapper.getMetaKey();
		assertEquals(serviceInstance.getKey(), result);
	}
}
