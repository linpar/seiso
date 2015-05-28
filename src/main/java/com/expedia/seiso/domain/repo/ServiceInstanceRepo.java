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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.expedia.seiso.core.ann.FindByKey;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.repo.custom.ServiceInstanceRepoCustom;
import com.expedia.serf.ann.RestResource;

/**
 * @author Willie Wheeler
 */
@RestResource(rel = RepoKeys.SERVICE_INSTANCES, path = RepoKeys.SERVICE_INSTANCES)
public interface ServiceInstanceRepo
		extends PagingAndSortingRepository<ServiceInstance, Long>, ServiceInstanceRepoCustom {
	
	// Need the left outer joins to ensure that nodeless service instances show up.
	public static final String FIND_COUNTS_BY_SERVICE =
			"select " +
			"  si, " +
			"  count(*), " +
			"  count(case when st.key in ('info', 'success') then 1 end) " +
			"from " +
			"  ServiceInstance si left outer join si.nodes n " +
			"  left outer join n.healthStatus hs " +
			"  left outer join hs.statusType st " +
			"where " +
			"  si.service.key = :key " +
			"group by " +
			"  si";
	
	// Ugh: http://en.wikibooks.org/wiki/Java_Persistence/JPQL#Sub-selects_in_FROM_clause
//	public static final String FIND_COUNTS_ALL_SERVICES =
//			"select " +
//			"  *, " +
//			"  numHealthy / numNodes as percentHealthy " +
//			"from (" +
//			"  select " +
//			"    si, " +
//			"    count(*) as numNodes, " +
//			"    count(case when st.key in ('info', 'success') then 1 end) as numHealthy " +
//			"  from " +
//			"    Node n inner join n.serviceInstance si, " +
//			"    StatusType st " +
//			"  where " +
//			"    n.healthStatus.statusType = st " +
//			"  group by " +
//			"    si.id) " +
//			"order by " +
//			"  percentHealthy";
	
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
	
//	@Query(FIND_COUNTS_ALL_SERVICES)
//	Page<Object[]> findCountsAllServices(Pageable pageable);
	
	// TODO Can't expose this yet, because the RepoSearchDelegate doesn't know how to handle
	// Object[] results. The ResourceAssembler doesn't know either. Do we treat the individual results
	// here as resources? (Service instances?) [WLW]
//	@RestResource(path = "find-counts-by-service")
	@Query(FIND_COUNTS_BY_SERVICE)
	List<Object[]> findCountsByService(@Param("key") String key);
}
