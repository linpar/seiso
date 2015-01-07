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
package com.expedia.seiso.domain.repo.adapter;

import lombok.NonNull;
import lombok.val;

import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.NodeIpAddressKey;
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;

/**
 * @author Willie Wheeler
 */
@Component
public class NodeIpAddressRepoAdapter implements RepoAdapter {
	private NodeIpAddressRepo nodeIpAddressRepo;
	
	public NodeIpAddressRepoAdapter(@NonNull NodeIpAddressRepo repo) {
		this.nodeIpAddressRepo = repo;
	}
	
	@Override
	public boolean supports(@NonNull Class<?> itemClass) {
		return itemClass == NodeIpAddress.class;
	}

	@Override
	public Item find(@NonNull ItemKey key) {
		val nodeIpAddressKey = (NodeIpAddressKey) key;
		val nodeName = nodeIpAddressKey.getNodeName();
		val ipAddress = nodeIpAddressKey.getIpAddress();
		return nodeIpAddressRepo.findByNodeNameAndIpAddress(nodeName, ipAddress);
	}

	@Override
	public void delete(@NonNull ItemKey key) {
		val nodeIpAddressKey = (NodeIpAddressKey) key;
		val nodeName = nodeIpAddressKey.getNodeName();
		val ipAddress = nodeIpAddressKey.getIpAddress();
		nodeIpAddressRepo.deleteByNodeNameAndIpAddress(nodeName, ipAddress);
	}
}
