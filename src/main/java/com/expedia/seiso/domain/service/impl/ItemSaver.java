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

import javax.transaction.Transactional;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.repo.HealthStatusRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.domain.service.ServiceInstanceService;
import com.expedia.serf.util.SerfReflectionUtils;

// TODO Use pluggable interceptors instead of pre/post create/update. [WLW]

/**
 * Service delegate to save items to the database.
 * 
 * @author Willie Wheeler
 */
@Component
@Transactional
@XSlf4j
public class ItemSaver {
	@Autowired private ItemService itemService;
	@Autowired private ServiceInstanceService serviceInstanceService;
	@Autowired private Repositories repositories;
	@Autowired private RotationStatusRepo rotationStatusRepo;
	@Autowired private ItemMerger itemMerger;
	
	public void create(@NonNull Item item, boolean mergeAssociations) {
		val itemClass = item.getClass();
		val itemToSave = SerfReflectionUtils.createInstance(itemClass);
		itemMerger.merge(item, itemToSave, mergeAssociations);
		preCreate(itemToSave);
		val repo = (CrudRepository) repositories.getRepositoryFor(itemClass);
		repo.save(itemToSave);
		postCreate(itemToSave);
	}
	
	public void update(@NonNull Item itemData, @NonNull Item itemToSave, boolean mergeAssociations) {
		itemMerger.merge(itemData, itemToSave, mergeAssociations);
		preUpdate(itemToSave);
		val itemClass = itemData.getClass();
		val repo = (CrudRepository) repositories.getRepositoryFor(itemClass);
		repo.save(itemToSave);
		postUpdate(itemToSave);
	}
	
	private void preCreate(Item item) {
		replaceNullStatusesWithUnknown(item);
	}
	
	private void postCreate(Item item) {
		
		// endpoints
		if (item instanceof NodeIpAddress) {
			createEndpointsForNodeIpAddress((NodeIpAddress) item);
		} else if (item instanceof ServiceInstancePort) {
			createEndpointsForPort((ServiceInstancePort) item);
		}
		
		// aggregate rotation status
		if (item instanceof Endpoint) {
			recalcNipAndNode(((Endpoint) item).getIpAddress());
			
		} else if (item instanceof NodeIpAddress) {
			NodeIpAddress nip = (NodeIpAddress) item;
			
			// This is a new NIP so the rotation status is unknown. 
			nip.setRotationStatus(getUnknownRotationStatus());
			
			// This must result in the aggregate rotation status being non-null.
			recalcNipAndNode(nip);
			
			if (nip.getAggregateRotationStatus() == null) {
				throw new IllegalStateException("nip.aggregateRotationStatus is unexpectedly null");
			}
			
		} else if (item instanceof Node) {
			Node node = (Node) item;
			
			// This must result in the aggregate rotation status being non-null.
			recalcNode(node);
			
			if (node.getAggregateRotationStatus() == null) {
				throw new IllegalStateException("node.aggregateRotationStatus is unexpectedly null");
			}
		}
	}
	
	private void preUpdate(Item item) {
		replaceNullStatusesWithUnknown(item);
	}
	
	private void postUpdate(Item item) {
		if (item instanceof Endpoint) {
			recalcNipAndNode(((Endpoint) item).getIpAddress());
		} else if (item instanceof NodeIpAddress) {
			recalcNipAndNode((NodeIpAddress) item);
		}
	}
	
	private void replaceNullStatusesWithUnknown(Item item) {
		if (item instanceof Node) {
			Node node = (Node) item;
			if (node.getHealthStatus() == null) {
				node.setHealthStatus(unknownHealthStatus());
			}
			if (node.getAggregateRotationStatus() == null) {
				node.setAggregateRotationStatus(unknownRotationStatus());
			}
		} else if (item instanceof NodeIpAddress) {
			NodeIpAddress nip = (NodeIpAddress) item;
			if (nip.getRotationStatus() == null) {
				nip.setRotationStatus(unknownRotationStatus());
			}
			if (nip.getAggregateRotationStatus() == null) {
				nip.setAggregateRotationStatus(unknownRotationStatus());
			}
		} else if (item instanceof Endpoint) {
			Endpoint endpoint = (Endpoint) item;
			if (endpoint.getRotationStatus() == null) {
				endpoint.setRotationStatus(unknownRotationStatus());
			}
		}
	}
	
	private HealthStatus unknownHealthStatus() {
		val healthStatusRepo = (HealthStatusRepo) repositories.getRepositoryFor(HealthStatus.class);
		return healthStatusRepo.findByKey("unknown");
	}
	
	private RotationStatus unknownRotationStatus() {
		val rotationStatusRepo = (RotationStatusRepo) repositories.getRepositoryFor(RotationStatus.class);
		return rotationStatusRepo.findByKey("unknown");
	}
	
	private void createEndpointsForNodeIpAddress(NodeIpAddress nodeIpAddress) {
		log.info("Post-processing node IP address: {}", nodeIpAddress);

		// val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		val user = (User) null;
//		val now = new Date();

		// For some reason, when we save the endpoint, it doesn't see the NIP ID (even though we're able to see it
		// here). So we use a reference instead. [WLW]
		val nipRef = new NodeIpAddress();
		nipRef.setId(nodeIpAddress.getId());

		val ports = nodeIpAddress.getNode().getServiceInstance().getPorts();
		log.trace("Found {} ports", ports.size());
		ports.forEach(port -> {
			Endpoint endpoint = new Endpoint().setIpAddress(nipRef).setPort(port);
			log.info("Creating endpoint: {}", endpoint);
//			endpointRepo.save(endpoint);
			itemService.save(endpoint, true);
		});
	}
	
	private void createEndpointsForPort(ServiceInstancePort port) {
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
//				endpointRepo.save(endpoint);
				itemService.save(endpoint, true);
			}
		}
	}
	
	private RotationStatus getUnknownRotationStatus() {
		return rotationStatusRepo.findByKey("unknown");
	}
	
	private void recalcNipAndNode(NodeIpAddress nip) {
		log.trace("Recalculating NIP");
		serviceInstanceService.recalculateAggregateRotationStatus(nip);
		recalcNode(nip.getNode());
	}
	
	private void recalcNode(Node node) {
		log.trace("Recalculating node");
		serviceInstanceService.recalculateAggregateRotationStatus(node);
	}
}
