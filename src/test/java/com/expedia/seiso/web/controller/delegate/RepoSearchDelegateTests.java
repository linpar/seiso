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
package com.expedia.seiso.web.controller.delegate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
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

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.PersonRepo;
import com.expedia.seiso.domain.repo.ServiceRepo;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.serf.hmedia.Resource;

/**
 * @author Willie Wheeler
 */
public class RepoSearchDelegateTests {
	private static final String PAGING_REPO_KEY = "my-paging-repo";
	private static final Class<?> PAGING_ITEM_CLASS = Person.class;
	private static final String SEARCH_PATH = "find-by-last-name";
	private static final String VIEW_KEY = Projection.DEFAULT;
	
	// Class under test
	private RepoSearchDelegate delegate;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private Repositories repositories;
	@Mock private PersonRepo pagingRepo;
	@Mock private ItemService itemService;
	@Mock private ResourceAssembler resourceAssembler;
	@Mock private ConversionService conversionService;
	
	// Test data
	@Mock private Pageable pageable;
	private MultiValueMap<String, String> params;
	@Mock private ItemMeta pagingItemMeta;
	private Method queryMethodWithPagingResults;
	private Method queryMethodWithUniqueResult;
	private List<Method> queryMethods;
	@Mock private RepositoryInformation repoInfo;
	@Mock private Page<Person> pageSearchResult;
	@Mock private Resource resource;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
		
		this.delegate = new RepoSearchDelegate(resourceAssembler);
		delegate.setItemMetaLookup(itemMetaLookup);
		delegate.setRepositories(repositories);
		delegate.setItemService(itemService);
		delegate.setConversionService(conversionService);
	}

	private void setUpTestData() {
		this.params = new LinkedMultiValueMap<>();
		params.set("name", "Aurelius");
		
		when(pagingItemMeta.getRepositorySearchMethod(SEARCH_PATH)).thenReturn(queryMethodWithPagingResults);
		
		this.queryMethodWithPagingResults = ReflectionUtils
				.findMethod(PersonRepo.class, "findByLastName", String.class, Pageable.class);
		assert(queryMethodWithPagingResults != null);
		
		this.queryMethodWithUniqueResult = ReflectionUtils.findMethod(ServiceRepo.class, "findByName", String.class);
		assert(queryMethodWithUniqueResult != null);
		
		this.queryMethods = Arrays.asList(queryMethodWithUniqueResult);
		when(repoInfo.getQueryMethods()).thenReturn(queryMethods);
		
		when(pagingItemMeta.getRepositorySearchMethod(SEARCH_PATH)).thenReturn(queryMethodWithPagingResults);
	}
	
	private void setUpDependencies() {
		
		when(itemMetaLookup.getItemClass(PAGING_REPO_KEY)).thenReturn(PAGING_ITEM_CLASS);
		when(itemMetaLookup.getItemMeta(PAGING_ITEM_CLASS)).thenReturn(pagingItemMeta);
		
		when(repositories.getRepositoryInformationFor(PAGING_ITEM_CLASS)).thenReturn(repoInfo);
		when(repositories.getRepositoryFor(PAGING_ITEM_CLASS)).thenReturn(pagingRepo);
		
		when(pagingRepo.findByLastName("Aurelius", pageable)).thenReturn(pageSearchResult);
		
		when(resourceAssembler.toRepoSearchList(eq(ApiVersion.V2), anyString())).thenReturn(resource);
	}
	
	@Test
	public void getRepoSearchList() {
		val result = delegate.getRepoSearchList(ApiVersion.V2, PAGING_REPO_KEY);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void getRepoSearchList_null() {
		delegate.getRepoSearchList(ApiVersion.V2, null);
	}
	
	@Test
	public void repoSearch_resultPage() {
		// FIXME Need to include/require params, as repo searches always involve them.
		val result = delegate.repoSearch(ApiVersion.V2, PAGING_REPO_KEY, SEARCH_PATH, VIEW_KEY, pageable, params);
		// TODO
	}
}
