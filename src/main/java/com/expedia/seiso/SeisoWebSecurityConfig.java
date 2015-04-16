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
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.expedia.seiso.core.security.Roles;
import com.expedia.seiso.core.security.UserDetailsServiceImpl;

// See http://blog.springsource.org/2013/07/03/spring-security-java-config-preview-web-security/
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
public class SeisoWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected UserDetailsService userDetailsService() { return userDetailsServiceImpl(); }

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeRequests()
				
				// Admin console
				// Spring Security prepends "ROLE_" to the role.
				// So in the DB, the roles have to be of the form ROLE_XXX.
				.antMatchers(HttpMethod.GET, "/health").hasRole(Roles.ROLE_ADMIN)
				.antMatchers(HttpMethod.GET, "/metrics").hasRole(Roles.ROLE_ADMIN)
				.antMatchers(HttpMethod.GET, "/env").hasRole(Roles.ROLE_ADMIN)
				.antMatchers(HttpMethod.GET, "/dump").hasRole(Roles.ROLE_ADMIN)
				
				.antMatchers(HttpMethod.GET, "/**").permitAll()
				.antMatchers(HttpMethod.POST, "/v1/machines/search").permitAll()
				
				// For Eos commands
				.antMatchers(HttpMethod.POST, "/internal/**").permitAll()
				
//				.anyRequest().hasRole(Roles.ROLE_USER)
				
				// FIXME Change to denyAll() (blacklist)
				.anyRequest().authenticated()
				
				.and()
			.httpBasic()
				.authenticationEntryPoint(entryPoint())
				.and()
			.exceptionHandling()
				.authenticationEntryPoint(entryPoint())
				.and()
//			.headers()
//				.cacheControl()
//				.contentTypeOptions()
//				.frameOptions()
//				.httpStrictTransportSecurity()
//				.and()
			.csrf()
				.disable();
		// @formatter:on
	}
	
	@Bean
	public UserDetailsService userDetailsServiceImpl() { return new UserDetailsServiceImpl(); }
	
	@Bean
	public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

	@Bean
	public BasicAuthenticationEntryPoint entryPoint() {
		val entry = new BasicAuthenticationEntryPoint();
		entry.setRealmName("Seiso");
		return entry;
	}
}
