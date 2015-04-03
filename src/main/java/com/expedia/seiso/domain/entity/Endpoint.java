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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.key.EndpointKey;
import com.expedia.seiso.domain.entity.key.ItemKey;

/**
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "ipAddress", "port" })
@ToString(callSuper = true, of = { "ipAddress", "port", "rotationStatus" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = {
			"ipAddress.ipAddressRole",
			"port",
			"rotationStatus"
			}),
	@Projection(cardinality = Cardinality.COLLECTION, name = "nodeEndpoints", paths = {
			"port",
			"rotationStatus"
			}),
	@Projection(cardinality = Cardinality.SINGLE, paths = {
			"ipAddress.ipAddressRole",
			"port",
			"rotationStatus"
			}),
	@Projection(cardinality = Cardinality.SINGLE, name = "nodeEndpoint", paths = {
			"port",
			"rotationStatus"
			})
	})
//@formatter:on
public class Endpoint extends AbstractItem {

	@ManyToOne(optional = false)
	@JoinColumn(name = "node_ip_address_id")
	@RestResource(path = "ip-address")
	private NodeIpAddress ipAddress;

	@ManyToOne(optional = false)
	@JoinColumn(name = "service_instance_port_id")
	private ServiceInstancePort port;

	@ManyToOne(optional = true)
	@JoinColumn(name = "rotation_status_id")
	@RestResource(path = "rotation-status")
	private RotationStatus rotationStatus;

	@Override
	public ItemKey itemKey() {
		// TODO Make this more like DocLink's itemKey() method.
		// And once we do, we can get rid of EndpointKey.
		return new EndpointKey(getId());
	}

	// TODO Adopt this pattern for bidirectional associations throughout. [WLW]
	// public Endpoint setIpAddress(NodeIpAddress ipAddress) {
	// this.ipAddress = ipAddress;
	// if (ipAddress != null) {
	// // TODO Use a Set instead of a List so we don't have to do this check. [WLW]
	// val endpoints = ipAddress.getEndpoints();
	// if (!endpoints.contains(this)) { endpoints.add(this); }
	// }
	// return this;
	// }
}
