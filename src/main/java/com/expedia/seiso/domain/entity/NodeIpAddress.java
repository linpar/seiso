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

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.NodeIpAddressKey;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.serf.ann.RestResource;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "node", "ipAddressRole", "ipAddress" })
@ToString(callSuper = true, of = { "node", "ipAddressRole", "ipAddress" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = {
			"node",
			"ipAddressRole",
			"rotationStatus"
			}),
	@Projection(cardinality = Cardinality.SINGLE, paths = {
			"node",
			"ipAddressRole",
			"endpoints.port",
			"endpoints.rotationStatus.statusType",
			"rotationStatus.statusType",
			"aggregateRotationStatus.statusType"
			})
	})
//@formatter:on
public class NodeIpAddress extends AbstractItem {
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "node_id")
	private Node node;
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "ip_address_role_id")
	@RestResource(path = "ip-address-role")
	private IpAddressRole ipAddressRole;
	
	@NotNull
	@Size(min = 1, max = 20)
	private String ipAddress;

	@NonNull
	@OneToMany(mappedBy = "ipAddress", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Endpoint> endpoints = new ArrayList<>();

	@ManyToOne(optional = true)
	@JoinColumn(name = "rotation_status_id")
	@RestResource(path = "rotation-status")
	private RotationStatus rotationStatus;
	
	@ManyToOne
	@JoinColumn(name = "aggregate_rotation_status_id")
	@RestResource(path = "aggregate-rotation-status")
	private RotationStatus aggregateRotationStatus;

	// TODO Adopt this pattern for bidirectional associations throughout. [WLW]
	// public NodeIpAddress setNode(Node node) {
	// this.node = node;
	// if (node != null) {
	// // TODO Use a Set instead of a List so we don't have to do this check. [WLW]
	// val ipAddresses = node.getIpAddresses();
	// if (!ipAddresses.contains(this)) { ipAddresses.add(this); }
	// }
	// return this;
	// }
	
	@Override
	public ItemKey itemKey() {
		// FIXME This NPEs when there's no node loaded. So at least make it more explicit with ISE.
		// Need to get away from itemKeys and use URIs instead.
		if (node == null) {
			throw new IllegalStateException("Need node to generate itemKey");
		}
		return new NodeIpAddressKey(node.getName(), ipAddress);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.domain.entity.Item#itemPath()
	 */
	@Override
	public String[] itemPath() {
		return new String[] {
				RepoKeys.NODES,
				node.getName(),
				"ip-addresses",
				getIpAddress()
		};
	}	
}
