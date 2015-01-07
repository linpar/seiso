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
package com.expedia.seiso.web.assembler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.BeanWrapper;

import com.expedia.seiso.domain.entity.Item;

/**
 * @author Willie Wheeler
 */
public class ItemPropertyHandlerTests {

	// Class under test
	private ItemPropertyHandler handler;

	// Test data
	@Mock private BeanWrapper<Item> wrapper;
	@Mock private Map<String, Object> model;
	@Mock private PersistentProperty<?> idProp;
	@Mock private PersistentProperty<?> keyProp;
	@Mock private Object wrappedProp;

	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.handler = new ItemPropertyHandler(wrapper, model);
		when(idProp.isIdProperty()).thenReturn(true);
		when(idProp.getName()).thenReturn("id");
		when(keyProp.isIdProperty()).thenReturn(false);
		when(keyProp.getName()).thenReturn("key");
		when(wrapper.getProperty(keyProp)).thenReturn(wrappedProp);
	}

	@Test(expected = NullPointerException.class)
	public void requireBeanWrapper() {
		new ItemPropertyHandler(null, model);
	}

	@Test(expected = NullPointerException.class)
	public void requireModel() {
		new ItemPropertyHandler(wrapper, null);
	}

	@Test
	public void doWithPersistentProperty_key() {
		handler.doWithPersistentProperty(keyProp);
		verify(model).put("key", wrappedProp);
	}
}
