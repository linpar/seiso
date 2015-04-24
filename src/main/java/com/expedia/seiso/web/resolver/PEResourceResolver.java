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
package com.expedia.seiso.web.resolver;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.expedia.rf.hmedia.PEResource;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.meta.ItemMetaLookup;

/**
 * Reads a resource off the request, binds it to the relevant item metadata, and returns the result.
 * 
 * @author Willie Wheeler
 */
@Component
public class PEResourceResolver implements HandlerMethodArgumentResolver {
	
	// FIXME DRY up. We're repeating info in com.expedia.seiso.web.converter.
	private static final String ITEM_FORMAT = "/{version}/{repoKey}/{itemKey}";
	private static final String PROPERTY_FORMAT = "/{version}/{repoKey}/{itemKey}/{propKey}";
	private static final String COLLECTION_ELEMENT_FORMAT = "/{version}/{repoKey}/{itemKey}/{propKey}/{propValue}";

	private List<SimplePropertyEntry> simplePropertyEntries;
	
	@Autowired private Repositories repositories;
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private ResolverUtils resolverUtils;
	@Autowired private List<HttpMessageConverter<?>> messageConverters;
	
	public PEResourceResolver(List<SimplePropertyEntry> entries) {
		this.simplePropertyEntries = entries;
	}
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return PEResource.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(
			MethodParameter param,
			ModelAndViewContainer mavContainer,
			NativeWebRequest nativeWebRequest,
			WebDataBinderFactory binderFactory) throws Exception {

		val nativeRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
		val path = nativeRequest.getRequestURI();

		Class<?> itemClass = null;
		val matcher = new AntPathMatcher();
		if (matcher.match(ITEM_FORMAT, path)) {
			val variables = matcher.extractUriTemplateVariables(ITEM_FORMAT, path);
			itemClass = itemMetaLookup.getItemClass(variables.get("repoKey"));
		} else if (matcher.match(PROPERTY_FORMAT, path)) {
			val variables = matcher.extractUriTemplateVariables(PROPERTY_FORMAT, path);
			val repoKey = variables.get("repoKey");
			val propKey = variables.get("propKey");
			itemClass = findPropertyClass(repoKey, propKey);
		} else if (matcher.match(COLLECTION_ELEMENT_FORMAT, path)) {
			val variables = matcher.extractUriTemplateVariables(COLLECTION_ELEMENT_FORMAT, path);
			val repoKey = variables.get("repoKey");
			val propKey = variables.get("propKey");
			itemClass = findPropertyClass(repoKey, propKey);
		} else {
			throw new RuntimeException("No resolver for requestUri=" + path);
		}

		val pEntity = repositories.getPersistentEntity(itemClass);
		val item = toItem(itemClass, nativeRequest);
		return new PEResource(pEntity, item);

	}

	private Class<?> findPropertyClass(String repoKey, String propKey) {
		for (val entry : simplePropertyEntries) {
			if (entry.getRepoKey().equals(repoKey) && entry.getPropKey().equals(propKey)) {
				return entry.getPropClass();
			}
		}
		throw new RuntimeException("No simple property entry for repoKey=" + repoKey + ", propKey=" + propKey);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Item toItem(Class<?> itemClass, HttpServletRequest request) throws IOException {
		val wrappedRequest = resolverUtils.wrapRequest(request);
		val contentType = wrappedRequest.getHeaders().getContentType();

		for (HttpMessageConverter messageConverter : messageConverters) {
			
			// This is how we process application/json separately from application/hal+json.
			if (messageConverter.canRead(itemClass, contentType)) {
				return (Item) messageConverter.read(itemClass, wrappedRequest);
			}
		}

		throw new RuntimeException("No converter for itemClass=" + itemClass.getName() + ", contentType="
				+ contentType.getType());
	}
}
