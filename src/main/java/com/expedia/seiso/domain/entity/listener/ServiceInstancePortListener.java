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

import javax.persistence.PostPersist;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.context.ApplicationContext;

import com.expedia.seiso.core.util.ApplicationContextProvider;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.repo.EndpointRepo;

/**
 * JPA 2 entity listener to auto-create endpoints after saving a service instance port.
 * 
 * @author Willie Wheeler
 */
@XSlf4j
public class ServiceInstancePortListener {

	// Supports Mockito dependency injection.
	private ApplicationContext appContext;

	@PostPersist
	public void postPersist(@NonNull ServiceInstancePort port) {

		// For JPA, since v2.0 doesn't support dependency injection.
		if (appContext == null) {
			this.appContext = ApplicationContextProvider.getApplicationContext();
		}
		
		assert (appContext != null);

		val endpointRepo = appContext.getBean(EndpointRepo.class);
		val creator = new EndpointCreator(endpointRepo);
		creator.createEndpointsForPort(port);
	}

	@AllArgsConstructor
	static class EndpointCreator {
		private EndpointRepo endpointRepo;

		public void createEndpointsForPort(ServiceInstancePort port) {
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
					endpointRepo.save(endpoint);
				}
			}
		}
	}
}
