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
package com.expedia.seiso.web.eventhandler;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.web.assembler.ServiceInstanceService;

/**
 * @author Willie Wheeler
 */
@RepositoryEventHandler(Endpoint.class)
@Component
public class EndpointEventHandler {
	@Autowired private NodeRepo nodeRepo;
	@Autowired private NodeIpAddressRepo nodeIpAddressRepo;
	@Autowired private ServiceInstanceService serviceInstanceService;
	
	@HandleAfterSave
	public void handleAfterSave(Endpoint endpoint) {
		val nip = endpoint.getIpAddress();
		val node = nip.getNode();
		serviceInstanceService.recalculateAggregateRotationStatus(nip);
		nodeIpAddressRepo.save(nip);
		serviceInstanceService.recalculateAggregateRotationStatus(node);
		nodeRepo.save(node);
	}
}
