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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

import com.expedia.rf.hmedia.Link;
import com.expedia.rf.hmedia.Resource;
import com.expedia.rf.hmedia.Resources;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ResourceAssembler;

// TODO Handle the case where a repo search method returns a unique result (e.g., PersonRepo.findByEmail()). [WLW]

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
	 * @param search
	 *            search path
	 * @param pageable
	 *            page request params
	 * @param params
	 *            all params
	 * @return result page
	 */
	@SuppressWarnings("rawtypes")
	public Object repoSearch(
			@NonNull ApiVersion apiVersion,
			@NonNull String repoKey,
			@NonNull String search,
			@NonNull String view,
			@NonNull Pageable pageable,
			@NonNull MultiValueMap<String, String> params) {
		
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val searchMethod = itemMeta.getRepositorySearchMethod(search);
		notNull(searchMethod, "Unknown search: " + search);
		val returnType = searchMethod.getReturnType();
		val proj = itemMeta.getProjectionNode(apiVersion, Projection.Cardinality.COLLECTION, view);
		val result = getResult(itemClass, searchMethod, pageable, params);
		
		if (returnType == List.class) {
			val resultList = (List) result;
			val resourceList = resourceAssembler.toResourceList(apiVersion, resultList, proj);
			// TODO Probably want a self link in here, at least. [WLW]
			return new Resources(new ArrayList<Link>(), resourceList);
		} else if (returnType == Page.class) {
			val resultPage = (Page) result;
			return resourceAssembler.toRepoSearchPagedResources(apiVersion, resultPage, itemClass, search, params, proj);
		} else {
			throw new UnsupportedOperationException(
					"Don't know how to handle search " + search + " with return type " + returnType.getName());
		}
	}
	
	// FIXME Some of this belongs in ItemServiceImpl.
	// This can return either a Page or a List, depending on the search method we invoke.
	private Object getResult(
			Class<?> itemClass,
			Method searchMethod,
			Pageable pageable,
			MultiValueMap<String, String> params) {
		
		val searchMethodName = searchMethod.getName();
		log.trace("Finding {} using method {}", itemClass.getSimpleName(), searchMethodName);
		val repo = repositories.getRepositoryFor(itemClass);
		val paramClasses = searchMethod.getParameterTypes();
		val allAnns = searchMethod.getParameterAnnotations();
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

		log.trace("Invoking {}.{} with {} params", repo.getClass().getName(), searchMethodName, paramVals.length);
		return ReflectionUtils.invokeMethod(searchMethod, repo, paramVals);
	}
}
