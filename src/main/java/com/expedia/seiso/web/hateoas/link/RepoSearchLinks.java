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

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.hateoas.Link;

/**
 * @author Willie Wheeler
 */
@Component
@RequiredArgsConstructor
public class RepoSearchLinks {
	
	/** Includes version prefix; e.g., /v1, /v2 */
	@NonNull private URI versionUri;
	
	@NonNull private ItemPaths itemPaths;
	@NonNull private ItemMetaLookup itemMetaLookup;
	
	public Link repoSearchListLink(@NonNull String rel, @NonNull Class<?> itemClass) {
		// @formatter:off
		val href = UriComponentsBuilder
				.fromUri(versionUri)
				.pathSegment(repoPath(itemClass), "search")
				.build()
				.toString();
		// @formatter:on
		return new Link(rel, href);
	}
	
	public Link repoSearchLink(@NonNull String rel, @NonNull Class<?> itemClass, String path) {
		// @formatter:off
		val href = UriComponentsBuilder
				.fromUri(versionUri)
				.pathSegment(repoPath(itemClass), "search", path)
				.build()
				.toString();
		// @formatter:on
		return new Link(rel, href);
	}
	
	
	// =================================================================================================================
	// Private
	// =================================================================================================================
	
	private String repoPath(Class<?> itemClass) {
		return itemMetaLookup.getItemMeta(itemClass).getRepoKey();
	}
}
