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

import com.expedia.seiso.core.util.C;
import com.expedia.seiso.gateway.ActionGateway;
import com.expedia.seiso.gateway.model.ActionRequest;

/**
 * Action gateway implementation.
 * 
 * @author Willie Wheeler
 */
@Component
public class ActionGatewayImpl implements ActionGateway {
	@Autowired
	private AmqpTemplate amqpTemplate;

	// Asynchronous because we don't want failures here to impact the core app.
	// TODO Verify that this actually works. [WLW]
	@Async
	@Override
	public void publish(@NonNull ActionRequest request) {
		val exchange = C.AMQP_EXCHANGE_SEISO_ACTION_REQUESTS;
		val routingKey = request.getCode();
		amqpTemplate.convertAndSend(exchange, routingKey, request);
	}
}
