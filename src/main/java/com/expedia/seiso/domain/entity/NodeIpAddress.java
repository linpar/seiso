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
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "node", "ipAddressRole", "ipAddress" })
@ToString(callSuper = true, of = { "node", "ipAddressRole", "ipAddress" })
@Entity
public class NodeIpAddress extends AbstractItem {
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "node_id")
	private Node node;
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "ip_address_role_id")
	private IpAddressRole ipAddressRole;
	
	@NotNull
	@Size(min = 1, max = 20)
	private String ipAddress;

	@NonNull
	@OneToMany(mappedBy = "ipAddress", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Endpoint> endpoints = new ArrayList<>();

	@ManyToOne(optional = true)
	@JoinColumn(name = "rotation_status_id")
	private RotationStatus rotationStatus;
	
	@ManyToOne
	@JoinColumn(name = "aggregate_rotation_status_id")
	private RotationStatus aggregateRotationStatus;
}
