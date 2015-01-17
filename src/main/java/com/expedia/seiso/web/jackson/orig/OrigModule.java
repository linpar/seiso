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
package com.expedia.seiso.web.jackson.orig;

import lombok.NonNull;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Willie Wheeler
 */
@Component
@SuppressWarnings("serial")
public class OrigModule extends SimpleModule {
	
	public OrigModule(
			@NonNull OrigResourceSerializer resourceSerializer,
			@NonNull OrigResourcesSerializer resourcesSerializer,
			@NonNull OrigPagedResourcesSerializer pagedResourcesSerializer) {
		
		addSerializer(resourceSerializer);
		addSerializer(resourcesSerializer);
		addSerializer(pagedResourcesSerializer);
	}
}
