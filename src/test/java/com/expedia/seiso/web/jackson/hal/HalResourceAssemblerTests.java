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
package com.expedia.seiso.web.jackson.hal;

import java.util.Arrays;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.rf.hmedia.Link;
import com.expedia.rf.hmedia.PageMetadata;
import com.expedia.rf.hmedia.PagedResources;
import com.expedia.rf.hmedia.Resource;

/**
 * @author Willie Wheeler
 */
public class HalResourceAssemblerTests {
	
	// Class under test
	private HalResourceAssembler assembler;
	
	// Test data
	private Resource resource;
	private PagedResources pagedResources;
	@Mock private Link link;
	@Mock private PageMetadata pageMeta;
	
	@Before
	public void setUp() {
		this.assembler = new HalResourceAssembler();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
	}
	
	private void setUpTestData() {
		this.resource = new Resource();
		resource.addLink(link);
		resource.setProperty("someProperty", "someValue");
		resource.setProperty("someOtherProperty", null);
		resource.setAssociation("someSingleAssociation", new Resource());
		resource.setAssociation("someListAssociation", Arrays.asList(new Resource()));
		resource.setAssociation("someOtherAssociation", null);
		
		this.pagedResources = new PagedResources(Arrays.asList(link), pageMeta, Arrays.asList(resource));
	}
	
	@Test
	public void toHalResourcePage() {
		val result = assembler.toHalPagedResources(pagedResources);
		// TODO
	}
	
	@Test(expected = NullPointerException.class)
	public void toHalResourcePage_null() {
		assembler.toHalPagedResources(null);
	}
	
	@Test
	public void toHalResource() {
		val result = assembler.toHalResource(resource, true);
		// TODO
	}
	
	@Test(expected = NullPointerException.class)
	public void toHalResource_null() {
		assembler.toHalResource(null, true);
	}

}
