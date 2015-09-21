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
package com.expedia.seiso.entity;

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
public class ServiceInstance extends AbstractItem {
	
	// TODO Lock this down to lowercase, but let people update first. [WLW]
	@NotNull
	@Pattern(regexp = "[A-Za-z0-9-]+")
	@Size(min = 1, max = 40)
	@Column(name = "ukey")
	private String key;
	
	@Size(max = 250)
	private String description;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "service_id")
	private Service service;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "environment_id")
	private Environment environment;

	@ManyToOne
	@JoinColumn(name = "data_center_id")
	private DataCenter dataCenter;

	@ManyToOne
	@JoinColumn(name = "load_balancer_id")
	private LoadBalancer loadBalancer;

	@NonNull
	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<IpAddressRole> ipAddressRoles = new ArrayList<>();

	@NonNull
	@OrderBy("number")
	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ServiceInstancePort> ports = new ArrayList<>();

	@NonNull
	@OrderBy("name")
	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, orphanRemoval = true)
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
	private List<Dashboard> dashboards;
	
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
}
