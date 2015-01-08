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
import java.util.Arrays;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.expedia.seiso.SeisoWebConfigBeans.ArgResolverConfig;
import com.expedia.seiso.SeisoWebConfigBeans.AssemblyConfig;
import com.expedia.seiso.SeisoWebConfigBeans.ControllerConfig;
import com.expedia.seiso.SeisoWebConfigBeans.HateoasConfig;
import com.expedia.seiso.SeisoWebConfigBeans.HttpMessageConverterConfig;
import com.expedia.seiso.core.config.CustomProperties;
import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.seiso.web.MediaTypes;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.controller.ExceptionHandlerAdvice;
import com.expedia.seiso.web.controller.RepoConverter;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.seiso.web.controller.delegate.GlobalSearchDelegate;
import com.expedia.seiso.web.controller.delegate.RepoSearchDelegate;
import com.expedia.seiso.web.controller.internal.GlobalSearchController;
import com.expedia.seiso.web.controller.v1.IpAddressRoleControllerV1;
import com.expedia.seiso.web.controller.v1.ItemControllerV1;
import com.expedia.seiso.web.controller.v1.NodeControllerV1;
import com.expedia.seiso.web.controller.v1.NodeIpAddressControllerV1;
import com.expedia.seiso.web.controller.v1.RepoSearchControllerV1;
import com.expedia.seiso.web.controller.v1.ResponseHeadersV1;
import com.expedia.seiso.web.controller.v1.ServiceInstancePortControllerV1;
import com.expedia.seiso.web.controller.v2.ItemControllerV2;
import com.expedia.seiso.web.controller.v2.PersonControllerV2;
import com.expedia.seiso.web.controller.v2.RepoSearchControllerV2;
import com.expedia.seiso.web.hateoas.link.ItemPaths;
import com.expedia.seiso.web.hateoas.link.LinkFactory;
import com.expedia.seiso.web.jackson.hal.HalMapper;
import com.expedia.seiso.web.jackson.hal.HalModule;
import com.expedia.seiso.web.jackson.hal.HalResourceAssembler;
import com.expedia.seiso.web.jackson.hal.HalResourcePageSerializer;
import com.expedia.seiso.web.jackson.hal.HalResourceSerializer;
import com.expedia.seiso.web.jackson.v1.V1Mapper;
import com.expedia.seiso.web.jackson.v1.V1Module;
import com.expedia.seiso.web.jackson.v1.V1ResourceAssembler;
import com.expedia.seiso.web.jackson.v1.V1ResourcePageSerializer;
import com.expedia.seiso.web.jackson.v1.V1ResourceSerializer;
import com.expedia.seiso.web.resolver.PEResourceListResolver;
import com.expedia.seiso.web.resolver.PEResourceResolver;
import com.expedia.seiso.web.resolver.ResolverUtils;
import com.expedia.seiso.web.resolver.SimplePropertyEntry;

/**
 * @author Willie Wheeler
 */
@Configuration
@Import({
	ArgResolverConfig.class,
	HttpMessageConverterConfig.class,
	HateoasConfig.class,
	AssemblyConfig.class,
	ControllerConfig.class
})
public class SeisoWebConfigBeans {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private Repositories repositories;
	@Autowired private ItemAssembler itemAssembler;
	
	@Bean
	public RepoConverter repoConverter() { return new RepoConverter(itemMetaLookup); }
	
	@Bean
	public InternalResourceViewResolver defaultViewResolver() {
		// Need this so we can forward to index.html.
		return new InternalResourceViewResolver();
	}
	
	@Bean
	public ExceptionHandlerAdvice exceptionHandlerAdvice() { return new ExceptionHandlerAdvice(); }
	
	@Configuration
	public static class ArgResolverConfig {
		
		@Bean
		public PEResourceResolver peItemDtoResolver() {
			// @formatter:off
			return new PEResourceResolver(Arrays.asList(
					new SimplePropertyEntry(RepoKeys.NODES, "ip-addresses", NodeIpAddress.class),
					new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "ip-address-roles", IpAddressRole.class),
					new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "ports", ServiceInstancePort.class)));
			// @formatter:on
		}
		
		@Bean
		public PEResourceListResolver peItemDtoListResolver() { return new PEResourceListResolver(); }
		
		@Bean
		public PageableHandlerMethodArgumentResolver pageableResolver() {
			val resolver = new PageableHandlerMethodArgumentResolver();
			resolver.setMaxPageSize(500);
			resolver.setOneIndexedParameters(false);
			return resolver;
		}
		
		@Bean
		public ResolverUtils resolverUtils() { return new ResolverUtils(); }
	}
	
	@Configuration
	public static class HttpMessageConverterConfig {
		
		@Bean
		public V1Mapper v1Mapper() {
			val assembler = new V1ResourceAssembler();
			val dtoSerializer = new V1ResourceSerializer(assembler);
			val dtoPageSerializer = new V1ResourcePageSerializer(assembler);
			return new V1Mapper(new V1Module(dtoSerializer, dtoPageSerializer));
		}
		
		@Bean
		public HalMapper halMapper() {
			val assembler = new HalResourceAssembler();
			val dtoSerializer = new HalResourceSerializer(assembler);
			val dtoPageSerializer = new HalResourcePageSerializer(assembler);
			return new HalMapper(new HalModule(dtoSerializer, dtoPageSerializer));
		}
		
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
		public MappingJackson2HttpMessageConverter v1HttpMessageConverter() {
			val converter = new MappingJackson2HttpMessageConverter(v1Mapper());
			converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
			return converter;
		}
		
		@Bean
		public MappingJackson2HttpMessageConverter halHttpMessageConverter() {
			val converter = new MappingJackson2HttpMessageConverter(halMapper());
			converter.setSupportedMediaTypes(Arrays.asList(MediaTypes.APPLICATION_HAL_JSON));
			return converter;
		}
	}
	
	@Configuration
	public static class HateoasConfig {
		@Autowired private CustomProperties customProperties;
		@Autowired private ItemMetaLookup itemMetaLookup;
		
		@Bean
		public ItemPaths itemPaths() {
			return new ItemPaths();
		}
		
		@Bean
		public LinkFactory linkFactoryV1() throws Exception {
			return new LinkFactory(getVersionUri("v1"), itemPaths(), itemMetaLookup);
		}
		
		@Bean
		public LinkFactory linkFactoryV2() throws Exception {
			return new LinkFactory(getVersionUri("v2"), itemPaths(), itemMetaLookup);
		}
		
		private URI getVersionUri(String version) throws Exception {
			return new URI(slashify(customProperties.getBaseUri()) + version);
		}
		
		private String slashify(String s) {
			return s.endsWith("/") ? s : s + "/";
		}
	}
	
	// TODO Move this into HateoasConfig
	@Configuration
	public static class AssemblyConfig {
		
		@Bean
		public ItemAssembler itemAssembler() { return new ItemAssembler(); }
	}
	
	@Configuration
	public static class ControllerConfig {
		
		@Bean
		public BasicItemDelegate basicItemDelegate() { return new BasicItemDelegate(); }
		
		@Bean
		public RepoSearchDelegate itemSearchDelegate() { return new RepoSearchDelegate(); }
		
		@Bean
		public GlobalSearchDelegate globalSearchDelegate() { return new GlobalSearchDelegate(); }
		
		// v1
		
		@Bean
		public ResponseHeadersV1 responseHeadersV1() { return new ResponseHeadersV1(); }
		
		@Bean
		public ItemControllerV1 itemControllerV1() { return new ItemControllerV1(); }
		
		@Bean
		public RepoSearchControllerV1 repoSearchControllerV1() { return new RepoSearchControllerV1(); }
		
		@Bean
		public NodeControllerV1 nodeControllerV1() { return new NodeControllerV1(); }
		
		@Bean
		public IpAddressRoleControllerV1 ipAddressRoleControllerV1() { return new IpAddressRoleControllerV1(); }
		
		@Bean
		public NodeIpAddressControllerV1 nodeIpAddressControllerV1() { return new NodeIpAddressControllerV1(); }
		
		@Bean
		public ServiceInstancePortControllerV1 serviceInstancePortControllerV1() {
			return new ServiceInstancePortControllerV1();
		}
		
		// v2
		
		@Bean
		public ItemControllerV2 itemControllerV2() { return new ItemControllerV2(); }
		
		@Bean
		public RepoSearchControllerV2 repoSearchControllerV2() { return new RepoSearchControllerV2(); }
		
		@Bean
		public PersonControllerV2 personControllerV2() { return new PersonControllerV2(); }
		
		// Internal
		
		@Bean
		public GlobalSearchController globalSearchController() { return new GlobalSearchController(); }
	}
}
