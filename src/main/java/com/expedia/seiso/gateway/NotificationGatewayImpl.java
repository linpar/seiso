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
package com.expedia.seiso.gateway;

import lombok.NonNull;
import lombok.val;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.expedia.seiso.SeisoProperties;
import com.expedia.seiso.domain.entity.Item;

/**
 * @author Willie Wheeler
 */
@Component
public class NotificationGatewayImpl implements NotificationGateway {
	@Autowired private SeisoProperties seisoProperties;
	@Autowired private AmqpTemplate amqpTemplate;

	@Override
	public void notify(@NonNull Item item, @NonNull String itemKey, @NonNull String operation) {
		val itemType = item.getClass();
		val itemTypeName = itemType.getSimpleName();
		val exchange = seisoProperties.getChangeNotificationExchange();
		val notification = new ItemNotification(itemTypeName, itemKey, operation);
		val routingKey = itemTypeName + "." + operation;
		amqpTemplate.convertAndSend(exchange, routingKey, notification);
	}
}
