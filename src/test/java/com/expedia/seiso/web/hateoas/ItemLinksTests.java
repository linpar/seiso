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
package com.expedia.seiso.web.hateoas;

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

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.Relations;

/**
 * @author Willie Wheeler
 */
public class ItemLinksTests {

	// Class under test
	private ItemLinks itemLinks;

	// Dependencies
	@Mock private ItemPaths itemPaths;
	@Mock private ItemMetaLookup itemMetaLookup;

	// Test data
	private URI v2BaseUri;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
		this.itemLinks = new ItemLinks(v2BaseUri, itemPaths, itemMetaLookup);
	}

	private void initTestData() throws Exception {
		this.v2BaseUri = new URI("http://seiso.example.com/v2");
	}

	private void initDependencies() {
		when(itemPaths.resolve((Item) anyObject())).thenReturn(new String[0]);
	}

	@Test
	public void itemLink() {
		val service = new Service().setKey("some-key");
		val link = itemLinks.itemLink(service);
		assertNotNull(link);
		assertEquals(Relations.SELF, link.getRel());
	}
}
