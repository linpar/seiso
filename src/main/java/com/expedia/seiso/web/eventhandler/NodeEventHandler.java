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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.Domain;
import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.repo.HealthStatusRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.gateway.NotificationGateway;
import com.expedia.seiso.web.controller.internal.GlobalSearchController;

import lombok.extern.slf4j.XSlf4j;

/**
 * @author Willie Wheeler
 */
@RepositoryEventHandler(Node.class)
@Component
@XSlf4j
public class NodeEventHandler {
	
	@Autowired
	private HealthStatusRepo healthStatusRepo;
	
	@Autowired
	private RotationStatusRepo rotationStatusRepo;
	
	@Autowired
	private NodeRepo nodeRepo;

	@Autowired
	private NotificationGateway notificationGateway;
	
	private HealthStatus unknownHealthStatus;
	private RotationStatus unknownRotationStatus;
	
	@PostConstruct
	public void postConstruct() {
		// Assume these don't change over time.
		this.unknownHealthStatus = healthStatusRepo.findByKey(Domain.UNKNOWN_HEALTH_STATUS_KEY);
		this.unknownRotationStatus = rotationStatusRepo.findByKey(Domain.UNKNOWN_ROTATION_STATUS_KEY);
	}

	/**
	 * If the health or aggregate rotation status is {@code null}, we initialize
	 * it to the corresponding "unknown" status entity instead of leaving it
	 * null. This allows the UI to render missing/unknown statuses without doing
	 * explicit null checks.
	 * 
	 * @param node
	 *            node to create
	 */
	@HandleBeforeCreate
	public void handleBeforeCreate(Node node) {
		replaceNullStatusesWithUnknown(node);
		log.info("Node created.");
		log.info(getNodeInfo(node));
	}
	
	@HandleAfterCreate
	public void handleAfterCreate(Node node) {
		notify(node, NotificationGateway.OP_CREATE);
	}

	/**
	 * <p>
	 * If the health or aggregate rotation status is {@code null}, we initialize
	 * it to the corresponding "unknown" status entity instead of leaving it
	 * null. This allows the UI to render missing/unknown statuses without doing
	 * explicit null checks.
	 * </p>
	 * <p>
	 * Generally we shouldn't have to do this, since we try to prevent nodes
	 * from having {@code null} statuses, but we're just being paranoid.
	 * </p>
	 * 
	 * @param node
	 *            node to save
	 */
	@HandleBeforeSave
	public void handleBeforeSave(Node node) {
		replaceNullStatusesWithUnknown(node);
		log.info("Node saved/updated.");
		log.info(getNodeInfo(node));
	}
	
	@HandleAfterSave
	public void handleAfterSave(Node node) {
		notify(node, NotificationGateway.OP_UPDATE);
		
		
	}

	@HandleAfterDelete
	public void handleAfterDelete(Node node) {
		log.info("Node deleted: " + node.getId());
		notify(node, NotificationGateway.OP_DELETE);
	}
	
	// TODO Move this somewhere else. It doesn't belong here. [WLW]
	private void replaceNullStatusesWithUnknown(Node node) {
		if (node.getHealthStatus() == null) {
			node.setHealthStatus(unknownHealthStatus);
		}
		if (node.getAggregateRotationStatus() == null) {
			node.setAggregateRotationStatus(unknownRotationStatus);
		}
	}
	
	private void notify(Node node, String op) {
		notificationGateway.notify(node, node.getName(), op);
	}
	
	private String getNodeInfo(Node node){
		StringBuilder content = new StringBuilder();
		content.append("Node attributes: \r\n");
		content.append("  Id:" + node.getId() + "\r\n");
		content.append("  Name: " + node.getName() + "\r\n");
		content.append("  Description: " + node.getDescription() + "\r\n");
		content.append("  Version:" + node.getVersion() + "\r\n");
		content.append("  Build Version:" + node.getBuildVersion() + "\r\n");
		
		content.append("  Health Status Link:" + node.getHealthStatusLink() + "\r\n");
		content.append("  Health Status Reason:" + node.getHealthStatusReason() + "\r\n");
		try {
			content.append("  Health Status Id:" + node.getHealthStatus().getId() + "\r\n");
		} catch (NullPointerException ex){
			content.append("  Health Status Id: Null\r\n");
		}
		try {
			content.append("  Machine Id:" + node.getMachine().getId() + "\r\n");
		} catch (NullPointerException ex){
			content.append("  Machine Id: Null\r\n");
		}
		try { 
			content.append("  Aggregation Rotation Status Id:" + node.getAggregateRotationStatus().getId() + "\r\n");
		} catch (NullPointerException ex){
			content.append("  Aggregation Rotation Status Id: Null\r\n");
		}
		try {
			content.append("  Service Instance Id:" + node.getServiceInstance().getId() + "\r\n");
		} catch (NullPointerException ex){
			content.append("  Service Instance Id: Null\r\n");
		}
		content.append("  Ip Addresses:\r\n");
		List<NodeIpAddress> addresses = node.getIpAddresses();
		for (NodeIpAddress address : addresses){
			content.append("    " + address.getIpAddress() + "\r\n");
		}
		return content.toString();
	}
}