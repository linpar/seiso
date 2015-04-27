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
package com.expedia.serf.meta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

import com.expedia.serf.ann.RestResource;
import com.expedia.serf.exception.JavaConfigurationException;

/**
 * @author Willie Wheeler
 */
@Component
public class RepoMetaRegistry {
	@Autowired private Repositories repositories;
	
	private Map<Class<?>, RepoMeta> repoMetas = new HashMap<>();
	private Map<String, Class<?>> entityClassesByRepoPath = new HashMap<>();
	
	@PostConstruct
	public void postConstruct() {
		for (val entityClass : repositories) {
			val repo = repositories.getRepositoryFor(entityClass);
			val repoClass = repo.getClass();
			val restResourceAnn = AnnotationUtils.findAnnotation(repoClass, RestResource.class);
			
			if (restResourceAnn == null) {
				continue;
			}
			
			val repoMeta = toRepoMeta(restResourceAnn);
			repoMetas.put(entityClass, repoMeta);
			
			if (repoMeta.isExported()) {
				val rel = repoMeta.getRel();
				if (rel == null) {
					// repoClass.getName() returns (e.g.) "com.sun.proxy.$Proxy110",
					// so do entityClass.getName() instead.
					val errMsg = String.format(
							"%s repo has a @RestResource with exported=true, but the rel isn't set",
							entityClass.getName());
					throw new JavaConfigurationException(errMsg);
				}
				
				val path = repoMeta.getPath();
				if (path == null) {
					val errMsg = String.format(
							"%s repo has a @RestResource with exported=true, but the path isn't set",
							entityClass.getName());
					throw new JavaConfigurationException(errMsg);
				}
				
				entityClassesByRepoPath.put(path, entityClass);
			}
		}
	}
	
	public Class<?> getEntityClass(@NonNull String repoPath) {
		val entityClass = entityClassesByRepoPath.get(repoPath);
		
		if (entityClass == null) {
			val errMsg = String.format("Illegal repoPath: %s", repoPath);
			throw new IllegalArgumentException(errMsg);
		}
		
		return entityClass;
	}
	
	public List<RepoMeta> getRepoMetasForExportedRepos() {
		// @formatter:off
		return repoMetas
				.values()
				.stream()
				.filter(m -> m.isExported())
				.sorted()
				.collect(Collectors.toList());
		// @formatter:on
	}
	
	public RepoMeta getRepoMeta(@NonNull Class<?> entityClass) {
		val repoMeta = repoMetas.get(entityClass);
		
		if (repoMeta == null) {
			val errMsg = String.format("Illegal entityClass: %s", entityClass.getName());
			throw new IllegalArgumentException(errMsg);
		}
		
		return repoMeta;
	}
	
	private static RepoMeta toRepoMeta(RestResource ann) {
		val exported = ann.exported();
		val rel = emptyToNull(ann.rel());
		val path = emptyToNull(ann.path());
		return new RepoMeta(exported, rel, path);
	}
	
	private static String emptyToNull(String s) {
		return ("".equals(s) ? null : s); 
	}
}
