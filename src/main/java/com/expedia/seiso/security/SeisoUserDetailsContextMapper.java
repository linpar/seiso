/* 
 * Copyright 2013-2016 the original author or authors.
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
package com.expedia.seiso.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;

import lombok.extern.slf4j.XSlf4j;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;


/**
 * Maps Active Directory user to Seiso user. This might work for LDAP servers generally as well. I'm not sure.
 * 
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class SeisoUserDetailsContextMapper implements UserDetailsContextMapper {
	
	// TODO Inject role/permission mapping and use it to add permissions
	
	private static final String AD_GIVEN_NAME = "givenname";
	private static final String AD_SURNAME = "sn";
	private static final String AD_EMAIL = "mail";
	
	@Override
	public UserDetails mapUserFromContext(
			DirContextOperations ctx,
			String username,
			Collection<? extends GrantedAuthority> authorities) {
		
//		printAttributeIds(ctx);
		
		SeisoUser user = new SeisoUser(username, mapAuthorities(authorities));
		user.setFirstName(extractUnique(ctx, AD_GIVEN_NAME));
		user.setLastName(extractUnique(ctx, AD_SURNAME));
		user.setEmail(extractUnique(ctx, AD_EMAIL));
		
		// FIXME Figure out how we want to handle these
		user.setEnabled(true);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		
		return user;
	}
	
	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		// Not relevant for authentication:
		// http://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#ldap-custom-user-details
		throw new UnsupportedOperationException("Not supported");
	}
	
	@SuppressWarnings("unused")
	private void printAttributeIds(DirContextOperations ctx) {
		Attributes attrs = ctx.getAttributes();
		NamingEnumeration<? extends String> allAttrs = attrs.getIDs();
		while (allAttrs.hasMoreElements()) {
			String attrId = allAttrs.nextElement();
			log.trace("attrId={}", attrId);
		}
	}
	
	private Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
		
		// TODO Handle administrative users.
		// Fine to hardcode as "Seiso Admin" -> Roles.ADMIN for now.
		Set<GrantedAuthority> mapped = new HashSet<>();
		// Auto-prepend ROLE_ since the framework expects this.
		mapped.add(new SimpleGrantedAuthority("ROLE_" + Roles.USER));
		return mapped;
	}
	
	private String extractUnique(DirContextOperations ctx, String name) {
		SortedSet<String> mailSet = ctx.getAttributeSortedStringSet(name);
		Iterator<String> it = mailSet.iterator();
		return it.hasNext() ? it.next() : null;
	}
}
