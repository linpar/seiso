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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import lombok.val;

import org.junit.Before;
import org.junit.Test;

import com.expedia.seiso.core.security.UserDetailsAdapter;
import com.expedia.seiso.domain.entity.Role;
import com.expedia.seiso.domain.entity.User;

/**
 * @author Willie Wheeler
 */
public class UserDetailsAdapterTests {

	// Class under test
	private UserDetailsAdapter adapter;

	// Test data
	private User user;

	@Before
	public void setUp() {
		val roles = new ArrayList<Role>();
		roles.add(new Role().setName("ROLE_USER"));
		roles.add(new Role().setName("ROLE_ADMIN"));

		this.user = new User().setUsername("happy1").setPassword("p@ssw0rd").setEnabled(false).setRoles(roles);
		this.adapter = new UserDetailsAdapter(user);
	}

	@Test
	public void testUsername() {
		assertEquals(user.getUsername(), adapter.getUsername());
	}

	@Test
	public void testPassword() {
		assertEquals(user.getPassword(), adapter.getPassword());
	}

	@Test
	public void testEnabled() {
		assertEquals(user.getEnabled(), adapter.isEnabled());
	}

	@Test
	public void testAccountNonExpired() {
		assertTrue(adapter.isAccountNonExpired());
	}

	@Test
	public void testAccountNonLocked() {
		assertTrue(adapter.isAccountNonLocked());
	}

	@Test
	public void testCredentialsNonExpired() {
		assertTrue(adapter.isCredentialsNonExpired());
	}

	@Test
	public void testGetAuthorities() {
		val actualAuthorities = adapter.getAuthorities();
		assertEquals(actualAuthorities.size(), user.getRoles().size());
	}
}
