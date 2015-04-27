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
package com.expedia.seiso.web.controller.v1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Test;

import com.expedia.serf.hypermedia.Link;
import com.expedia.serf.hypermedia.PageMetadata;
import com.expedia.serf.hypermedia.PagedResources;
import com.expedia.serf.hypermedia.Relations;
import com.expedia.serf.hypermedia.Resource;

/**
 * @author Willie Wheeler
 */
public class ResponseHeadersV1Tests {
	private static final String FIRST_HREF = "https://first";
	private static final String PREV_HREF = "https://prev";
	private static final String NEXT_HREF = "https://next";
	private static final String LAST_HREF = "https://last";
	private static final long TOTAL_ITEMS = 200L;
	private static final long PAGE_SIZE = 10;
	
	// Class under test
	private ResponseHeadersV1 responseHeaders;
	
	// Test data
	private List<Link> links;
	private PageMetadata pageMeta;
	private List<Resource> items;
	private PagedResources baseResourcePage;
	
	@Before
	public void setUp() {
		this.responseHeaders = new ResponseHeadersV1();
		setUpTestData();
	}
	
	private void setUpTestData() {
		this.links = new ArrayList<>();
		links.add(new Link(Relations.FIRST, FIRST_HREF));
		links.add(new Link(Relations.PREVIOUS, PREV_HREF));
		links.add(new Link(Relations.NEXT, NEXT_HREF));
		links.add(new Link(Relations.LAST, LAST_HREF));
		
		this.pageMeta = new PageMetadata(PAGE_SIZE, 2, TOTAL_ITEMS);
		this.items = new ArrayList<Resource>();
		this.baseResourcePage = new PagedResources(links, pageMeta, items);
	}
	
	@Test
	public void buildResponseHeaders() {
		val result = responseHeaders.buildResponseHeaders(baseResourcePage);
		assertEquals(FIRST_HREF, result.getFirst(ResponseHeadersV1.X_PAGINATION_FIRST));
		assertEquals(PREV_HREF, result.getFirst(ResponseHeadersV1.X_PAGINATION_PREV));
		assertEquals(NEXT_HREF, result.getFirst(ResponseHeadersV1.X_PAGINATION_NEXT));
		assertEquals(LAST_HREF, result.getFirst(ResponseHeadersV1.X_PAGINATION_LAST));
		
		val resultTotalItems = Long.parseLong(result.getFirst(ResponseHeadersV1.X_PAGINATION_TOTAL_ELEMENTS));
		val resultTotalPages = Long.parseLong(result.getFirst(ResponseHeadersV1.X_PAGINATION_TOTAL_PAGES));
		assertEquals(pageMeta.getTotalItems(), resultTotalItems);
		assertEquals(pageMeta.getTotalPages(), resultTotalPages);
	}
	
	@Test(expected = NullPointerException.class)
	public void buildResponseHeaders_null() {
		responseHeaders.buildResponseHeaders(null);
	}
}
