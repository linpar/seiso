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
package com.expedia.rf.web.controller;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.repository.support.Repositories;

import com.expedia.serf.web.controller.EntryController;

/**
 * @author Willie Wheeler
 */
public class EntryControllerTests {
	
	// Class under test
	@InjectMocks private EntryController controller;
	
	// Dependencies
	@Mock private Repositories repositories;
	
	@Before
	public void setUp() throws Exception {
		this.controller = new EntryController();
		MockitoAnnotations.initMocks(this);
	}
}
