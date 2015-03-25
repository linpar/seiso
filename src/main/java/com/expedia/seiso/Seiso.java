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

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Willie Wheeler
 */
@Configuration
@Import({
	SeisoCoreConfig.class,
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
	public EmbeddedServletContainerFactory servletContainer() {
		val tomcat = new TomcatEmbeddedServletContainerFactory() {
			
			@Override
			protected void postProcessContext(Context context) {
				val securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				val collection = new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};
		tomcat.addAdditionalTomcatConnectors(createHttpConnector());
		return tomcat;
	}
	
	private Connector createHttpConnector() {
		val connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//		val protocol = (Http11NioProtocol) connector.getProtocolHandler();
//		protocol.setSSLEnabled(false);
		connector.setScheme("http");
		connector.setSecure(false);
		connector.setPort(8080);
		connector.setRedirectPort(8443);
		return connector;
	}
}
