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

import static org.junit.Assert.*;
import lombok.val;

import org.junit.Test;

import com.expedia.rf.hmedia.Link;
import com.expedia.rf.hmedia.Relations;

/**
 * This test exists primarily to exclude {@link Link} from the code coverage "missed instructions" count.
 * 
 * @author Willie Wheeler
 */
public class LinkTests {
	private static final String REL = Relations.SELF;
	private static final String HREF = "https://seiso.example.com/v2/services";
	private static final String TITLE = "Services";
	
	@Test
	public void newLink() {
		val link = new Link(REL, HREF);
		link.setTitle(TITLE);
		link.setTemplated(false);
	}
	
	@Test(expected = NullPointerException.class)
	public void newLink_nullRel() {
		new Link(null, HREF);
	}
	
	@Test(expected = NullPointerException.class)
	public void newLink_nullHref() {
		new Link(REL, null);
	}
	
	@Test
	public void testAccessors() {
		val link = new Link(REL, HREF);
		assertEquals(REL, link.getRel());
		assertEquals(HREF, link.getHref());
		
		link.setRel(REL + "-extra");
		assertEquals(REL + "-extra", link.getRel());
		
		link.setHref(HREF + "-extra");
		assertEquals(HREF + "-extra", link.getHref());
		
		assertNull(link.getTitle());
		link.setTitle(TITLE);
		assertEquals(TITLE, link.getTitle());
		
		assertNull(link.getTemplated());
		link.setTemplated(true);
		assertTrue(link.getTemplated());
	}
	
	@Test
	public void testEquals() {
		val link1 = new Link("self", "https://seiso.example.com/v42/services");
		val link2 = new Link("self", "https://seiso.example.com/v42/services");
		assertTrue(link1.equals(link2));
		
		link1.setTitle("Services");
		link2.setTitle("Services");
		assertTrue(link1.equals(link2));
		
		link1.setTemplated(false);
		link2.setTemplated(false);
		assertTrue(link1.equals(link2));
		
		link1.setRel("other");
		assertFalse(link1.equals(link2));
		link1.setRel("self");
		assertTrue(link1.equals(link2));
		
		link1.setHref("https://seiso.io");
		assertFalse(link1.equals(link2));
		link1.setHref("https://seiso.example.com/v42/services");
		assertTrue(link1.equals(link2));
		
		link1.setTitle("Data Centers");
		assertFalse(link1.equals(link2));
		link1.setTitle("Services");
		assertTrue(link1.equals(link2));
		
		link1.setTemplated(true);
		assertFalse(link1.equals(link2));
		link1.setTemplated(false);
		assertTrue(link1.equals(link2));
	}
	
	@Test
	public void testHashCode() {
		val link = new Link(REL, HREF);
		assertTrue(link.hashCode() >= Integer.MIN_VALUE);
		
		link.setTitle(TITLE);
		assertTrue(link.hashCode() >= Integer.MIN_VALUE);
		
		link.setTemplated(false);
		assertTrue(link.hashCode() >= Integer.MIN_VALUE);
	}
	
	@Test
	public void testToString() {
		val link = new Link(REL, HREF);
		assertFalse(link.toString().isEmpty());
		
		link.setTitle(TITLE);
		assertFalse(link.toString().isEmpty());
		
		link.setTemplated(false);
		assertFalse(link.toString().isEmpty());
	}
}
