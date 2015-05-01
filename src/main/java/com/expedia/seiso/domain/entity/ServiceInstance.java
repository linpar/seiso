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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
 * <p>
 * An instance of a service in an environment.
 * </p>
 * 
 * @author Willie Wheeler
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "key")
@ToString(callSuper = true, of = { "key", "service", "environment", "dataCenter" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = {
			"service.group",
			"service.type",
			"service.owner",
			"environment",
			"dataCenter.region.infrastructureProvider",
			"loadBalancer",
			"source"
			}),
	@Projection(apiVersions = ApiVersion.V1, cardinality = Cardinality.SINGLE, paths = {
			"service.group",
			"service.type",
			"service.owner",
			"environment",
			"dataCenter.region.infrastructureProvider",
			"loadBalancer",
			"ipAddressRoles",
			"ports",
			"nodes.ipAddresses.endpoints.port",
			"nodes.ipAddresses.endpoints.rotationStatus.statusType",
			"nodes.ipAddresses.ipAddressRole",
			"nodes.ipAddresses.rotationStatus.statusType",
			"nodes.ipAddresses.aggregateRotationStatus.statusType",
			"nodes.healthStatus.statusType",
			"dashboards",
			"seyrenChecks",
			"source"
			}),
	@Projection(apiVersions = ApiVersion.V2, cardinality = Cardinality.SINGLE, paths = {
			"service.group",
			"service.type",
			"service.owner",
			"environment",
			"dataCenter.region.infrastructureProvider",
			"loadBalancer",
			"ipAddressRoles",
			"ports",
			"dashboards",
			"seyrenChecks",
			"source"
			}),
	@Projection(cardinality = Cardinality.SINGLE, name = "infrastructure", paths = {
			"environment",
			"dataCenter.region.infrastructureProvider",
			"loadBalancer",
			"source"
			})
	})
//@formatter:on
public class ServiceInstance extends AbstractItem {
	
	// TODO Lock this down to lowercase, but let people update first. [WLW]
	@NotNull
	@Pattern(regexp = "[A-Za-z0-9-]+")
	@Size(min = 1, max = 40)
	@Key
	@Column(name = "ukey")
	private String key;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "service_id")
	@RestResource(path = "service")
	private Service service;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "environment_id")
	@RestResource(path = "environment")
	private Environment environment;

	@ManyToOne
	@JoinColumn(name = "data_center_id")
	@RestResource(path = "data-center")
	private DataCenter dataCenter;

	@ManyToOne
	@JoinColumn(name = "load_balancer_id")
	@RestResource(path = "load-balancer")
	private LoadBalancer loadBalancer;

	@NonNull
	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, orphanRemoval = true)
	@RestResource(path = "ip-address-roles")
	private List<IpAddressRole> ipAddressRoles = new ArrayList<>();

	@NonNull
	@OrderBy("number")
	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, orphanRemoval = true)
	@RestResource(path = "ports")
	private List<ServiceInstancePort> ports = new ArrayList<>();

	@NonNull
	@OrderBy("name")
	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, orphanRemoval = true)
	@RestResource(path = "nodes")
	private List<Node> nodes = new ArrayList<>();

	private Boolean loadBalanced;
	
	/**
	 * Required capacity, in deployment contexts, as a percentage of available nodes to total nodes. For example, if
	 * there are 10 nodes and we want at least 6 to be available at any given time, then the value here will be 60. Note
	 * that we use an integer representation to avoid floating point representation issues.
	 */
	private Integer minCapacityDeploy;

	/**
	 * Required capacity, in operational contexts, as a percentage of available nodes to total nodes. For example, if
	 * there are 10 nodes and we want at least 6 to be available at any given time, then the value here will be 60. Note
	 * that we use an integer representation to avoid floating point representation issues.
	 */
	private Integer minCapacityOps;
	
	@ManyToMany
	@JoinTable(
			name = "service_instance_dashboard",
			joinColumns = @JoinColumn(name = "service_instance_id"),
			inverseJoinColumns = @JoinColumn(name = "dashboard_id"))
	@RestResource(path = "dashboards")
	private List<Dashboard> dashboards;
	
	// FIXME Hm, don't think we want to have to grow the ServiceInstance class every time we add support for a new type
	// of alert. (Open/closed principle violation.) At the same time if multiple types of entity can have dashboards and
	// alerts, then we don't want to bake all those entity types into the Dashboard and SeyrenCheck classes. So we will
	// need to decide how we want to handle this. [WLW]
	@ManyToMany
	@JoinTable(
			name = "service_instance_seyren_check",
			joinColumns =  @JoinColumn(name = "service_instance_id"),
			inverseJoinColumns = @JoinColumn(name = "seyren_check_id"))
	@RestResource(path = "seyren-checks")
	private List<SeyrenCheck> seyrenChecks;
	
	
	/**
	 * Indicates whether Eos manages this service instance; i.e., whether Eos manages health state machines for the
	 * service instance's nodes.
	 * 
	 * DEPRECATED Don't want EOS-specific stuff here, at least not while Eos is Expedia-proprietary software. If we
	 * open source Eos then we might rename this to enableEos. [WLW]
	 */
	private Boolean eosManaged;
	
	/**
	 * Enable Seyren integration.
	 */
	private Boolean enableSeyren;

	@Deprecated
	public Boolean getEosManaged() {
		return eosManaged;
	}

	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(ServiceInstance.class, key);
	}
}
