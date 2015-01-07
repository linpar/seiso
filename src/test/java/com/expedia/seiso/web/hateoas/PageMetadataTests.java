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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import lombok.val;

import org.junit.Test;

/**
 * @author Willie Wheeler
 */
public class PageMetadataTests {
	
	@Test
	public void newPageMetadata() {
		val meta = new PageMetadata(20, 2, 201);
		assertEquals(20, meta.getPageSize());
		assertEquals(2, meta.getPageNumber());
		assertEquals(201, meta.getTotalItems());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void newPageMetadata_pageSizeNegative() {
		new PageMetadata(-1, 2, 201);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void newPageMetadata_pageSizeZero() {
		new PageMetadata(0, 2, 201);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void newPageMetadata_pageNumberNegative() {
		new PageMetadata(20, -1, 201);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void newPageMetadata_totalItemsNegative() {
		new PageMetadata(20, 2, -1);
	}
	
	@Test
	public void getTotalPages_pageSizeDividesTotalItems() {
		val meta1 = new PageMetadata(20, 2, 200);
		assertEquals(10, meta1.getTotalPages());
		
		val meta2 = new PageMetadata(20, 0, 20);
		assertEquals(1, meta2.getTotalPages());
	}
	
	@Test
	public void getTotalPages_pageSizeDoesNotDivideTotalItems() {
		val meta1 = new PageMetadata(20, 2, 201);
		assertEquals(11, meta1.getTotalPages());
		
		val meta2 = new PageMetadata(20, 0, 5);
		assertEquals(1, meta2.getTotalPages());
	}
	
	@Test
	public void testEquals() {
		val meta1 = new PageMetadata(20, 2, 201);
		val meta2 = new PageMetadata(20, 2, 201);
		assertTrue(meta1.equals(meta2));
	}
	
	@Test
	public void testHashCode() {
		val meta = new PageMetadata(20, 2, 201);
		assertTrue(meta.hashCode() >= Integer.MIN_VALUE);
	}
	
	@Test
	public void testToString() {
		val meta = new PageMetadata(20, 2, 201);
		assertFalse(meta.toString().isEmpty());
	}
}
