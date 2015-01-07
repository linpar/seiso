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
package com.expedia.seiso.web.controller.v1;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Environment;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaImpl;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.DataCenterRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.seiso.web.ResponseHeadersV1;
import com.expedia.seiso.web.controller.BasicItemDelegate;
import com.expedia.seiso.web.controller.ItemSearchDelegate;
import com.expedia.seiso.web.controller.PEResource;
import com.expedia.seiso.web.hateoas.BaseResource;
import com.expedia.seiso.web.hateoas.BaseResourcePage;
import com.expedia.seiso.web.hateoas.PageMetadata;

/**
 * @author Willie Wheeler
 */
public class ItemControllerV1Tests {

	// Class under test
	@InjectMocks private ItemControllerV1 controller;

	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private BasicItemDelegate basicItemDelegate;
	@Mock private ItemSearchDelegate itemSearchDelegate;
	@Mock private ResponseHeadersV1 responseHeaders;

	// Test data
	private ItemMeta dataCenterRepoMeta;
	private ItemMeta nodeRepoMeta;

	private DataCenter existingDataCenter, nonExistingDataCenter;
	private Environment existingEnvironment;
	private Node existingNode, nonExistingNode;
	private Service existingService, nonExistingService;

	@Mock private PEResource existingDataCenterDto, nonExistingDataCenterDto;
	@Mock private PEResource existingNodeDto, nonExistingNodeDto;
	@Mock private PEResource existingServiceDto, nonExistingServiceDto;
	@Mock private PEResource existingServiceInstanceDto, nonExistingServiceInstanceDto;

	private ServiceInstance nonExistingServiceInstance, existingServiceInstance;

	@Mock private BaseResourcePage dataCenterBaseResourcePage;
	@Mock private PageMetadata dataCenterPageMeta;
	@Mock private BaseResource dataCenterBaseResource;

	@Before
	public void init() throws Exception {
		this.controller = new ItemControllerV1();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}

	private void initTestData() {

		// @formatter:off
		this.dataCenterRepoMeta = new ItemMetaImpl(DataCenter.class, DataCenterRepo.class, false);
		this.nodeRepoMeta = new ItemMetaImpl(Node.class, NodeRepo.class, true);

		// Existing items
		this.existingDataCenter = new DataCenter().setKey("existing-data-center");
		this.existingEnvironment = new Environment().setKey("existing-environment");
		this.existingService = new Service().setKey("existing-service");
		this.existingServiceInstance = new ServiceInstance()
				.setKey("existing-service-instance")
				.setService(existingService)
				.setDataCenter(existingDataCenter)
				.setPorts(new ArrayList<ServiceInstancePort>())
				.setNodes(new ArrayList<Node>());
		this.existingNode = new Node().setName("existing-node")
				.setServiceInstance(existingServiceInstance)
				.setIpAddresses(new ArrayList<NodeIpAddress>());

		// Non-existing items
		this.nonExistingDataCenter = new DataCenter().setKey("non-existing-data-center");
		this.nonExistingService = new Service().setKey("non-existing-service");
		this.nonExistingServiceInstance = new ServiceInstance()
				.setKey("non-existing-service-instance")
				.setService(existingService)
				.setEnvironment(existingEnvironment)
				.setDataCenter(existingDataCenter)
				.setPorts(new ArrayList<ServiceInstancePort>())
				.setNodes(new ArrayList<Node>());
		this.nonExistingNode = new Node()
				.setName("non-existing-node")
				.setServiceInstance(existingServiceInstance)
				.setIpAddresses(new ArrayList<NodeIpAddress>());
		// @formatter:on

		val dataCenterList = new ArrayList<>();
		dataCenterList.add(nonExistingDataCenter);

		when(existingDataCenterDto.getItem()).thenReturn(existingDataCenter);
		when(existingNodeDto.getItem()).thenReturn(existingNode);
		when(existingServiceDto.getItem()).thenReturn(existingService);
		when(existingServiceInstanceDto.getItem()).thenReturn(existingServiceInstance);

		when(nonExistingDataCenterDto.getItem()).thenReturn(nonExistingDataCenter);
		when(nonExistingNodeDto.getItem()).thenReturn(nonExistingNode);
		when(nonExistingServiceDto.getItem()).thenReturn(nonExistingService);
		when(nonExistingServiceInstanceDto.getItem()).thenReturn(nonExistingServiceInstance);

		when(dataCenterPageMeta.getPageSize()).thenReturn(100L);
		when(dataCenterPageMeta.getPageNumber()).thenReturn(0L);
		when(dataCenterPageMeta.getTotalItems()).thenReturn(502L);
		// PageMetadata returns the total number of *full* pages (i.e., excluding partials), for whatever weird reason.
		when(dataCenterPageMeta.getTotalPages()).thenReturn(5L);

		when(dataCenterBaseResourcePage.getMetadata()).thenReturn(dataCenterPageMeta);
	}

	private void initDependencies() {
		when(itemMetaLookup.getItemClass(RepoKeys.DATA_CENTERS)).thenReturn(DataCenter.class);
		when(itemMetaLookup.getItemClass(RepoKeys.NODES)).thenReturn(Node.class);
		when(itemMetaLookup.getItemClass(RepoKeys.SERVICES)).thenReturn(Service.class);
		when(itemMetaLookup.getItemClass(RepoKeys.SERVICE_INSTANCES)).thenReturn(ServiceInstance.class);

		when(itemMetaLookup.getItemMeta(DataCenter.class)).thenReturn(dataCenterRepoMeta);
		when(itemMetaLookup.getItemMeta(Node.class)).thenReturn(nodeRepoMeta);
	}

	@Test
	public void deleteExistingService() {
		controller.delete(RepoKeys.SERVICES, "existing-service");
	}
}
