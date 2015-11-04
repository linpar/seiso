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
package com.expedia.seiso.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "key")
@ToString(of = { "key", "name" })
@Entity
public class ServiceType extends AbstractItem {

	@NotNull
	@Size(min = 1, max = 40)
	@Pattern(regexp = "[a-z0-9-]+")
	@Column(name = "ukey")
	private String key;

	@NotNull
	@Size(min = 1, max = 80)
	@Column(name = "name")
	private String name;
	
	@Size(max = 250)
	private String description;

	// Don't want cascading here. If we delete a service type, then the former members are simply orphaned rather than
	// being deleted. [WLW]
	@NonNull
	@OneToMany(mappedBy = "type")
	private List<Service> services = new ArrayList<>();
}
