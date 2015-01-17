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
package com.expedia.seiso.web.jackson.v1;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.web.jackson.orig.OrigMapper;
import com.expedia.seiso.web.jackson.orig.OrigModule;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Willie Wheeler
 */
public class V1MapperTests {
	
	// Dependencies
	@Mock private OrigModule v1Module;
	
	// Test data
	@Mock private Version version;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		when(v1Module.getModuleName()).thenReturn("some-name");
		when(v1Module.version()).thenReturn(version);
	}
	
	@Test
	public void newMapper() {
		val mapper = new OrigMapper(v1Module);
		assertTrue(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
	}
	
	@Test(expected = NullPointerException.class)
	public void newMapper_nullModule() {
		new OrigMapper(null);
	}
}
