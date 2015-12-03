/* 
 * Copyright 2013-2016 the original author or authors.
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
package com.expedia.seiso.domain.entity.listener;

import java.lang.reflect.ParameterizedType;

import lombok.val;

import com.expedia.seiso.domain.entity.interceptor.EntityInterceptor;
import com.expedia.seiso.util.ApplicationContextProvider;

/**
 * @author Willie Wheeler
 */
public abstract class AbstractEntityListener<T extends EntityInterceptor<?>> {
	private Class<T> interceptorClass;
	
	@SuppressWarnings("unchecked")
	public AbstractEntityListener() {
		val superclass = (ParameterizedType) getClass().getGenericSuperclass();
		val typeArgs = superclass.getActualTypeArguments();
		this.interceptorClass = (Class<T>) typeArgs[1];
	}
	
	protected T getInterceptor() {
		return ApplicationContextProvider.applicationContext().getBean(interceptorClass);
	}
}
