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
package com.expedia.seiso.web.httpmessageconverter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;

import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.ServiceInstancePortKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.web.converter.UriToItemKeyConverter;

/**
 * @author Willie Wheeler
 */
public class ItemKeyHttpMessageConverterTests {
	
	// Class under test
	private ItemKeyHttpMessageConverter httpMessageConverter;
	
	// Dependencies
	@Mock private UriToItemKeyConverter uriToItemKeyConverter;
	
	// Test data
	@Mock private ItemKey itemKey;
	@Mock private HttpInputMessage inputMessage;
	@Mock private HttpOutputMessage outputMessage;
	private InputStream inputStream;
	
	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.httpMessageConverter = new ItemKeyHttpMessageConverter(uriToItemKeyConverter);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() throws Exception {
		this.inputStream = new ByteArrayInputStream("foo".getBytes(StandardCharsets.UTF_8));
		when(inputMessage.getBody()).thenReturn(inputStream);
	}
	
	private void initDependencies() {
		when(uriToItemKeyConverter.convert(anyString())).thenReturn(itemKey);
	}
	
	@Test(expected = NullPointerException.class)
	public void init_null() {
		new ItemKeyHttpMessageConverter(null);
	}
	
	@Test
	public void supports() {
		assertTrue(httpMessageConverter.supports(ItemKey.class));
		assertTrue(httpMessageConverter.supports(SimpleItemKey.class));
		assertTrue(httpMessageConverter.supports(ServiceInstancePortKey.class));
	}
	
	@Test
	public void readInternal() throws Exception {
		val result = httpMessageConverter.readInternal(SimpleItemKey.class, inputMessage);
		assertNotNull(result);
		assertSame(itemKey, result);
	}
	
	@Test
	public void writeInternal() throws Exception {
		httpMessageConverter.writeInternal(itemKey, outputMessage);
	}
}
