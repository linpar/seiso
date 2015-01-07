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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.expedia.seiso.domain.repo.custom.SearchableRepository;
import com.expedia.seiso.domain.service.SearchEngine;
import com.expedia.seiso.domain.service.SearchResults;
import com.expedia.seiso.domain.service.search.SearchQuery;

/**
 * Simple search engine implementation.
 * 
 * @author Ken Van Eyk
 * @author Willie Wheeler
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SearchEngineImpl implements SearchEngine {
	private static final int DEFAULT_THREAD_POOL_SIZE = 10;

	@NonNull private Repositories repositories;
	@NonNull private ExecutorService executorService;

	public SearchEngineImpl(@NonNull Repositories repositories) {
		this(repositories, Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE));
	}

	@Override
	@SneakyThrows({ ExecutionException.class, InterruptedException.class })
	public SearchResults search(@NonNull SearchQuery query, @NonNull Pageable pageable) {
		val allResults = new SearchResults();
		val tokens = query.getTokens();
		if (!CollectionUtils.isEmpty(tokens)) {
			val tasks = createSearchTasks(tokens, pageable);
			val futures = executorService.invokeAll(tasks);
			for (val future : futures) {
				val serp = future.get();
				allResults.putTypedSerp(serp.getItemClass(), serp.getResultPage());
			}
		}
		return allResults;
	}
	
	private Collection<SearchTask> createSearchTasks(Set<String> tokens, Pageable pageable) {
		val tasks = new LinkedList<SearchTask>();
		for (val itemClass : repositories) {
			val repo = repositories.getRepositoryFor(itemClass);
			if (repo instanceof SearchableRepository) {
				val searchableRepo = (SearchableRepository) repo;
				tasks.add(new SearchTask(searchableRepo, tokens, pageable));
			}
		}
		return tasks;
	}
	
	@RequiredArgsConstructor
	private class SearchTask implements Callable<TypedSerp> {
		@NonNull private SearchableRepository searchableRepo;
		@NonNull private Set<String> keywords;
		@NonNull private Pageable pageable;

		@Override
		public TypedSerp call() throws Exception {
			val itemClass = searchableRepo.getResultType();
			val itemPage = searchableRepo.search(keywords, pageable);
			return new TypedSerp(itemClass, itemPage);
		}
	}
	
	@Data
	@AllArgsConstructor
	static class TypedSerp {
		@NonNull private Class itemClass;
		private Page resultPage;
	}
}
