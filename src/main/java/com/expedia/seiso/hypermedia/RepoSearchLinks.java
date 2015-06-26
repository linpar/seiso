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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.serf.hypermedia.Link;

/**
 * @author Willie Wheeler
 */
@Component
@RequiredArgsConstructor
public class RepoSearchLinks {
	private static final String BASE_SEARCH_PATH = "search";
	
	/** Includes version prefix; e.g., /v1, /v2 */
	@NonNull private URI versionUri;
	
	@NonNull private ItemMetaLookup itemMetaLookup;
	
	public Link repoSearchListLink(@NonNull String rel, @NonNull Class<?> itemClass) {
		// @formatter:off
		val href = UriComponentsBuilder
				.fromUri(versionUri)
				.pathSegment(repoPath(itemClass), BASE_SEARCH_PATH)
				.build()
				.toString();
		// @formatter:on
		return new Link(rel, href);
	}
	
	public Link toRepoSearchLinkTemplate(
			@NonNull String rel,
			@NonNull Class itemClass,
			@NonNull String path,
			@NonNull MultiValueMap<String, String> params) {
		
		val hrefBuilder = new StringBuilder();
		
		// @formatter:off
		hrefBuilder.append(versionUri)
				.append("/").append(repoPath(itemClass))
				.append("/").append(BASE_SEARCH_PATH)
				.append("/").append(path);
		// @formatter:off
		
		val queryString = toQueryString(params);
		if (!queryString.isEmpty()) {
			hrefBuilder.append("?");
			hrefBuilder.append(queryString);
		}
				
		val link = new Link(rel, hrefBuilder.toString());
		link.setTemplated(true);
		return link;
	}
	
	public PaginationLinkBuilder toPaginationLinkBuilder(
			@NonNull Page page,
			@NonNull Class itemClass,
			@NonNull String path,
			@NonNull MultiValueMap<String, String> params) {
		
		// @formatter:off
		val uriComponents = UriComponentsBuilder
				.fromUri(versionUri)
				.pathSegment(repoPath(itemClass), BASE_SEARCH_PATH, path)
				.build();
		// @formatter:on
		
		return new PaginationLinkBuilder(page, uriComponents, params);
	}
	
	private String repoPath(Class<?> itemClass) {
		return itemMetaLookup.getItemMeta(itemClass).getRepoKey();
	}
	
	private String toQueryString(MultiValueMap<String, String> params) {
		val paramNames = new ArrayList<String>(params.keySet());
		Collections.sort(paramNames);
		
		val builder = new StringBuilder();
		boolean first = true;
		for (val paramName : paramNames) {
			if (!first) { builder.append("&"); }
			builder.append(paramName);
			builder.append("=");
			
			// FIXME The responsibility for formatting template variables belongs here, not with the clients. [WLW]
			builder.append(params.getFirst(paramName));
			
			first = false;
		}
		
		return builder.toString();
	}
}
