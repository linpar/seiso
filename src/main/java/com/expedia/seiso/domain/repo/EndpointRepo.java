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

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.serf.ann.RestResource;

// FIXME Decouple the REST params from the JPA params. The JPA params use camelCase wheres the REST params ought to use
// hyphenated-lowercase. [WLW]

// TODO Suppress unwanted repo methods. E.g., we don't want findAll(). [WLW]

/**
 * @author Willie Wheeler
 */
@RestResource(rel = RepoKeys.ENDPOINTS, path = RepoKeys.ENDPOINTS)
public interface EndpointRepo extends PagingAndSortingRepository<Endpoint, Long> {

	@RestResource(path = "find-by-service-instance")
	@Query("from Endpoint e where e.port.serviceInstance = :serviceInstance")
	List<Endpoint> findByServiceInstance(@Param("serviceInstance") ServiceInstance serviceInstance);

	@RestResource(path = "find-by-machine")
	@Query("from Endpoint e where e.ipAddress.node.machine = :machine")
	List<Endpoint> findByMachine(@Param("machine") Machine machine);

	@RestResource(path = "find-by-node")
	@Query("from Endpoint e where e.ipAddress.node.name = :node")
	List<Endpoint> findByNode(@Param("node") String node);

	// Not sure why Eclipse is raising this warning here:
	// "Parameter type (String) does not match domain class property definition (NodeIpAddress)."
	// e.ipAddress.machineIpAddress.ipAddress is a string.
	// Oh, it's probably matching ipAddress to endpoint.ipAddress (by name), which is a NodeIpAddress.
	@RestResource(path = "find-by-ip-address")
	@Query("from Endpoint e where e.ipAddress.ipAddress = :ipAddress")
	List<Endpoint> findByIpAddress(@Param("ipAddress") String ipAddress);

	@RestResource(path = "find-by-ip-address-and-port")
	@Query("from Endpoint e where e.ipAddress.ipAddress = :ipAddress and e.port.number = :port")
	Endpoint findByIpAddressAndPort(@Param("ipAddress") String ipAddress, @Param("port") int port);
	
	@RestResource(path = "find-by-source")
	Page<Endpoint> findBySourceKey(@Param("key") String key, Pageable pageable);
}
