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

import com.expedia.rf.ann.RestResource;
import com.expedia.seiso.core.ann.FindByKey;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.repo.custom.NodeRepoCustom;

/**
 * @author Willie Wheeler
 */
@RestResource(path = RepoKeys.NODES)
public interface NodeRepo extends PagingAndSortingRepository<Node, Long>, NodeRepoCustom {
	
	@FindByKey
	Node findByName(@Param("name") String name);
	
	@RestResource(path = "find-by-service-instance")
	Page<Node> findByServiceInstanceKey(@Param("key") String key, Pageable pageable);
	
	// FIXME Shouldn't this return a unique result?
	@RestResource(path = "find-by-ip-address-and-port")
	@Query("select n from Node n join n.ipAddresses nip join nip.endpoints e where nip.ipAddress = :ipAddress and e.port.number = :port")
	List<Node> findByIpAddressAndPort(@Param("ipAddress") String ipAddress, @Param("port") Integer port);
	
	@RestResource(path = "find-by-source")
	Page<Node> findBySourceKey(@Param("key") String key, Pageable pageable);
}
