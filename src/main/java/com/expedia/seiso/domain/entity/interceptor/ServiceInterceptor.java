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

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Service;

/**
 * @author Willie Wheeler
 */
@Component
@Slf4j
public class ServiceInterceptor extends EntityInterceptorSupport<Service> {
	
	@Override
	public void postPersist(@NonNull Service service) {
		log.trace("Post-persisting service: {}", service);
	}
	
	@Override
	public void postUpdate(@NonNull Service service) {
		log.trace("Post-updating service: {}", service);
	}
}
