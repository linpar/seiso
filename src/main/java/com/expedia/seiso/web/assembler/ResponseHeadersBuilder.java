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

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.ResponseHeaders;

/**
 * <p>
 * Centralizes the logic for building response headers.
 * </p>
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Component
public class ResponseHeadersBuilder {
	@Autowired private ItemMetaLookup itemMetaLookup;
	
	// Inject using specific class here rather than EntityLinks, since we need to call special methods. 
	@Autowired private ItemLinks itemLinks;
	
	@Autowired private PageLinks pageLinks;
	
	/**
	 * Builds the HTTP response headers for a non-paging repository.
	 * 
	 * @param repoKey
	 *            repo key
	 * @param projectionName
	 *            projection name
	 * @param pageMeta
	 *            page metadata
	 * @return HTTP response headers
	 */
	public HttpHeaders buildForCrudRepo(@NonNull String repoKey, @NonNull String projectionName) {
		val headers = new HttpHeaders();
		val selfLink = itemLinks.selfLinkForCrudRepo(repoKey, projectionName);
		headers.add(ResponseHeaders.LINK, selfLink.toString());
		headers.add(ResponseHeaders.X_SELF, selfLink.getHref());
		return headers;
	}
	
	/**
	 * Builds the HTTP response headers for a paging repository.
	 * 
	 * @param repoKey
	 *            repo key
	 * @param projectionName
	 *            projection name
	 * @param pageMeta
	 *            page metadata
	 * @return HTTP response headers
	 */
	public HttpHeaders buildForPagingAndSortingRepo(
			@NonNull String repoKey,
			@NonNull String projectionName,
			@NonNull PageMetadata pageMeta) {		
		
		// PagedResources has getPreviousLink() and getNextLink(). But it doesn't have first/last, which we need.
		val selfLink = pageLinks.selfLink(repoKey, projectionName, pageMeta);
		val firstLink = pageLinks.firstLink(repoKey, projectionName, pageMeta);
		val prevLink = pageLinks.prevLink(repoKey, projectionName, pageMeta);
		val nextLink = pageLinks.nextLink(repoKey, projectionName, pageMeta);
		val lastLink = pageLinks.lastLink(repoKey, projectionName, pageMeta);
		val totalElems = pageMeta.getTotalElements();
		val totalPages = pageMeta.getTotalPages();
		
		val headers = new HttpHeaders();
		
		// RFC 5988: http://tools.ietf.org/html/rfc5988
		val links = new ArrayList<Link>();
		links.add(selfLink);
		links.add(firstLink);
		if (prevLink != null) { links.add(prevLink); }
		if (nextLink != null) { links.add(nextLink); }
		links.add(lastLink);
		headers.add(ResponseHeaders.LINK, toString(links));
		
		// Extension headers
		headers.add(ResponseHeaders.X_SELF, selfLink.getHref());
		headers.add(ResponseHeaders.X_PAGINATION_FIRST, firstLink.getHref());
		if (prevLink != null) {
			headers.add(ResponseHeaders.X_PAGINATION_PREV, prevLink.getHref());
		}
		if (nextLink != null) {
			headers.add(ResponseHeaders.X_PAGINATION_NEXT, nextLink.getHref());
		}
		headers.add(ResponseHeaders.X_PAGINATION_LAST, lastLink.getHref());
		headers.add(ResponseHeaders.X_PAGINATION_TOTAL_ELEMENTS, String.valueOf(totalElems));
		headers.add(ResponseHeaders.X_PAGINATION_TOTAL_PAGES, String.valueOf(totalPages));
		
		return headers;
	}
	
	private String toString(List<Link> links) {
		// RFC 5988 expects multiple links to appear a certain way, which doesn't exactly match what calling
		// links.toString() generates. So we build the string here.
		val builder = new StringBuilder();
		int numLinks = links.size();
		for (int i = 0; i < numLinks; i++) {
			if (i > 0) { builder.append(","); }
			builder.append(links.get(i).toString());
		}
		return builder.toString();
	}
}
