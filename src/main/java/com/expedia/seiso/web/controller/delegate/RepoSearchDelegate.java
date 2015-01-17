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
package com.expedia.seiso.web.controller.delegate;

import static org.springframework.util.Assert.notNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.seiso.web.hateoas.PagedResources;
import com.expedia.seiso.web.hateoas.Resource;

// TODO
// - Handle list results
// - Handle unique result

/**
 * Web component to perform repository searches on behalf of version-specific controllers.
 * 
 * @author Willie Wheeler
 */
@Component
@RequiredArgsConstructor
@XSlf4j
public class RepoSearchDelegate {
	@NonNull private ResourceAssembler resourceAssembler;
	@Autowired @Setter private ItemMetaLookup itemMetaLookup;
	@Autowired @Setter private Repositories repositories;
	@Autowired @Setter private ItemService itemService;
	@Autowired @Setter private ConversionService conversionService;
	
	public Resource getRepoSearchList(@NonNull ApiVersion apiVersion, @NonNull String repoKey) {
		return resourceAssembler.toRepoSearchList(apiVersion, repoKey);
	}
	
	/**
	 * Search with paging results.
	 * 
	 * @param apiVersion
	 *            API version
	 * @param repoKey
	 *            repository key
	 * @param path
	 *            search path
	 * @param pageable
	 *            page request params
	 * @param params
	 *            all params
	 * @return result page
	 */
	@SuppressWarnings("rawtypes")
	public PagedResources repoSearch(
			@NonNull ApiVersion apiVersion,
			@NonNull String repoKey,
			@NonNull String path,
			@NonNull String view,
			@NonNull Pageable pageable,
			@NonNull MultiValueMap<String, String> params) {

		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val itemPage = repoSearch(itemClass, path, pageable, params);
		val proj = itemMeta.getProjectionNode(Projection.Cardinality.COLLECTION, view);
		return resourceAssembler.toRepoSearchResource(apiVersion, itemPage, itemClass, path, params, proj);
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
