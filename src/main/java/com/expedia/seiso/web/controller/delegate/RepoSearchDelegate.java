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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.web.Relations;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.seiso.web.hateoas.Resource;
import com.expedia.seiso.web.hateoas.PagedResources;
import com.expedia.seiso.web.hateoas.link.LinkFactory;

// TODO
// - Handle list results
// - Handle unique result

/**
 * Web component to perform repository searches on behalf of version-specific controllers.
 * 
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class RepoSearchDelegate {
	@Autowired private Repositories repositories;
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private ItemService itemService;
	@Autowired private ResourceAssembler itemAssembler;
	@Autowired @Qualifier("linkFactoryV2") private LinkFactory linkFactoryV2;
	@Autowired private ConversionService conversionService;
	
	/**
	 * @param repoKey
	 * @return
	 */
	public Resource getRepoSearchList(@NonNull String repoKey) {
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val repoInfo = repositories.getRepositoryInformationFor(itemClass);
		val queryMethods = repoInfo.getQueryMethods();
		
		val itemLinksV2 = linkFactoryV2.getItemLinks();
		val repoSearchLinksV2 = linkFactoryV2.getRepoSearchLinks();
		
		val resource = new Resource();
		resource.addV2Link(repoSearchLinksV2.repoSearchListLink(Relations.SELF, itemClass));
		resource.addV2Link(itemLinksV2.repoLink(Relations.UP, itemClass));
		
		// Query method links
		for (val queryMethod : queryMethods) {
			val restResource = AnnotationUtils.getAnnotation(queryMethod, RestResource.class);
			if (restResource == null) { continue; }
			
			val path = restResource.path();
			if (path.isEmpty()) { continue; }
			
			val requestParams = new LinkedMultiValueMap<String, String>();
			val methodParams = queryMethod.getParameters();
			for (val methodParam : methodParams) {
				val paramAnn = methodParam.getAnnotation(Param.class);
				if (paramAnn != null) {
					val requestParamName = paramAnn.value();
					// FIXME Formatting the URI template variables belongs with the xxxLinks objects, not here. [WLW]
					requestParams.set(requestParamName, "{" + requestParamName + "}"); 
				}
			}
			
			val rel = "s:" + (restResource.rel().isEmpty() ? path : restResource.rel());
			resource.addV2Link(repoSearchLinksV2.toRepoSearchLinkTemplate(rel, itemClass, path, requestParams));
		}
		
		return resource;
	}
	
	/**
	 * Search with paging results.
	 * 
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
			@NonNull String repoKey,
			@NonNull String path,
			@NonNull String view,
			@NonNull Pageable pageable,
			@NonNull MultiValueMap<String, String> params) {

		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val itemPage = repoSearch(itemClass, path, pageable, params);
		val proj = itemMeta.getProjectionNode(Projection.Cardinality.COLLECTION, view);
		return itemAssembler.toRepoSearchResource(itemPage, itemClass, path, params, proj);
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
