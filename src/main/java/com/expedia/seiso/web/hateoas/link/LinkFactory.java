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

import lombok.Getter;
import lombok.NonNull;

import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.meta.ItemMetaLookup;

/**
 * Creates {@link ItemLinks} and {@link RepoSearchLinks} instances and makes them available for use.
 * 
 * @author Willie Wheeler
 */
@Component
public class LinkFactory {
	@Getter private ItemLinks itemLinks;
	@Getter private RepoSearchLinks repoSearchLinks;
	
	/**
	 * @param versionUri
	 *            URI path up to and including the API version.
	 * @param itemPaths
	 *            Maps items to their URI paths.
	 * @param itemMetaLookup
	 *            Item metadata lookup.
	 */
	public LinkFactory(@NonNull URI versionUri, @NonNull ItemPaths itemPaths, @NonNull ItemMetaLookup itemMetaLookup) {
		this.itemLinks = new ItemLinks(versionUri, itemPaths, itemMetaLookup);
		this.repoSearchLinks = new RepoSearchLinks(versionUri, itemPaths, itemMetaLookup);
	}
}
