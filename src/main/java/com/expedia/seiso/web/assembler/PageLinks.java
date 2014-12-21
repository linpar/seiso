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

import java.net.URI;

import lombok.NonNull;
import lombok.val;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.seiso.core.util.PageMetadataUtils;

/**
 * Produces page links to include in response headers per
 * <a href="http://seiso.io/docs/current/api-reference/pagination/">http://seiso.io/docs/current/api-reference/pagination/</a>.
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class PageLinks {
	private URI baseUri;

	public PageLinks(@NonNull URI baseUri) {
		this.baseUri = baseUri;
	}
	
	public Link selfLink(String repoKey, String projectionName, PageMetadata pageMeta) {
		return link(repoKey, projectionName, pageMeta, pageMeta.getNumber(), Link.REL_SELF);
	}
	
	public Link firstLink(String repoKey, String projectionName, PageMetadata pageMeta) {
		val firstPage = 0;
		return link(repoKey, projectionName, pageMeta, firstPage, Link.REL_FIRST);
	}

	public Link prevLink(String repoKey, String projectionName, PageMetadata pageMeta) {
		val number = pageMeta.getNumber();
		val firstPage = 0;
		return (number <= firstPage ? null : link(repoKey, projectionName, pageMeta, number - 1, Link.REL_PREVIOUS));
	}

	public Link nextLink(String repoKey, String projectionName, PageMetadata pageMeta) {
		val number = pageMeta.getNumber();
		val totalPages = PageMetadataUtils.getCorrectedTotalPages(pageMeta);
		val lastPage = totalPages - 1;
		return (number >= lastPage ? null : link(repoKey, projectionName, pageMeta, number + 1, Link.REL_NEXT));
	}

	public Link lastLink(String repoKey, String projectionName, PageMetadata pageMeta) {
		val totalPages = PageMetadataUtils.getCorrectedTotalPages(pageMeta);
		val lastPage = totalPages - 1;
		return link(repoKey, projectionName, pageMeta, lastPage, Link.REL_LAST);
	}
	
	private Link link(String repoKey, String projectionName, PageMetadata pageMeta, long number, String rel) {
		
		// FIXME Need to handle sorting too, I think.
		val path = new StringBuilder(repoKey).toString();

		// @formatter:off
		val uriComponents = UriComponentsBuilder
				.fromUri(baseUri)
				.path(path)
				.queryParam("view", projectionName)
				.queryParam("page", number)
				.queryParam("size", pageMeta.getSize())
				.build();
		// @formatter:on

		return new Link(uriComponents.toString(), rel);
	}
}
