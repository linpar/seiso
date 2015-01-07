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

import static org.junit.Assert.assertNotNull;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Environment;
import com.expedia.seiso.domain.entity.InfrastructureProvider;
import com.expedia.seiso.domain.entity.LoadBalancer;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.Region;
import com.expedia.seiso.domain.entity.Service;

/**
 * @author Willie Wheeler
 */
public class ItemPathsTests {
	private static final String KEY = "some-key";

	// Class under test
	@InjectMocks private ItemPaths itemPaths;

	// Dependencies

	// Test data

	@Before
	public void setUp() {
		this.itemPaths = new ItemPaths();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void resolveDataCenter() {
		val item = new DataCenter().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveEnvironment() {
		val item = new Environment().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveInfrastructureProvider() {
		val item = new InfrastructureProvider().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveLoadBalancer() {
		val item = new LoadBalancer().setName(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolvePerson() {
		val item = new Person().setUsername(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveRegion() {
		val item = new Region().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test
	public void resolveService() {
		val item = new Service().setKey(KEY);
		val path = itemPaths.resolve(item);
		assertNotNull(path);
	}

	@Test(expected = NullPointerException.class)
	public void resolve_null() {
		itemPaths.resolve(null);
	}
}
