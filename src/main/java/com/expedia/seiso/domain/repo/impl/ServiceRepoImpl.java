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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.repo.custom.ServiceRepoCustom;

/**
 * @author Ken Van Eyk
 */
public class ServiceRepoImpl implements ServiceRepoCustom {
	private static final String ENTITY_NAME = "Service";
	private static final Set<String> FIELD_NAMES = new LinkedHashSet<String>(Arrays.asList(new String[] { "name" }));

	@PersistenceContext
	private EntityManager entityManager;
	private RepoImplUtils repoUtils;

	// TODO @PersistenceContext can't be applied at the constructor level, reconcile for consistency
	// https://jira.spring.io/browse/SPR-10443
	public ServiceRepoImpl() {
		this(null);
	}

	public ServiceRepoImpl(@NotNull EntityManager entityManager) {
		this(entityManager, null);
	}

	public ServiceRepoImpl(@NotNull EntityManager entityManager, RepoImplUtils repoUtils) {
		this.entityManager = entityManager;
		this.repoUtils = (repoUtils == null ? RepoImplUtils.getInstance() : repoUtils);
	}

	@Override
	public Class<Service> getResultType() {
		return Service.class;
	}

	@Override
	public Page<Service> search(@NonNull Set<String> searchTokens, Pageable pageable) {
		Page<Service> searchPage = this.repoUtils.search(ServiceRepoImpl.ENTITY_NAME, this.entityManager,
				ServiceRepoImpl.FIELD_NAMES, searchTokens, pageable);
		;

		return searchPage;
	}

}
