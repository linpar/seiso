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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.expedia.seiso.domain.entity.Dashboard;
import com.expedia.seiso.domain.entity.DocLink;
import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.SeyrenCheck;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.seiso.hypermedia.ItemPaths;
import com.expedia.seiso.hypermedia.LinkFactory;
import com.expedia.seiso.web.SimplePropertyEntry;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.seiso.web.controller.v1.ResponseHeadersV1;
import com.expedia.seiso.web.jackson.orig.OrigMapper;
import com.expedia.seiso.web.resolver.v1.PEResourceResolver;
import com.expedia.seiso.web.resolver.v1.PEResourcesResolver;
import com.expedia.serf.SerfProperties;
import com.expedia.serf.exception.ConfigurationException;
import com.expedia.serf.hypermedia.hal.HalMapper;
import com.expedia.serf.meta.RepoMetaRegistry;
import com.expedia.serf.util.ResolverUtils;
import com.expedia.serf.web.MediaTypes;
import com.expedia.serf.web.PersistentEntityResourceResolver;
import com.expedia.serf.web.controller.ExceptionHandlerAdvice;

// Don't use @EnableWebMvc here since we are using WebMvcConfigurationSupport directly. [WLW]

/**
 * Seiso web configuration.
 * 
 * @author Willie Wheeler
 */
@Configuration
// Don't scan com.expedia.serf.web.controller yet, as it has incomplete controllers.
@ComponentScan(basePackages = "com.expedia.seiso.web.controller")
public class SeisoWebConfig extends WebMvcConfigurationSupport {
	@Autowired private SerfProperties serfProperties;
	@Autowired private OrigMapper origMapper;
	@Autowired private HalMapper halMapper;
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
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// Turn this off for now since NodeIpAddresses currently have an IP address as part of the key.
		configurer.setUseSuffixPatternMatch(false);
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
	
	/*
	@Override
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		
		// This replaces the default handler mapping with one that knows about the configured base path.
		val mapping = new BasePathAwareHandlerMapping(serfProperties.getBasePath());
		
		// The rest is the same as what we're overriding.
		mapping.setOrder(0);
		mapping.setInterceptors(getInterceptors());
		mapping.setContentNegotiationManager(mvcContentNegotiationManager());
		
		val configurer = getPathMatchConfigurer();
		if (configurer.isUseSuffixPatternMatch() != null) {
			mapping.setUseSuffixPatternMatch(configurer.isUseSuffixPatternMatch());
		}
		if (configurer.isUseRegisteredSuffixPatternMatch() != null) {
			mapping.setUseRegisteredSuffixPatternMatch(configurer.isUseRegisteredSuffixPatternMatch());
		}
		if (configurer.isUseTrailingSlashMatch() != null) {
			mapping.setUseTrailingSlashMatch(configurer.isUseTrailingSlashMatch());
		}
		if (configurer.getPathMatcher() != null) {
			mapping.setPathMatcher(configurer.getPathMatcher());
		}
		if (configurer.getUrlPathHelper() != null) {
			mapping.setUrlPathHelper(configurer.getUrlPathHelper());
		}
		
		return mapping;
	}
	*/
	
	
	// =================================================================================================================
	// HTTP message converters
	// =================================================================================================================
	
	// TODO Remove?
//	@Bean
//	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
//		return new ByteArrayHttpMessageConverter();
//	}
	
	// TODO Remove?
//	@Bean
//	public StringHttpMessageConverter stringHttpMessageConverter() {
//		val converter = new StringHttpMessageConverter();
//		converter.setWriteAcceptCharset(false);
//		return converter;
//	}
	
	// V1
	@Bean
	public MappingJackson2HttpMessageConverter origMappingJackson2HttpMessageConverter() {
		val converter = new MappingJackson2HttpMessageConverter(origMapper);
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		return converter;
	}
	
	// V2
	@Bean
	public MappingJackson2HttpMessageConverter halHttpMessageConverter() {
		val converter = new MappingJackson2HttpMessageConverter(halMapper);
		converter.setSupportedMediaTypes(Arrays.asList(MediaTypes.APPLICATION_HAL_JSON));
		return converter;
	}
	
	
	// =================================================================================================================
	// Arg resolvers
	// =================================================================================================================
	
	@Bean
	public PageableHandlerMethodArgumentResolver pageableResolver() {
		val resolver = new PageableHandlerMethodArgumentResolver();
		resolver.setMaxPageSize(500);
		resolver.setOneIndexedParameters(false);
		return resolver;
	}
	
	// V1
	@Bean
	public PEResourceResolver peResourceResolver() {
		// FIXME DRY up. See com.expedia.seiso.web.converter, which repeats the same info.
		// FIXME Don't hardcode this stuff.
		// @formatter:off
		return new PEResourceResolver(Arrays.asList(
				new SimplePropertyEntry(RepoKeys.NODES, "ip-addresses", NodeIpAddress.class),
				new SimplePropertyEntry(RepoKeys.SERVICES, "doc-links", DocLink.class),
				new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "dashboards", Dashboard.class),
				new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "ip-address-roles", IpAddressRole.class),
				new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "ports", ServiceInstancePort.class),
				new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "seyren-checks", SeyrenCheck.class)));
		// @formatter:on
	}
	
	// V1
	@Bean
	public PEResourcesResolver peResourcesResolver() { return new PEResourcesResolver(); }
	
	// V1
	@Bean
	public ResolverUtils resolverUtils() { return new ResolverUtils(); }
	
	// V2
	@Bean
	public PersistentEntityResourceResolver persistentEntityResourceResolver() {
		return new PersistentEntityResourceResolver();
	}
	
	
	// =================================================================================================================
	// View resolvers
	// =================================================================================================================
	
	/** Allows us to forward to index.html. */
	@Bean
	public InternalResourceViewResolver defaultViewResolver() { return new InternalResourceViewResolver(); }
	
	
	// =================================================================================================================
	// Custom
	// =================================================================================================================
	
	// V1
	@Bean
	public ResponseHeadersV1 responseHeadersV1() {
		return new ResponseHeadersV1();
	}
	
	@Bean
	public RepoMetaRegistry repoMetaRegistry() { return new RepoMetaRegistry(); }
	
	@Bean
	public ResourceAssembler resourceAssembler() { return new ResourceAssembler(); }
	
	@Bean
	public LinkFactory linkFactoryV1() {
		return new LinkFactory(getVersionUri("v1"), itemPaths(), itemMetaLookup);
	}
	
	@Bean
	public LinkFactory linkFactoryV2() {
		return new LinkFactory(getVersionUri("v2"), itemPaths(), itemMetaLookup);
	}
	
	@Bean
	public ItemPaths itemPaths() { return new ItemPaths(); }
	
	@Bean
	public ExceptionHandlerAdvice exceptionHandlerAdvice() {
		return new ExceptionHandlerAdvice();
	}
	
	
	// =================================================================================================================
	// Private
	// =================================================================================================================
	
	private URI getVersionUri(String version) {
		val baseUri = serfProperties.getBaseUri();
		
		if (baseUri == null) {
			val msg = "baseUri is null. Be sure that you've defined serf.base-uri in application.yml.";
			throw new ConfigurationException(msg);
		}
		
		try {
			return new URI(slashify(baseUri) + version);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private String slashify(String s) {
		return s.endsWith("/") ? s : s + "/";
	}
}
