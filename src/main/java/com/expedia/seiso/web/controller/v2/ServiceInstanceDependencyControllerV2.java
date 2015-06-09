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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.ServiceInstanceDependency;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.ServiceInstanceDependencyRepo;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.serf.C;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.hypermedia.Link;
import com.expedia.serf.hypermedia.PageMetadata;
import com.expedia.serf.hypermedia.PagedResources;
import com.expedia.serf.hypermedia.Resource;
import com.expedia.serf.web.MediaTypes;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2/service-instance-dependencies")
@SuppressBasePath
@XSlf4j
public class ServiceInstanceDependencyControllerV2 {
	private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
	
	@Autowired private ServiceInstanceDependencyRepo serviceInstanceDependencyRepo;
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private ResourceAssembler resourceAssembler;
	
	@RequestMapping(
			value = "/search/find-by-dependent",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public PagedResources findByDependent(
			@RequestParam String key,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					sort = "dependency.key",
					direction = Direction.ASC)
			Pageable pageable) {
		
		Page<Object[]> results = serviceInstanceDependencyRepo.findByDependentWithCounts(key, pageable);
		val itemMeta = itemMetaLookup.getItemMeta(ServiceInstanceDependency.class);
		val proj = itemMeta.getProjectionNode(ApiVersion.V2, Projection.Cardinality.COLLECTION, "by-dependent");
		return toServiceInstanceDependencyPagedResources(results, proj);
	}
	
	@RequestMapping(
			value = "/search/find-by-dependency",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public PagedResources findByDependency(
			@RequestParam String key,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					sort = "dependent.key",
					direction = Direction.ASC)
			Pageable pageable) {
		
		Page<Object[]> results = serviceInstanceDependencyRepo.findByDependencyWithCounts(key, pageable);
		val itemMeta = itemMetaLookup.getItemMeta(ServiceInstanceDependency.class);
		val proj = itemMeta.getProjectionNode(ApiVersion.V2, Projection.Cardinality.COLLECTION, "by-dependency");
		return toServiceInstanceDependencyPagedResources(results, proj);
	}
	
	private PagedResources toServiceInstanceDependencyPagedResources(Page<Object[]> itemPage, ProjectionNode proj) {
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
			itemResources.add(toServiceInstanceDependencyResource(item, proj));
		}
		
		return new PagedResources(links, metadata, itemResources);
	}
	
	private Resource toServiceInstanceDependencyResource(Object[] data, ProjectionNode proj) {
		ServiceInstanceDependency sid = (ServiceInstanceDependency) data[0];
		Long numNodes = (data[1] == null ? 0L : (Long) data[1]);
		Long numHealthy = (data[2] == null ? 0L : (Long) data[2]);
		String percentHealthy = (numNodes == 0L ? "N/A" :
			percentFormat.format((double) numHealthy / (double) numNodes));
		
		// TODO Remove hardcodes
		String healthKey = (numHealthy < numNodes ? "warning" : "success");
		
		Resource siResource = resourceAssembler.toResource(ApiVersion.V2, sid, proj);
		siResource.setProperty("numNodes", numNodes);
		siResource.setProperty("numHealthy", numHealthy);
		siResource.setProperty("percentHealthy", percentHealthy);
		siResource.setProperty("healthKey", healthKey);
		return siResource;
	}
}
