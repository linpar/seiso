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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.repository.support.Repositories;

import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.repo.PersonRepo;

/**
 * @author Willie Wheeler
 */
public class ItemSaverTests {
	
	// Class under test
	@InjectMocks private ItemSaver itemSaver;
	
	// Dependencies
	@Mock private Repositories repositories;
	@Mock private PersonRepo personRepo;
	@Mock private ItemMerger itemMerger;
	
	// Test data
	private Person person, manager;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
		this.itemSaver = new ItemSaver(repositories, itemMerger);
	}
	
	private void setUpTestData() {
		// @formatter:off
		this.manager = new Person()
				.setUsername("jhogg")
				.setFirstName("Jefferson")
				.setLastName("Hogg")
				.setEmail("boss@example.com");
		this.person = new Person()
				.setUsername("jdoe")
				.setFirstName("John")
				.setLastName("Doe")
				.setEmail("jdoe@example.com")
				.setManager(manager);
		// @formatter:on
	}
	
	private void setUpDependencies() {
		when(repositories.getRepositoryFor(Person.class)).thenReturn(personRepo);
	}
	
	@Test(expected = NullPointerException.class)
	public void init_nullRepositories() {
		new ItemSaver(null, itemMerger);
	}
	
	@Test(expected = NullPointerException.class)
	public void init_nullItemMerger() {
		new ItemSaver(repositories, null);
	}
	
	@Test
	public void create() {
		itemSaver.create(person, true);
		verify(personRepo).save((Person) anyObject());
	}
	
	@Test(expected = NullPointerException.class)
	public void create_null() {
		itemSaver.create(null, true);
	}
	
	@Test
	public void update() {
		itemSaver.update(person, person, true);
		verify(itemMerger).merge(person, person, true);
		verify(personRepo).save(person);
	}
	
	@Test(expected = NullPointerException.class)
	public void update_nullItemData() {
		itemSaver.update(null, person, true);
	}
	
	@Test(expected = NullPointerException.class)
	public void update_nullItemToSave() {
		itemSaver.update(person, null, true);
	}
}
