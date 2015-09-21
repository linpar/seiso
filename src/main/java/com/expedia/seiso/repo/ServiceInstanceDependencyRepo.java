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
package com.expedia.seiso.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.expedia.seiso.entity.ServiceInstanceDependency;

/**
 * @author Willie Wheeler
 */
public interface ServiceInstanceDependencyRepo extends PagingAndSortingRepository<ServiceInstanceDependency, Long> {
	
	public static final String FIND_BY_KEYS =
			"from " +
			"  ServiceInstanceDependency " +
			"where " +
			"  dependent.key = :dependent and " +
			"  dependency.key = :dependency";
	
	public static final String FIND_BY_DEPENDENT_WITH_COUNTS =
			"select distinct " +
			"  sid, " +
			"  count(*), " +
			"  count(case when st.key in ('info', 'success') then 1 end) " +
			"from " +
			"  ServiceInstanceDependency sid left outer join sid.dependency si " +
			"  left outer join si.nodes n " +
			"  left outer join n.healthStatus hs " +
			"  left outer join hs.statusType st " +
			"where " +
			"  sid.dependent.key = :key " +
			"group by " +
			"  si.id";
	
	public static final String FIND_BY_DEPENDENCY_WITH_COUNTS =
			"select distinct " +
			"  sid, " +
			"  count(*), " +
			"  count(case when st.key in ('info', 'success') then 1 end) " +
			"from " +
			"  ServiceInstanceDependency sid left outer join sid.dependent si " +
			"  left outer join si.nodes n " +
			"  left outer join n.healthStatus hs " +
			"  left outer join hs.statusType st " +
			"where " +
			"  sid.dependency.key = :key " +
			"group by " +
			"  si.id";
	
	@Query(FIND_BY_KEYS)
	ServiceInstanceDependency findByKeys(
			@Param("dependent") String dependentKey,
			@Param("dependency") String dependencyKey);
	
	Page<ServiceInstanceDependency> findByDependentKey(String key, Pageable pageable);
	
	Page<ServiceInstanceDependency> findByDependencyKey(String key, Pageable pageable);
	
	/**
	 * @param dependentKey
	 *            dependent service instance key
	 * @param pageable
	 * @return
	 */
	// TODO Can't expose this yet, because the RepoSearchDelegate doesn't know how to handle
	// Object[] results. The ResourceAssembler doesn't know either. [WLW]
	@Query(FIND_BY_DEPENDENT_WITH_COUNTS)
	Page<Object[]> findByDependentWithCounts(@Param("key") String dependentKey, Pageable pageable);
	
	/**
	 * @param dependencyKey
	 *            dependency service instance key
	 * @param pageable
	 * @return
	 */
	// TODO Can't expose this yet, because the RepoSearchDelegate doesn't know how to handle
	// Object[] results. The ResourceAssembler doesn't know either. [WLW]
	@Query(FIND_BY_DEPENDENCY_WITH_COUNTS)
	Page<Object[]> findByDependencyWithCounts(@Param("key") String dependencyKey, Pageable pageable);
}
