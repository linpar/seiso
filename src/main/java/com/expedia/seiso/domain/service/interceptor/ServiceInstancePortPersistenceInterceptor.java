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
package com.expedia.seiso.domain.service.interceptor;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.serf.service.AbstractPersistenceInterceptor;

/**
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class ServiceInstancePortPersistenceInterceptor extends AbstractPersistenceInterceptor {
	@Autowired private ItemService itemService;
	
	@Override
	public void postCreate(Object entity) {
		createEndpointsForPort((ServiceInstancePort) entity);
	}
	
	private void createEndpointsForPort(ServiceInstancePort port) {
		log.info("Post-processing port insertion: id={}", port.getId());
		val nodes = port.getServiceInstance().getNodes();

		// For some reason, when we save the endpoint, it doesn't see the port ID (even though we're able to see it
		// here). So we use a reference instead. [WLW]
		val portRef = new ServiceInstancePort();
		portRef.setId(port.getId());

		for (val node : nodes) {
			val nodeIpAddresses = node.getIpAddresses();
			for (val nodeIpAddress : nodeIpAddresses) {
				val endpoint = new Endpoint().setIpAddress(nodeIpAddress).setPort(portRef);
				log.info("Creating endpoint: {}", endpoint);
				itemService.save(endpoint, true);
			}
		}
	}
}
