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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.repo.custom.NodeRepoCustom;

/**
 * @author Ken Van Eyk
 */
public class NodeRepoImpl implements NodeRepoCustom {
	private static final String ENTITY_NAME = "Node";
	private static final Set<String> FIELD_NAMES = new LinkedHashSet<String>(Arrays.asList(new String[] { "name" }));

	@PersistenceContext
	private EntityManager entityManager;
	private RepoImplUtils repoUtils;

	// TODO @PersistenceContext can't be applied at the constructor level, reconcile for consistency
	// https://jira.spring.io/browse/SPR-10443
	public NodeRepoImpl() {
		this(null);
	}

	public NodeRepoImpl(@NotNull EntityManager entityManager) {
		this(entityManager, null);
	}

	public NodeRepoImpl(@NotNull EntityManager entityManager, RepoImplUtils repoUtils) {
		this.entityManager = entityManager;
		this.repoUtils = (repoUtils == null ? RepoImplUtils.getInstance() : repoUtils);
	}

	@Override
	public Class<Node> getResultType() {
		return Node.class;
	}

	@Override
	public Page<Node> search(Set<String> searchTokens, Pageable pageable) {
		Page<Node> searchPage = this.repoUtils.search(NodeRepoImpl.ENTITY_NAME, this.entityManager,
				NodeRepoImpl.FIELD_NAMES, searchTokens, pageable);
		;

		return searchPage;
	}
}
