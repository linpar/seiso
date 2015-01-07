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
package com.expedia.seiso.domain.service.impl;

import static org.mockito.Mockito.when;

import java.util.Iterator;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.Repositories;

import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.custom.SearchableRepository;
import com.expedia.seiso.domain.service.SearchEngine;
import com.expedia.seiso.domain.service.search.SpaceDelimitedDatabaseWildCardTokenizer;
import com.expedia.seiso.domain.service.search.SearchQuery;

/**
 * @author Ken Van Eyk
 * @author Willie Wheeler
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SearchEngineImplTests {
	
	// Class under test
	private SearchEngine searchEngine;
	
	// Dependencies
	@Mock private Repositories repositories;
	@Mock private SearchableRepository nodeRepo;
	@Mock private SearchableRepository machineRepo;
	
	// Test data
	private SearchQuery goodQuery;
	private SearchQuery badSearchQuery;
	private Pageable pageable = new PageRequest(1, 1);

	@Mock private Page<Node> nodePage;
	@Mock private Page<Machine> machinePage;

	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private Iterator repoIterator;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.initTestData();
		this.initDependencies();
		this.searchEngine = new SearchEngineImpl(repositories);
	}

	private void initTestData() {
		this.badSearchQuery = query("bad search query");
		this.goodQuery = query("good search query");
	}

	private SearchQuery query(String query) {
		val tokenizer = new SpaceDelimitedDatabaseWildCardTokenizer();
		val tokens = tokenizer.tokenize(query);
		return new SearchQuery(query, tokens);
	}

	private void initDependencies() {
		when(repositories.iterator()).thenReturn(repoIterator);
		when(repositories.getRepositoryFor(Node.class)).thenReturn(nodeRepo);
		when(repositories.getRepositoryFor(Machine.class)).thenReturn(machineRepo);
		
		when(nodeRepo.getResultType()).thenReturn(Node.class);
		when(nodeRepo.search(goodQuery.getTokens(), pageable)).thenReturn(nodePage);
		
		when(machineRepo.getResultType()).thenReturn(Machine.class);
		when(machineRepo.search(goodQuery.getTokens(), pageable)).thenReturn(machinePage);

		// @formatter:off
		when(repoIterator.hasNext())
				.thenReturn(true)
				.thenReturn(true)
				.thenReturn(false);
		when(repoIterator.next())
				.thenReturn(Node.class)
				.thenReturn(Machine.class)
				.thenReturn(null);
		// @formatter:on
	}

	@Test
	public void search() throws Exception {
		val result = searchEngine.search(goodQuery, pageable);
		// TODO Verify
	}

	@Test
	public void search_badQuery()  throws Exception {
		val result = searchEngine.search(badSearchQuery, pageable);
		// TODO Verify
	}
}
