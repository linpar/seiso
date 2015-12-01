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

import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;

/**
 * @author Willie Wheeler
 */
public interface NodeIpAddressRepo extends PagingAndSortingRepository<NodeIpAddress, Long> {

	@Query("from NodeIpAddress nip where nip.node.name = :nodeName and nip.ipAddress = :ipAddress")
	NodeIpAddress findByNodeNameAndIpAddress(@Param("nodeName") String nodeName, @Param("ipAddress") String ipAddress);

	@Modifying
	@Query("delete from NodeIpAddress nip where nip.node = :node and nip.ipAddress = :ipAddress")
	void deleteByNodeAndIpAddress(@Param("node") Node node, @Param("ipAddress") String ipAddress);

	@Modifying
	@Query("delete from NodeIpAddress nip where nip.node.name = :nodeName and nip.ipAddress = :ipAddress")
	void deleteByNodeNameAndIpAddress(@Param("nodeName") String nodeName, @Param("ipAddress") String ipAddress);
}
