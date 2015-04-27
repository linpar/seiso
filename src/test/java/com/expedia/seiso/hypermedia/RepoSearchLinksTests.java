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
package com.expedia.seiso.hypermedia;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.net.URI;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;

import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.hypermedia.ItemPaths;
import com.expedia.seiso.hypermedia.RepoSearchLinks;
import com.expedia.serf.hypermedia.Relations;

/**
 * @author Willie Wheeler
 */
public class RepoSearchLinksTests {
	private static final Class<?> ITEM_CLASS = Service.class;
	private static final String SEARCH_PATH = "some-search-path";
	
	// Class under test
	private RepoSearchLinks repoSearchLinks;
	
	// Dependencies
	@Mock private ItemPaths itemPaths;
	@Mock private ItemMetaLookup itemMetaLookup;
	
	// Test data
	private URI versionUri;
	@Mock private MultiValueMap<String, String> params;
	@Mock private Page resultPage;
	@Mock private ItemMeta itemMeta;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
		this.repoSearchLinks = new RepoSearchLinks(versionUri, itemPaths, itemMetaLookup);
	}
	
	private void setUpTestData() throws Exception {
		this.versionUri = new URI("https://seiso.example.com/v2");
	}
	
	private void setUpDependencies() {
		when(itemMetaLookup.getItemMeta((Class<?>) anyObject())).thenReturn(itemMeta);
	}
	
	@Test
	public void repoSearchListLink() {
		val result = repoSearchLinks.repoSearchListLink(Relations.SELF, Service.class);
		assertNotNull(result);
		// TODO
	}
	
	@Test(expected = NullPointerException.class)
	public void repoSearchListLink_nullRel()  {
		repoSearchLinks.repoSearchListLink(null, Service.class);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoSearchListLink_nullItemClass()  {
		repoSearchLinks.repoSearchListLink(Relations.SELF, null);
	}
}
