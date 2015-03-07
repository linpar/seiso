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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.expedia.seiso.core.ann.FindByKey;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.LoadBalancer;
import com.expedia.seiso.domain.repo.custom.LoadBalancerRepoCustom;

/**
 * @author Willie Wheeler
 */
@RestResource(path = RepoKeys.LOAD_BALANCERS)
public interface LoadBalancerRepo extends PagingAndSortingRepository<LoadBalancer, Long>, LoadBalancerRepoCustom {
	
	@FindByKey
	LoadBalancer findByName(@Param("name") String name);
	
	// FIXME v1 sees the param name as "dataCenterKey", not "data-center".
	// But production currently uses "data-center".
//	@RestResource(path = "find-by-data-center")
//	List<LoadBalancer> findByDataCenterKey(@Param("data-center") String dataCenterKey);
	
	// I replaced the above with this one. It may break the NetScaler sync job. I don't think so, but it might. [WLW]
	@RestResource(path = "find-by-data-center")
	@Query("from LoadBalancer lb where lb.dataCenter.key = :key")
	Page<LoadBalancer> findByDataCenterKey(@Param("key") String key, Pageable pageable);
	
	@RestResource(path = "find-by-source")
	Page<LoadBalancer> findBySourceKey(@Param("key") String key, Pageable pageable);
}
