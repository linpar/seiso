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
import java.util.Map;

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
	
	private Map<String, Class<?>> entityClassesByRepoPath = new HashMap<>();
	
	@PostConstruct
	public void postConstruct() {
		for (val entityClass : repositories) {
			val repo = repositories.getRepositoryFor(entityClass);
			val repoClass = repo.getClass();
			val restResourceAnn = AnnotationUtils.findAnnotation(repoClass, RestResource.class);
			
			if (restResourceAnn == null || !restResourceAnn.exported()) {
				continue;
			}
			
			if (restResourceAnn.path() == null) {
				val errMsg = String.format(
						"%s has a @RestResource with exported=true, but the path isn't set",
						repoClass.getName());
				throw new JavaConfigurationException(errMsg);
			}
			
			entityClassesByRepoPath.put(restResourceAnn.path(), entityClass);
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
}
