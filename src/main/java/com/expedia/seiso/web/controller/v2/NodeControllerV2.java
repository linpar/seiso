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

import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.repo.MachineRepo;
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.domain.repo.ServiceInstanceRepo;
import com.expedia.seiso.web.dto.v1.PEResource;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.hypermedia.hal.HalResource;
import com.expedia.serf.service.CrudService;
import com.expedia.serf.web.MediaTypes;
import com.expedia.serf.web.PersistentEntityResource;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2/nodes")
@SuppressBasePath
@Transactional
@XSlf4j
public class NodeControllerV2 {
	@Autowired private MachineRepo machineRepo;
	@Autowired private NodeRepo nodeRepo;
	@Autowired private NodeIpAddressRepo nipRepo;
	@Autowired private ServiceInstanceRepo serviceInstanceRepo;
	@Autowired private CrudService crudService;
	
	@RequestMapping(
			value = "/{name}",
			method = RequestMethod.PUT,
			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public void putNode(
			@PathVariable String name,
			@RequestParam(value = "include-state", defaultValue = "true") Boolean includeState,
			PersistentEntityResource peResource) {
		
		log.trace("Putting node: name={}, includeState={}", name, includeState);
		HalResource nodeData = peResource.getHalResource();
		
		// CrudService resolves entities by ID. Note that we override client-provided IDs.
		// TODO Move URI resolution to the PersistentEntityResourceResolver, and handle it generically.
//		Node node = nodeRepo.findByName(name);
//		nodeData.setId(node == null ? null : node.getId());
		
//		ServiceInstance siData = nodeData.getServiceInstance();
//		ServiceInstance si = serviceInstanceRepo.findByKey(siData.getKey());
//		nodeData.setServiceInstance(si);
//		
//		Machine machineData = nodeData.getMachine();
//		Machine machine = machineRepo.findByName(machineData.getName());
//		nodeData.setMachine(machine);
		
		log.trace("nodeData={}", nodeData);
		
		// Don't exclude name. That way we can rename nodes through the API.
		// TODO Use projections or something along those lines.
		// FIXME Currently this *never* includes state.
		final String[] excludeProps = new String[] { "healthStatus", "aggregateRotationStatus", "version" };
//		crudService.save(nodeData, null, excludeProps);
	}
	
	@RequestMapping(
			value = "/{name}/ip-addresses/{ipAddress}",
			method = RequestMethod.PUT,
			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public void putNodeIpAddress(
			@PathVariable String name,
			@PathVariable String ipAddress,
			PEResource peResource) {
		
		log.trace("Putting NIP: node={}, ipAddress={}", name, ipAddress);
	}
}
