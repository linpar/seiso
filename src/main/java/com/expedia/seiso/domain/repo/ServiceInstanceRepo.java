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
package com.expedia.seiso.domain.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.expedia.seiso.core.ann.FindByKey;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.repo.custom.ServiceInstanceRepoCustom;

/**
 * @author Willie Wheeler
 */
@RestResource(path = RepoKeys.SERVICE_INSTANCES)
public interface ServiceInstanceRepo
		extends PagingAndSortingRepository<ServiceInstance, Long>, ServiceInstanceRepoCustom {

	@FindByKey
	ServiceInstance findByKey(@Param("key") String key);
	
	@RestResource(path = "find-by-data-center")
	Page<ServiceInstance> findByDataCenterKey(@Param("key") String key, Pageable pageable);
	
	@RestResource(path = "find-by-environment")
	Page<ServiceInstance> findByEnvironmentKey(@Param("key") String key, Pageable pageable);
	
	@RestResource(path = "find-by-environment-and-eos-managed")
	List<ServiceInstance> findByEnvironmentKeyAndEosManaged(
			@Param("env") String environmentKey,
			@Param("eos") Boolean eosManaged);
	
	@RestResource(path = "find-by-source")
	Page<ServiceInstance> findBySourceKey(@Param("key") String key, Pageable pageable);
}
