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
package com.expedia.seiso.gateway.impl;

import lombok.NonNull;
import lombok.val;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.expedia.seiso.conf.CustomProperties;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.gateway.NotificationGateway;
import com.expedia.seiso.gateway.model.ItemNotification;
import com.expedia.seiso.web.assembler.ResourceAssembler;

/**
 * Outbound notification gateway implementation.
 * 
 * @author Willie Wheeler
 */
@Component
public class NotificationGatewayImpl implements NotificationGateway {
	@Autowired private AmqpTemplate amqpTemplate;
	@Autowired private ResourceAssembler resourceAssembler;
	@Autowired private CustomProperties customProperties;
	
	// Asynchronous because we don't want failures here to impact the core app. For example, if RabbitMQ goes down, we
	// don't want Seiso to be unable to create/update/delete items.
	@Async
	public void notify(@NonNull Item item, @NonNull String operation) {
		val itemType = item.getClass();
		val itemTypeName = itemType.getSimpleName();
		
		// TODO Currently just service instances. Suresh and I need to work out the details for a more general set of
		// notifications. [WLW]
		if (itemType == ServiceInstance.class) {
			val serviceInstance = (ServiceInstance) item;
			val exchange = customProperties.getChangeNotificationExchange();
			val notification = new ItemNotification(itemTypeName, serviceInstance.getKey(), operation);
			val routingKey = itemTypeName + "." + operation;
			amqpTemplate.convertAndSend(exchange, routingKey, notification);
		}
	}
}
