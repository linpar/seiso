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

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.expedia.seiso.core.config.CustomProperties;
import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.seiso.web.controller.ExceptionHandlerAdvice;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.seiso.web.controller.delegate.GlobalSearchDelegate;
import com.expedia.seiso.web.controller.delegate.RepoSearchDelegate;
import com.expedia.seiso.web.controller.internal.ControllerInternalMarker;
import com.expedia.seiso.web.hateoas.link.ItemPaths;
import com.expedia.seiso.web.hateoas.link.LinkFactory;
import com.expedia.seiso.web.resolver.PEResourceResolver;
import com.expedia.seiso.web.resolver.PEResourcesResolver;
import com.expedia.seiso.web.resolver.ResolverUtils;
import com.expedia.seiso.web.resolver.SimplePropertyEntry;

/**
 * Web configuration beans common to both v1 and v2.
 * 
 * @author Willie Wheeler
 */
@Configuration
@ComponentScan(basePackageClasses = ControllerInternalMarker.class)
public class SeisoWebConfigBeans {
	@Autowired private CustomProperties customProperties;
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private Repositories repositories;
	
	@Bean
	public PEResourceResolver peResourceResolver() {
		// FIXME DRY up. See com.expedia.seiso.web.converter, which repeats the same info.
		// @formatter:off
		return new PEResourceResolver(Arrays.asList(
				new SimplePropertyEntry(RepoKeys.NODES, "ip-addresses", NodeIpAddress.class),
				new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "ip-address-roles", IpAddressRole.class),
				new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "ports", ServiceInstancePort.class)));
		// @formatter:on
	}
	
	@Bean
	public PEResourcesResolver peResourcesResolver() { return new PEResourcesResolver(); }
	
	@Bean
	public PageableHandlerMethodArgumentResolver pageableResolver() {
		val resolver = new PageableHandlerMethodArgumentResolver();
		resolver.setMaxPageSize(500);
		resolver.setOneIndexedParameters(false);
		return resolver;
	}
	
	@Bean
	public ResolverUtils resolverUtils() { return new ResolverUtils(); }
	
	@Bean
	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
		return new ByteArrayHttpMessageConverter();
	}
	
	@Bean
	public StringHttpMessageConverter stringHttpMessageConverter() {
		val converter = new StringHttpMessageConverter();
		converter.setWriteAcceptCharset(false);
		return converter;
	}
	
	@Bean
	public ItemPaths itemPaths() { return new ItemPaths(); }
	
	@Bean
	public LinkFactory linkFactoryV1() {
		return new LinkFactory(customProperties, getVersionUri("v1"), itemPaths(), itemMetaLookup);
	}
	
	@Bean
	public LinkFactory linkFactoryV2() {
		return new LinkFactory(customProperties, getVersionUri("v2"), itemPaths(), itemMetaLookup);
	}
	
	@Bean
	public ResourceAssembler resourceAssembler() { return new ResourceAssembler(); }
	
	@Bean
	public BasicItemDelegate basicItemDelegate() {
		return new BasicItemDelegate(resourceAssembler());
	}
	
	@Bean
	public RepoSearchDelegate itemSearchDelegate() {
		return new RepoSearchDelegate(resourceAssembler());
	}
	
	@Bean
	public GlobalSearchDelegate globalSearchDelegate() {
		return new GlobalSearchDelegate();
	}
	
	/** Allows us to forward to index.html. */
	@Bean
	public InternalResourceViewResolver defaultViewResolver() { return new InternalResourceViewResolver(); }
	
	@Bean
	public ExceptionHandlerAdvice exceptionHandlerAdvice() { return new ExceptionHandlerAdvice(); }

	private URI getVersionUri(String version) {
		try {
			return new URI(slashify(customProperties.getBaseUri()) + version);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private String slashify(String s) {
		return s.endsWith("/") ? s : s + "/";
	}
}
