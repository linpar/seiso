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
package com.expedia.seiso.web.hmedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.util.Collections;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.seiso.web.hmedia.PaginationLinkBuilder;
import com.expedia.serf.hmedia.Relations;

/**
 * @author Willie Wheeler
 */
public class PaginationLinkBuilderTests {
	
	// Class under test
	private PaginationLinkBuilder builder;
	
	// Test data
	@Mock private Page mockPage;
	private UriComponents uriComponents;
	private MultiValueMap<String, String> params;
	
	@Before
	public void setUp() throws Exception {
		val uri = new URI("https://seiso.example.com/v2/cars/find-by-name");
		this.uriComponents = UriComponentsBuilder.fromUri(uri).build();
		
		this.params = new LinkedMultiValueMap<String, String>();
		params.set("name", "honda");
		
		this.builder = paginationLinkBuilder(3, 20, 204);
	}
		
	@Test(expected = NullPointerException.class)
	public void newBuilder_nullPage() {
		new PaginationLinkBuilder(null, uriComponents, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void newBuilder_nullUriComponents() {
		new PaginationLinkBuilder(mockPage, null, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void newBuilder_nullParams() {
		new PaginationLinkBuilder(mockPage, uriComponents, params);
	}
	
	@Test
	public void buildSelfLink() {
		val link = builder.buildSelfLink();
		assertNotNull(link);
		assertEquals(Relations.SELF, link.getRel());
		assertNotNull(link.getHref());
	}
	
	@Test
	public void buildFirstLink() {
		val link = builder.buildFirstLink();
		assertNotNull(link);
		assertEquals(Relations.FIRST, link.getRel());
		assertNotNull(link.getHref());
	}
	
	@Test
	public void buildPreviousLink() {
		val link = builder.buildPreviousLink();
		assertNotNull(link);
		assertEquals(Relations.PREVIOUS, link.getRel());
		assertNotNull(link.getHref());
	}
	
	@Test
	public void buildPreviousLinkForFirstPage() {
		this.builder = paginationLinkBuilder(0, 20, 204);
		val link = builder.buildPreviousLink();
		assertNull(link);
	}
	
	@Test
	public void buildNextLink() {
		val link = builder.buildNextLink();
		assertNotNull(link);
		assertEquals(Relations.NEXT, link.getRel());
		assertNotNull(link.getHref());
	}
	
	@Test
	public void buildNextLinkForLastPage() {
		this.builder = paginationLinkBuilder(10, 20, 204);
		val link = builder.buildNextLink();
		assertNull(link);
	}
	
	@Test
	public void buildLastLink() {
		val builder = paginationLinkBuilder(3, 20, 204);
		val link = builder.buildLastLink();
		assertNotNull(link);
		assertEquals(Relations.LAST, link.getRel());
		assertNotNull(link.getHref());
	}
	
	@Test
	public void buildLinks_noPages() {
		this.builder = paginationLinkBuilder(0, 20, 0);
		assertNull(builder.buildFirstLink());
		assertNull(builder.buildPreviousLink());
		assertNull(builder.buildNextLink());
		assertNull(builder.buildLastLink());
	}
	
	private PaginationLinkBuilder paginationLinkBuilder(int pageNumber, int pageSize, int totalItems) {
		val pageable = new PageRequest(pageNumber, pageSize);
		val page = new PageImpl(Collections.EMPTY_LIST, pageable, totalItems);
		return new PaginationLinkBuilder(page, uriComponents, params);
	}
}
