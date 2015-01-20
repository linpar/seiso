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
package com.expedia.seiso.domain.meta;

import lombok.val;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.repository.support.Repositories;

import com.expedia.seiso.core.exception.NotFoundException;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.meta.ItemMetaLookup;

/**
 * @author Willie Wheeler
 */
public class ItemMetaLookupTests {

	// Class under test
	@InjectMocks private ItemMetaLookup lookup;

	// Dependencies
	@Mock private Repositories repositories;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.lookup = new ItemMetaLookup(repositories);
	}

	// FIXME Ignoring because I'm not yet sure how best to mock Repositories. [WLW]
	@Ignore
	@Test
	public void testFindByItemClass() {
		val result = lookup.getItemMeta(Node.class);
	}

	@Test(expected = NullPointerException.class)
	public void testFindByItemClass_nullClass() {
		lookup.getItemMeta((Class<?>) null);
	}

	@Test(expected = NotFoundException.class)
	public void testFindByItemClass_notFound() {
		lookup.getItemMeta(Item.class);
	}
}
