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
package com.expedia.seiso.domain.service.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.domain.service.ServiceInstanceService;
import com.expedia.serf.service.AbstractPersistenceInterceptor;

/**
 * @author Willie Wheeler
 */
@Component
public class EndpointPersistenceInterceptor extends AbstractPersistenceInterceptor {
	@Autowired private ServiceInstanceService serviceInstanceService;
	@Autowired private NodeRepo nodeRepo;
	@Autowired private NodeIpAddressRepo nodeIpAddressRepo;
	
	@Override
	public void postUpdate(Object entity) {
		Endpoint endpoint = (Endpoint) entity;
		NodeIpAddress nip = endpoint.getIpAddress();
		Node node = nip.getNode();
		serviceInstanceService.recalculateAggregateRotationStatus(nip);
		nodeIpAddressRepo.save(nip);
		serviceInstanceService.recalculateAggregateRotationStatus(node);
		nodeRepo.save(node);
	}
}
