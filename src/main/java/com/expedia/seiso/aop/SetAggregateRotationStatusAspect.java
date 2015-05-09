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
package com.expedia.seiso.aop;

import lombok.NonNull;
import lombok.extern.slf4j.XSlf4j;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.domain.service.ServiceInstanceService;

// Originally I tried putting this in a JPA entity listener, but this breaks with a ConcurrentModification Exception:
// 
// 1) https://hibernate.atlassian.net/browse/HHH-7537
// "In our case we had an entity manager query being invoked by some code called by our entity listener."
// "Hibernate specifically states this is not supported."
// 
// 2) See also the last sentence on this page:
// https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Web_Platform/5/html/Hibernate_Entity_Manager_Reference_Guide/listeners.html
// "A callback method must not invoke EntityManager or Query methods!"
// 
// 3) Finally see Java Persistence with Hibernate p. 557, which again states that entity listeners aren't allowed
// to use the entity manager. [WLW]
// 
// Ah, I think I know what's going on.
// The entity listeners fire after the session flush. Hibernate has a list of actions that it's processing.
// When the entity listener makes changes, this modifies the list of actions (Hibernate prunes useless updates, etc.),
// causing the observed ConcurrentModificationException. [WLW]

/**
 * <p>
 * Recalculates aggregate rotation statuses on nodes and node IP addresses.
 * </p>
 * <p>
 * Not totally convinced that this should be an aspect. It's business logic, and I'm thinking that we might want to
 * reserve aspects for more infrastructural concerns, such as firing data change notifications (see
 * {@link NotificationAspect}). But I put it here for now since it allows me to perform the recalculation without making
 * the core item service more complex.
 * </p>
 * 
 * @author Willie Wheeler
 */
//@Aspect
//@Order(AdvisorOrder.SET_AGGREGATE_ROTATION_STATUS_ADVISOR_ORDER)
@XSlf4j
public class SetAggregateRotationStatusAspect {
	@Autowired private ServiceInstanceService serviceInstanceService;
	@Autowired private RotationStatusRepo rotationStatusRepo;
	
	@Pointcut(Pointcuts.CREATE_ITEM_POINTCUT)
	private void createItemOps() { }
	
	@Pointcut(Pointcuts.UPDATE_ITEM_POINTCUT)
	private void updateItemOps() { }
	
	@Pointcut(Pointcuts.DELETE_ITEM_POINTCUT)
	private void deleteItemOps() { }
	
	@AfterReturning(pointcut = "createItemOps() && args(item, mergeAssociations)")
	public void recalcOnCreate(@NonNull Item item, boolean mergeAssociations) {
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
	
	/**
	 * @param srcItem
	 *            The data that {@link ItemService} merged into the persistent entity.
	 * @param destItem
	 *            The persistent entity.
	 * @param mergeAssociations
	 *            Flag indicating whether to merge associations.
	 */
	@AfterReturning(pointcut = "updateItemOps() && args(srcItem, destItem, mergeAssociations)")
	public void recalcOnUpdate(@NonNull Item srcItem, @NonNull Item destItem, boolean mergeAssociations) {
		if (destItem instanceof Endpoint) {
			recalcNipAndNode(((Endpoint) destItem).getIpAddress());
		} else if (destItem instanceof NodeIpAddress) {
			recalcNipAndNode((NodeIpAddress) destItem);
		}
	}
	
	@AfterReturning(pointcut = "deleteItemOps() && args(item)")
	public void recalcOnDelete(@NonNull Item item) {
		if (item instanceof Endpoint) {
			recalcNipAndNode(((Endpoint) item).getIpAddress());
		} else if (item instanceof NodeIpAddress) {
			recalcNipAndNode((NodeIpAddress) item);
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
