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
package com.expedia.seiso.web.controller.v2;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.ServiceInstanceRepo;
import com.expedia.seiso.domain.service.ServiceInstanceService;
import com.expedia.seiso.hypermedia.LinkFactory;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.serf.C;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.hypermedia.Link;
import com.expedia.serf.hypermedia.PageMetadata;
import com.expedia.serf.hypermedia.PagedResources;
import com.expedia.serf.hypermedia.Relations;
import com.expedia.serf.hypermedia.Resource;
import com.expedia.serf.hypermedia.Resources;
import com.expedia.serf.web.MediaTypes;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2/service-instances")
@SuppressBasePath
@XSlf4j
public class ServiceInstanceControllerV2 {
	private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
	
	@Autowired private ServiceInstanceRepo serviceInstanceRepo;
	@Autowired private ServiceInstanceService serviceInstanceService;
	
	@Autowired
	@Qualifier("linkFactoryV2")
	private LinkFactory linkFactoryV2;
	
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private ResourceAssembler resourceAssembler;
	
	static {
		percentFormat.setMinimumFractionDigits(1);
		percentFormat.setMaximumFractionDigits(1);
	}
	
	@RequestMapping(
			value = "/{key}/node-summary",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Resource getNodeSummary(@PathVariable String key) {
		val links = linkFactoryV2.getItemLinks();
		val serviceInstance = getServiceInstance(key);
		val data = serviceInstanceService.getNodeSummary(key);
		val resource = new Resource();
		resource.addLink(links.serviceInstanceNodeSummaryLink(Relations.SELF, serviceInstance));
		resource.addLink(links.itemLink(Relations.UP, serviceInstance));
		resource.setProperty("numNodes", data.getNumNodes());
		resource.setProperty("numHealthy", data.getNumHealthy());
		resource.setProperty("numEnabled", data.getNumEnabled());
		resource.setProperty("numHealthyGivenEnabled", data.getNumHealthyGivenEnabled());
		return resource;
	}
	
	@RequestMapping(
			value = "/{key}/health-breakdown",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Resource getHealthBreakdown(@PathVariable String key) {
		val links = linkFactoryV2.getItemLinks();
		val serviceInstance = getServiceInstance(key);
		val data = serviceInstanceService.getHealthBreakdown(key);
		val resource = new Resource();
		resource.addLink(links.serviceInstanceHealthBreakdownLink(Relations.SELF, serviceInstance));
		resource.addLink(links.itemLink(Relations.UP, serviceInstance));
		resource.setProperty("items",  data);
		return resource;
	}
	
	@RequestMapping(
			value = "/{key}/rotation-breakdown",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Resource getRotationBreakdown(@PathVariable String key) {
		val links = linkFactoryV2.getItemLinks();
		val serviceInstance = getServiceInstance(key);
		val data = serviceInstanceService.getRotationBreakdown(key);
		val resource = new Resource();
		resource.addLink(links.serviceInstanceRotationBreakdownLink(Relations.SELF, serviceInstance));
		resource.addLink(links.itemLink(Relations.UP, serviceInstance));
		resource.setProperty("items",  data);
		return resource;
	}
	
	// TODO Make this paging?
	// Not sure. It would make this like the environment/data center cases. But would there really be that many service
	// instances in a service? Probably not.
	@RequestMapping(
			value = "/search/find-by-service",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Resources findByService(@RequestParam String key) {
		List<Object[]> results = serviceInstanceRepo.findByServiceWithCounts(key);
		val itemMeta = itemMetaLookup.getItemMeta(ServiceInstance.class);
		val proj = itemMeta.getProjectionNode(ApiVersion.V2, Projection.Cardinality.COLLECTION, "by-service");
		return toServiceInstanceResources(results, proj);
	}
	
	@RequestMapping(
			value = "/search/find-by-environment",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public PagedResources findByEnvironment(
			@RequestParam String key,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					sort = "key",
					direction = Direction.ASC)
			Pageable pageable) {
		
		Page<Object[]> results = serviceInstanceRepo.findByEnvironmentWithCounts(key, pageable);
		val itemMeta = itemMetaLookup.getItemMeta(ServiceInstance.class);
		val proj = itemMeta.getProjectionNode(ApiVersion.V2, Projection.Cardinality.COLLECTION, "by-environment");
		return toServiceInstancePagedResources(results, proj);
	}
	
	@RequestMapping(
			value = "/search/find-by-data-center",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public PagedResources findByDataCenter(
			@RequestParam String key,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					sort = "key",
					direction = Direction.ASC)
			Pageable pageable) {
		
		Page<Object[]> results = serviceInstanceRepo.findByDataCenterWithCounts(key, pageable);
		val itemMeta = itemMetaLookup.getItemMeta(ServiceInstance.class);
		val proj = itemMeta.getProjectionNode(ApiVersion.V2, Projection.Cardinality.COLLECTION, "by-data-center");
		return toServiceInstancePagedResources(results, proj);
	}
	
	private ServiceInstance getServiceInstance(String key) {
		ServiceInstance dummy = new ServiceInstance();
		dummy.setKey(key);
		return dummy;
	}
	
	private Resources toServiceInstanceResources(List<Object[]> itemList, ProjectionNode proj) {
		List<Resource> itemResources = new ArrayList<>();
		for (Object[] item : itemList) {
			itemResources.add(toServiceInstanceResource(item, proj));
		}
		Resources resources = new Resources();
		resources.setItems(itemResources);
		return resources;
	}
	
	private PagedResources toServiceInstancePagedResources(Page<Object[]> itemPage, ProjectionNode proj) {
		List<Link> links = new ArrayList<>();
		
		// FIXME This is misreporting the total number of items.
		// See e.g. amazon-us-east-1d
		int pageSize = itemPage.getSize();
		int pageNumber = itemPage.getNumber();
		long totalElements = itemPage.getTotalElements();
		log.trace("pageSize={}, pageNumber={}, totalElements={}", pageSize, pageNumber, totalElements);
		PageMetadata metadata = new PageMetadata(pageSize, pageNumber, totalElements);
		
		List<Object[]> itemList = itemPage.getContent();
		log.trace("itemList.size={}", itemList.size());
		List<Resource> itemResources = new ArrayList<>();
		for (Object[] item : itemList) {
			itemResources.add(toServiceInstanceResource(item, proj));
		}
		
		return new PagedResources(links, metadata, itemResources);
	}
	
	private Resource toServiceInstanceResource(Object[] data, ProjectionNode proj) {
		ServiceInstance si = (ServiceInstance) data[0];
		Long numNodes = (data[1] == null ? 0L : (Long) data[1]);
		Long numHealthy = (data[2] == null ? 0L : (Long) data[2]);
		String percentHealthy = (numNodes == 0L ? "N/A" :
			percentFormat.format((double) numHealthy / (double) numNodes));
		
		// TODO Remove hardcodes
		String healthKey = (numHealthy < numNodes ? "warning" : "success");
		
		Resource siResource = resourceAssembler.toResource(ApiVersion.V2, si, proj);
		siResource.setProperty("numNodes", numNodes);
		siResource.setProperty("numHealthy", numHealthy);
		siResource.setProperty("percentHealthy", percentHealthy);
		siResource.setProperty("healthKey", healthKey);
		return siResource;
	}
}
