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

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.val;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.expedia.serf.ann.SuppressBasePath;

/**
 * <p>
 * A handler mapping that knows about the globally-defined base path.
 * </p>
 * <p>
 * This is a simplified version of the Spring Data REST class
 * <code>org.springframework.data.rest.webmvc.BasePathAwareHandlerMapping</code>, 
 * by Oliver Gierke.
 * </p>
 * 
 * @author Oliver Gierke
 * @author Willie Wheeler
 */
public class BasePathAwareHandlerMapping extends RequestMappingHandlerMapping {
	private final String basePath;
	
	public BasePathAwareHandlerMapping(@NonNull String basePath) {
		this.basePath = basePath;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#getMappingForMethod(java.lang.reflect.Method, java.lang.Class)
	 */
	@Override
	protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		val info = super.getMappingForMethod(method, handlerType);
		val suppressBasePath = (AnnotationUtils.findAnnotation(handlerType, SuppressBasePath.class) != null);
		
		if (info == null || suppressBasePath) {
			return info;
		}
		
		// @formatter:off
		return new RequestMappingInfo(
				augment(info.getPatternsCondition()),
				info.getMethodsCondition(),
				info.getParamsCondition(),
				info.getHeadersCondition(),
				info.getConsumesCondition(),
				info.getProducesCondition(),
				info.getCustomCondition());
		// @formatter:on
	}
	
	private PatternsRequestCondition augment(PatternsRequestCondition condition) {
		// @formatter:off
		return new PatternsRequestCondition(
				augment(condition.getPatterns()),
				getUrlPathHelper(),
				getPathMatcher(),
				useSuffixPatternMatch(),
				useTrailingSlashMatch(),
				getFileExtensions());
		// @formatter:on
	}
	
	private String[] augment(Set<String> patterns) {
		String[] newPatterns = new String[patterns.size()];
		// @formatter:off
		return patterns
				.stream()
				.map(p -> basePath.concat(p))
				.collect(Collectors.toList())
				.toArray(newPatterns);
		// @formatter:on
	}
}
