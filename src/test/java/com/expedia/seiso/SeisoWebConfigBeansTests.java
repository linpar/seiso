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
package com.expedia.seiso;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.core.config.CustomProperties;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.UriToItemKeyConverter;
import com.expedia.serf.SerfProperties;
import com.expedia.serf.hypermedia.hal.HalMapper;

/**
 * @author Willie Wheeler
 */
public class SeisoWebConfigBeansTests {
	
	// Classes under test
	@InjectMocks private SeisoWebConfigBeans beans;
	
	// Dependencies
	@Mock private SerfProperties serfProperties;
	@Mock private CustomProperties customProperties;
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private HalMapper hapMapper;
	@Mock private UriToItemKeyConverter uriToItemKeyConverter;
	
	@Before
	public void setUp() {
		this.beans = new SeisoWebConfigBeans();
		MockitoAnnotations.initMocks(this);
		setUpDependencies();
	}
	
	private void setUpDependencies() {
		when(serfProperties.getBaseUri()).thenReturn("https://seiso.example.com/v42");
	}
	
	/**
	 * This test doesn't really test anything, but I don't want the bean config to drag down the code coverage.
	 */
	@Test
	public void touchStuffToAvoidCodeCoverageHits() throws Exception {
		assertNotNull(beans.peResourceResolver());
		assertNotNull(beans.peResourcesResolver());
		assertNotNull(beans.pageableResolver());
		assertNotNull(beans.resolverUtils());
		assertNotNull(beans.byteArrayHttpMessageConverter());
		assertNotNull(beans.stringHttpMessageConverter());
		assertNotNull(beans.itemPaths());
		assertNotNull(beans.resourceAssembler());
		assertNotNull(beans.basicItemDelegate());
		assertNotNull(beans.itemSearchDelegate());
		assertNotNull(beans.globalSearchDelegate());
		assertNotNull(beans.defaultViewResolver());
		assertNotNull(beans.exceptionHandlerAdvice());
	}
}
