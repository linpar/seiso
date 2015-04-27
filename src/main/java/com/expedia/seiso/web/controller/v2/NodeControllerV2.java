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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.web.PEResource;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.web.MediaTypes;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2/nodes")
@SuppressBasePath
@XSlf4j
public class NodeControllerV2 {
	@Autowired private NodeIpAddressRepo nipRepo;
	
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
