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
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
@EqualsAndHashCode(callSuper = false, of = { "serviceInstance", "number" })
@ToString(of = { "serviceInstance", "number", "protocol", "description" })
@Entity
public class ServiceInstancePort extends AbstractItem {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "service_instance_id")
	private ServiceInstance serviceInstance;
	
	@NotNull
	@Min(0)
	@Max(65535)
	private Integer number;
	
	@Size(max = 40)
	private String protocol;
	
	@Size(max = 250) 
	private String description;

	// FIXME For some reason, this is not cascade deleting endpoints. I get a constraint violation when calling it from
	// ServiceInstancePortControllerV1.deletePort(). [WLW]
	@NonNull
	@OneToMany(mappedBy = "port", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Endpoint> endpoints = new ArrayList<>();
}
