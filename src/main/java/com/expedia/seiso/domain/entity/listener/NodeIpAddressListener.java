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
package com.expedia.seiso.domain.entity.listener;

import java.util.Date;

import javax.persistence.PostPersist;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.context.ApplicationContext;

import com.expedia.seiso.core.util.ApplicationContextProvider;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.User;
import com.expedia.seiso.domain.repo.EndpointRepo;

/**
 * JPA 2 entity listener to auto-create endpoints after saving a node IP address.
 * 
 * @author Willie Wheeler
 */
@XSlf4j
public class NodeIpAddressListener {

	// Supports Mockito dependency injection.
	private ApplicationContext appContext;

	@PostPersist
	public void postPersist(@NonNull NodeIpAddress nodeIpAddress) {

		// For JPA, since v2.0 doesn't support dependency injection.
		if (appContext == null) {
			this.appContext = ApplicationContextProvider.getApplicationContext();
		}
		
		assert (appContext != null);

		val endpointRepo = appContext.getBean(EndpointRepo.class);
		val creator = new EndpointCreator(endpointRepo);
		creator.createEndpointsForNodeIpAddress(nodeIpAddress);
	}

	@AllArgsConstructor
	static class EndpointCreator {
		private EndpointRepo endpointRepo;

		public void createEndpointsForNodeIpAddress(NodeIpAddress nodeIpAddress) {
			log.info("Post-processing node IP address: {}", nodeIpAddress);

			// val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			val user = (User) null;
			val now = new Date();

			// For some reason, when we save the endpoint, it doesn't see the NIP ID (even though we're able to see it
			// here). So we use a reference instead. [WLW]
			val nipRef = new NodeIpAddress();
			nipRef.setId(nodeIpAddress.getId());

			val ports = nodeIpAddress.getNode().getServiceInstance().getPorts();
			log.trace("Found {} ports", ports.size());
			for (val port : ports) {
				val endpoint = new Endpoint().setIpAddress(nipRef).setPort(port);
				log.info("Creating endpoint: {}", endpoint);
				endpointRepo.save(endpoint);
			}
		}
	}
}
