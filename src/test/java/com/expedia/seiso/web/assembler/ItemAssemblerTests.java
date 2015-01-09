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
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.MultiValueMap;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.service.SearchResults;
import com.expedia.seiso.web.hateoas.Link;
import com.expedia.seiso.web.hateoas.link.ItemLinks;
import com.expedia.seiso.web.hateoas.link.LinkFactory;
import com.expedia.seiso.web.hateoas.link.PaginationLinkBuilder;
import com.expedia.seiso.web.hateoas.link.RepoSearchLinks;

/**
 * @author Willie Wheeler
 */
public class ItemAssemblerTests {
	private static final Class ITEM_CLASS = Service.class;
	private static final String SEARCH_PATH = "some-search-path";
	private static final Pageable PAGE_REQUEST = new PageRequest(5, 20);
	private static final ProjectionNode PROJECTION = ProjectionNode.FLAT_PROJECTION_NODE;
	
	// Class under test
	@InjectMocks private ItemAssembler assembler;
	
	// Dependencies
	@Mock private Repositories repositories;
	@Mock(name = "linkFactoryV1") private LinkFactory linkFactoryV1;
	@Mock(name = "linkFactoryV2") private LinkFactory linkFactoryV2;
	@Mock private ItemLinks itemLinksV1, itemLinksV2;
	@Mock private RepoSearchLinks repoSearchLinksV1, repoSearchLinksV2;
	@Mock private PaginationLinkBuilder paginationLinkBuilder;
	
	// Test data
	private List<Service> itemList;
	private PageImpl<Service> itemPage;
	private Person person;
	private Service service;
	
	@Mock private MultiValueMap<String, String> params;
	
	@Mock private Link link;
	@Mock private PersistentEntity persistentEntity;
	
	@Before
	public void init() {
		this.assembler = new ItemAssembler();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() {
		
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
	
	private void initDependencies() {
		when(repositories.getPersistentEntity((Class<?>) anyObject())).thenReturn(persistentEntity);
		
		when(linkFactoryV1.getItemLinks()).thenReturn(itemLinksV1);
		when(linkFactoryV1.getRepoSearchLinks()).thenReturn(repoSearchLinksV1);
		
		when(linkFactoryV2.getItemLinks()).thenReturn(itemLinksV2);
		when(linkFactoryV2.getRepoSearchLinks()).thenReturn(repoSearchLinksV2);
		
		when(itemLinksV1.itemLink((Item) anyObject())).thenReturn(link);
		when(itemLinksV2.itemLink((Item) anyObject())).thenReturn(link);
		when(itemLinksV2.repoLink(anyString(), (Class<?>) anyObject())).thenReturn(link);
		
		when(repoSearchLinksV2.toPaginationLinkBuilder(
				(Page) anyObject(), (Class) anyObject(), anyString(), eq(params)))
						.thenReturn(paginationLinkBuilder);
	}
	
	@Test
	public void toBaseResourceList() {
		val result = assembler.toBaseResourceList(itemList, PROJECTION);
		assertNotNull(result);
		assertEquals(itemList.size(), result.size());
	}
	
	@Test
	public void toBaseResourceList_nullItemList() {
		val result = assembler.toBaseResourceList(null, PROJECTION);
		assertNull(result);
	}
	
	@Test
	public void toBaseResourcePage() {
		val result = assembler.toBaseResourcePage(Service.class, itemPage);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toBaseResourcePage_nullItemClass() {
		assembler.toBaseResourcePage(null, itemPage);
	}
	
	@Test
	public void toBaseResourcePage_nullItemPage() {
		val result = assembler.toBaseResourcePage(Service.class, null, PROJECTION);
		assertNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toBaseResourcePage2_nullItemClass() {
		assembler.toBaseResourcePage(null, itemPage, PROJECTION);
	}
	
	@Test
	public void toBaseResource_item() {
		val result = assembler.toBaseResource(service, PROJECTION);
		assertNotNull(result);
	}
	
	
	// =================================================================================================================
	// Repo search resources
	// =================================================================================================================
	
	@Test
	public void toRepoSearchResource() {
		val result = assembler.toRepoSearchResource(itemPage, ITEM_CLASS, SEARCH_PATH, params, PROJECTION);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toRepoSearchResource_nullItemClass() {
		assembler.toRepoSearchResource(itemPage, null, SEARCH_PATH, params, PROJECTION);
	}
	
	@Test(expected = NullPointerException.class)
	public void toRepoSearchResource_nullItemPage() {
		assembler.toRepoSearchResource(null, ITEM_CLASS, SEARCH_PATH, params, PROJECTION);
	}
	
	
	// =================================================================================================================
	// Global search resources
	// =================================================================================================================
	
	@Test
	public void toGlobalSearchResource() {
		// TODO
	}
	
	@Test(expected = NullPointerException.class)
	public void toGlobalSearchResource_null() {
		assembler.toGlobalSearchResource((SearchResults) null);
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
		val result = assembler.toUsernamePage(null, params);
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
