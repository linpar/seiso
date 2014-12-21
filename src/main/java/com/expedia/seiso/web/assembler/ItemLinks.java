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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.core.AbstractEntityLinks;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.meta.ItemMetaLookup;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class ItemLinks extends AbstractEntityLinks {
	private URI baseUri;

	@Autowired private Repositories repositories;
	@Autowired private ItemMetaLookup itemMetaLookup;

	public ItemLinks(@NonNull URI baseUri) {
		this.baseUri = baseUri;
	}

	@Override
	public boolean supports(@NonNull Class<?> type) {
		return repositories.hasRepositoryFor(type);
	}

	// By contract, this never return null.
	@Override
	@SuppressWarnings("unchecked")
	public LinkBuilder linkFor(@NonNull Class<?> type) {
		val itemClass = (Class<? extends Item>) type;
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		return new ItemLinkBuilder(baseUri, itemMeta);
	}

	@Override
	public LinkBuilder linkFor(@NonNull Class<?> type, Object... parameters) {
		return linkFor(type);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Link linkToCollectionResource(@NonNull Class<?> type) {
		val itemClass = (Class<? extends Item>) type;
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val rel = itemMeta.getRel();
		return linkFor(type).withRel(rel);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Link linkToSingleResource(@NonNull Class<?> type, @NonNull Object key) {
		val itemClass = (Class<? extends Item>) type;
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);

		// SDR org.springframework.data.rest.webmvc.support.RepositoryEntityLinks uses getSingleResourceRel() here.
		// FIXME For now, just temporarily affix "-single"
		return linkFor(type).slash(key).withRel(itemMeta.getRel() + "-single");
	}
	
	// Special
	public Link selfLinkForCrudRepo(@NonNull String repoKey, @NonNull String projectionName) {
		val path = new StringBuilder(repoKey).toString();

		// @formatter:off
		val uriComponents = UriComponentsBuilder
				.fromUri(baseUri)
				.path(path)
				.queryParam("view", projectionName)
				.build();
		// @formatter:on
		
		return new Link(uriComponents.toString(), Link.REL_SELF);
	}
}
