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
package com.expedia.seiso.web.filter;

import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.web.filter.LogFilter;

public class LogFilterTests {

	// Class under test
	private LogFilter filter;

	// Test data
	@Mock
	private FilterConfig filterConfig;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain chain;

	@Before
	public void setUp() throws Exception {
		this.filter = new LogFilter();
		MockitoAnnotations.initMocks(this);
		filter.init(filterConfig);
	}

	@Test
	public void doFilter_2xx() throws IOException, ServletException {
		when(response.getStatus()).thenReturn(201);
		filter.doFilter(request, response, chain);
	}

	@Test
	public void doFilter_4xx() throws IOException, ServletException {
		when(response.getStatus()).thenReturn(422);
		filter.doFilter(request, response, chain);
	}

	@Test
	public void doFilter_5xx() throws IOException, ServletException {
		when(response.getStatus()).thenReturn(500);
		filter.doFilter(request, response, chain);
	}

	@Test
	public void destroy() {
		filter.destroy();
	}
}
