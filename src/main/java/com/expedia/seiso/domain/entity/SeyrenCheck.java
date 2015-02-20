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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.key.ItemKey;

/**
 * @author Willie Wheeler
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "seyrenId")
@ToString(callSuper = true, of = { "seyrenId", "name" })
@Entity
// @formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = {
	}),
	@Projection(cardinality = Cardinality.SINGLE, paths = {
	})
})
// @formatter:on
public class SeyrenCheck extends AbstractItem {
	
	@ManyToMany
	@JoinTable(
			name = "service_instance_seyren_check",
			joinColumns = @JoinColumn(name = "seyren_check_id"),
			inverseJoinColumns =  @JoinColumn(name = "service_instance_id"))
	@RestResource(path = "service-instances")
	private List<ServiceInstance> serviceInstances;
	
	@Key
	private String seyrenId;
	
	private String name;
	private String description;
	private String graphiteBaseUrl;
	private String target;
	private Long warn;
	private Long error;
	private Boolean enabled;
	private String state;
	
	/* (non-Javadoc)
	 * @see com.expedia.seiso.domain.entity.Item#itemKey()
	 */
	@Override
	public ItemKey itemKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
