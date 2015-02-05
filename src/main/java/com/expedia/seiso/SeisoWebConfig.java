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

import java.util.List;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.expedia.seiso.domain.meta.ItemMetaLookup;

// Don't use @EnableWebMvc here since we are using WebMvcConfigurationSupport directly. [WLW]

/**
 * Seiso web configuration.
 * 
 * @author Willie Wheeler
 */
@Configuration
public class SeisoWebConfig extends WebMvcConfigurationSupport {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private List<HandlerMethodArgumentResolver> argumentResolvers;
	@Autowired private List<HttpMessageConverter<?>> httpMessageConverters;
	
	@Override
	protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.addAll(argumentResolvers);
	}

	@Override
	protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		// @formatter:off
		configurer
				.favorPathExtension(false)
				.favorParameter(false)
				.ignoreAcceptHeader(false)
				.useJaf(false)
				// https://github.com/ExpediaDotCom/seiso/issues/48
				// Use application/json as the default to avoid a breaking change to the v1 API.
//				.defaultContentType(MediaTypes.APPLICATION_HAL_JSON);
				.defaultContentType(MediaType.APPLICATION_JSON);
		// @formatter:on
	}
	
	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.addAll(httpMessageConverters);
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

	@Override
	protected void addViewControllers(ViewControllerRegistry registry) {
		// Lifted from org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration. [WLW]
		registry.addViewController("/").setViewName("forward:/index.html");
	}

	@Override
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		val mapping = new RequestMappingHandlerMapping();
		mapping.setOrder(0);
		mapping.setInterceptors(getInterceptors());
		mapping.setContentNegotiationManager(mvcContentNegotiationManager());
		// Suppress suffix pattern matching since machine names can have periods. [WLW]
		mapping.setUseSuffixPatternMatch(false);
		return mapping;
	}
}
