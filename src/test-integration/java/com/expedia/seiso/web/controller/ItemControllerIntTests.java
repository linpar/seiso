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
package com.expedia.seiso.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.expedia.seiso.Seiso;

// See http://www.jayway.com/2014/07/04/integration-testing-a-spring-boot-application/

// TODO This is brittle right now because we're not managing the data as test data. Probably want to set up a
// separate integration test database and then seed it with controlled test data. [WLW]

/**
 * @author Willie Wheeler
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = Seiso.class)
//@WebAppConfiguration
//@IntegrationTest("server.port:0")
public class ItemControllerIntTests {
	
	@Test
	public void dummy() {
	}
}
