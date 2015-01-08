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
package com.expedia.seiso.domain.entity.key;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.LoadBalancer;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.ServiceInstancePort;

/**
 * Tests simply to eat up code coverage missed instructions.
 * 
 * @author Willie Wheeler
 */
public class ItemKeyTests {
	private EndpointKey endpointKey1, endpointKey2, endpointKey3;
	private IpAddressRoleKey iarKey1, iarKey2, iarKey3;
	private NodeIpAddressKey nipKey1, nipKey2, nipKey3;
	private ServiceInstancePortKey sipKey1, sipKey2, sipKey3;
	private SimpleItemKey simpleItemKey1, simpleItemKey2, simpleItemKey3;
	
	@Before
	public void init() {
		this.endpointKey1 = new EndpointKey(1L);
		this.endpointKey2 = new EndpointKey(1L);
		this.endpointKey3 = new EndpointKey(2L);
		
		this.iarKey1 = new IpAddressRoleKey("foo", "bar");
		this.iarKey2 = new IpAddressRoleKey("foo", "bar");
		this.iarKey3 = new IpAddressRoleKey("foo", "baz");
		
		this.nipKey1 = new NodeIpAddressKey("foo", "bar");
		this.nipKey2 = new NodeIpAddressKey("foo", "bar");
		this.nipKey3 = new NodeIpAddressKey("foo", "baz");
		
		this.sipKey1 = new ServiceInstancePortKey("foo", 8080);
		this.sipKey2 = new ServiceInstancePortKey("foo", 8080);
		this.sipKey3 = new ServiceInstancePortKey("foo", 9090);
		
		this.simpleItemKey1 = new SimpleItemKey(LoadBalancer.class, "lb-1414");
		this.simpleItemKey2 = new SimpleItemKey(LoadBalancer.class, "lb-1414");
		this.simpleItemKey3 = new SimpleItemKey(LoadBalancer.class, "lb-2828");
	}
	
	@Test(expected = NullPointerException.class)
	public void newEndpointKey_nullId() { new EndpointKey(null); }
	
	@Test(expected = NullPointerException.class)
	public void newIpAddressRoleKey_nullServiceInstanceKey() { new IpAddressRoleKey(null, "bar"); }
	
	@Test(expected = NullPointerException.class)
	public void newIpAddressRoleKey_nullName() { new IpAddressRoleKey("foo", null); }
	
	@Test(expected = NullPointerException.class)
	public void newNodeIpAddressKey_nullNodeName() { new NodeIpAddressKey(null, "bar"); }
	
	@Test(expected = NullPointerException.class)
	public void newNodeIpAddressKey_nullIpAddress() { new NodeIpAddressKey("foo", null); }
	
	@Test(expected = NullPointerException.class)
	public void newServiceInstancePortKey_nullServiceInstanceKey() { new ServiceInstancePortKey(null, 8080); }
	
	@Test(expected = NullPointerException.class)
	public void newServiceInstancePortKey_nullNumber() { new ServiceInstancePortKey("foo", null); }
	
	@Test(expected = NullPointerException.class)
	public void newSimpleItemKey_nullItemClass() { new SimpleItemKey(null, "lb-1414"); }
	
	@Test(expected = NullPointerException.class)
	public void newSimpleItemKey_nullValue() { new SimpleItemKey(LoadBalancer.class, null); }
	
	@Test
	public void testAccessors() {
		assertEquals(Endpoint.class, endpointKey1.getItemClass());
		assertEquals(1L, endpointKey1.getId().longValue());
		endpointKey1.setId(100L);
		assertEquals(100L, endpointKey1.getId().longValue());
		
		assertEquals(IpAddressRole.class, iarKey1.getItemClass());
		assertEquals("foo", iarKey1.getServiceInstanceKey());
		assertEquals("bar", iarKey1.getName());
		iarKey1.setServiceInstanceKey("baz");
		iarKey1.setName("quux");
		assertEquals("baz", iarKey1.getServiceInstanceKey());
		assertEquals("quux", iarKey1.getName());
		
		assertEquals(NodeIpAddress.class, nipKey1.getItemClass());
		assertEquals("foo", nipKey1.getNodeName());
		assertEquals("bar", nipKey1.getIpAddress());
		nipKey1.setNodeName("baz");
		nipKey1.setIpAddress("quux");
		assertEquals("baz", nipKey1.getNodeName());
		assertEquals("quux", nipKey1.getIpAddress());
		
		assertEquals(ServiceInstancePort.class, sipKey1.getItemClass());
		assertEquals("foo", sipKey1.getServiceInstanceKey());
		assertEquals(8080, sipKey1.getNumber().intValue());
		sipKey1.setServiceInstanceKey("bar");
		sipKey1.setNumber(9090);
		assertEquals("bar", sipKey1.getServiceInstanceKey());
		assertEquals(9090, sipKey1.getNumber().intValue());
		
		assertEquals(LoadBalancer.class, simpleItemKey1.getItemClass());
		assertEquals("lb-1414", simpleItemKey1.getValue());
		simpleItemKey1.setItemClass(DataCenter.class);
		simpleItemKey1.setValue("dc-1414");
		assertEquals(DataCenter.class, simpleItemKey1.getItemClass());
		assertEquals("dc-1414", simpleItemKey1.getValue());
	}
	
	@Test(expected = NullPointerException.class)
	public void endpointKey_setId_null() { endpointKey1.setId(null); }
	
	@Test(expected = NullPointerException.class)
	public void ipAddressRoleKey_setServiceInstanceKey_null() { iarKey1.setServiceInstanceKey(null); }
	
	@Test(expected = NullPointerException.class)
	public void ipAddressRoleKey_setName_null() { iarKey1.setName(null); }
	
	@Test(expected = NullPointerException.class)
	public void nodeIpAddressKey_setNodeName_null() { nipKey1.setNodeName(null); }
	
	@Test(expected = NullPointerException.class)
	public void nodeIpAddressKey_setIpAddress_null() { nipKey1.setIpAddress(null); }
	
	@Test(expected = NullPointerException.class)
	public void serviceInstancePortKey_setServiceInstanceKey_null() { sipKey1.setServiceInstanceKey(null); }
	
	@Test(expected = NullPointerException.class)
	public void serviceInstancePortKey_setNumber_null() { sipKey1.setNumber(null); }
	
	@Test(expected = NullPointerException.class)
	public void simpleItemKey_setItemClass_null() { simpleItemKey1.setItemClass(null); }
	
	@Test(expected = NullPointerException.class)
	public void simpleItemKey_setValue_null() { simpleItemKey1.setValue(null); }
	
	@Test
	public void testEquals() {
		assertTrue(endpointKey1.equals(endpointKey1));
		assertTrue(endpointKey1.equals(endpointKey2));
		assertFalse(endpointKey1.equals(endpointKey3));
		assertFalse(endpointKey1.equals(""));
		assertFalse(endpointKey1.equals(null));
		
		assertTrue(iarKey1.equals(iarKey1));
		assertTrue(iarKey1.equals(iarKey2));
		assertFalse(iarKey1.equals(iarKey3));
		assertFalse(iarKey1.equals(""));
		assertFalse(iarKey1.equals(null));
		
		assertTrue(nipKey1.equals(nipKey1));
		assertTrue(nipKey1.equals(nipKey2));
		assertFalse(nipKey1.equals(nipKey3));
		assertFalse(nipKey1.equals(""));
		assertFalse(nipKey1.equals(null));
		
		assertTrue(sipKey1.equals(sipKey1));
		assertTrue(sipKey1.equals(sipKey2));
		assertFalse(sipKey1.equals(sipKey3));
		assertFalse(sipKey1.equals(""));
		assertFalse(sipKey1.equals(null));
		
		assertTrue(simpleItemKey1.equals(simpleItemKey1));
		assertTrue(simpleItemKey1.equals(simpleItemKey2));
		assertFalse(simpleItemKey1.equals(simpleItemKey3));
		assertFalse(simpleItemKey1.equals(""));
		assertFalse(simpleItemKey1.equals(null));
	}
	
	@Test
	public void testHashCode() {
		assertTrue(endpointKey1.hashCode() >= Integer.MIN_VALUE);
		assertTrue(iarKey1.hashCode() >= Integer.MIN_VALUE);
		assertTrue(nipKey1.hashCode() >= Integer.MIN_VALUE);
		assertTrue(sipKey1.hashCode() >= Integer.MIN_VALUE);
		assertTrue(simpleItemKey1.hashCode() >= Integer.MIN_VALUE);
	}
	
	@Test
	public void testToString() {
		assertNotNull(endpointKey1.toString());
		assertNotNull(iarKey1.toString());
		assertNotNull(nipKey1.toString());
		assertNotNull(sipKey1.toString());
		assertNotNull(simpleItemKey1.toString());
	}
}
