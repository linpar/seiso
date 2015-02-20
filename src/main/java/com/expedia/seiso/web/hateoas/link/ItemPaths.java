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
package com.expedia.seiso.web.hateoas.link;

import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import lombok.val;

import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.DataSource;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Environment;
import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.InfrastructureProvider;
import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.LoadBalancer;
import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.Region;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceGroup;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.ServiceType;
import com.expedia.seiso.domain.entity.SeyrenCheck;
import com.expedia.seiso.domain.entity.StatusType;
import com.expedia.seiso.domain.repo.RepoKeys;

// This isn't a converter per se, but I moved this class to this package to start consolidating the URI mapping code.
// Really need to DRY it up. [WLW]

/**
 * Resolves items to their URI paths, relative to the version segment (/v1, /v2, etc.) of the URI. Currently these are
 * <em>mostly</em> the same for /v1 and /v2 ({@link Endpoint} stands out as an exception), so we consolidate the logic
 * here.
 * 
 * @author Willie Wheeler
 */
@Component
public class ItemPaths {
	private final Map<Class<?>, ItemPathConverter> converters = new HashMap<>();
	
	public ItemPaths() {
		converters.put(DataCenter.class,
				(Item item) -> new String[] { RepoKeys.DATA_CENTERS, ((DataCenter) item).getKey() });
		converters.put(DataSource.class,
				(Item item) -> new String[] { RepoKeys.DATA_SOURCES, ((DataSource) item).getKey() });
		
		// FIXME This is the v1 path, but we don't want to use the database ID in the v2 path.
		converters.put(Endpoint.class,
				(Item item) -> new String[] { RepoKeys.ENDPOINTS, String.valueOf(((Endpoint) item).getId()) });
		
		converters.put(Environment.class,
				(Item item) -> new String[] { RepoKeys.ENVIRONMENTS, ((Environment) item).getKey() });
		converters.put(HealthStatus.class,
				(Item item) -> new String[] { RepoKeys.HEALTH_STATUSES, ((HealthStatus) item).getKey() });
		converters.put(InfrastructureProvider.class,
				(Item item) -> new String[] { RepoKeys.INFRASTRUCTURE_PROVIDERS, ((InfrastructureProvider) item).getKey() });
		converters.put(IpAddressRole.class,
				(Item item) -> {
					IpAddressRole role = (IpAddressRole) item;
					return new String[] {
							RepoKeys.SERVICE_INSTANCES,
							role.getServiceInstance().getKey(),
							RepoKeys.IP_ADDRESS_ROLES,
							role.getName()
					};
				});
		converters.put(LoadBalancer.class,
				(Item item) -> new String[] { RepoKeys.LOAD_BALANCERS, ((LoadBalancer) item).getName() });
		converters.put(Machine.class,
				(Item item) -> new String[] { RepoKeys.MACHINES, ((Machine) item).getName() });
		converters.put(Node.class,
				(Item item) -> new String[] { RepoKeys.NODES, ((Node) item).getName() });
		converters.put(NodeIpAddress.class,
				(Item item) -> {
					NodeIpAddress nip = (NodeIpAddress) item;
					return new String[] {
							RepoKeys.NODES,
							nip.getNode().getName(),
							"ip-addresses",
							nip.getIpAddress()
					};
				});
		converters.put(Person.class,
				(Item item) -> new String[] { RepoKeys.PEOPLE, ((Person) item).getUsername() });
		converters.put(Region.class,
				(Item item) -> new String[] { RepoKeys.REGIONS, ((Region) item).getKey() });
		converters.put(RotationStatus.class,
				(Item item) -> new String[] { RepoKeys.ROTATION_STATUSES, ((RotationStatus) item).getKey() });
		converters.put(Service.class,
				(Item item) -> new String[] { RepoKeys.SERVICES, ((Service) item).getKey() });
		converters.put(ServiceGroup.class,
				(Item item) -> new String[] { RepoKeys.SERVICE_GROUPS, ((ServiceGroup) item).getKey() });
		converters.put(ServiceInstance.class,
				(Item item) -> new String[] { RepoKeys.SERVICE_INSTANCES, ((ServiceInstance) item).getKey() });
		converters.put(ServiceInstancePort.class,
				(Item item) -> {
					ServiceInstancePort port = (ServiceInstancePort) item;
					return new String[] {
							RepoKeys.SERVICE_INSTANCES,
							port.getServiceInstance().getKey(),
							RepoKeys.SERVICE_INSTANCE_PORTS,
							String.valueOf(port.getNumber())
					};
				});
		converters.put(ServiceType.class,
				(Item item) -> new String[] { RepoKeys.SERVICE_TYPES, ((ServiceType) item).getKey() });
		converters.put(StatusType.class,
				(Item item) -> new String[] { RepoKeys.STATUS_TYPES, ((StatusType) item).getKey() });
		
		// Custom integrations
		converters.put(SeyrenCheck.class,
				(Item item) -> new String[] { RepoKeys.SEYREN_CHECKS, ((SeyrenCheck) item).getSeyrenId() });
	}
	
	public String[] convert(@NonNull Item item) {
		val itemClass = item.getClass();
		val converter = converters.get(itemClass);
		if (converter == null) {
			throw new IllegalArgumentException("No converter for itemClass=" + itemClass.getName());
		}
		return converter.convert(item);
	}
}
