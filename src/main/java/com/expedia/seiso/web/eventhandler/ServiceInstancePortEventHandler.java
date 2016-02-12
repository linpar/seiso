/* 
 * Copyright 2013-2016 the original author or authors.
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
package com.expedia.seiso.web.eventhandler;

import javax.annotation.PostConstruct;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.Domain;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.repo.EndpointRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.domain.service.RotationService;

/**
 * @author Willie Wheeler
 */
@RepositoryEventHandler(ServiceInstancePort.class)
@Component
@Slf4j
public class ServiceInstancePortEventHandler {
	@Autowired private EndpointRepo endpointRepo;
	@Autowired private RotationStatusRepo rotationStatusRepo;
	@Autowired private RotationService rotationService;
	
	private RotationStatus unknownRotationStatus;
	
	@PostConstruct
	public void postConstruct() {
		// Assume this doesn't change over time.
		this.unknownRotationStatus = rotationStatusRepo.findByKey(Domain.UNKNOWN_ROTATION_STATUS_KEY);
	}
	
	@HandleAfterCreate
	public void handleAfterCreate(ServiceInstancePort sip) {
		createEndpointsForPort(sip);
	}
	
	private void createEndpointsForPort(ServiceInstancePort sip) {
		log.info("Post-processing port insertion: id={}", sip.getId());
		val nodes = sip.getServiceInstance().getNodes();
		
		// For some reason, when we save the endpoint, it doesn't see the port IO, even though we can see it here. So
		// use a reference instead. [WLW]
		val sipRef = new ServiceInstancePort();
		sipRef.setId(sip.getId());
		
		for (val node : nodes) {
			val nips = node.getIpAddresses();
			for (val nip : nips) {
				// Set the unknown rotation status on the endpoint immediately. This is because Hibernate can decide to
				// flush the persistence context at any time (e.g., before executing queries) and we need to ensure that
				// we don't save the endpoint until it's ready. [WLW]
				// @formatter:off
				val endpoint = new Endpoint()
						.setRotationStatus(unknownRotationStatus)
						.setIpAddress(nip)
						.setPort(sipRef);
				// @formatter:on
				log.info("Creating endpoint: {}", endpoint);
				endpointRepo.save(endpoint);
			}
			rotationService.recalculateAggregateRotationStatus(node);
		}		
	}
}
