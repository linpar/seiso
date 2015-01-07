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
import lombok.val;

import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.ServiceInstancePortKey;
import com.expedia.seiso.domain.repo.ServiceInstancePortRepo;

/**
 * Service instance port lookup strategy.
 * 
 * @author Willie Wheeler
 */
@Component
public class ServiceInstancePortRepoAdapter implements RepoAdapter {
	private ServiceInstancePortRepo serviceInstancePortRepo;
	
	public ServiceInstancePortRepoAdapter(@NonNull ServiceInstancePortRepo repo) {
		this.serviceInstancePortRepo = repo;
	}
	
	@Override
	public boolean supports(@NonNull Class<?> itemClass) {
		return itemClass == ServiceInstancePort.class;
	}

	@Override
	public ServiceInstancePort find(@NonNull ItemKey key) {
		val portKey = (ServiceInstancePortKey) key;
		val serviceInstanceKey = portKey.getServiceInstanceKey();
		val number = portKey.getNumber();
		return serviceInstancePortRepo.findByServiceInstanceKeyAndNumber(serviceInstanceKey, number);
	}

	@Override
	public void delete(@NonNull ItemKey key) {
		val portKey = (ServiceInstancePortKey) key;
		val serviceInstanceKey = portKey.getServiceInstanceKey();
		val number = portKey.getNumber();
		serviceInstancePortRepo.deleteByServiceInstanceKeyAndNumber(serviceInstanceKey, number);
	}
}
