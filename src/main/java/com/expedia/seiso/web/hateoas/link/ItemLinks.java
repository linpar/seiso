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
package com.expedia.seiso.web.hateoas.link;

import java.net.URI;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.Relations;
import com.expedia.seiso.web.hateoas.Link;

/**
 * Various factory methods for creating API links.
 * 
 * @author Willie Wheeler
 */
@Component
@RequiredArgsConstructor
public class ItemLinks {
	private static final MultiValueMap<String, String> EMPTY_PARAMS = new LinkedMultiValueMap<>();
	
	/** Includes version prefix; e.g., /v1, /v2 */
	@NonNull private URI versionUri;
	
	@NonNull private ItemPaths itemPaths;
	@NonNull private ItemMetaLookup itemMetaLookup;
	
	
	// =================================================================================================================
	// Repo links
	// =================================================================================================================
	
	public Link repoLink(@NonNull Class<?> itemClass, @NonNull MultiValueMap<String, String> params) {
		return doRepoLink(Relations.SELF, itemClass, params);
	}
	
	public Link repoLink(@NonNull String rel, @NonNull Class<?> itemClass) {
		return doRepoLink(rel, itemClass, EMPTY_PARAMS);
	}
	
	public Link repoFirstLink(
			@NonNull Class<?> itemClass,
			@NonNull Page<?> page,
			@NonNull MultiValueMap<String, String> params) {
		
		val newParams = new LinkedMultiValueMap<String, String>(params);
		newParams.set("page", "0");
		return doRepoLink(Relations.FIRST, itemClass, newParams);
	}
	
	public Link repoPrevLink(
			@NonNull Class<?> itemClass,
			@NonNull Page<?> page,
			@NonNull MultiValueMap<String, String> params) {
		
		val newParams = new LinkedMultiValueMap<String, String>(params);
		newParams.set("page", String.valueOf(page.getNumber() - 1));
		return doRepoLink(Relations.PREVIOUS, itemClass, newParams);
	}
	
	public Link repoNextLink(
			@NonNull Class<?> itemClass,
			@NonNull Page<?> page,
			@NonNull MultiValueMap<String, String> params) {
		
		val newParams = new LinkedMultiValueMap<String, String>(params);
		newParams.set("page", String.valueOf(page.getNumber() + 1));
		return doRepoLink(Relations.NEXT, itemClass, newParams);
	}
	
	public Link repoLastLink(
			@NonNull Class<?> itemClass,
			@NonNull Page<?> page,
			@NonNull MultiValueMap<String, String> params) {
		
		val newParams = new LinkedMultiValueMap<String, String>(params);
		newParams.set("page", String.valueOf(page.getTotalPages() - 1));
		return doRepoLink(Relations.LAST, itemClass, newParams);
	}

	
	// =================================================================================================================
	// Item links
	// =================================================================================================================
	
	public Link itemLink(@NonNull Item item) {
		return itemLink(Relations.SELF, item);
	}
	
	public Link itemLink(@NonNull String rel, @NonNull Item item) {
		return itemLink(rel, item, EMPTY_PARAMS);
	}
	
	public Link itemLink(@NonNull String rel, @NonNull Item item, @NonNull MultiValueMap<String, String> params) {
		// @formatter:off
		val href = repoUri(item.getClass(), params)
				.pathSegment(itemPathSegment(item))
				.build()
				.toString();
		// @formatter:on
		return new Link(rel, href);
	}
	
	public Link itemPropertyLink(@NonNull Item item, @NonNull String prop) {
		// @formatter:off
		val href = repoUri(item.getClass(), EMPTY_PARAMS)
				.pathSegment(itemPathSegment(item))
				.pathSegment(prop)
				.build()
				.toString();
		// @formatter:on
		return new Link("s:" + prop, href);
	}
	
	
	// =================================================================================================================
	// Private
	// =================================================================================================================
	
	private Link doRepoLink(String rel, Class<?> itemClass, MultiValueMap<String, String> params) {
		// @formatter:off
		val href = repoUri(itemClass, params)
				.pathSegment(repoPath(itemClass))
				.build()
				.toString();
		// @formatter:on
		return new Link(rel, href);
	}
	
	private UriComponentsBuilder repoUri(Class<?> itemClass, MultiValueMap<String, String> params) {
		// @formatter:off
		return UriComponentsBuilder
				.fromUri(versionUri)
				.queryParams(params);
		// @formatter:on
	}
	
	private String repoPath(Class<?> itemClass) {
		return itemMetaLookup.getItemMeta(itemClass).getRepoKey();
	}
	
	private String[] itemPathSegment(Item item) {
		return itemPaths.resolve(item);
	}
}
