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
import com.expedia.seiso.domain.entity.SeyrenCheck;

/**
 * @author Willie Wheeler
 */
@RestResource(path = RepoKeys.SEYREN_CHECKS)
public interface SeyrenCheckRepo extends PagingAndSortingRepository<SeyrenCheck, Long> {
	
	@FindByKey
	SeyrenCheck findBySeyrenId(@Param("id") String seyrenId);
	
	@RestResource(path = "find-by-service-instance")
//	@Query("from SeyrenCheck c where c.serviceInstances.key = :key order by name")
//	List<SeyrenCheck> findByServiceInstanceKey(@Param("key") String key);
	List<SeyrenCheck> findByServiceInstancesKey(@Param("key") String key);
}
