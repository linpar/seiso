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

import java.util.Arrays;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.expedia.seiso.conf.CustomProperties;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.hypermedia.ItemPaths;
import com.expedia.seiso.web.controller.v1.ControllerV1Marker;
import com.expedia.seiso.web.controller.v1.ResponseHeadersV1;
import com.expedia.seiso.web.jackson.orig.OrigMapper;
import com.expedia.seiso.web.jackson.orig.OrigModule;
import com.expedia.seiso.web.jackson.orig.OrigPagedResourcesSerializer;
import com.expedia.seiso.web.jackson.orig.OrigResourceAssembler;
import com.expedia.seiso.web.jackson.orig.OrigResourceSerializer;
import com.expedia.seiso.web.jackson.orig.OrigResourcesSerializer;

/**
 * @author Willie Wheeler
 */
@Configuration
@ComponentScan(basePackageClasses = ControllerV1Marker.class)
public class SeisoWebConfigBeansV1 {
	// @formatter:off
	@Autowired private CustomProperties customProperties;
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private Repositories repositories;
	@Autowired private ItemService itemService;
	@Autowired private ItemPaths itemPaths;
	// @formatter:on

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

	@Bean
	public MappingJackson2HttpMessageConverter origMappingJackson2HttpMessageConverter() {
		val converter = new MappingJackson2HttpMessageConverter(origMapper());
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		return converter;
	}

	@Bean
	public ResponseHeadersV1 responseHeadersV1() {
		return new ResponseHeadersV1();
	}
}
