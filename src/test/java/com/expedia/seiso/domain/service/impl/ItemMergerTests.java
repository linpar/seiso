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
package com.expedia.seiso.domain.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceGroup;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.repo.adapter.RepoAdapterLookup;
import com.expedia.seiso.domain.repo.adapter.SimpleItemRepoAdapter;

public class ItemMergerTests {

	// Class under test
	@InjectMocks private ItemMerger itemMerger;

	// Dependencies
	@Mock private RepoAdapterLookup repoAdapterLookup;
	@Mock private SimpleItemRepoAdapter simpleItemRepoAdapter;

	// Test data
	private ServiceGroup serviceGroup;
	private Service srcService;
	private Service destService;
	private Node srcNode;
	private Node destNode;
	private IpAddressRole srcIpAddressRole;
	private IpAddressRole destIpAddressRole;

	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.itemMerger = new ItemMerger(repoAdapterLookup);
		initTestData();
		initDependencies();
	}

	private void initTestData() {
		// @formatter:off
		this.serviceGroup = new ServiceGroup().setKey("my-service-group").setName("My Service Group");
		this.srcService = new Service().setKey("my-service").setName("My Service").setGroup(serviceGroup);
		this.destService = new Service().setKey("my-service").setName("My Old Name")
				.setDescription("Some old description");
		destService.setId(14L);
		// @formatter:off
	}

	private void initDependencies() {
		// @formatter:off
		when(repoAdapterLookup.getRepoAdapterFor(ServiceGroup.class)).thenReturn(simpleItemRepoAdapter);
		when(simpleItemRepoAdapter.find(new SimpleItemKey(ServiceGroup.class, serviceGroup.getKey()))).thenReturn(
				serviceGroup);
		// @formatter:on
	}

	@Test
	public void mergeService() {
		itemMerger.merge(srcService, destService);
		assertEquals(srcService.getKey(), destService.getKey());
		assertEquals(srcService.getName(), destService.getName());
		assertEquals(srcService.getDescription(), destService.getDescription());
	}

	@Test
	public void mergeNode() {

	}

	@Test
	public void mergeIpAddressRole() {

	}
}
