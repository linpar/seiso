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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.repo.HealthStatusRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.domain.service.ServiceInstanceService;
import com.expedia.serf.service.AbstractPersistenceInterceptor;

/**
 * @author Willie Wheeler
 */
@Component
public class NodePersistenceInterceptor extends AbstractPersistenceInterceptor {
	@Autowired private HealthStatusRepo healthStatusRepo;
	@Autowired private RotationStatusRepo rotationStatusRepo;
	@Autowired private ServiceInstanceService serviceInstanceService;
	
	@Override
	public void preCreate(Object entity) {
		replaceNullStatusesWithUnknown(entity);
	}
	
	@Override
	public void postCreate(Object entity) {
		Node node = (Node) entity;
		serviceInstanceService.recalculateAggregateRotationStatus(node);
	}
	
	@Override
	public void preUpdate(Object entity) {
		replaceNullStatusesWithUnknown(entity);
	}
	
	private void replaceNullStatusesWithUnknown(Object entity) {
		Node node = (Node) entity;
		if (node.getHealthStatus() == null) {
			node.setHealthStatus(unknownHealthStatus());
		}
		if (node.getAggregateRotationStatus() == null) {
			node.setAggregateRotationStatus(unknownRotationStatus());
		}
	}
	
	private HealthStatus unknownHealthStatus() {
		return healthStatusRepo.findByKey("unknown");
	}
	
	private RotationStatus unknownRotationStatus() {
		return rotationStatusRepo.findByKey("unknown");
	}
}
