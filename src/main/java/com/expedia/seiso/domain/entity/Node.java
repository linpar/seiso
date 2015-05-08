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
package com.expedia.seiso.domain.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.serf.ann.RestResource;

/**
 * @author Willie Wheeler
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "name" })
@ToString(callSuper = true, of = { "name", "serviceInstance", "machine" })
@Entity
// TODO Handle createdBy and updatedBy automatically in the assembler. [WLW]
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = {
			"serviceInstance",
			"machine",
			"healthStatus.statusType"
			}),
	@Projection(apiVersions = ApiVersion.V1, cardinality = Cardinality.COLLECTION, name = "serviceInstanceNodes", paths = {
			"machine"
			}),
	@Projection(apiVersions = ApiVersion.V1, cardinality = Cardinality.COLLECTION, name = "withEndpoints", paths = {
			"serviceInstance",
			"machine",
			"ipAddresses.ipAddressRole",
			"ipAddresses.endpoints.port",
			"ipAddresses.endpoints.rotationStatus"
			}),
	
	// View to show nodes on the service instance details page.
	// N.B. v2 uses lowercase-and-hyphens rather than camelCase.
	@Projection(apiVersions = ApiVersion.V2, cardinality = Cardinality.COLLECTION, name = "service-instance-nodes", paths = {
			"machine",
			"healthStatus.statusType",
			"aggregateRotationStatus.statusType",
			"ipAddresses.ipAddressRole",
			"ipAddresses.endpoints.port",
			"ipAddresses.endpoints.rotationStatus",
			"ipAddresses.aggregateRotationStatus.statusType"
	}),
	
	// TODO Hm, maybe we should make the default single view show what we expect users to provide on a put. GET/PUT
	// symmetry kind of thing. Then use the other projections to support specific automation/UI needs. [WLW]
	@Projection(cardinality = Cardinality.SINGLE, paths = {
			"serviceInstance.dataCenter.region.infrastructureProvider",
			"serviceInstance.environment",
			"serviceInstance.service.ipAddressRoles",
			"serviceInstance.service.owner",
			"serviceInstance.ports",
			"machine",
			"ipAddresses.endpoints.port",
			"ipAddresses.endpoints.rotationStatus.statusType",
			"ipAddresses.ipAddressRole",
			"healthStatus.statusType"
			}),

	@Projection(apiVersions = ApiVersion.V1, cardinality = Cardinality.SINGLE, name = "state", paths = {
			"serviceInstance.service.ipAddressRoles",
			"serviceInstance.ports",
			"machine",
			"ipAddresses.endpoints.port",
			"ipAddresses.endpoints.rotationStatus.statusType",
			"ipAddresses.ipAddressRole",
			"healthStatus"
			})
	})
//@formatter:on
public class Node extends AbstractItem {
	
	// TODO Lock this down to lowercase, but let people update first. [WLW]
	// Also some people have periods in their nodes.
	@NotNull
//	@Pattern(regexp = "[A-Za-z0-9-]+")
	@Size(min = 1, max = 80)
	@Key
	private String name;

	/**
	 * Optional description to support cases where service instance nodes aren't entirely interchangeable. For instance
	 * we have Splunk service instances where each service instance has its own purpose (ad hoc searches, summary
	 * searches, alerts, dashboards, etc.).
	 */
	@Size(max = 250)
	private String description;
	
	@Size(max = 128)
	private String version;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "service_instance_id")
	@RestResource(path = "service-instance")
	private ServiceInstance serviceInstance;
	
	@ManyToOne
	@JoinColumn(name = "machine_id")
	@RestResource(path = "machine")
	private Machine machine;

//	@NonNull
//	@ElementCollection
	@OneToMany(mappedBy = "node", cascade = CascadeType.ALL, orphanRemoval = true)
	@RestResource(path = "ip-addresses")
	private List<NodeIpAddress> ipAddresses = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "health_status_id")
	@RestResource(path = "health-status")
	private HealthStatus healthStatus;
	
	// We use this primarily to find node alerts. See NodeRepo.
	@ManyToOne
	@JoinColumn(name = "aggregate_rotation_status_id")
	@RestResource(path = "aggregate-rotation-status")
	private RotationStatus aggregateRotationStatus;
	
	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(Node.class, name);
	}
}
