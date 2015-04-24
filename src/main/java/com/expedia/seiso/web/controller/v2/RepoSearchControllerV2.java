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

import com.expedia.rf.hmedia.Resource;
import com.expedia.rf.util.C;
import com.expedia.rf.web.MediaTypes;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.controller.delegate.RepoSearchDelegate;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2")
@Transactional
public class RepoSearchControllerV2 {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private RepoSearchDelegate delegate;
	
	// FIXME This makes "search" a reserved word across all repo types. That's no good. [WLW]
	@RequestMapping(
			value = "/{repoKey}/search",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Resource getRepoSearchList(@PathVariable String repoKey) {
		return delegate.getRepoSearchList(ApiVersion.V2, repoKey);
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
	 * @return {@code List<Resource>} or {@code PagedResources} depending on the repo type
	 */
	@RequestMapping(
			value = "/{repoKey}/search/{search}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Object repoSearch(
			@PathVariable String repoKey,
			@PathVariable String search,
			@RequestParam(defaultValue = Projection.DEFAULT) String view,
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
			throw new UnsupportedOperationException("Repo search with unique result not yet supported");
		} else if (itemMeta.isPagingRepo()) {
			return delegate.repoSearch(ApiVersion.V2, repoKey, search, view, pageable, params);
		} else {
			throw new UnsupportedOperationException("Repo search with list results not yet supported");
		}
	}
}
