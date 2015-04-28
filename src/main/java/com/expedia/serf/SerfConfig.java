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
package com.expedia.serf;

import lombok.val;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.expedia.serf.hypermedia.PathBuilder;
import com.expedia.serf.meta.RepoMetaRegistry;
import com.expedia.serf.service.CrudService;
import com.expedia.serf.service.impl.CrudServiceImpl;
import com.expedia.serf.web.PersistentEntityResourceResolver;
import com.expedia.serf.web.controller.EntityController;
import com.expedia.serf.web.controller.EntryController;

/**
 * @author Willie Wheeler
 */
@Configuration
public class SerfConfig {
	
	@Bean
	public SerfProperties serfProperties() {
		return new SerfProperties();
	}
	
	@Bean
	public CrudService crudService() {
		return new CrudServiceImpl();
	}
	
	@Bean
	public RepoMetaRegistry repoMetaRegistry() {
		return new RepoMetaRegistry();
	}
	
	@Bean
	public PersistentEntityResourceResolver persistentEntityResourceResolver() {
		return new PersistentEntityResourceResolver();
	}
	
	@Bean
	public PathBuilder pathBuilder() {
		val props = serfProperties();
		return new PathBuilder(props.getBaseUri(), props.getBasePath());
	}
	
	@Bean
	public EntryController entryController() {
		return new EntryController();
	}
	
	@Bean
	public EntityController entityController() {
		return new EntityController();
	}
}
