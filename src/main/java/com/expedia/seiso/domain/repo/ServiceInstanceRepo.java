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

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.expedia.seiso.core.ann.FindByKey;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.ServiceInstance;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@RestResource(path = RepoKeys.SERVICE_INSTANCES)
public interface ServiceInstanceRepo extends PagingAndSortingRepository<ServiceInstance, Long> {

	@FindByKey
	ServiceInstance findByKey(@Param("key") String key);

	@RestResource(path = "find-by-environment-and-eos-managed")
	List<ServiceInstance> findByEnvironmentKeyAndEosManaged(
			@Param("env") String environmentKey,
			@Param("eos") Boolean eosManaged);
}
