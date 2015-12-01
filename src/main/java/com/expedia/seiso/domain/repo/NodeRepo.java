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

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.expedia.seiso.domain.entity.Node;

/**
 * @author Willie Wheeler
 */
public interface NodeRepo extends PagingAndSortingRepository<Node, Long> {
	
	// Include infos here too, the idea being that these are things that we want to let people know about, but they are
	// purely informational in nature as opposed to warnings or danger alerts. We use default statuses for things that
	// we don't want to show up in the alert list. This is something Mike Nash and came up with after discussion so
	// please don't change it without talking to one of us first. [WLW]
	public static final String FIND_NODE_ALERTS_JPQL =
			"select " +
			"  n " +
			"from " +
			"  Node n " +
			"  join n.healthStatus hs " +
			"  join hs.statusType hst " +
			"where " +
			"  n.serviceInstance.key = :key " +
			"  and (hst.key in ('info', 'warning', 'danger')" +
			"      or n.aggregateRotationStatus.statusType.key in ('info', 'warning', 'danger'))";
	
	Node findByName(@Param("name") String name);
	
	Page<Node> findByServiceInstanceKey(@Param("key") String key, Pageable pageable);
	
	@Query(FIND_NODE_ALERTS_JPQL)
	Page<Node> findNodeAlertsByServiceInstance(@Param("key") String key, Pageable pageable);
	
	// FIXME Shouldn't this return a unique result?
	@Query("select n from Node n join n.ipAddresses nip join nip.endpoints e where nip.ipAddress = :ipAddress and e.port.number = :port")
	List<Node> findByIpAddressAndPort(@Param("ipAddress") String ipAddress, @Param("port") Integer port);
}
