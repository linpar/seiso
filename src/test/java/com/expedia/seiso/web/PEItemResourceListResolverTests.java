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
package com.expedia.seiso.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.PEResourcesResolver;
import com.expedia.serf.util.ResolverUtils;

/**
 * @author Willie Wheeler
 */
public class PEItemResourceListResolverTests {

	// Class under test
	@InjectMocks
	private PEResourcesResolver resolver;

	// Dependencies
	private List<HttpMessageConverter<?>> messageConverters;
	@Mock
	private HttpMessageConverter<?> messageConverter;
	@Mock
	private ItemMetaLookup itemMetaLookup;
	@Mock
	private Repositories repositories;
	@Mock
	private ResolverUtils resolverUtils;

	// Test data
	@Mock
	private MethodParameter peItemDtoListParam;
	@Mock
	private MethodParameter dummyParam;
	@Mock
	private ModelAndViewContainer mavContainer;
	@Mock
	private NativeWebRequest nativeWebRequest;
	@Mock
	private WebDataBinderFactory binderFactory;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private ServletServerHttpRequest servletServerHttpRequest;
	@Mock
	private HttpHeaders httpHeaders;

	@Before
	public void setUp() throws Exception {
		this.resolver = new PEResourcesResolver();
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}

	private void setUpTestData() {
		when(peItemDtoListParam.getParameterType()).thenReturn((Class) PEResources.class);
		when(dummyParam.getParameterType()).thenReturn((Class) Object.class);
		when(nativeWebRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
		when(httpServletRequest.getRequestURI()).thenReturn("/v1/services/some-service");
		when(servletServerHttpRequest.getHeaders()).thenReturn(httpHeaders);
		when(httpHeaders.getContentType()).thenReturn(MediaType.APPLICATION_JSON);
	}

	private void setUpDependencies() {
		this.messageConverters = new ArrayList<>();
		messageConverters.add(messageConverter);
		ReflectionTestUtils.setField(resolver, "messageConverters", messageConverters);

		when(itemMetaLookup.getItemClass("services")).thenReturn(Service.class);
		when(resolverUtils.wrapRequest(httpServletRequest)).thenReturn(servletServerHttpRequest);
	}

	@Test
	public void supportsParameter_matching() {
		val result = resolver.supportsParameter(peItemDtoListParam);
		assertTrue(result);
	}

	@Test
	public void supportsParameter_no_match() {
		val result = resolver.supportsParameter(dummyParam);
		assertFalse(result);
	}

	@Test
	public void resolveArgument() throws Exception {
		val result = resolver.resolveArgument(peItemDtoListParam, mavContainer, nativeWebRequest, binderFactory);
	}
}
