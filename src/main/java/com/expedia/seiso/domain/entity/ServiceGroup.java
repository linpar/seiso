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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;

/**
 * A group of services. A service can be in at most one group.
 * 
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "key")
@ToString(of = { "key", "name" })
@Entity
// @formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION),
	@Projection(cardinality = Cardinality.SINGLE, paths = { "services.type", "services.owner" })
})
// @formatter:on
public class ServiceGroup extends AbstractItem {

	@Key
	@Column(name = "ukey")
	private String key;

	private String name;
	private String description;
	
	// Don't want cascading here. If we delete a service group, then the former members are simply orphaned rather than
	// being deleted. [WLW]
	@NonNull
	@OneToMany(mappedBy = "group")
	private List<Service> services = new ArrayList<>();

	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(ServiceGroup.class, key);
	}
}
