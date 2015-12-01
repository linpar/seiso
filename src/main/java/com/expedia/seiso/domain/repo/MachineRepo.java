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

import com.expedia.seiso.domain.entity.Machine;

/**
 * @author Willie Wheeler
 */
public interface MachineRepo extends PagingAndSortingRepository<Machine, Long> {

	@Query("from Machine m order by m.name")
	@Override
	Iterable<Machine> findAll();

	Machine findByName(@Param("name") String name);

	// FIXME This won't work til NodeControllerV1 can handle single return values. [WLW]
	// @RestResource(path = "find-by-fqdn")
	Machine findByFqdn(@Param("fqdn") String fqdn);

	// FIXME This won't work til NodeControllerV1 can handle single return values. [WLW]
	// @RestResource(path = "find-by-ip-address")
	Machine findByIpAddress(@Param("ip") String ipAddress);
	
	@Query("select n.machine from ServiceInstance si join si.nodes n where si.key = :key")
	Page<Machine> findByServiceInstance(@Param("key") String key, Pageable pageable);
}
