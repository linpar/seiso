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

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.service.ServiceInstanceService;
import com.expedia.seiso.hypermedia.LinkFactory;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.hypermedia.Relations;
import com.expedia.serf.hypermedia.Resource;
import com.expedia.serf.web.MediaTypes;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2/service-instances")
@SuppressBasePath
public class ServiceInstanceControllerV2 {
	@Autowired private ServiceInstanceService serviceInstanceService;
	@Autowired @Qualifier("linkFactoryV2") private LinkFactory linkFactoryV2;
	
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
	
	private ServiceInstance getServiceInstance(String key) {
		val dummy = new ServiceInstance();
		dummy.setKey(key);
		return dummy;
	}
}
