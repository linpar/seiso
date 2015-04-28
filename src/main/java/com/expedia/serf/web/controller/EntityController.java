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
package com.expedia.serf.web.controller;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.serf.hypermedia.Resource;
import com.expedia.serf.hypermedia.Resources;
import com.expedia.serf.meta.RepoMetaRegistry;
import com.expedia.serf.service.CrudService;
import com.expedia.serf.service.impl.DynaEntity;
import com.expedia.serf.web.MediaTypes;
import com.expedia.serf.web.PersistentEntityResource;

/**
 * @author Willie Wheeler
 */
@RestController
@XSlf4j
public class EntityController {
	
	@Value("#{serfProperties.basePath}")
	private String basePath;
	
	@Autowired private RepoMetaRegistry repoMetaRegistry;
	@Autowired private Repositories repositories;
	@Autowired private CrudService crudService;
	
	@RequestMapping(
			value = "/{repo}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Resources getAll(@PathVariable("repo") String repoPath) {
		log.trace("GET /{}/{}", basePath, repoPath);
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@RequestMapping(
			value = "/{repo}/{id}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Resource get(
			@PathVariable("repo") String repoPath,
			@PathVariable("id") Long entityId) {
		
		log.trace("GET /{}/{}/{}", basePath, repoPath, entityId);
		val repo = getRepo(repoPath);
		val entity = repo.findOne(entityId);
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@RequestMapping(
			value = "/{repo}",
			method = RequestMethod.POST,
			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public void post(
			@PathVariable("repo") String repoPath,
			@RequestParam(value = "include", required = false) String include,
			@RequestParam(value = "exclude", required = false) String exclude,
			PersistentEntityResource peResource) {
		
		log.trace("POST /{}/{}", basePath, repoPath);
		val entity = peResource.getEntity();
		val dynaEntity = new DynaEntity(entity);
		dynaEntity.setId(null);
		crudService.save(entity, parseList(include), parseList(exclude));
		
		// TODO Return 201 CREATED
	}
	
	@RequestMapping(
			value = "/{repo}/{id}",
			method = RequestMethod.PUT,
			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public void put(
			@PathVariable("repo") String repoPath,
			@PathVariable("id") Long entityId,
			@RequestParam(value = "include", required = false) String include,
			@RequestParam(value = "exclude", required = false) String exclude,
			PersistentEntityResource peResource) {
		
		log.trace("PUT /{}/{}/{}", basePath, repoPath, entityId);
		val entity = peResource.getEntity();
		val dynaEntity = new DynaEntity(entity);
		dynaEntity.setId(entityId);
		crudService.save(entity, parseList(include), parseList(exclude));
	}
	
	@SuppressWarnings("rawtypes")
	private CrudRepository getRepo(String repoPath) {
		val entityClass = repoMetaRegistry.getEntityClass(repoPath);
		return (CrudRepository) repositories.getRepositoryFor(entityClass);
	}
	
	private String[] parseList(String listExpr) {
		return (listExpr == null ? null : listExpr.split(","));
	}
}
