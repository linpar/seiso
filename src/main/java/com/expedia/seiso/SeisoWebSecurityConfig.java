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
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.expedia.seiso.conf.CustomProperties;
import com.expedia.seiso.core.security.NoOpAuthenticationFailureHandler;
import com.expedia.seiso.core.security.NoOpAuthenticationSuccessHandler;
import com.expedia.seiso.core.security.NoOpLogoutSuccessHandler;
import com.expedia.seiso.core.security.Roles;
import com.expedia.seiso.core.security.SeisoUserDetailsContextMapper;
import com.expedia.seiso.core.security.UserDetailsServiceImpl;

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
@EnableWebMvcSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SeisoWebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired private CustomProperties customProperties;

	@Override
	protected UserDetailsService userDetailsService() { return userDetailsServiceImpl(); }

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
		// FIXME How do we specify an order here?
		// http://stackoverflow.com/questions/31302262/provider-order-using-authenticationmanagerbuilder
//		configureTestLdap(auth);
		if (customProperties.getEnableActiveDirectory()) {
			configureActiveDirectory(auth);
		}
		configureUserDetailsService(auth);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		// @formatter:off
		web
			.ignoring()
				.antMatchers("/bower_components/**")
				.antMatchers("/css/**")
				.antMatchers("/images/**")
				.antMatchers("/js/**")
				;
		// @formatter:on
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
				
				// "The HTML resources need to be available to anonymous users, not just ignored by Spring Security,
				// for reasons that will become clear."
				// https://spring.io/guides/tutorials/spring-security-and-angular-js/
				.antMatchers("/").permitAll()
				.antMatchers("/index.html").permitAll()
				.antMatchers("/view/**").permitAll()
			
				// v1 API
				.antMatchers(HttpMethod.GET, "/v1/**").permitAll()
				.antMatchers(HttpMethod.POST, "/v1/machines/search").permitAll()
				.antMatchers(HttpMethod.POST, "/v1/**").hasRole(Roles.USER)
				.antMatchers(HttpMethod.PUT, "/v1/**").hasRole(Roles.USER)
				.antMatchers(HttpMethod.DELETE, "/v1/**").hasRole(Roles.USER)
				
				// v2 API
				.antMatchers(HttpMethod.GET, "/v2/**").permitAll()
				.antMatchers(HttpMethod.POST, "/v2/**").hasRole(Roles.USER)
				.antMatchers(HttpMethod.PUT, "/v2/**").hasRole(Roles.USER)
				.antMatchers(HttpMethod.DELETE, "/v2/**").hasRole(Roles.USER)
				
				// Internal API
				.antMatchers(HttpMethod.GET, "/internal/**").permitAll()
				.antMatchers(HttpMethod.POST, "/internal/**").hasRole(Roles.USER)
				.antMatchers(HttpMethod.GET, "/mb/**").permitAll()
				
				// Admin console
				.antMatchers(HttpMethod.GET, "/health").hasRole(Roles.ADMIN)
				.antMatchers(HttpMethod.GET, "/metrics").hasRole(Roles.ADMIN)
				.antMatchers(HttpMethod.GET, "/env").hasRole(Roles.ADMIN)
				.antMatchers(HttpMethod.GET, "/dump").hasRole(Roles.ADMIN)
				
				// Blacklist
//				.anyRequest().denyAll()
				.anyRequest().hasRole(Roles.USER)
				.and()
			.httpBasic()
				.authenticationEntryPoint(entryPoint())
				.and()
			.formLogin()
//				.loginPage("TODO")
				.loginProcessingUrl("/login")
				.successHandler(new NoOpAuthenticationSuccessHandler())
				.failureHandler(new NoOpAuthenticationFailureHandler())
				.permitAll()
				.and()
			.logout()
				.logoutUrl("/logout")
				.logoutSuccessHandler(new NoOpLogoutSuccessHandler())
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
				.permitAll()
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
			// FIXME Enable. See https://spring.io/guides/tutorials/spring-security-and-angular-js/
			.csrf()
				.disable()
			;
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
	
	@Bean
	public UserDetailsContextMapper userDetailsContextMapper() {
		return new SeisoUserDetailsContextMapper();
	}
	
//	@Bean
//	public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
//		return new SeisoGrantedAuthoritiesMapper();
//	}
	
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
	
	private void configureActiveDirectory(AuthenticationManagerBuilder auth) throws Exception {
		String domain = customProperties.getAdDomain();
		String url = customProperties.getAdUrl();
		if (domain != null) {
			ActiveDirectoryLdapAuthenticationProvider provider =
					new ActiveDirectoryLdapAuthenticationProvider(domain, url);
			provider.setUserDetailsContextMapper(userDetailsContextMapper());
			
			// Hm, this doesn't seem to have any effect, so handle the mapping in the SeisoUserDetailsContextMapper.
//			provider.setAuthoritiesMapper(grantedAuthoritiesMapper());
			
			auth.authenticationProvider(provider);
		}
	}
	
	private void configureUserDetailsService(AuthenticationManagerBuilder auth) throws Exception {
		// @formatter:off
		auth
			.userDetailsService(userDetailsService())
			.passwordEncoder(passwordEncoder());
		// @formatter:on
	}
}
