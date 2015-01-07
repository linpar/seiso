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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;

import com.expedia.seiso.domain.entity.Service;

/**
 * @author Willie Wheeler
 */
public class ItemDeleterTests {

	// Class under test
	@InjectMocks private ItemDeleter itemDeleter;

	// Dependencies
	@Mock private Repositories repositories;
	@Mock private CrudRepository serviceRepo;

	// Test data
	@Mock
	private Service service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.itemDeleter = new ItemDeleter(repositories);
		initTestData();
		initDependencies();
	}

	private void initTestData() {
	}

	private void initDependencies() {
		when(repositories.getRepositoryFor(service.getClass())).thenReturn(serviceRepo);
	}

	@Test
	public void delete() {
		itemDeleter.delete(service);
		verify(serviceRepo).delete(service);
	}

	@Test(expected = NullPointerException.class)
	public void delete_null() {
		itemDeleter.delete(null);
	}
}
