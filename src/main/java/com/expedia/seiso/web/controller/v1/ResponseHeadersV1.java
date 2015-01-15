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
package com.expedia.seiso.web.controller.v1;


import lombok.NonNull;
import lombok.val;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.expedia.seiso.web.Relations;
import com.expedia.seiso.web.hateoas.PagedResources;

/**
 * @author Willie Wheeler
 */
@Component
public class ResponseHeadersV1 {
	static final String X_PAGINATION_FIRST = "X-Pagination-First";
	static final String X_PAGINATION_PREV = "X-Pagination-Prev";
	static final String X_PAGINATION_NEXT = "X-Pagination-Next";
	static final String X_PAGINATION_LAST = "X-Pagination-Last";
	static final String X_PAGINATION_TOTAL_ELEMENTS = "X-Pagination-TotalElements";
	static final String X_PAGINATION_TOTAL_PAGES = "X-Pagination-TotalPages";
	
	public HttpHeaders buildResponseHeaders(@NonNull PagedResources page) {
		val links = page.getLinks();
		val meta = page.getMetadata();
		
		val headers = new HttpHeaders();
		for (val link : links) {
			val rel = link.getRel();
			val href = link.getHref();
			if (Relations.FIRST.equals(rel)) {
				headers.add(X_PAGINATION_FIRST, href);
			} else if (Relations.PREVIOUS.equals(rel)) {
				headers.add(X_PAGINATION_PREV, href);
			} else if (Relations.NEXT.equals(rel)) {
				headers.add(X_PAGINATION_NEXT, href);
			} else if (Relations.LAST.equals(rel)) {
				headers.add(X_PAGINATION_LAST, href);
			}
		}
		headers.add(X_PAGINATION_TOTAL_ELEMENTS, String.valueOf(meta.getTotalItems()));
		headers.add(X_PAGINATION_TOTAL_PAGES, String.valueOf(meta.getTotalPages()));
		return headers;
	}
}
