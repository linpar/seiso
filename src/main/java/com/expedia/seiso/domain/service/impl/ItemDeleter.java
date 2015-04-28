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
package com.expedia.seiso.domain.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.gateway.impl.NotificationAspect;

/**
 * Implemented as a separate component to support {@link NotificationAspect} proxying.
 * 
 * @author Willie Wheeler
 */
@Component
@RequiredArgsConstructor
public class ItemDeleter {
	@NonNull private Repositories repositories;
	
	public void delete(@NonNull Item item) {
		val repo = (CrudRepository) repositories.getRepositoryFor(item.getClass());
		repo.delete(item);
	}
}
