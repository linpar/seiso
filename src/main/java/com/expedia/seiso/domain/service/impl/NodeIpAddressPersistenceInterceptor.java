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
package com.expedia.seiso.domain.service.impl;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.domain.service.ServiceInstanceService;
import com.expedia.serf.service.AbstractPersistenceInterceptor;

/**
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class NodeIpAddressPersistenceInterceptor extends AbstractPersistenceInterceptor {
	@Autowired private RotationStatusRepo rotationStatusRepo;
	@Autowired private ItemService itemService;
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
		serviceInstanceService.recalculateAggregateRotationStatus(nip);
		serviceInstanceService.recalculateAggregateRotationStatus(nip.getNode());
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
		log.info("Post-processing node IP address: {}", nip);

		// For some reason, when we save the endpoint, it doesn't see the NIP ID (even though we're able to see it
		// here). So we use a reference instead. [WLW]
		val nipRef = new NodeIpAddress();
		nipRef.setId(nip.getId());

		val ports = nip.getNode().getServiceInstance().getPorts();
		log.trace("Found {} ports", ports.size());
		ports.forEach(port -> {
			Endpoint endpoint = new Endpoint().setIpAddress(nipRef).setPort(port);
			log.info("Creating endpoint: {}", endpoint);
			itemService.save(endpoint, true);
		});
	}
	
	private RotationStatus unknownRotationStatus() {
		return rotationStatusRepo.findByKey("unknown");
	}
}
