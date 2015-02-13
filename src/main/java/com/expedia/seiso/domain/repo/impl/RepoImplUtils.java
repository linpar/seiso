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
package com.expedia.seiso.domain.repo.impl;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import lombok.NonNull;
import lombok.val;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.service.search.QueryFactory;

/**
 * @author Ken Van Eyk
 * @author Willie Wheeler
 */
@Component
public class RepoImplUtils {

	// TODO use pageable ?
	@SuppressWarnings("unchecked")
	public <T> Page<T> search(
			@NotNull String entityName,
			@NotNull EntityManager entityManager,
			@NonNull Set<String> fieldNames,
			@NotNull Set<String> searchTokens,
			Pageable pageable) {
		
		// Set max results to avoid huge search queries.
		val items = new QueryFactory()
			.buildQuery(entityName, entityManager, fieldNames, searchTokens)
			.setMaxResults(50)
			.getResultList();
		return new PageImpl<T>(items);
	}
}
