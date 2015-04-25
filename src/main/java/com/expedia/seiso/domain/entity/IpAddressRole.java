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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.IpAddressRoleKey;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.serf.ann.RestResource;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "serviceInstance", "name" })
@ToString(callSuper = true, of = { "serviceInstance", "name" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = { "serviceInstance" }),
	@Projection(cardinality = Cardinality.SINGLE, paths = { "serviceInstance", "ipAddresses" })
	})
//@formatter:on
public class IpAddressRole extends AbstractItem {

	@ManyToOne
	@JoinColumn(name = "service_instance_id")
	@RestResource(path = "service-instance")
	private ServiceInstance serviceInstance;

	private String name;
	private String description;

	@OneToMany(mappedBy = "ipAddressRole", cascade = CascadeType.ALL, orphanRemoval = true)
	@RestResource(path = "ip-addresses")
	private List<NodeIpAddress> ipAddresses;

	@Override
	public ItemKey itemKey() {
		if (serviceInstance == null) {
			throw new IllegalStateException("serviceInstance can't be null");
		} else if (name == null) {
			throw new IllegalStateException("name can't be null");
		}
		return new IpAddressRoleKey(serviceInstance.getKey(), name);
	}
}
