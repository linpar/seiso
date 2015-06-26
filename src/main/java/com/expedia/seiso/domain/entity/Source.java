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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.repo.RepoKeys;

/**
 * Represents a source of Seiso item data. In general we create sync processes to copy data from the data source into
 * Seiso, sometimes as a batch process and sometimes in real time. Examples include:
 * 
 * <ul>
 * <li>Active Directory</li>
 * <li>Chef server</li>
 * <li>Load balancers</li>
 * <li>Git repositories</li>
 * <li>Seyren servers</li>
 * </ul>
 * 
 * and so forth.
 * 
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "key")
@ToString(of = { "key", "baseUri" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = {}),
	@Projection(cardinality = Cardinality.SINGLE, paths = {})
})
//@formatter:on
public class Source extends AbstractItem {
	
	@Key
	@Column(name = "ukey")
	private String key;
	
	private String baseUri;

	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(Source.class, key);
	}

	@Override
	public String[] itemPath() {
		return new String[] { RepoKeys.SOURCES, key };
	}
}
