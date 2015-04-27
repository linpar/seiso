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
import com.expedia.seiso.domain.repo.ServiceInstanceRepo;
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
	@Autowired private ServiceInstanceRepo serviceInstanceRepo;
	@Autowired @Qualifier("linkFactoryV2") private LinkFactory linkFactoryV2;
	
	@RequestMapping(
			value = "/{key}/node-stats",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Resource getNodeStats(@PathVariable String key) {
		val itemLinksV2 = linkFactoryV2.getItemLinks();
		
		val dummy = new ServiceInstance();
		dummy.setKey(key);

		val statsData = serviceInstanceRepo.getServiceInstanceNodeStats(key);
		
		val statsResource = new Resource();
		statsResource.addLink(itemLinksV2.serviceInstanceNodeStatsLink(Relations.SELF, dummy));
		statsResource.addLink(itemLinksV2.itemLink(Relations.UP, dummy));
		statsResource.setProperty("numNodes", statsData.getNumNodes());
		statsResource.setProperty("numHealthy", statsData.getNumHealthy());
		statsResource.setProperty("numEnabled", statsData.getNumEnabled());
		statsResource.setProperty("numHealthyGivenEnabled", statsData.getNumHealthyGivenEnabled());
		return statsResource;
	}
}
