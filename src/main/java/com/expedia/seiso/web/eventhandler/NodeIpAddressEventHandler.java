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

import java.util.List;

import javax.annotation.PostConstruct;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.Domain;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.repo.EndpointRepo;
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.web.assembler.ServiceInstanceService;

/**
 * @author Willie Wheeler
 */
@RepositoryEventHandler(NodeIpAddress.class)
@Component
@Slf4j
public class NodeIpAddressEventHandler {
	@Autowired private NodeRepo nodeRepo;
	@Autowired private NodeIpAddressRepo nodeIpAddressRepo;
	@Autowired private EndpointRepo endpointRepo;
	@Autowired private RotationStatusRepo rotationStatusRepo;
	@Autowired private ServiceInstanceService serviceInstanceService;
	
	private RotationStatus unknownRotationStatus;
	
	@PostConstruct
	public void postConstruct() {
		// Assume this doesn't change over time.
		this.unknownRotationStatus = rotationStatusRepo.findByKey(Domain.UNKNOWN_ROTATION_STATUS_KEY);
	}
	
	/**
	 * If the rotation status or aggregate rotation status is {@code null}, we initialize it to the corresponding
	 * "unknown" status entity instead of leaving it null. This allows the UI to render missing/unknown statuses without
	 * doing explicit null checks.
	 * 
	 * @param nip
	 *            node IP address to create
	 */
	@HandleBeforeCreate
	public void handleBeforeCreate(NodeIpAddress nip) {
		replaceNullStatusesWithUnknown(nip);
	}
	
	@HandleAfterCreate
	public void handleAfterCreate(NodeIpAddress nip) {
		createEndpointsForNodeIpAddress(nip);
	}
	
	/**
	 * <p>
	 * If the rotation status or aggregate rotation status is {@code null}, we initialize it to the corresponding
	 * "unknown" status entity instead of leaving it null. This allows the UI to render missing/unknown statuses without
	 * doing explicit null checks.
	 * </p>
	 * <p>
	 * Generally we shouldn't have to do this, since we try to prevent node IP addresses from having {@code null}
	 * statuses, but we're just being paranoid.
	 * </p>
	 * 
	 * @param nip
	 *            node IP address to save
	 */
	@HandleBeforeSave
	public void handleBeforeSave(NodeIpAddress nip) {
		replaceNullStatusesWithUnknown(nip);
	}
	
	@HandleAfterSave
	public void handleAfterSave(NodeIpAddress nip) {
		serviceInstanceService.recalculateAggregateRotationStatus(nip);
		nodeIpAddressRepo.save(nip);
		
		val node = nip.getNode();
		serviceInstanceService.recalculateAggregateRotationStatus(node);
		nodeRepo.save(node);
	}
	
	private void replaceNullStatusesWithUnknown(NodeIpAddress nip) {
		if (nip.getRotationStatus() == null) {
			nip.setRotationStatus(unknownRotationStatus);
		}
		if (nip.getAggregateRotationStatus() == null) {
			nip.setAggregateRotationStatus(unknownRotationStatus);
		}
	}

	private void createEndpointsForNodeIpAddress(NodeIpAddress nip) {		
		val ipAddress = nip.getIpAddress();
		val node = nip.getNode();
		val sips = node.getServiceInstance().getPorts();
		log.info("Creating endpoints for nip={} and each of {} ports", ipAddress, sips.size());
		
		sips.forEach(sip -> {
			
			// Set the rotation status on the endpoint. Hibernate can flush the persistence context at any time (e.g.,
			// before executing queries) and we must ensure that the endpoint is ready. I *think* the call to
			// setIpAddress() ends up making the endpoint persistent because it adds the endpoint to the NIP's list of
			// endpoints, but I'm not positive.

			// The call to setIpAddress() in particular needs to result in updating *both* sides of the relationship.
			// Otherwise, when ServiceInstanceServiceImpl recalculates the aggregate rotation statuses, it won't see
			// that the NIP has endpoints, and will assign the "no-endpoints" status. [WLW]
			
			// For some reason, gradle assemble (and hence Travis CI) craps out when I use a val here. I assume it has
			// something to do with the lambda. So use Endpoint. [WLW]
			log.info("Creating endpoint: nip={}, port={}", ipAddress, sip.getNumber());
			// @formatter:off
			Endpoint endpoint = new Endpoint()
					.setRotationStatus(unknownRotationStatus)
					.setIpAddress(nip)
					.setPort(sip);
			// @formatter:on

			log.trace("nip={} has {} endpoints", ipAddress, nip.getEndpoints().size());
			endpointRepo.save(endpoint);
		});

		serviceInstanceService.recalculateAggregateRotationStatus(nip);
		nodeIpAddressRepo.save(nip);
		
		serviceInstanceService.recalculateAggregateRotationStatus(node);
		nodeRepo.save(node);
	}
}
