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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.PagedResources.PageMetadata;

import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.ResponseHeaders;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class ResponseHeadersBuilderTests {
	
	// Class under test
	@InjectMocks private ResponseHeadersBuilder builder;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private ItemLinks itemLinks;
	@Mock private PageLinks pageLinks;
	
	// Test objects
	@Mock private PageMetadata pageMeta;
	@Mock private LinkBuilder selfLinkBuilder;
	private Link selfLink, firstLink, prevLink, nextLink, lastLink;
	
	@Before
	public void setUp() throws Exception {
		this.builder = new ResponseHeadersBuilder();
		MockitoAnnotations.initMocks(this);
		initTestObjects();
		initDependencies();
	}
	
	private void initTestObjects() {
		this.selfLink = new Link("http://self");
		this.firstLink = new Link("http://first", "first");
		this.prevLink = new Link("http://prev", "prev");
		this.nextLink = new Link("http://next", "next");
		this.lastLink = new Link("http://last", "last");
		when(selfLinkBuilder.withRel(Link.REL_SELF)).thenReturn(selfLink);
	}
	
	private void initDependencies() {
		when(itemMetaLookup.getItemClass("people")).thenReturn(Person.class);
		when(itemLinks.selfLinkForCrudRepo("people", "keys")).thenReturn(selfLink);
		when(pageLinks.selfLink("people", "keys", pageMeta)).thenReturn(selfLink);
		when(pageLinks.firstLink("people", "keys", pageMeta)).thenReturn(firstLink);
		when(pageLinks.prevLink("people", "keys", pageMeta)).thenReturn(prevLink);
		when(pageLinks.nextLink("people", "keys", pageMeta)).thenReturn(nextLink);
		when(pageLinks.lastLink("people", "keys", pageMeta)).thenReturn(lastLink);
	}
	
	@Test
	public void buildForCrudRepo() {
		// In reality, people/keys is paging, but it doesn't matter in this test.
		val headers = builder.buildForCrudRepo("people", "keys");
		assertNotNull(headers.get(ResponseHeaders.LINK));
		assertNotNull(headers.get(ResponseHeaders.X_SELF));
		assertNull(headers.get(ResponseHeaders.X_PAGINATION_FIRST));
		assertNull(headers.get(ResponseHeaders.X_PAGINATION_PREV));
		assertNull(headers.get(ResponseHeaders.X_PAGINATION_NEXT));
		assertNull(headers.get(ResponseHeaders.X_PAGINATION_LAST));
	}
	
	@Test
	public void buildForPagingAndSortingRepo() {
		val headers = builder.buildForPagingAndSortingRepo("people", "keys", pageMeta);
		assertNotNull(headers.get(ResponseHeaders.LINK));
		assertNotNull(headers.get(ResponseHeaders.X_SELF));
		assertNotNull(headers.get(ResponseHeaders.X_PAGINATION_FIRST));
		assertNotNull(headers.get(ResponseHeaders.X_PAGINATION_PREV));
		assertNotNull(headers.get(ResponseHeaders.X_PAGINATION_NEXT));
		assertNotNull(headers.get(ResponseHeaders.X_PAGINATION_LAST));
	}
}
