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
package com.expedia.seiso.web.hmedia;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.data.domain.Page;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.rf.hmedia.Link;
import com.expedia.rf.hmedia.Relations;

/**
 * Builder for pagination links (self, first, previous, next, last). This is for paging resources generally, not for any
 * particular kind of paging resource (e.g. repo search). So we don't include the "up" relation here, since that would
 * require knowledge of the parent resource.
 * 
 * @author Willie Wheeler
 */
@RequiredArgsConstructor
public class PaginationLinkBuilder {
	@NonNull private Page page;
	@NonNull private UriComponents uriComponents;
	@NonNull private MultiValueMap<String, String> params;
	
	public Link buildSelfLink() {
		// @formatter:off
		val href = uriComponentsBuilder()
				.queryParams(params)
				.build()
				.toString();
		// @formatter:on
		return new Link(Relations.SELF, href);
	}
	
	public Link buildFirstLink() {
		val totalPages = page.getTotalPages();
		val firstPage = 0;
		
		if (totalPages <= 0) { return null; }
		
		// @formatter:off
		val href = uriComponentsBuilder()
				.queryParams(paramsWithReplacementPageNumber(firstPage))
				.build()
				.toString();
		// @formatter:on
		return new Link(Relations.FIRST, href);
	}
	
	public Link buildPreviousLink() {
		val totalPages = page.getTotalPages();
		val firstPage = 0;
		val currentPage = page.getNumber();
		
		if (totalPages <= 0 || currentPage <= firstPage) { return null; }
		
		// @formatter:off
		val href = uriComponentsBuilder()
				.queryParams(paramsWithReplacementPageNumber(currentPage - 1))
				.build()
				.toString();
		// @formatter:on
		
		return new Link(Relations.PREVIOUS, href);
	}
	
	public Link buildNextLink() {
		val totalPages = page.getTotalPages();
		val lastPage = totalPages - 1;
		val currentPage = page.getNumber();
		
		if (totalPages <= 0 || currentPage >= lastPage) { return null; }
		
		// @formatter:off
		val href = uriComponentsBuilder()
				.queryParams(paramsWithReplacementPageNumber(currentPage + 1))
				.build()
				.toString();
		// @formatter:on
		
		return new Link(Relations.NEXT, href);
	}
	
	public Link buildLastLink() {
		val totalPages = page.getTotalPages();
		val lastPage = totalPages - 1;
		
		if (totalPages <= 0) { return null; }
			
		// @formatter:off
		val href = uriComponentsBuilder()
				.queryParams(paramsWithReplacementPageNumber(lastPage))
				.build()
				.toString();
		// @formatter:on
		
		return new Link(Relations.LAST, href);
	}
	
	private UriComponentsBuilder uriComponentsBuilder() {
		return UriComponentsBuilder.fromUri(uriComponents.toUri());
	}
	
	private MultiValueMap<String, String> paramsWithReplacementPageNumber(int pageNumber) {
		val newParams = new LinkedMultiValueMap<String, String>(params);
		newParams.set("page", String.valueOf(pageNumber));
		return newParams;
	}
}
