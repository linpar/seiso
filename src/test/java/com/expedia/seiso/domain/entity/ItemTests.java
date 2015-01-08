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
package com.expedia.seiso.domain.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

/**
 * This "test case" (using that term loosely here) is mostly intended to exercise the entity code for coverage purposes.
 * It gets rid of false gaps. Granted we could also simply exclude the entities from the coverage path.
 * 
 * @author Willie Wheeler
 */
@XSlf4j
public class ItemTests {
	private DataCenter dataCenter;
	private Endpoint endpoint;
	private Environment environment;
	private HealthStatus healthStatus;
	private InfrastructureProvider infrastructureProvider;
	private IpAddressRole ipAddressRole;
	private LoadBalancer loadBalancer;
	private Machine machine;
	private Node node;
	private NodeIpAddress nodeIpAddress;
	private Person person;
	private Region region;
	private RotationStatus rotationStatus;
	private Service service;
	private ServiceInstance serviceInstance;
	private ServiceInstancePort serviceInstancePort;
	private ServiceGroup serviceGroup;
	private ServiceType serviceType;
	private StatusType statusType;

	// These are part of "core", but I'm putting them here since I think we're going to end up collapsing core and
	// domain anyway. [WLW]
	private Role role;
	private User user;

	@Before
	public void init() throws Exception {
		
		// @formatter:off

		// Core
		this.role = new Role().setName("ROLE_SORCEROR");
		this.user = new User().setUsername("some-username").setPassword("some-p@ssword").setEnabled(true)
				.setRoles(Collections.singletonList(role));

		// Statuses
		this.statusType = new StatusType().setKey("warning").setName("Warning");
		this.healthStatus = new HealthStatus().setKey("degraded").setName("Degraded").setStatusType(statusType);

		// People
		this.person = new Person().setUsername("doge").setFirstName("Such").setLastName("Generous")
				.setEmail("such.generous@example.com");

		// Large-scale infrastructure
		this.infrastructureProvider = new InfrastructureProvider().setKey("amazon").setName("Amazon")
				.setRegions(new ArrayList<Region>());
		this.region = new Region().setKey("us-west").setName("US West")
				.setInfrastructureProvider(infrastructureProvider);
		this.dataCenter = new DataCenter().setKey("switch-supernap").setName("Switch SuperNAP").setRegion(region);

		// Load balancers
		this.rotationStatus = new RotationStatus().setKey("enabled").setName("Enabled");
		this.loadBalancer = new LoadBalancer().setName("my-load-balancer").setDataCenter(dataCenter)
				.setIpAddress("10.10.10.10").setApiUrl("https://www.example.com/");

		// Machines
		this.machine = new Machine().setName("my-machine").setFqdn("mymachine.example.com").setHostname("mymachine")
				.setDomain("example.com").setIpAddress("200.200.200.200").setIp6Address("blahblahblah")
				.setMacAddress("blahblah").setPlatform("amazon").setPlatformVersion("2014.1").setOs("linux")
				.setOsVersion("blahblah").setMachineType("large").setNativeMachineId("xyz123")
				.setNodes(new ArrayList<Node>()).setChefRole("seiso-api").setDataCenter(dataCenter);

		// Services
		this.serviceGroup = new ServiceGroup().setKey("my-service-group").setName("My Service Group");
		this.serviceType = new ServiceType().setKey("database").setName("Database");
		this.service = new Service().setKey("seiso").setName("Seiso").setType(serviceType)
				.setServiceInstances(new ArrayList<ServiceInstance>()).setOwner(person).setPlatform("Java")
				.setScmRepository("https://ewegithub.sb.karmalab.net/wwheeler/seiso");
		this.environment = new Environment().setKey("test").setName("Test").setAka("QA")
				.setDescription("Test environment");
		this.serviceInstance = new ServiceInstance().setKey("seiso-api-test").setService(service)
				.setEnvironment(environment).setDataCenter(dataCenter).setPorts(new ArrayList<ServiceInstancePort>())
				.setNodes(new ArrayList<Node>()).setMinCapacityOps(50).setMinCapacityOps(75).setEosManaged(true);
		this.ipAddressRole = new IpAddressRole().setName("default").setServiceInstance(serviceInstance)
				.setIpAddresses(new ArrayList<NodeIpAddress>());
		this.serviceInstancePort = new ServiceInstancePort().setServiceInstance(serviceInstance).setNumber(8080)
				.setProtocol("HTTP").setDescription("HTTP REST API");
		this.node = new Node().setName("my-node").setServiceInstance(serviceInstance).setMachine(machine)
				.setIpAddresses(new ArrayList<NodeIpAddress>());
		this.nodeIpAddress = new NodeIpAddress().setNode(node).setIpAddressRole(ipAddressRole).setIpAddress("1.1.1.1")
				.setEndpoints(new ArrayList<Endpoint>());
		this.endpoint = new Endpoint().setIpAddress(nodeIpAddress).setPort(serviceInstancePort)
				.setRotationStatus(rotationStatus);
		// @formatter:on
	}

	@Test
	public void testAccessors() throws Exception {
		// @formatter:off
		exerciseCode(dataCenter, endpoint, environment, healthStatus, infrastructureProvider, ipAddressRole,
				loadBalancer, machine, node, nodeIpAddress, person, region, rotationStatus, service,
				serviceInstance, serviceInstancePort, serviceGroup, serviceType, statusType,

				// Core entities
				user);
		// @formatter:on

		exerciseCode(role);
	}

	private void exerciseCode(Object... items) throws Exception {
		for (val item : items) {
			val itemClass = item.getClass();
			exerciseAccessors(item, itemClass);
			exerciseEquals(item);
			exerciseHashCode(item);
			exerciseToString(item);
		}
	}

	private void exerciseAccessors(Object item, Class<?> itemClass) throws IllegalAccessException,
			InvocationTargetException {

		log.trace("Exercising accessors: entityClass={}", itemClass.getName());
		val descs = BeanUtils.getPropertyDescriptors(itemClass);
		for (val desc : descs) {
			val propName = desc.getName();
			if ("class".equals(propName)) {
				continue;
			}
			log.trace("Testing property: {}.{}", itemClass.getSimpleName(), propName);
			Method reader = desc.getReadMethod();
			Object value = reader.invoke(item);
			Method writer = desc.getWriteMethod();
			if (writer != null) {
				writer.invoke(item, value);
			}
		}
	}

	private void exerciseEquals(Object item) {
		log.trace("Exercising equals()");

		// The following is legitimate testing, as opposed to simple coverage grabs.
		assertTrue(item.equals(item));

		val copy = BeanUtils.instantiate(item.getClass());
		// FIXME This is true when we don't set anything on the POJO. [WLW]
		assertFalse(item.equals(copy));

		BeanUtils.copyProperties(item, copy);
		assertTrue(item.equals(copy));

		assertFalse(item.equals(null));
		assertFalse(item.equals("dlafkjs"));
	}

	private void exerciseHashCode(Object item) {
		log.trace("Exercising hashCode()");
		item.hashCode();
	}

	private void exerciseToString(Object item) {
		log.trace("Exercising toString()");
		assertNotNull(item.toString());
	}
}
