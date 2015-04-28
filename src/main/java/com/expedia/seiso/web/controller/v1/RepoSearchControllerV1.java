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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.controller.delegate.RepoSearchDelegate;
import com.expedia.serf.C;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.hypermedia.PagedResources;
import com.expedia.serf.hypermedia.Resource;
import com.expedia.serf.hypermedia.Resources;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v1")
@SuppressBasePath
public class RepoSearchControllerV1 {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private RepoSearchDelegate delegate;
	@Autowired private ResponseHeadersV1 responseHeaders;
	
	// FIXME This makes "search" a reserved word across all repo types. That's no good. [WLW]
	@RequestMapping(
			value = "/{repoKey}/search",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Resource getRepoSearchList(@PathVariable String repoKey) {
		return delegate.getRepoSearchList(ApiVersion.V1, repoKey);
	}
	
	@RequestMapping(
			value = "/{repoKey}/search/{search}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<Object> repoSearch(
			@PathVariable String repoKey,
			@PathVariable String search,
			@RequestParam(defaultValue = Projection.DEFAULT) String view,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					direction = Direction.ASC)
			Pageable pageable,
			@RequestParam MultiValueMap<String, String> params) {
		
		val result = delegate.repoSearch(ApiVersion.V1, repoKey, search, view, pageable, params);
		val resultType = result.getClass();
		if (PagedResources.class.isAssignableFrom(resultType)) {
			val pagedResources = (PagedResources) result;
			val headers = responseHeaders.buildResponseHeaders(pagedResources);
			return new HttpEntity<>(pagedResources, headers);
		} else if (Resources.class.isAssignableFrom(resultType)) {
			val resources = (Resources) result;
			return new HttpEntity<>(resources);
		} else {
			// We *could* just wrap the result with an HttpEntity and return it, but we're not expecting any such
			// responses, so safer to simply whitelist the result types. (That way we don't have to commit to supporting
			// them.) If later we have searches that return a single value or whatever, then we can handle those. [WLW]
			throw new UnsupportedOperationException(
					"Don't know how to handle repo search result of type " + resultType.getName());
		}
	}
}
