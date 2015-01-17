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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.expedia.seiso.core.config.CustomProperties;
import com.expedia.seiso.core.util.ApplicationContextProvider;
import com.expedia.seiso.web.jackson.hal.HalMapper;
import com.expedia.seiso.web.jackson.hal.HalModule;
import com.expedia.seiso.web.jackson.hal.HalPagedResourcesSerializer;
import com.expedia.seiso.web.jackson.hal.HalResourceAssembler;
import com.expedia.seiso.web.jackson.hal.HalResourceSerializer;
import com.expedia.seiso.web.jackson.hal.HalResourcesSerializer;

/**
 * @author Willie Wheeler
 */
@Configuration
@Import({
	SeisoRabbitConfig.class,
	SeisoDomainConfig.class,
	SeisoWebConfigBeans.class,
	SeisoWebConfigBeansV1.class,
	SeisoWebConfigBeansV2.class,
	SeisoWebConfig.class,
	SeisoWebSecurityConfig.class
})

// Exclude HTTP message converters since they expect a single ObjectMapper, while we have two.
@EnableAutoConfiguration(exclude = { HttpMessageConvertersAutoConfiguration.class })

//@EnableAspectJAutoProxy
@EnableConfigurationProperties
public class Seiso {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Seiso.class, args);
	}
	
	@Bean
	public CustomProperties customProperties() { return new CustomProperties(); }
	
	@Bean
	public ApplicationContextProvider applicationContextProvider() {
		return new ApplicationContextProvider();
	}
	
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
