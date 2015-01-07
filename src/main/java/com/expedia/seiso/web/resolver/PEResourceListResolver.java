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
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.expedia.seiso.core.util.ReflectionUtils;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.list.EntityListPackageMarker;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.controller.PEResourceList;

/**
 * Reads a resource off the request, binds it to the relevant item metadata, and returns the result.
 * 
 * @author Willie Wheeler
 */
public class PEResourceListResolver implements HandlerMethodArgumentResolver {
	@Autowired private Repositories repositories;
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private ResolverUtils resolverUtils;
	@Autowired private List<HttpMessageConverter<?>> messageConverters;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return PEResourceList.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter param, ModelAndViewContainer mavContainer,
			NativeWebRequest nativeWebRequest, WebDataBinderFactory binderFactory) throws Exception {

		val nativeRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
		return toItems(nativeRequest);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object toItems(HttpServletRequest nativeRequest) throws IOException {
		val parts = extractParts(nativeRequest);

		// parts[0] is the version identifier, so skip it.
		val repoKey = parts[1];

		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemListClass = toItemListClass(itemClass);
		val pEntity = repositories.getPersistentEntity(itemClass);
		val request = resolverUtils.wrapRequest(nativeRequest);
		val contentType = request.getHeaders().getContentType();

		for (HttpMessageConverter messageConverter : messageConverters) {
			if (messageConverter.canRead(PEResourceList.class, contentType)) {
				val itemList = (List<Item>) messageConverter.read(itemListClass, request);
				val peItemDtoList = new PEResourceList(pEntity);
				for (val item : itemList) {
					peItemDtoList.add(item);
				}
				return peItemDtoList;
			}
		}

		return null;
	}

	private String[] extractParts(HttpServletRequest request) {
		// substring() removes initial /
		return request.getRequestURI().substring(1).split("/");
	}

	private Class<?> toItemListClass(Class<?> itemClass) {
		val packageName = EntityListPackageMarker.class.getPackage().getName();
		val itemListClassName = packageName + "." + itemClass.getSimpleName() + "List";
		return ReflectionUtils.classForName(itemListClassName);
	}
}
