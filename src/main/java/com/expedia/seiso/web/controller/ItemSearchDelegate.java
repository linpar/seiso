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
package com.expedia.seiso.web.controller;

import static org.springframework.util.Assert.notNull;

import java.util.List;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.domain.service.SearchEngine;
import com.expedia.seiso.domain.service.search.SearchQuery;
import com.expedia.seiso.web.Relations;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.hateoas.BaseResource;
import com.expedia.seiso.web.hateoas.BaseResourcePage;
import com.expedia.seiso.web.hateoas.ItemLinks;

/**
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class ItemSearchDelegate {
	@Autowired private Repositories repositories;
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private ItemService itemService;
	@Autowired private SearchEngine searchEngine;
	@Autowired private ItemAssembler itemAssembler;
	@Autowired @Qualifier("itemLinksV2") private ItemLinks itemLinksV2;
	@Autowired private ConversionService conversionService;
	
	/**
	 * @param repoKey
	 * @return
	 */
	public BaseResource getRepoSearchList(@NonNull String repoKey) {
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val repoInfo = repositories.getRepositoryInformationFor(itemClass);
		val queryMethods = repoInfo.getQueryMethods();
		
		// v1 doesn't support this endpoint, so we don't need to add v1 links.
		
		val resource = new BaseResource();
		resource.addV2Link(itemLinksV2.itemRepoSearchListLink(Relations.SELF, itemClass));
		resource.addV2Link(itemLinksV2.itemRepoLink(Relations.UP, itemClass));
		
		// Query method links
		for (val queryMethod : queryMethods) {
			val restResource = AnnotationUtils.getAnnotation(queryMethod, RestResource.class);
			if (restResource == null) { continue; }
			val path = restResource.path();
			if (path.isEmpty()) { continue; }
			val rel = "s:" + (restResource.rel().isEmpty() ? path : restResource.rel());
			resource.addV2Link(itemLinksV2.itemRepoSearchLink(rel, itemClass, path));
		}
		
		return resource;
	}
	
	/**
	 * Search with non-paging results.
	 * 
	 * @param repoKey
	 *            repository key
	 * @param query
	 *            search query
	 * @return result list
	 */
	public List<BaseResource> repoSearch(@NonNull String repoKey, @NonNull String query) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 * Search with paging results.
	 * 
	 * @param repoKey
	 *            repository key
	 * @param query
	 *            search query
	 * @param pageable
	 *            page request params
	 * @param params
	 *            all params
	 * @return result page
	 */
	@SuppressWarnings("rawtypes")
	public BaseResourcePage repoSearch(
			@NonNull String repoKey,
			@NonNull String query,
			Pageable pageable,
			MultiValueMap<String, String> params) {

		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val itemPage = repoSearch(itemClass, query, pageable, params);
		val proj = itemMeta.getProjectionNode(Projection.Cardinality.COLLECTION, Projection.DEFAULT);
		return itemAssembler.toBaseResourcePage(itemClass, itemPage, proj);
	}
	
	public BaseResource repoSearchUnique(@NonNull String repoKey, @NonNull String query) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public BaseResource globalSearch(@NonNull SearchQuery query, @NonNull Pageable pageable) {
		val results = searchEngine.search(query, pageable);
		return itemAssembler.toBaseResource(results);
	}
	
	// FIXME Some of this belongs in ItemServiceImpl.
	@SuppressWarnings("unchecked")
	private <T extends Item> Page<T> repoSearch(
			Class<?> itemClass,
			String query,
			Pageable pageable,
			MultiValueMap<String, String> params) {
		
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val method = itemMeta.getRepositorySearchMethod(query);
		notNull(method, "Unknown search: " + query);

		log.trace("Finding {} using method {}", itemClass.getSimpleName(), method.getName());
		val repo = repositories.getRepositoryFor(itemClass);
		val paramClasses = method.getParameterTypes();
		val allAnns = method.getParameterAnnotations();
		val paramVals = new Object[paramClasses.length];
		for (int i = 0; i < paramClasses.length; i++) {
			log.trace("Processing param {}", i);
			if (paramClasses[i] == Pageable.class) {
				paramVals[i] = pageable;
			} else {
				val currentAnns = allAnns[i];
				for (val currentAnn : currentAnns) {
					if (Param.class.equals(currentAnn.annotationType())) {
						log.trace("Found @Param");
						if (conversionService.canConvert(String.class, paramClasses[i])) {
							val paramAnn = (Param) currentAnn;
							val paramName = paramAnn.value();
							val paramValAsStr = params.getFirst(paramName);
							log.trace("Setting param: {}={}", paramName, paramValAsStr);
							paramVals[i] = conversionService.convert(paramValAsStr, paramClasses[i]);
						} else {
							log.trace("BUG! Not setting the param value!");
						}
					}
				}
			}
		}

		log.trace("Invoking method {} on repo {} with {} params", method.getName(), repo.getClass(), paramVals.length);
		// FIXME ClassCastException when this isn't a list. E.g. EndpointRepo.findByIpAddressAndPort. [WLW]
//		val result = (List<T>) ReflectionUtils.invokeMethod(method, repo, paramVals);
		val result = (Page<T>) ReflectionUtils.invokeMethod(method, repo, paramVals);
		return result;
	}
}
