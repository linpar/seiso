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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.expedia.seiso.conf.CustomProperties;
import com.expedia.seiso.web.jackson.orig.OrigMapper;
import com.expedia.seiso.web.jackson.orig.OrigModule;
import com.expedia.seiso.web.jackson.orig.OrigPagedResourcesSerializer;
import com.expedia.seiso.web.jackson.orig.OrigResourceAssembler;
import com.expedia.seiso.web.jackson.orig.OrigResourceSerializer;
import com.expedia.seiso.web.jackson.orig.OrigResourcesSerializer;
import com.expedia.serf.SerfProperties;
import com.expedia.serf.hypermedia.hal.HalMapper;
import com.expedia.serf.hypermedia.hal.HalModule;
import com.expedia.serf.hypermedia.hal.HalPagedResourcesSerializer;
import com.expedia.serf.hypermedia.hal.HalResourceAssembler;
import com.expedia.serf.hypermedia.hal.HalResourceSerializer;
import com.expedia.serf.hypermedia.hal.HalResourcesSerializer;

/**
 * @author Willie Wheeler
 */
@Configuration
public class SeisoCoreConfig {
	
	@Bean
	public SerfProperties serfProperties() { return new SerfProperties(); }
	
	@Bean
	public CustomProperties customProperties() { return new CustomProperties(); }
	
	
	// =================================================================================================================
	// Jackson object mappers
	// =================================================================================================================
	
	// V1
	@Bean
	public OrigMapper origMapper() {
		val assembler = new OrigResourceAssembler();
		// @formatter:off
		return new OrigMapper(new OrigModule(
				new OrigResourceSerializer(assembler),
				new OrigResourcesSerializer(assembler),
				new OrigPagedResourcesSerializer(assembler)));
		// @formatter:on
	}
	
	// V2
	// Putting this here since both AMQP and REST API need it.
	// Actually the NotificationGatewayImpl uses the ResourceAssembler too, so we may need to move that here.
	@Bean
	public HalMapper halMapper() {
		val assembler = new HalResourceAssembler();
		// @formatter:off
		return new HalMapper(new HalModule(
				new HalResourceSerializer(assembler),
				new HalResourcesSerializer(assembler),
				new HalPagedResourcesSerializer(assembler)));
		// @formatter:on
	}
}
