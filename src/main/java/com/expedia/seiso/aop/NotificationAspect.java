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

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.gateway.NotificationGateway;
import com.expedia.seiso.gateway.model.ItemNotification;

/**
 * Notification aspect. Allows us to avoid polluting the domain code with integration-related concerns.
 * 
 * @author Willie Wheeler
 */
@Aspect
@Order(AdvisorOrder.NOTIFICATION_ADVISOR_ORDER)
@XSlf4j
public class NotificationAspect {
	@Autowired private NotificationGateway notificationGateway;
	
	@Pointcut(Pointcuts.CREATE_ITEM_POINTCUT)
	private void createItemOps() { }
	
	@Pointcut(Pointcuts.UPDATE_ITEM_POINTCUT)
	private void updateItemOps() { }
	
	@Pointcut(Pointcuts.DELETE_ITEM_POINTCUT)
	private void deleteItemOps() { }
	
	@AfterReturning(pointcut = "createItemOps() && args(item, mergeAssociations)")
	public void notifyCreate(@NonNull Item item, boolean mergeAssociations) {
		log.trace("Sending create notification: item={}", item.itemKey());
		notificationGateway.notify(item, ItemNotification.OP_CREATE);
	}
	
	@AfterReturning(pointcut = "updateItemOps() && args(srcItem, destItem, mergeAssociations)")
	public void notifyUpdate(@NonNull Item srcItem, @NonNull Item destItem, boolean mergeAssociations) {
		log.trace("Sending update notification: item={}", srcItem.itemKey());
		notificationGateway.notify(destItem, ItemNotification.OP_UPDATE);
	}

	@AfterReturning(pointcut = "deleteItemOps() && args(item)")
	public void notifyDelete(@NonNull Item item) {
		log.trace("Sending delete notification: item={}", item.itemKey());
		notificationGateway.notify(item, ItemNotification.OP_DELETE);
	}
}
