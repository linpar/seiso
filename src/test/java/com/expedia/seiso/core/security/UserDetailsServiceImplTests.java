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
package com.expedia.seiso.core.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.expedia.seiso.core.security.UserDetailsServiceImpl;
import com.expedia.seiso.domain.entity.User;
import com.expedia.seiso.domain.repo.UserRepo;

/**
 * @author Willie Wheeler
 */
public class UserDetailsServiceImplTests {

	// Class under test
	@InjectMocks
	private UserDetailsServiceImpl service;

	// Dependencies
	@Mock
	private UserRepo userRepo;

	// Test data
	private User existingUser;

	@Before
	public void setUp() {
		this.service = new UserDetailsServiceImpl();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}

	private void setUpTestData() {
		this.existingUser = new User().setUsername("existing-user");
	}

	private void setUpDependencies() {
		when(userRepo.findByUsername(existingUser.getUsername())).thenReturn(existingUser);
	}

	@Test
	public void testLoadUserByUsername() {
		val username = existingUser.getUsername();
		val actualUserDetails = service.loadUserByUsername(username);
		assertEquals(username, actualUserDetails.getUsername());
	}

	@Test(expected = NullPointerException.class)
	public void testLoadUserByNullUsername() {
		service.loadUserByUsername(null);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void testLoadNonExistentUserByUsername() {
		service.loadUserByUsername("non-existing-user");
	}
}
