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
import javax.persistence.OrderBy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.web.ApiVersion;

/**
 * An environment into which we deploy artifacts. Examples would be trunk, integration, live, hotfix and DR.
 * 
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "key")
@ToString(callSuper = true, of = { "key", "name" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION),
	@Projection(apiVersions = ApiVersion.V1, cardinality = Cardinality.SINGLE, paths = {
			"serviceInstances.service.type",
			"serviceInstances.service.owner",
			"serviceInstances.dataCenter.region.infrastructureProvider"
			}),
			
	// https://github.com/ExpediaDotCom/seiso/issues/75
	@Projection(apiVersions = ApiVersion.V2, cardinality = Cardinality.SINGLE)
	})
//@formatter:on
public class Environment extends AbstractItem {

	@Key
	@Column(name = "ukey")
	private String key;

	private String name;
	private String aka;
	private String description;

	@NonNull
	@OneToMany(mappedBy = "environment")
	@OrderBy("key")
	@RestResource(path = "service-instances")
	private List<ServiceInstance> serviceInstances = new ArrayList<>();

	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(Environment.class, key);
	}
}
