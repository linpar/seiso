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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.Domain;
import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.repo.HealthStatusRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;

/**
 * @author Willie Wheeler
 */
@RepositoryEventHandler(Node.class)
@Component
public class NodeEventHandler {
	@Autowired private HealthStatusRepo healthStatusRepo;
	@Autowired private RotationStatusRepo rotationStatusRepo;
	
	private HealthStatus unknownHealthStatus;
	private RotationStatus unknownRotationStatus;
	
	@PostConstruct
	public void postConstruct() {
		// Assume these don't change over time.
		this.unknownHealthStatus = healthStatusRepo.findByKey(Domain.UNKNOWN_HEALTH_STATUS_KEY);
		this.unknownRotationStatus = rotationStatusRepo.findByKey(Domain.UNKNOWN_ROTATION_STATUS_KEY);
	}
	
	/**
	 * If the health or aggregate rotation status is {@code null}, we initialize it to the corresponding "unknown"
	 * status entity instead of leaving it null. This allows the UI to render missing/unknown statuses without doing
	 * explicit null checks.
	 * 
	 * @param node
	 *            node to create
	 */
	@HandleBeforeCreate
	public void handleBeforeCreate(Node node) {
		replaceNullStatusesWithUnknown(node);
	}
	
	/**
	 * <p>
	 * If the health or aggregate rotation status is {@code null}, we initialize it to the corresponding "unknown"
	 * status entity instead of leaving it null. This allows the UI to render missing/unknown statuses without doing
	 * explicit null checks.
	 * </p>
	 * <p>
	 * Generally we shouldn't have to do this, since we try to prevent nodes from having {@code null} statuses, but
	 * we're just being paranoid.
	 * </p>
	 * 
	 * @param node
	 *            node to save
	 */
	@HandleBeforeSave
	public void handleBeforeSave(Node node) {
		replaceNullStatusesWithUnknown(node);
	}
	
	private void replaceNullStatusesWithUnknown(Node node) {
		if (node.getHealthStatus() == null) {
			node.setHealthStatus(unknownHealthStatus);
		}
		if (node.getAggregateRotationStatus() == null) {
			node.setAggregateRotationStatus(unknownRotationStatus);
		}
	}
}
