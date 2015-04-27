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
package com.expedia.seiso.hypermedia;

/**
 * @author Willie Wheeler
 */
public class SeisoRelations {
	public static final String S_DASHBOARD_API = "s:dashboard-api";
	public static final String S_DASHBOARD_UI = "s:dashboard-ui";
	
	// IANA has a standard link relationship for search, but it's for OpenSearch. So we namespace it here.
	// http://www.iana.org/assignments/link-relations/link-relations.xhtml
	public static final String S_SEARCH = "s:search";
	
	public static final String S_SEYREN_CHECK_API = "s:seyren-check-api";
	public static final String S_SEYREN_CHECK_UI = "s:seyren-check-ui";
}
