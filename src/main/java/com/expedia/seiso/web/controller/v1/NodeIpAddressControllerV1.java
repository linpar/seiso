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

import javax.transaction.Transactional;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.key.NodeIpAddressKey;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.web.controller.PEResource;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.seiso.web.hateoas.Resource;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v1")
@Transactional
public class NodeIpAddressControllerV1 {
	private static final String NODE_IP_ADDRESS_URI_TEMPLATE = "/nodes/{nodeName}/ip-addresses/{ipAddress}";
	
	@Autowired private BasicItemDelegate basicItemDelegate;
	@Autowired private NodeRepo nodeRepo;
	
	@RequestMapping(
			value = NODE_IP_ADDRESS_URI_TEMPLATE,
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Resource get(@PathVariable String nodeName, @PathVariable String ipAddress) {
		val itemKey = new NodeIpAddressKey(nodeName, ipAddress);
		return basicItemDelegate.getOne(itemKey);
	}

	@RequestMapping(
			value = NODE_IP_ADDRESS_URI_TEMPLATE,
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void put(@PathVariable String nodeName, @PathVariable String ipAddress, PEResource nipResource) {
		val node = nodeRepo.findByName(nodeName);
		val serviceInstance = node.getServiceInstance();

		// Enrich the node IP address so we can save it. [WLW]
		val nipData = (NodeIpAddress) nipResource.getItem();
		nipData.setNode(node);
		nipData.setIpAddress(ipAddress);
		nipData.getIpAddressRole().setServiceInstance(serviceInstance);
		basicItemDelegate.put(nipData, true);
	}

	@RequestMapping(value = NODE_IP_ADDRESS_URI_TEMPLATE, method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String nodeName, @PathVariable String ipAddress) {
		val itemKey = new NodeIpAddressKey(nodeName, ipAddress);
		basicItemDelegate.delete(itemKey);
	}
}
