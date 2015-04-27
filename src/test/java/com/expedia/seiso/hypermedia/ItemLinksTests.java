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

import static org.junit.Assert.assertEquals;
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

import com.expedia.seiso.core.config.CustomProperties;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.hypermedia.ItemLinks;
import com.expedia.seiso.hypermedia.ItemPaths;
import com.expedia.serf.hypermedia.Relations;

/**
 * @author Willie Wheeler
 */
public class ItemLinksTests {

	// Class under test
	private ItemLinks itemLinks;

	// Dependencies
	@Mock private CustomProperties customProperties;
	@Mock private ItemPaths itemPaths;
	@Mock private ItemMetaLookup itemMetaLookup;

	// Test data
	private URI v2BaseUri;
	@Mock private ItemMeta serviceMeta;
	@Mock private Page page;
	@Mock private MultiValueMap<String, String> params;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
		this.itemLinks = new ItemLinks(v2BaseUri, itemPaths, itemMetaLookup);
	}

	private void setUpTestData() throws Exception {
		this.v2BaseUri = new URI("http://seiso.example.com/v2");
	}

	private void setUpDependencies() {
		when(itemMetaLookup.getItemMeta(Service.class)).thenReturn(serviceMeta);
		when(itemPaths.convert((Item) anyObject())).thenReturn(new String[0]);
	}

	@Test
	public void itemLink() {
		val service = new Service().setKey("some-key");
		val link = itemLinks.itemLink(service);
		assertNotNull(link);
		assertEquals(Relations.SELF, link.getRel());
	}
	
	@Test
	public void repoFirstLink() {
		val result = itemLinks.repoFirstLink(Service.class, page, params);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoFirstLink_nullItemClass() {
		itemLinks.repoFirstLink(null, page, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoFirstLink_nullPage() {
		itemLinks.repoFirstLink(Service.class, null, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoFirstLink_nullParams() {
		itemLinks.repoFirstLink(Service.class, page, null);
	}
	
	@Test
	public void repoPrevLink() {
		val result = itemLinks.repoPrevLink(Service.class, page, params);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoPrevLink_nullItemClass() {
		itemLinks.repoPrevLink(null, page, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoPrevLink_nullPage() {
		itemLinks.repoPrevLink(Service.class, null, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoPrevLink_nullParams() {
		itemLinks.repoPrevLink(Service.class, page, null);
	}
	
	@Test
	public void repoNextLink() {
		val result = itemLinks.repoNextLink(Service.class, page, params);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoNextLink_nullItemClass() {
		itemLinks.repoNextLink(null, page, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoNextLink_nullPage() {
		itemLinks.repoNextLink(Service.class, null, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoNextLink_nullParams() {
		itemLinks.repoNextLink(Service.class, page, null);
	}
	
	@Test
	public void repoLastLink() {
		val result = itemLinks.repoLastLink(Service.class, page, params);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoLastLink_nullItemClass() {
		itemLinks.repoLastLink(null, page, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoLastLink_nullPage() {
		itemLinks.repoLastLink(Service.class, null, params);
	}
	
	@Test(expected = NullPointerException.class)
	public void repoLastLink_nullParams() {
		itemLinks.repoLastLink(Service.class, page, null);
	}
}
