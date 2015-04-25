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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.expedia.seiso.core.config.CustomProperties;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.ItemKeyHttpMessageConverter;
import com.expedia.seiso.web.UriToItemKeyConverter;
import com.expedia.seiso.web.controller.v2.ControllerV2Marker;
import com.expedia.serf.SerfProperties;
import com.expedia.serf.hmedia.hal.HalMapper;
import com.expedia.serf.web.MediaTypes;

/**
 * @author Willie Wheeler
 */
@Configuration
@ComponentScan(basePackageClasses = ControllerV2Marker.class)
public class SeisoWebConfigBeansV2 {
	@Autowired private SerfProperties serfProperties;
	@Autowired private CustomProperties customProperties;
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private HalMapper halMapper;
	
	@Bean
	public UriToItemKeyConverter uriToItemKeyConverter() {
		return new UriToItemKeyConverter(getVersionUri().toString());
	}
	
	@Bean
	public MappingJackson2HttpMessageConverter halHttpMessageConverter() {
		val converter = new MappingJackson2HttpMessageConverter(halMapper);
		converter.setSupportedMediaTypes(Arrays.asList(MediaTypes.APPLICATION_HAL_JSON));
		return converter;
	}
	
	@Bean
	public ItemKeyHttpMessageConverter itemKeyHttpMessageConverter() {
		return new ItemKeyHttpMessageConverter();
	}
	
	private URI getVersionUri() {
		val baseUri = serfProperties.getBaseUri();
		
		if (baseUri == null) {
			val msg = "baseUri is null. Be sure that you've defined custom.base-uri in application.yml.";
			throw new ConfigurationException(msg);
		}
		
		try {
			return new URI(slashify(baseUri) + "v2");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private String slashify(String s) {
		return s.endsWith("/") ? s : s + "/";
	}
}
