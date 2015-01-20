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
package com.expedia.seiso.web.resolver;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.val;

import org.junit.Before;
import org.junit.Ignore;
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

import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.seiso.web.resolver.PEResourceResolver;
import com.expedia.seiso.web.resolver.ResolverUtils;
import com.expedia.seiso.web.resolver.SimplePropertyEntry;

/**
 * @author Willie Wheeler
 */
public class PEItemResourceResolverTests {

	// Class under test
	@InjectMocks
	private PEResourceResolver resolver;

	// Dependencies
	private List<HttpMessageConverter<?>> messageConverters;
	@Mock
	private ItemMetaLookup itemMetaLookup;
	@Mock
	private Repositories repositories;
	@Mock
	private HttpMessageConverter<?> messageConverter;
	@Mock
	private ResolverUtils resolverUtils;

	// Test data
	@Mock
	private MethodParameter param;
	@Mock
	private ModelAndViewContainer mavContainer;
	@Mock
	private NativeWebRequest nativeWebRequest;
	@Mock
	private WebDataBinderFactory binderFactory;
	@Mock
	private ItemMeta nodeRepoMeta;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private ServletServerHttpRequest servletServerHttpRequest;
	@Mock
	private HttpHeaders httpHeaders;

	@Before
	public void setUp() {
		this.resolver = new PEResourceResolver(new ArrayList<SimplePropertyEntry>());
		MockitoAnnotations.initMocks(this);
		setUpTestData();
		setUpDependencies();
	}

	private void setUpTestData() {
		// this.httpServletRequest = new MockHttpServletRequest();
		// httpServletRequest.addHeader("Accept", "application/json");
		// httpServletRequest.addHeader("Content-Type", "application/json");
		//
		when(nativeWebRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
		when(servletServerHttpRequest.getHeaders()).thenReturn(httpHeaders);
		when(httpHeaders.getContentType()).thenReturn(MediaType.APPLICATION_JSON);
	}

	private void setUpDependencies() {
		when(itemMetaLookup.getItemClass(RepoKeys.NODES)).thenReturn(Node.class);

		this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(messageConverter);
		ReflectionTestUtils.setField(resolver, "messageConverters", messageConverters);

		when(resolverUtils.wrapRequest(httpServletRequest)).thenReturn(servletServerHttpRequest);
	}

	// TODO
	@Test
	@Ignore
	public void resolveArgument() throws Exception {
		val result = resolver.resolveArgument(param, mavContainer, nativeWebRequest, binderFactory);

		// Need to set the converter up to make this work.
		// assertNotNull(result);
	}
}
