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

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.util.C;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.SaveAllResponse;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.seiso.web.hateoas.PEResource;
import com.expedia.seiso.web.hateoas.PEResources;
import com.expedia.seiso.web.hateoas.PagedResources;
import com.expedia.seiso.web.hateoas.Resource;
import com.expedia.seiso.web.hateoas.Resources;

/**
 * Thin wrapper around the {@link BasicItemDelegate} to handle v1 API requests.
 * 
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v1")
@Transactional
@XSlf4j
public class ItemControllerV1 {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private BasicItemDelegate delegate;
	@Autowired private ResponseHeadersV1 responseHeaders;
	
	/**
	 * @param repoKey
	 * @param view
	 * @param pageable
	 * @param params
	 * @return either {@link Resources} or {@link PagedResources} depending on the repo type
	 */
	@RequestMapping(
			value = "/{repoKey}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<?> getAll(
			@PathVariable String repoKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					direction = Direction.ASC)
			Pageable pageable,
			@RequestParam MultiValueMap<String, String> params) {
		
		log.trace("Getting all items: repoKey={}", repoKey);
		val result = delegate.getAll(ApiVersion.V1, repoKey, view, pageable, params);
		val resultClass = result.getClass();
		
		if (PagedResources.class.isAssignableFrom(resultClass)) {
			val baseResourcePage = (PagedResources) result;
			val headers = responseHeaders.buildResponseHeaders(baseResourcePage);
			return new HttpEntity<PagedResources>(baseResourcePage, headers);
		} else if (Resources.class.isAssignableFrom(resultClass)) {
			return new HttpEntity<Resources>((Resources) result);
		} else {
			throw new RuntimeException("Unknown result type: " + resultClass);
		}
	}
	
	@RequestMapping(
			value = "/{repoKey}/{itemKey}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Resource getOne(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view) {
		
		return delegate.getOne(ApiVersion.V1, repoKey, itemKey, view);
	}
	
	@RequestMapping(
			value = "/{repoKey}/{itemKey}/{propKey}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getProperty(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@PathVariable String propKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view) {
		
		return delegate.getProperty(ApiVersion.V1, repoKey, itemKey, propKey, view);
	}
	
	@RequestMapping(
			value = "/{repoKey}",
			method = RequestMethod.POST,
			params = "mode=batch",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(propagation = Propagation.NEVER)
	public SaveAllResponse postAll(@PathVariable String repoKey, PEResources peResourceList) {
		log.trace("Batch saving {} items: repoKey={}", peResourceList.size(), repoKey);
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		return delegate.postAll(itemClass, peResourceList, true);
	}

	/**
	 * Handles HTTP PUT requests against top-level resources. Following HTTP semantics, this creates the resource if it
	 * doesn't already exist; otherwise, it completely replaces the existing resource (as opposed to merging it). With
	 * respect to the latter, null and missing fields in the incoming representation map to null fields in the
	 * persistent resource.
	 * 
	 * @param repoKey
	 *            Repository key
	 * @param itemKey
	 *            Item key
	 * @param peResource
	 *            Item to save
	 */
	@RequestMapping(
			value = "/{repoKey}/{itemKey}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void put(@PathVariable String repoKey, @PathVariable String itemKey, PEResource peResource) {
		delegate.put(peResource.getItem(), true);
	}

	@RequestMapping(value = "/{repoKey}/{itemKey}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SuppressWarnings("rawtypes")
	public void delete(@PathVariable String repoKey, @PathVariable String itemKey) {
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		delegate.delete(new SimpleItemKey(itemClass, itemKey));
	}
}
