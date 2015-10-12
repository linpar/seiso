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
package com.expedia.seiso.service.impl;

import static com.expedia.seiso.entity.RotationStatus.DISABLED;
import static com.expedia.seiso.entity.RotationStatus.ENABLED;
import static com.expedia.seiso.entity.RotationStatus.EXCLUDED;
import static com.expedia.seiso.entity.RotationStatus.NO_ENDPOINTS;
import static com.expedia.seiso.entity.RotationStatus.PARTIAL;
import static com.expedia.seiso.entity.RotationStatus.UNKNOWN;

import java.util.List;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expedia.seiso.entity.Endpoint;
import com.expedia.seiso.entity.Node;
import com.expedia.seiso.entity.NodeIpAddress;
import com.expedia.seiso.entity.RotationStatus;
import com.expedia.seiso.repo.RotationStatusRepo;
import com.expedia.seiso.repo.ServiceInstanceRepo;
import com.expedia.seiso.resource.BreakdownItem;
import com.expedia.seiso.resource.NodeSummary;
import com.expedia.seiso.service.ServiceInstanceService;

/**
 * @author Willie Wheeler
 */
@Service
@Transactional
@XSlf4j
public class ServiceInstanceServiceImpl implements ServiceInstanceService {
	@Autowired private ServiceInstanceRepo serviceInstanceRepo;
	@Autowired private RotationStatusRepo rotationStatusRepo;
	
	@Override
	public NodeSummary getNodeSummary(@NonNull Long id) {
		return serviceInstanceRepo.getServiceInstanceNodeSummary(id);
	}

	@Override
	public List<BreakdownItem> getHealthBreakdown(@NonNull Long id) {
		return serviceInstanceRepo.getServiceInstanceHealthBreakdown(id);
	}

	@Override
	public List<BreakdownItem> getRotationBreakdown(@NonNull Long id) {
		return serviceInstanceRepo.getServiceInstanceRotationBreakdown(id);
	}

	@Override
	public void recalculateAggregateRotationStatus(@NonNull Node node) {
		log.trace("Recalculating node aggregate rotation status: node={}", node.getName());
		
		RotationStatus nodeStatus = null;
		
		val nips = node.getIpAddresses();
		
		if (nips.isEmpty()) {
			nodeStatus = NO_ENDPOINTS;
		} else {
			int numNips = nips.size();
			int numEnabled = 0;
			int numDisabled = 0;
			int numExcluded = 0;
			int numPartial = 0;
			int numNoEndpoints = 0;
					
			for (NodeIpAddress nip : nips) {
				RotationStatus nipStatus = nip.getAggregateRotationStatus();
				if (ENABLED.equals(nipStatus)) {
					numEnabled++;
				} else if (DISABLED.equals(nipStatus)) {
					numDisabled++;
				} else if (EXCLUDED.equals(nipStatus)) {
					numExcluded++;
				} else if (PARTIAL.equals(nipStatus)) {
					numPartial++;
				} else if (NO_ENDPOINTS.equals(nipStatus)) {
					numNoEndpoints++;
				}
			}
			
			if (numEnabled == numNips) {
				nodeStatus = ENABLED;
			} else if (numDisabled == numNips) {
				nodeStatus = DISABLED;
			} else if (numExcluded == numNips) {
				nodeStatus = EXCLUDED;
			} else if (numNoEndpoints == numNips) {
				nodeStatus = NO_ENDPOINTS;
			} else if (numEnabled > 0 || numPartial > 0) {
				nodeStatus = PARTIAL;
			} else {
				nodeStatus = UNKNOWN;
			}
		}
		
		String nodeStatusKey = nodeStatus.getKey();
		log.trace("Setting node rotation status to {}", nodeStatusKey);
		val persistentNodeStatus = rotationStatusRepo.findByKey(nodeStatusKey);
		node.setAggregateRotationStatus(persistentNodeStatus);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.domain.service.ServiceInstanceService#recalculateAggregateRotationStatus(com.expedia.seiso.domain.entity.NodeIpAddress)
	 */
	@Override
	@Transactional
	public void recalculateAggregateRotationStatus(@NonNull NodeIpAddress nip) {
		log.trace("Recalculating node IP address aggregate rotation status: nip={}", nip.getIpAddress());
		
		val nipRotStatus = nip.getRotationStatus();
		
		if (nipRotStatus == null) {
			throw new IllegalStateException("nipRotStatus can't be null");
		}
		
		RotationStatus nipAggRotStatus = null;
		
		val endpoints = nip.getEndpoints();
		log.trace("Found {} endpoints", endpoints.size());
		
		if (endpoints.isEmpty()) {
			nipAggRotStatus = NO_ENDPOINTS;
		} else if (DISABLED.equals(nipRotStatus)) {
			nipAggRotStatus = DISABLED;
		} else if (EXCLUDED.equals(nipRotStatus)) {
			nipAggRotStatus = EXCLUDED;
		} else {
			int numEndpoints = endpoints.size();
			int numEnabled = 0;
			int numDisabled = 0;
			int numExcluded = 0;
			
			for (Endpoint endpoint : endpoints) {
				RotationStatus endpointRotStatus = endpoint.getRotationStatus();
				if (ENABLED.equals(endpointRotStatus)) {
					numEnabled++;
				} else if (DISABLED.equals(endpointRotStatus)) {
					numDisabled++;
				} else if (EXCLUDED.equals(endpointRotStatus)) {
					numExcluded++;
				}
			}
			
			if (ENABLED.equals(nipRotStatus)) {
				if (numEnabled == numEndpoints) {
					nipAggRotStatus = ENABLED;
				} else if (numDisabled == numEndpoints) {
					nipAggRotStatus = DISABLED;
				} else if (numExcluded == numEndpoints) {
					nipAggRotStatus = EXCLUDED;
				} else if (numEnabled > 0) {
					nipAggRotStatus = PARTIAL;
				} else {
					nipAggRotStatus = UNKNOWN;
				}
			} else if (UNKNOWN.equals(nipRotStatus)) {
				if (numExcluded == numEndpoints) {
					nipAggRotStatus = EXCLUDED;
				} else {
					nipAggRotStatus = UNKNOWN;
				}
			} else {
				// This count occur if somebody adds or renames rotation statuses.
				// But we don't expect it.
				nipAggRotStatus = UNKNOWN;
			}
		}
		
		String nipAggRotStatusKey = nipAggRotStatus.getKey();
		log.trace("Setting node IP address aggregate rotation status to {}", nipAggRotStatusKey);
		RotationStatus persistentNipAggRotStatus = rotationStatusRepo.findByKey(nipAggRotStatusKey);
		nip.setAggregateRotationStatus(persistentNipAggRotStatus);
	}
}
