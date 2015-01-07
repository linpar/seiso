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
package com.expedia.seiso.web.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.PersonRepo;
import com.expedia.seiso.domain.repo.ServiceRepo;
import com.expedia.seiso.web.Relations;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.hateoas.ItemLinks;
import com.expedia.seiso.web.hateoas.Link;
import com.expedia.seiso.web.hateoas.BaseResourcePage;

/**
 * @author Willie Wheeler
 */
public class ItemSearchDelegateTests {
	private static final String NONPAGING_REPO_KEY = "my-nonpaging-repo";
	private static final String PAGING_REPO_KEY = "my-paging-repo";
	private static final Class<?> PAGING_ITEM_CLASS = Person.class;
	private static final String QUERY_WITH_RESULT_LIST = "find-by-data-center-key";
	private static final String QUERY_WITH_RESULT_PAGE = "find-by-last-name";
	private static final String QUERY_WITH_UNIQUE_RESULT = "find-by-name";
	
	// Class under test
	@InjectMocks private ItemSearchDelegate delegate;
	
	// Dependencies
	@Mock private Repositories repositories;
	@Mock private PersonRepo pagingRepo;
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private ItemAssembler itemAssembler;
	@Mock private ItemLinks itemLinks;
	@Mock private ConversionService conversionService;
	
	// Test data
	@Mock private Pageable pageable;
	private MultiValueMap<String, String> params;
	@Mock private ItemMeta pagingItemMeta;
	private Method queryMethodWithPagingResults;
	private Method queryMethodWithUniqueResult;
	private List<Method> queryMethods;
	@Mock private RepositoryInformation repoInfo;
	@Mock private Link searchListSelfLink, searchListUpLink, searchListFindByNameLink;
	@Mock private Page<Person> pageSearchResult;
	@Mock private BaseResourcePage dtoPageSearchResult;
	
	@Before
	public void setUp() {
		this.delegate = new ItemSearchDelegate();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}

	private void initTestData() {
		this.params = new LinkedMultiValueMap<>();
		params.set("name", "Aurelius");
		
		when(pagingItemMeta.getRepositorySearchMethod(QUERY_WITH_RESULT_PAGE)).thenReturn(queryMethodWithPagingResults);
		
		this.queryMethodWithPagingResults = ReflectionUtils
				.findMethod(PersonRepo.class, "findByLastName", String.class, Pageable.class);
		assert(queryMethodWithPagingResults != null);
		this.queryMethodWithUniqueResult = ReflectionUtils.findMethod(ServiceRepo.class, "findByName", String.class);
		assert(queryMethodWithUniqueResult != null);
		this.queryMethods = Arrays.asList(queryMethodWithUniqueResult);
		when(repoInfo.getQueryMethods()).thenReturn(queryMethods);
		
		when(pagingItemMeta.getRepositorySearchMethod(QUERY_WITH_RESULT_PAGE)).thenReturn(queryMethodWithPagingResults);
	}
	
	private void initDependencies() {
		when(itemMetaLookup.getItemClass(PAGING_REPO_KEY)).thenReturn(PAGING_ITEM_CLASS);
		when(itemMetaLookup.getItemMeta(PAGING_ITEM_CLASS)).thenReturn(pagingItemMeta);
		
		when(repositories.getRepositoryInformationFor(PAGING_ITEM_CLASS)).thenReturn(repoInfo);
		when(repositories.getRepositoryFor(PAGING_ITEM_CLASS)).thenReturn(pagingRepo);
		
		when(pagingRepo.findByLastName("Aurelius", pageable)).thenReturn(pageSearchResult);
		
		when(itemLinks.itemRepoSearchListLink(Relations.SELF, PAGING_ITEM_CLASS))
				.thenReturn(searchListSelfLink);
		when(itemLinks.itemRepoLink(Relations.UP, PAGING_ITEM_CLASS))
				.thenReturn(searchListUpLink);
		when(itemLinks.itemRepoSearchLink("s:find-by-name", PAGING_ITEM_CLASS, "find-by-name"))
				.thenReturn(searchListFindByNameLink);
	}
	
	@Test
	public void getSearchList() {
		val result = delegate.getRepoSearchList(PAGING_REPO_KEY);
		assertNotNull(result);
		val links = result.getV2Links();
		assertFalse(links.isEmpty());
		assertTrue(links.contains(searchListSelfLink));
		assertTrue(links.contains(searchListUpLink));
		assertTrue(links.contains(searchListFindByNameLink));
	}
	
	@Test
	@Ignore
	public void search_resultList() {
		val result = delegate.repoSearch(NONPAGING_REPO_KEY, QUERY_WITH_RESULT_LIST);
	}
	
	@Test
	public void search_resultPage() {
		val result = delegate.repoSearch(PAGING_REPO_KEY, QUERY_WITH_RESULT_PAGE, pageable, params);
		// TODO
//		assertNotNull(result);
//		assertSame(dtoPageSearchResult, result);
	}
	
	@Test
	@Ignore
	public void search_uniqueResult() {
		val result = delegate.repoSearch(PAGING_REPO_KEY, QUERY_WITH_UNIQUE_RESULT);
	}
}
