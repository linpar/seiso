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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Set;

import javax.persistence.EntityManager;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Ken Van Eyk
 * @author Willie Wheeler
 */
public class NodeRepoImplTests {
	
	// Class under test
	@InjectMocks private NodeRepoImpl repo;
	
	// Dependencies
	@Mock private EntityManager entityManager;
	@Mock private RepoImplUtils repoUtils;
	
	// Test data
	@Mock private Set<String> searchTokens;
	@Mock private Pageable pageable;
	@Mock private Page resultsPage;

	@Before
	public void setUp() {
		this.repo = new NodeRepoImpl();
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(repo, "entityManager", entityManager);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() {
	}
	
	@SuppressWarnings("unchecked")
	private void initDependencies() {
		when(repoUtils.search(anyString(), eq(entityManager), (Set) anyObject(), eq(searchTokens), eq(pageable)))
				.thenReturn(resultsPage);
	}

	@Test
	public void searchTest() {
		val actual = repo.search(searchTokens, pageable);
		assertEquals(resultsPage, actual);
	}
}
