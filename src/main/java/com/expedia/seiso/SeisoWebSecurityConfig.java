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
package com.expedia.seiso;


import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.expedia.seiso.security.Roles;
import com.expedia.seiso.security.SeisoUserDetailsContextMapper;
import com.expedia.seiso.security.UserDetailsServiceImpl;

// See
// http://kielczewski.eu/2014/12/spring-boot-security-application/
// http://blog.springsource.org/2013/07/03/spring-security-java-config-preview-web-security/
// https://spring.io/guides/tutorials/spring-security-and-angular-js/
// https://spring.io/guides/gs/authenticating-ldap/
// http://stackoverflow.com/questions/8658584/spring-security-salt-for-custom-userdetails
// http://stackoverflow.com/questions/8521251/spring-securitypassword-encoding-in-db-and-in-applicationconext
// http://docs.spring.io/spring-security/site/docs/3.2.x/guides/helloworld.html

// Also for OAuth2
// http://aaronparecki.com/articles/2012/07/29/1/oauth2-simplified

/**
 * Java configuration for Seiso security.
 * 
 * @author Willie Wheeler
 */
@Configuration
// Leave these commented out for now. [WLW 2015-12-01]
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SeisoWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SeisoProperties seisoProperties;
	
	@Override
	protected UserDetailsService userDetailsService() {
		return userDetailsServiceImpl();
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			// TODO Would prefer to do this without sessions if possible. But see
			// https://spring.io/guides/tutorials/spring-security-and-angular-js/
			// http://docs.aws.amazon.com/ElasticLoadBalancing/latest/DeveloperGuide/elb-sticky-sessions.html
//			.sessionManagement()
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//				.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/internal/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/**").hasAnyRole(Roles.USER, Roles.ADMIN)
				.antMatchers(HttpMethod.PUT, "/api/**").hasAnyRole(Roles.USER, Roles.ADMIN)
				.antMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole(Roles.USER, Roles.ADMIN)
				.antMatchers(HttpMethod.PATCH, "/api/**").hasAnyRole(Roles.USER, Roles.ADMIN)
				
				// Admin console
				.antMatchers(HttpMethod.GET, "/admin").hasRole(Roles.ADMIN)
				.antMatchers(HttpMethod.GET, "/admin/**").hasRole(Roles.ADMIN)
				
				// Blacklist
				.anyRequest().denyAll()
//				.anyRequest().hasRole(Roles.USER)
				.and()
			.httpBasic()
				.authenticationEntryPoint(entryPoint())
				.and()
			.exceptionHandling()
				.authenticationEntryPoint(entryPoint())
				.and()
			// FIXME Enable. See https://spring.io/guides/tutorials/spring-security-and-angular-js/
			.csrf()
				.disable()
			;
		// @formatter:on
	}
	
	@Bean
	public UserDetailsService userDetailsServiceImpl() { return new UserDetailsServiceImpl(); }
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
		// FIXME How do we specify an order here?
		// http://stackoverflow.com/questions/31302262/provider-order-using-authenticationmanagerbuilder
//		configureTestLdap(auth);
		if (seisoProperties.getEnableActiveDirectory()) {
			configureActiveDirectory(auth);
		}
		configureUserDetailsService(auth);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
	
	@Bean
	public BasicAuthenticationEntryPoint entryPoint() {
		val entry = new BasicAuthenticationEntryPoint();
		entry.setRealmName("Seiso");
		return entry;
	}
	
	@Bean
	public UserDetailsContextMapper userDetailsContextMapper() {
		return new SeisoUserDetailsContextMapper();
	}
	
	// @Bean
	// public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
	// 	   return new SeisoGrantedAuthoritiesMapper();
	// }
	
	private void configureActiveDirectory(AuthenticationManagerBuilder auth) throws Exception {
		String domain = seisoProperties.getAdDomain();
		String url = seisoProperties.getAdUrl();
		if (domain != null) {
			ActiveDirectoryLdapAuthenticationProvider provider =
					new ActiveDirectoryLdapAuthenticationProvider(domain, url);
			provider.setUserDetailsContextMapper(userDetailsContextMapper());
			
			// Hm, this doesn't seem to have any effect, so handle the mapping in the SeisoUserDetailsContextMapper.
//			provider.setAuthoritiesMapper(grantedAuthoritiesMapper());
			
			auth.authenticationProvider(provider);
		}
	}
	
	@SuppressWarnings("unused")
	private void configureTestLdap(AuthenticationManagerBuilder auth) throws Exception {
		// @formatter:off
		auth
			.ldapAuthentication()
				.userDnPatterns("uid={0},ou=people")
				.groupSearchBase("ou=groups")
				.contextSource()
				.ldif("classpath:test-server.ldif");
		// @formatter:on
	}
	

	private void configureUserDetailsService(AuthenticationManagerBuilder auth) throws Exception {
		// @formatter:off
		auth
			.userDetailsService(userDetailsService())
			.passwordEncoder(passwordEncoder());
		// @formatter:on
	}

}
