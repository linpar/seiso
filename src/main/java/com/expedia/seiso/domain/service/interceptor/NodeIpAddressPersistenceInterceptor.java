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
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.repo.EndpointRepo;
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.domain.service.ServiceInstanceService;
import com.expedia.serf.service.AbstractPersistenceInterceptor;

/**
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class NodeIpAddressPersistenceInterceptor extends AbstractPersistenceInterceptor {
	@Autowired private RotationStatusRepo rotationStatusRepo;
	@Autowired private EndpointRepo endpointRepo;
	@Autowired private NodeRepo nodeRepo;
	@Autowired private NodeIpAddressRepo nodeIpAddressRepo;
	@Autowired private ServiceInstanceService serviceInstanceService;
	
	@Override
	public void preCreate(Object entity) {
		replaceNullStatusesWithUnknown(entity);
	}
	
	@Override
	public void postCreate(Object entity) {
		NodeIpAddress nip = (NodeIpAddress) entity;
		createEndpointsForNodeIpAddress(nip);
	}
	
	@Override
	public void preUpdate(Object entity) {
		replaceNullStatusesWithUnknown(entity);
	}
	
	@Override
	public void postUpdate(Object entity) {
		NodeIpAddress nip = (NodeIpAddress) entity;
		Node node = nip.getNode();
		serviceInstanceService.recalculateAggregateRotationStatus(nip);
		nodeIpAddressRepo.save(nip);
		serviceInstanceService.recalculateAggregateRotationStatus(node);
		nodeRepo.save(node);
	}
	
	private void replaceNullStatusesWithUnknown(Object entity) {
		NodeIpAddress nip = (NodeIpAddress) entity;
		if (nip.getRotationStatus() == null) {
			nip.setRotationStatus(unknownRotationStatus());
		}
		if (nip.getAggregateRotationStatus() == null) {
			nip.setAggregateRotationStatus(unknownRotationStatus());
		}
	}
	
	private void createEndpointsForNodeIpAddress(NodeIpAddress nip) {
		Node node = nip.getNode();
		
		// Need to load the unknown rotation status and set it on the endpoint.
		// This is because Hibernate can decide to flush the persistence context at any time (e.g., before executing
		// queries) and we need to make sure that we don't save the endpoint unless it's actually ready to go.
		// I *think* the call to setIpAddress() ends up making the endpoint persistent because it adds the endpoint to
		// the NIP's list of endpoints, but I'm not positive.
		val unknownRotationStatus = rotationStatusRepo.findByKey(RotationStatus.UNKNOWN.getKey());
		
		val ports = nip.getNode().getServiceInstance().getPorts();
		log.info("Creating endpoints for nip={} and each of {} ports", nip.getIpAddress(), ports.size());
		ports.forEach(port -> {
			
			// The call to setIpAddress() in particular needs to result in updating *both* sides of the relationship.
			// Otherwise, when ServiceInstanceServiceImpl recalculates the aggregate rotation statuses, it won't see
			// that the NIP has endpoints, and will assign the "no-endpoints" status. [WLW]
			
			log.info("Creating endpoint: nip={}, port={}", nip.getIpAddress(), port.getNumber());
			// @formatter:off
			Endpoint endpoint = new Endpoint()
					.setRotationStatus(unknownRotationStatus)
					.setIpAddress(nip)
					.setPort(port);
			// @formatter:on
			
			log.trace("nip={} has {} endpoints", nip.getIpAddress(), nip.getEndpoints().size());
			endpointRepo.save(endpoint);
		});
		
		serviceInstanceService.recalculateAggregateRotationStatus(nip);
		nodeIpAddressRepo.save(nip);
		serviceInstanceService.recalculateAggregateRotationStatus(node);
		nodeRepo.save(node);
	}
	
	private RotationStatus unknownRotationStatus() {
		return rotationStatusRepo.findByKey("unknown");
	}
}
