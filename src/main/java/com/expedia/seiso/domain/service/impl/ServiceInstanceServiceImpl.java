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

import java.util.List;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expedia.seiso.domain.dto.BreakdownItem;
import com.expedia.seiso.domain.dto.NodeSummary;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.domain.repo.ServiceInstanceRepo;
import com.expedia.seiso.domain.service.ServiceInstanceService;

/**
 * @author Willie Wheeler
 */
@Service
@Transactional
@XSlf4j
public class ServiceInstanceServiceImpl implements ServiceInstanceService {
	@Autowired private ServiceInstanceRepo serviceInstanceRepo;
	@Autowired private RotationStatusRepo rotationStatusRepo;
	
	/* (non-Javadoc)
	 * @see com.expedia.seiso.domain.service.ServiceInstanceService#getNodeSummary(java.lang.String)
	 */
	@Override
	public NodeSummary getNodeSummary(@NonNull String key) {
		return serviceInstanceRepo.getServiceInstanceNodeSummary(key);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.domain.service.ServiceInstanceService#getHealthBreakdown(java.lang.String)
	 */
	@Override
	public List<BreakdownItem> getHealthBreakdown(@NonNull String key) {
		return serviceInstanceRepo.getServiceInstanceHealthBreakdown(key);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.domain.service.ServiceInstanceService#getRotationBreakdown(java.lang.String)
	 */
	@Override
	public List<BreakdownItem> getRotationBreakdown(@NonNull String key) {
		return serviceInstanceRepo.getServiceInstanceRotationBreakdown(key);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.domain.service.ServiceInstanceService#recalculateAggregateRotationStatus(com.expedia.seiso.domain.entity.Node)
	 */
	@Override
	public void recalculateAggregateRotationStatus(@NonNull Node node) {
		log.trace("Recalculating node aggregate rotation status");
		
		val nips = node.getIpAddresses();
		int numNips = nips.size();
		int numEnabled = 0;
		int numDisabled = 0;
		int numExcluded = 0;
		int numPartial = 0;
		int numNoEndpoints = 0;
		int numUnknown = 0;
		
		String nodeKey = null;
		
		for (NodeIpAddress nip : nips) {
			String nipKey = nip.getAggregateRotationStatus().getKey();
			if ("enabled".equals(nipKey)) {
				numEnabled++;
			} else if ("disabled".equals(nipKey)) {
				numDisabled++;
			} else if ("excluded".equals(nipKey)) {
				numExcluded++;
			} else if ("partial".equals(nipKey)) {
				numPartial++;
			} else if ("no-endpoints".equals(nipKey)) {
				numNoEndpoints++;
			} else {
				numUnknown++;
			}
		}
		
		if (numEnabled == numNips) {
			nodeKey = "enabled";
		} else if (numDisabled == numNips) {
			nodeKey = "disabled";
		} else if (numExcluded == numNips) {
			nodeKey = "excluded";
		} else if (numNoEndpoints == numNips) {
			nodeKey = "no-endpoints";
		} else if (numEnabled > 0 || numPartial > 0) {
			nodeKey = "partial";
		} else if (numDisabled > 0) {
			nodeKey = "disabled";
		} else {
			nodeKey = "unknown";
		}
		
		log.trace("Setting node rotation status to {}", nodeKey);
		val nodeStatus = rotationStatusRepo.findByKey(nodeKey);
		node.setAggregateRotationStatus(nodeStatus);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.domain.service.ServiceInstanceService#recalculateAggregateRotationStatus(com.expedia.seiso.domain.entity.NodeIpAddress)
	 */
	@Override
	@Transactional
	public void recalculateAggregateRotationStatus(@NonNull NodeIpAddress nip) {
		log.trace("Recalculating node IP address aggregate rotation status");
		
		val nipRotStatus = nip.getRotationStatus();
		
		if (nipRotStatus == null) {
			throw new IllegalStateException("nipRotStatus can't be null");
		}
		
		val nipRotStatusKey = nipRotStatus.getKey();
		val endpoints = nip.getEndpoints();
		
		String nipAggRotStatusKey = null;
		
		if ("disabled".equals(nipRotStatusKey)) {
			nipAggRotStatusKey = "disabled";
		} else if ("excluded".equals(nipRotStatusKey)) {
			nipAggRotStatusKey = "excluded";
		} else if (endpoints.isEmpty()) {
			nipAggRotStatusKey = "no-endpoints";
		} else {
			int numEndpoints = endpoints.size();
			int numEnabled = 0;
			int numDisabled = 0;
			int numExcluded = 0;
			
			for (Endpoint endpoint : endpoints) {
				val endpointRotStatusKey = endpoint.getRotationStatus().getKey();
				if ("enabled".equals(endpointRotStatusKey)) {
					numEnabled++;
				} else if ("disabled".equals(endpointRotStatusKey)) {
					numDisabled++;
				} else if ("excluded".equals(endpointRotStatusKey)) {
					numExcluded++;
				}
			}
			
			if ("enabled".equals(nipRotStatusKey)) {
				if (numEnabled == numEndpoints) {
					nipAggRotStatusKey = "enabled";
				} else if (numDisabled == numEndpoints) {
					nipAggRotStatusKey = "disabled";
				} else if (numExcluded == numEndpoints) {
					nipAggRotStatusKey = "excluded";
				} else if (numEnabled > 0) {
					nipAggRotStatusKey = "partial";
				} else {
					nipAggRotStatusKey = "unknown";
				}
			} else if ("unknown".equals(nipRotStatusKey)) {
				if (numDisabled == numEndpoints) {
					nipAggRotStatusKey = "disabled";
				} else if (numExcluded == numEndpoints) {
					nipAggRotStatusKey = "excluded";
				} else {
					nipAggRotStatusKey = "unknown";
				}
			} else {
				// This count occur if somebody adds or renames rotation statuses.
				// But we don't expect it.
				nipAggRotStatusKey = "unknown";
			}
		}
		
		log.trace("Setting node IP address aggregate rotation status to {}", nipAggRotStatusKey);
		val nipAggRotStatus = rotationStatusRepo.findByKey(nipAggRotStatusKey);
		nip.setAggregateRotationStatus(nipAggRotStatus);
	}
}
