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
package com.expedia.seiso.domain.repo.adapter;

import lombok.NonNull;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.ServiceInstanceDependency;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.repo.ServiceInstanceDependencyRepo;

/**
 * @author Willie Wheeler
 */
public class ServiceInstanceDependencyRepoAdapter implements RepoAdapter {
	private ServiceInstanceDependencyRepo serviceInstanceDependencyRepo;
	
	public ServiceInstanceDependencyRepoAdapter(@NonNull ServiceInstanceDependencyRepo sidRepo) {
		this.serviceInstanceDependencyRepo = sidRepo;
	}
	
	@Override
	public boolean supports(@NonNull Class<?> itemClass) {
		return itemClass == ServiceInstanceDependency.class;
	}

	@Override
	public Item find(@NonNull ItemKey key) {
		SimpleItemKey sidKey = (SimpleItemKey) key;
		Long id = (Long) sidKey.getValue();
		return serviceInstanceDependencyRepo.findOne(id);
	}

	@Override
	public void delete(@NonNull ItemKey key) {
		SimpleItemKey sidKey = (SimpleItemKey) key;
		Long id = (Long) sidKey.getValue();
		serviceInstanceDependencyRepo.delete(id);
	}
}
