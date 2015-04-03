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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;

/**
 * <p>
 * A service.
 * </p>
 * 
 * @author Willie Wheeler
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "key")
@ToString(callSuper = true, of = { "key", "name", "type" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = { "group", "type", "owner" }),
	@Projection(cardinality = Cardinality.SINGLE, paths = { "group", "type", "owner", }),
	@Projection(cardinality = Cardinality.SINGLE, name = "instances", paths = {
			"serviceInstances.environment",
			"serviceInstances.dataCenter.region.infrastructureProvider",
			"serviceInstances.nodes"
		})
	})
//@formatter:on
public class Service extends AbstractItem {
	
	@Key
	@Column(name = "ukey")
	private String key;
	
	private String name;
	private String description;
	private String platform;
	private String scmRepository;

	@ManyToOne
	@JoinColumn(name = "group_id")
	@Fetch(FetchMode.JOIN)
	private ServiceGroup group;

	@ManyToOne
	@JoinColumn(name = "type_id")
	@Fetch(FetchMode.JOIN)
	private ServiceType type;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	@Fetch(FetchMode.JOIN)
	private Person owner;
	
	@NonNull
	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("key")
	@RestResource(path = "service-instances")
	private List<ServiceInstance> serviceInstances = new ArrayList<>();
	
	@NonNull
	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("title")
	@RestResource(path = "doc-links")
	private List<DocLink> docLinks = new ArrayList<>();
	
	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(Service.class, key);
	}
}
