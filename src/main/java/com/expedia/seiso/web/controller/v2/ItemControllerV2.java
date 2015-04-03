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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.util.C;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.MediaTypes;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.seiso.web.hateoas.PEResource;
import com.expedia.seiso.web.hateoas.PagedResources;
import com.expedia.seiso.web.hateoas.Resource;
import com.expedia.seiso.web.hateoas.Resources;

// TODO Support patching. The reason is that due to the recursive relationship between people and their managers, we
// want to be able to update people in two passes:
// 1) Create all people (need to do this first so we can resolve managers in pass #2)
// 2) Link to managers
// When updating people in pass #1, we don't want to null out their managers.

// TODO Support batch linking of people to managers. But require pagination.

// TODO Support batch deleting of people. But require pagination.

// http://stackoverflow.com/questions/2810652/how-to-design-a-restful-collection-resource
// http://stackoverflow.com/questions/306271/bulk-collection-manipulation-through-a-rest-restful-api

/**
 * Thin wrapper around the {@link BasicItemDelegate} to handle v2 API requests.
 * 
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2")
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
	
	/**
	 * Returns the given property. This can be single- or collection-valued.
	 * 
	 * @param repoKey
	 * @param itemKey
	 * @param propKey
	 * @param view
	 * @return
	 */
	@RequestMapping(
			value = "/{repoKey}/{itemKey}/{propKey}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Object getProperty(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@PathVariable String propKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view) {
		
		return delegate.getProperty(ApiVersion.V2, repoKey, itemKey, propKey, view);
	}
	
//	@RequestMapping(
//			value = "/{repoKey}",
//			method = RequestMethod.POST,
//			params = "mode=batch",
//			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE,
//			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
//	@Transactional(propagation = Propagation.NEVER)
//	public SaveAllResponse postAll(@PathVariable String repoKey, PEResources peResources) {
//		log.trace("Batch saving {} items: repoKey={}", peResources.size(), repoKey);
//		val itemClass = itemMetaLookup.getItemClass(repoKey);
//		return delegate.postAll(itemClass, peResources, true);
//	}
	
	@RequestMapping(
			value = "/{repoKey}/{itemKey}/{propKey}",
			method = RequestMethod.POST,
			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	// TODO Location header
	public void postCollectionPropertyElement(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@PathVariable String propKey,
			PEResource peResource) {
		
		delegate.postCollectionPropertyElement(ApiVersion.V2, repoKey, itemKey, propKey, peResource);
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
			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void put(@PathVariable String repoKey, @PathVariable String itemKey, PEResource peResource) {
		// mergeAssociations = false because v2 treats associations as separate resources, and merging associations
		// would null out the current item's associations.
		delegate.put(peResource.getItem(), false);
	}
	
	/**
	 * Assigns an item to a given property.
	 * 
	 * @param repoKey
	 *            Repository key
	 * @param itemKey
	 *            Item key
	 * @param propKey
	 *            Property key
	 * @param propItemKey
	 *            Key for the item to assign to the property
	 */
	@RequestMapping(
			value = "/{repoKey}/{itemKey}/{propKey}",
			method = RequestMethod.PUT,
			consumes = MediaTypes.TEXT_URI_LIST_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void putProperty(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@PathVariable String propKey,
			@RequestBody(required = false) ItemKey propItemKey) {
		
		delegate.putProperty(repoKey, itemKey, propKey, propItemKey);
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
	
	@RequestMapping(
			value = "/{repoKey}/{itemKey}/{propKey}/{elemId}",
			method = RequestMethod.DELETE)
	public void deleteCollectionPropertyElement(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@PathVariable String propKey,
			@PathVariable Long elemId) {
		
		delegate.deleteCollectionPropertyElement(ApiVersion.V2, repoKey, itemKey, propKey, elemId);
	}
}
