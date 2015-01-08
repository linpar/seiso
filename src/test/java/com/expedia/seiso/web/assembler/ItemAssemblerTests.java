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
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import com.expedia.seiso.web.hateoas.link.RepoSearchLinks;

/**
 * @author Willie Wheeler
 */
public class ItemAssemblerTests {
	
	// Class under test
	@InjectMocks private ItemAssembler assembler;
	
	// Dependencies
	@Mock private Repositories repositories;
	@Mock(name = "linkFactoryV1") private LinkFactory linkFactoryV1;
	@Mock(name = "linkFactoryV2") private LinkFactory linkFactoryV2;
	@Mock private ItemLinks itemLinksV1, itemLinksV2;
	@Mock private RepoSearchLinks repoSearchLinksV1, repoSearchLinksV2;
	
	// Test data
	private List<Service> itemList;
	private PageRequest pageRequest;
	private PageImpl<Service> itemPage;
	@Mock private PersistentEntity persistentEntity;
	@Mock private Link link;
	private Person person;
	private Service service;
	private ProjectionNode projectionNode;
	@Mock private MultiValueMap<String, String> params;
	
	@Before
	public void init() {
		this.assembler = new ItemAssembler();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() {
		this.pageRequest = new PageRequest(5, 20);
		this.itemList = Arrays.asList(service);
		this.itemPage = new PageImpl<Service>(itemList, pageRequest, 8675309);
		
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
		this.projectionNode = ProjectionNode.FLAT_PROJECTION_NODE;
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
	}
	
	@Test
	public void toBaseResourceList() {
		val result = assembler.toBaseResourceList(itemList, projectionNode);
		assertNotNull(result);
		assertEquals(itemList.size(), result.size());
	}
	
	@Test
	public void toBaseResourceList_nullItemList() {
		val result = assembler.toBaseResourceList(null, projectionNode);
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
		val result = assembler.toBaseResourcePage(Service.class, null, projectionNode);
		assertNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toBaseResourcePage2_nullItemClass() {
		assembler.toBaseResourcePage(null, itemPage, projectionNode);
	}
	
	@Test
	public void toBaseResource_item() {
		val result = assembler.toBaseResource(service, projectionNode);
		assertNotNull(result);
	}
	
	@Test
	public void toBaseResource_searchResults() {
		// TODO
	}
	
	@Test
	public void toBaseResource_searchResults_null() {
		val result = assembler.toBaseResource((SearchResults) null);
		assertNull(result);
	}
	
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
