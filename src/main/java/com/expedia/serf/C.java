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
package com.expedia.serf;

import lombok.experimental.UtilityClass;

/**
 * Constants.
 * 
 * @author Willie Wheeler
 */
@UtilityClass
public class C {
	public static final int DEFAULT_PAGE_NUMBER = 0;
	public static final int DEFAULT_PAGE_SIZE = 100;

	// Error codes
	public static final String EC_HTTP_CLIENT_ERROR = "http_client_error";
	public static final String EC_INTERNAL_ERROR = "internal_error";
	
	/** Syntax is fine, but semantics wrong (e.g. array passed where we expect a single value) */
	public static final String EC_INVALID_REQUEST_BODY = "invalid_request_body";
	
	public static final String EC_NOT_IMPLEMENTED = "not_implemented";
	
	/** Request requires a request body */
	public static final String EC_REQUEST_BODY_REQUIRED = "request_body_required";
	
	public static final String EC_RESOURCE_NOT_FOUND = "resource_not_found";
	public static final String EC_VALIDATION_ERROR = "validation_error";
}
