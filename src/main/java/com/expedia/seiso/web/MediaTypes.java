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

import org.springframework.http.MediaType;

/**
 * @author Willie Wheeler
 */
public class MediaTypes {
	public static final String APPLICATION_HAL_JSON_VALUE = "application/hal+json";
	public static final String TEXT_URI_LIST_VALUE = "text/uri-list";
	
	public static final MediaType APPLICATION_HAL_JSON = new MediaType("application", "hal+json");
	public static final MediaType TEXT_URI_LIST = new MediaType("text", "uri-list");
}
