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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;

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
			"source"
	})
})
// @formatter:on
public class SeyrenCheck extends AbstractItem {
	
	@NotNull
	@Size(min = 1, max = 250)
	private String seyrenBaseUrl;
	
	@NotNull
	@Size(min = 1, max = 40)
	private String seyrenId;
	
	@NotNull
	@Size(min = 1, max = 250)
	private String name;
	
	@Size(min = 1, max = 1000)
	private String description;
	
	@NotNull
	@Size(min = 1, max = 250)
	private String graphiteBaseUrl;
	
	@NotNull
	@Size(min = 1, max = 1000)
	private String target;
	
	@NotNull
	private Long warn;
	
	@NotNull
	private Long error;
	
	@NotNull
	private Boolean enabled;
	
	@Size(min = 1, max = 20)
	private String state;
	
	@ManyToOne
	@JoinColumn(name = "source_id")
	private Source source;
	
	@Override
	public ItemKey itemKey() {
		Long id = getId();
		return (id == null ? null : new SimpleItemKey(SeyrenCheck.class, id));
	}
}
