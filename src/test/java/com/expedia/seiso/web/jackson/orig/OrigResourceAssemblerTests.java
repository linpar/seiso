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
package com.expedia.seiso.web.jackson.orig;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.expedia.rf.hmedia.Link;
import com.expedia.rf.hmedia.PageMetadata;
import com.expedia.rf.hmedia.PagedResources;
import com.expedia.rf.hmedia.Resource;
import com.expedia.rf.hmedia.Resources;

/**
 * @author Willie Wheeler
 */
public class OrigResourceAssemblerTests {
	private static final String SERVICES_HREF = "https://seiso.example.com/v1/services";
	private static final String SEARCH_HREF = SERVICES_HREF + "/search";
	private static final String MY_SERVICE_HREF = SERVICES_HREF + "/my-service";
	
	// Class under test
	@InjectMocks private OrigResourceAssembler assembler;
	
	// Test data
	private Resources resources;
	private PagedResources pagedResources;
	private Resource resource;
	
	@Before
	public void setUp() {
		this.assembler = new OrigResourceAssembler();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
	}
	
	private void setUpTestData() {
		val links = Arrays.asList(new Link("self", SERVICES_HREF), new Link("s:search", SEARCH_HREF));
		val pageMeta = new PageMetadata(20, 0, 100);
		
		this.resource = new Resource();
		resource.addLink(new Link("self", MY_SERVICE_HREF));
		resource.addLink(new Link("up", SERVICES_HREF));
		resource.setProperty("key", "my-service");
		resource.setProperty("name", "My Service");
		resource.setAssociation("group", null);
		resource.setAssociation("type", new Resource());
		resource.setAssociation("serviceInstances", Collections.EMPTY_LIST);
		resource.setAssociation("unknownType", Collections.EMPTY_SET);
		
		this.resources = new Resources(links, Arrays.asList(resource));
		this.pagedResources = new PagedResources(links, pageMeta, Arrays.asList(resource));
	}
	
	@Test
	public void toOrigResources() {
		val result = assembler.toOrigResources(resources);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toOrigResources_null() {
		assembler.toOrigResources(null);
	}
	
	@Test
	public void toOrigPagedResources() {
		val result = assembler.toOrigPagedResources(pagedResources);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toOrigPagedResources_null() {
		assembler.toOrigPagedResources(null);
	}
	
	@Test
	public void toOrigResource() {
		val result = assembler.toOrigResource(resource);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void toOrigResource_null() {
		assembler.toOrigResource(null);
	}
}
