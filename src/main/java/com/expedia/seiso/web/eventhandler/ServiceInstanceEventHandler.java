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

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.Domain;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.repo.EndpointRepo;
import com.expedia.seiso.domain.repo.EnvironmentRepo;
import com.expedia.seiso.domain.repo.IpAddressRoleRepo;
import com.expedia.seiso.domain.repo.LoadBalancerRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.domain.repo.RotationStatusRepo;
import com.expedia.seiso.domain.repo.ServiceInstancePortRepo;
import com.expedia.seiso.domain.repo.ServiceRepo;
import com.expedia.seiso.web.assembler.ServiceInstanceService;

/**
 * @author Willie Wheeler
 */
@RepositoryEventHandler(ServiceInstance.class)
@Component
@Slf4j
public class ServiceInstanceEventHandler {
	
	@Autowired private EnvironmentRepo endpointRepo;
	
	@Autowired private IpAddressRoleRepo rotationStatusRepo;
	
	@Autowired private LoadBalancerRepo serviceInstanceService;
	
	@Autowired private NodeRepo nodeRepo;
	
	@Autowired private ServiceRepo serviceRepo;
	
	@Autowired private ServiceInstancePortRepo serviceInstancePortRepo;

	private static final int PAGE_SIZE = 50;
	
	
	@HandleBeforeDelete
	public void handleBeforeDelete(ServiceInstance serviceInstance) {
		Long id = serviceInstance.getId();
		cascadeDeleteToNodes(id);
		cascadeDeleteToSIPort(id);
	}
	
	private boolean cascadeDeleteToNodes(Long serviceInstanceID){
		try {
			List<Node> nodes = nodeRepo.findByServiceInstanceID(serviceInstanceID);
			Iterator<Node> iterator = nodes.iterator();
			while (iterator.hasNext()){
				Node node = iterator.next();
				nodeRepo.delete(node.getId());
			}
			return true;
		}
		catch (Exception e){
			return false;
		}
	}
	
	private boolean cascadeDeleteToSIPort(Long serviceInstanceID){
		try {
			List<ServiceInstancePort> siPorts = serviceInstancePortRepo.findByServiceInstanceID(serviceInstanceID);
			Iterator<ServiceInstancePort> iterator = siPorts.iterator();
			while (iterator.hasNext()){
				ServiceInstancePort port = iterator.next();
				serviceInstancePortRepo.delete(port.getId());
			}
			return true;
		}
		catch (Exception e){
			return false;
		}
	}
}
