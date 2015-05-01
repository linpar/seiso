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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
import com.expedia.serf.ann.RestResource;

/**
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "key")
@ToString(of = { "key", "name" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = { "infrastructureProvider" }),
	@Projection(cardinality = Cardinality.SINGLE, paths = { "infrastructureProvider", "dataCenters" }) })
//@formatter:on
public class Region extends AbstractItem {

	@NotNull
	@Size(min = 1, max = 40)
	@Pattern(regexp = "[a-z0-9-]+")
	@Key
	@Column(name = "ukey")
	private String key;

	@NotNull
	@Size(min = 1, max = 80)
	private String name;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "provider_id")
	@RestResource(path = "infrastructure-provider")
	private InfrastructureProvider infrastructureProvider;
	
	@Size(max = 4)
	private String regionKey;

	@NonNull
	@OneToMany(mappedBy = "region")
	@OrderBy("name, key")
	@RestResource(path = "data-centers")
	private List<DataCenter> dataCenters = new ArrayList<>();

	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(Region.class, key);
	}
}
