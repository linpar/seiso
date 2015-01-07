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

import com.expedia.seiso.domain.entity.LoadBalancer;
import com.expedia.seiso.domain.repo.custom.LoadBalancerRepoCustom;

/**
 * @author Ken Van Eyk
 */
public class LoadBalancerRepoImpl implements LoadBalancerRepoCustom {
	private static final String ENTITY_NAME = "LoadBalancer";
	private static final Set<String> FIELD_NAMES = new LinkedHashSet<String>(Arrays.asList(new String[] { "name",
			"ipAddress" }));

	@PersistenceContext
	private EntityManager entityManager;
	private RepoImplUtils repoUtils;

	// TODO @PersistenceContext can't be applied at the constructor level, reconcile for consistency
	// https://jira.spring.io/browse/SPR-10443
	public LoadBalancerRepoImpl() {
		this(null);
	}

	public LoadBalancerRepoImpl(@NotNull EntityManager entityManager) {
		this(entityManager, null);
	}

	public LoadBalancerRepoImpl(@NotNull EntityManager entityManager, RepoImplUtils repoUtils) {
		this.entityManager = entityManager;
		this.repoUtils = (repoUtils == null ? RepoImplUtils.getInstance() : repoUtils);
	}

	@Override
	public Class<LoadBalancer> getResultType() {
		return LoadBalancer.class;
	}

	@Override
	public Page<LoadBalancer> search(@NonNull Set<String> searchTokens, Pageable pageable) {
		Page<LoadBalancer> searchPage = this.repoUtils.search(LoadBalancerRepoImpl.ENTITY_NAME, this.entityManager,
				LoadBalancerRepoImpl.FIELD_NAMES, searchTokens, pageable);
		;

		return searchPage;
	}

}
