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
package com.expedia.seiso.web.controller.v2;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.PEResource;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.serf.C;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.hypermedia.PagedResources;
import com.expedia.serf.hypermedia.Resource;
import com.expedia.serf.hypermedia.Resources;
import com.expedia.serf.web.MediaTypes;

// http://stackoverflow.com/questions/2810652/how-to-design-a-restful-collection-resource
// http://stackoverflow.com/questions/306271/bulk-collection-manipulation-through-a-rest-restful-api

/**
 * Thin wrapper around the {@link BasicItemDelegate} to handle v2 API requests.
 * 
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2")
@SuppressBasePath
@Transactional
@XSlf4j
public class ItemControllerV2 {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private BasicItemDelegate delegate;
	
	/**
	 * @param repoKey
	 *            repository key
	 * @param view
	 *            view key
	 * @param pageable
	 *            Page request params, if relevant. Don't need these if the repo isn't a paging repo, or if the search
	 *            in question returns a single search result.
	 * @param params
	 *            all HTTP params
	 * @return {@link Resources} or {@link PagedResources} depending on the repo type
	 */
	@RequestMapping(
			value = "/{repoKey}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Object getAll(
			@PathVariable String repoKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					direction = Direction.ASC)
			Pageable pageable,
			@RequestParam MultiValueMap<String, String> params) {
		
		return delegate.getAll(ApiVersion.V2, repoKey, view, pageable, params);
	}
	
	@RequestMapping(
			value = "/{repoKey}/{itemKey}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Resource getOne(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view) {
		
		return delegate.getOne(ApiVersion.V2, repoKey, itemKey, view);
	}
	
//	@RequestMapping(
//			value = "/{repoKey}",
//			method = RequestMethod.POST,
//			params = "mode=batch",
//			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE,
//			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
//	@Transactional(propagation = Propagation.NEVER)
//	public SaveAllResult postAll(@PathVariable String repoKey, PEResources peResources) {
//		log.trace("Batch saving {} items: repoKey={}", peResources.size(), repoKey);
//		val itemClass = itemMetaLookup.getItemClass(repoKey);
//		return delegate.postAll(itemClass, peResources, true);
//	}
	
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
			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void put(@PathVariable String repoKey, @PathVariable String itemKey, PEResource peResource) {
		log.trace("Putting /{}/{}", repoKey, itemKey);
		delegate.put(peResource.getItem(), true);
	}
	
	/**
	 * Deletes the specified item.
	 * 
	 * @param repoKey
	 *            repository key
	 * @param itemKey
	 *            item key
	 */
	@RequestMapping(value = "/{repoKey}/{itemKey}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SuppressWarnings("rawtypes")
	public void delete(@PathVariable String repoKey, @PathVariable String itemKey) {
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		delegate.delete(new SimpleItemKey(itemClass, itemKey));
	}
}
