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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.seiso.domain.entity.Dashboard;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.SeyrenCheck;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.serf.hypermedia.Link;
import com.expedia.serf.hypermedia.Relations;

/**
 * Various factory methods for creating item links. This does not include the repository search links, which
 * {@link RepoSearchLinks} handles.
 * 
 * @author Willie Wheeler
 */
@Component
@RequiredArgsConstructor
public class ItemLinks {
	private static final MultiValueMap<String, String> EMPTY_PARAMS = new LinkedMultiValueMap<>();
	
	/** Includes version prefix; e.g., /v1, /v2 */
	@NonNull private URI versionUri;
	
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
	
	/**
	 * Returns a self link to the given item.
	 * 
	 * @param item
	 *            item
	 * @return self link to the item.
	 */
	public Link itemLink(@NonNull Item item) {
		return itemLink(Relations.SELF, item);
	}
	
	public Link itemLink(@NonNull String rel, @NonNull Item item) {
		return itemLink(rel, item, EMPTY_PARAMS);
	}
	
	public Link itemLink(@NonNull String rel, @NonNull Item item, @NonNull MultiValueMap<String, String> params) {
		// @formatter:off
		val href = repoUri(item.getClass(), params)
				.pathSegment(item.itemPath())
				.build()
				.toString();
		// @formatter:on
		return new Link(rel, href);
	}
	
	public Link itemPropertyLink(@NonNull Item item, @NonNull String prop) {
		// @formatter:off
		val href = repoUri(item.getClass(), EMPTY_PARAMS)
				.pathSegment(item.itemPath())
				.pathSegment(prop)
				.build()
				.toString();
		// @formatter:on
		return new Link("s:" + prop, href);
	}
	
	
	// =================================================================================================================
	// Special item links
	// =================================================================================================================
	
	public Link serviceInstanceNodeSummaryLink(String rel, @NonNull ServiceInstance serviceInstance) {
		// @formatter:off
		val href = repoUri(ServiceInstance.class, EMPTY_PARAMS)
				.pathSegment(serviceInstance.itemPath())
				.pathSegment("node-summary")
				.build()
				.toString();
		// @formatter:on
		return new Link(rel, href);
	}
	
	public Link serviceInstanceHealthBreakdownLink(String rel, @NonNull ServiceInstance serviceInstance) {
		// @formatter:off
		val href = repoUri(ServiceInstance.class, EMPTY_PARAMS)
				.pathSegment(serviceInstance.itemPath())
				.pathSegment("health-breakdown")
				.build()
				.toString();
		// @formatter:on
		return new Link(rel, href);
	}
	
	public Link serviceInstanceRotationBreakdownLink(String rel, @NonNull ServiceInstance serviceInstance) {
		// @formatter:off
		val href = repoUri(ServiceInstance.class, EMPTY_PARAMS)
				.pathSegment(serviceInstance.itemPath())
				.pathSegment("rotation-breakdown")
				.build()
				.toString();
		// @formatter:on
		return new Link(rel, href);
	}
	
	public Link dashboardApiLink(@NonNull Dashboard dashboard) {
		val href = dashboard.getApiUri();
		return (href == null ? null : new Link(SeisoRelations.S_DASHBOARD_API, href));
	}
	
	public Link dashboardUiLink(@NonNull Dashboard dashboard) {
		val href = dashboard.getUiUri();
		return (href == null ? null : new Link(SeisoRelations.S_DASHBOARD_API, href));
	}
	
	public Link seyrenCheckApiLink(@NonNull SeyrenCheck check) {
		val source = check.getSource();
		assert (source != null);
		val href = source.getBaseUri() + "/api/checks/" + check.getSeyrenId();
		return new Link(SeisoRelations.S_SEYREN_CHECK_API, href);
	}
	
	public Link seyrenCheckUiLink(@NonNull SeyrenCheck check) {
		val source = check.getSource();
		assert (source != null);
		val href = source.getBaseUri() + "/#/checks/" + check.getSeyrenId();
		return new Link(SeisoRelations.S_SEYREN_CHECK_UI, href);
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
}
