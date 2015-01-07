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

import java.io.Serializable;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.expedia.seiso.core.util.C;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.meta.DynaItem;
import com.expedia.seiso.gateway.NotificationGateway;
import com.expedia.seiso.gateway.model.ConfigManagementEvent;

/**
 * Outbound notification gateway implementation.
 * 
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class NotificationGatewayImpl implements NotificationGateway {
//	@Autowired private AmqpTemplate amqpTemplate;

	// Asynchronous because we don't want failures here to impact the core app. For example, if RabbitMQ goes down, we
	// don't want Seiso to be unable to create/update/delete items.
	@Async
	public void notify(@NonNull Item item, @NonNull String operation) {
		val event = buildEvent(item, operation);
		val routingKey = event.getItemType() + "." + event.getOperation();
		log.info("Sending notification: itemType={}, itemKey={}, operation={}", event.getItemType(),
				event.getItemKey(), event.getOperation());
//		 amqpTemplate.convertAndSend(C.AMQP_EXCHANGE_SEISO_NOTIFICATIONS, routingKey, event);
	}

	private ConfigManagementEvent buildEvent(Item item, String operation) {
		val itemClass = item.getClass();
		val itemClassName = itemClass.getSimpleName();
		val dynaItem = new DynaItem(item);

		// val itemKey = item.itemKey();
		// FIXME This assumes a single item key, which isn't right. [WLW]
		val itemKey = dynaItem.getMetaKey();

		// FIXME Temporary special case for Eos
		if (item instanceof Node && ConfigManagementEvent.OP_UPDATE.equals(operation)) {
			return new HackyNodeUpdateEvent(itemClassName, itemKey, operation, item);
		}

		return new ConfigManagementEvent(itemClassName, itemKey, operation);
	}

	// FIXME Temporary hack to provide Eos with contextual information about nodes. Will remove once we have proper
	// update events in place. (The proper event will describe exactly what changed.) [WLW]
	public static class HackyNodeUpdateEvent extends ConfigManagementEvent {
		private Item item;

		public HackyNodeUpdateEvent(String itemClassName, Serializable itemKey, String operation, Item item) {
			super(itemClassName, itemKey, operation);
			this.item = item;
		}

		public Item getItem() {
			return item;
		}

		public void setItem(Item item) {
			this.item = item;
		}
	}
}
