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
package com.expedia.serf.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

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

import com.expedia.serf.exception.JavaConfigurationException;
import com.expedia.serf.hypermedia.hal.HalResource;
import com.expedia.serf.meta.RepoMetaRegistry;
import com.expedia.serf.util.ResolverUtils;

/**
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class PersistentEntityResourceResolver implements HandlerMethodArgumentResolver {
	private static final String POST_ENTITY_FORMAT = "/{basePath}/{repoPath}";
	private static final String PUT_ENTITY_FORMAT = "/{basePath}/{repoPath}/{entityId}";
	
	@Autowired private RepoMetaRegistry repoMetaRegistry;
	@Autowired private Repositories repositories;
	@Autowired private ResolverUtils resolverUtils;
	@Autowired private List<HttpMessageConverter<?>> messageConverters;

	/* (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		log.trace("Evaluating parameter: {}", parameter.getParameterName());
		val paramType = parameter.getParameterType();
		return PersistentEntityResource.class.isAssignableFrom(paramType);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	public Object resolveArgument(
			MethodParameter parameter,
			ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		
		log.trace("Resolving argument: {}", parameter);
		
		val nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		val requestUri = nativeRequest.getRequestURI();
		
		// Probably need to add other entity formats in the future, like for collection properties.
		Class<?> entityClass = null;
		val matcher = new AntPathMatcher();
		if (matcher.match(POST_ENTITY_FORMAT, requestUri)) {
			val vars = matcher.extractUriTemplateVariables(POST_ENTITY_FORMAT, requestUri);
			val repoPath = vars.get("repoPath");
			entityClass = repoMetaRegistry.getEntityClass(repoPath);
		} else if (matcher.match(PUT_ENTITY_FORMAT, requestUri)) {
			val vars = matcher.extractUriTemplateVariables(PUT_ENTITY_FORMAT, requestUri);
			val repoPath = vars.get("repoPath");
			entityClass = repoMetaRegistry.getEntityClass(repoPath);
		} else {
			val errMsg = String.format("Unsupported request URI: %s", requestUri);
			throw new UnsupportedOperationException(errMsg);
		}
		
		val persistentEntity = repositories.getPersistentEntity(entityClass);
		val halResource = toHalResource(nativeRequest);
		return new PersistentEntityResource(persistentEntity, halResource);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private HalResource toHalResource(HttpServletRequest request) throws IOException {
		val wrappedRequest = resolverUtils.wrapRequest(request);
		val contentType = wrappedRequest.getHeaders().getContentType();
		for (HttpMessageConverter messageConverter : messageConverters) {
//			if (messageConverter.canRead(entityClass, contentType)) {
//				return messageConverter.read(entityClass, wrappedRequest);
//			}
			log.trace("Testing to see whether {} can read {}", messageConverter, contentType);
			if (messageConverter.canRead(HalResource.class, contentType)) {
				log.trace("Yes!");
				return (HalResource) messageConverter.read(HalResource.class, wrappedRequest);
			} else {
				log.trace("No!");
			}
		}
		val errMsg = String.format("No HttpMessageConverter for HalResource, contentType=%s", contentType);
		throw new JavaConfigurationException(errMsg);
	}
}
