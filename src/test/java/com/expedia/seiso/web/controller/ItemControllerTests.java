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
package com.expedia.seiso.web.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import lombok.val;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.exception.InvalidRequestException;
import com.expedia.seiso.core.exception.ResourceNotFoundException;
import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Environment;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaImpl;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.meta.RepoMeta;
import com.expedia.seiso.domain.repo.DataCenterRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.gateway.NotificationGateway;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.assembler.ResponseHeadersBuilder;
import com.expedia.seiso.web.dto.MapItemDto;
import com.expedia.seiso.web.dto.PEItemDto;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class ItemControllerTests {

	// Class under test
	@InjectMocks private ItemController controller;

	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private ItemService itemService;
	@Mock private ItemAssembler itemAssembler;
	@Mock private ResponseHeadersBuilder responseHeadersBuilder;
	@Mock private NotificationGateway publishGateway;

	// Test data
	private ItemMeta dataCenterRepoMeta;
	private ItemMeta nodeRepoMeta;
	// private ItemMeta serviceRepoMeta;
	// private ItemMeta serviceInstanceRepoMeta;

	private PageRequest pageRequest;

	private DataCenter existingDataCenter, nonExistingDataCenter;
	private Environment existingEnvironment;
	private Node existingNode, nonExistingNode;
	private Service existingService, nonExistingService;

	@Mock
	private PEItemDto existingDataCenterDto, nonExistingDataCenterDto;
	@Mock
	private PEItemDto existingNodeDto, nonExistingNodeDto;
	@Mock
	private PEItemDto existingServiceDto, nonExistingServiceDto;
	@Mock
	private PEItemDto existingServiceInstanceDto, nonExistingServiceInstanceDto;

	private ServiceInstance nonExistingServiceInstance, existingServiceInstance;
	private Page<DataCenter> dataCenters;

	@Mock
	private PagedResources<MapItemDto> dataCenterMapDtoPage;
	@Mock
	private PageMetadata dataCenterPageMeta;
	@Mock
	private MapItemDto dataCenterMapDto;
	@Mock
	private LinkBuilder linkBuilder;
	@Mock
	private Link link;

	@Before
	public void init() throws Exception {
		this.controller = new ItemController();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}

	private void initTestData() {

		// @formatter:off
		this.dataCenterRepoMeta = new ItemMetaImpl(DataCenter.class, DataCenterRepo.class, false);
		this.nodeRepoMeta = new ItemMetaImpl(Node.class, NodeRepo.class, true);
		// this.serviceRepoMeta = new ItemMetaImpl(Service.class, ServiceRepo.class, true);
		// this.serviceInstanceRepoMeta = new ItemMetaImpl(ServiceInstance.class, ServiceInstanceRepo.class, true);

		this.pageRequest = new PageRequest(0, 100);

		// Existing items
		this.existingDataCenter = new DataCenter().setKey("existing-data-center");
		this.existingEnvironment = new Environment().setKey("existing-environment");
		this.existingService = new Service().setKey("existing-service");
		this.existingServiceInstance = new ServiceInstance().setKey("existing-service-instance")
				.setService(existingService).setDataCenter(existingDataCenter)
				.setPorts(new ArrayList<ServiceInstancePort>()).setNodes(new ArrayList<Node>());
		this.existingNode = new Node().setName("existing-node").setServiceInstance(existingServiceInstance)
				.setIpAddresses(new ArrayList<NodeIpAddress>());

		// Non-existing items
		this.nonExistingDataCenter = new DataCenter().setKey("non-existing-data-center");
		this.nonExistingService = new Service().setKey("non-existing-service");
		this.nonExistingServiceInstance = new ServiceInstance().setKey("non-existing-service-instance")
				.setService(existingService).setEnvironment(existingEnvironment).setDataCenter(existingDataCenter)
				.setPorts(new ArrayList<ServiceInstancePort>()).setNodes(new ArrayList<Node>());
		this.nonExistingNode = new Node().setName("non-existing-node").setServiceInstance(existingServiceInstance)
				.setIpAddresses(new ArrayList<NodeIpAddress>());
		// @formatter:on

		val dataCenterList = new ArrayList<>();
		dataCenterList.add(nonExistingDataCenter);
		this.dataCenters = new PageImpl(dataCenterList);

		when(existingDataCenterDto.getItem()).thenReturn(existingDataCenter);
		when(existingNodeDto.getItem()).thenReturn(existingNode);
		when(existingServiceDto.getItem()).thenReturn(existingService);
		when(existingServiceInstanceDto.getItem()).thenReturn(existingServiceInstance);

		when(nonExistingDataCenterDto.getItem()).thenReturn(nonExistingDataCenter);
		when(nonExistingNodeDto.getItem()).thenReturn(nonExistingNode);
		when(nonExistingServiceDto.getItem()).thenReturn(nonExistingService);
		when(nonExistingServiceInstanceDto.getItem()).thenReturn(nonExistingServiceInstance);

		when(dataCenterPageMeta.getSize()).thenReturn(100L);
		when(dataCenterPageMeta.getNumber()).thenReturn(0L);
		when(dataCenterPageMeta.getTotalElements()).thenReturn(502L);
		// PageMetadata returns the total number of *full* pages (i.e., excluding partials), for whatever weird reason.
		when(dataCenterPageMeta.getTotalPages()).thenReturn(5L);

		when(dataCenterMapDtoPage.getMetadata()).thenReturn(dataCenterPageMeta);

		when(linkBuilder.withRel(anyString())).thenReturn(link);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initDependencies() {
		when(itemMetaLookup.getItemClass(RepoKeys.DATA_CENTERS)).thenReturn(DataCenter.class);
		when(itemMetaLookup.getItemClass(RepoKeys.NODES)).thenReturn(Node.class);
		when(itemMetaLookup.getItemClass(RepoKeys.SERVICES)).thenReturn(Service.class);
		when(itemMetaLookup.getItemClass(RepoKeys.SERVICE_INSTANCES)).thenReturn(ServiceInstance.class);

		when(itemMetaLookup.getItemMeta(DataCenter.class)).thenReturn(dataCenterRepoMeta);
		when(itemMetaLookup.getItemMeta(Node.class)).thenReturn(nodeRepoMeta);

		when(itemService.findAll(DataCenter.class, pageRequest)).thenReturn(dataCenters);

		when(itemService.find(new SimpleItemKey(DataCenter.class, existingDataCenter.getKey()))).thenReturn(
				existingDataCenter);
		when(itemService.find(new SimpleItemKey(Environment.class, existingEnvironment.getKey()))).thenReturn(
				existingEnvironment);
		when(itemService.find(new SimpleItemKey(Node.class, existingNode.getName()))).thenReturn(existingNode);
		when(itemService.find(new SimpleItemKey(Service.class, existingService.getKey()))).thenReturn(existingService);
		when(itemService.find(new SimpleItemKey(ServiceInstance.class, existingServiceInstance.getKey()))).thenReturn(
				existingServiceInstance);

		doThrow(new ResourceNotFoundException()).when(itemService).find(
				new SimpleItemKey(Service.class, "non-existing-service"));

		doThrow(new ResourceNotFoundException()).when(itemService).delete(
				new SimpleItemKey(Service.class, "non-existing-service"));

		when(itemAssembler.toDtoPage((Page) anyObject(), (ProjectionNode) anyObject()))
				.thenReturn(dataCenterMapDtoPage);

		when(itemAssembler.toDto(eq(existingDataCenter), (ProjectionNode) anyObject())).thenReturn(dataCenterMapDto);
	}

	@Test
	public void getPage() {
		val result = controller.getPage(RepoKeys.NODES, Projection.DEFAULT, pageRequest);
		assertNotNull(result);
		verify(itemService).findAll(Node.class, pageRequest);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getPage_nonExistingItemType() {
		controller.getPage("non-existing-item-type", Projection.DEFAULT, pageRequest);
	}

	@Test
	public void getOne() {
		val key = existingDataCenter.getKey();
		val result = controller.getOne(new RepoMeta(DataCenter.class), key, "default");
		assertNotNull(result);
		verify(itemService).find(new SimpleItemKey(DataCenter.class, key));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getNonExistingService() {
		controller.getOne(new RepoMeta(Service.class), "non-existing-service", Projection.DEFAULT);
	}

	@Test
	public void putExistingDataCenter() {
		controller.put(RepoKeys.DATA_CENTERS, "existing-data-center", existingDataCenterDto);
		verify(itemService).save(existingDataCenter);
	}

	@Test
	public void putNonExistingNode() {
		controller.put(RepoKeys.NODES, "non-existing-node", nonExistingNodeDto);
		verify(itemService).save(nonExistingNode);
	}

	@Test
	public void putExistingNode() {
		controller.put(RepoKeys.NODES, "existing-node", existingNodeDto);
		verify(itemService).save(existingNode);
	}

	@Test
	public void putExistingServiceInstance() {
		controller.put(RepoKeys.SERVICE_INSTANCES, "existing-service-instance", existingServiceInstanceDto);
		verify(itemService).save(existingServiceInstance);
	}

	// Ignoring because this is no longer a controller test.
	@Ignore
	@Test(expected = InvalidRequestException.class)
	public void putExistingServiceInstanceWithNonexistingService() {
		existingServiceInstance.setService(nonExistingService);
		controller.put(RepoKeys.SERVICE_INSTANCES, "existing-service-instance", existingServiceInstanceDto);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void deleteNonExistingService() {
		controller.delete(RepoKeys.SERVICES, "non-existing-service");
	}

	@Test
	public void deleteExistingService() {
		controller.delete(RepoKeys.SERVICES, "existing-service");
	}
}
