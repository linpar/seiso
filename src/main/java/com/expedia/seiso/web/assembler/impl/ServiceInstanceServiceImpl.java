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
package com.expedia.seiso.web.assembler.impl;

import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expedia.seiso.domain.repo.ServiceInstanceRepo;
import com.expedia.seiso.web.assembler.ServiceInstanceService;
import com.expedia.seiso.web.resource.BreakdownItem;
import com.expedia.seiso.web.resource.NodeSummary;

/**
 * @author Willie Wheeler
 */
@Service
@Transactional
public class ServiceInstanceServiceImpl implements ServiceInstanceService {
	@Autowired private ServiceInstanceRepo serviceInstanceRepo;
	
	@Override
	public NodeSummary getNodeSummary(@NonNull Long id) {
		return serviceInstanceRepo.getServiceInstanceNodeSummary(id);
	}

	@Override
	public Resources<BreakdownItem> getHealthBreakdown(@NonNull Long id) {
		val items = serviceInstanceRepo.getServiceInstanceHealthBreakdown(id);
		return new Resources<BreakdownItem>(items);
	}

	@Override
	public Resources<BreakdownItem> getRotationBreakdown(@NonNull Long id) {
		val items = serviceInstanceRepo.getServiceInstanceRotationBreakdown(id);
		return new Resources<BreakdownItem>(items);
	}
}
