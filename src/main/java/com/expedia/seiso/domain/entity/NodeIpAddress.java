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

import static com.expedia.seiso.domain.entity.RotationStatus.DISABLED;
import static com.expedia.seiso.domain.entity.RotationStatus.ENABLED;
import static com.expedia.seiso.domain.entity.RotationStatus.EXCLUDED;
import static com.expedia.seiso.domain.entity.RotationStatus.NO_ENDPOINTS;
import static com.expedia.seiso.domain.entity.RotationStatus.PARTIAL;
import static com.expedia.seiso.domain.entity.RotationStatus.UNKNOWN;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.NodeIpAddressKey;
import com.expedia.seiso.domain.entity.listener.NodeIpAddressListener;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "node", "ipAddressRole", "ipAddress" })
@ToString(callSuper = true, of = { "node", "ipAddressRole", "ipAddress" })
@Entity
@EntityListeners(NodeIpAddressListener.class)
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

	@ManyToOne(optional = false)
	@JoinColumn(name = "node_id")
	private Node node;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ip_address_role_id")
	@RestResource(path = "ip-address-role")
	private IpAddressRole ipAddressRole;

	private String ipAddress;

	@NonNull
	@OneToMany(mappedBy = "ipAddress", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Endpoint> endpoints = new ArrayList<>();

	@ManyToOne(optional = true)
	@JoinColumn(name = "rotation_status_id")
	@RestResource(path = "rotation-status")
	private RotationStatus rotationStatus;

	// FIXME Hm this isn't showing up in the DTO because it's not a persistent property.
	// I've hardcoded ResourceAssembler.doSpecialNonPersistentAssociations() to deal with this, but we need a more
	// general solution. [WLW]
	@Transient
	public RotationStatus getAggregateRotationStatus() {

		// Short-circuit the endpoint checks if appropriate.
		if (DISABLED.equals(rotationStatus)) {
			return DISABLED;
		} else if (EXCLUDED.equals(rotationStatus)) {
			return EXCLUDED;
		}

		if (endpoints.isEmpty()) {
			return NO_ENDPOINTS;
		}

		int endpointCount = endpoints.size();
		int enabledCount = 0;
		int disabledCount = 0;
		int excludedCount = 0;
		int unknownCount = 0;

		for (val endpoint : endpoints) {
			val endpointRotationStatus = endpoint.getRotationStatus();
			if (ENABLED.equals(endpointRotationStatus)) {
				enabledCount++;
			} else if (DISABLED.equals(endpointRotationStatus)) {
				disabledCount++;
			} else if (EXCLUDED.equals(endpointRotationStatus)) {
				excludedCount++;
			} else {
				unknownCount++;
			}
		}

		if (ENABLED.equals(rotationStatus)) {
			if (enabledCount == endpointCount) {
				return ENABLED;
			} else if (disabledCount == endpointCount) {
				return DISABLED;
			} else if (excludedCount == endpointCount) {
				return EXCLUDED;
			} else if (unknownCount == endpointCount) {
				return UNKNOWN;
			} else if (enabledCount > 0) {
				return PARTIAL;
			} else {
				return UNKNOWN;
			}
		} else if (rotationStatus == null) {
			if (disabledCount == endpointCount) {
				return DISABLED;
			} else if (excludedCount == endpointCount) {
				return EXCLUDED;
			} else {
				return UNKNOWN;
			}
		} else {
			// This could occur if somebody adds or renames rotation statuses. So just punt.
			return UNKNOWN;
		}
	}

	@Override
	public ItemKey itemKey() {
		// FIXME This NPEs when there's no node loaded. So at least make it more explicit with ISE.
		// Need to get away from itemKeys and use URIs instead.
		if (node == null) {
			throw new IllegalStateException("Need node to generate itemKey");
		}
		return new NodeIpAddressKey(node.getName(), ipAddress);
	}

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
}
