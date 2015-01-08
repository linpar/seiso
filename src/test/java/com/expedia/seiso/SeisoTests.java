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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.env.Environment;

public class SeisoTests {

	// Class under test
	@InjectMocks
	private Seiso config;

	// Dependencies
	@Mock
	private ListableBeanFactory beanFactory;
	@Mock
	private Environment env;

	@Before
	public void init() throws Exception {
		this.config = new Seiso();
		MockitoAnnotations.initMocks(this);
		initDependencies();
	}

	private void initDependencies() {
	}

	@Test
	public void dataSource() {
		// TODO
	}
}
