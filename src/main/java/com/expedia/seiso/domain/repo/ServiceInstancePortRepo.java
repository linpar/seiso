/* 
 * Copyright 2013-2016 the original author or authors.
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

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;

/**
 * @author Willie Wheeler
 */
public interface ServiceInstancePortRepo extends PagingAndSortingRepository<ServiceInstancePort, Long> {

	@RestResource(exported = false)
	ServiceInstancePort findByServiceInstanceAndNumber(
			ServiceInstance serviceInstance,
			Integer number);

	ServiceInstancePort findByServiceInstanceKeyAndNumber(
			@Param("si") String serviceInstanceKey,
			@Param("number") Integer number);
	
	@Modifying
	@Query("delete from ServiceInstancePort p where p.serviceInstance.key = :si and p.number = :number")
	void deleteByServiceInstanceKeyAndNumber(
			@Param("si") String serviceInstanceKey,
			@Param("number") Integer number);
}
