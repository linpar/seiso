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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.util.C;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.MediaTypes;
import com.expedia.seiso.web.controller.BasicItemDelegate;
import com.expedia.seiso.web.controller.ItemSearchDelegate;
import com.expedia.seiso.web.hateoas.BaseResource;

// TODO Support patching. The reason is that due to the recursive relationship between people and their managers, we
// want to be able to update people in two passes:
// 1) Create all people (need to do this first so we can resolve managers in pass #2)
// 2) Link to managers
// When updating people in pass #1, we don't want to null out their managers.

// TODO Support batch linking of people to managers. But require pagination.

// TODO Support batch deleting of people. But require pagination.

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2")
@Transactional
public class ItemControllerV2 {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private BasicItemDelegate basicItemDelegate;
	@Autowired private ItemSearchDelegate itemSearchDelegate;
	
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
	 * @return {@code List<BaseResource>} or {@code BaseResourcePage} depending on the repo type
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
		
		return basicItemDelegate.getAll(repoKey, view, pageable, params);
	}
	
	@RequestMapping(
			value = "/{repoKey}/{itemKey}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public BaseResource getOne(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view) {
		
		return basicItemDelegate.getOne(repoKey, itemKey, view);
	}
	
	@RequestMapping(
			value = "/{repoKey}/{itemKey}/{propKey}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Object getProperty(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@PathVariable String propKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view) {
		
		return basicItemDelegate.getProperty(repoKey, itemKey, propKey, view);
	}
	
	// FIXME This makes "search" a reserved word across all repo types. That's no good. [WLW]
	@RequestMapping(
			value = "/{repoKey}/search",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public BaseResource getRepoSearchList(@PathVariable String repoKey) {
		return itemSearchDelegate.getRepoSearchList(repoKey);
	}
	
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
	 * @return {@code List<BaseResource>} or {@code BaseResourcePage} depending on the repo type
	 */
	@RequestMapping(
			value = "/{repoKey}/search/{search}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Object repoSearch(
			@PathVariable String repoKey,
			@PathVariable String search,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					direction = Direction.ASC)
			Pageable pageable,
			@RequestParam MultiValueMap<String, String> params) {
		
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val searchMethod = itemMeta.getRepositorySearchMethod(search);
		val resultType = searchMethod.getReturnType();
		
		if (Item.class.isAssignableFrom(resultType)) {
			return itemSearchDelegate.repoSearchUnique(repoKey, search);
		} else if (itemMeta.isPagingRepo()) {
			return itemSearchDelegate.repoSearch(repoKey, search, pageable, params);
		} else {
			return itemSearchDelegate.repoSearch(repoKey, search);
		}
	}
}
