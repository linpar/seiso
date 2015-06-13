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
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.expedia.seiso.conf.CustomProperties;
import com.expedia.seiso.integration.eos.connector.Eos;
import com.expedia.seiso.integration.eos.connector.impl.EosTemplate;

// Recent changes to this configuration:
// 
// 1. Removed actionRequestsExchange since we don't yet support such requests.
// 2. Removed custom HAL mapper here since it's unclear whether we will actually use this.
// 
// In both cases, see git history for details. [WLW]

// TODO Do we want this?
//@EnableRabbit

/**
 * @author Willie Wheeler
 */
@Configuration
@XSlf4j
public class SeisoIntegrationConfig {
	@Autowired private CachingConnectionFactory connectionFactory;
	@Autowired private CustomProperties customProperties;
	
	@Bean
	public RabbitAdmin rabbitAdmin() {
		log.trace("connectionFactory.host={}", connectionFactory.getHost());
		val admin = new RabbitAdmin(connectionFactory);
		admin.declareExchange(seisoNotificationsExchange());
		return admin;
	}

	@Bean
	public Exchange seisoNotificationsExchange() {
		return new TopicExchange(customProperties.getChangeNotificationExchange());
	}
	
	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter() {
		val converter = new Jackson2JsonMessageConverter();
//		converter.setJsonObjectMapper(halMapper);
		return converter;
	}
	
	@Bean
	public AmqpTemplate amqpTemplate() {
		val template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}
	
	@Bean
	public Eos eos() {
		return new EosTemplate();
	}
}
