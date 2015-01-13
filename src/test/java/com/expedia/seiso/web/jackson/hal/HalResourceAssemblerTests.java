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

import com.expedia.seiso.web.hateoas.BaseResource;
import com.expedia.seiso.web.hateoas.BaseResourcePage;
import com.expedia.seiso.web.hateoas.Link;
import com.expedia.seiso.web.hateoas.PageMetadata;

/**
 * @author Willie Wheeler
 */
public class HalResourceAssemblerTests {
	
	// Class under test
	private HalResourceAssembler assembler;
	
	// Test data
	private BaseResource baseResource;
	private BaseResourcePage baseResourcePage;
	@Mock private Link link;
	@Mock private PageMetadata pageMeta;
	
	@Before
	public void init() {
		this.assembler = new HalResourceAssembler();
		MockitoAnnotations.initMocks(this);
		initTestData();
	}
	
	private void initTestData() {
		this.baseResource = new BaseResource();
		baseResource.addV1Link(link);
		baseResource.addV2Link(link);
		baseResource.setProperty("someProperty", "someValue");
		baseResource.setProperty("someOtherProperty", null);
		baseResource.setAssociation("someSingleAssociation", new BaseResource());
		baseResource.setAssociation("someListAssociation", Arrays.asList(new BaseResource()));
		baseResource.setAssociation("someOtherAssociation", null);
		
		this.baseResourcePage = new BaseResourcePage(Arrays.asList(link), pageMeta, Arrays.asList(baseResource));
	}
	
	@Test
	public void toHalResourcePage() {
		val result = assembler.toHalResourcePage(baseResourcePage);
		// TODO
	}
	
	@Test(expected = NullPointerException.class)
	public void toHalResourcePage_null() {
		assembler.toHalResourcePage(null);
	}
	
	@Test
	public void toHalResource() {
		val result = assembler.toHalResource(baseResource, true);
		// TODO
	}
	
	@Test(expected = NullPointerException.class)
	public void toHalResource_null() {
		assembler.toHalResource(null, true);
	}

}
