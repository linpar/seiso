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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.repo.RepoKeys;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Accessors(chain = true)
@ToString(of = { "key", "name" })
@EqualsAndHashCode(callSuper = false, of = { "key", "name" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION),
	@Projection(cardinality = Cardinality.SINGLE)
	})
//@formatter:on
public class StatusType extends AbstractItem {

	// TODO We might want to replace the data-driven status types with fixed types, since we have hand-coded rotation
	// status aggregation logic involving specific types. [WLW]
	public static final StatusType DEFAULT = new StatusType("default", "Default");
	public static final StatusType PRIMARY = new StatusType("primary", "Primary");
	public static final StatusType SUCCESS = new StatusType("success", "Success");
	public static final StatusType INFO = new StatusType("info", "Info");
	public static final StatusType WARNING = new StatusType("warning", "Warning");
	public static final StatusType DANGER = new StatusType("danger", "Danger");
	
	@NonNull
	@NotNull
	@Size(min = 1, max = 20)
	@Pattern(regexp = "[a-z0-9-]+")
	@Key
	@Column(name = "ukey")
	private String key;

	@NonNull
	@NotNull
	@Size(min = 1, max = 80)
	@Column(name = "name")
	private String name;
	
	@Size(max = 250)
	private String description;

	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(StatusType.class, key);
	}

	@Override
	public String[] itemPath() {
		return new String[] { RepoKeys.STATUS_TYPES, key };
	}
}
