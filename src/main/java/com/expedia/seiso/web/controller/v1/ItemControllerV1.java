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

import java.util.List;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
import com.expedia.seiso.web.Relations;
import com.expedia.seiso.web.ResponseHeadersV1;
import com.expedia.seiso.web.controller.BasicItemDelegate;
import com.expedia.seiso.web.controller.ItemSearchDelegate;
import com.expedia.seiso.web.controller.PEResource;
import com.expedia.seiso.web.controller.PEResourceList;
import com.expedia.seiso.web.hateoas.BaseResource;
import com.expedia.seiso.web.hateoas.BaseResourcePage;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v1")
@Transactional
@XSlf4j
public class ItemControllerV1 {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private BasicItemDelegate basicItemDelegate;
	@Autowired private ItemSearchDelegate itemSearchDelegate;
	
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
		
		val result = basicItemDelegate.getAll(repoKey, view, pageable, params);
		
		if (BaseResourcePage.class.isAssignableFrom(result.getClass())) {
			val baseResourcePage = (BaseResourcePage) result;
			val headers = buildResponseHeaders(baseResourcePage);
			return new HttpEntity<BaseResourcePage>(baseResourcePage, headers);
		} else {
			val baseResourceList = (List<BaseResource>) result;
			return new HttpEntity<List<BaseResource>>(baseResourceList);
		}
	}
	
	@RequestMapping(
			value = "/{repoKey}/{itemKey}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public BaseResource getOne(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view) {
		
		return basicItemDelegate.getOne(repoKey, itemKey, view);
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
		
		return basicItemDelegate.getProperty(repoKey, itemKey, propKey, view);
	}
	
	// FIXME This makes "search" a reserved word across all repo types. That's no good. [WLW]
	@RequestMapping(
			value = "/{repoKey}/search",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public BaseResource getSearchList(@PathVariable String repoKey) {
		return itemSearchDelegate.getRepoSearchList(repoKey);
	}
	
	@RequestMapping(
			value = "/{repoKey}/search/{search}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<BaseResourcePage> search(
			@PathVariable String repoKey,
			@PathVariable String search,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					direction = Direction.ASC)
			Pageable pageable,
			@RequestParam MultiValueMap<String, String> params) {
		
		// Is it correct that v1 always returns a page here? [WLW]
		val baseResourcePage = itemSearchDelegate.repoSearch(repoKey, search, pageable, params);
		val headers = buildResponseHeaders(baseResourcePage);
		return new HttpEntity<BaseResourcePage>(baseResourcePage, headers);
	}
	
	@RequestMapping(
			value = "/{repoKey}",
			method = RequestMethod.POST,
			params = "mode=batch",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(propagation = Propagation.NEVER)
	public SaveAllResponse postAll(@PathVariable String repoKey, PEResourceList peResourceList) {
		log.trace("Batch saving {} items: repoKey={}", peResourceList.size(), repoKey);
		return basicItemDelegate.postAll(peResourceList);
	}

	/**
	 * Handles HTTP PUT requests against top-level resources. Following HTTP semantics, this creates the resource if it
	 * doesn't already exist; otherwise, it completely replaces the existing resource (as opposed to merging it). With
	 * respect to the latter, null and missing fields in the incoming representation map to null fields in the
	 * persistent resource.
	 * 
	 * @param itemDto
	 *            item to save
	 */
	@RequestMapping(
			value = "/{repoKey}/{itemKey}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void put(@PathVariable String repoKey, @PathVariable String itemKey, PEResource itemDto) {
		log.trace("Putting item: repoKey={}, itemKey={}", repoKey, itemKey);
		basicItemDelegate.put(itemDto.getItem());
	}

	@RequestMapping(value = "/{repoKey}/{itemKey}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SuppressWarnings("rawtypes")
	public void delete(@PathVariable String repoKey, @PathVariable String itemKey) {
		log.info("Deleting item: /{}/{}", repoKey, itemKey);
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		basicItemDelegate.delete(new SimpleItemKey(itemClass, itemKey));
	}
	
	private HttpHeaders buildResponseHeaders(BaseResourcePage page) {
		val links = page.getLinks();
		val meta = page.getMetadata();
		
		val headers = new HttpHeaders();
		for (val link : links) {
			val rel = link.getRel();
			val href = link.getHref();
			if (Relations.FIRST.equals(rel)) {
				headers.add(ResponseHeadersV1.X_PAGINATION_FIRST, href);
			} else if (Relations.PREVIOUS.equals(rel)) {
				headers.add(ResponseHeadersV1.X_PAGINATION_PREV, href);
			} else if (Relations.NEXT.equals(rel)) {
				headers.add(ResponseHeadersV1.X_PAGINATION_NEXT, href);
			} else if (Relations.LAST.equals(rel)) {
				headers.add(ResponseHeadersV1.X_PAGINATION_LAST, href);
			}
		}
		headers.add(ResponseHeadersV1.X_PAGINATION_TOTAL_ELEMENTS, String.valueOf(meta.getTotalItems()));
		headers.add(ResponseHeadersV1.X_PAGINATION_TOTAL_PAGES, String.valueOf(meta.getTotalPages()));
		return headers;
	}
}
