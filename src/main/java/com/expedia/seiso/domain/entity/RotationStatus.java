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

import static com.expedia.seiso.domain.entity.StatusType.INFO;
import static com.expedia.seiso.domain.entity.StatusType.SUCCESS;
import static com.expedia.seiso.domain.entity.StatusType.WARNING;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.serf.ann.RestResource;

/**
 * @author Willie Wheeler
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(of = { "key", "name", "statusType" })
@EqualsAndHashCode(callSuper = false, of = { "key" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = { "statusType" }),
	@Projection(cardinality = Cardinality.SINGLE, paths = { "statusType" })
})
//@formatter:on
public class RotationStatus extends AbstractItem {

	// TODO We might want to replace the data-driven rotation statuses with fixed statuses, since we have hand-coded
	// rotation status aggregation logic involving specific statuses. [WLW]
	public static final RotationStatus ENABLED = new RotationStatus("enabled", "Enabled", null, SUCCESS);
	public static final RotationStatus DISABLED = new RotationStatus("disabled", "Disabled", null, WARNING);
	public static final RotationStatus EXCLUDED = new RotationStatus("excluded", "Excluded", null, INFO);
	public static final RotationStatus NO_ENDPOINTS = new RotationStatus("no-endpoints", "No endpoints", null, INFO);
	public static final RotationStatus PARTIAL = new RotationStatus("partial", "Partial", null, WARNING);
	public static final RotationStatus UNKNOWN = new RotationStatus("unknown", "Unknown", null, WARNING);
	
	public static final RotationStatus getRotationStatus(@NonNull String key) {
		// FIXME This sucks. Figure out a better way.
		switch (key) {
		case "enabled":
			return ENABLED;
		case "disabled":
			return DISABLED;
		case "excluded":
			return EXCLUDED;
		case "no-endponits":
			return NO_ENDPOINTS;
		case "partial":
			return PARTIAL;
		case "unknown":
			return UNKNOWN;
		default:
			throw new IllegalArgumentException("Illegal rotation state: " + key);
		}
	}
	
	@NotNull
	@Size(min = 1, max = 20)
	@Pattern(regexp = "[a-z0-9-]+")
	@Key
	@Column(name = "ukey")
	private String key;

	@NotNull
	@Size(min = 1, max = 80)
	@Column(name = "name")
	private String name;
	
	@Size(max = 250)
	private String description;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "status_type_id")
	@RestResource(path = "status-type")
	private StatusType statusType;

	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(RotationStatus.class, key);
	}

	@Override
	public String[] itemPath() {
		return new String[] { RepoKeys.ROTATION_STATUSES, key };
	}
}
