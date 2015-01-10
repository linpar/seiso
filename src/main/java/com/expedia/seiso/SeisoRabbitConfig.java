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
package com.expedia.seiso;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.expedia.seiso.core.util.C;
import com.expedia.seiso.web.jackson.hal.HalMapper;

// TODO Do we want this?
//@EnableRabbit

/**
 * @author Willie Wheeler
 */
@Configuration
@XSlf4j
public class SeisoRabbitConfig {
	@Autowired private CachingConnectionFactory connectionFactory;
	@Autowired private HalMapper halMapper;
	
	@Bean
	public RabbitAdmin rabbitAdmin() {
		log.trace("connectionFactory.host={}", connectionFactory.getHost());
		val admin = new RabbitAdmin(connectionFactory);
		admin.declareExchange(seisoNotificationsExchange());
		admin.declareExchange(seisoActionRequestsExchange());
		return admin;
	}

	@Bean
	public Exchange seisoNotificationsExchange() {
		val exchange = new TopicExchange(C.AMQP_EXCHANGE_SEISO_NOTIFICATIONS);
		// exchange.setAdminsThatShouldDeclare(rabbitAdmin());
		return exchange;
	}

	@Bean
	public Exchange seisoActionRequestsExchange() {
		// Use a direct exchange since we want to route requests according to their request codes.
		val exchange = new DirectExchange(C.AMQP_EXCHANGE_SEISO_ACTION_REQUESTS);
		return exchange;
	}
	
	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter() {
		val converter = new Jackson2JsonMessageConverter();
		converter.setJsonObjectMapper(halMapper);
		return converter;
	}
	
	@Bean
	public AmqpTemplate amqpTemplate() {
		val template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}
}
