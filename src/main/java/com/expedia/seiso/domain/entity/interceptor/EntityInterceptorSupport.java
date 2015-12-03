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
package com.expedia.seiso.domain.entity.interceptor;

import com.expedia.seiso.domain.entity.Item;

/**
 * No-op {@link EntityInterceptor} base class to facilitate item-specific implementations.
 * 
 * @author Willie Wheeler
 */
public class EntityInterceptorSupport<T extends Item> implements EntityInterceptor<T> {
	
	@Override
	public void prePersist(T item) { }
	
	@Override
	public void postPersist(T item) { }

	@Override
	public void preUpdate(T item) { }

	@Override
	public void postUpdate(T item) { }

	@Override
	public void preRemove(T item) { }

	@Override
	public void postRemove(T item) { }
}
