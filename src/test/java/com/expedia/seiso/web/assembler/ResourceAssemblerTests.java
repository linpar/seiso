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
package com.expedia.seiso.web.assembler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import com.expedia.rf.hmedia.Link;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.ServiceRepo;
import com.expedia.seiso.domain.service.SearchResults;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.hmedia.ItemLinks;
import com.expedia.seiso.web.hmedia.LinkFactory;
import com.expedia.seiso.web.hmedia.PaginationLinkBuilder;
import com.expedia.seiso.web.hmedia.RepoSearchLinks;

/**
 * @author Willie Wheeler
 */
public class ResourceAssemblerTests {
	private static final Class ITEM_CLASS = Service.class;
	private static final String SEARCH_PATH = "some-search-path";
	private static final Pageable PAGE_REQUEST = new PageRequest(5, 20);
	private static final ProjectionNode PROJECTION = ProjectionNode.FLAT_PROJECTION_NODE;
	
	// Class under test
	@InjectMocks private ResourceAssembler assembler;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private Repositories repositories;
	@Mock private LinkFactory linkFactoryV1;
	@Mock private LinkFactory linkFactoryV2;
	@Mock private ItemLinks itemLinks;
	@Mock private RepoSearchLinks repoSearchLinks;
	@Mock private PaginationLinkBuilder paginationLinkBuilder;
	
	// Test data
	private List<Service> itemList;
	private PageImpl<Service> itemPage;
	private Person person;
	private Service service;
	private Method queryMethod;
	private Iterable<Method> queryMethods;
	@Mock private RepositoryInformation repoInfo;
	@Mock private MultiValueMap<String, String> params;
	@Mock private PersistentEntity persistentEntity;
	@Mock private Link link;
	@Mock private SearchResults searchResults;
	
	@Before
	public void setUp() {
		this.assembler = new ResourceAssembler();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}
	
	private void setUpTestData() {
		this.queryMethod = ReflectionUtils.findMethod(ServiceRepo.class, "findByName", String.class);
		this.queryMethods = Arrays.asList(queryMethod);
		
		when(repoInfo.getQueryMethods()).thenReturn(queryMethods);
		
		// @formatter:off
		this.person = new Person()
				.setUsername("mkozelek")
				.setFirstName("Mark")
				.setLastName("Kozelek");
		this.service = new Service()
				.setKey("benji")
				.setName("Benji")
				.setDescription("My Benji service")
				.setOwner(person);
		// @formatter:on
		
		this.itemList = Arrays.asList(service);
		this.itemPage = new PageImpl<Service>(itemList, PAGE_REQUEST, 8675309);
	}
	
	private void setUpDependencies() {
		when(itemMetaLookup.getItemClass("services")).thenReturn(Service.class);
		
		when(repositories.getRepositoryInformationFor(Service.class)).thenReturn(repoInfo);
		when(repositories.getPersistentEntity((Class<?>) anyObject())).thenReturn(persistentEntity);
		
		when(linkFactoryV1.getItemLinks()).thenReturn(itemLinks);
		when(linkFactoryV1.getRepoSearchLinks()).thenReturn(repoSearchLinks);
		
		when(linkFactoryV2.getItemLinks()).thenReturn(itemLinks);
		when(linkFactoryV2.getRepoSearchLinks()).thenReturn(repoSearchLinks);
		
		when(itemLinks.itemLink((Item) anyObject())).thenReturn(link);
		when(itemLinks.repoLink(anyString(), (Class<?>) anyObject())).thenReturn(link);
		
		when(repoSearchLinks.repoSearchListLink(anyString(), (Class) anyObject()))
				.thenReturn(link);
		when(repoSearchLinks.toRepoSearchLinkTemplate(anyString(), (Class) anyObject(), anyString(), (MultiValueMap) anyObject()))
				.thenReturn(link);
		when(repoSearchLinks.toPaginationLinkBuilder((Page) anyObject(), (Class) anyObject(), anyString(), eq(params)))
				.thenReturn(paginationLinkBuilder);
	}
	
	@Test
	public void toResources() {
		val result = assembler.toResources(ApiVersion.V2, Service.class, itemList, PROJECTION);
		assertNotNull(result);
		
		val resultLinks = result.getLinks();
		assertNotNull(resultLinks);
		// TODO Need more link assertions
		
		val resultItems = result.getItems();
		assertNotNull(resultItems);
		assertEquals(itemList.size(), resultItems.size());
	}
	
	@Test
	public void toResources_nullItemList() {
		val result = assembler.toResources(ApiVersion.V2, Service.class, null, PROJECTION);
		assertNull(result);
	}
	
	@Test
	public void toPagedResources() {
		val result = assembler.toPagedResources(ApiVersion.V2, Service.class, itemPage, PROJECTION);
		assertNotNull(result);
	}
	
	@Test
	public void toPagedResources_nullItemPage() {
		val result = assembler.toPagedResources(ApiVersion.V2, Service.class, null, PROJECTION);
		assertNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toPagedResources_nullApiVersion() {
		assembler.toPagedResources(null, Service.class, itemPage, PROJECTION);
	}
	
	@Test(expected = NullPointerException.class)
	public void toPagedResources_nullItemClass() {
		assembler.toPagedResources(ApiVersion.V2, null, itemPage, PROJECTION);
	}
	
	@Test
	public void toResource_item() {
		val result = assembler.toResource(ApiVersion.V2, service, PROJECTION);
		assertNotNull(result);
	}
	
	
	// =================================================================================================================
	// Repo search resources
	// =================================================================================================================
	
	@Test
	public void toRepoSearchList() {
		val result = assembler.toRepoSearchList(ApiVersion.V2, "services");
		assertNotNull(result);
	}
	
	@Test
	public void toRepoSearchResource() {
		val result =
				assembler.toRepoSearchPagedResources(ApiVersion.V2, itemPage, ITEM_CLASS, SEARCH_PATH, params, PROJECTION);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toRepoSearchResource_nullApiVersion() {
		assembler.toRepoSearchPagedResources(null, itemPage, null, SEARCH_PATH, params, PROJECTION);
	}
	
	@Test(expected = NullPointerException.class)
	public void toRepoSearchResource_nullItemClass() {
		assembler.toRepoSearchPagedResources(ApiVersion.V2, itemPage, null, SEARCH_PATH, params, PROJECTION);
	}
	
	@Test(expected = NullPointerException.class)
	public void toRepoSearchResource_nullItemPage() {
		assembler.toRepoSearchPagedResources(ApiVersion.V2, null, ITEM_CLASS, SEARCH_PATH, params, PROJECTION);
	}
	
	
	// =================================================================================================================
	// Global search resources
	// =================================================================================================================
	
	@Test
	public void toGlobalSearchResource() {
		val result = assembler.toGlobalSearchResource(ApiVersion.V2, searchResults);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toGlobalSearchResource_nullApiVersion() {
		assembler.toGlobalSearchResource(null, searchResults);
	}
	
	@Test(expected = NullPointerException.class)
	public void toGlobalSearchResource_nullSearchResults() {
		assembler.toGlobalSearchResource(ApiVersion.V2, (SearchResults) null);
	}
	
	
	// =================================================================================================================
	// Special resources
	// =================================================================================================================
	
	@Deprecated
	@Test
	public void toUsernamePage() {
		// TODO
	}
	
	@Deprecated
	@Test
	public void toUsernamePage_nullPersonPage() {
		val result = assembler.toUsernamePage(ApiVersion.V2, null, params);
		assertNull(result);
	}
	
	@Deprecated
	@Test
	public void toUsernameList() {
		// TODO
	}
	
	@Deprecated
	@Test
	public void toUsernameList_nullPersonList() {
		val result = assembler.toUsernameList(null);
		assertNull(result);
	}
}
